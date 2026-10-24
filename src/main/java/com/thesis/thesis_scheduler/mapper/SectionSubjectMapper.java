
package com.thesis.thesis_scheduler.mapper;

import com.thesis.thesis_scheduler.dto.SectionSubjectRequest;
import com.thesis.thesis_scheduler.dto.SectionSubjectResponse;
import com.thesis.thesis_scheduler.entity.SectionSubject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SectionSubjectMapper {

    @Mapping(target = "sectionSubjectId", ignore = true)
    @Mapping(target = "section", ignore = true)
    @Mapping(target = "offering", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    SectionSubject toEntity(SectionSubjectRequest request);

    @Mapping(target = "sectionId", source = "section.sectionId")
    @Mapping(target = "sectionCode", source = "section.sectionCode")
    @Mapping(target = "offeringId", source = "offering.offeringId")
    @Mapping(target = "offeringCode", source = "offering.offeringCode")
    SectionSubjectResponse toResponse(SectionSubject entity);

    @Mapping(target = "sectionSubjectId", ignore = true)
    @Mapping(target = "section", ignore = true)
    @Mapping(target = "offering", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(
            SectionSubjectRequest request,
            @MappingTarget SectionSubject entity
    );
}
