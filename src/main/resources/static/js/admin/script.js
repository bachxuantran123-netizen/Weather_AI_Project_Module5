document.addEventListener("DOMContentLoaded", function() {
    console.log("Admin Dashboard JS đã được load thành công!");

    const ctx = document.getElementById('trafficChart');

    if (ctx) {
        new Chart(ctx, {
            type: 'line', // Biểu đồ dạng đường
            data: {
                labels: window.dynamicChartLabels.length > 0 ? window.dynamicChartLabels : ['Thứ 2', 'Thứ 3', 'Thứ 4', 'Thứ 5', 'Thứ 6', 'Thứ 7', 'CN'],
                datasets: [{
                    label: 'Số lượng Request',
                    data: window.dynamicChartData.length > 0 ? window.dynamicChartData : [120, 190, 300, 250, 420, 380, 500],
                    borderColor: '#4e73df',
                    backgroundColor: 'rgba(78, 115, 223, 0.1)',
                    borderWidth: 3,
                    tension: 0.4,
                    fill: true,
                    pointBackgroundColor: '#fff',
                    pointBorderColor: '#4e73df',
                    pointBorderWidth: 2,
                    pointRadius: 4,
                    pointHoverRadius: 6
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false // Ẩn chú thích
                    }
                },
                scales: {
                    x: {
                        grid: { display: false } // Ẩn đường kẻ dọc
                    },
                    y: {
                        beginAtZero: true,
                        grid: { borderDash: [5, 5] } // Đường kẻ ngang nét đứt
                    }
                }
            }
        });
    }
});