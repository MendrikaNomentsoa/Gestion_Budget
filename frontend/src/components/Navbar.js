import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Navbar() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <nav style={styles.nav}>
            <div style={styles.logo}>💰 Budget App</div>
            <div style={styles.liens}>
                <Link style={styles.lien} to="/dashboard">Dashboard</Link>
                <Link style={styles.lien} to="/transactions">Transactions</Link>
                <Link style={styles.lien} to="/categories">Catégories</Link>
            </div>
            <div style={styles.user}>
                <span style={styles.nom}>👤 {user?.nom}</span>
                <button style={styles.btnLogout} onClick={handleLogout}>
                    Déconnexion
                </button>
            </div>
        </nav>
    );
}

const styles = {
    nav: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        padding: '0 24px',
        height: '60px',
        backgroundColor: '#2C3E50',
        color: 'white'
    },
    logo: { fontSize: '20px', fontWeight: 'bold' },
    liens: { display: 'flex', gap: '24px' },
    lien: { color: 'white', textDecoration: 'none', fontSize: '14px' },
    user: { display: 'flex', alignItems: 'center', gap: '16px' },
    nom: { fontSize: '14px' },
    btnLogout: {
        padding: '8px 16px',
        backgroundColor: '#E74C3C',
        color: 'white',
        border: 'none',
        borderRadius: '6px',
        cursor: 'pointer',
        fontSize: '14px'
    }
};