package com.airbnb.shared.dto.entity;

import java.util.Date;


public class Reservation{

    private Integer id;
    private Residence residenceEntity;
    private Date arrivalDate;

    private Date departureDate;

    public Date getArrivalDate() {
        return arrivalDate;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setArrivalDate(Date arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Residence getResidenceEntity() {
        return residenceEntity;
    }

    public void setResidenceEntity(Residence residenceEntity) {
        this.residenceEntity = residenceEntity;
    }
}
