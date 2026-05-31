import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import DashboardPage from './pages/DashboardPage';
import TransactionsPage from './pages/TransactionsPage';
import CategoriesPage from './pages/CategoriesPage';
import Navbar from './components/Navbar';
import BudgetsPage from './pages/BudgetsPage';
import ObjectifsPage from './pages/ObjectifsPage';

function PrivateRoute({ children }) {
    const { isAuthenticated } = useAuth();
    return isAuthenticated ? (
        <>
            <Navbar />
            {children}
        </>
    ) : <Navigate to="/login" />;
}

function App() {
    return (
        <AuthProvider>
            <BrowserRouter>
                {/* ToastContainer global — affiche les notifications partout */}
                <ToastContainer
                    position="top-right"
                    autoClose={3000}
                    hideProgressBar={false}
                    closeOnClick
                    pauseOnHover
                />
                <Routes>
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/register" element={<RegisterPage />} />
                    <Route path="/dashboard" element={
                        <PrivateRoute><DashboardPage /></PrivateRoute>
                    } />
                    <Route path="/budgets" element={
                        <PrivateRoute><BudgetsPage /></PrivateRoute>
                    } />
                    <Route path="/transactions" element={
                        <PrivateRoute><TransactionsPage /></PrivateRoute>
                    } />
                    <Route path="/categories" element={
                        <PrivateRoute><CategoriesPage /></PrivateRoute>
                    } />
                    <Route path="/" element={<Navigate to="/dashboard" />} />
                    <Route path="/objectifs" element={
                        <PrivateRoute><ObjectifsPage /></PrivateRoute>
                    } />
                </Routes>
            </BrowserRouter>
        </AuthProvider>
    );
}

export default App;