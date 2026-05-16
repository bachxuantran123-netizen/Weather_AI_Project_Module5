document.addEventListener('DOMContentLoaded', function() {
    const rainContainer = document.getElementById('rainContainer');
    if (!rainContainer) return;

    // Tạo 150 giọt mưa
    for (let i = 0; i < 150; i++) {
        const raindrop = document.createElement('div');
        raindrop.classList.add('raindrop');

        // Vị trí ngẫu nhiên
        const left = Math.random() * 100;
        const animationDuration = 0.5 + Math.random() * 1.2;
        const animationDelay = Math.random() * 2;
        const height = 10 + Math.random() * 25;

        raindrop.style.left = left + '%';
        raindrop.style.height = height + 'px';
        raindrop.style.animationDuration = animationDuration + 's';
        raindrop.style.animationDelay = animationDelay + 's';
        raindrop.style.opacity = 0.2 + Math.random() * 0.5;

        rainContainer.appendChild(raindrop);
    }
});
