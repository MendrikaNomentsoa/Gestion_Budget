import React, { useEffect, useState } from 'react';
import { toast } from 'react-toastify';
import objectifService from '../services/objectifService';

export default function ObjectifsPage() {
    const [objectifs, setObjectifs] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showForm, setShowForm] = useState(false);
    const [objectifAModifier, setObjectifAModifier] = useState(null);
    const [showEpargne, setShowEpargne] = useState(null);
    const [showRetrait, setShowRetrait] = useState(null);
    const [montantEpargne, setMontantEpargne] = useState('');
    const [montantRetrait, setMontantRetrait] = useState('');
    const [form, setForm] = useState({
        nom: '',
        montantCible: '',
        description: '',
        dateEcheance: ''
    });

    useEffect(() => {
        charger();
    }, []);

    const charger = async () => {
        try {
            const res = await objectifService.lister();
            setObjectifs(res.data);
        } catch (err) {
            toast.error('Erreur lors du chargement');
        } finally {
            setLoading(false);
        }
    };

    const handleModifier = (objectif) => {
        setObjectifAModifier(objectif);
        setForm({
            nom: objectif.nom,
            montantCible: objectif.montantCible,
            description: objectif.description || '',
            dateEcheance: objectif.dateEcheance || ''
        });
        setShowForm(true);
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    const handleAnnuler = () => {
        setShowForm(false);
        setObjectifAModifier(null);
        setForm({ nom: '', montantCible: '', description: '', dateEcheance: '' });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const data = {
                ...form,
                montantCible: parseFloat(form.montantCible)
            };

            if (objectifAModifier) {
                await objectifService.modifier(objectifAModifier.id, data);
                toast.success('Objectif modifié avec succès !');
            } else {
                await objectifService.creer(data);
                toast.success('Objectif créé avec succès !');
            }

            handleAnnuler();
            charger();
        } catch (err) {
            toast.error(err.response?.data?.erreur || 'Erreur lors de la sauvegarde');
        }
    };

    const handleAjouterEpargne = async (id) => {
        if (!montantEpargne || parseFloat(montantEpargne) <= 0) {
            toast.error('Montant invalide');
            return;
        }
        try {
            const res = await objectifService.ajouterEpargne(id, parseFloat(montantEpargne));
            if (res.data.statut === 'ATTEINT') {
                toast.success('🎉 Félicitations ! Objectif atteint !');
            } else {
                toast.success(`Épargne ajoutée ! Progression : ${res.data.progression.toFixed(1)}%`);
            }
            setShowEpargne(null);
            setMontantEpargne('');
            charger();
        } catch (err) {
            toast.error(err.response?.data?.erreur || 'Erreur');
        }
    };

    const handleRetrait = async (id) => {
        if (!montantRetrait || parseFloat(montantRetrait) <= 0) {
            toast.error('Montant invalide');
            return;
        }
        try {
            await objectifService.retirerMontant(id, parseFloat(montantRetrait));
            toast.warning('Retrait effectué — continuez à épargner !');
            setShowRetrait(null);
            setMontantRetrait('');
            charger();
        } catch (err) {
            toast.error(err.response?.data?.erreur || 'Erreur');
        }
    };

    const handleSupprimer = async (id) => {
        if (window.confirm('Supprimer cet objectif ?')) {
            try {
                await objectifService.supprimer(id);
                toast.success('Objectif supprimé');
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
                <h2>Objectifs financiers</h2>
                <button style={styles.btnAjouter} onClick={() => {
                    setObjectifAModifier(null);
                    setShowForm(!showForm);
                }}>
                    {showForm ? 'Annuler' : '+ Ajouter'}
                </button>
            </div>

            {/* Formulaire ajout/modification */}
            {showForm && (
                <div style={styles.form}>
                    <h3 style={{ marginBottom: '16px', color: '#333' }}>
                        {objectifAModifier ? "Modifier l'objectif" : 'Nouvel objectif'}
                    </h3>
                    <form onSubmit={handleSubmit}>
                        <div style={styles.formGrid}>
                            <div>
                                <label style={styles.label}>Nom</label>
                                <input
                                    style={styles.input}
                                    type="text"
                                    placeholder="ex: Vacances"
                                    value={form.nom}
                                    onChange={e => setForm({ ...form, nom: e.target.value })}
                                    required
                                />
                            </div>
                            <div>
                                <label style={styles.label}>Montant cible (Ar)</label>
                                <input
                                    style={styles.input}
                                    type="number"
                                    placeholder="ex: 500000"
                                    value={form.montantCible}
                                    onChange={e => setForm({ ...form, montantCible: e.target.value })}
                                    required
                                    min="0"
                                />
                            </div>
                            <div>
                                <label style={styles.label}>Date échéance</label>
                                <input
                                    style={styles.input}
                                    type="date"
                                    value={form.dateEcheance}
                                    onChange={e => setForm({ ...form, dateEcheance: e.target.value })}
                                />
                            </div>
                            <div>
                                <label style={styles.label}>Description</label>
                                <input
                                    style={styles.input}
                                    type="text"
                                    placeholder="ex: Pour les vacances d'été"
                                    value={form.description}
                                    onChange={e => setForm({ ...form, description: e.target.value })}
                                />
                            </div>
                        </div>
                        <div style={{ display: 'flex', gap: '12px' }}>
                            <button style={styles.btnSauvegarder} type="submit">
                                {objectifAModifier ? 'Modifier' : 'Sauvegarder'}
                            </button>
                            <button style={styles.btnAnnuler} type="button" onClick={handleAnnuler}>
                                Annuler
                            </button>
                        </div>
                    </form>
                </div>
            )}

            {/* Liste des objectifs */}
            {objectifs.length === 0 ? (
                <div style={styles.vide}>
                    Aucun objectif — définissez vos buts financiers !
                </div>
            ) : (
                <div style={styles.liste}>
                    {objectifs.map(o => (
                        <div key={o.id} style={{
                            ...styles.objectif,
                            borderLeft: `4px solid ${o.statut === 'ATTEINT' ? '#2ECC71' : '#3498DB'}`
                        }}>
                            {/* En-tête */}
                            <div style={styles.objectifHeader}>
                                <div>
                                    <h3 style={styles.objectifTitre}>
                                        {o.statut === 'ATTEINT' ? '✅ ' : '🎯 '}{o.nom}
                                    </h3>
                                    {o.description && (
                                        <p style={styles.objectifDesc}>{o.description}</p>
                                    )}
                                    {o.dateEcheance && (
                                        <p style={styles.objectifDate}>
                                            Échéance : {o.dateEcheance}
                                        </p>
                                    )}
                                </div>
                                <div style={styles.objectifActions}>
                                    {/* Bouton épargner — visible seulement si EN_COURS */}
                                    {o.statut !== 'ATTEINT' && (
                                        <button
                                            style={styles.btnEpargne}
                                            onClick={() => {
                                                setShowEpargne(showEpargne === o.id ? null : o.id);
                                                setShowRetrait(null);
                                            }}
                                        >
                                            + Épargner
                                        </button>
                                    )}
                                    {/* Bouton retirer — visible si montant > 0 */}
                                    {o.montantActuel > 0 && (
                                        <button
                                            style={styles.btnRetrait}
                                            onClick={() => {
                                                setShowRetrait(showRetrait === o.id ? null : o.id);
                                                setShowEpargne(null);
                                            }}
                                        >
                                            - Retirer
                                        </button>
                                    )}
                                    <button
                                        style={styles.btnModifier}
                                        onClick={() => handleModifier(o)}
                                    >
                                        Modifier
                                    </button>
                                    <button
                                        style={styles.btnSupprimer}
                                        onClick={() => handleSupprimer(o.id)}
                                    >
                                        Supprimer
                                    </button>
                                </div>
                            </div>

                            {/* Formulaire épargne */}
                            {showEpargne === o.id && (
                                <div style={styles.epargneForm}>
                                    <input
                                        style={{ ...styles.input, width: '200px' }}
                                        type="number"
                                        placeholder="Montant à épargner"
                                        value={montantEpargne}
                                        onChange={e => setMontantEpargne(e.target.value)}
                                        min="0"
                                    />
                                    <button
                                        style={styles.btnSauvegarder}
                                        onClick={() => handleAjouterEpargne(o.id)}
                                    >
                                        Confirmer
                                    </button>
                                    <button
                                        style={styles.btnAnnuler}
                                        onClick={() => {
                                            setShowEpargne(null);
                                            setMontantEpargne('');
                                        }}
                                    >
                                        Annuler
                                    </button>
                                </div>
                            )}

                            {/* Formulaire retrait */}
                            {showRetrait === o.id && (
                                <div style={{ ...styles.epargneForm, backgroundColor: '#fff3f3' }}>
                                    <input
                                        style={{ ...styles.input, width: '200px' }}
                                        type="number"
                                        placeholder="Montant à retirer"
                                        value={montantRetrait}
                                        onChange={e => setMontantRetrait(e.target.value)}
                                        min="0"
                                    />
                                    <button
                                        style={styles.btnSauvegarder}
                                        onClick={() => handleRetrait(o.id)}
                                    >
                                        Confirmer
                                    </button>
                                    <button
                                        style={styles.btnAnnuler}
                                        onClick={() => {
                                            setShowRetrait(null);
                                            setMontantRetrait('');
                                        }}
                                    >
                                        Annuler
                                    </button>
                                </div>
                            )}

                            {/* Barre de progression */}
                            <div style={styles.progressContainer}>
                                <div style={styles.progressInfo}>
                                    <span style={styles.progressMontant}>
                                        {o.montantActuel.toLocaleString()} Ar
                                    </span>
                                    <span style={styles.progressPourcent}>
                                        {o.progression.toFixed(1)}%
                                    </span>
                                    <span style={styles.progressMontant}>
                                        {o.montantCible.toLocaleString()} Ar
                                    </span>
                                </div>
                                <div style={styles.progressBar}>
                                    <div style={{
                                        ...styles.progressFill,
                                        width: `${o.progression}%`,
                                        backgroundColor: o.statut === 'ATTEINT' ? '#2ECC71' : '#3498DB'
                                    }} />
                                </div>
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
    formGrid: { display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: '12px', marginBottom: '16px' },
    label: { display: 'block', fontSize: '12px', color: '#666', marginBottom: '4px' },
    input: { width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #ddd', fontSize: '14px', boxSizing: 'border-box' },
    btnSauvegarder: { padding: '10px 24px', backgroundColor: '#2196F3', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer' },
    btnAnnuler: { padding: '10px 24px', backgroundColor: '#95a5a6', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer' },
    liste: { display: 'flex', flexDirection: 'column', gap: '16px' },
    objectif: { backgroundColor: 'white', padding: '20px', borderRadius: '8px', boxShadow: '0 2px 10px rgba(0,0,0,0.1)' },
    objectifHeader: { display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '16px' },
    objectifTitre: { margin: '0 0 4px 0', fontSize: '18px', color: '#333' },
    objectifDesc: { margin: '0 0 4px 0', fontSize: '13px', color: '#666' },
    objectifDate: { margin: 0, fontSize: '12px', color: '#999' },
    objectifActions: { display: 'flex', gap: '8px', flexShrink: 0, flexWrap: 'wrap', justifyContent: 'flex-end' },
    epargneForm: { display: 'flex', gap: '12px', alignItems: 'center', marginBottom: '16px', padding: '12px', backgroundColor: '#f0fff4', borderRadius: '6px' },
    progressContainer: { marginTop: '8px' },
    progressInfo: { display: 'flex', justifyContent: 'space-between', marginBottom: '6px', fontSize: '13px', color: '#666' },
    progressMontant: { fontWeight: '500' },
    progressPourcent: { fontWeight: 'bold', color: '#333' },
    progressBar: { width: '100%', height: '12px', backgroundColor: '#ecf0f1', borderRadius: '6px', overflow: 'hidden' },
    progressFill: { height: '100%', borderRadius: '6px', transition: 'width 0.5s ease' },
    btnEpargne: { padding: '6px 12px', backgroundColor: '#2ECC71', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '12px' },
    btnRetrait: { padding: '6px 12px', backgroundColor: '#E67E22', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '12px' },
    btnModifier: { padding: '6px 12px', backgroundColor: '#F39C12', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '12px' },
    btnSupprimer: { padding: '6px 12px', backgroundColor: '#E74C3C', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '12px' },
    vide: { textAlign: 'center', padding: '40px', backgroundColor: 'white', borderRadius: '8px', color: '#666' }
};