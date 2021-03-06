package com.airbnb.shared.dto.entity;

import java.util.ArrayList;
import java.util.List;

public class Residence {

    private Integer residenceId;
    private String address;
    private Double geoX;
    private Double geoY;
    private Integer capacity;
    private Integer prize;
    private String type;
    private String rules;
    private String description;
    private Integer bathrooms;
    private Double size;
    private Integer bedrooms;
    private Boolean livingRoom;
    private String location;
    private String title;
    private Integer beds;

    private List<Photo> photoPaths = new ArrayList<>();

    //private List<User> users;
    private List<Comment> comments = new ArrayList<>();
    private List<Reservation> reservationInfo = new ArrayList<>();

    public Integer getResidenceId() {
        return residenceId;
    }

    public void setResidenceId(Integer residenceId) {
        this.residenceId = residenceId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getGeoX() {
        return geoX;
    }

    public void setGeoX(Double geoX) {
        this.geoX = geoX;
    }

    public Double getGeoY() {
        return geoY;
    }

    public void setGeoY(Double geoY) {
        this.geoY = geoY;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getPrize() {
        return prize;
    }

    public void setPrize(Integer prize) {
        this.prize = prize;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(Integer bathrooms) {
        this.bathrooms = bathrooms;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public Integer getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(Integer bedrooms) {
        this.bedrooms = bedrooms;
    }

    public Boolean getLivingRoom() {
        return livingRoom;
    }

    public void setLivingRoom(Boolean livingRoom) {
        this.livingRoom = livingRoom;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


   /* public void setUsers(List<User> users) {
        this.users = users;
    }*/

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Reservation> getReservationInfo() {
        return reservationInfo;
    }

    public void setReservationInfo(List<Reservation> reservationInfo) {
        this.reservationInfo = reservationInfo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getBeds() {
        return beds;
    }

    public void setBeds(Integer beds) {
        this.beds = beds;
    }

    public List<Photo> getPhotoPaths() {
        return photoPaths;
    }

    public void setPhotoPaths(List<Photo> photoPaths) {
        this.photoPaths = photoPaths;
    }
}
