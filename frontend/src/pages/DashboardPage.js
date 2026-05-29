import React, { useEffect, useState } from 'react';
import { PieChart, Pie, Cell, BarChart, Bar, XAxis, YAxis, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import statService from '../services/statService';

const COLORS = ['#FF5733', '#3498DB', '#2ECC71', '#F39C12', '#9B59B6'];

export default function DashboardPage() {
    const [stats, setStats] = useState(null);
    const [alertes, setAlertes] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const charger = async () => {
            try {
                const [statsRes, alertesRes] = await Promise.all([
                    statService.getStats(),
                    statService.getAlertes()
                ]);
                setStats(statsRes.data);
                setAlertes(alertesRes.data);
            } catch (err) {
                console.error(err);
            } finally {
                setLoading(false);
            }
        };
        charger();
    }, []);

    if (loading) return <p style={{ textAlign: 'center', marginTop: '50px' }}>Chargement...</p>;

    // Prépare les données pour le graphique camembert
    const dataCategorie = stats?.depensesParCategorie
        ? Object.entries(stats.depensesParCategorie).map(([nom, valeur]) => ({ nom, valeur }))
        : [];

    // Prépare les données pour le graphique barres
    const dataMois = stats?.depensesParMois
        ? Object.entries(stats.depensesParMois).map(([mois, valeur]) => ({ mois, valeur }))
        : [];

    return (
        <div style={styles.container}>
            <h2 style={styles.title}>Dashboard</h2>

            {/* Alertes */}
            {alertes.length > 0 && (
                <div style={styles.alerteContainer}>
                    {alertes.map((a, i) => (
                        <div key={i} style={styles.alerte}>
                            ⚠️ {a.message}
                        </div>
                    ))}
                </div>
            )}

            {/* Cartes statistiques */}
            <div style={styles.cartes}>
                <div style={{ ...styles.carte, backgroundColor: '#2ECC71' }}>
                    <h3>Revenus</h3>
                    <p style={styles.montant}>{stats?.totalRevenus?.toLocaleString()} Ar</p>
                </div>
                <div style={{ ...styles.carte, backgroundColor: '#E74C3C' }}>
                    <h3>Dépenses</h3>
                    <p style={styles.montant}>{stats?.totalDepenses?.toLocaleString()} Ar</p>
                </div>
                <div style={{ ...styles.carte, backgroundColor: stats?.solde >= 0 ? '#3498DB' : '#E74C3C' }}>
                    <h3>Solde</h3>
                    <p style={styles.montant}>{stats?.solde?.toLocaleString()} Ar</p>
                </div>
            </div>

            {/* Graphiques */}
            <div style={styles.graphiques}>
                {/* Camembert par catégorie */}
                {dataCategorie.length > 0 && (
                    <div style={styles.graphique}>
                        <h3>Dépenses par catégorie</h3>
                        <ResponsiveContainer width="100%" height={300}>
                            <PieChart>
                                <Pie data={dataCategorie} dataKey="valeur" nameKey="nom" cx="50%" cy="50%" outerRadius={100} label>
                                    {dataCategorie.map((_, index) => (
                                        <Cell key={index} fill={COLORS[index % COLORS.length]} />
                                    ))}
                                </Pie>
                                <Tooltip />
                                <Legend />
                            </PieChart>
                        </ResponsiveContainer>
                    </div>
                )}

                {/* Barres par mois */}
                {dataMois.length > 0 && (
                    <div style={styles.graphique}>
                        <h3>Dépenses par mois</h3>
                        <ResponsiveContainer width="100%" height={300}>
                            <BarChart data={dataMois}>
                                <XAxis dataKey="mois" />
                                <YAxis />
                                <Tooltip />
                                <Legend />
                                <Bar dataKey="valeur" fill="#3498DB" name="Dépenses" />
                            </BarChart>
                        </ResponsiveContainer>
                    </div>
                )}
            </div>
        </div>
    );
}

const styles = {
    container: { padding: '24px', maxWidth: '1200px', margin: '0 auto' },
    title: { marginBottom: '24px', color: '#333' },
    alerteContainer: { marginBottom: '24px' },
    alerte: {
        backgroundColor: '#FFF3CD',
        border: '1px solid #FFEAA7',
        padding: '12px 16px',
        borderRadius: '6px',
        marginBottom: '8px',
        color: '#856404'
    },
    cartes: { display: 'flex', gap: '16px', marginBottom: '32px', flexWrap: 'wrap' },
    carte: {
        flex: 1,
        minWidth: '200px',
        padding: '24px',
        borderRadius: '8px',
        color: 'white',
        textAlign: 'center'
    },
    montant: { fontSize: '24px', fontWeight: 'bold', margin: 0 },
    graphiques: { display: 'flex', gap: '24px', flexWrap: 'wrap' },
    graphique: {
        flex: 1,
        minWidth: '300px',
        backgroundColor: 'white',
        padding: '24px',
        borderRadius: '8px',
        boxShadow: '0 2px 10px rgba(0,0,0,0.1)'
    }
};