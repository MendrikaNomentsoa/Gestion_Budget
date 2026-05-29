import api from './api';

const statService = {
    getStats: () => api.get('/stats'),
    getAlertes: () => api.get('/alertes')
};

export default statService;