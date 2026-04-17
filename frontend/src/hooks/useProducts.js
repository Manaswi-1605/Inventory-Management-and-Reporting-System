import { useCallback, useEffect, useMemo, useState } from 'react';
import api from '../utilities/ApiUtils';

function normalizeProduct(raw = {}) {
  return {
    id: raw.id,
    name: raw.name ?? '',
    sku: raw.sku ?? '',
    description: raw.description ?? '',
    quantity: Number(raw.quantity ?? 0),
    reorderLevel: Number(raw.reorderLevel ?? 0),
    unitPrice: Number(raw.unitPrice ?? raw.price ?? 0),
    category: raw.category?.name ?? raw.categoryName ?? raw.category ?? '',
    supplier: raw.supplier?.name ?? raw.supplierName ?? raw.supplier ?? '',
    categoryId: raw.category?.id ?? raw.categoryId ?? '',
    supplierId: raw.supplier?.id ?? raw.supplierId ?? '',
    active: raw.active ?? true,
  };
}

function toApiPayload(data = {}, id) {
  return {
    ...(id ? { id } : {}),
    name: data.name,
    sku: data.sku,
    description: data.description,
    price: Number(data.unitPrice),
    quantity: Number(data.quantity),
    reorderLevel: Number(data.reorderLevel),
    category: { id: Number(data.categoryId) },
    supplier: { id: Number(data.supplierId) },
  };
}

export function useProducts() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const fetchProducts = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const response = await api.get('/api/products');
      const list = Array.isArray(response) ? response : [];
      setProducts(list.map(normalizeProduct));
    } catch (e) {
      setError(e?.response?.data?.message || e.message || 'Failed to fetch products');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchProducts();
  }, [fetchProducts]);

  const addProduct = async (data) => {
    await api.post('/api/product', toApiPayload(data));
    await fetchProducts();
  };

  const editProduct = async (id, data) => {
    await api.post('/api/product', toApiPayload(data, id));
    await fetchProducts();
  };

  const deleteProduct = async (id) => {
    await api.delete(`/api/product/${id}`);
    await fetchProducts();
  };

  const lowStockProducts = useMemo(
    () => products.filter((p) => p.quantity <= p.reorderLevel),
    [products]
  );

  return {
    products,
    loading,
    error,
    fetchProducts,
    addProduct,
    editProduct,
    deleteProduct,
    lowStockProducts,
  };
}
