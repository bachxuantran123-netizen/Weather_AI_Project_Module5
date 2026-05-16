document.addEventListener('DOMContentLoaded', function() {
    const rainContainer = document.getElementById('rainContainer');
    if (!rainContainer) return;
    for (let i = 0; i < 150; i++) {
        const raindrop = document.createElement('div');
        raindrop.classList.add('raindrop');
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
    const passwordInput = document.getElementById('password');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const confirmPasswordError = document.getElementById('confirmPasswordError');
    const registerForm = document.getElementById('registerForm');
    function validatePasswordMatch() {
        if (confirmPasswordInput.value && confirmPasswordInput.value !== passwordInput.value) {
            confirmPasswordError.style.display = 'block';
            confirmPasswordInput.style.borderColor = '#dc3545';
            return false;
        } else {
            confirmPasswordError.style.display = 'none';
            confirmPasswordInput.style.borderColor = '#e2e8f0';
            return true;
        }
    }
    confirmPasswordInput.addEventListener('input', validatePasswordMatch);
    passwordInput.addEventListener('input', function() {
        if (confirmPasswordInput.value) {
            validatePasswordMatch();
        }
    });
    registerForm.addEventListener('submit', function(e) {
        if (!validatePasswordMatch()) {
            e.preventDefault();
            confirmPasswordInput.focus();
        }
    });
});