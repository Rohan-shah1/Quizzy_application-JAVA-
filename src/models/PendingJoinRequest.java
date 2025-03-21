package models;

import java.sql.Timestamp;



public class PendingJoinRequest {

    private String name;
    private String phoneNumber;
    private String email;
    private Timestamp requestedAt;
    private String address;
    private String password;

    // Constructor for the PendingJoinRequest
    public PendingJoinRequest(String name, String phoneNumber, String email, Timestamp requestedAt) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.requestedAt = requestedAt;
    }

    // Constructor including address and password (used after acceptance)
    public PendingJoinRequest(String name, String phoneNumber, String email, Timestamp requestedAt, String address, String password) {
        this(name, phoneNumber, email, requestedAt);
        this.address = address;
        this.password = password;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(Timestamp requestedAt) {
        this.requestedAt = requestedAt;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}

   

