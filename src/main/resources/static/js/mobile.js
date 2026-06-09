document.addEventListener("DOMContentLoaded", () => {

    // --- CÔNG CỤ HIỂN THỊ TOAST ---
    function showToast(message, type = 'success') {
        let container = document.getElementById('mobile-toast-container');
        if (!container) {
            container = document.createElement('div');
            container.id = 'mobile-toast-container';
            document.body.appendChild(container);
        }
        const toast = document.createElement('div');
        toast.className = `mobile-toast ${type}`;
        const icon = type === 'success' ? '<i class="fa-solid fa-circle-check text-success fs-4"></i>' : '<i class="fa-solid fa-triangle-exclamation text-danger fs-4"></i>';
        toast.innerHTML = `${icon} <span style="flex: 1">${message}</span>`;

        container.appendChild(toast);
        setTimeout(() => {
            toast.style.opacity = '0';
            toast.style.transform = 'translateY(-20px)';
            toast.style.transition = 'all 0.3s ease';
            setTimeout(() => toast.remove(), 300);
        }, 3000);
    }

    // --- 1. XỬ LÝ BOTTOM NAV ---
    const navItems = document.querySelectorAll('.nav-item');
    navItems.forEach(item => {
        item.addEventListener('click', () => {
            navItems.forEach(nav => nav.classList.remove('active'));
            item.classList.add('active');
        });
    });

    // --- 2. MODAL XÁC THỰC (AUTH) ---
    const authModal = document.getElementById('authModal');
    const loginForm = document.getElementById('loginFormContainer');
    const registerForm = document.getElementById('registerFormContainer');

    const userProfileBtn = document.getElementById('userProfileBtn');
    if(userProfileBtn) userProfileBtn.addEventListener('click', () => authModal.classList.add('active'));

    const closeAuthModalBtn = document.getElementById('closeAuthModal');
    if(closeAuthModalBtn) closeAuthModalBtn.addEventListener('click', () => authModal.classList.remove('active'));

    document.getElementById('switchToRegister').addEventListener('click', () => { loginForm.style.display = 'none'; registerForm.style.display = 'block'; });
    document.getElementById('switchToLogin').addEventListener('click', () => { registerForm.style.display = 'none'; loginForm.style.display = 'block'; });

    // Đăng ký
    document.getElementById('btnRegisterSubmit').addEventListener('click', async () => {
        const u = document.getElementById('registerUsername').value;
        const p = document.getElementById('registerPassword').value;
        if(!u || !p) return showToast('Vui lòng điền đủ thông tin!', 'error');
        try {
            let res = await fetch('/api/auth/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username: u, password: p })
            });
            let text = await res.text();
            if (res.ok) {
                showToast('Đăng ký thành công! Hãy đăng nhập.', 'success');
                document.getElementById('switchToLogin').click();
            } else {
                showToast(text || 'Lỗi đăng ký!', 'error');
            }
        } catch (err) { showToast('Lỗi kết nối máy chủ!', 'error'); }
    });

    // Đăng nhập
    document.getElementById('btnLoginSubmit').addEventListener('click', async () => {
        const u = document.getElementById('loginUsername').value;
        const p = document.getElementById('loginPassword').value;
        if(!u || !p) return showToast('Vui lòng điền đủ thông tin!', 'error');
        try {
            let res = await fetch('/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username: u, password: p })
            });
            if (res.ok) {
                let json = await res.json();
                showToast('Đăng nhập thành công!', 'success');
                localStorage.setItem('jwtToken', json.token || json.data?.token);
                authModal.classList.remove('active');
            } else {
                showToast('Sai tên đăng nhập hoặc mật khẩu!', 'error');
            }
        } catch (err) { showToast('Lỗi kết nối máy chủ!', 'error'); }
    });

    // --- 3. FIX LỖI THÊM ĐỊA ĐIỂM YÊU THÍCH ---
    document.getElementById('addLocationBtn').addEventListener('click', async () => {
        const token = localStorage.getItem('jwtToken');
        if (!token || token === 'undefined') {
            showToast('Bạn cần đăng nhập để lưu địa điểm!', 'error');
            authModal.classList.add('active');
            return;
        }

        const cityName = document.getElementById('cityName').innerText;
        if(cityName === "Đang tìm...") return;

        try {
            let res = await fetch('/api/v1/locations', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                // Đảm bảo số thực (0.0) tránh lỗi parse Double của Backend
                body: JSON.stringify({ cityName: cityName, latitude: 0.0, longitude: 0.0, alias: 'Yêu thích' })
            });

            // Parse response thông minh (nhận cả Object DTO lẫn String thuần)
            let text = await res.text();
            let jsonResponse;
            try { jsonResponse = JSON.parse(text); } catch(e) { jsonResponse = text; }

            if (res.ok) {
                let msg = typeof jsonResponse === 'object' ? (jsonResponse.message || jsonResponse.data || "Đã lưu thành công!") : jsonResponse;
                showToast(msg, 'success');
            } else {
                let err = typeof jsonResponse === 'object' ? (jsonResponse.message || "Truy cập bị từ chối!") : (jsonResponse || "Lỗi khi lưu!");
                showToast(err, 'error');
            }
        } catch (err) {
            showToast('Mất kết nối đến máy chủ!', 'error');
        }
    });

    // --- 4. TÌM KIẾM BẰNG MODAL SIÊU ĐẸP (THAY THẾ PROMPT CŨ) ---
    const searchModal = document.getElementById('searchModal');
    const searchInput = document.getElementById('searchInput');

    // Mở Modal Tìm Kiếm
    document.getElementById('searchBtn').addEventListener('click', () => {
        searchModal.classList.add('active');
        setTimeout(() => searchInput.focus(), 100); // Tự động focus vào ô nhập liệu
    });

    // Đóng Modal Tìm Kiếm
    document.getElementById('closeSearchModal').addEventListener('click', () => {
        searchModal.classList.remove('active');
    });

    // Bắt sự kiện phím Enter trong ô input
    searchInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') document.getElementById('btnSearchSubmit').click();
    });

    // Thực hiện Tìm Kiếm
    document.getElementById('btnSearchSubmit').addEventListener('click', () => {
        const city = searchInput.value;
        if (city && city.trim() !== "") {
            searchModal.classList.remove('active');
            searchInput.value = ''; // Xóa trắng input cho lần sau
            fetchMockWeather(city.trim());
        } else {
            showToast("Vui lòng nhập tên thành phố hợp lệ!", "error");
        }
    });

    // --- HÀM GIẢ LẬP DỮ LIỆU THỜI TIẾT (GIỮ NGUYÊN) ---
    function fetchMockWeather(city) {
        document.getElementById('cityName').innerText = "Đang tìm...";
        document.getElementById('temperature').innerText = "--";
        document.getElementById('condition').innerText = "Đang kết nối vệ tinh...";
        document.getElementById('aiAdviceContent').innerHTML = "<i class='fa-solid fa-spinner fa-spin'></i> AI đang phân tích...";
        document.getElementById('aiTags').innerHTML = "";

        setTimeout(() => {
            const mockResponse = {
                cityName: city.charAt(0).toUpperCase() + city.slice(1),
                temperature: (Math.random() * 15 + 15).toFixed(1),
                condition: "Nắng Đẹp",
                aiAdvice: {
                    advice: `Trời quang mây tạnh tại ${city}, rất thích hợp để ra ngoài đi dạo. Đừng quên mang kính râm!`,
                    items_to_bring: ["Kính râm", "Mũ lưỡi trai", "Nước suối"],
                    warnings: ["Chỉ số UV có thể tăng cao vào buổi trưa."]
                }
            };

            if (mockResponse.temperature < 20) {
                mockResponse.condition = "Mưa Lạnh";
                mockResponse.aiAdvice = {
                    advice: `Nhiệt độ khá thấp tại ${city}, có mưa phùn. Hãy mặc áo ấm và mang ô.`,
                    items_to_bring: ["Áo khoác len", "Ô che mưa"],
                    warnings: ["Đường trơn trượt", "Dễ cảm lạnh"]
                };
            }

            updateUI(mockResponse);
        }, 1500);
    }

    // Cập nhật DOM
    function updateUI(data) {
        document.getElementById('cityName').innerText = data.cityName;
        document.getElementById('temperature').innerText = Math.round(data.temperature);
        document.getElementById('condition').innerText = data.condition;

        document.getElementById('tempHigh').innerText = Math.round(data.temperature) + 4;
        document.getElementById('tempLow').innerText = Math.round(data.temperature) - 3;
        document.getElementById('uvIndex').innerText = data.temperature > 25 ? 8 : 3;
        document.getElementById('uvDesc').innerText = data.temperature > 25 ? "Rất cao" : "Thấp";

        const aiData = data.aiAdvice;
        document.getElementById('aiAdviceContent').innerText = `"${aiData.advice}"`;

        let tagsHtml = '';
        aiData.items_to_bring.forEach(item => {
            tagsHtml += `<span class="ai-tag"><i class="fa-solid fa-check text-success"></i> ${item}</span>`;
        });

        aiData.warnings.forEach(warning => {
            tagsHtml += `<span class="ai-tag" style="background: rgba(239, 68, 68, 0.2); color: #fca5a5;">
                            <i class="fa-solid fa-triangle-exclamation"></i> ${warning}
                         </span>`;
        });

        document.getElementById('aiTags').innerHTML = tagsHtml;
    }
});