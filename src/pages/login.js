import React, { useState } from 'react';
import { Link } from "react-router-dom";
import axios from 'axios';

const handleLogin = async (username, password) => {
  try {
    const response = await axios.post('http://localhost:8080/api/seller/authenticate', {
      username,
      password,
    });
    const token = response.data.token;
    const seller_id = response.data.id;
    // Save JWT token to local storage
    localStorage.setItem('jwtToken', token);
    localStorage.setItem('seller_id', seller_id);

    // Redirect to seller page
    window.location.href = '/seller';
  } catch (error) {
    console.error('Login failed:', error.response.data);
    if (error.response && error.response.status === 401) {
      alert('Invalid username or password');
    } else {
      alert('An unexpected error occurred. Please try again.');
    }
  }
};

function LoginPage() {
  const [formData, setFormData] = useState({
    username: '',
    password: ''
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const { username, password } = formData;
    console.log('Login Data Submitted:', username, password);
    handleLogin(username, password);
  };

  return (
    <div style={{ maxWidth: '400px', margin: '0 auto', padding: '20px' }}>
      <h2>Login</h2>
      <form onSubmit={handleSubmit}>
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
          />
        </div>
        <button type="submit">Login</button>
      </form>
      <Link to="/" className="btn btn-link">
        Back to Home
      </Link>
    </div>
  );
}

export default LoginPage;
