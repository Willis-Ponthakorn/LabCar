package com.carmarketplace.carmarketplace.controller;

import com.carmarketplace.carmarketplace.dto.SellerStatisticsDTO;
import com.carmarketplace.carmarketplace.service.CarService;
import com.carmarketplace.carmarketplace.service.SellerService;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/seller")
public class SellerStatisticController {
    @Autowired
    private CarService carService;

    @Autowired
    private SellerService sellerService;

    // Endpoint to get overall statistics for the seller
    @GetMapping("/statistics/{sellerId}")
    public ResponseEntity<SellerStatisticsDTO> getSellerStatistics(@PathVariable Long sellerId) {
        try {
            int totalCarsSold = carService.getTotalCarsSold(sellerId);
            int availableCars = carService.getAvailableCarsCount(sellerId);
            double totalRevenue = carService.getTotalRevenue(sellerId);
            Map<String, Double> getCarsSoldRevenueByMonthAndYear = carService.getCarsSoldRevenueByMonthAndYear(sellerId);

            SellerStatisticsDTO statisticsDTO = new SellerStatisticsDTO(
                    totalCarsSold, availableCars, totalRevenue, getCarsSoldRevenueByMonthAndYear
            );
            return new ResponseEntity<>(statisticsDTO, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
