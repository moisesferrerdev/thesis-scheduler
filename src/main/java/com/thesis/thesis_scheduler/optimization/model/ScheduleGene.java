
package com.thesis.thesis_scheduler.optimization.model;

public class ScheduleGene {

    private Long teachingAssignmentId;
    private Long instructorId;
    private Long sectionId;
    private Long roomId;
    private Long timeslotId;

    public ScheduleGene() {
    }

    public ScheduleGene(
            Long teachingAssignmentId,
            Long instructorId,
            Long sectionId,
            Long roomId,
            Long timeslotId) {
        this.teachingAssignmentId = teachingAssignmentId;
        this.instructorId = instructorId;
        this.sectionId = sectionId;
        this.roomId = roomId;
        this.timeslotId = timeslotId;
    }

    public Long getTeachingAssignmentId() {
        return teachingAssignmentId;
    }

    public void setTeachingAssignmentId(Long teachingAssignmentId) {
        this.teachingAssignmentId = teachingAssignmentId;
    }

    public Long getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(Long instructorId) {
        this.instructorId = instructorId;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Long getTimeslotId() {
        return timeslotId;
    }

    public void setTimeslotId(Long timeslotId) {
        this.timeslotId = timeslotId;
    }
}
