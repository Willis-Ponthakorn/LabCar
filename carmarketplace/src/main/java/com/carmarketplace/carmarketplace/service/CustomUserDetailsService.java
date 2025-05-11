package com.carmarketplace.carmarketplace.service;

import com.carmarketplace.carmarketplace.model.Seller;
import com.carmarketplace.carmarketplace.repository.SellerProjection;
import com.carmarketplace.carmarketplace.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private final SellerRepository sellerRepository;

    public CustomUserDetailsService(SellerRepository sellerRepository) {
        this.sellerRepository = sellerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Find the seller by username
        SellerProjection seller = sellerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.builder()
                .username(seller.getUsername())
                .password(seller.getPassword())  // Password should be encoded
                .authorities("USER")
                .build();
    }
}
