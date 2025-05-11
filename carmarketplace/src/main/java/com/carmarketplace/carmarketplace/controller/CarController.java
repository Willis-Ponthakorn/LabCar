package com.carmarketplace.carmarketplace.controller;

import com.carmarketplace.carmarketplace.model.Car;
import com.carmarketplace.carmarketplace.model.Seller;
import com.carmarketplace.carmarketplace.repository.CarProjection;
import com.carmarketplace.carmarketplace.service.CarService;
import com.carmarketplace.carmarketplace.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    @Autowired
    private CarService carService;

    @Autowired
    private SellerService sellerService;

    // Add a new car
    @PostMapping
    public ResponseEntity<Car> addCar(@RequestParam("carInfo") String carInfo,
                                      @RequestParam("price") String price,
                                      @RequestParam("sellerId") Long sellerId,
                                      @RequestParam("carImage") MultipartFile carImage) {
        try {
            // Encrypt the image
            byte[] encryptedImage = carService.encryptImage(carImage.getBytes());

            Optional<Seller> seller = sellerService.getById(sellerId);
            if (seller.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // Create and save the Car entity
            Car car = new Car();
            car.setCarInfo(carInfo);
            car.setPrice(price);
            car.setSeller(seller.get());
            car.setCarImage(encryptedImage);
            Car savedCar = carService.addCar(car);

            return new ResponseEntity<>(savedCar, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get all cars by a specific seller
    @GetMapping("/seller")
    public ResponseEntity<List<CarProjection>> getCarsBySeller(@RequestParam Long sellerId) {
        try {
            List<CarProjection> cars = carService.getCarsBySellerID(sellerId);
            return new ResponseEntity<>(cars, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Get a car by ID
    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        Optional<Car> car = carService.getCarById(id);
        return car.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Update a car's details
    @PutMapping("/{id}")
    public ResponseEntity<Car> updateCar(@PathVariable Long id, @RequestBody Car updatedCar) {
        try {
            Car updated = carService.updateCar(id, updatedCar);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // Car not found
        }
    }

    @GetMapping("/image/{carId}")
    public ResponseEntity<byte[]> getCarImage(@PathVariable Long carId) {
        try {
            Optional<Car> carOptional = carService.getCarById(carId);
            if (carOptional.isPresent()) {
                byte[] decryptedImage = carService.decryptImage(carOptional.get().getCarImage());
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
    
    @PutMapping("/sold/{id}")
    public ResponseEntity<Car> updateCarSold(@PathVariable Long id) {
        try {
            Car updated = carService.updateCarSold(id);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeCar(@PathVariable Long id) {
        try {
            carService.deleteCar(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Car not found
        }
    }
}
