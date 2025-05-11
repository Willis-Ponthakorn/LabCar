import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import CarList from '../components/carlist';

const convertArrayBufferToBase64 = (arrayBuffer) => {
    return new Promise((resolve, reject) => {
      const blob = new Blob([arrayBuffer]);
      const reader = new FileReader();
      reader.onloadend = () => {
        const base64String = reader.result.split(',')[1];
        resolve(`data:image/jpeg;base64,${base64String}`);
      };
      reader.onerror = reject;
      reader.readAsDataURL(blob);
    });
  };


const SellerDetailPage = () => {
  const { sellerId } = useParams();  // Get sellerId from URL parameters
  const [seller, setSeller] = useState(null);
  const [sellerImage, setSellerImage] = useState(null);
  const [cars, setCars] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchSellerDetails = async () => {
      try {
        // Fetch seller's details
        const sellerResponse = await axios.get(`http://localhost:8080/api/seller/${sellerId}`);
        setSeller(sellerResponse.data);

        console.log(sellerResponse.data.id);
        const sellerImageResponse = await axios.get(
          `http://localhost:8080/api/seller/image/${sellerResponse.data.id}`,
          {
            responseType: 'arraybuffer', // Handle binary data
          }
        );

        const sellerImage = await convertArrayBufferToBase64(sellerImageResponse.data);
        setSellerImage(sellerImage);

        // Fetch cars from the seller
        const carsResponse = await axios.get(`http://localhost:8080/api/cars/seller?sellerId=${sellerId}`);

        const carsWithImages = await Promise.all(
            carsResponse.data.map(async (car) => {
              try {
                // Fetch the image for the car using its ID
                const imageResponse = await axios.get(
                  `http://localhost:8080/api/cars/image/${car.id}`,
                  {
                    responseType: 'arraybuffer', // Handle binary data
                  }
                );
    
                // Convert image data to Base64
                const base64Image = await convertArrayBufferToBase64(imageResponse.data);
    
                // Combine car data with its image
                return { ...car, image: base64Image };
              } catch (imageError) {
                console.error(`Error fetching image for car ${car.id}:`, imageError);
                // Add car without image if fetching fails
                return { ...car, image: null };
              }
            })
          );

        setCars(carsWithImages);

        setLoading(false);
      } catch (error) {
        console.error("Error fetching seller details:", error);
        setLoading(false);
      }
    };

    fetchSellerDetails();
  }, [sellerId]);

  if (loading) {
    return <p>Loading...</p>;
  }

  if (!seller) {
    return <p>Seller not found</p>;
  }

  // Filter available cars (where isSold is false)
  const availableCars = cars.filter(car => car.soldDate == null);

  return (
    <div>
      <button onClick={() => navigate('/')} style={{ marginBottom: '20px' }}>Back to Home</button>
      <h1>{seller.firstname} {seller.lastname}</h1>
      {sellerImage ? (
              <img 
              src={sellerImage}
              alt={`Seller ${seller.id}`} 
              className='seller-image'
              width="200px"
              height="200px"
              />
            ) : (
              <p>No image available</p>
        )}
      <p>Email: {seller.email}</p>
      <p>Mobile: {seller.mobileNumber}</p>
      <h2>Available Cars for Sale:</h2>
      {/* {availableCars.length > 0 ? (
        <ul>
          {availableCars.map((car) => (
            <li key={car.id}>
              <h3>{car.carInfo} - {"price " + car.price}</h3>
              {car.image ? (
              <img 
              src={car.image}
              alt={`Car ${car.id}`} 
              className='car-image'
              />
            ) : (
              <p>No image available</p>
            )}
            </li>
          ))}
        </ul>
      ) : (
        <p>No available cars at the moment.</p>
      )} */}
      {loading ? (
        <p>Loading...</p>
        ) : availableCars.length > 0 ? (
        <CarList cars={availableCars} />
        ) : (
        <p>No cars available.</p>
      )}
    </div>
  );
};

export default SellerDetailPage;
