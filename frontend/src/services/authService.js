import api from './api';

const authService = {

    login: async (email, password) => {
        const response = await api.post('/auth/login', { email, password });
        const { token, userId, nom, email: userEmail } = response.data;
        localStorage.setItem('token', token);
        localStorage.setItem('user', JSON.stringify({ userId, nom, email: userEmail }));
        return response.data;
    },

    register: async (nom, email, password, montantInitial = 0) => {
        const response = await api.post('/auth/register', {
            nom,
            email,
            password,
            montantInitial
        });
        const { token, userId, email: userEmail } = response.data;
        localStorage.setItem('token', token);
        localStorage.setItem('user', JSON.stringify({ userId, nom, email: userEmail }));
        return response.data;
    },

    logout: () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
    },

    isAuthenticated: () => {
        return localStorage.getItem('token') !== null;
    },

    getUser: () => {
        const user = localStorage.getItem('user');
        return user ? JSON.parse(user) : null;
    }
};

export default authService;