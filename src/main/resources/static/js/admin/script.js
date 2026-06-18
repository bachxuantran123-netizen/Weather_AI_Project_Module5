// script.js
document.addEventListener("DOMContentLoaded", function() {
    console.log("[SYSTEM_INIT] Admin Dashboard Loaded.");

    async function fetchDashboardStats() {
        const token = localStorage.getItem("jwt_token");
        if (!token) return;

        try {
            const res = await fetch('/api/admin/stats', {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            if (res.ok) {
                const json = await res.json();
                if (json.success) {
                    const data = json.data;
                    
                    const elTotalRequests = document.getElementById('totalRequests');
                    if (elTotalRequests) elTotalRequests.innerText = data.totalRequests;
                    
                    const elAiAdvices = document.getElementById('aiAdvicesGenerated');
                    if (elAiAdvices) elAiAdvices.innerText = data.aiAdvicesGenerated;
                    
                    const elActiveUsers = document.getElementById('activeUsers');
                    if (elActiveUsers) elActiveUsers.innerText = data.totalUsers;

                    const ctx = document.getElementById('trafficChart');
                    if (ctx) {
                        new Chart(ctx, {
                            type: 'line',
                            data: {
                                labels: data.chartLabels && data.chartLabels.length > 0 ? data.chartLabels : ['Day 1', 'Day 2', 'Day 3', 'Day 4', 'Day 5', 'Day 6', 'Day 7'],
                                datasets: [{
                                    label: 'Requests',
                                    data: data.chartData && data.chartData.length > 0 ? data.chartData : [12, 19, 30, 25, 42, 38, 50],
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
                }
            }
        } catch (err) {
            console.error("Lỗi lấy dữ liệu thống kê: ", err);
        }
    }

    fetchDashboardStats();

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

            localStorage.removeItem("jwt_token");
            document.cookie = "jwt_token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
            window.location.href = "/admin-login";
        });
    }

    // 2. TÍNH NĂNG CẤP QUYỀN (THÊM TÀI KHOẢN BẰNG SWEETALERT2)
    const addUserBtn = document.querySelector('.btn-cyber');
    if (addUserBtn) {
        addUserBtn.addEventListener('click', async () => {
            const { value: formValues } = await Swal.fire({
                title: 'CẤP QUYỀN TÀI KHOẢN MỚI',
                html:
                    '<input id="swal-input1" class="swal2-input" placeholder="Tên đăng nhập">' +
                    '<input id="swal-input2" class="swal2-input" type="password" placeholder="Mật khẩu (Tối thiểu 6 ký tự)">',
                focusConfirm: false,
                showCancelButton: true,
                confirmButtonText: 'Tạo Tài Khoản',
                cancelButtonText: 'Hủy',
                preConfirm: () => {
                    const user = document.getElementById('swal-input1').value;
                    const pass = document.getElementById('swal-input2').value;
                    if (!user || !pass) {
                        Swal.showValidationMessage('Vui lòng nhập đầy đủ tài khoản và mật khẩu!');
                    }
                    return { username: user, password: pass };
                }
            });

            if (formValues) {
                const originalHtml = addUserBtn.innerHTML;
                addUserBtn.innerHTML = '<i class="fa-solid fa-spinner fa-spin me-2"></i>ĐANG XỬ LÝ...';
                addUserBtn.disabled = true;

                try {
                    const response = await fetch('/api/auth/register', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ username: formValues.username.trim(), password: formValues.password.trim() })
                    });

                    const resJson = await response.json();
                    if (response.ok && resJson.success) {
                        window.showCyberToast("THÀNH CÔNG", "Tạo tài khoản thành công!", "success");
                        setTimeout(() => window.location.reload(), 1500);
                    } else {
                        window.showCyberToast("LỖI", resJson.message || "Tên đăng nhập đã tồn tại!", "error");
                    }
                } catch (error) {
                    window.showCyberToast("MẤT KẾT NỐI", "Không thể kết nối máy chủ", "error");
                } finally {
                    addUserBtn.innerHTML = originalHtml;
                    addUserBtn.disabled = false;
                }
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

// 5. TÍNH NĂNG XUẤT BÁO CÁO (EXPORT CSV)
const btnExport = document.getElementById('btnExportReport');
if (btnExport) {
    btnExport.addEventListener('click', () => {
        window.showCyberToast("HỆ THỐNG", "Đang trích xuất dữ liệu...", "success");

        // Giả lập luồng xuất file CSV ở Frontend (Sau này có thể thay bằng fetch API)
        setTimeout(() => {
            const tableRows = document.querySelectorAll('.table-custom tbody tr');
            let csvContent = "data:text/csv;charset=utf-8,User,Thành phố,Trạng thái\n";

            tableRows.forEach(row => {
                const cols = row.querySelectorAll('td');
                if(cols.length >= 3) {
                    const user = cols[0].innerText;
                    const city = cols[1].innerText;
                    const status = cols[2].innerText;
                    csvContent += `${user},${city},${status}\n`;
                }
            });

            const encodedUri = encodeURI(csvContent);
            const link = document.createElement("a");
            link.setAttribute("href", encodedUri);
            link.setAttribute("download", `WeatherAI_Report_${new Date().getTime()}.csv`);
            document.body.appendChild(link);
            link.click();
            link.remove();
        }, 1000);
    });
}

// 6. CÁC NÚT THAO TÁC TRÊN DANH SÁCH USER

// Nút Xem
window.viewUser = function(id) {
    Swal.fire({
        title: 'Thông tin User #' + id,
        text: 'Tính năng xem chi tiết hoạt động của User đang được phát triển (Phase 4).',
        icon: 'info',
        confirmButtonColor: '#8b5cf6'
    });
};

// Nút Sửa (Phân quyền)
window.editUser = function(id) {
    Swal.fire({
        title: 'Phân quyền User #' + id,
        text: 'Chức năng nâng cấp lên quyền Admin đang được phát triển.',
        icon: 'warning',
        confirmButtonColor: '#f59e0b'
    });
};

// Nút Xóa
window.deleteUser = async function(id, username) {
    const result = await Swal.fire({
        title: 'Xác nhận xóa?',
        html: `Bạn có chắc chắn muốn xóa tài khoản <b>${username}</b>? Hành động này sẽ khóa vĩnh viễn tài khoản.`,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#ef4444',
        cancelButtonColor: '#6c757d',
        confirmButtonText: '<i class="fa-solid fa-trash me-2"></i>Xóa ngay!',
        cancelButtonText: 'Hủy'
    });

    if (result.isConfirmed) {
        try {
            const token = localStorage.getItem("jwt_token");
            const response = await fetch(`/api/admin/users/${id}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (response.ok) {
                Swal.fire('Đã xóa!', `Tài khoản ${username} đã bị xóa khỏi hệ thống.`, 'success')
                    .then(() => window.location.reload());
            } else {
                Swal.fire('Lỗi thao tác!', 'Không thể xóa tài khoản này.', 'error');
            }
        } catch (error) {
            Swal.fire('Mất kết nối', 'Không thể kết nối tới server.', 'error');
        }
    }
};

// QUẢN LÝ BẢNG TIN CỘNG ĐỒNG

function deleteCommunityReport(id) {
    if (!confirm('Bạn có chắc chắn muốn xóa bài viết này không?')) return;

    fetch(`/api/admin/community/${id}`, {
        method: 'DELETE'
    })
        .then(res => res.json())
        .then(json => {
            if (json.success) {
                alert('Đã xóa thành công!');
                window.location.reload();
            } else {
                alert('Lỗi: ' + json.message);
            }
        })
        .catch(err => {
            console.error(err);
            alert('Lỗi kết nối máy chủ!');
        });
}