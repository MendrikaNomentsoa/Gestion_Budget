import api from './api';

const transactionService = {
    lister: () => api.get('/transactions'),
    creer: (data) => api.post('/transactions', data),
    modifier: (id, data) => api.put(`/transactions/${id}`, data),
    supprimer: (id) => api.delete(`/transactions/${id}`)
};

export default transactionService;