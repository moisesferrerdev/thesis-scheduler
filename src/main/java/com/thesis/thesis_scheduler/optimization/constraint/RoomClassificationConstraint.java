
package com.thesis.thesis_scheduler.optimization.constraint;

import com.thesis.thesis_scheduler.entity.Room;
import com.thesis.thesis_scheduler.entity.RoomClassification;
import com.thesis.thesis_scheduler.entity.Subject;
import com.thesis.thesis_scheduler.entity.TeachingAssignment;
import com.thesis.thesis_scheduler.optimization.model.ScheduleChromosome;
import com.thesis.thesis_scheduler.optimization.model.ScheduleGene;
import com.thesis.thesis_scheduler.repository.RoomRepository;
import com.thesis.thesis_scheduler.repository.TeachingAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoomClassificationConstraint {

    private static final String ACTIVE = "ACTIVE";
    private static final String LECTURE = "LECTURE";
    private static final String LABORATORY = "LABORATORY";

    private final RoomRepository roomRepository;
    private final TeachingAssignmentRepository teachingAssignmentRepository;

    public boolean hasConflict(ScheduleChromosome chromosome) {

        if (chromosome == null || chromosome.getGenes() == null) {
            return true;
        }

        for (ScheduleGene gene : chromosome.getGenes()) {

            if (gene == null
                    || gene.getTeachingAssignmentId() == null
                    || gene.getRoomId() == null
                    || gene.getSessionType() == null) {
                return true;
            }

            TeachingAssignment assignment =
                    teachingAssignmentRepository
                            .findWithSubjectById(gene.getTeachingAssignmentId())
                            .orElse(null);

            Room room = roomRepository
                    .findWithClassificationById(gene.getRoomId())
                    .orElse(null);

            if (assignment == null || room == null) {
                return true;
            }

            if (!ACTIVE.equalsIgnoreCase(room.getStatus())) {
                return true;
            }

            RoomClassification roomClassification =
                    room.getClassification();

            if (roomClassification == null
                    || !ACTIVE.equalsIgnoreCase(
                    roomClassification.getStatus())) {
                return true;
            }

            String sessionType = gene.getSessionType();

            if (LECTURE.equalsIgnoreCase(sessionType)) {

                if (!LECTURE.equalsIgnoreCase(room.getRoomType())
                        || !LECTURE.equalsIgnoreCase(
                        roomClassification.getRoomType())) {
                    return true;
                }

            } else if (LABORATORY.equalsIgnoreCase(sessionType)) {

                if (!LABORATORY.equalsIgnoreCase(room.getRoomType())
                        || !LABORATORY.equalsIgnoreCase(
                        roomClassification.getRoomType())) {
                    return true;
                }

                if (assignment.getSectionSubject() == null
                        || assignment.getSectionSubject().getOffering() == null
                        || assignment.getSectionSubject()
                        .getOffering().getSubject() == null) {
                    return true;
                }

                Subject subject = assignment.getSectionSubject()
                        .getOffering().getSubject();

                if (subject.getRequiredLaboratoryClassification() == null) {
                    return true;
                }

                Long requiredClassificationId = subject
                        .getRequiredLaboratoryClassification()
                        .getClassificationId();

                Long roomClassificationId =
                        roomClassification.getClassificationId();

                if (requiredClassificationId == null
                        || !requiredClassificationId.equals(
                        roomClassificationId)) {
                    return true;
                }

            } else {
                return true;
            }
        }

        return false;
    }
}
