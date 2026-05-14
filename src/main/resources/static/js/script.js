let isAudioPlaying = false;
let bsModalInstance = null;

// Khởi tạo Modal Bootstrap khi tải xong trang
document.addEventListener('DOMContentLoaded', () => {
    bsModalInstance = new bootstrap.Modal(document.getElementById('statusModal'));
    updateClock();
    setInterval(updateClock, 1000);

    // Mặc định mở phần đăng nhập/cảnh báo cho bà con
    switchSection('auth');
});

// Hàm chuyển đổi các vùng giao diện (SPA workflow)
function switchSection(targetId) {
    const sections = ['auth', 'dashboard', 'map', 'drone', 'report'];

    // Ẩn toàn bộ
    sections.forEach(sec => {
        const el = document.getElementById(`section-${sec}`);
        if (el) el.classList.add('d-none');
    });

    // Hiện vùng đích
    const targetEl = document.getElementById(`section-${targetId}`);
    if (targetEl) {
        targetEl.classList.remove('d-none');
        // Cuộn mượt lên đầu
        window.scrollTo({ top: 0, behavior: 'smooth' });
    }

    // Đổi trạng thái menu active
    document.querySelectorAll('.navbar-nav .nav-link').forEach(link => link.classList.remove('active'));
    const activeLink = document.getElementById(`nav-${targetId}`);
    if (activeLink) activeLink.classList.add('active');

    // Đóng menu mobile nếu đang mở
    const navCollapse = document.getElementById('navbarContent');
    if (navCollapse.classList.contains('show')) {
        bootstrap.Collapse.getInstance(navCollapse).hide();
    }
}

// Bỏ qua đăng nhập đi thẳng vào Bảng thông tin
function bypassToDashboard() {
    updateUserGreeting("Công Dân Giám Sát", "Kênh Tra Cứu Tự Do");
    switchSection('dashboard');
    triggerAlertModal("Truy Cập Thành Công", "Bà con đang xem thông tin trực tiếp từ trạm khí tượng thủy văn của tỉnh. Vui lòng chú ý các mốc nước dâng cao.");
}

// Xử lý đăng nhập của người dân
function handleCitizenLogin(e) {
    e.preventDefault();
    updateUserGreeting("Bà con Nhân dân", "Kênh Hướng Dẫn Sơ Tán");
    triggerAlertModal("Xác Thực Thành Công!", "Đã kết nối thành công số điện thoại của bà con. Hệ thống đang tải lộ trình sơ tán an toàn nhất.");
    setTimeout(() => switchSection('dashboard'), 1200);
}

// Xử lý đăng nhập của Đội cứu hộ
function handleRescueLogin(e) {
    e.preventDefault();
    updateUserGreeting("Đội Phản Ứng Nhanh", "Kênh Chỉ Huy AI");
    triggerAlertModal("Kết Nối Kênh Bộ Đàm AI", "Đã nạp bản đồ nhiệt và hệ thống định vị mục tiêu. Kính chúc các đồng chí hoàn thành xuất sắc nhiệm vụ cứu hộ đồng bào.");
    setTimeout(() => switchSection('dashboard'), 1200);
}

// Cập nhật thẻ chào mừng trên Navbar
function updateUserGreeting(name, role) {
    const area = document.getElementById('user-status-area');
    area.innerHTML = `
                <div class="d-flex align-items-center gap-2">
                    <div class="rounded-circle bg-warning text-dark fw-bold d-flex align-items-center justify-content-center" style="width: 32px; height: 32px; font-size: 0.75rem;">
                        <i class="fa-solid fa-user-check"></i>
                    </div>
                    <div class="text-start d-none d-sm-block">
                        <span class="d-block text-white fw-medium" style="font-size: 0.75rem;">${name}</span>
                        <small class="text-cyan font-tech d-block" style="font-size: 0.65rem;">${role}</small>
                    </div>
                    <button class="btn btn-link text-secondary p-1" onclick="logout()" title="Thoát kênh"><i class="fa-solid fa-right-from-bracket"></i></button>
                </div>
            `;
}

function logout() {
    const area = document.getElementById('user-status-area');
    area.innerHTML = `
                <button class="btn-gradient py-1.5 px-3 fs-6 rounded-3" onclick="switchSection('auth')">
                    <i class="fa-solid fa-user-shield me-1"></i> Đăng nhập
                </button>
            `;
    switchSection('auth');
}

// Bật/tắt mật khẩu
function togglePassword(btn) {
    const input = btn.previousElementSibling;
    if (input.type === 'password') {
        input.type = 'text';
        btn.innerHTML = '<i class="fa-solid fa-eye-slash"></i>';
    } else {
        input.type = 'password';
        btn.innerHTML = '<i class="fa-solid fa-eye"></i>';
    }
}

// Kích hoạt Modal thông báo đẹp
function triggerAlertModal(title, msg) {
    document.getElementById('modal-heading').innerText = title;
    document.getElementById('modal-body-text').innerText = msg;
    if (bsModalInstance) bsModalInstance.show();
}

// Cập nhật đồng hồ hệ thống
function updateClock() {
    const timeEl = document.getElementById('live-time');
    if (!timeEl) return;
    const now = new Date();
    timeEl.innerText = now.toTimeString().split(' ')[0] + ' GMT+7';
}

// Chức năng lọc Bản đồ rủi ro
function changeMapLayer(layer) {
    const overlay = document.getElementById('layer-color-overlay');
    const bgImg = document.getElementById('satellite-img');

    // Cập nhật class active cho nút
    const buttons = event.target.parentElement.querySelectorAll('button');
    buttons.forEach(b => b.classList.remove('active'));
    event.target.classList.add('active');

    if (layer === 'flood') {
        overlay.className = "absolute inset-0 bg-gradient-to-tr from-cyan border-opacity-25 to-danger opacity-40 pointer-events-none transition-all duration-500";
        bgImg.style.filter = "hue-rotate(0deg) brightness(80%)";
    } else if (layer === 'landslide') {
        overlay.className = "absolute inset-0 bg-gradient-to-r from-danger via-warning to-danger opacity-50 pointer-events-none transition-all duration-500";
        bgImg.style.filter = "hue-rotate(60deg) contrast(130%)";
    } else {
        overlay.className = "absolute inset-0 bg-success opacity-30 pointer-events-none transition-all duration-500";
        bgImg.style.filter = "brightness(90%)";
    }
}

// Xem chi tiết Marker bản đồ
function showPinInfo(title, type, desc, colorClass) {
    document.getElementById('pin-title').innerText = title;
    document.getElementById('pin-desc').innerText = desc;

    const badge = document.getElementById('pin-type-badge');
    badge.innerText = type;
    badge.className = `badge bg-${colorClass} font-tech`;

    const panel = document.getElementById('pin-detail-panel');
    panel.style.display = 'block';
}

// Xử lý gửi báo cáo SOS
function autoFetchLocation() {
    const locInput = document.getElementById('sos-location');
    locInput.value = "Đang quét định vị GPS...";
    setTimeout(() => {
        locInput.value = "Tọa độ: 15.241°N, 108.042°E (Khu vực rốn lũ Trà Leng, Nam Trà My)";
    }, 600);
}

function showUploadedFileName(input) {
    const preview = document.getElementById('file-name-preview');
    if (input.files && input.files[0]) {
        preview.innerText = `[AI Đã Tải Ảnh] ${input.files[0].name}`;
        preview.classList.remove('d-none');
    }
}

function handleSosSubmit(e) {
    e.preventDefault();
    triggerAlertModal("ĐÃ PHÁT TÍN HIỆU CẤP CỨU!", "Ban chỉ đạo PCTT tỉnh đã nhận được tọa độ chính xác của bà con. Kính đề nghị bà con bám chặt vào vị trí kiên cố, lực lượng cứu hộ đang di chuyển tới.");
    document.getElementById('form-sos-report').reset();
    document.getElementById('file-name-preview').classList.add('d-none');
}

// Bật âm thanh còi hú giả lập bằng Web Audio API
function toggleAudioAlert() {
    const btn = document.getElementById('soundBtn');
    isAudioPlaying = !isAudioPlaying;

    if (isAudioPlaying) {
        btn.classList.remove('btn-outline-secondary');
        btn.classList.add('btn-danger');
        btn.innerHTML = '<i class="fa-solid fa-bell animate-bounce text-white"></i>';
        playHooter();
    } else {
        btn.classList.remove('btn-danger');
        btn.classList.add('btn-outline-secondary');
        btn.innerHTML = '<i class="fa-solid fa-bell-slash text-secondary"></i>';
    }
}

function playHooter() {
    if (!isAudioPlaying) return;
    try {
        const AudioCtx = window.AudioContext || window.webkitAudioContext;
        if (!AudioCtx) return;
        const ctx = new AudioCtx();
        const osc = ctx.createOscillator();
        const gain = ctx.createGain();

        osc.type = 'sawtooth';
        osc.frequency.setValueAtTime(600, ctx.currentTime);
        osc.frequency.linearRampToValueAtTime(900, ctx.currentTime + 0.4);
        osc.frequency.linearRampToValueAtTime(600, ctx.currentTime + 0.8);

        gain.gain.setValueAtTime(0.05, ctx.currentTime);
        gain.gain.exponentialRampToValueAtTime(0.001, ctx.currentTime + 0.8);

        osc.connect(gain);
        gain.connect(ctx.destination);
        osc.start();
        osc.stop(ctx.currentTime + 0.8);

        setTimeout(playHooter, 1000);
    } catch (err) {}
}