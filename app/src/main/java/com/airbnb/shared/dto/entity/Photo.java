package com.airbnb.shared.dto.entity;

public class Photo {

    private Integer photoId;

    private String path;

    private User user;

    private Residence residenceEntity;

    public Integer getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Integer photoId) {
        this.photoId = photoId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Residence getResidenceEntity() {
        return residenceEntity;
    }

    public void setResidenceEntity(Residence residenceEntity) {
        this.residenceEntity = residenceEntity;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}