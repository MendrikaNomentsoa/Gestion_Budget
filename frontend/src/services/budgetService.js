import api from './api';

const budgetService = {
    lister: () => api.get('/budgets'),
    creer: (data) => api.post('/budgets', data),
    modifier: (id, data) => api.put(`/budgets/${id}`, data),
    supprimer: (id) => api.delete(`/budgets/${id}`)
};

export default budgetService;