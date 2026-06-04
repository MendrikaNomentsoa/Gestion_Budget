import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080/budget-app/api',
    headers: {
        'Content-Type': 'application/json'
    }
});

api.interceptors.request.use(config => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

api.interceptors.response.use(
    response => response,
    error => {
        /**
         * On ne redirige vers /login que si l'utilisateur est déjà connecté
         * et que son token a expiré (pas pendant le login lui-même)
         */
        if (error.response?.status === 401) {
            const token = localStorage.getItem('token');
            // Si token existe → token expiré → déconnexion
            // Si token n'existe pas → c'est un login raté → ne pas recharger
            if (token) {
                localStorage.removeItem('token');
                localStorage.removeItem('user');
                window.location.href = '/login';
            }
        }
        return Promise.reject(error);
    }
);

export default api;