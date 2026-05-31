import api from './api';

const objectifService = {
    lister: () => api.get('/objectifs'),
    creer: (data) => api.post('/objectifs', data),
    modifier: (id, data) => api.put(`/objectifs/${id}`, data),
    supprimer: (id) => api.delete(`/objectifs/${id}`),
    ajouterEpargne: (id, montant) => api.post(`/objectifs/${id}/epargne`, { montant }),
    retirerMontant: (id, montant) => api.post(`/objectifs/${id}/retrait`, { montant })
};

export default objectifService;