import React, { useEffect, useState } from 'react';
import { toast } from 'react-toastify';
import transactionService from '../services/transactionService';
import categoryService from '../services/categoryService';

export default function TransactionsPage() {
    const [transactions, setTransactions] = useState([]);
    const [transactionsFiltrees, setTransactionsFiltrees] = useState([]);
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showForm, setShowForm] = useState(false);
    const [transactionAModifier, setTransactionAModifier] = useState(null);

    // Filtres
    const [filtreType, setFiltreType] = useState('TOUS');
    const [filtreCategorie, setFiltreCategorie] = useState('');
    const [filtreDateDebut, setFiltreDateDebut] = useState('');
    const [filtreDateFin, setFiltreDateFin] = useState('');

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

    // Applique les filtres à chaque changement
    useEffect(() => {
        appliquerFiltres();
    }, [transactions, filtreType, filtreCategorie, filtreDateDebut, filtreDateFin]);

    const charger = async () => {
        try {
            const [transRes, catRes] = await Promise.all([
                transactionService.lister(),
                categoryService.lister()
            ]);
            setTransactions(transRes.data);
            setCategories(catRes.data);
        } catch (err) {
            toast.error('Erreur lors du chargement');
        } finally {
            setLoading(false);
        }
    };

    /**
     * Applique les filtres sur la liste des transactions
     */
    const appliquerFiltres = () => {
        let resultat = [...transactions];

        // Filtre par type
        if (filtreType !== 'TOUS') {
            resultat = resultat.filter(t => t.type === filtreType);
        }

        // Filtre par catégorie
        if (filtreCategorie) {
            resultat = resultat.filter(t => t.categoryId === parseInt(filtreCategorie));
        }

        // Filtre par date début
        if (filtreDateDebut) {
            resultat = resultat.filter(t => t.date >= filtreDateDebut);
        }

        // Filtre par date fin
        if (filtreDateFin) {
            resultat = resultat.filter(t => t.date <= filtreDateFin);
        }

        setTransactionsFiltrees(resultat);
    };

    const reinitialiserFiltres = () => {
        setFiltreType('TOUS');
        setFiltreCategorie('');
        setFiltreDateDebut('');
        setFiltreDateFin('');
    };

    /**
     * Ouvre le formulaire en mode modification
     */
    const handleModifier = (transaction) => {
        setTransactionAModifier(transaction);
        setForm({
            montant: transaction.montant,
            type: transaction.type,
            description: transaction.description || '',
            date: transaction.date,
            categoryId: transaction.categoryId || ''
        });
        setShowForm(true);
        // Scroll vers le formulaire
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const data = {
                ...form,
                montant: parseFloat(form.montant),
                categoryId: form.categoryId || null
            };

            if (transactionAModifier) {
                // Mode modification
                await transactionService.modifier(transactionAModifier.id, data);
                toast.success('Transaction modifiée avec succès !');
            } else {
                // Mode création
                await transactionService.creer(data);
                toast.success('Transaction enregistrée avec succès !');
            }

            // Réinitialise le formulaire
            setShowForm(false);
            setTransactionAModifier(null);
            setForm({
                montant: '',
                type: 'DEPENSE',
                description: '',
                date: new Date().toISOString().split('T')[0],
                categoryId: ''
            });
            charger();
        } catch (err) {
            toast.error(err.response?.data?.erreur || 'Erreur lors de la sauvegarde');
        }
    };

    const handleAnnuler = () => {
        setShowForm(false);
        setTransactionAModifier(null);
        setForm({
            montant: '',
            type: 'DEPENSE',
            description: '',
            date: new Date().toISOString().split('T')[0],
            categoryId: ''
        });
    };

    const handleSupprimer = async (id) => {
        if (window.confirm('Supprimer cette transaction ?')) {
            try {
                await transactionService.supprimer(id);
                toast.success('Transaction supprimée');
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
                <h2>Transactions ({transactionsFiltrees.length})</h2>
                <button style={styles.btnAjouter} onClick={() => {
                    setTransactionAModifier(null);
                    setShowForm(!showForm);
                }}>
                    {showForm ? 'Annuler' : '+ Ajouter'}
                </button>
            </div>

            {/* Formulaire ajout/modification */}
            {showForm && (
                <div style={styles.form}>
                    <h3 style={{ marginBottom: '16px', color: '#333' }}>
                        {transactionAModifier ? 'Modifier la transaction' : 'Nouvelle transaction'}
                    </h3>
                    <form onSubmit={handleSubmit}>
                        <div style={styles.formGrid}>
                            <div>
                                <label style={styles.label}>Montant (Ar)</label>
                                <input
                                    style={styles.input}
                                    type="number"
                                    placeholder="ex: 50000"
                                    value={form.montant}
                                    onChange={e => setForm({ ...form, montant: e.target.value })}
                                    required
                                />
                            </div>
                            <div>
                                <label style={styles.label}>Type</label>
                                <select
                                    style={styles.input}
                                    value={form.type}
                                    onChange={e => setForm({ ...form, type: e.target.value })}
                                >
                                    <option value="DEPENSE">Dépense</option>
                                    <option value="REVENU">Revenu</option>
                                </select>
                            </div>
                            <div>
                                <label style={styles.label}>Description</label>
                                <input
                                    style={styles.input}
                                    type="text"
                                    placeholder="ex: Courses supermarché"
                                    value={form.description}
                                    onChange={e => setForm({ ...form, description: e.target.value })}
                                />
                            </div>
                            <div>
                                <label style={styles.label}>Date</label>
                                <input
                                    style={styles.input}
                                    type="date"
                                    value={form.date}
                                    onChange={e => setForm({ ...form, date: e.target.value })}
                                    required
                                />
                            </div>
                            <div>
                                <label style={styles.label}>Catégorie</label>
                                <select
                                    style={styles.input}
                                    value={form.categoryId}
                                    onChange={e => setForm({ ...form, categoryId: e.target.value })}
                                >
                                    <option value="">-- Aucune catégorie --</option>
                                    {categories.map(c => (
                                        <option key={c.id} value={c.id}>{c.nom}</option>
                                    ))}
                                </select>
                            </div>
                        </div>
                        <div style={{ display: 'flex', gap: '12px' }}>
                            <button style={styles.btnSauvegarder} type="submit">
                                {transactionAModifier ? 'Modifier' : 'Sauvegarder'}
                            </button>
                            <button style={styles.btnAnnuler} type="button" onClick={handleAnnuler}>
                                Annuler
                            </button>
                        </div>
                    </form>
                </div>
            )}

            {/* Filtres */}
            <div style={styles.filtres}>
                <h4 style={{ margin: '0 0 12px 0' }}>Filtres</h4>
                <div style={styles.filtresGrid}>
                    <div>
                        <label style={styles.label}>Type</label>
                        <select
                            style={styles.input}
                            value={filtreType}
                            onChange={e => setFiltreType(e.target.value)}
                        >
                            <option value="TOUS">Tous</option>
                            <option value="DEPENSE">Dépenses</option>
                            <option value="REVENU">Revenus</option>
                        </select>
                    </div>
                    <div>
                        <label style={styles.label}>Catégorie</label>
                        <select
                            style={styles.input}
                            value={filtreCategorie}
                            onChange={e => setFiltreCategorie(e.target.value)}
                        >
                            <option value="">Toutes</option>
                            {categories.map(c => (
                                <option key={c.id} value={c.id}>{c.nom}</option>
                            ))}
                        </select>
                    </div>
                    <div>
                        <label style={styles.label}>Date début</label>
                        <input
                            style={styles.input}
                            type="date"
                            value={filtreDateDebut}
                            onChange={e => setFiltreDateDebut(e.target.value)}
                        />
                    </div>
                    <div>
                        <label style={styles.label}>Date fin</label>
                        <input
                            style={styles.input}
                            type="date"
                            value={filtreDateFin}
                            onChange={e => setFiltreDateFin(e.target.value)}
                        />
                    </div>
                    <div style={{ display: 'flex', alignItems: 'flex-end' }}>
                        <button style={styles.btnReinit} onClick={reinitialiserFiltres}>
                            Réinitialiser
                        </button>
                    </div>
                </div>
            </div>

            {/* Liste des transactions */}
            {transactionsFiltrees.length === 0 ? (
                <div style={styles.vide}>Aucune transaction trouvée</div>
            ) : (
                <table style={styles.table}>
                    <thead>
                        <tr style={styles.thead}>
                            <th style={styles.th}>Date</th>
                            <th style={styles.th}>Description</th>
                            <th style={styles.th}>Catégorie</th>
                            <th style={styles.th}>Type</th>
                            <th style={styles.th}>Montant</th>
                            <th style={styles.th}>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {transactionsFiltrees.map(t => (
                            <tr key={t.id} style={styles.tr}>
                                <td style={styles.td}>{t.date}</td>
                                <td style={styles.td}>{t.description || '-'}</td>
                                <td style={styles.td}>{t.categoryNom || '-'}</td>
                                <td style={styles.td}>
                                    <span style={{
                                        ...styles.badge,
                                        backgroundColor: t.type === 'DEPENSE' ? '#E74C3C' : '#2ECC71'
                                    }}>
                                        {t.type}
                                    </span>
                                </td>
                                <td style={{
                                    ...styles.td,
                                    color: t.type === 'DEPENSE' ? '#E74C3C' : '#2ECC71',
                                    fontWeight: 'bold'
                                }}>
                                    {t.type === 'DEPENSE' ? '-' : '+'}{t.montant.toLocaleString()} Ar
                                </td>
                                <td style={styles.td}>
                                    <button
                                        style={styles.btnModifier}
                                        onClick={() => handleModifier(t)}
                                    >
                                        Modifier
                                    </button>
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
            )}
        </div>
    );
}

const styles = {
    container: { padding: '24px', maxWidth: '1200px', margin: '0 auto' },
    header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' },
    btnAjouter: { padding: '10px 20px', backgroundColor: '#4CAF50', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer', fontSize: '14px' },
    form: { backgroundColor: 'white', padding: '24px', borderRadius: '8px', boxShadow: '0 2px 10px rgba(0,0,0,0.1)', marginBottom: '24px' },
    formGrid: { display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '12px', marginBottom: '16px' },
    filtres: { backgroundColor: 'white', padding: '24px', borderRadius: '8px', boxShadow: '0 2px 10px rgba(0,0,0,0.1)', marginBottom: '24px' },
    filtresGrid: { display: 'grid', gridTemplateColumns: 'repeat(5, 1fr)', gap: '12px' },
    label: { display: 'block', fontSize: '12px', color: '#666', marginBottom: '4px' },
    input: { width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #ddd', fontSize: '14px', boxSizing: 'border-box' },
    btnSauvegarder: { padding: '10px 24px', backgroundColor: '#2196F3', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer' },
    btnAnnuler: { padding: '10px 24px', backgroundColor: '#95a5a6', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer' },
    btnReinit: { padding: '10px 16px', backgroundColor: '#95a5a6', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer', width: '100%' },
    table: { width: '100%', borderCollapse: 'collapse', backgroundColor: 'white', borderRadius: '8px', overflow: 'hidden', boxShadow: '0 2px 10px rgba(0,0,0,0.1)' },
    thead: { backgroundColor: '#f5f5f5' },
    th: { padding: '12px 16px', textAlign: 'left', fontSize: '13px', color: '#666' },
    tr: { borderBottom: '1px solid #eee' },
    td: { padding: '12px 16px', fontSize: '14px' },
    badge: { padding: '4px 8px', borderRadius: '4px', color: 'white', fontSize: '12px' },
    btnModifier: { padding: '6px 12px', backgroundColor: '#F39C12', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '12px', marginRight: '8px' },
    btnSupprimer: { padding: '6px 12px', backgroundColor: '#E74C3C', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '12px' },
    vide: { textAlign: 'center', padding: '40px', backgroundColor: 'white', borderRadius: '8px', color: '#666' }
};