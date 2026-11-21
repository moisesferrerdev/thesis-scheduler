
package com.thesis.thesis_scheduler.service.impl;

import com.thesis.thesis_scheduler.dto.*;
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
     * Initial schedule version creation is handled by
     * OfficialScheduleService.createFromCandidate().
     *
     * This prevents arbitrary schedule versions from being created
     * without validating the selected generated candidate.
     */
    @Override
    public ScheduleVersionResponse create(ScheduleVersionRequest request) {

        throw new UnsupportedOperationException(
                "Schedule versions cannot be created directly. "
                        + "Create an official schedule from a feasible "
                        + "generated candidate instead."
        );
    }

    /*
     * Retrieve one schedule version.
     */
    @Override
    @Transactional(readOnly = true)
    public ScheduleVersionResponse getById(Long id) {

        return mapper.toResponse(findById(id));
    }

    /*
     * Retrieve all schedule versions.
     */
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
     * Updates must create a new version instead.
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
     * Schedule versions cannot be deleted independently.
     */
    @Override
    public void delete(Long id) {

        findById(id);

        throw new UnsupportedOperationException(
                "Schedule versions cannot be deleted through this service."
        );
    }

    /*
     * Retrieve a schedule version by ID.
     */
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
     * Retrieve all versions belonging to an official schedule.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ScheduleVersionResponse> getByOfficialScheduleId(
            Long officialScheduleId) {

        if (officialScheduleId == null || officialScheduleId <= 0) {
            throw new IllegalArgumentException(
                    "Official schedule ID must be positive."
            );
        }

        if (!officialScheduleRepository.existsById(officialScheduleId)) {
            throw new EntityNotFoundException(
                    "Official schedule not found: " + officialScheduleId
            );
        }

        return repository
                .findByOfficialSchedule_OfficialScheduleIdOrderByVersionNumberAsc(
                        officialScheduleId
                )
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    /*
     * Revert an official schedule to the contents of an earlier version.
     *
     * A new version is created.
     *
     * parentVersion:
     *     The previous current version.
     *
     * copiedFromVersion:
     *     The historical version whose entries are restored.
     *
     * Existing versions and their entries are never modified.
     */
    @Override
    public ScheduleVersionResponse revertToVersion(
            Long officialScheduleId,
            RevertScheduleVersionRequest request) {

        if (officialScheduleId == null || officialScheduleId <= 0) {
            throw new IllegalArgumentException(
                    "Official schedule ID must be positive."
            );
        }

        if (request == null
                || request.getSourceVersionId() == null
                || request.getSourceVersionId() <= 0) {

            throw new IllegalArgumentException(
                    "Source schedule version ID is required."
            );
        }

        OfficialSchedule officialSchedule =
                officialScheduleRepository.findById(officialScheduleId)
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Official schedule not found: "
                                        + officialScheduleId
                        ));

        ScheduleVersion sourceVersion =
                findById(request.getSourceVersionId());

        // Ensure the selected historical version belongs to this schedule.
        if (sourceVersion.getOfficialSchedule() == null
                || !officialScheduleId.equals(
                sourceVersion.getOfficialSchedule()
                        .getOfficialScheduleId())) {

            throw new IllegalArgumentException(
                    "The selected source version does not belong "
                            + "to the requested official schedule."
            );
        }

        ScheduleVersion currentVersion =
                officialSchedule.getCurrentVersion();

        if (currentVersion == null) {
            throw new IllegalStateException(
                    "The official schedule has no current version."
            );
        }

        if (currentVersion.getScheduleVersionId()
                .equals(sourceVersion.getScheduleVersionId())) {

            throw new IllegalArgumentException(
                    "The selected version is already the current version."
            );
        }

        // Retrieve the historical version's entries.
        List<ScheduleEntry> sourceEntries =
                scheduleEntryRepository
                        .findByScheduleVersion_ScheduleVersionId(
                                sourceVersion.getScheduleVersionId()
                        );

        if (sourceEntries.isEmpty()) {
            throw new IllegalStateException(
                    "The selected source version contains no schedule entries."
            );
        }

        // Determine the next version number for this official schedule.
        List<ScheduleVersion> existingVersions =
                repository
                        .findByOfficialSchedule_OfficialScheduleIdOrderByVersionNumberAsc(
                                officialScheduleId
                        );

        int nextVersionNumber = existingVersions.stream()
                .mapToInt(ScheduleVersion::getVersionNumber)
                .max()
                .orElse(0) + 1;

        // Create a new immutable version.
        ScheduleVersion revertedVersion = new ScheduleVersion();

        revertedVersion.setOfficialSchedule(officialSchedule);

        // Preserve original generated candidate provenance.
        revertedVersion.setGeneratedSchedule(
                sourceVersion.getGeneratedSchedule()
        );

        // Preserve chronological and copy lineage separately.
        revertedVersion.setParentVersion(currentVersion);
        revertedVersion.setCopiedFromVersion(sourceVersion);

        revertedVersion.setVersionNumber(nextVersionNumber);
        revertedVersion.setVersionType("REVERT");
        revertedVersion.setVersionStatus("CURRENT");

        revertedVersion.setChangeSummary(
                request.getChangeSummary()
        );

        revertedVersion.setCreatedBy(
                request.getCreatedBy()
        );

        ScheduleVersion savedVersion =
                repository.save(revertedVersion);

        // Copy historical entries into the newly created version.
        List<ScheduleEntry> copiedEntries = sourceEntries.stream()
                .map(source -> {
                    ScheduleEntry copy = new ScheduleEntry();

                    copy.setScheduleVersion(savedVersion);
                    copy.setAssignment(source.getAssignment());
                    copy.setRoom(source.getRoom());
                    copy.setTimeslot(source.getTimeslot());
                    copy.setSessionType(source.getSessionType());

                    return copy;
                })
                .toList();

        scheduleEntryRepository.saveAll(copiedEntries);

        // Supersede the previous current version.
        currentVersion.setVersionStatus("SUPERSEDED");
        repository.save(currentVersion);

        // Update the official schedule's current version.
        officialSchedule.setCurrentVersion(savedVersion);
        officialScheduleRepository.save(officialSchedule);

        return mapper.toResponse(savedVersion);
    }

    /*
     * Create a new immutable version from a manual schedule revision.
     *
     * The parent version remains unchanged.
     * A new set of ScheduleEntry records is created.
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

        // 1. Retrieve the parent version.
        ScheduleVersion parentVersion =
                findById(request.getParentVersionId());

        // 2. Retrieve its official schedule.
        OfficialSchedule officialSchedule =
                parentVersion.getOfficialSchedule();

        if (officialSchedule == null) {
            throw new IllegalStateException(
                    "The parent schedule version is not associated "
                            + "with an official schedule."
            );
        }

        // 3. Ensure the parent is still the current version.
        ScheduleVersion currentVersion =
                officialSchedule.getCurrentVersion();

        if (currentVersion == null
                || !currentVersion.getScheduleVersionId()
                .equals(parentVersion.getScheduleVersionId())) {

            throw new IllegalStateException(
                    "Manual editing must be based on the current "
                            + "official schedule version."
            );
        }

        // 4. Retrieve the original generated candidate.
        GeneratedSchedule generatedSchedule =
                parentVersion.getGeneratedSchedule();

        if (generatedSchedule == null) {
            throw new IllegalStateException(
                    "The parent schedule version has no source "
                            + "generated candidate."
            );
        }

        // 5. Retrieve the academic term.
        Long termId = officialSchedule.getTerm().getTermId();

        // 6. Prepare genes and schedule entries.
        List<ScheduleGene> genes = new ArrayList<>();
        List<ScheduleEntry> entries = new ArrayList<>();

        /*
         * SectionSubject ID -> Session type -> Timeslot ID -> Duration.
         *
         * Timeslot durations are counted once for each
         * SectionSubject and session type.
         */
        Map<Long, Map<String, Map<Long, Integer>>>
                scheduledMinutesBySectionSubject = new HashMap<>();

        for (ManualScheduleEntryRequest entryRequest : request.getEntries()) {

            if (entryRequest.getAssignmentId() == null
                    || entryRequest.getRoomId() == null
                    || entryRequest.getTimeslotId() == null) {

                throw new IllegalArgumentException(
                        "Assignment ID, room ID, and timeslot ID are required."
                );
            }

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

            // Validate assignment relationships.
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

            // Build a gene for hard-constraint validation.
            ScheduleGene gene = new ScheduleGene(
                    assignment.getAssignmentId(),
                    assignment.getInstructor().getInstructorId(),
                    sectionSubject.getSection().getSectionId(),
                    room.getRoomId(),
                    timeslot.getTimeslotId()
            );

            gene.setSessionType(sessionType);
            genes.add(gene);

            // Track unique timeslots by SectionSubject and session type.
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

        // 7. Validate active teaching assignment completeness.
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

        // 8. Validate lecture and laboratory hours separately.
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

            // Compare minutes directly to avoid decimal division rounding.
            BigDecimal actualLectureMinutesDecimal =
                    BigDecimal.valueOf(actualLectureMinutes);

            BigDecimal requiredLectureMinutes =
                    requiredLectureHours.multiply(BigDecimal.valueOf(60));

            BigDecimal actualLabMinutesDecimal =
                    BigDecimal.valueOf(actualLabMinutes);

            BigDecimal requiredLabMinutes =
                    requiredLabHours.multiply(BigDecimal.valueOf(60));

            if (actualLectureMinutesDecimal.compareTo(
                    requiredLectureMinutes) != 0) {

                throw new IllegalArgumentException(
                        "Lecture-hour requirement not satisfied for "
                                + "SectionSubject ID: " + sectionSubjectId
                                + ". Required: " + requiredLectureHours
                                + " hours, Scheduled: "
                                + actualLectureMinutes + " minutes."
                );
            }

            if (actualLabMinutesDecimal.compareTo(
                    requiredLabMinutes) != 0) {

                throw new IllegalArgumentException(
                        "Laboratory-hour requirement not satisfied for "
                                + "SectionSubject ID: " + sectionSubjectId
                                + ". Required: " + requiredLabHours
                                + " hours, Scheduled: "
                                + actualLabMinutes + " minutes."
                );
            }
        }

        // 9. Validate the complete timetable against hard constraints.
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

        // 10. Determine the next version number within this official schedule.
        List<ScheduleVersion> existingVersions =
                repository
                        .findByOfficialSchedule_OfficialScheduleIdOrderByVersionNumberAsc(
                                officialSchedule.getOfficialScheduleId()
                        );

        int nextVersionNumber = existingVersions.stream()
                .mapToInt(ScheduleVersion::getVersionNumber)
                .max()
                .orElse(0) + 1;

        // 11. Create a new immutable manual version.
        ScheduleVersion newVersion = new ScheduleVersion();

        newVersion.setOfficialSchedule(officialSchedule);

        // Preserve original candidate provenance.
        newVersion.setGeneratedSchedule(generatedSchedule);

        // Parent is the previous current version.
        newVersion.setParentVersion(parentVersion);

        // A manual revision is based on a new submitted timetable.
        newVersion.setCopiedFromVersion(null);

        newVersion.setVersionNumber(nextVersionNumber);
        newVersion.setVersionType("MANUAL");
        newVersion.setVersionStatus("CURRENT");

        newVersion.setChangeSummary(
                request.getChangeSummary()
        );

        newVersion.setCreatedBy(
                request.getCreatedBy()
        );

        ScheduleVersion savedVersion =
                repository.save(newVersion);

        // 12. Attach the new entries to the new version.
        for (ScheduleEntry entry : entries) {
            entry.setScheduleVersion(savedVersion);
        }

        scheduleEntryRepository.saveAll(entries);

        // 13. Supersede the previous version.
        parentVersion.setVersionStatus("SUPERSEDED");
        repository.save(parentVersion);

        // 14. Update the official schedule's current version.
        officialSchedule.setCurrentVersion(savedVersion);
        officialScheduleRepository.save(officialSchedule);

        return mapper.toResponse(savedVersion);
    }

    /*
     * Retrieve versions associated with a generated candidate.
     */
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
