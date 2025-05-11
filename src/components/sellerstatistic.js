import React, { useEffect, useState } from 'react';
import { Bar, Pie } from 'react-chartjs-2';
import axios from 'axios';
import { Chart as ChartJS, CategoryScale, ArcElement, Tooltip, Legend, BarElement, LinearScale } from 'chart.js';

ChartJS.register(CategoryScale, ArcElement, Tooltip, Legend, BarElement, LinearScale);

const SellerStatistics = ({ sellerId, token }) => {
  const [statistics, setStatistics] = useState(null);

  useEffect(() => {
    const fetchStatistics = async () => {
      try {
        const response = await axios.get(
          `http://localhost:8080/api/seller/statistics/${sellerId}`,
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
        console.log(response.data);
        setStatistics(response.data);
      } catch (error) {
        console.error('Error fetching statistics:', error);
      }
    };
    fetchStatistics();
  }, [sellerId, token]);

  // Pie chart data
  const pieData = {
    labels: ['Sold Cars', 'Available Cars'],
    datasets: [
      {
        data: [statistics?.totalCarsSold, statistics?.availableCars],
        backgroundColor: ['rgba(75, 192, 192, 0.5)', 'rgba(153, 102, 255, 0.5)'],
        hoverOffset: 4,
      },
    ],
  };

  // Bar chart data for sold cars by month
  const barData = {
    labels: Object.keys(statistics?.carSoldCountByMonthAndYear || {}),
    datasets: [
      {
        label: 'Revenue each Month',
        data: Object.values(statistics?.carSoldCountByMonthAndYear || {}),
        backgroundColor: 'rgba(54, 162, 235, 0.5)',
        borderColor: 'rgba(54, 162, 235, 1)',
        borderWidth: 1,
      },
    ],
  };

  // Chart options
  const options = {
    responsive: true,
    plugins: {
      tooltip: {
        callbacks: {
          label: (tooltipItem) => {
            return `${tooltipItem.label}: ${tooltipItem.raw} cars`;
          },
        },
      },
      legend: {
        position: 'top',
      },
    },
  };

  const barOptions = {
    responsive: true,
    scales: {
      x: {
        title: {
          display: true,
          text: 'Year-Month',
        },
      },
      y: {
        title: {
          display: true,
          text: 'Revenue',
        },
        beginAtZero: true,
      },
    },
  };

  return (
    <div>
      <h2>Seller Statistics</h2>
      {statistics ? (
        <>
          {/* Pie Chart */}
          { statistics.totalCarsSold + statistics.availableCars === 0 ? <p>No Statistics Available</p> :
          <div style={{ height: '300px', width: '300px' }}>
            <Pie data={pieData} options={options} />
          </div>
          }
          {/* Bar Chart */}
          { statistics.availableCars === 0 ? <p></p> 
          : ( statistics.totalCarsSold !== 0 ? (
          <div style={{ height: '400px', width: '600px' }}>
            <Bar data={barData} options={barOptions} />
          </div>)
          : (<p>No Revenue Statistics Available</p>)
          )}
          {/* Display statistics */}
          <div>
            <p>Total Cars Sold: {statistics.totalCarsSold}</p>
            <p>Available Cars: {statistics.availableCars}</p>
            <p>Total Revenue: {statistics.totalRevenue.toFixed(2)}</p>
          </div>
        </>
      ) : (
        <p>Loading statistics...</p>
      )}
    </div>
  );
};

export default SellerStatistics;
