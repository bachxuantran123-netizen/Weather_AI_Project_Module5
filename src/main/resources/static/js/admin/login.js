// login.js
document.addEventListener("DOMContentLoaded", function() {
    const loginForm = document.getElementById('loginForm');

    if(loginForm) {
        loginForm.addEventListener('submit', async function(e) {
            e.preventDefault(); // Chặn load lại trang

            const btn = document.getElementById('loginBtn');
            const errorDiv = document.getElementById('error-message');
            const errorText = document.getElementById('error-text');

            btn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> ĐANG KẾT NỐI...';
            btn.disabled = true;
            errorDiv.style.display = 'none';

            const payload = {
                username: document.getElementById('username').value,
                password: document.getElementById('password').value
            };

            try {
                const response = await fetch('/api/auth/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });

                const resJson = await response.json();
                if (response.ok && resJson.success) {
                    const token = resJson.data.token;

                    // 1. Lưu JWT vào LocalStorage
                    localStorage.setItem('jwt_token', token);

                    // 2. Lưu JWT vào Cookie (max-age tính bằng giây = 24h)
                    document.cookie = `jwt_token=${token}; path=/; max-age=86400; SameSite=Strict`;

                    // 3. Chuyển hướng
                    btn.innerHTML = 'TRUY CẬP THÀNH CÔNG';
                    btn.style.backgroundColor = '#00ff66';

                    setTimeout(() => {
                        window.location.href = '/admin/dashboard';
                    }, 500);

                } else {
                    errorText.innerText = resJson.message || "Sai tài khoản hoặc mật khẩu!";
                    errorDiv.style.display = 'block';
                    btn.innerHTML = 'XÁC THỰC CẤP QUYỀN';
                    btn.disabled = false;
                }
            } catch (error) {
                errorText.innerText = "Lỗi kết nối máy chủ!";
                errorDiv.style.display = 'block';
                btn.innerHTML = 'XÁC THỰC CẤP QUYỀN';
                btn.disabled = false;
            }
        });
    }
});