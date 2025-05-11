package com.carmarketplace.carmarketplace.repository;

import com.carmarketplace.carmarketplace.model.Car;
import com.carmarketplace.carmarketplace.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    // You can add custom queries if necessary
    List<CarProjection> findBySeller(Seller seller);
    List<CarProjection> findBySellerId(Long id);
}