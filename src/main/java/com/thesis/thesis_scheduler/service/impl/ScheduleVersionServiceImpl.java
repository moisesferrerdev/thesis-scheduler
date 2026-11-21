
package com.thesis.thesis_scheduler.service.impl;

import com.thesis.thesis_scheduler.dto.ManualScheduleEntryRequest;
import com.thesis.thesis_scheduler.dto.ManualScheduleVersionRequest;
import com.thesis.thesis_scheduler.dto.ScheduleVersionRequest;
import com.thesis.thesis_scheduler.dto.ScheduleVersionResponse;

import com.thesis.thesis_scheduler.entity.*;
import com.thesis.thesis_scheduler.mapper.ScheduleVersionMapper;
import com.thesis.thesis_scheduler.repository.*;
import com.thesis.thesis_scheduler.service.ScheduleVersionService;

import com.thesis.thesis_scheduler.optimization.ga.CompleteScheduleValidator;
import com.thesis.thesis_scheduler.optimization.model.ScheduleChromosome;
import com.thesis.thesis_scheduler.optimization.model.ScheduleGene;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleVersionServiceImpl implements ScheduleVersionService {

    private final CompleteScheduleValidator completeScheduleValidator;

    private final ScheduleEntryRepository scheduleEntryRepository;
    private final TeachingAssignmentRepository teachingAssignmentRepository;
    private final RoomRepository roomRepository;
    private final TimeslotRepository timeslotRepository;

    private final ScheduleVersionRepository repository;
    private final GeneratedScheduleRepository generatedScheduleRepository;
    private final OfficialScheduleRepository officialScheduleRepository;

    private final ScheduleVersionMapper mapper;

    /*
     * Generic creation is disabled.
     *
     * The initial schedule version must be created through
     * OfficialScheduleService.createFromCandidate().
     *
     * This ensures that candidate feasibility, academic term ownership,
     * and initial schedule-entry copying are validated consistently.
     */
    @Override
    public ScheduleVersionResponse create(ScheduleVersionRequest request) {

        throw new UnsupportedOperationException(
                "Schedule versions cannot be created directly. "
                        + "Create an official schedule from a feasible "
                        + "generated candidate instead."
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ScheduleVersionResponse getById(Long id) {

        return mapper.toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleVersionResponse> getAll() {

        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    /*
     * Schedule versions are immutable.
     * Changes must create a new version.
     */
    @Override
    public ScheduleVersionResponse update(
            Long id,
            ScheduleVersionRequest request) {

        findById(id);

        throw new UnsupportedOperationException(
                "Schedule versions are immutable. "
                        + "Create a new version instead."
        );
    }

    /*
     * Schedule versions cannot be deleted through this service.
     */
    @Override
    public void delete(Long id) {

        findById(id);

        throw new UnsupportedOperationException(
                "Schedule versions cannot be deleted through this service."
        );
    }

    private ScheduleVersion findById(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "Schedule version ID must be positive."
            );
        }

        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Schedule version not found: " + id
                ));
    }

    /*
     * Creates a new immutable schedule version from a manual revision.
     *
     * The new version:
     * - Belongs to the same OfficialSchedule as its parent.
     * - Retains the original GeneratedSchedule as provenance.
     * - References its parent version.
     * - Contains a new set of ScheduleEntry records.
     * - Becomes the OfficialSchedule's current version.
     *
     * The parent version and its entries are never overwritten.
     */
    @Override
    public ScheduleVersionResponse createManualVersion(
            ManualScheduleVersionRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Manual schedule version request is required."
            );
        }

        if (request.getParentVersionId() == null
                || request.getParentVersionId() <= 0) {

            throw new IllegalArgumentException(
                    "Parent schedule version ID must be positive."
            );
        }

        if (request.getEntries() == null
                || request.getEntries().isEmpty()) {

            throw new IllegalArgumentException(
                    "Manual schedule entries are required."
            );
        }

        // 1. Retrieve the parent schedule version.
        ScheduleVersion parentVersion =
                repository.findById(request.getParentVersionId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Parent schedule version not found: "
                                        + request.getParentVersionId()
                        ));

        // 2. Retrieve the official schedule.
        OfficialSchedule officialSchedule =
                parentVersion.getOfficialSchedule();

        if (officialSchedule == null) {
            throw new IllegalStateException(
                    "The parent schedule version is not associated "
                            + "with an official schedule."
            );
        }

        // 3. Retrieve the original generated candidate.
        GeneratedSchedule generatedSchedule =
                parentVersion.getGeneratedSchedule();

        if (generatedSchedule == null) {
            throw new IllegalStateException(
                    "The parent schedule version has no source "
                            + "generated candidate."
            );
        }

        // 4. Retrieve the academic term from the official schedule.
        Long termId = officialSchedule.getTerm().getTermId();

        // 5. Prepare genes, entries, and scheduled-hour tracking.
        List<ScheduleGene> genes = new ArrayList<>();
        List<ScheduleEntry> entries = new ArrayList<>();

        /*
         * SectionSubject ID -> Session type -> Timeslot ID -> Duration.
         *
         * This prevents duplicate timeslot duration counting
         * for the same SectionSubject and session type.
         */
        Map<Long, Map<String, Map<Long, Integer>>>
                scheduledMinutesBySectionSubject = new HashMap<>();

        for (ManualScheduleEntryRequest entryRequest : request.getEntries()) {

            // Retrieve teaching assignment.
            TeachingAssignment assignment =
                    teachingAssignmentRepository.findById(
                            entryRequest.getAssignmentId()
                    ).orElseThrow(() -> new EntityNotFoundException(
                            "Teaching assignment not found: "
                                    + entryRequest.getAssignmentId()
                    ));

            // Retrieve room.
            Room room = roomRepository.findById(
                    entryRequest.getRoomId()
            ).orElseThrow(() -> new EntityNotFoundException(
                    "Room not found: " + entryRequest.getRoomId()
            ));

            // Retrieve timeslot.
            Timeslot timeslot = timeslotRepository.findById(
                    entryRequest.getTimeslotId()
            ).orElseThrow(() -> new EntityNotFoundException(
                    "Timeslot not found: " + entryRequest.getTimeslotId()
            ));

            // Validate session type.
            String sessionType = entryRequest.getSessionType();

            if (!"LECTURE".equals(sessionType)
                    && !"LABORATORY".equals(sessionType)) {

                throw new IllegalArgumentException(
                        "Invalid session type: " + sessionType
                );
            }

            // Validate teaching assignment relationships.
            if (assignment.getInstructor() == null
                    || assignment.getSectionSubject() == null
                    || assignment.getSectionSubject().getSection() == null) {

                throw new IllegalStateException(
                        "Teaching assignment has incomplete relationships: "
                                + assignment.getAssignmentId()
                );
            }

            // Validate timeslot duration.
            if (timeslot.getDurationMinutes() == null
                    || timeslot.getDurationMinutes() <= 0) {

                throw new IllegalArgumentException(
                        "Invalid timeslot duration: "
                                + timeslot.getTimeslotId()
                );
            }

            SectionSubject sectionSubject =
                    assignment.getSectionSubject();

            // Build gene for hard-constraint validation.
            ScheduleGene gene = new ScheduleGene(
                    assignment.getAssignmentId(),
                    assignment.getInstructor().getInstructorId(),
                    sectionSubject.getSection().getSectionId(),
                    room.getRoomId(),
                    timeslot.getTimeslotId()
            );

            gene.setSessionType(sessionType);

            genes.add(gene);

            // Track unique timeslots by subject and session type.
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
                            timeslot.getTimeslotId(),
                            timeslot.getDurationMinutes()
                    );

            // Prepare a new schedule entry.
            ScheduleEntry entry = new ScheduleEntry();

            entry.setAssignment(assignment);
            entry.setRoom(room);
            entry.setTimeslot(timeslot);
            entry.setSessionType(sessionType);

            entries.add(entry);
        }

        // 6. Validate active teaching assignment completeness.
        List<TeachingAssignment> requiredAssignments =
                teachingAssignmentRepository.findActiveAssignmentsByTermId(
                        termId
                );

        Set<Long> requiredAssignmentIds = new LinkedHashSet<>();

        for (TeachingAssignment assignment : requiredAssignments) {
            requiredAssignmentIds.add(assignment.getAssignmentId());
        }

        Set<Long> submittedAssignmentIds = new LinkedHashSet<>();

        for (ScheduleGene gene : genes) {
            submittedAssignmentIds.add(
                    gene.getTeachingAssignmentId()
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

            throw new IllegalArgumentException(
                    "Manual schedule revision is incomplete or contains "
                            + "invalid teaching assignments. Missing assignments: "
                            + missingAssignmentIds
                            + ". Unexpected assignments: "
                            + unexpectedAssignmentIds
            );
        }

        // 7. Validate lecture and laboratory hours separately.
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

            // Calculate actual lecture duration.
            int actualLectureMinutes =
                    sessionMinutes.getOrDefault(
                                    "LECTURE",
                                    Map.of()
                            )
                            .values()
                            .stream()
                            .mapToInt(Integer::intValue)
                            .sum();

            // Calculate actual laboratory duration.
            int actualLabMinutes =
                    sessionMinutes.getOrDefault(
                                    "LABORATORY",
                                    Map.of()
                            )
                            .values()
                            .stream()
                            .mapToInt(Integer::intValue)
                            .sum();

            BigDecimal actualLectureHours =
                    BigDecimal.valueOf(actualLectureMinutes)
                            .divide(BigDecimal.valueOf(60));

            BigDecimal actualLabHours =
                    BigDecimal.valueOf(actualLabMinutes)
                            .divide(BigDecimal.valueOf(60));

            if (actualLectureHours.compareTo(requiredLectureHours) != 0) {

                throw new IllegalArgumentException(
                        "Lecture-hour requirement not satisfied for "
                                + "SectionSubject ID: " + sectionSubjectId
                                + ". Required: " + requiredLectureHours
                                + " hours, Scheduled: " + actualLectureHours
                                + " hours."
                );
            }

            if (actualLabHours.compareTo(requiredLabHours) != 0) {

                throw new IllegalArgumentException(
                        "Laboratory-hour requirement not satisfied for "
                                + "SectionSubject ID: " + sectionSubjectId
                                + ". Required: " + requiredLabHours
                                + " hours, Scheduled: " + actualLabHours
                                + " hours."
                );
            }
        }

        // 8. Validate the complete timetable against hard constraints.
        ScheduleChromosome chromosome =
                new ScheduleChromosome(genes);

        CompleteScheduleValidator.ValidationResult validationResult =
                completeScheduleValidator.validate(
                        chromosome,
                        termId
                );

        if (!validationResult.feasible()) {

            String violations = validationResult.violations()
                    .stream()
                    .map(violation ->
                            violation.getConstraintCode()
                                    + ": "
                                    + violation.getDescription()
                    )
                    .reduce(
                            (first, second) -> first + "; " + second
                    )
                    .orElse(
                            "Unknown scheduling constraint violation."
                    );

            throw new IllegalArgumentException(
                    "Manual schedule revision is invalid: " + violations
            );
        }

        /*
         * 9. Determine the next version number within this
         * official schedule, not within the generated candidate.
         */
        List<ScheduleVersion> existingVersions =
                repository
                        .findByOfficialSchedule_OfficialScheduleIdOrderByVersionNumberAsc(
                                officialSchedule.getOfficialScheduleId()
                        );

        int nextVersionNumber = existingVersions.stream()
                .mapToInt(ScheduleVersion::getVersionNumber)
                .max()
                .orElse(0) + 1;

        // 10. Create a new immutable schedule version.
        ScheduleVersion newVersion = new ScheduleVersion();

        // Preserve official schedule ownership.
        newVersion.setOfficialSchedule(officialSchedule);

        // Preserve original generated candidate provenance.
        newVersion.setGeneratedSchedule(generatedSchedule);

        // Preserve version lineage.
        newVersion.setParentVersion(parentVersion);

        newVersion.setVersionNumber(nextVersionNumber);
        newVersion.setVersionType("MANUAL");
        newVersion.setVersionStatus("GENERATED");

        newVersion.setChangeSummary(
                request.getChangeSummary()
        );

        newVersion.setCreatedBy(
                request.getCreatedBy()
        );

        ScheduleVersion savedVersion =
                repository.save(newVersion);

        // 11. Attach all entries to the new version.
        for (ScheduleEntry entry : entries) {
            entry.setScheduleVersion(savedVersion);
        }

        scheduleEntryRepository.saveAll(entries);

        /*
         * 12. Update the official schedule's current version.
         *
         * The previous version and its entries remain unchanged.
         */
        officialSchedule.setCurrentVersion(savedVersion);

        officialScheduleRepository.save(officialSchedule);

        return mapper.toResponse(savedVersion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleVersionResponse> getByGeneratedScheduleId(
            Long generatedScheduleId) {

        if (generatedScheduleId == null || generatedScheduleId <= 0) {
            throw new IllegalArgumentException(
                    "Generated schedule ID must be positive."
            );
        }

        if (!generatedScheduleRepository.existsById(generatedScheduleId)) {
            throw new EntityNotFoundException(
                    "Generated schedule not found: " + generatedScheduleId
            );
        }

        return repository
                .findByGeneratedSchedule_GeneratedScheduleIdOrderByVersionNumberAsc(
                        generatedScheduleId
                )
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}
