package com.airbnb.shared.dto.entity;


import java.util.ArrayList;
import java.util.List;

public class User {

    private String username;
    private String password;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private String city;
    private String salt;
    private Photo profilePhoto;
    private List<Role> roles = new ArrayList<>();
    private List<Residence> residences = new ArrayList<>();

    private List<Search> searchedLocations = new ArrayList<>();
    private List<Reservation> reservedResidences = new ArrayList<>();

    private Mailbox mailbox = new Mailbox();

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<Residence> getResidences() {
        return residences;
    }

    public void setResidences(List<Residence> residences) {
        this.residences = residences;
    }

    public List<Search> getSearchedLocations() {
        return searchedLocations;
    }

    public void setSearchedLocations(List<Search> searchedLocations) {
        this.searchedLocations = searchedLocations;
    }

    public List<Reservation> getReservedResidences() {
        return reservedResidences;
    }

    public void setReservedResidences(List<Reservation> reservedResidences) {
        this.reservedResidences = reservedResidences;
    }

    public User() {
    }

    public void setMailbox(Mailbox mailbox) {
        this.mailbox = mailbox;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Photo getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(Photo profilePhoto) {
        this.profilePhoto = profilePhoto;
    }
}
