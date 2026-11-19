
package com.thesis.thesis_scheduler.service.impl;

import com.thesis.thesis_scheduler.dto.SchedulingConfigRequest;
import com.thesis.thesis_scheduler.dto.SchedulingConfigResponse;
import com.thesis.thesis_scheduler.entity.AcademicTerm;
import com.thesis.thesis_scheduler.entity.SchedulingConfig;
import com.thesis.thesis_scheduler.entity.Timeslot;
import com.thesis.thesis_scheduler.repository.AcademicTermRepository;
import com.thesis.thesis_scheduler.repository.SchedulingConfigRepository;
import com.thesis.thesis_scheduler.repository.TimeslotRepository;
import com.thesis.thesis_scheduler.service.SchedulingConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class SchedulingConfigServiceImpl
        implements SchedulingConfigService {

    // The current CBOM session placement algorithm uses fixed 30-minute blocks.
    private static final int TIMESLOT_INTERVAL_MINUTES = 30;

    private final SchedulingConfigRepository schedulingConfigRepository;
    private final AcademicTermRepository academicTermRepository;
    private final TimeslotRepository timeslotRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SchedulingConfigResponse> getAllConfigurations() {

        return schedulingConfigRepository
                .findAllByOrderByTerm_StartDateDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SchedulingConfigResponse getConfiguration(Long termId) {

        SchedulingConfig config = schedulingConfigRepository
                .findByTerm_TermId(termId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No scheduling configuration found for academic term ID: "
                                + termId
                ));

        return toResponse(config);
    }

    @Override
    public SchedulingConfigResponse saveConfiguration(
            SchedulingConfigRequest request) {

        validate(request);

        AcademicTerm term = academicTermRepository
                .findById(request.getTermId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Academic term not found: " + request.getTermId()
                ));

        SchedulingConfig config = schedulingConfigRepository
                .findByTerm_TermId(request.getTermId())
                .orElseGet(SchedulingConfig::new);

        List<Integer> operatingDays = request.getOperatingDays()
                .stream()
                .distinct()
                .sorted()
                .toList();

        String days = operatingDays.stream()
                .map(String::valueOf)
                .reduce((left, right) -> left + "," + right)
                .orElseThrow();

        config.setTerm(term);
        config.setOperatingDays(days);
        config.setOpeningTime(request.getOpeningTime());
        config.setClosingTime(request.getClosingTime());
        config.setTimeslotIntervalMinutes(
                request.getTimeslotIntervalMinutes()
        );

        SchedulingConfig savedConfig =
                schedulingConfigRepository.save(config);

        // The institution window is the source of the term's room scheduling grid.
        // Add missing 30-minute slots without deleting historical slots that may
        // already be referenced by immutable schedule versions.
        generateMissingTimeslots(savedConfig);

        return toResponse(savedConfig);
    }

    private void generateMissingTimeslots(SchedulingConfig config) {

        List<Timeslot> existingTimeslots = timeslotRepository
                .findByTerm_TermId(config.getTerm().getTermId());

        Set<String> existingKeys = new HashSet<>();

        for (Timeslot timeslot : existingTimeslots) {
            existingKeys.add(timeslotKey(
                    timeslot.getDayOfWeek(),
                    timeslot.getStartTime(),
                    timeslot.getEndTime(),
                    timeslot.getDurationMinutes()
            ));
        }

        List<Timeslot> missingTimeslots = new ArrayList<>();

        List<Integer> operatingDays = Arrays.stream(
                        config.getOperatingDays().split(","))
                .map(Integer::parseInt)
                .toList();

        for (Integer day : operatingDays) {

            LocalTime start = config.getOpeningTime();

            while (!start.plusMinutes(TIMESLOT_INTERVAL_MINUTES)
                    .isAfter(config.getClosingTime())) {

                LocalTime end = start.plusMinutes(
                        TIMESLOT_INTERVAL_MINUTES
                );

                String key = timeslotKey(
                        day.shortValue(),
                        start,
                        end,
                        TIMESLOT_INTERVAL_MINUTES
                );

                if (existingKeys.add(key)) {

                    Timeslot timeslot = new Timeslot();

                    timeslot.setTerm(config.getTerm());
                    timeslot.setDayOfWeek(day.shortValue());
                    timeslot.setStartTime(start);
                    timeslot.setEndTime(end);
                    timeslot.setDurationMinutes(
                            TIMESLOT_INTERVAL_MINUTES
                    );
                    timeslot.setSlotLabel(start + " - " + end);

                    missingTimeslots.add(timeslot);
                }

                start = end;
            }
        }

        if (!missingTimeslots.isEmpty()) {
            timeslotRepository.saveAll(missingTimeslots);
        }
    }

    private String timeslotKey(
            Short day,
            LocalTime start,
            LocalTime end,
            Integer durationMinutes) {

        return day + "|" + start + "|" + end + "|" + durationMinutes;
    }

    private void validate(SchedulingConfigRequest request) {

        if (request.getTermId() == null) {
            throw new IllegalArgumentException(
                    "Select an academic term."
            );
        }

        if (request.getOperatingDays() == null
                || request.getOperatingDays().isEmpty()) {

            throw new IllegalArgumentException(
                    "Select at least one operating day."
            );
        }

        // Monday = 1 through Sunday = 7.
        if (request.getOperatingDays().stream().anyMatch(day ->
                day == null || day < 1 || day > 7)) {

            throw new IllegalArgumentException(
                    "Operating days must be between 1 (Monday) "
                            + "and 7 (Sunday)."
            );
        }

        if (request.getOpeningTime() == null
                || request.getClosingTime() == null
                || !request.getOpeningTime()
                .isBefore(request.getClosingTime())) {

            throw new IllegalArgumentException(
                    "Closing time must be later than opening time."
            );
        }

        if (!Integer.valueOf(TIMESLOT_INTERVAL_MINUTES).equals(
                request.getTimeslotIntervalMinutes())) {

            throw new IllegalArgumentException(
                    "The current CBOM optimizer requires a 30-minute timeslot interval."
            );
        }

        long operatingMinutes = Duration.between(
                request.getOpeningTime(),
                request.getClosingTime()
        ).toMinutes();

        if (operatingMinutes
                % request.getTimeslotIntervalMinutes() != 0) {

            throw new IllegalArgumentException(
                    "The operating window must be evenly divisible "
                            + "by the selected timeslot interval."
            );
        }
    }

    private SchedulingConfigResponse toResponse(
            SchedulingConfig config) {

        AcademicTerm term = config.getTerm();

        List<Integer> operatingDays = Arrays.stream(
                        config.getOperatingDays().split(","))
                .map(Integer::parseInt)
                .toList();

        return new SchedulingConfigResponse(
                config.getConfigId(),
                term.getTermId(),
                term.getTermName(),
                term.getAcademicYear(),
                term.getSemester(),
                operatingDays,
                config.getOpeningTime(),
                config.getClosingTime(),
                config.getTimeslotIntervalMinutes()
        );
    }
}
