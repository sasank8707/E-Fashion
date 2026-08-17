// E-Fashion Website - Frontend Interaction JS
const API_BASE = 'http://localhost:8080/api';

// Helper: Check if user is logged in
function getLoggedInUser() {
    const userJson = localStorage.getItem('efashion_user');
    return userJson ? JSON.parse(userJson) : null;
}

// Helper: Set login session
function setLoggedInUser(user) {
    localStorage.setItem('efashion_user', JSON.stringify(user));
}

// Helper: Logout
function logoutUser() {
    localStorage.removeItem('efashion_user');
    window.location.href = 'index.html';
}

// Update Navbar links based on login state
function updateNavbar() {
    const user = getLoggedInUser();
    const navAuthSection = document.getElementById('nav-auth-section');
    if (!navAuthSection) return;

    if (user) {
        navAuthSection.innerHTML = `
            <li class="nav-item">
                <span class="nav-link text-dark">Hello, <strong>${user.name}</strong></span>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="cart.html" id="nav-cart">
                    <i class="bi bi-cart"></i> Cart
                    <span class="badge bg-dark rounded-pill" id="cart-badge-count">0</span>
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="#" id="btn-logout">Logout</a>
            </li>
        `;
        document.getElementById('btn-logout').addEventListener('click', (e) => {
            e.preventDefault();
            logoutUser();
        });
        updateCartBadge(user.id);
    } else {
        navAuthSection.innerHTML = `
            <li class="nav-item">
                <a class="nav-link" href="login.html">Login</a>
            </li>
            <li class="nav-item">
                <a class="nav-link btn btn-sm btn-efashion text-white ms-2 px-3" href="register.html">Register</a>
            </li>
        `;
    }
}

// Update Cart Badge Count
async function updateCartBadge(userId) {
    try {
        const response = await fetch(`${API_BASE}/cart/${userId}`);
        if (response.ok) {
            const cartItems = await response.json();
            const totalCount = cartItems.reduce((sum, item) => sum + item.quantity, 0);
            const badge = document.getElementById('cart-badge-count');
            if (badge) {
                badge.innerText = totalCount;
            }
        }
    } catch (err) {
        console.error("Error updating cart badge:", err);
    }
}

// Add Item to Cart
async function addToCart(productId, quantity = 1) {
    const user = getLoggedInUser();
    if (!user) {
        alert("Please login to add products to your cart.");
        window.location.href = 'login.html';
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/cart/${user.id}/add`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ productId, quantity })
        });
        const data = await response.json();
        if (response.ok) {
            alert("Product added to cart!");
            updateCartBadge(user.id);
        } else {
            alert(data.error || "Failed to add product to cart.");
        }
    } catch (err) {
        console.error("Cart error:", err);
        alert("Error connecting to backend server.");
    }
}

// Global initialization
document.addEventListener('DOMContentLoaded', () => {
    updateNavbar();
});
