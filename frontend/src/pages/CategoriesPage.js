import React, { useEffect, useState } from 'react';
import { toast } from 'react-toastify';
import categoryService from '../services/categoryService';

export default function CategoriesPage() {
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showForm, setShowForm] = useState(false);
    const [categorieAModifier, setCategorieAModifier] = useState(null);
    const [form, setForm] = useState({ nom: '', couleur: '#FF5733' });

    useEffect(() => {
        charger();
    }, []);

    const charger = async () => {
        try {
            const res = await categoryService.lister();
            setCategories(res.data);
        } catch (err) {
            toast.error('Erreur lors du chargement');
        } finally {
            setLoading(false);
        }
    };

    const handleModifier = (categorie) => {
        setCategorieAModifier(categorie);
        setForm({ nom: categorie.nom, couleur: categorie.couleur || '#FF5733' });
        setShowForm(true);
    };

    const handleAnnuler = () => {
        setShowForm(false);
        setCategorieAModifier(null);
        setForm({ nom: '', couleur: '#FF5733' });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (categorieAModifier) {
                await categoryService.modifier(categorieAModifier.id, form);
                toast.success('Catégorie modifiée avec succès !');
            } else {
                await categoryService.creer(form);
                toast.success('Catégorie créée avec succès !');
            }
            handleAnnuler();
            charger();
        } catch (err) {
            toast.error(err.response?.data?.erreur || 'Erreur lors de la sauvegarde');
        }
    };

    const handleSupprimer = async (id) => {
        if (window.confirm('Supprimer cette catégorie ?')) {
            try {
                await categoryService.supprimer(id);
                toast.success('Catégorie supprimée');
                charger();
            } catch (err) {
                toast.error('Erreur lors de la suppression');
            }
        }
    };

    if (loading) return <p style={{ textAlign: 'center', marginTop: '50px' }}>Chargement...</p>;

    return (
        <div style={styles.container}>
            <div style={styles.header}>
                <h2>Catégories</h2>
                <button style={styles.btnAjouter} onClick={() => {
                    setCategorieAModifier(null);
                    setShowForm(!showForm);
                }}>
                    {showForm ? 'Annuler' : '+ Ajouter'}
                </button>
            </div>

            {/* Formulaire ajout/modification */}
            {showForm && (
                <div style={styles.form}>
                    <h3 style={{ marginBottom: '16px', color: '#333' }}>
                        {categorieAModifier ? 'Modifier la catégorie' : 'Nouvelle catégorie'}
                    </h3>
                    <form onSubmit={handleSubmit} style={styles.formRow}>
                        <div style={{ flex: 1 }}>
                            <label style={styles.label}>Nom</label>
                            <input
                                style={styles.input}
                                type="text"
                                placeholder="ex: Nourriture"
                                value={form.nom}
                                onChange={e => setForm({ ...form, nom: e.target.value })}
                                required
                            />
                        </div>
                        <div>
                            <label style={styles.label}>Couleur</label>
                            <input
                                style={{ ...styles.input, width: '60px', padding: '8px' }}
                                type="color"
                                value={form.couleur}
                                onChange={e => setForm({ ...form, couleur: e.target.value })}
                            />
                        </div>
                        <div style={{ display: 'flex', alignItems: 'flex-end', gap: '8px' }}>
                            <button style={styles.btnSauvegarder} type="submit">
                                {categorieAModifier ? 'Modifier' : 'Ajouter'}
                            </button>
                            <button style={styles.btnAnnuler} type="button" onClick={handleAnnuler}>
                                Annuler
                            </button>
                        </div>
                    </form>
                </div>
            )}

            {/* Liste des catégories */}
            {categories.length === 0 ? (
                <div style={styles.vide}>Aucune catégorie — créez-en une !</div>
            ) : (
                <div style={styles.liste}>
                    {categories.map(c => (
                        <div key={c.id} style={styles.categorie}>
                            <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                                <div style={{
                                    width: '24px',
                                    height: '24px',
                                    borderRadius: '50%',
                                    backgroundColor: c.couleur || '#ccc'
                                }} />
                                <span style={{ fontWeight: '500' }}>{c.nom}</span>
                            </div>
                            <div style={{ display: 'flex', gap: '8px' }}>
                                <button
                                    style={styles.btnModifier}
                                    onClick={() => handleModifier(c)}
                                >
                                    Modifier
                                </button>
                                <button
                                    style={styles.btnSupprimer}
                                    onClick={() => handleSupprimer(c.id)}
                                >
                                    Supprimer
                                </button>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

const styles = {
    container: { padding: '24px', maxWidth: '800px', margin: '0 auto' },
    header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' },
    btnAjouter: { padding: '10px 20px', backgroundColor: '#4CAF50', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer', fontSize: '14px' },
    form: { backgroundColor: 'white', padding: '24px', borderRadius: '8px', boxShadow: '0 2px 10px rgba(0,0,0,0.1)', marginBottom: '24px' },
    formRow: { display: 'flex', gap: '12px', alignItems: 'flex-end' },
    label: { display: 'block', fontSize: '12px', color: '#666', marginBottom: '4px' },
    input: { width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #ddd', fontSize: '14px', boxSizing: 'border-box' },
    btnSauvegarder: { padding: '10px 24px', backgroundColor: '#2196F3', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer' },
    btnAnnuler: { padding: '10px 24px', backgroundColor: '#95a5a6', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer' },
    liste: { display: 'flex', flexDirection: 'column', gap: '8px' },
    categorie: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', backgroundColor: 'white', padding: '16px', borderRadius: '8px', boxShadow: '0 2px 10px rgba(0,0,0,0.1)' },
    btnModifier: { padding: '6px 12px', backgroundColor: '#F39C12', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '12px' },
    btnSupprimer: { padding: '6px 12px', backgroundColor: '#E74C3C', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '12px' },
    vide: { textAlign: 'center', padding: '40px', backgroundColor: 'white', borderRadius: '8px', color: '#666' }
};