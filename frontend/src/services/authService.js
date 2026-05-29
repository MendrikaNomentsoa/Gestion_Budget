import api from './api';

/**
 * Service d'authentification
 * Gère les appels API pour login et register
 */
const authService = {

    /**
     * Connecte un utilisateur
     * Stocke le token et les infos user dans localStorage
     */
    login: async (email, password) => {
        const response = await api.post('/auth/login', { email, password });
        const { token, userId, nom, email: userEmail } = response.data;
        
        // Stocke le token pour les prochaines requêtes
        localStorage.setItem('token', token);
        localStorage.setItem('user', JSON.stringify({ userId, nom, email: userEmail }));
        
        return response.data;
    },

    /**
     * Inscrit un nouvel utilisateur
     */
    register: async (nom, email, password) => {
        const response = await api.post('/auth/register', { nom, email, password });
        const { token, userId, email: userEmail } = response.data;
        
        localStorage.setItem('token', token);
        localStorage.setItem('user', JSON.stringify({ userId, nom, email: userEmail }));
        
        return response.data;
    },

    /**
     * Déconnecte l'utilisateur
     * Supprime le token du localStorage
     */
    logout: () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
    },

    /**
     * Vérifie si l'utilisateur est connecté
     */
    isAuthenticated: () => {
        return localStorage.getItem('token') !== null;
    },

    /**
     * Retourne les infos de l'utilisateur connecté
     */
    getUser: () => {
        const user = localStorage.getItem('user');
        return user ? JSON.parse(user) : null;
    }
};

export default authService;