package com.carmarketplace.carmarketplace.repository;

import com.carmarketplace.carmarketplace.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {
    Optional<SellerProjection> findSellerById(Long sellerId);
    Optional<SellerProjection> findByUsername(String username);
    Optional<SellerProjection> findByFirstnameAndLastname(String firstName, String lastName);
    Optional<SellerProjection> findByCitizenID(String citizenID);
    boolean existsByUsername(String username);

    List<Seller> getSellersById(Long id);
}
