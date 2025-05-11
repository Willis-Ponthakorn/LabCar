import React from "react";
import SellerCard from "./sellercard";

const SellerList = ({ sellers }) => {
  return (
    <div style={{ display: "flex", flexWrap: "wrap", gap: "1rem" }}>
      {sellers.map((seller) => (
        <SellerCard key={seller.id} seller={seller} />
      ))}
    </div>
  );
};

export default SellerList;
