
package com.thesis.thesis_scheduler.optimization.model;

public class ConstraintViolation {

    private String constraintCode;
    private String description;
    private int penalty;

    public ConstraintViolation() {
    }

    public ConstraintViolation(
            String constraintCode,
            String description,
            int penalty) {
        this.constraintCode = constraintCode;
        this.description = description;
        this.penalty = penalty;
    }

    public String getConstraintCode() {
        return constraintCode;
    }

    public void setConstraintCode(String constraintCode) {
        this.constraintCode = constraintCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPenalty() {
        return penalty;
    }

    public void setPenalty(int penalty) {
        this.penalty = penalty;
    }
}
