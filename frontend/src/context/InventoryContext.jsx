import { createContext, useContext, useState, useCallback } from 'react';
import api from '../utilities/ApiUtils';

const InventoryContext = createContext(null);

export function InventoryProvider({ children }) {
  const [products, setProducts] = useState([]);
  const [loading,  setLoading]  = useState(false);
  const [error,    setError]    = useState(null);

  const fetchProducts = useCallback(async () => {
    setLoading(true); setError(null);
    try {
      const data = await api.get('/api/products');
      setProducts(data);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  }, []);

  const addProduct = async (product) => {
    const data = await api.post('/api/products', product);
    setProducts(prev => [data, ...prev]);
    return data;
  };

  const updateProduct = async (id, product) => {
    const data = await api.put(`/api/products/${id}`, product);
    setProducts(prev => prev.map(p => p.id === id ? data : p));
    return data;
  };

  const deleteProduct = async (id) => {
    await api.delete(`/api/products/${id}`);
    setProducts(prev => prev.filter(p => p.id !== id));
  };

  const getLowStock = () => products.filter(p => p.quantity <= p.reorderLevel);

  return (
    <InventoryContext.Provider value={{
      products, loading, error,
      fetchProducts, addProduct, updateProduct, deleteProduct, getLowStock,
    }}>
      {children}
    </InventoryContext.Provider>
  );
}

export const useInventoryContext = () => useContext(InventoryContext);
