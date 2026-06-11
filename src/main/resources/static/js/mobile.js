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

    // Đăng nhập (Đã fix triệt để lỗi Undefined Token)
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
            let json = await res.json();

            if (res.ok && json.success) {
                showToast('Đăng nhập thành công!', 'success');

                // Gọi chính xác json.data.accessToken theo chuẩn của JwtResponse
                const validToken = json.data?.accessToken;

                if (validToken && validToken !== 'undefined') {
                    // Đồng bộ lưu dưới key jwt_token để dùng chung phiên với web Admin
                    localStorage.setItem('jwt_token', validToken);
                    authModal.classList.remove('active');
                } else {
                    showToast('Lỗi: Server không trả về Token hợp lệ!', 'error');
                }
            } else {
                showToast(json.message || 'Sai tên đăng nhập hoặc mật khẩu!', 'error');
            }
        } catch (err) { showToast('Lỗi kết nối máy chủ!', 'error'); }
    });

    // --- 3. THÊM ĐỊA ĐIỂM YÊU THÍCH ---
    document.getElementById('addLocationBtn').addEventListener('click', async () => {
        // Hỗ trợ đọc cả 2 loại key phòng hờ
        let token = localStorage.getItem('jwt_token') || localStorage.getItem('jwtToken');

        // Chặn tuyệt đối chuỗi rác "undefined"
        if (!token || token === 'undefined' || token === 'null') {
            showToast('Bạn cần đăng nhập để lưu địa điểm!', 'error');
            localStorage.removeItem('jwt_token');
            localStorage.removeItem('jwtToken');
            authModal.classList.add('active');
            return;
        }

        const cityName = document.getElementById('cityName').innerText;
        if(cityName === "Đang tải...") return;

        try {
            let res = await fetch('/api/v1/locations', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ cityName: cityName, latitude: 0.0, longitude: 0.0, alias: 'Yêu thích' })
            });

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

    // --- 4. TÌM KIẾM MODAL ---
    const searchModal = document.getElementById('searchModal');
    const searchInput = document.getElementById('searchInput');

    document.getElementById('searchBtn').addEventListener('click', () => {
        searchModal.classList.add('active');
        setTimeout(() => searchInput.focus(), 100);
    });

    document.getElementById('closeSearchModal').addEventListener('click', () => {
        searchModal.classList.remove('active');
    });

    searchInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') document.getElementById('btnSearchSubmit').click();
    });

    document.getElementById('btnSearchSubmit').addEventListener('click', () => {
        const city = searchInput.value;
        if (city && city.trim() !== "") {
            searchModal.classList.remove('active');
            searchInput.value = '';
            fetchRealWeather(city.trim());
        } else {
            showToast("Vui lòng nhập tên thành phố hợp lệ!", "error");
        }
    });

    // --- 5. DATA BINDING: GỌI API THỜI TIẾT THỰC TẾ ---
    async function fetchRealWeather(city) {
        let token = localStorage.getItem('jwt_token') || localStorage.getItem('jwtToken');

        // Chặn tuyệt đối chuỗi rác "undefined"
        if (!token || token === 'undefined' || token === 'null') {
            showToast('Vui lòng đăng nhập để xem thời tiết!', 'error');
            localStorage.removeItem('jwt_token');
            localStorage.removeItem('jwtToken');
            authModal.classList.add('active');
            return;
        }

        // Giao diện chờ loading
        document.getElementById('cityName').innerText = "Đang tải...";
        document.getElementById('temperature').innerText = "--";
        document.getElementById('condition').innerText = "Đang kết nối vệ tinh...";
        document.getElementById('tempHigh').innerText = "--";
        document.getElementById('tempLow').innerText = "--";
        document.getElementById('aiAdviceContent').innerHTML = "<i class='fa-solid fa-spinner fa-spin'></i> AI đang phân tích dữ liệu...";
        document.getElementById('aiTags').innerHTML = "";
        document.querySelector('.hourly-forecast').innerHTML = "<div style='text-align:center; width:100%; color:#b5b8d9;'><i class='fa-solid fa-spinner fa-spin'></i> Đang lấy dữ liệu 24h tới...</div>";

        try {
            const res = await fetch(`/api/v1/weather/current?city=${encodeURIComponent(city)}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (res.ok) {
                const data = await res.json();
                updateRealUI(data);
            } else if (res.status === 429) { // Quá giới hạn Bucket4j
                showToast("Bạn tra cứu quá nhanh (10 lần/phút). Vui lòng đợi!", "error");
                resetUI();
            } else if (res.status === 401 || res.status === 403) {
                showToast("Phiên đăng nhập hết hạn hoặc bị khóa!", "error");
                localStorage.removeItem('jwt_token');
                localStorage.removeItem('jwtToken');
                authModal.classList.add('active');
                resetUI();
            } else {
                showToast("Không tìm thấy dữ liệu cho khu vực này!", "error");
                resetUI();
            }
        } catch (err) {
            showToast("Mất kết nối đến máy chủ API!", "error");
            resetUI();
        }
    }

    // Reset lại UI khi lỗi
    function resetUI() {
        document.getElementById('cityName').innerText = "Vui lòng thử lại";
        document.getElementById('temperature').innerText = "--";
        document.getElementById('condition').innerText = "--";
        document.getElementById('aiAdviceContent').innerText = "Không có dữ liệu AI.";
        document.querySelector('.hourly-forecast').innerHTML = "";
    }

    // --- 6. RENDER DỮ LIỆU JSON LÊN DOM ---
    function updateRealUI(data) {
        document.getElementById('cityName').innerText = data.cityName;
        document.getElementById('temperature').innerText = Math.round(data.temperature);
        document.getElementById('condition').innerText = data.condition;
        document.getElementById('tempHigh').innerText = Math.round(data.tempHigh);
        document.getElementById('tempLow').innerText = Math.round(data.tempLow);

        document.getElementById('uvIndex').innerText = data.uvIndex;
        let uvDesc = "Thấp";
        if (data.uvIndex >= 3 && data.uvIndex <= 5) uvDesc = "Trung bình";
        else if (data.uvIndex >= 6 && data.uvIndex <= 7) uvDesc = "Cao";
        else if (data.uvIndex >= 8) uvDesc = "Rất cao";
        document.getElementById('uvDesc').innerText = uvDesc;

        document.getElementById('humidity').innerText = data.humidity;
        document.getElementById('windSpeed').innerText = data.windSpeed;
        document.getElementById('visibility').innerText = data.visibility;

        const aiData = data.aiAdvice;
        if (aiData) {
            document.getElementById('aiAdviceContent').innerText = `"${aiData.advice}"`;

            let tagsHtml = '';
            if (aiData.items_to_bring && aiData.items_to_bring.length > 0) {
                aiData.items_to_bring.forEach(item => {
                    tagsHtml += `<span class="ai-tag"><i class="fa-solid fa-check text-success"></i> ${item}</span>`;
                });
            }
            if (aiData.warnings && aiData.warnings.length > 0) {
                aiData.warnings.forEach(warning => {
                    tagsHtml += `<span class="ai-tag" style="background: rgba(239, 68, 68, 0.2); color: #fca5a5;">
                                    <i class="fa-solid fa-triangle-exclamation"></i> ${warning}
                                 </span>`;
                });
            }
            document.getElementById('aiTags').innerHTML = tagsHtml;
        }

        const hourlyContainer = document.querySelector('.hourly-forecast');
        let hourlyHtml = '';
        if (data.hourlyForecast && data.hourlyForecast.length > 0) {
            data.hourlyForecast.forEach((hourData, index) => {
                const isActive = index === 0 ? 'active' : '';
                const timeLabel = index === 0 ? 'Bây giờ' : hourData.time;

                hourlyHtml += `
                    <div class="hour-item ${isActive}">
                        <div class="time">${timeLabel}</div>
                        <img src="${hourData.iconUrl}" alt="icon" style="width: 40px; height: 40px; margin-bottom: 5px;">
                        <div class="temp">${Math.round(hourData.temp)}°</div>
                    </div>
                `;
            });
        }
        hourlyContainer.innerHTML = hourlyHtml;
    }
});