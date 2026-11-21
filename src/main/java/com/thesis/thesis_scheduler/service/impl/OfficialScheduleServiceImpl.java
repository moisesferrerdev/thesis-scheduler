
package com.thesis.thesis_scheduler.service.impl;

import com.thesis.thesis_scheduler.dto.OfficialScheduleRequest;
import com.thesis.thesis_scheduler.dto.OfficialScheduleResponse;

import com.thesis.thesis_scheduler.entity.AcademicTerm;
import com.thesis.thesis_scheduler.entity.GeneratedSchedule;
import com.thesis.thesis_scheduler.entity.GeneratedScheduleEntry;
import com.thesis.thesis_scheduler.entity.OfficialSchedule;
import com.thesis.thesis_scheduler.entity.ScheduleEntry;
import com.thesis.thesis_scheduler.entity.ScheduleVersion;

import com.thesis.thesis_scheduler.repository.AcademicTermRepository;
import com.thesis.thesis_scheduler.repository.GeneratedScheduleEntryRepository;
import com.thesis.thesis_scheduler.repository.GeneratedScheduleRepository;
import com.thesis.thesis_scheduler.repository.OfficialScheduleRepository;
import com.thesis.thesis_scheduler.repository.ScheduleEntryRepository;
import com.thesis.thesis_scheduler.repository.ScheduleVersionRepository;

import com.thesis.thesis_scheduler.service.OfficialScheduleService;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OfficialScheduleServiceImpl
        implements OfficialScheduleService {

    private final OfficialScheduleRepository officialScheduleRepository;
    private final AcademicTermRepository academicTermRepository;
    private final GeneratedScheduleRepository generatedScheduleRepository;
    private final GeneratedScheduleEntryRepository generatedScheduleEntryRepository;
    private final ScheduleVersionRepository scheduleVersionRepository;
    private final ScheduleEntryRepository scheduleEntryRepository;

    @Override
    public OfficialScheduleResponse createFromCandidate(
            OfficialScheduleRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Official schedule request is required."
            );
        }

        AcademicTerm term = academicTermRepository
                .findById(request.getTermId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Academic term not found: " + request.getTermId()
                ));

        if (officialScheduleRepository.existsByTerm_TermId(
                request.getTermId())) {

            throw new IllegalStateException(
                    "An official schedule already exists for academic term: "
                            + request.getTermId()
            );
        }

        GeneratedSchedule candidate = generatedScheduleRepository
                .findById(request.getGeneratedScheduleId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Generated schedule not found: "
                                + request.getGeneratedScheduleId()
                ));

        Long candidateTermId = candidate.getOptimizationRun()
                .getTerm()
                .getTermId();

        if (!candidateTermId.equals(term.getTermId())) {
            throw new IllegalArgumentException(
                    "The selected candidate does not belong to the requested academic term."
            );
        }

        if (!Boolean.TRUE.equals(candidate.getFeasible())
                || candidate.getHardConstraintViolations() == null
                || candidate.getHardConstraintViolations() != 0) {

            throw new IllegalArgumentException(
                    "Only feasible candidates with zero hard-constraint violations "
                            + "can become an official schedule."
            );
        }

        List<GeneratedScheduleEntry> candidateEntries =
                generatedScheduleEntryRepository
                        .findByGeneratedSchedule_GeneratedScheduleId(
                                candidate.getGeneratedScheduleId()
                        );

        if (candidateEntries.isEmpty()) {
            throw new IllegalStateException(
                    "The selected candidate has no schedule entries."
            );
        }

        /*
         * Create the official schedule.
         */
        OfficialSchedule officialSchedule = new OfficialSchedule();

        officialSchedule.setTerm(term);
        officialSchedule.setScheduleStatus("DRAFT");

        OfficialSchedule savedOfficialSchedule =
                officialScheduleRepository.save(officialSchedule);

        /*
         * Create the initial immutable schedule version.
         */
        ScheduleVersion initialVersion = new ScheduleVersion();

        initialVersion.setOfficialSchedule(savedOfficialSchedule);
        initialVersion.setGeneratedSchedule(candidate);
        initialVersion.setParentVersion(null);
        initialVersion.setVersionNumber(1);
        initialVersion.setVersionType("INITIAL");
        initialVersion.setVersionStatus("CURRENT");
        initialVersion.setChangeSummary(
                request.getChangeSummary() == null
                        || request.getChangeSummary().isBlank()
                        ? "Initial official schedule created from generated candidate "
                        + candidate.getCandidateNumber()
                        : request.getChangeSummary()
        );
        initialVersion.setCreatedBy(request.getCreatedBy());

        ScheduleVersion savedVersion =
                scheduleVersionRepository.save(initialVersion);

        /*
         * Copy candidate entries into the official schedule version.
         *
         * The original GeneratedScheduleEntry records are not modified.
         */
        List<ScheduleEntry> officialEntries = candidateEntries.stream()
                .map(this::copyToScheduleEntry)
                .peek(entry -> entry.setScheduleVersion(savedVersion))
                .toList();

        scheduleEntryRepository.saveAll(officialEntries);

        /*
         * Set the current version after the initial version is saved.
         */
        savedOfficialSchedule.setCurrentVersion(savedVersion);

        OfficialSchedule updatedOfficialSchedule =
                officialScheduleRepository.save(savedOfficialSchedule);

        return toResponse(updatedOfficialSchedule);
    }

    @Override
    @Transactional(readOnly = true)
    public OfficialScheduleResponse getById(Long id) {

        OfficialSchedule officialSchedule =
                officialScheduleRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Official schedule not found: " + id
                        ));

        return toResponse(officialSchedule);
    }

    @Override
    @Transactional(readOnly = true)
    public OfficialScheduleResponse getByTermId(Long termId) {

        OfficialSchedule officialSchedule =
                officialScheduleRepository.findByTerm_TermId(termId)
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Official schedule not found for term: " + termId
                        ));

        return toResponse(officialSchedule);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OfficialScheduleResponse> getAll() {

        return officialScheduleRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ScheduleEntry copyToScheduleEntry(
            GeneratedScheduleEntry source) {

        ScheduleEntry entry = new ScheduleEntry();

        entry.setAssignment(source.getAssignment());
        entry.setRoom(source.getRoom());
        entry.setTimeslot(source.getTimeslot());
        entry.setSessionType(source.getSessionType());

        return entry;
    }

    private OfficialScheduleResponse toResponse(
            OfficialSchedule officialSchedule) {

        OfficialScheduleResponse response =
                new OfficialScheduleResponse();

        response.setOfficialScheduleId(
                officialSchedule.getOfficialScheduleId()
        );

        response.setTermId(
                officialSchedule.getTerm().getTermId()
        );

        response.setTermName(
                officialSchedule.getTerm().getTermName()
        );

        response.setScheduleStatus(
                officialSchedule.getScheduleStatus()
        );

        response.setCreatedAt(officialSchedule.getCreatedAt());
        response.setUpdatedAt(officialSchedule.getUpdatedAt());

        if (officialSchedule.getCurrentVersion() != null) {

            ScheduleVersion currentVersion =
                    officialSchedule.getCurrentVersion();

            response.setCurrentVersionId(
                    currentVersion.getScheduleVersionId()
            );

            response.setCurrentVersionNumber(
                    currentVersion.getVersionNumber()
            );

            response.setSourceGeneratedScheduleId(
                    currentVersion.getGeneratedSchedule()
                            .getGeneratedScheduleId()
            );

            response.setSourceCandidateNumber(
                    currentVersion.getGeneratedSchedule()
                            .getCandidateNumber()
            );
        }

        if (officialSchedule.getPublishedVersion() != null) {
            response.setPublishedVersionId(
                    officialSchedule.getPublishedVersion()
                            .getScheduleVersionId()
            );
        }

        return response;
    }
}
