importScripts('https://www.gstatic.com/firebasejs/9.22.1/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/9.22.1/firebase-messaging-compat.js');

const firebaseConfig = {
    apiKey: "AIzaSyD94NwWN-0JrLn-5h3ME2gDR69GQyk5Ze0",
    authDomain: "weatherai-956d2.firebaseapp.com",
    projectId: "weatherai-956d2",
    storageBucket: "weatherai-956d2.firebasestorage.app",
    messagingSenderId: "891706686099",
    appId: "1:891706686099:web:fe33af5fa85ae94a676b66",
    measurementId: "G-RKRY4L2TD5"
};

try {
    firebase.initializeApp(firebaseConfig);
    const messaging = firebase.messaging();

    // Xử lý thông báo đẩy khi app đang chạy nền (Background)
    messaging.onBackgroundMessage((payload) => {
        console.log('[firebase-messaging-sw.js] Nhận tin nhắn background: ', payload);
        
        const notificationTitle = payload.notification?.title || 'Cảnh báo Thiên tai';
        const notificationOptions = {
            body: payload.notification?.body || 'Vui lòng kiểm tra ứng dụng.',
            icon: 'https://cdn-icons-png.flaticon.com/512/1157/1157000.png',
            vibrate: [200, 100, 200, 100, 200, 100, 200]
        };

        self.registration.showNotification(notificationTitle, notificationOptions);
    });
} catch (error) {
    console.error("Lỗi khởi tạo Firebase Service Worker:", error);
}
