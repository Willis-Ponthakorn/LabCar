import React, { useState } from 'react';
import axios from 'axios';

const addCar = async (carInfo, price, carImage) => {
    const token = localStorage.getItem('jwtToken');
    const sellerId = localStorage.getItem('seller_id')
    const formData = new FormData();
    formData.append('carInfo', carInfo);
    formData.append('price', price);
    formData.append('sellerId', sellerId);
    formData.append('carImage', carImage);
    console.log(token)
  
    try {
      const response = await axios.post('http://localhost:8080/api/cars', formData, {
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'multipart/form-data',
        },
      });
      return response.data; // Newly added car
    } catch (error) {
      console.error('Error adding car:', error);
      throw error;
    }
  };
  

const AddCarPage = () => {
  const [carInfo, setCarInfo] = useState('');
  const [price, setPrice] = useState('');
  const [carImage, setCarImage] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await addCar(carInfo, price, carImage);
      alert('Car added successfully!');
      window.location.href = '/seller';
    } catch (error) {
      console.error('Failed to add car:', error);
      alert('Error adding car. Please try again.');
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="text"
        placeholder="Car Info"
        value={carInfo}
        onChange={(e) => setCarInfo(e.target.value)}
        required
      />
      <input
        type="number"
        placeholder="Price"
        value={price}
        onChange={(e) => setPrice(e.target.value)}
        required
      />
      <input
        type="file"
        onChange={(e) => setCarImage(e.target.files[0])}
        required
      />
      <button type="submit">Add Car</button>
    </form>
  );
};

export default AddCarPage;
