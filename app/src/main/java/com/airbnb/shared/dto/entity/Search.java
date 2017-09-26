package com.airbnb.shared.dto.entity;

import java.util.ArrayList;
import java.util.List;


public class Search{

    private Integer searchId;
    private String location;
    private List<User> users = new ArrayList<>();

    public Integer getSearchId() {
        return searchId;
    }

    public void setSearchId(Integer searchId) {
        this.searchId = searchId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
