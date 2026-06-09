// script.js
document.addEventListener("DOMContentLoaded", function() {
    console.log("[SYSTEM_INIT] Admin Dashboard Loaded.");

    const ctx = document.getElementById('trafficChart');
    if (ctx) {
        new Chart(ctx, {
            type: 'line',
            data: {
                labels: window.dynamicChartLabels && window.dynamicChartLabels.length > 0 ? window.dynamicChartLabels : ['Day 1', 'Day 2', 'Day 3', 'Day 4', 'Day 5', 'Day 6', 'Day 7'],
                datasets: [{
                    label: 'Requests',
                    data: window.dynamicChartData && window.dynamicChartData.length > 0 ? window.dynamicChartData : [12, 19, 30, 25, 42, 38, 50],
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
                plugins: { legend: { display: false } },
                scales: {
                    x: {
                        grid: { color: '#edf2f7', drawBorder: false },
                        ticks: { color: '#a0aec0', font: { family: 'Nunito', weight: 600 } }
                    },
                    y: {
                        beginAtZero: true,
                        grid: { color: '#edf2f7', borderDash: [5, 5] },
                        ticks: { color: '#a0aec0', font: { family: 'Nunito', weight: 600 } }
                    }
                }
            }
        });
    }

    // 1. TÍNH NĂNG ĐĂNG XUẤT (LOGOUT)
    const logoutBtn = document.querySelector('.text-danger');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', async (e) => {
            e.preventDefault();
            const token = localStorage.getItem("jwt_token");

            if (token) {
                try {
                    await fetch('/api/auth/logout', {
                        method: 'POST',
                        headers: {
                            'Authorization': `Bearer ${token}`,
                            'Content-Type': 'application/json'
                        }
                    });
                } catch (err) { console.error("[LOGOUT_ERROR]", err); }
            }

            // Xóa rác, đưa về Login
            localStorage.removeItem("jwt_token");
            document.cookie = "jwt_token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
            window.location.href = "/admin-login";
        });
    }

    // 2. TÍNH NĂNG CẤP QUYỀN (THÊM TÀI KHOẢN)
    const addUserBtn = document.querySelector('.btn-cyber');
    if (addUserBtn) {
        addUserBtn.addEventListener('click', async () => {
            const username = prompt("Nhập tên đăng nhập cho tài khoản mới:");
            if (!username || username.trim() === "") return;

            const password = prompt("Nhập mật khẩu (tối thiểu 6 ký tự):");
            if (!password || password.trim() === "") return;

            const originalHtml = addUserBtn.innerHTML;
            addUserBtn.innerHTML = '<i class="fa-solid fa-spinner fa-spin me-2"></i>ĐANG XỬ LÝ...';
            addUserBtn.disabled = true;

            try {
                const response = await fetch('/api/auth/register', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ username: username.trim(), password: password.trim() })
                });

                const resJson = await response.json();
                if (response.ok && resJson.success) {
                    window.showCyberToast("THÀNH CÔNG", "Tạo tài khoản thành công!", "success");
                    setTimeout(() => window.location.reload(), 1500); // Reload để thấy user mới
                } else {
                    window.showCyberToast("LỖI", resJson.message || "Tên đăng nhập đã tồn tại!", "error");
                }
            } catch (error) {
                window.showCyberToast("MẤT KẾT NỐI", "Không thể kết nối máy chủ", "error");
            } finally {
                addUserBtn.innerHTML = originalHtml;
                addUserBtn.disabled = false;
            }
        });
    }
});

// 3. TÍNH NĂNG KHÓA / MỞ TÀI KHOẢN (Global Window Scope)
window.toggleUserStatus = async function(userId, checkboxElement) {
    const originalState = !checkboxElement.checked;
    checkboxElement.disabled = true;

    try {
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
            window.showCyberToast("SYSTEM", responseText, "success");
        } else {
            window.showCyberToast("LỖI TRUY CẬP", responseText || "Hành động bị từ chối.", "error");
            checkboxElement.checked = originalState;
        }
    } catch (error) {
        window.showCyberToast("MẤT KẾT NỐI", "Lỗi mạng / Server không phản hồi.", "error");
        checkboxElement.checked = originalState;
    } finally {
        checkboxElement.disabled = false;
    }
};

// 4. HÀM RENDER TOAST MESSAGE
window.showCyberToast = function(title, message, type) {
    const container = document.getElementById("cyber-toast-container");
    if (!container) return;

    const toast = document.createElement("div");
    toast.className = `cyber-toast ${type}`;

    const icon = type === 'success'
        ? '<i class="fa-solid fa-check text-primary me-2"></i>'
        : '<i class="fa-solid fa-triangle-exclamation text-warning me-2"></i>';

    toast.innerHTML = `
        <div class="fw-bold mb-1" style="font-size: 0.85rem; letter-spacing: 1px;">
            ${icon} [${title}]
        </div>
        <div style="font-size: 0.95rem; color: #8b978f;">${message}</div>
    `;

    container.appendChild(toast);
    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(100%)';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
};
