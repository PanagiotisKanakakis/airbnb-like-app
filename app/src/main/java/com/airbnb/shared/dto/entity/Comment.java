package com.airbnb.shared.dto.entity;

public class Comment{

    private Integer commentId;
    private String comment;
    private Integer grade;
    //private Residence residenceEntity;

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    /*public Residence getResidenceEntity() {
        return residenceEntity;
    }

    public void setResidenceEntity(Residence residenceEntity) {
        this.residenceEntity = residenceEntity;
    }*/
}
