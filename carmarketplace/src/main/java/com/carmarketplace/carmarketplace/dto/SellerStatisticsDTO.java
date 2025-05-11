package com.carmarketplace.carmarketplace.dto;

import java.util.Map;

public class SellerStatisticsDTO {
    private int totalCarsSold;
    private int availableCars;
    private double totalRevenue;
    private Map<String, Double> carSoldCountByMonthAndYear;
    
    public SellerStatisticsDTO(int totalCarsSold, int availableCars, double totalRevenue ,Map<String, Double> carSoldCountByMonthAndYear) {
        this.totalCarsSold = totalCarsSold;
        this.availableCars = availableCars;
        this.totalRevenue = totalRevenue;
        this.carSoldCountByMonthAndYear = carSoldCountByMonthAndYear;
    }
    
public int getTotalCarsSold() {
    return totalCarsSold;
}

public void setTotalCarsSold(int totalCarsSold) {
    this.totalCarsSold = totalCarsSold;
}

public int getAvailableCars() {
    return availableCars;
}

public void setAvailableCars(int availableCars) {
    this.availableCars = availableCars;
}

public double getTotalRevenue() {
    return totalRevenue;
}

public void setTotalRevenue(double totalRevenue) {
    this.totalRevenue = totalRevenue;
}

    public Map<String, Double> getCarSoldCountByMonthAndYear() {
        return carSoldCountByMonthAndYear;
    }

    public void setCarSoldCountByMonthAndYear(Map<String, Double> carSoldCountByMonthAndYear) {
        this.carSoldCountByMonthAndYear = carSoldCountByMonthAndYear;
    }
}
