import React from "react";
import { Link } from "react-router-dom";

const SellerCard = ({ seller }) => {
  return (
    <Link to={`/seller/${seller.id}`} style={{ textDecoration: 'none' }}>
      <div
        style={{
          border: "1px solid #ddd",
          borderRadius: "8px",
          padding: "1rem",
          width: "200px",
          textAlign: "center",
        }}
      >
        {seller.image ? (
              <img 
              src={seller.image}
              alt={`Seller ${seller.id}`} 
              className='seller-image'
              width= "200px"
              height="200px"
              />
            ) : (
              <p>No image available</p>
        )}
        <h3>{seller.firstname} {seller.lastname}</h3>
        <p>Email: {seller.email}</p>
        <p>Mobile: {seller.mobileNumber}</p>
        <p>Available Cars: {seller.carCount}</p>
      </div>
    </Link>
  );
};

export default SellerCard;
