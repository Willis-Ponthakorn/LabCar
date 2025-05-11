package com.carmarketplace.carmarketplace.service;

import com.carmarketplace.carmarketplace.model.Seller;
import com.carmarketplace.carmarketplace.repository.SellerProjection;
import com.carmarketplace.carmarketplace.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SellerService {

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Register a new seller
    public Seller registerSeller(Seller seller) {
        if (sellerRepository.existsByUsername(seller.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        seller.setPassword(passwordEncoder.encode(seller.getPassword()));
        return sellerRepository.save(seller);
    }

    // Get seller by ID
    public Optional<SellerProjection> getSellerById(Long id) {
        return sellerRepository.findSellerById(id);
    }

    public Optional<SellerProjection> getSellerByCitizenID(String citizenid) { return sellerRepository.findByCitizenID(citizenid);}

    // Get seller by username
    public Optional<SellerProjection> getSellerByUsername(String username) {
        return sellerRepository.findByUsername(username);
    }

    public Optional<SellerProjection> getSellerByFirstNameAndLastName(String firstName, String lastName) {
        return sellerRepository.findByFirstnameAndLastname(firstName, lastName);
    }

    public List<Seller> getAllSellers() {
        return sellerRepository.findAll();
    }

    public Optional<Seller> updateSeller(Long id, Seller seller) {
        if (sellerRepository.existsById(id)) {
            seller.setId(id);
            return Optional.of(sellerRepository.save(seller));
        }
        return Optional.empty();
    }

    public Optional<Seller> updateSellerImage(Long id, byte[] image) {
        Optional<SellerProjection> seller = sellerRepository.findSellerById(id);

        if (seller.isPresent()) {
            Seller existingSeller = new Seller();
            existingSeller.setId(seller.get().getId());
            existingSeller.setFirstname(seller.get().getFirstname());
            existingSeller.setLastname(seller.get().getLastname());
            existingSeller.setUsername(seller.get().getUsername());
            existingSeller.setPassword(seller.get().getPassword());
            existingSeller.setEmail(seller.get().getEmail());
            existingSeller.setCitizenID(seller.get().getCitizenID());
            existingSeller.setMobileNumber(seller.get().getMobileNumber());

            // Update the seller's image
            existingSeller.setSellerImage(image);

            // Save the updated seller to the database
            sellerRepository.save(existingSeller);
        }

        // Return an empty Optional if no seller was found with the given ID
        return Optional.empty();
    }

    public boolean deleteSeller(Long id) {
        if (sellerRepository.existsById(id)) {
            sellerRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<Seller> getById(Long sellerId) {
        return sellerRepository.findById(sellerId);
    }
}
