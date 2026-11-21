
package com.thesis.thesis_scheduler.service.impl;

import com.thesis.thesis_scheduler.dto.SchedulePublicationEventResponse;
import com.thesis.thesis_scheduler.dto.SchedulePublicationRequest;
import com.thesis.thesis_scheduler.dto.ScheduleWithdrawalRequest;

import com.thesis.thesis_scheduler.entity.OfficialSchedule;
import com.thesis.thesis_scheduler.entity.ScheduleEntry;
import com.thesis.thesis_scheduler.entity.SchedulePublicationEvent;
import com.thesis.thesis_scheduler.entity.ScheduleVersion;
import com.thesis.thesis_scheduler.entity.SectionSubject;
import com.thesis.thesis_scheduler.entity.TeachingAssignment;

import com.thesis.thesis_scheduler.optimization.ga.CompleteScheduleValidator;
import com.thesis.thesis_scheduler.optimization.model.ScheduleChromosome;
import com.thesis.thesis_scheduler.optimization.model.ScheduleGene;

import com.thesis.thesis_scheduler.repository.OfficialScheduleRepository;
import com.thesis.thesis_scheduler.repository.ScheduleEntryRepository;
import com.thesis.thesis_scheduler.repository.SchedulePublicationEventRepository;
import com.thesis.thesis_scheduler.repository.TeachingAssignmentRepository;

import com.thesis.thesis_scheduler.service.SchedulePublicationService;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class SchedulePublicationServiceImpl
        implements SchedulePublicationService {

    private static final String EVENT_PUBLISHED = "PUBLISHED";
    private static final String EVENT_WITHDRAWN = "WITHDRAWN";

    private static final String STATUS_PUBLISHED = "PUBLISHED";
    private static final String STATUS_WITHDRAWN = "WITHDRAWN";

    private final OfficialScheduleRepository officialScheduleRepository;

    private final ScheduleEntryRepository scheduleEntryRepository;

    private final SchedulePublicationEventRepository
            publicationEventRepository;

    private final TeachingAssignmentRepository teachingAssignmentRepository;

    private final CompleteScheduleValidator completeScheduleValidator;

    /*
     * Publish the current version of an official schedule.
     *
     * Publication is allowed only when:
     * 1. The official schedule exists.
     * 2. A current version exists and belongs to the official schedule.
     * 3. The schedule is not already published.
     * 4. The version contains schedule entries.
     * 5. All required teaching assignments are represented.
     * 6. Lecture and laboratory hours are satisfied.
     * 7. All hard constraints pass.
     */
    @Override
    public SchedulePublicationEventResponse publish(
            Long officialScheduleId,
            SchedulePublicationRequest request) {

        validateOfficialScheduleId(officialScheduleId);

        if (request == null) {
            throw new IllegalArgumentException(
                    "Schedule publication request is required."
            );
        }

        // Lock the official schedule to prevent concurrent publication.
        OfficialSchedule officialSchedule =
                findOfficialScheduleForUpdate(officialScheduleId);

        ScheduleVersion currentVersion =
                officialSchedule.getCurrentVersion();

        if (currentVersion == null) {
            throw new IllegalStateException(
                    "Cannot publish an official schedule without a current version."
            );
        }

        if (currentVersion.getOfficialSchedule() == null
                || !officialScheduleId.equals(
                currentVersion.getOfficialSchedule()
                        .getOfficialScheduleId())) {

            throw new IllegalStateException(
                    "The current version does not belong to this official schedule."
            );
        }

        // Check the official schedule state.
        if (STATUS_PUBLISHED.equals(
                officialSchedule.getScheduleStatus())) {

            throw new IllegalStateException(
                    "The official schedule is already published. "
                            + "Withdraw the existing publication before publishing again."
            );
        }

        // Check the latest publication event.
        SchedulePublicationEvent latestEvent =
                publicationEventRepository
                        .findTopByOfficialSchedule_OfficialScheduleIdOrderByEventAtDescPublicationEventIdDesc(
                                officialScheduleId
                        )
                        .orElse(null);

        if (latestEvent != null
                && EVENT_PUBLISHED.equals(latestEvent.getEventType())) {

            throw new IllegalStateException(
                    "The official schedule already has an active publication."
            );
        }

        // Validate the complete timetable before publication.
        validateVersionForPublication(
                officialSchedule,
                currentVersion
        );

        SchedulePublicationEvent event =
                new SchedulePublicationEvent();

        event.setOfficialSchedule(officialSchedule);
        event.setScheduleVersion(currentVersion);
        event.setEventType(EVENT_PUBLISHED);
        event.setRelatedEvent(null);
        event.setPerformedBy(request.getPerformedBy());
        event.setRemarks(request.getRemarks());

        SchedulePublicationEvent savedEvent =
                publicationEventRepository.save(event);

        // Retain the exact version that was published.
        officialSchedule.setPublishedVersion(currentVersion);
        officialSchedule.setScheduleStatus(STATUS_PUBLISHED);

        officialScheduleRepository.save(officialSchedule);

        return toResponse(savedEvent);
    }

    /*
     * Withdraw the latest active publication.
     *
     * The published version reference is retained for audit purposes.
     */
    @Override
    public SchedulePublicationEventResponse withdraw(
            Long officialScheduleId,
            ScheduleWithdrawalRequest request) {

        validateOfficialScheduleId(officialScheduleId);

        if (request == null
                || request.getRemarks() == null
                || request.getRemarks().isBlank()) {

            throw new IllegalArgumentException(
                    "A withdrawal reason is required."
            );
        }

        // Lock the official schedule to prevent concurrent withdrawals.
        OfficialSchedule officialSchedule =
                findOfficialScheduleForUpdate(officialScheduleId);

        SchedulePublicationEvent latestEvent =
                publicationEventRepository
                        .findTopByOfficialSchedule_OfficialScheduleIdOrderByEventAtDescPublicationEventIdDesc(
                                officialScheduleId
                        )
                        .orElseThrow(() -> new IllegalStateException(
                                "No publication history exists for this official schedule."
                        ));

        if (!EVENT_PUBLISHED.equals(latestEvent.getEventType())) {
            throw new IllegalStateException(
                    "There is no active publication to withdraw."
            );
        }

        if (!STATUS_PUBLISHED.equals(
                officialSchedule.getScheduleStatus())) {

            throw new IllegalStateException(
                    "The official schedule is not currently published."
            );
        }

        SchedulePublicationEvent withdrawalEvent =
                new SchedulePublicationEvent();

        withdrawalEvent.setOfficialSchedule(officialSchedule);

        // Identify the exact version that was published.
        withdrawalEvent.setScheduleVersion(
                latestEvent.getScheduleVersion()
        );

        withdrawalEvent.setEventType(EVENT_WITHDRAWN);

        // Link the withdrawal to its original publication event.
        withdrawalEvent.setRelatedEvent(latestEvent);

        withdrawalEvent.setPerformedBy(request.getPerformedBy());
        withdrawalEvent.setRemarks(request.getRemarks());

        SchedulePublicationEvent savedEvent =
                publicationEventRepository.save(withdrawalEvent);

        // Do not clear publishedVersion.
        officialSchedule.setScheduleStatus(STATUS_WITHDRAWN);

        officialScheduleRepository.save(officialSchedule);

        return toResponse(savedEvent);
    }

    /*
     * Retrieve publication and withdrawal history.
     */
    @Override
    @Transactional(readOnly = true)
    public List<SchedulePublicationEventResponse> getPublicationHistory(
            Long officialScheduleId) {

        validateOfficialScheduleId(officialScheduleId);

        if (!officialScheduleRepository.existsById(officialScheduleId)) {
            throw new EntityNotFoundException(
                    "Official schedule not found: " + officialScheduleId
            );
        }

        return publicationEventRepository
                .findByOfficialSchedule_OfficialScheduleIdOrderByEventAtDescPublicationEventIdDesc(
                        officialScheduleId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /*
     * Validate the complete schedule before publication.
     */
    private void validateVersionForPublication(
            OfficialSchedule officialSchedule,
            ScheduleVersion version) {

        List<ScheduleEntry> entries =
                scheduleEntryRepository
                        .findByScheduleVersion_ScheduleVersionId(
                                version.getScheduleVersionId()
                        );

        if (entries.isEmpty()) {
            throw new IllegalStateException(
                    "Cannot publish a schedule version without schedule entries."
            );
        }

        Long termId = officialSchedule.getTerm().getTermId();

        List<ScheduleGene> genes = new ArrayList<>();

        /*
         * SectionSubject ID -> Session type -> Timeslot ID -> Duration.
         */
        Map<Long, Map<String, Map<Long, Integer>>>
                scheduledMinutesBySectionSubject = new HashMap<>();

        Set<Long> submittedAssignmentIds = new LinkedHashSet<>();

        for (ScheduleEntry entry : entries) {

            if (entry.getAssignment() == null
                    || entry.getAssignment().getInstructor() == null
                    || entry.getAssignment().getSectionSubject() == null
                    || entry.getAssignment().getSectionSubject().getSection() == null
                    || entry.getRoom() == null
                    || entry.getTimeslot() == null) {

                throw new IllegalStateException(
                        "Schedule version contains an entry with incomplete relationships: "
                                + entry.getScheduleEntryId()
                );
            }

            String sessionType = entry.getSessionType();

            if (!"LECTURE".equals(sessionType)
                    && !"LABORATORY".equals(sessionType)) {

                throw new IllegalStateException(
                        "Invalid session type in schedule entry: "
                                + entry.getScheduleEntryId()
                );
            }

            if (entry.getTimeslot().getDurationMinutes() == null
                    || entry.getTimeslot().getDurationMinutes() <= 0) {

                throw new IllegalStateException(
                        "Invalid timeslot duration in schedule entry: "
                                + entry.getScheduleEntryId()
                );
            }

            TeachingAssignment assignment = entry.getAssignment();

            SectionSubject sectionSubject =
                    assignment.getSectionSubject();

            submittedAssignmentIds.add(
                    assignment.getAssignmentId()
            );

            ScheduleGene gene = new ScheduleGene(
                    assignment.getAssignmentId(),
                    assignment.getInstructor().getInstructorId(),
                    sectionSubject.getSection().getSectionId(),
                    entry.getRoom().getRoomId(),
                    entry.getTimeslot().getTimeslotId()
            );

            gene.setSessionType(sessionType);
            genes.add(gene);

            scheduledMinutesBySectionSubject
                    .computeIfAbsent(
                            sectionSubject.getSectionSubjectId(),
                            key -> new HashMap<>()
                    )
                    .computeIfAbsent(
                            sessionType,
                            key -> new HashMap<>()
                    )
                    .putIfAbsent(
                            entry.getTimeslot().getTimeslotId(),
                            entry.getTimeslot().getDurationMinutes()
                    );
        }

        // Validate that all active assignments for the term are present.
        List<TeachingAssignment> requiredAssignments =
                teachingAssignmentRepository.findActiveAssignmentsByTermId(
                        termId
                );

        Set<Long> requiredAssignmentIds = new LinkedHashSet<>();

        for (TeachingAssignment assignment : requiredAssignments) {
            requiredAssignmentIds.add(
                    assignment.getAssignmentId()
            );
        }

        Set<Long> missingAssignmentIds =
                new LinkedHashSet<>(requiredAssignmentIds);

        missingAssignmentIds.removeAll(submittedAssignmentIds);

        Set<Long> unexpectedAssignmentIds =
                new LinkedHashSet<>(submittedAssignmentIds);

        unexpectedAssignmentIds.removeAll(requiredAssignmentIds);

        if (!missingAssignmentIds.isEmpty()
                || !unexpectedAssignmentIds.isEmpty()) {

            throw new IllegalStateException(
                    "The schedule does not contain the exact set of active "
                            + "teaching assignments. Missing: "
                            + missingAssignmentIds
                            + ". Unexpected: "
                            + unexpectedAssignmentIds
            );
        }

        // Validate required lecture and laboratory hours.
        Set<Long> validatedSectionSubjectIds = new HashSet<>();

        for (TeachingAssignment assignment : requiredAssignments) {

            SectionSubject sectionSubject =
                    assignment.getSectionSubject();

            if (sectionSubject == null) {
                throw new IllegalStateException(
                        "Teaching assignment has no SectionSubject: "
                                + assignment.getAssignmentId()
                );
            }

            Long sectionSubjectId =
                    sectionSubject.getSectionSubjectId();

            if (!validatedSectionSubjectIds.add(sectionSubjectId)) {
                continue;
            }

            BigDecimal requiredLectureHours =
                    sectionSubject.getRequiredLectureHours();

            BigDecimal requiredLabHours =
                    sectionSubject.getRequiredLabHours();

            if (requiredLectureHours == null
                    || requiredLabHours == null
                    || requiredLectureHours.signum() < 0
                    || requiredLabHours.signum() < 0) {

                throw new IllegalStateException(
                        "Invalid required teaching hours for SectionSubject: "
                                + sectionSubjectId
                );
            }

            Map<String, Map<Long, Integer>> sessionMinutes =
                    scheduledMinutesBySectionSubject.getOrDefault(
                            sectionSubjectId,
                            Map.of()
                    );

            int actualLectureMinutes =
                    sessionMinutes.getOrDefault(
                                    "LECTURE",
                                    Map.of()
                            )
                            .values()
                            .stream()
                            .mapToInt(Integer::intValue)
                            .sum();

            int actualLabMinutes =
                    sessionMinutes.getOrDefault(
                                    "LABORATORY",
                                    Map.of()
                            )
                            .values()
                            .stream()
                            .mapToInt(Integer::intValue)
                            .sum();

            BigDecimal requiredLectureMinutes =
                    requiredLectureHours.multiply(BigDecimal.valueOf(60));

            BigDecimal requiredLabMinutes =
                    requiredLabHours.multiply(BigDecimal.valueOf(60));

            if (BigDecimal.valueOf(actualLectureMinutes)
                    .compareTo(requiredLectureMinutes) != 0) {

                throw new IllegalStateException(
                        "Lecture-hour requirement not satisfied for "
                                + "SectionSubject ID: " + sectionSubjectId
                                + ". Required: " + requiredLectureHours
                                + " hours, Scheduled: "
                                + actualLectureMinutes + " minutes."
                );
            }

            if (BigDecimal.valueOf(actualLabMinutes)
                    .compareTo(requiredLabMinutes) != 0) {

                throw new IllegalStateException(
                        "Laboratory-hour requirement not satisfied for "
                                + "SectionSubject ID: " + sectionSubjectId
                                + ". Required: " + requiredLabHours
                                + " hours, Scheduled: "
                                + actualLabMinutes + " minutes."
                );
            }
        }

        // Finally validate all hard constraints.
        ScheduleChromosome chromosome =
                new ScheduleChromosome(genes);

        CompleteScheduleValidator.ValidationResult result =
                completeScheduleValidator.validate(
                        chromosome,
                        termId
                );

        if (!result.feasible()) {

            String violations = result.violations()
                    .stream()
                    .map(violation ->
                            violation.getConstraintCode()
                                    + ": "
                                    + violation.getDescription()
                    )
                    .reduce(
                            (first, second) -> first + "; " + second
                    )
                    .orElse("Unknown hard-constraint violation.");

            throw new IllegalStateException(
                    "The current schedule version cannot be published "
                            + "because it violates hard constraints: "
                            + violations
            );
        }
    }

    private OfficialSchedule findOfficialScheduleForUpdate(Long id) {

        return officialScheduleRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Official schedule not found: " + id
                ));
    }

    private void validateOfficialScheduleId(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "Official schedule ID must be positive."
            );
        }
    }

    private SchedulePublicationEventResponse toResponse(
            SchedulePublicationEvent event) {

        SchedulePublicationEventResponse response =
                new SchedulePublicationEventResponse();

        response.setPublicationEventId(
                event.getPublicationEventId()
        );

        response.setOfficialScheduleId(
                event.getOfficialSchedule()
                        .getOfficialScheduleId()
        );

        response.setScheduleVersionId(
                event.getScheduleVersion()
                        .getScheduleVersionId()
        );

        response.setVersionNumber(
                event.getScheduleVersion().getVersionNumber()
        );

        response.setEventType(event.getEventType());

        if (event.getRelatedEvent() != null) {
            response.setRelatedEventId(
                    event.getRelatedEvent().getPublicationEventId()
            );
        }

        response.setPerformedBy(event.getPerformedBy());
        response.setRemarks(event.getRemarks());
        response.setEventAt(event.getEventAt());

        return response;
    }
}
