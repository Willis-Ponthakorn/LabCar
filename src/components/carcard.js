import React from "react";

const CarCard = ({ car, seller, onRemove, onMarkAsSold }) => {
  return (
    <div
        style={{
          border: "1px solid #ddd",
          borderRadius: "8px",
          padding: "1rem",
          width: "200px",
          textAlign: "center",
        }}
    >
        {car.image ? (
              <img 
              src={car.image}
              alt={`Car ${car.id}`} 
              className='car-image'
              width="200px"
              height="200px"
              />
            ) : (
              <p>No image available</p>
        )}
        <h3>{car.carInfo}</h3>
        <p>Price: {car.price}</p>
        {car.soldDate ? (
          <p>Sold on: {new Date(car.soldDate).toLocaleDateString('en-GB', { day: '2-digit', month: 'long', year: 'numeric' })}</p>
        ) : (
          <p>Status: Available</p>
        )}
        {seller && !car.soldDate && (
        <>
          <button onClick={() => onMarkAsSold(car.id)}>Mark as Sold</button>
          <button onClick={() => onRemove(car.id)}>Remove</button>
        </>
      )}
    </div>
  );
};

export default CarCard;
