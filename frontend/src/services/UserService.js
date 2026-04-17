import api from '../utilities/ApiUtils';
 
export const UserService = {
  getAll:   ()             => api.get('/api/users'),
  getById:  (id)           => api.get(`/api/users/${id}`),
  create:   (user)         => api.post('/api/users', user),
  update:   (id, data)     => api.put(`/api/users/${id}`, data),
  delete:   (id)           => api.delete(`/api/users/${id}`),
  changePassword: (data)   => api.put('/api/users/password', data),
};