// script.js
document.addEventListener("DOMContentLoaded", function() {
    console.log("[SYSTEM_INIT] Admin Dashboard Loaded.");

    const ctx = document.getElementById('trafficChart');
    if (ctx) {
        new Chart(ctx, {
            type: 'line',
            data: {
                labels: window.dynamicChartLabels.length > 0 ? window.dynamicChartLabels : ['Day 1', 'Day 2', 'Day 3', 'Day 4', 'Day 5', 'Day 6', 'Day 7'],
                datasets: [{
                    label: 'Requests',
                    data: window.dynamicChartData.length > 0 ? window.dynamicChartData : [12, 19, 30, 25, 42, 38, 50],
                    borderColor: '#8b5cf6',
                    backgroundColor: 'rgba(139, 92, 246, 0.15)',
                    borderWidth: 3,
                    tension: 0.4,
                    fill: true,
                    pointBackgroundColor: '#ffffff',
                    pointBorderColor: '#8b5cf6',
                    pointBorderWidth: 2,
                    pointRadius: 5,
                    pointHoverRadius: 7,
                    pointStyle: 'circle'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false }
                },
                scales: {
                    x: {
                        grid: {
                            color: '#edf2f7',
                            drawBorder: false
                        },
                        ticks: { color: '#a0aec0', font: { family: 'Nunito', weight: 600 } }
                    },
                    y: {
                        beginAtZero: true,
                        grid: {
                            color: '#edf2f7',
                            borderDash: [5, 5]
                        },
                        ticks: { color: '#a0aec0', font: { family: 'Nunito', weight: 600 } }
                    }
                }
            }
        });
    }

    // [FEATURE] Thực hiện Fetch API để tải dữ liệu (Giả lập gọi Location API)
    // Nếu ứng dụng Mobile gọi API bằng JWT, trên web chúng ta thao tác tương tự.
    fetchLocationsData();
});

async function fetchLocationsData() {
    // Lưu ý: Route này dùng để test Fetch API theo luồng yêu cầu ở phần Mobile.
    const token = localStorage.getItem("jwt_token"); // Giả định token lưu ở localStorage
    if (!token) return;

    try {
        const response = await fetch('/api/v1/locations', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const data = await response.json();
            console.log("[FETCH_SUCCESS] Locations loaded:", data);
            // Logic DOM Manipulation để render data ra bảng có thể viết tiếp tại đây
        } else {
            console.error("[FETCH_ERROR] Unauthorized or limit reached.");
        }
    } catch (error) {
        console.error("[NETWORK_ERROR]", error);
    }

    async function toggleUserStatus(userId, checkboxElement) {
        // Lưu lại trạng thái gốc phòng trường hợp API gọi lỗi thì hoàn tác
        const originalState = !checkboxElement.checked;

        // Tạm khóa nút bấm để tránh spam click
        checkboxElement.disabled = true;

        try {
            // Lấy token JWT từ LocalStorage (Bắt buộc phải có để gọi API bảo mật)
            const token = localStorage.getItem("jwt_token");

            const response = await fetch(`/api/admin/users/${userId}/toggle-lock`, {
                method: 'PUT',
                headers: {
                    'Authorization': token ? `Bearer ${token}` : '',
                    'Content-Type': 'application/json'
                }
            });

            const responseText = await response.text();

            if (response.ok) {
                // API thành công
                showCyberToast("SYSTEM", responseText, "success");
            } else {
                // Lỗi 403 (Không có quyền) hoặc lỗi hệ thống
                showCyberToast("LỖI TRUY CẬP", responseText || "Không thể thực hiện hành động này.", "error");
                checkboxElement.checked = originalState; // Hoàn tác UI
            }
        } catch (error) {
            showCyberToast("MẤT KẾT NỐI", "Lỗi mạng hoặc server không phản hồi.", "error");
            checkboxElement.checked = originalState; // Hoàn tác UI
        } finally {
            // Mở khóa nút bấm
            checkboxElement.disabled = false;
        }
    }

// Hàm render Toast Message sắc nét
    function showCyberToast(title, message, type) {
        const container = document.getElementById("cyber-toast-container");
        if (!container) return;

        const toast = document.createElement("div");
        toast.className = `cyber-toast ${type}`;

        // Icon thay đổi theo trạng thái
        const icon = type === 'success' ? '<i class="fa-solid fa-check text-primary me-2"></i>' : '<i class="fa-solid fa-triangle-exclamation text-warning me-2"></i>';

        toast.innerHTML = `
        <div class="fw-bold mb-1" style="font-size: 0.85rem; letter-spacing: 1px;">
            ${icon} [${title}]
        </div>
        <div style="font-size: 0.95rem; color: #8b978f;">${message}</div>
    `;

        container.appendChild(toast);

        // Tự động hủy sau 3 giây
        setTimeout(() => {
            toast.style.opacity = '0';
            toast.style.transform = 'translateX(100%)';
            setTimeout(() => toast.remove(), 300); // Đợi CSS transition chạy xong
        }, 3000);
    }
}