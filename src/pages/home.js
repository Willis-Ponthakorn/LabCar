import React, { useEffect, useState } from "react";
import axios from "axios";
import SellerList from "../components/sellerlist";
import { Link } from "react-router-dom";
import "./home.css";
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Container from 'react-bootstrap/Container';

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

const HomePage = () => {
  const [sellers, setSellers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    axios
      .get("http://localhost:8080/api/seller/")
      .then((response) => {
        //console.log("Response Data:", response.data);
        setSellers(response.data);
        setLoading(false);
      })
      .catch((error) => {
        console.error("Error fetching sellers:", error);
        setLoading(false);
      });
  }, []);

  useEffect(() => {
    const fetchSellers = async () => {
      try {
        const response = await axios.get(
          `http://localhost:8080/api/seller/`,
        );
        // Use the seller ID from the first response to fetch images
        const sellersWithImages = await Promise.all(
          response.data.map(async (seller) => {
            try {
              // Fetch the image for the seller using its ID
              const imageResponse = await axios.get(
                `http://localhost:8080/api/seller/image/${seller.id}`,
                {
                  responseType: 'arraybuffer', // Handle binary data
                }
              );
  
              // Convert image data to Base64
              const base64Image = await convertArrayBufferToBase64(imageResponse.data);
  
              // Combine car data with its image
              return { ...seller, image: base64Image };
            } catch (imageError) {
              console.error(`Error fetching image for seller ${seller.id}:`, imageError);
              // Add car without image if fetching fails
              return { ...seller, image: null };
            }
          })
        );

        // Update state with cars and their respective images
      setSellers(sellersWithImages);
    } catch (error) {
      console.error('Error fetching cars:', error);
    }
  };

    fetchSellers();
  }, []);

  return (
    <div>
      <h1>Car Marketplace</h1>
      <div>
      <span style={{ display: "inline-block" }}>Don't have an account to be seller? </span>
      <Link to="/login" className="btn btn-link link-spacing">Login</Link>
      <Link to="/register" className="btn btn-link">Register</Link>
      </div>
      {loading ? <p>Loading...</p> : <SellerList sellers={sellers} />}
    </div>
  );
};

export default HomePage;
