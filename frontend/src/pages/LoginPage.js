import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { toast } from 'react-toastify';
import { useAuth } from '../context/AuthContext';

export default function LoginPage() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [loading, setLoading] = useState(false);
    const [erreurs, setErreurs] = useState({});

    const { login } = useAuth();
    const navigate = useNavigate();

    const valider = () => {
        const nouvErreurs = {};

        if (!email.trim()) {
            nouvErreurs.email = "L'email est obligatoire";
        } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(email)) {
            nouvErreurs.email = "Format d'email invalide (ex: nom@gmail.com)";
        }

        if (!password) {
            nouvErreurs.password = 'Le mot de passe est obligatoire';
        } else if (password.length < 6) {
            nouvErreurs.password = 'Le mot de passe doit contenir au moins 6 caractères';
        }

        setErreurs(nouvErreurs);
        return Object.keys(nouvErreurs).length === 0;
    };

    const handleSubmit = async () => {
        if (!valider()) return;

        setLoading(true);
        try {
            await login(email, password);
            toast.success('Connexion réussie ! Bienvenue 👋');
            navigate('/dashboard');
        } catch (err) {
            setErreurs({
                global: err.response?.data?.erreur || 'Email ou mot de passe incorrect'
            });
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={styles.container}>
            <div style={styles.card}>
                <h2 style={styles.title}>Connexion</h2>

                {erreurs.global && (
                    <div style={styles.erreurGlobal}>
                        ⚠️ {erreurs.global}
                    </div>
                )}

                <div>
                    <div style={styles.champ}>
                        <input
                            style={{
                                ...styles.input,
                                borderColor: erreurs.email ? '#E74C3C' : '#ddd'
                            }}
                            type="text"
                            placeholder="Email"
                            value={email}
                            onChange={e => {
                                setEmail(e.target.value);
                                setErreurs({ ...erreurs, email: '', global: '' });
                            }}
                        />
                        {erreurs.email && <p style={styles.erreurChamp}>{erreurs.email}</p>}
                    </div>

                    <div style={styles.champ}>
                        <input
                            style={{
                                ...styles.input,
                                borderColor: erreurs.password ? '#E74C3C' : '#ddd'
                            }}
                            type="password"
                            placeholder="Mot de passe"
                            value={password}
                            onChange={e => {
                                setPassword(e.target.value);
                                setErreurs({ ...erreurs, password: '', global: '' });
                            }}
                        />
                        {erreurs.password && <p style={styles.erreurChamp}>{erreurs.password}</p>}
                    </div>

                    <button
                        style={styles.button}
                        type="button"
                        onClick={handleSubmit}
                        disabled={loading}
                    >
                        {loading ? 'Connexion...' : 'Se connecter'}
                    </button>
                </div>

                <p style={styles.link}>
                    Pas de compte ? <Link to="/register">S'inscrire</Link>
                </p>
            </div>
        </div>
    );
}

const styles = {
    container: {
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        height: '100vh',
        backgroundColor: '#f5f5f5'
    },
    card: {
        backgroundColor: 'white',
        padding: '40px',
        borderRadius: '8px',
        boxShadow: '0 2px 10px rgba(0,0,0,0.1)',
        width: '100%',
        maxWidth: '400px'
    },
    title: { textAlign: 'center', marginBottom: '24px', color: '#333' },
    erreurGlobal: {
        backgroundColor: '#FDEDEC',
        border: '1px solid #E74C3C',
        color: '#E74C3C',
        padding: '12px',
        borderRadius: '6px',
        marginBottom: '16px',
        fontSize: '14px',
        textAlign: 'center'
    },
    champ: { marginBottom: '16px' },
    input: {
        width: '100%',
        padding: '12px',
        borderRadius: '6px',
        border: '1px solid #ddd',
        fontSize: '14px',
        boxSizing: 'border-box',
        outline: 'none'
    },
    erreurChamp: {
        color: '#E74C3C',
        fontSize: '12px',
        margin: '4px 0 0 0'
    },
    button: {
        width: '100%',
        padding: '12px',
        backgroundColor: '#4CAF50',
        color: 'white',
        border: 'none',
        borderRadius: '6px',
        fontSize: '16px',
        cursor: 'pointer',
        marginTop: '8px'
    },
    link: { textAlign: 'center', marginTop: '16px' }
};