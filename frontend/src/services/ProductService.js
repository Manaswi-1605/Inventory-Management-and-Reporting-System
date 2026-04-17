import { api } from '../utilities/ApiUtils';
 
export const ProductService = {
  getAll:        ()          => api.get('/api/products'),
  getById:       (id)        => api.get(`/api/products/${id}`),
  search:        (q)         => api.get(`/api/products/search?q=${encodeURIComponent(q)}`),
  getByCategory: (cat)       => api.get(`/api/products/category/${encodeURIComponent(cat)}`),
  getLowStock:   ()          => api.get('/api/products/low-stock'),
  create:        (product)   => api.post('/api/product', product),
  update:        (id, data)  => api.put(`/api/products/${id}`, data),
  delete:        (id)        => api.delete(`/api/products/${id}`),
  restock:       (id, qty)   => api.put(`/api/products/${id}/restock`, { quantity: qty }),
};