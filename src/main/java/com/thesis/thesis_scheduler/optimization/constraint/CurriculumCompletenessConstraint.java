
package com.thesis.thesis_scheduler.optimization.constraint;

import com.thesis.thesis_scheduler.entity.SectionSubject;
import com.thesis.thesis_scheduler.entity.TeachingAssignment;
import com.thesis.thesis_scheduler.optimization.model.ScheduleChromosome;
import com.thesis.thesis_scheduler.optimization.model.ScheduleGene;
import com.thesis.thesis_scheduler.repository.SectionSubjectRepository;
import com.thesis.thesis_scheduler.repository.StudentSectionRepository;
import com.thesis.thesis_scheduler.repository.TeachingAssignmentRepository;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class CurriculumCompletenessConstraint {

    private static final String ACTIVE = "ACTIVE";

    private final StudentSectionRepository studentSectionRepository;
    private final SectionSubjectRepository sectionSubjectRepository;
    private final TeachingAssignmentRepository teachingAssignmentRepository;

    public boolean hasConflict(
            ScheduleChromosome chromosome,
            Long termId) {

        if (chromosome == null || chromosome.getGenes() == null) {
            return false;
        }

        if (termId == null) {
            return true;
        }

        Set<Long> scheduledSectionSubjectIds = new HashSet<>();

        for (ScheduleGene gene : chromosome.getGenes()) {
            if (gene == null || gene.getTeachingAssignmentId() == null) {
                continue;
            }

            TeachingAssignment assignment =
                    teachingAssignmentRepository
                            .findById(gene.getTeachingAssignmentId())
                            .orElse(null);

            if (assignment == null
                    || assignment.getSectionSubject() == null
                    || !ACTIVE.equalsIgnoreCase(
                    assignment.getAssignmentStatus())) {
                continue;
            }

            SectionSubject sectionSubject = assignment.getSectionSubject();

            if (sectionSubject.getSection() != null
                    && sectionSubject.getSection().getAcademicTerm() != null
                    && termId.equals(
                    sectionSubject.getSection()
                            .getAcademicTerm().getTermId())) {

                scheduledSectionSubjectIds.add(
                        sectionSubject.getSectionSubjectId());
            }
        }

        List<com.thesis.thesis_scheduler.entity.StudentSection> sections =
                studentSectionRepository
                        .findByAcademicTerm_TermId(termId);

        for (var section : sections) {
            List<SectionSubject> sectionSubjects =
                    sectionSubjectRepository
                            .findBySection_SectionId(section.getSectionId());

            for (SectionSubject sectionSubject : sectionSubjects) {
                if (sectionSubject.getOffering() == null
                        || sectionSubject.getOffering().getCurriculum() == null
                        || sectionSubject.getOffering().getTerm() == null) {
                    return true;
                }

                if (!termId.equals(
                        sectionSubject.getOffering().getTerm().getTermId())) {
                    continue;
                }

                Boolean required = sectionSubject.getOffering()
                        .getCurriculum().getRequired();

                if (Boolean.TRUE.equals(required)
                        && !scheduledSectionSubjectIds.contains(
                        sectionSubject.getSectionSubjectId())) {
                    return true;
                }
            }
        }

        return false;
    }
}
