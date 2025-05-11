package com.carmarketplace.carmarketplace.dto;

public class SellerResponseDTO {
    private Long id;
    private String firstname;
    private String lastname;
    private String citizenID;
    private String mobileNumber;
    private String email;
    private Integer carCount;

    public SellerResponseDTO(Long id, String firstname, String lastname, String citizenID,
                             String mobileNumber, String email, Integer carCount) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.citizenID = citizenID;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.carCount = carCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getCitizenID() {
        return citizenID;
    }

    public void setCitizenID(String citizenID) {
        this.citizenID = citizenID;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getCarCount() {
        return carCount;
    }

    public void setCarCount(Integer carCount) {
        this.carCount = carCount;
    }
}
