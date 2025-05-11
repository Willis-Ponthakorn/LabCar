package com.carmarketplace.carmarketplace.controller;

import com.carmarketplace.carmarketplace.dto.LoginResponseDTO;
import com.carmarketplace.carmarketplace.dto.SellerResponseDTO;
import com.carmarketplace.carmarketplace.model.Car;
import com.carmarketplace.carmarketplace.model.LoginForm;
import com.carmarketplace.carmarketplace.model.Seller;
import com.carmarketplace.carmarketplace.repository.SellerProjection;
import com.carmarketplace.carmarketplace.service.CarService;
import com.carmarketplace.carmarketplace.service.CustomUserDetailsService;
import com.carmarketplace.carmarketplace.service.JwtService;
import com.carmarketplace.carmarketplace.service.SellerService;
import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/seller")
@Validated
public class SellerController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    private final SellerService sellerService;
    private final CarService carService;

    @Autowired
    public SellerController(SellerService sellerService, CarService carService) {
        this.sellerService = sellerService;
        this.carService = carService;
    }

    // Endpoint to register a new seller
    @PostMapping("/register")
    public ResponseEntity<Seller> registerSeller(
            @RequestParam("firstname") String firstname,
            @RequestParam("lastname") String lastname,
            @RequestParam("citizenID") String citizenID,
            @RequestParam("mobileNumber") String mobileNumber,
            @RequestParam("email") String email,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("sellerImage") MultipartFile sellerImage) {
        try {

            byte[] encryptedImage = carService.encryptImage(sellerImage.getBytes());

            Seller seller = new Seller();
            seller.setFirstname(firstname);
            seller.setLastname(lastname);
            seller.setCitizenID(citizenID);
            seller.setMobileNumber(mobileNumber);
            seller.setEmail(email);
            seller.setUsername(username);
            seller.setPassword(password);
            seller.setSellerImage(encryptedImage);
            Seller registeredSeller = sellerService.registerSeller(seller);

            return new ResponseEntity<>(registeredSeller, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody LoginForm loginForm) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginForm.username(), loginForm.password())
            );

            // Generate JWT token if authenticated
            if (authentication.isAuthenticated()) {
                SellerProjection seller = sellerService.getSellerByUsername(loginForm.username())
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
                LoginResponseDTO loginResponseDTO = new LoginResponseDTO(
                        seller.getId(),
                        jwtService.generateToken(userDetailsService.loadUserByUsername(loginForm.username()))
                );
                return new ResponseEntity<>(loginResponseDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
            }
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint to get a seller by their ID
    @GetMapping("/{id}")
    public ResponseEntity<SellerProjection> getSellerByCitizenID(@PathVariable Long id) {
        Optional<SellerProjection> seller = sellerService.getSellerById(id);
        return seller.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


    // Endpoint to get all sellers
    @GetMapping("/")
    public ResponseEntity<List<SellerResponseDTO>> getAllSellers() throws Exception {
        try {
            List<Seller> sellers = sellerService.getAllSellers();
            List<SellerResponseDTO> sellerResponseDTOS = sellers.stream()
                    .map(seller -> new SellerResponseDTO(
                            seller.getId(),
                            seller.getFirstname(),
                            seller.getLastname(),
                            seller.getCitizenID(),
                            seller.getMobileNumber(),
                            seller.getEmail(),
                            carService.getAvailableCarsCount(seller.getId())
                    ))
                    .collect(Collectors.toList());
            for (Seller seller : sellers) {
                carService.getNumberOfCarsBySellerID(seller.getId());
            }
            return new ResponseEntity<>(sellerResponseDTOS, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Endpoint to update seller information
    @PutMapping("/{id}")
    public ResponseEntity<Seller> updateSeller(@PathVariable Long id, @Valid @RequestBody Seller seller) {
        Optional<Seller> updatedSeller = sellerService.updateSeller(id, seller);
        return updatedSeller.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/image/{selllerId}")
    public ResponseEntity<?> updateSellerImage(@PathVariable Long selllerId, @Valid @RequestBody MultipartFile sellerImage) {
        try {
            Optional<SellerProjection> seller = sellerService.getSellerById(selllerId);

            byte[] encryptedImage = carService.encryptImage(sellerImage.getBytes());

            if (seller.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            sellerService.updateSellerImage(selllerId, encryptedImage);

            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/image/{sellerId}")
    public ResponseEntity<byte[]> getSellerImage(@PathVariable Long sellerId) {
        try {
            Optional<Seller> sellerOptional = sellerService.getById(sellerId);
            if (sellerOptional.isPresent()) {
                byte[] decryptedImage = carService.decryptImage(sellerOptional.get().getSellerImage());
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(decryptedImage);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint to delete a seller
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeller(@PathVariable Long id) {
        boolean isDeleted = sellerService.deleteSeller(id);
        return isDeleted ? ResponseEntity.noContent().build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
