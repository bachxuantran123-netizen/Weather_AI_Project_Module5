document.addEventListener("DOMContentLoaded", () => {
    if ("Notification" in window) {
        if (Notification.permission !== "granted" && Notification.permission !== "denied") {
            Notification.requestPermission().then(permission => {
                if (permission === "granted") {
                    console.log("Đã cấp quyền gửi thông báo đẩy!");
                }
            });
        }
    }

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

    const userProfileBtn = document.getElementById('userProfileBtn');
    function initApp() {
        let token = localStorage.getItem('jwt_token') || localStorage.getItem('jwtToken');
        if (token && token !== 'undefined' && token !== 'null') {
            if (userProfileBtn) {
                userProfileBtn.style.color = '#ffffff';
            }
        }
    }
    initApp();

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
            let json = await res.json();

            if (res.ok && json.success) {
                showToast('Đăng nhập thành công!', 'success');
                const validToken = json.data?.accessToken;

                if (validToken && validToken !== 'undefined') {
                    localStorage.setItem('jwt_token', validToken);
                    authModal.classList.remove('active');
                    initApp(); // Cập nhật lại màu Icon User
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
        let token = localStorage.getItem('jwt_token') || localStorage.getItem('jwtToken');
        if (!token || token === 'undefined' || token === 'null') {
            showToast('Bạn cần đăng nhập để lưu địa điểm!', 'error');
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
        } catch (err) { showToast('Mất kết nối đến máy chủ!', 'error'); }
    });

    // --- 4. TÌM KIẾM MODAL ---
    const searchModal = document.getElementById('searchModal');
    const searchInput = document.getElementById('searchInput');

    document.getElementById('searchBtn').addEventListener('click', () => {
        searchModal.classList.add('active');
        setTimeout(() => searchInput.focus(), 100);
    });

    document.getElementById('closeSearchModal').addEventListener('click', () => searchModal.classList.remove('active'));

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
        if (city.trim().toLowerCase() === 'demacia' || city.trim().toLowerCase() === 'testbao') {
            const mockData = {
                cityName: "Gotham City", temperature: 36, condition: "Bão Siêu Cấp", tempHigh: 30, tempLow: 25, uvIndex: 1, humidity: 99, windSpeed: 200, visibility: 0,
                aiAdvice: {
                    advice: "TÌM NƠI TRÚ ẨN NGAY LẬP TỨC!",
                    items_to_bring: ["Đèn pin", "Lương khô", "Áo phao"],
                    warnings: ["BÃO CẤP 15 ĐANG TIẾN VÀO THÀNH PHỐ!", "NGUY CƠ SẠT LỞ ĐẤT VÀ LŨ QUÉT ĐẶC BIỆT NGHIÊM TRỌNG!"]
                },
                hourlyForecast: []
            };
            if (searchModal) searchModal.classList.remove('active');
            updateRealUI(mockData);
            return;
        }

        let token = localStorage.getItem('jwt_token') || localStorage.getItem('jwtToken');
        if (!token || token === 'undefined' || token === 'null') {
            showToast('Vui lòng đăng nhập để xem thời tiết!', 'error');
            authModal.classList.add('active');
            return;
        }

        // Giao diện chờ loading
        document.getElementById('cityName').innerText = "Đang tải...";
        document.getElementById('temperature').innerText = "--";
        document.getElementById('condition').innerText = "Đang kết nối...";
        document.getElementById('tempHigh').innerText = "--";
        document.getElementById('tempLow').innerText = "--";
        document.getElementById('aiAdviceContent').innerHTML = "<i class='fa-solid fa-spinner fa-spin'></i> AI đang phân tích dữ liệu...";
        document.getElementById('aiTags').innerHTML = "";
        document.querySelector('.hourly-forecast').innerHTML = "<div style='text-align:center; width:100%; color:#b5b8d9;'><i class='fa-solid fa-spinner fa-spin'></i> Đang lấy dữ liệu...</div>";

        try {
            const res = await fetch(`/api/v1/weather/current?city=${encodeURIComponent(city)}`, {
                method: 'GET',
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (res.ok) {
                const data = await res.json();
                updateRealUI(data);
            } else if (res.status === 429) {
                showToast("Bạn tra cứu quá nhanh. Vui lòng đợi!", "error");
                resetUI();
            } else if (res.status === 401 || res.status === 403) {
                showToast("Phiên đăng nhập hết hạn!", "error");
                localStorage.removeItem('jwt_token');
                authModal.classList.add('active');
                resetUI();
            } else {
                showToast("Không tìm thấy dữ liệu khu vực này!", "error");
                resetUI();
            }
        } catch (err) {
            showToast("Mất kết nối đến máy chủ API!", "error");
            resetUI();
        }
    }

    function resetUI() {
        document.getElementById('cityName').innerText = "Lỗi kết nối";
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
            if (aiData.warnings && aiData.warnings.length > 0) {
                triggerDisasterAlert(aiData.warnings);
            }
            document.getElementById('aiAdviceContent').innerText = `"${aiData.advice}"`;
            let tagsHtml = '';
            if (aiData.items_to_bring && aiData.items_to_bring.length > 0) {
                aiData.items_to_bring.forEach(item => {
                    tagsHtml += `<span class="ai-tag"><i class="fa-solid fa-check text-success"></i> ${item}</span>`;
                });
            }
            if (aiData.warnings && aiData.warnings.length > 0) {
                aiData.warnings.forEach(warning => {
                    tagsHtml += `<span class="ai-tag" style="background: rgba(239, 68, 68, 0.2); color: #fca5a5;"><i class="fa-solid fa-triangle-exclamation"></i> ${warning}</span>`;
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
                    </div>`;
            });
        }
        hourlyContainer.innerHTML = hourlyHtml;
    }

    // --- 7. AUTO FETCH LOCATION ON LOAD ---
    if ("geolocation" in navigator) {
        navigator.geolocation.getCurrentPosition(
            (position) => { fetchRealWeather(`${position.coords.latitude},${position.coords.longitude}`); },
            (error) => { fetchRealWeather("Hanoi"); },
            { enableHighAccuracy: true, timeout: 5000 }
        );
    } else {
        fetchRealWeather("Hanoi");
    }

    // --- 8. XỬ LÝ USER PROFILE (AVATAR & ĐỔI MẬT KHẨU) ---
    const profileModal = document.getElementById('profileModal');
    if (userProfileBtn) {
        userProfileBtn.addEventListener('click', () => {
            let token = localStorage.getItem('jwt_token') || localStorage.getItem('jwtToken');
            if (!token || token === 'undefined' || token === 'null') {
                authModal.classList.add('active');
            } else {
                profileModal.classList.add('active');
                loadUserProfile(token);
            }
        });
    }

    const closeProfileBtn = document.getElementById('closeProfileModal');
    if(closeProfileBtn) closeProfileBtn.addEventListener('click', () => profileModal.classList.remove('active'));

    // Gọi API lấy thông tin Profile
    async function loadUserProfile(token) {
        try {
            let res = await fetch('/api/v1/users/me', { headers: { 'Authorization': `Bearer ${token}` } });
            if (res.ok) {
                let json = await res.json();
                let user = json.data || json;

                // Nếu dùng Google đăng nhập, tên có thể là email. Cắt phần @gmail.com đi cho gọn.
                let displayName = user.username;
                if(displayName.includes('@')) displayName = displayName.split('@')[0];

                document.getElementById('profileUsername').innerText = `@${displayName}`;
                document.getElementById('profileAvatar').src = `https://ui-avatars.com/api/?name=${displayName}&background=8b5cf6&color=fff&size=200`;
            } else {
                localStorage.removeItem('jwt_token');
                profileModal.classList.remove('active');
                authModal.classList.add('active');
            }
        } catch (err) { showToast('Lỗi tải dữ liệu hồ sơ!', 'error'); }
    }

    // Đổi mật khẩu
    document.getElementById('btnChangePassword').addEventListener('click', async () => {
        const oldPass = document.getElementById('oldPassword').value;
        const newPass = document.getElementById('newPassword').value;
        if(!oldPass || !newPass) return showToast('Vui lòng nhập đủ thông tin!', 'error');

        let token = localStorage.getItem('jwt_token');
        try {
            let res = await fetch('/api/v1/users/change-password', {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
                body: JSON.stringify({ oldPassword: oldPass, newPassword: newPass })
            });
            let json = await res.json();
            if (res.ok && json.success) {
                showToast('Đổi mật khẩu thành công!', 'success');
                document.getElementById('oldPassword').value = '';
                document.getElementById('newPassword').value = '';
            } else {
                showToast(json.message || 'Lỗi đổi mật khẩu!', 'error');
            }
        } catch(e) { showToast('Lỗi kết nối mạng!', 'error'); }
    });

    // Cập nhật Avatar (Giữ nguyên logic của bạn)
    document.getElementById('avatarUpload').addEventListener('change', async (e) => {
        const file = e.target.files[0];
        if (!file) return;
        const reader = new FileReader();
        reader.onload = (event) => document.getElementById('profileAvatar').src = event.target.result;
        reader.readAsDataURL(file);

        let token = localStorage.getItem('jwt_token');
        const formData = new FormData();
        formData.append('avatar', file);

        try {
            showToast('Đang tải ảnh lên...', 'success');
            let res = await fetch('/api/v1/users/avatar', {
                method: 'POST',
                headers: { 'Authorization': `Bearer ${token}` },
                body: formData
            });
            let json = await res.json();
            if (res.ok && json.success) showToast('Lưu ảnh đại diện thành công!', 'success');
            else showToast(json.message || 'Lỗi upload ảnh!', 'error');
        } catch(error) { showToast('Lỗi mạng khi upload!', 'error'); }
    });

    // Đăng xuất
    document.getElementById('btnLogoutMobile').addEventListener('click', () => {
        localStorage.removeItem('jwt_token');
        localStorage.removeItem('jwtToken');
        profileModal.classList.remove('active');
        userProfileBtn.style.color = '#b5b8d9'; // Trả lại màu cũ
        showToast('Đã đăng xuất!', 'success');
        setTimeout(() => window.location.reload(), 1000);
    });

    // --- HÀM KÍCH HOẠT CẢNH BÁO THIÊN TAI KHẨN CẤP ---
    function triggerDisasterAlert(warnings) {
        if (!warnings || warnings.length === 0) return;
        if ("vibrate" in navigator) navigator.vibrate([1000, 500, 1000, 500, 2000]);
        try {
            let siren = new Audio('https://actions.google.com/sounds/v1/alarms/spaceship_alarm.ogg');
            siren.volume = 1.0;
            siren.play().catch(e => console.warn("Trình duyệt chặn âm thanh."));
        } catch (e) {}
        if ("Notification" in window && Notification.permission === "granted") {
            new Notification("🚨 CẢNH BÁO THIÊN TAI KHẨN CẤP!", { body: warnings[0], icon: 'https://cdn-icons-png.flaticon.com/512/1157/1157000.png', vibrate: [200, 100, 200] });
        }
        if (typeof Swal !== 'undefined') {
            Swal.fire({
                title: '🚨 NGUY HIỂM!',
                html: warnings.map(w => `<b style="font-size: 1.1rem;">${w}</b>`).join('<br><br>'),
                icon: 'error',
                background: '#2b0202',
                color: '#ff4d4d',
                confirmButtonText: 'TÔI ĐÃ RÕ VÀ SẼ TÌM NƠI TRÚ ẨN',
                confirmButtonColor: '#d33',
                allowOutsideClick: false,
                allowEscapeKey: false
            });
        }
    }
});