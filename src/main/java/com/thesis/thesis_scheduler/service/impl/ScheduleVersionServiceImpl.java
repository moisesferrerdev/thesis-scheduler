
package com.thesis.thesis_scheduler.service.impl;

import com.thesis.thesis_scheduler.dto.ManualScheduleEntryRequest;
import com.thesis.thesis_scheduler.dto.ManualScheduleVersionRequest;
import com.thesis.thesis_scheduler.dto.ScheduleVersionRequest;
import com.thesis.thesis_scheduler.dto.ScheduleVersionResponse;
import com.thesis.thesis_scheduler.entity.*;
import com.thesis.thesis_scheduler.mapper.ScheduleVersionMapper;
import com.thesis.thesis_scheduler.repository.*;
import com.thesis.thesis_scheduler.service.ScheduleVersionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thesis.thesis_scheduler.optimization.ga.CompleteScheduleValidator;
import com.thesis.thesis_scheduler.optimization.model.ScheduleChromosome;
import com.thesis.thesis_scheduler.optimization.model.ScheduleGene;

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
    private final ScheduleVersionMapper mapper;

    @Override
    public ScheduleVersionResponse create(ScheduleVersionRequest request) {
        ScheduleVersion entity = mapper.toEntity(request);

        entity.setGeneratedSchedule(
                generatedScheduleRepository.findById(
                                request.getGeneratedScheduleId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Generated schedule not found: "
                                        + request.getGeneratedScheduleId())));

        if (request.getParentVersionId() != null) {
            entity.setParentVersion(
                    repository.findById(request.getParentVersionId())
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "Parent schedule version not found: "
                                            + request.getParentVersionId())));
        }

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public ScheduleVersionResponse getById(Long id) {
        return mapper.toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleVersionResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public ScheduleVersionResponse update(
            Long id, ScheduleVersionRequest request) {
        findById(id);
        throw new UnsupportedOperationException(
                "Schedule versions are immutable. Create a new version instead.");
    }

    @Override
    public void delete(Long id) {
        findById(id);
        throw new UnsupportedOperationException(
                "Schedule versions cannot be deleted through this service.");
    }

    private ScheduleVersion findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Schedule version not found: " + id));
    }




    @Override
    @Transactional
    public ScheduleVersionResponse createManualVersion(
            ManualScheduleVersionRequest request) {

        // 1. Retrieve the parent version.
        ScheduleVersion parentVersion =
                repository.findById(request.getParentVersionId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Parent schedule version not found: "
                                        + request.getParentVersionId()
                        ));

        // 2. Retrieve the academic term.
        Long termId = parentVersion.getGeneratedSchedule()
                .getOptimizationRun()
                .getTerm()
                .getTermId();

        // 3. Prepare genes, entries, and scheduled-hour tracking.
        List<ScheduleGene> genes = new ArrayList<>();
        List<ScheduleEntry> entries = new ArrayList<>();

        // SectionSubject ID -> Session type -> Timeslot ID -> Duration.
        Map<Long, Map<String, Map<Long, Integer>>>
                scheduledMinutesBySectionSubject = new HashMap<>();

        for (ManualScheduleEntryRequest entryRequest : request.getEntries()) {

            TeachingAssignment assignment =
                    teachingAssignmentRepository.findById(
                            entryRequest.getAssignmentId()
                    ).orElseThrow(() -> new EntityNotFoundException(
                            "Teaching assignment not found: "
                                    + entryRequest.getAssignmentId()
                    ));

            Room room = roomRepository.findById(
                    entryRequest.getRoomId()
            ).orElseThrow(() -> new EntityNotFoundException(
                    "Room not found: " + entryRequest.getRoomId()
            ));

            Timeslot timeslot = timeslotRepository.findById(
                    entryRequest.getTimeslotId()
            ).orElseThrow(() -> new EntityNotFoundException(
                    "Timeslot not found: " + entryRequest.getTimeslotId()
            ));

            String sessionType = entryRequest.getSessionType();

            if (!"LECTURE".equals(sessionType)
                    && !"LABORATORY".equals(sessionType)) {
                throw new IllegalArgumentException(
                        "Invalid session type: " + sessionType
                );
            }

            if (assignment.getInstructor() == null
                    || assignment.getSectionSubject() == null
                    || assignment.getSectionSubject().getSection() == null) {

                throw new IllegalStateException(
                        "Teaching assignment has incomplete relationships: "
                                + assignment.getAssignmentId()
                );
            }

            if (timeslot.getDurationMinutes() == null
                    || timeslot.getDurationMinutes() <= 0) {

                throw new IllegalArgumentException(
                        "Invalid timeslot duration: "
                                + timeslot.getTimeslotId()
                );
            }

            SectionSubject sectionSubject = assignment.getSectionSubject();

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

            // Prepare schedule entry.
            ScheduleEntry entry = new ScheduleEntry();
            entry.setAssignment(assignment);
            entry.setRoom(room);
            entry.setTimeslot(timeslot);
            entry.setSessionType(sessionType);

            entries.add(entry);
        }

        // 4. Validate active teaching assignment completeness.
        List<TeachingAssignment> requiredAssignments =
                teachingAssignmentRepository.findActiveAssignmentsByTermId(termId);

        Set<Long> requiredAssignmentIds = new LinkedHashSet<>();

        for (TeachingAssignment assignment : requiredAssignments) {
            requiredAssignmentIds.add(assignment.getAssignmentId());
        }

        Set<Long> submittedAssignmentIds = new LinkedHashSet<>();

        for (ScheduleGene gene : genes) {
            submittedAssignmentIds.add(gene.getTeachingAssignmentId());
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

        // 5. Validate lecture and laboratory hours separately.
        Set<Long> validatedSectionSubjectIds = new HashSet<>();

        for (TeachingAssignment assignment : requiredAssignments) {

            SectionSubject sectionSubject = assignment.getSectionSubject();

            if (sectionSubject == null) {
                throw new IllegalStateException(
                        "Teaching assignment has no SectionSubject: "
                                + assignment.getAssignmentId()
                );
            }

            Long sectionSubjectId = sectionSubject.getSectionSubjectId();

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
                    sessionMinutes.getOrDefault("LECTURE", Map.of())
                            .values()
                            .stream()
                            .mapToInt(Integer::intValue)
                            .sum();

            // Calculate actual laboratory duration.
            int actualLabMinutes =
                    sessionMinutes.getOrDefault("LABORATORY", Map.of())
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

        // 6. Validate the complete timetable against hard constraints.
        ScheduleChromosome chromosome = new ScheduleChromosome(genes);

        CompleteScheduleValidator.ValidationResult validationResult =
                completeScheduleValidator.validate(chromosome, termId);

        if (!validationResult.feasible()) {

            String violations = validationResult.violations()
                    .stream()
                    .map(violation ->
                            violation.getConstraintCode()
                                    + ": "
                                    + violation.getDescription())
                    .reduce((first, second) -> first + "; " + second)
                    .orElse("Unknown scheduling constraint violation.");

            throw new IllegalArgumentException(
                    "Manual schedule revision is invalid: " + violations
            );
        }

        // 7. Determine the next version number.
        List<ScheduleVersion> existingVersions =
                repository.findByGeneratedSchedule_GeneratedScheduleIdOrderByVersionNumberAsc(
                        parentVersion.getGeneratedSchedule()
                                .getGeneratedScheduleId()
                );

        int nextVersionNumber = existingVersions.stream()
                .mapToInt(ScheduleVersion::getVersionNumber)
                .max()
                .orElse(0) + 1;

        // 8. Create the new immutable schedule version.
        ScheduleVersion newVersion = new ScheduleVersion();

        newVersion.setGeneratedSchedule(
                parentVersion.getGeneratedSchedule()
        );
        newVersion.setParentVersion(parentVersion);
        newVersion.setVersionNumber(nextVersionNumber);
        newVersion.setVersionType("MANUAL");
        newVersion.setVersionStatus("GENERATED");
        newVersion.setChangeSummary(request.getChangeSummary());
        newVersion.setCreatedBy(request.getCreatedBy());

        ScheduleVersion savedVersion = repository.save(newVersion);

        // 9. Attach all entries to the new version.
        for (ScheduleEntry entry : entries) {
            entry.setScheduleVersion(savedVersion);
        }

        scheduleEntryRepository.saveAll(entries);

        return mapper.toResponse(savedVersion);
    }




    @Override
    @Transactional(readOnly = true)
    public List<ScheduleVersionResponse> getByGeneratedScheduleId(
            Long generatedScheduleId) {

        if (!generatedScheduleRepository.existsById(generatedScheduleId)) {
            throw new EntityNotFoundException(
                    "Generated schedule not found: " + generatedScheduleId);
        }

        return repository
                .findByGeneratedSchedule_GeneratedScheduleIdOrderByVersionNumberAsc(
                        generatedScheduleId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}
