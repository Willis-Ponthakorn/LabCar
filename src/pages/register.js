import React, { useState } from 'react';
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";

function RegisterPage() {
  const [formData, setFormData] = useState({
    firstname: '',
    lastname: '',
    citizenID: '',
    mobileNumber: '',
    email: '',
    username: '',
    password: '',
    sellerImage: null,
  });

  const [errors, setErrors] = useState({});

  const validateField = (name, value) => {
    let error = "";
    if (name === "email" && !/^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}$/.test(value)) {
      error = "Invalid email format";
    }
    if (name === "password" && !/(?=.*[0-9])(?=.*[!@#$%^&*])(?=.*[A-Za-z]).{8,}/.test(value)) {
      error = "Password must include letters, numbers, and special characters";
    }
    if (name === "mobileNumber" && !/^[0-9]{10}$/.test(value)) {
      error = "Mobile number must be exactly 10 digits";
    }
    if (name === "citizenID" && !/^[0-9]{13}$/.test(value)) {
      error = "Citizen ID must be exactly 13 digits";
    }
    
    setErrors({ ...errors, [name]: error });
  };


  const handleChange = (e) => {
    const { name, value } = e.target;
    validateField(name, value);
    setFormData({
      ...formData,
      [name]: value
    });
  };

  const handleFileChange = (e) => {
    setFormData({
      ...formData,
      sellerImage: e.target.files[0], // Store the selected file
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const data = new FormData();
    Object.entries(formData).forEach(([key, value]) => {
      data.append(key, value);
    });
    console.log(data)
    try {
      const response = await axios.post('http://localhost:8080/api/seller/register', data);
      console.log('Form Data Submitted:', response.data);
      alert('Register successfully!');
      window.location.href = '/login';
    } catch (error) {
      console.error('Error:', error.response ? error.response.data : error.message);
      if (error.response && error.response.data) {
        console.error('Error:', error.response.data);
  
        if (error.response.data === 'Username is already taken') {
          alert('The username is already taken. Please choose another one.');
        } else {
          alert(`Registration error: ${error.response.data}`);
        }
      } else {
        console.error('Error:', error.message);
        alert('An unexpected error occurred. Please try again.');
      }
    }
  };

  return (
    <div style={{ maxWidth: '400px', margin: '0 auto', padding: '20px' }}>
      <h2>Register</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label>First Name:</label>
          <input
            type="text"
            name="firstname"
            value={formData.firstname}
            onChange={handleChange}
            required
          />
        </div>
        <div>
          <label>Last Name:</label>
          <input
            type="text"
            name="lastname"
            value={formData.lastname}
            onChange={handleChange}
            required
          />
        </div>
        <div>
          <label>Citizen ID:</label>
          <input
            type="text"
            name="citizenID"
            value={formData.citizenID}
            onChange={handleChange}
            required
            pattern="[0-9]{13}"
            title="Citizen ID must be a 13-digit number"
          />
        </div>
        <div>
          <label>Mobile Number:</label>
          <input
            type="tel"
            name="mobileNumber"
            value={formData.mobileNumber}
            onChange={handleChange}
            required
            pattern="[0-9]{10}"
            title="Please enter a 10-digit mobile number"
          />
        </div>
        <div>
          <label>Email:</label>
          <input
            type="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            required
            pattern="[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}$"
            title="Please enter a valid email address"
          />
        </div>
        <div>
          <label>Username:</label>
          <input
            type="text"
            name="username"
            value={formData.username}
            onChange={handleChange}
            required
          />
        </div>
        <div>
          <label>Password:</label>
          <input
            type="password"
            name="password"
            value={formData.password}
            onChange={handleChange}
            required
            pattern="(?=.*[0-9])(?=.*[!@#$%^&*])(?=.*[A-Za-z]).{8,}"
            title="Password must be at least 8 characters long and include at least one letter, one number, and one special character"
          />
        </div>
        <div>
          <label>Profile Image:</label>
          <input
            type="file"
            name="sellerImage"
            accept="image/*"
            onChange={handleFileChange}
            required
          />
        </div>
        <button type="submit">Register</button>
      </form>
      <Link to="/" className="btn btn-link">
        Back to Home
      </Link>
    </div>
  );
}

export default RegisterPage;
