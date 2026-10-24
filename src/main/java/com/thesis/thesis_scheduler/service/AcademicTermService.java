
package com.thesis.thesis_scheduler.service;

import com.thesis.thesis_scheduler.dto.AcademicTermRequest;
import com.thesis.thesis_scheduler.dto.AcademicTermResponse;
import java.util.List;

public interface AcademicTermService {

    AcademicTermResponse create(AcademicTermRequest request);

    AcademicTermResponse getById(Long id);

    List<AcademicTermResponse> getAll();

    AcademicTermResponse update(Long id, AcademicTermRequest request);

    void delete(Long id);
}
