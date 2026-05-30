import React, { useEffect, useState } from 'react';
import transactionService from '../services/transactionService';
import categoryService from '../services/categoryService';

export default function TransactionsPage() {
    const [transactions, setTransactions] = useState([]);
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showForm, setShowForm] = useState(false);
    const [form, setForm] = useState({
        montant: '',
        type: 'DEPENSE',
        description: '',
        date: new Date().toISOString().split('T')[0],
        categoryId: ''
    });

    useEffect(() => {
        charger();
    }, []);

    const charger = async () => {
        try {
            const [transRes, catRes] = await Promise.all([
                transactionService.lister(),
                categoryService.lister()
            ]);
            setTransactions(transRes.data);
            setCategories(catRes.data);
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await transactionService.creer({
                ...form,
                montant: parseFloat(form.montant),
                categoryId: form.categoryId || null
            });
            setShowForm(false);
            setForm({
                montant: '',
                type: 'DEPENSE',
                description: '',
                date: new Date().toISOString().split('T')[0],
                categoryId: ''
            });
            charger();
        } catch (err) {
            console.error(err);
        }
    };

    const handleSupprimer = async (id) => {
        if (window.confirm('Supprimer cette transaction ?')) {
            await transactionService.supprimer(id);
            charger();
        }
    };

    if (loading) return <p style={{ textAlign: 'center', marginTop: '50px' }}>Chargement...</p>;

    return (
        <div style={styles.container}>
            <div style={styles.header}>
                <h2>Transactions</h2>
                <button style={styles.btnAjouter} onClick={() => setShowForm(!showForm)}>
                    {showForm ? 'Annuler' : '+ Ajouter'}
                </button>
            </div>

            {/* Formulaire ajout */}
            {showForm && (
                <div style={styles.form}>
                    <form onSubmit={handleSubmit}>
                        <div style={styles.formGrid}>
                            <input
                                style={styles.input}
                                type="number"
                                placeholder="Montant (Ar)"
                                value={form.montant}
                                onChange={e => setForm({ ...form, montant: e.target.value })}
                                required
                            />
                            <select
                                style={styles.input}
                                value={form.type}
                                onChange={e => setForm({ ...form, type: e.target.value })}
                            >
                                <option value="DEPENSE">Dépense</option>
                                <option value="REVENU">Revenu</option>
                            </select>
                            <input
                                style={styles.input}
                                type="text"
                                placeholder="Description"
                                value={form.description}
                                onChange={e => setForm({ ...form, description: e.target.value })}
                            />
                            <input
                                style={styles.input}
                                type="date"
                                value={form.date}
                                onChange={e => setForm({ ...form, date: e.target.value })}
                                required
                            />
                            <select
                                style={styles.input}
                                value={form.categoryId}
                                onChange={e => setForm({ ...form, categoryId: e.target.value })}
                            >
                                <option value="">-- Catégorie --</option>
                                {categories.map(c => (
                                    <option key={c.id} value={c.id}>{c.nom}</option>
                                ))}
                            </select>
                        </div>
                        <button style={styles.btnSauvegarder} type="submit">
                            Sauvegarder
                        </button>
                    </form>
                </div>
            )}

            {/* Liste des transactions */}
            <table style={styles.table}>
                <thead>
                    <tr style={styles.thead}>
                        <th>Date</th>
                        <th>Description</th>
                        <th>Catégorie</th>
                        <th>Type</th>
                        <th>Montant</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {transactions.map(t => (
                        <tr key={t.id} style={styles.tr}>
                            <td>{t.date}</td>
                            <td>{t.description || '-'}</td>
                            <td>{t.categoryNom || '-'}</td>
                            <td>
                                <span style={{
                                    ...styles.badge,
                                    backgroundColor: t.type === 'DEPENSE' ? '#E74C3C' : '#2ECC71'
                                }}>
                                    {t.type}
                                </span>
                            </td>
                            <td style={{ color: t.type === 'DEPENSE' ? '#E74C3C' : '#2ECC71', fontWeight: 'bold' }}>
                                {t.type === 'DEPENSE' ? '-' : '+'}{t.montant.toLocaleString()} Ar
                            </td>
                            <td>
                                <button
                                    style={styles.btnSupprimer}
                                    onClick={() => handleSupprimer(t.id)}
                                >
                                    Supprimer
                                </button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}

const styles = {
    container: { padding: '24px', maxWidth: '1200px', margin: '0 auto' },
    header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' },
    btnAjouter: {
        padding: '10px 20px',
        backgroundColor: '#4CAF50',
        color: 'white',
        border: 'none',
        borderRadius: '6px',
        cursor: 'pointer',
        fontSize: '14px'
    },
    form: {
        backgroundColor: 'white',
        padding: '24px',
        borderRadius: '8px',
        boxShadow: '0 2px 10px rgba(0,0,0,0.1)',
        marginBottom: '24px'
    },
    formGrid: { display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '12px', marginBottom: '16px' },
    input: {
        padding: '10px',
        borderRadius: '6px',
        border: '1px solid #ddd',
        fontSize: '14px',
        width: '100%',
        boxSizing: 'border-box'
    },
    btnSauvegarder: {
        padding: '10px 24px',
        backgroundColor: '#2196F3',
        color: 'white',
        border: 'none',
        borderRadius: '6px',
        cursor: 'pointer'
    },
    table: { width: '100%', borderCollapse: 'collapse', backgroundColor: 'white', borderRadius: '8px', overflow: 'hidden', boxShadow: '0 2px 10px rgba(0,0,0,0.1)' },
    thead: { backgroundColor: '#f5f5f5' },
    tr: { borderBottom: '1px solid #eee' },
    badge: { padding: '4px 8px', borderRadius: '4px', color: 'white', fontSize: '12px' },
    btnSupprimer: {
        padding: '6px 12px',
        backgroundColor: '#E74C3C',
        color: 'white',
        border: 'none',
        borderRadius: '4px',
        cursor: 'pointer',
        fontSize: '12px'
    }
};