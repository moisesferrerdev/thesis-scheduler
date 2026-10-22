package com.thesis.thesis_scheduler.mapper;

import com.thesis.thesis_scheduler.dto.SubjectOfferingRequest;
import com.thesis.thesis_scheduler.dto.SubjectOfferingResponse;
import com.thesis.thesis_scheduler.entity.SubjectOffering;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubjectOfferingMapper {

    @Mapping(target = "offeringId", ignore = true)
    @Mapping(target = "term", ignore = true)
    @Mapping(target = "curriculum", ignore = true)
    @Mapping(target = "subject", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    SubjectOffering toEntity(SubjectOfferingRequest request);

    @Mapping(target = "termId", source = "term.termId")
    @Mapping(target = "termName", source = "term.termName")
    @Mapping(target = "curriculumId", source = "curriculum.curriculumId")
    @Mapping(target = "curriculumYear", source = "curriculum.curriculumYear")
    @Mapping(target = "subjectId", source = "subject.subjectId")
    @Mapping(target = "subjectCode", source = "subject.subjectCode")
    @Mapping(target = "subjectName", source = "subject.subjectName")
    SubjectOfferingResponse toResponse(SubjectOffering entity);
}