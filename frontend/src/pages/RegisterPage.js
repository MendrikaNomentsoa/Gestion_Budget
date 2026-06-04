import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { toast } from 'react-toastify';
import { useAuth } from '../context/AuthContext';

export default function RegisterPage() {
    const [nom, setNom] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [montantInitial, setMontantInitial] = useState('');
    const [loading, setLoading] = useState(false);
    const [erreurs, setErreurs] = useState({});

    const { register } = useAuth();
    const navigate = useNavigate();

    /**
     * Validation côté frontend avant d'envoyer au backend
     * Retourne true si tout est valide
     */
    const valider = () => {
        const nouvErreurs = {};

        if (!nom.trim()) {
            nouvErreurs.nom = 'Le nom est obligatoire';
        }

        if (!email.trim()) {
            nouvErreurs.email = "L'email est obligatoire";
        } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(email)){
            nouvErreurs.email = "Format d'email invalide (ex: nom@gmail.com)";
        }

        if (!password) {
            nouvErreurs.password = 'Le mot de passe est obligatoire';
        } else if (password.length < 6) {
            nouvErreurs.password = 'Le mot de passe doit contenir au moins 6 caractères';
        }

        if (!confirmPassword) {
            nouvErreurs.confirmPassword = 'Confirmez votre mot de passe';
        } else if (password !== confirmPassword) {
            nouvErreurs.confirmPassword = 'Les mots de passe ne correspondent pas';
        }

        if (montantInitial && parseFloat(montantInitial) < 0) {
            nouvErreurs.montantInitial = 'Le montant ne peut pas être négatif';
        }

        setErreurs(nouvErreurs);
        return Object.keys(nouvErreurs).length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        // Valide avant d'envoyer
        if (!valider()) return;

        setLoading(true);
        try {
            await register(nom, email, password, parseFloat(montantInitial) || 0);
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            toast.success('Compte créé avec succès ! Connectez-vous.');
            navigate('/login');
        } catch (err) {
            toast.error(err.response?.data?.erreur || 'Erreur lors de l\'inscription');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={styles.container}>
            <div style={styles.card}>
                <h2 style={styles.title}>Inscription</h2>
                <form onSubmit={handleSubmit}>

                    {/* Nom */}
                    <div style={styles.champ}>
                        <input
                            style={{
                                ...styles.input,
                                borderColor: erreurs.nom ? '#E74C3C' : '#ddd'
                            }}
                            type="text"
                            placeholder="Nom"
                            value={nom}
                            onChange={e => {
                                setNom(e.target.value);
                                setErreurs({ ...erreurs, nom: '' });
                            }}
                        />
                        {erreurs.nom && <p style={styles.erreurChamp}>{erreurs.nom}</p>}
                    </div>

                    {/* Email */}
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
                                setErreurs({ ...erreurs, email: '' });
                            }}
                        />
                        {erreurs.email && <p style={styles.erreurChamp}>{erreurs.email}</p>}
                    </div>

                    {/* Mot de passe */}
                    <div style={styles.champ}>
                        <input
                            style={{
                                ...styles.input,
                                borderColor: erreurs.password ? '#E74C3C' : '#ddd'
                            }}
                            type="password"
                            placeholder="Mot de passe (min. 6 caractères)"
                            value={password}
                            onChange={e => {
                                setPassword(e.target.value);
                                setErreurs({ ...erreurs, password: '' });
                            }}
                        />
                        {erreurs.password && <p style={styles.erreurChamp}>{erreurs.password}</p>}
                    </div>

                    {/* Confirmation mot de passe */}
                    <div style={styles.champ}>
                        <input
                            style={{
                                ...styles.input,
                                borderColor: erreurs.confirmPassword ? '#E74C3C' : '#ddd'
                            }}
                            type="password"
                            placeholder="Confirmer le mot de passe"
                            value={confirmPassword}
                            onChange={e => {
                                setConfirmPassword(e.target.value);
                                setErreurs({ ...erreurs, confirmPassword: '' });
                            }}
                        />
                        {erreurs.confirmPassword && <p style={styles.erreurChamp}>{erreurs.confirmPassword}</p>}
                    </div>

                    {/* Solde initial */}
                    <div style={styles.champ}>
                        <input
                            style={{
                                ...styles.input,
                                borderColor: erreurs.montantInitial ? '#E74C3C' : '#ddd'
                            }}
                            type="number"
                            placeholder="Solde initial (Ar) — optionnel"
                            value={montantInitial}
                            onChange={e => {
                                setMontantInitial(e.target.value);
                                setErreurs({ ...erreurs, montantInitial: '' });
                            }}
                            min="0"
                        />
                        {erreurs.montantInitial && <p style={styles.erreurChamp}>{erreurs.montantInitial}</p>}
                    </div>

                    <button style={styles.button} type="submit" disabled={loading}>
                        {loading ? 'Inscription...' : "S'inscrire"}
                    </button>
                </form>
                <p style={styles.link}>
                    Déjà un compte ? <Link to="/login">Se connecter</Link>
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
        minHeight: '100vh',
        backgroundColor: '#f5f5f5',
        padding: '20px'
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
        backgroundColor: '#2196F3',
        color: 'white',
        border: 'none',
        borderRadius: '6px',
        fontSize: '16px',
        cursor: 'pointer',
        marginTop: '8px'
    },
    link: { textAlign: 'center', marginTop: '16px' }
};