import React, { createContext, useState, useContext } from 'react';
import authService from '../services/authService';

/**
 * Context d'authentification
 * Permet à tous les composants d'accéder à l'état de connexion
 * sans passer les props manuellement à chaque niveau
 */
const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(authService.getUser());

    const login = async (email, password) => {
        const data = await authService.login(email, password);
        setUser(authService.getUser());
        return data;
    };

    const register = async (nom, email, password) => {
        const data = await authService.register(nom, email, password);
        setUser(authService.getUser());
        return data;
    };

    const logout = () => {
        authService.logout();
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{
            user,
            login,
            register,
            logout,
            isAuthenticated: !!user
        }}>
            {children}
        </AuthContext.Provider>
    );
};

/**
 * Hook personnalisé pour utiliser le contexte
 * ex: const { user, login, logout } = useAuth();
 */
export const useAuth = () => useContext(AuthContext);