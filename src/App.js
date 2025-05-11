import React, { useState } from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import LoginPage from './pages/login';
import RegisterPage from './pages/register';
import HomePage from "./pages/home";
import SellerPage from './pages/seller';
import AddCarPage from './pages/addcar';
import SellerDetailPage from './pages/sellerdetail';

import { Navigate } from 'react-router-dom';

const PrivateRoute = ({ children }) => {
  const token = localStorage.getItem('jwtToken');
  return token ? children : <Navigate to="/login" />;
};

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/seller/:sellerId" element={<SellerDetailPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/seller" element={<PrivateRoute><SellerPage /></PrivateRoute>} />
        <Route path="/add-car" element={<PrivateRoute><AddCarPage /></PrivateRoute>} />
      </Routes>
    </Router>
  );
}

export default App;
