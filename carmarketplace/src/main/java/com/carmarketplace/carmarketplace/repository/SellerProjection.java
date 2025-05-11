package com.carmarketplace.carmarketplace.repository;

import jakarta.validation.constraints.NotBlank;

public interface SellerProjection {
    Long getId();
    String getFirstname();
    String getLastname();
    String getCitizenID();
    String getMobileNumber();
    String getEmail();
    String getUsername();
    String getPassword();
}
