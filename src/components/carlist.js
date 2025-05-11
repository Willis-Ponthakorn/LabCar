import React from "react";
import CarCard from "./carcard";

const CarList = ({ cars, seller, onRemove, onMarkAsSold }) => {
  return (
    <div style={{ display: "flex", flexWrap: "wrap", gap: "1rem" }}>
      {cars.map((car) => (
        <CarCard key={car.id} car={car} seller={seller} onRemove={onRemove} onMarkAsSold={onMarkAsSold}/>
      ))}
    </div>
  );
};

export default CarList;
