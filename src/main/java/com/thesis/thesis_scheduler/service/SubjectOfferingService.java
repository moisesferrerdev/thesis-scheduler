
package com.thesis.thesis_scheduler.service;

import com.thesis.thesis_scheduler.dto.SubjectOfferingRequest;
import com.thesis.thesis_scheduler.dto.SubjectOfferingResponse;
import java.util.List;

public interface SubjectOfferingService {

    SubjectOfferingResponse create(SubjectOfferingRequest request);

    SubjectOfferingResponse getById(Long id);

    List<SubjectOfferingResponse> getAll();

    SubjectOfferingResponse update(Long id, SubjectOfferingRequest request);

    void delete(Long id);
}
