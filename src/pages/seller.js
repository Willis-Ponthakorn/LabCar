import axios from 'axios';
import { useEffect, useState } from 'react';
import "./seller.css";
import SellerStatistics from "../components/sellerstatistic";
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

const SellerPage = () => {
  const [cars, setCars] = useState([]);
  const [sellerImage, setSellerImage] = useState(null);
  const [profileImage, setProfileImage] = useState(null);
  const [seller, setSeller] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchCars = async () => {
      const token = localStorage.getItem('jwtToken');
      const seller_id = localStorage.getItem('seller_id');
      // console.log(seller_id);
      // console.log(token)
  
      try {
        const sellerResponse = await axios.get(`http://localhost:8080/api/seller/${seller_id}`);
        // console.log(sellerResponse.data)
        setSeller(sellerResponse.data);
        

        const response = await axios.get(
          `http://localhost:8080/api/cars/seller?sellerId=${seller_id}`,
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
        const sellerImageResponse = await axios.get(
          `http://localhost:8080/api/seller/image/${seller_id}`,
          {
            responseType: 'arraybuffer', // Handle binary data
          }
        );

        const sellerImage = await convertArrayBufferToBase64(sellerImageResponse.data);
        setProfileImage(sellerImage);
        // Use the car ID from the first response to fetch images
        const carsWithImages = await Promise.all(
          response.data.map(async (car) => {
            try {
              // Fetch the image for the car using its ID
              const imageResponse = await axios.get(
                `http://localhost:8080/api/cars/image/${car.id}`,
                {
                  headers: { Authorization: `Bearer ${token}` },
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

        // Update state with cars and their respective images
      setCars(carsWithImages);
      setLoading(false);
    } catch (error) {
      console.error('Error fetching cars:', error);
      setLoading(false);
    }
  };

    fetchCars();
  }, []);
  const seller_id = localStorage.getItem('seller_id');
  const token = localStorage.getItem('jwtToken')

  const updateImage = async (sellerImage) => {
    const formData = new FormData();
    formData.append('sellerImage', sellerImage);
    console.log(token)
  
    try {
      const response = await axios.put(`http://localhost:8080/api/seller/image/${seller_id}`, formData, {
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'multipart/form-data',
        },
      });
      return response.data;
    } catch (error) {
      console.error('Error adding car:', error);
      throw error;
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await updateImage(sellerImage);
      alert('Upload successfully!');
      window.location.reload();
    } catch (error) {
      console.error('Failed to add car:', error);
      alert('Error uploading image. Please try again.');
    }
  };

  return (
    <div>
    {loading ? (<p>Loading...</p>) : (
    <div> 
      <button onClick={handleLogout}>Logout</button>
      <h1>{seller.firstname} {seller.lastname}</h1>
      <div>
        {profileImage ? (
              <img 
              src={profileImage}
              alt={`Seller ${seller_id}`} 
              className='seller-image'
              width="200px"
              height="200px"
              />
            ) : (
              <p>No image available</p>
        )}
      </div>
      <p>Email: {seller.email}</p>
      <p>Mobile: {seller.mobileNumber}</p>
      <form onSubmit={handleSubmit}>
      <input
        type="file"
        onChange={(e) => setSellerImage(e.target.files[0])}
        required
      />
      <button type="submit">Upload</button>
    </form>
      <div>
      <h1>Seller Dashboard</h1>
      <SellerStatistics sellerId={seller_id} token={token} />
    </div>
      {/* <ul>
        {cars.map((car) => (
          <li key={car.id}>
            {car.carInfo} {'price ' + car.price} {car.image ? (
              <img 
              src={car.image}
              alt={`Car ${car.id}`} 
              className='car-image'
              />
            ) : (
              <p>No image available</p>
            )} - {car.isSold ? "Status: Sold" : "Status: Available"}
            {!car.isSold && (
            <button onClick={() => handleRemoveCar(car.id)}>Remove</button>
            )}
            {!car.isSold && (
            <button onClick={() => handleMarkAsSold(car.id)}>Mark as Sold</button>
            )}
          </li>
          
        ))}
      </ul> */}
        <CarList cars={cars} seller={true} onRemove={handleRemoveCar} onMarkAsSold={handleMarkAsSold}/>
      <button onClick={() => window.location.href = '/add-car'}>Add Car</button>
    </div> )}
    </div>
  );
};

const handleLogout = () => {
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('seller_id');

    window.location.href = '/login';
  };

const handleRemoveCar = async (carId) => {
  const token = localStorage.getItem('jwtToken');
  try {
    await axios.delete(`http://localhost:8080/api/cars/${carId}`, 
      {
        headers: { Authorization: `Bearer ${token}` },
      }
  );
    window.location.reload();
  } catch (error) {
    console.error('Error removing car:', error);
  }
};

const handleMarkAsSold = async (carId) => {
  const token = localStorage.getItem('jwtToken');
console.log(token);
try {
  await axios.put(
    `http://localhost:8080/api/cars/sold/${carId}`,
    {},
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
  window.location.reload();
} catch (error) {
  console.error('Error marking car as sold:', error);
}

};

export default SellerPage;
