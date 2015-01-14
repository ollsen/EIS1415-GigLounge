package com.eis.transteinle.gigloungeprototype.user;

import java.util.List;

/**
 * Created by DerOlli on 14.01.15.
 */
public class User {

    private String id, email, firstName, lastName, country, city, postcode, address;
    private List bands;
    private List instruments;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List getBands() {
        return bands;
    }

    public void setBands(List bands) {
        this.bands = bands;
    }

    public List getInstruments() {
        return instruments;
    }

    public void setInstruments(List instruments) {
        this.instruments = instruments;
    }
}
