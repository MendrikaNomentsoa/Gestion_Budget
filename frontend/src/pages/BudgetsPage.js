import React, { useEffect, useState } from 'react';
import { toast } from 'react-toastify';
import budgetService from '../services/budgetService';
import categoryService from '../services/categoryService';

export default function BudgetsPage() {
    const [budgets, setBudgets] = useState([]);
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showForm, setShowForm] = useState(false);
    const [budgetAModifier, setBudgetAModifier] = useState(null);
    const [form, setForm] = useState({
        categoryId: '',
        limite: '',
        mois: new Date().getMonth() + 1,
        annee: new Date().getFullYear()
    });

    useEffect(() => {
        charger();
    }, []);

    const charger = async () => {
        try {
            const [budgetsRes, catRes] = await Promise.all([
                budgetService.lister(),
                categoryService.lister()
            ]);
            setBudgets(budgetsRes.data);
            setCategories(catRes.data);
        } catch (err) {
            toast.error('Erreur lors du chargement');
        } finally {
            setLoading(false);
        }
    };

    const handleModifier = (budget) => {
        setBudgetAModifier(budget);
        setForm({
            categoryId: budget.categoryId,
            limite: budget.limite,
            mois: budget.mois,
            annee: budget.annee
        });
        setShowForm(true);
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    const handleAnnuler = () => {
        setShowForm(false);
        setBudgetAModifier(null);
        setForm({
            categoryId: '',
            limite: '',
            mois: new Date().getMonth() + 1,
            annee: new Date().getFullYear()
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const data = {
                ...form,
                limite: parseFloat(form.limite),
                categoryId: parseInt(form.categoryId),
                mois: parseInt(form.mois),
                annee: parseInt(form.annee)
            };

            if (budgetAModifier) {
                await budgetService.modifier(budgetAModifier.id, data);
                toast.success('Budget modifié avec succès !');
            } else {
                await budgetService.creer(data);
                toast.success('Budget créé avec succès !');
            }

            handleAnnuler();
            charger();
        } catch (err) {
            toast.error(err.response?.data?.erreur || 'Erreur lors de la sauvegarde');
        }
    };

    const handleSupprimer = async (id) => {
        if (window.confirm('Supprimer ce budget ?')) {
            try {
                await budgetService.supprimer(id);
                toast.success('Budget supprimé');
                charger();
            } catch (err) {
                toast.error('Erreur lors de la suppression');
            }
        }
    };

    // Noms des mois pour l'affichage
    const nomsMois = [
        '', 'Janvier', 'Février', 'Mars', 'Avril', 'Mai', 'Juin',
        'Juillet', 'Août', 'Septembre', 'Octobre', 'Novembre', 'Décembre'
    ];

    if (loading) return <p style={{ textAlign: 'center', marginTop: '50px' }}>Chargement...</p>;

    return (
        <div style={styles.container}>
            <div style={styles.header}>
                <h2>Budgets</h2>
                <button style={styles.btnAjouter} onClick={() => {
                    setBudgetAModifier(null);
                    setShowForm(!showForm);
                }}>
                    {showForm ? 'Annuler' : '+ Ajouter'}
                </button>
            </div>

            {/* Formulaire ajout/modification */}
            {showForm && (
                <div style={styles.form}>
                    <h3 style={{ marginBottom: '16px', color: '#333' }}>
                        {budgetAModifier ? 'Modifier le budget' : 'Nouveau budget'}
                    </h3>
                    <form onSubmit={handleSubmit}>
                        <div style={styles.formGrid}>
                            <div>
                                <label style={styles.label}>Catégorie</label>
                                <select
                                    style={styles.input}
                                    value={form.categoryId}
                                    onChange={e => setForm({ ...form, categoryId: e.target.value })}
                                    required
                                >
                                    <option value="">-- Choisir --</option>
                                    {categories.map(c => (
                                        <option key={c.id} value={c.id}>{c.nom}</option>
                                    ))}
                                </select>
                            </div>
                            <div>
                                <label style={styles.label}>Limite (Ar)</label>
                                <input
                                    style={styles.input}
                                    type="number"
                                    placeholder="ex: 100000"
                                    value={form.limite}
                                    onChange={e => setForm({ ...form, limite: e.target.value })}
                                    required
                                    min="0"
                                />
                            </div>
                            <div>
                                <label style={styles.label}>Mois</label>
                                <select
                                    style={styles.input}
                                    value={form.mois}
                                    onChange={e => setForm({ ...form, mois: e.target.value })}
                                >
                                    {nomsMois.slice(1).map((nom, i) => (
                                        <option key={i + 1} value={i + 1}>{nom}</option>
                                    ))}
                                </select>
                            </div>
                            <div>
                                <label style={styles.label}>Année</label>
                                <input
                                    style={styles.input}
                                    type="number"
                                    value={form.annee}
                                    onChange={e => setForm({ ...form, annee: e.target.value })}
                                    required
                                    min="2020"
                                    max="2030"
                                />
                            </div>
                        </div>
                        <div style={{ display: 'flex', gap: '12px' }}>
                            <button style={styles.btnSauvegarder} type="submit">
                                {budgetAModifier ? 'Modifier' : 'Sauvegarder'}
                            </button>
                            <button style={styles.btnAnnuler} type="button" onClick={handleAnnuler}>
                                Annuler
                            </button>
                        </div>
                    </form>
                </div>
            )}

            {/* Liste des budgets */}
            {budgets.length === 0 ? (
                <div style={styles.vide}>
                    Aucun budget — définissez vos limites de dépenses !
                </div>
            ) : (
                <div style={styles.liste}>
                    {budgets.map(b => (
                        <div key={b.id} style={styles.budget}>
                            <div style={styles.budgetInfo}>
                                <h3 style={styles.budgetTitre}>{b.categoryNom}</h3>
                                <p style={styles.budgetPeriode}>
                                    {nomsMois[b.mois]} {b.annee}
                                </p>
                            </div>
                            <div style={styles.budgetLimite}>
                                <span style={styles.limiteLabel}>Limite</span>
                                <span style={styles.limiteMontant}>
                                    {b.limite.toLocaleString()} Ar
                                </span>
                            </div>
                            <div style={{ display: 'flex', gap: '8px' }}>
                                <button
                                    style={styles.btnModifier}
                                    onClick={() => handleModifier(b)}
                                >
                                    Modifier
                                </button>
                                <button
                                    style={styles.btnSupprimer}
                                    onClick={() => handleSupprimer(b.id)}
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
    container: { padding: '24px', maxWidth: '900px', margin: '0 auto' },
    header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' },
    btnAjouter: { padding: '10px 20px', backgroundColor: '#4CAF50', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer', fontSize: '14px' },
    form: { backgroundColor: 'white', padding: '24px', borderRadius: '8px', boxShadow: '0 2px 10px rgba(0,0,0,0.1)', marginBottom: '24px' },
    formGrid: { display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '12px', marginBottom: '16px' },
    label: { display: 'block', fontSize: '12px', color: '#666', marginBottom: '4px' },
    input: { width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #ddd', fontSize: '14px', boxSizing: 'border-box' },
    btnSauvegarder: { padding: '10px 24px', backgroundColor: '#2196F3', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer' },
    btnAnnuler: { padding: '10px 24px', backgroundColor: '#95a5a6', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer' },
    liste: { display: 'flex', flexDirection: 'column', gap: '12px' },
    budget: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', backgroundColor: 'white', padding: '20px', borderRadius: '8px', boxShadow: '0 2px 10px rgba(0,0,0,0.1)' },
    budgetInfo: { flex: 1 },
    budgetTitre: { margin: '0 0 4px 0', fontSize: '16px', color: '#333' },
    budgetPeriode: { margin: 0, fontSize: '13px', color: '#666' },
    budgetLimite: { display: 'flex', flexDirection: 'column', alignItems: 'center', marginRight: '24px' },
    limiteLabel: { fontSize: '12px', color: '#666' },
    limiteMontant: { fontSize: '18px', fontWeight: 'bold', color: '#2196F3' },
    btnModifier: { padding: '6px 12px', backgroundColor: '#F39C12', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '12px' },
    btnSupprimer: { padding: '6px 12px', backgroundColor: '#E74C3C', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '12px' },
    vide: { textAlign: 'center', padding: '40px', backgroundColor: 'white', borderRadius: '8px', color: '#666' }
};