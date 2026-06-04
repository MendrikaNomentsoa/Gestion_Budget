import React, { createContext, useState, useContext } from 'react';
import authService from '../services/authService';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(authService.getUser());

    const login = async (email, password) => {
        try {
            const data = await authService.login(email, password);
            setUser(authService.getUser());
            return data;
        } catch (err) {
            // Propage l'erreur vers LoginPage
            throw err;
        }
    };

    const register = async (nom, email, password, montantInitial = 0) => {
        try {
            const data = await authService.register(nom, email, password, montantInitial);
            setUser(authService.getUser());
            return data;
        } catch (err) {
            throw err;
        }
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

export const useAuth = () => useContext(AuthContext);