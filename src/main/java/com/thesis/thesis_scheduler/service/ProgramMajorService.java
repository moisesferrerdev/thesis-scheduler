
package com.thesis.thesis_scheduler.service;

import com.thesis.thesis_scheduler.dto.ProgramMajorRequest;
import com.thesis.thesis_scheduler.dto.ProgramMajorResponse;

import java.util.List;

public interface ProgramMajorService {

    ProgramMajorResponse create(ProgramMajorRequest request);

    ProgramMajorResponse getById(Long id);

    List<ProgramMajorResponse> getAll();

    List<ProgramMajorResponse> getByProgramId(Long programId);

    ProgramMajorResponse update(Long id, ProgramMajorRequest request);

    void delete(Long id);
}
