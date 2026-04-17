import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import Sidebar from '../components/Sidebar';
import ProductForm from '../components/ProductForm';
import { useProducts } from '../hooks/useProducts';
import { useState } from 'react';
import { useAuth } from '../hooks/useAuth';
import { StorageUtils } from '../utilities/StorageUtils';
import '../styles/inventory.css';

function decodeJwtPayload(token) {
  try {
    const part = token.split('.')[1];
    if (!part) return null;
    const base64 = part.replace(/-/g, '+').replace(/_/g, '/');
    const padded = base64 + '='.repeat((4 - (base64.length % 4)) % 4);
    const json = atob(padded);
    return JSON.parse(json);
  } catch {
    return null;
  }
}

export default function AddProductPage() {
  const navigate             = useNavigate();
  const { addProduct }       = useProducts();
  const { user }             = useAuth();
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);

  const handleSubmit = async (data) => {
    setLoading(true);
    try {
      await addProduct(data);
      setSuccess(true);
      setTimeout(() => navigate('/products'), 1200);
    } catch (e) {
      const msg =
        e?.response?.data?.error ||
        e?.response?.data?.message ||
        (typeof e?.response?.data === 'string' ? e.response.data : '') ||
        e.message;
      const status = e?.response?.status;
      const tokenPresent = !!StorageUtils.getToken();
      const role = user?.role || 'UNKNOWN';
      const token = StorageUtils.getToken();
      const jwt = token ? decodeJwtPayload(token) : null;
      const extra = status === 403
        ? `\n\nNot authorized.\n- Current role (UI): ${role}\n- Token present: ${tokenPresent ? 'yes' : 'no'}\n- Token payload: ${jwt ? JSON.stringify(jwt, null, 2) : 'unreadable'}\n\nIf backend uses hasRole('ADMIN'), it usually expects authority 'ROLE_ADMIN' in the token/authentication.`
        : '';
      alert(`Failed to add product: ${msg}${status ? ` (HTTP ${status})` : ''}${extra}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-layout">
      <Sidebar />
      <div className="main-content">
        <Navbar title="Add Product" />
        <div className="page-inner fade-up">
          <button
            className="btn btn-ghost btn-sm"
            style={{ marginBottom: 20 }}
            onClick={() => navigate('/products')}
          >
            ← Back to Products
          </button>

          <h1 className="page-title">Add New Product</h1>
          <p className="page-subtitle">Fill in the details below to add a product to your inventory.</p>

          {success && (
            <div style={{
              background: 'rgba(34,197,94,0.1)', border: '1px solid rgba(34,197,94,0.3)',
              color: 'var(--accent-green)', borderRadius: 'var(--radius-sm)',
              padding: '12px 16px', marginBottom: 20, fontSize: '0.88rem',
            }}>
              ✓ Product added successfully! Redirecting…
            </div>
          )}

          <ProductForm
            onSubmit={handleSubmit}
            onCancel={() => navigate('/products')}
            loading={loading}
          />
        </div>
      </div>
    </div>
  );
}
