import { useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import Navbar from '../components/Navbar';
import Sidebar from '../components/Sidebar';
import ProductTable from '../components/ProductTable';
import ProductForm from '../components/ProductForm';
import { useProducts } from '../hooks/useProducts';
import { useAuth } from '../hooks/useAuth';
import '../styles/inventory.css';

export default function ProductsPage() {
  const { products, loading, error, editProduct, deleteProduct } = useProducts();
  const { hasRole } = useAuth();

  const [query, setQuery] = useState('');
  const [editing, setEditing] = useState(null);
  const [saving, setSaving] = useState(false);

  const filtered = useMemo(() => {
    const q = query.trim().toLowerCase();
    if (!q) return products;
    return products.filter((p) =>
      [p.name, p.sku, p.category, p.supplier].some((v) =>
        String(v || '').toLowerCase().includes(q)
      )
    );
  }, [products, query]);

  const canEdit = hasRole('SUPPLIER');
  const canDelete = hasRole('ADMIN');

  const handleSave = async (data) => {
    if (!editing?.id) return;
    setSaving(true);
    try {
      await editProduct(editing.id, data);
      setEditing(null);
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (product) => {
    if (!product?.id) return;
    const confirmed = window.confirm(`Delete "${product.name}"?`);
    if (!confirmed) return;
    await deleteProduct(product.id);
  };

  return (
    <div className="page-layout">
      <Sidebar />
      <div className="main-content">
        <Navbar title="Products" />
        <div className="page-inner fade-up">
          <h1 className="page-title">Products</h1>
          <p className="page-subtitle">Manage your product catalog, stock and pricing.</p>

          <div className="inventory-toolbar">
            <div className="search-box">
              <span className="search-icon">🔎</span>
              <input
                type="text"
                placeholder="Search by name, SKU, category, supplier..."
                value={query}
                onChange={(e) => setQuery(e.target.value)}
              />
            </div>
            {hasRole('SUPPLIER') && (
              <Link to="/add-product" className="btn btn-primary">
                + Add Product
              </Link>
            )}
          </div>

          {error && (
            <div className="empty-state" style={{ padding: '20px 16px', marginBottom: 16 }}>
              <h3>Unable to load products</h3>
              <p>{error}</p>
            </div>
          )}

          <ProductTable
            products={filtered}
            loading={loading}
            onEdit={setEditing}
            onDelete={handleDelete}
            canEdit={canEdit}
            canDelete={canDelete}
          />

          {editing && (
            <div style={{ marginTop: 24 }}>
              <h2 style={{ marginBottom: 12 }}>Edit Product</h2>
              <ProductForm
                initial={editing}
                onSubmit={handleSave}
                onCancel={() => setEditing(null)}
                loading={saving}
              />
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
