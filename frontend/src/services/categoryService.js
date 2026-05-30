import api from './api';

const categoryService = {
    lister: () => api.get('/categories'),
    creer: (data) => api.post('/categories', data),
    modifier: (id, data) => api.put(`/categories/${id}`, data),
    supprimer: (id) => api.delete(`/categories/${id}`)
};

export default categoryService;
