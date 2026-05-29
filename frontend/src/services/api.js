import axios from 'axios';

/**
 * Instance Axios configurée pour appeler le backend WildFly
 * Toutes les requêtes passeront par cette instance
 */
const api = axios.create({
    baseURL: 'http://localhost:8080/budget-app/api',
    headers: {
        'Content-Type': 'application/json'
    }
});

/**
 * Intercepteur de requête
 * Avant chaque requête, ajoute automatiquement le token JWT
 * dans le header Authorization
 * 
 * Sans ça, il faudrait ajouter le token manuellement partout
 */
api.interceptors.request.use(config => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

/**
 * Intercepteur de réponse
 * Si le serveur retourne 401 (token expiré ou invalide)
 * on déconnecte l'utilisateur automatiquement
 */
api.interceptors.response.use(
    response => response,
    error => {
        if (error.response?.status === 401) {
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

export default api;