package com.carmarketplace.carmarketplace.repository;

import java.time.LocalDate;
import java.util.Date;

public interface CarProjection {
    Long getId();
    String getCarInfo();
    String getPrice();
    Long getSellerId();
    LocalDate getSoldDate();
}
