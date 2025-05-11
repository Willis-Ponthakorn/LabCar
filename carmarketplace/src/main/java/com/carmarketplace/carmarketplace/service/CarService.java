package com.carmarketplace.carmarketplace.service;

import com.carmarketplace.carmarketplace.model.Car;
import com.carmarketplace.carmarketplace.model.Seller;
import com.carmarketplace.carmarketplace.repository.CarProjection;
import com.carmarketplace.carmarketplace.repository.CarRepository;
import com.carmarketplace.carmarketplace.repository.SellerProjection;
import com.carmarketplace.carmarketplace.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.time.format.DateTimeFormatter;

@Service
public class CarService {
    @Autowired
    private CarRepository carRepository;

    @Autowired
    private SellerRepository sellerRepository;

    public Car addCar(Car car) throws Exception {
        return carRepository.save(car);
    }

    public byte[] encryptImage(byte[] imageData) throws Exception {
        SecretKey key = generateKey();
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(imageData);
    }

    public byte[] decryptImage(byte[] encryptedData) throws Exception {
        SecretKey key = generateKey();
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encryptedData);
    }

    private SecretKey generateKey() throws Exception {
        String encryptionKey = "12345678901234567890123456789012";
        byte[] keyBytes = encryptionKey.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(keyBytes, "AES");
    }

    // Get all cars by seller
    public List<CarProjection> getCarsBySellerID(Long seller_id) throws Exception {
        Optional<SellerProjection> seller = sellerRepository.findSellerById(seller_id);

        if (seller.isPresent()) {
            List<CarProjection> cars = carRepository.findBySellerId(seller.get().getId());
            return cars;
        }
        else {
            throw new Exception("No seller found with the provided name.");
        }
    }

    public Integer getNumberOfCarsBySellerID(Long seller_id) {
        Optional<SellerProjection> seller = sellerRepository.findSellerById(seller_id);

        if(seller.isPresent()) {
            List<CarProjection> cars = carRepository.findBySellerId(seller.get().getId());
            return cars.size();
        }
        return 0;
    }

    // Get car by ID
    public Optional<Car> getCarById(Long id) {
        return carRepository.findById(id);
    }

    // Update car details
    public Car updateCar(Long id, Car updatedCar) {
        return carRepository.findById(id)
                .map(car -> {
                    // Update only the fields that need to be updated
                    car.setCarInfo(updatedCar.getCarInfo());
                    car.setCarImage(updatedCar.getCarImage());
                    car.setPrice(updatedCar.getPrice());
                    return carRepository.save(car);
                })
                .orElseThrow(() -> new RuntimeException("Car not found"));
    }

    public Car updateCarSold(Long id) {
        return carRepository.findById(id)
                .map( car -> {
                    car.setSoldDate(LocalDate.now());
                    return carRepository.save(car);
                })
                .orElseThrow(() -> new RuntimeException("Car not found"));
    }

    public void deleteCar(Long id) {
        if (carRepository.existsById(id)) {
            carRepository.deleteById(id);
        }
    }

    public int getTotalCarsSold(Long sellerId) {
        Optional<SellerProjection> seller = sellerRepository.findSellerById(sellerId);

        if (seller.isPresent()) {
            // Retrieve all cars from this seller
            List<CarProjection> cars = carRepository.findBySellerId(seller.get().getId());

            // Count cars where isSold is true
            long soldCarCount = cars.stream()
                    .filter(car -> car.getSoldDate() != null)
                    .count();

            return (int) soldCarCount;  // Return the count of sold cars
        } else {
            throw new NoSuchElementException("No seller found with the provided ID.");
        }
    }

    public int getAvailableCarsCount(Long sellerId) {
        Optional<SellerProjection> seller = sellerRepository.findSellerById(sellerId);

        if (seller.isPresent()) {
            // Retrieve all cars from this seller
            List<CarProjection> cars = carRepository.findBySellerId(seller.get().getId());

            // Count cars where isSold is false (not sold)
            long notSoldCarCount = cars.stream()
                    .filter(car -> car.getSoldDate() == null) // Filter cars where isSold is false
                    .count();

            return (int) notSoldCarCount;  // Return the count of not sold cars
        } else {
            throw new NoSuchElementException("No seller found with the provided ID.");
        }
    }

    public double getTotalRevenue(Long sellerId) {
        Optional<SellerProjection> seller = sellerRepository.findSellerById(sellerId);

        if (seller.isPresent()) {
            // Retrieve all cars from this seller
            List<CarProjection> cars = carRepository.findBySellerId(seller.get().getId());

            // Sum the prices of cars that are sold, converting String to Double
            // Filter cars where isSold is true
            // Parse the price string to Double
            // Return 0.0 if parsing fails
            // Sum the prices

            return cars.stream()
                    .filter(car -> car.getSoldDate() != null) // Filter cars where isSold is true
                    .mapToDouble(car -> {
                        try {
                            // Parse the price string to Double
                            return Double.parseDouble(car.getPrice());
                        } catch (NumberFormatException e) {
                            return 0.0; // Return 0.0 if parsing fails
                        }
                    })
                    .sum();
        } else {
            throw new NoSuchElementException("No seller found with the provided ID.");
        }
    }

    public Map<String, Double> getCarsSoldRevenueByMonthAndYear(Long sellerId) {
        Optional<SellerProjection> seller = sellerRepository.findSellerById(sellerId);

        // Check if the seller exists
        if (seller.isPresent()) {
            // Fetch all cars for the given seller (using CarProjection)
            List<CarProjection> cars = carRepository.findBySellerId(seller.get().getId());

            // Create a Map to hold the grouped results (Year-Month -> Total Revenue)
            Map<String, Double> carsRevenueByMonth = new TreeMap<>();

            // DateTimeFormatter to format LocalDate into "yyyy-MM"
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM");

            // Iterate over each car and check if it has been sold (soldDate is not null)
            for (CarProjection car : cars) {
                if (car.getSoldDate() != null) { // Check if car has a soldDate

                    // Format the soldDate to "yyyy-MM"
                    String yearMonth = car.getSoldDate().format(dateFormatter);

                    // Convert the price from String to Double and sum it for the corresponding month
                    try {
                        double price = Double.parseDouble(car.getPrice());
                        carsRevenueByMonth.put(yearMonth, carsRevenueByMonth.getOrDefault(yearMonth, 0.0) + price);
                    } catch (NumberFormatException e) {
                        // In case of an invalid price format, you can log an error or ignore
                        System.err.println("Invalid price format for car: " + car.getCarInfo());
                    }
                }
            }

            return carsRevenueByMonth; // Return the total revenue by month-year
        } else {
            throw new NoSuchElementException("No seller found with the provided ID.");
        }
    }
}
