import React, { useEffect, useState } from 'react';
import categoryService from '../services/categoryService';

export default function CategoriesPage() {
    const [categories, setCategories] = useState([]);
    const [nom, setNom] = useState('');
    const [couleur, setCouleur] = useState('#FF5733');
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        charger();
    }, []);

    const charger = async () => {
        try {
            const res = await categoryService.lister();
            setCategories(res.data);
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await categoryService.creer({ nom, couleur });
            setNom('');
            setCouleur('#FF5733');
            charger();
        } catch (err) {
            console.error(err);
        }
    };

    const handleSupprimer = async (id) => {
        if (window.confirm('Supprimer cette catégorie ?')) {
            await categoryService.supprimer(id);
            charger();
        }
    };

    if (loading) return <p style={{ textAlign: 'center', marginTop: '50px' }}>Chargement...</p>;

    return (
        <div style={styles.container}>
            <h2>Catégories</h2>

            {/* Formulaire ajout */}
            <div style={styles.form}>
                <form onSubmit={handleSubmit} style={styles.formRow}>
                    <input
                        style={styles.input}
                        type="text"
                        placeholder="Nom de la catégorie"
                        value={nom}
                        onChange={e => setNom(e.target.value)}
                        required
                    />
                    <input
                        style={{ ...styles.input, width: '60px', padding: '8px' }}
                        type="color"
                        value={couleur}
                        onChange={e => setCouleur(e.target.value)}
                    />
                    <button style={styles.btnAjouter} type="submit">
                        Ajouter
                    </button>
                </form>
            </div>

            {/* Liste des catégories */}
            <div style={styles.liste}>
                {categories.map(c => (
                    <div key={c.id} style={styles.categorie}>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                            <div style={{
                                width: '20px',
                                height: '20px',
                                borderRadius: '50%',
                                backgroundColor: c.couleur
                            }} />
                            <span>{c.nom}</span>
                        </div>
                        <button
                            style={styles.btnSupprimer}
                            onClick={() => handleSupprimer(c.id)}
                        >
                            Supprimer
                        </button>
                    </div>
                ))}
            </div>
        </div>
    );
}

const styles = {
    container: { padding: '24px', maxWidth: '800px', margin: '0 auto' },
    form: {
        backgroundColor: 'white',
        padding: '24px',
        borderRadius: '8px',
        boxShadow: '0 2px 10px rgba(0,0,0,0.1)',
        marginBottom: '24px'
    },
    formRow: { display: 'flex', gap: '12px', alignItems: 'center' },
    input: {
        flex: 1,
        padding: '10px',
        borderRadius: '6px',
        border: '1px solid #ddd',
        fontSize: '14px'
    },
    btnAjouter: {
        padding: '10px 20px',
        backgroundColor: '#4CAF50',
        color: 'white',
        border: 'none',
        borderRadius: '6px',
        cursor: 'pointer'
    },
    liste: { display: 'flex', flexDirection: 'column', gap: '8px' },
    categorie: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        backgroundColor: 'white',
        padding: '16px',
        borderRadius: '8px',
        boxShadow: '0 2px 10px rgba(0,0,0,0.1)'
    },
    btnSupprimer: {
        padding: '6px 12px',
        backgroundColor: '#E74C3C',
        color: 'white',
        border: 'none',
        borderRadius: '4px',
        cursor: 'pointer'
    }
};