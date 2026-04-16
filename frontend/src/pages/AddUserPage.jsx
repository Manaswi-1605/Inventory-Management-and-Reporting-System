import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import Sidebar from '../components/Sidebar';
import { UserService } from '../services/UserService';
import { ValidationUtils } from '../utilities/ValidationUtils';
import { ROLES } from '../utilities/Constants';
import '../styles/inventory.css';

export default function AddUserPage() {
  const navigate = useNavigate();
  const [form,    setForm]    = useState({ username: '', password: '', email: '', role: 'CUSTOMER' });
  const [errors,  setErrors]  = useState({});
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);

  const set = (f) => (e) => setForm(prev => ({ ...prev, [f]: e.target.value }));

  const validate = () => {
    const errs = {};
    if (!form.username.trim()) errs.username = 'Username is required.';
    if (!form.password || form.password.length < 6) errs.password = 'Password must be at least 6 characters.';
    const emailErr = ValidationUtils.email(form.email);
    if (emailErr) errs.email = emailErr;
    if (!form.role) errs.role = 'Role is required.';
    return errs;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const errs = validate();
    if (Object.keys(errs).length) { setErrors(errs); return; }
    setErrors({}); setLoading(true);
    try {
      await UserService.create(form);
      setSuccess(true);
      setTimeout(() => navigate('/dashboard'), 1400);
    } catch (err) {
      setErrors({ api: err.message || 'Failed to create user.' });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-layout">
      <Sidebar />
      <div className="main-content">
        <Navbar title="Add User" />
        <div className="page-inner fade-up">
          <button className="btn btn-ghost btn-sm" style={{ marginBottom: 20 }} onClick={() => navigate('/dashboard')}>
            ← Back to Dashboard
          </button>

          <h1 className="page-title">Add New User</h1>
          <p className="page-subtitle">Create a new account and assign a role.</p>

          {success && (
            <div style={{
              background: 'rgba(34,197,94,0.1)', border: '1px solid rgba(34,197,94,0.3)',
              color: 'var(--accent-green)', borderRadius: 'var(--radius-sm)',
              padding: '12px 16px', marginBottom: 20, fontSize: '0.88rem',
            }}>
              ✓ User created successfully! Redirecting…
            </div>
          )}

          {errors.api && (
            <div style={{
              background: 'rgba(239,68,68,0.1)', border: '1px solid rgba(239,68,68,0.3)',
              color: 'var(--accent-red)', borderRadius: 'var(--radius-sm)',
              padding: '11px 14px', marginBottom: 18, fontSize: '0.88rem',
            }}>
              {errors.api}
            </div>
          )}

          <form onSubmit={handleSubmit} className="form-card" noValidate>
            <div className="form-grid-2">
              <div className="form-group">
                <label>Username *</label>
                <input value={form.username} onChange={set('username')} placeholder="e.g. jsmith" />
                {errors.username && <div className="error-msg">{errors.username}</div>}
              </div>

              <div className="form-group">
                <label>Email *</label>
                <input type="email" value={form.email} onChange={set('email')} placeholder="user@company.com" />
                {errors.email && <div className="error-msg">{errors.email}</div>}
              </div>

              <div className="form-group">
                <label>Password *</label>
                <input type="password" value={form.password} onChange={set('password')} placeholder="Min. 6 characters" />
                {errors.password && <div className="error-msg">{errors.password}</div>}
              </div>

              <div className="form-group">
                <label>Role *</label>
                <select value={form.role} onChange={set('role')}>
                  {Object.values(ROLES).map(r => <option key={r} value={r}>{r}</option>)}
                </select>
                {errors.role && <div className="error-msg">{errors.role}</div>}
              </div>
            </div>

            {/* Role info hint */}
            <div style={{
              background: 'var(--bg-secondary)', border: '1px solid var(--border)',
              borderRadius: 'var(--radius-sm)', padding: '12px 16px', marginTop: 4,
              fontSize: '0.8rem', color: 'var(--text-secondary)', lineHeight: 1.6,
            }}>
              <strong style={{ color: 'var(--text-primary)' }}>Role permissions:</strong><br />
              <span style={{ color: 'var(--accent-purple)' }}>ADMIN</span> — Full access including user management &nbsp;|&nbsp;
              <span style={{ color: 'var(--accent-primary)' }}>SUPPLIER</span> — Add / manage products &nbsp;|&nbsp;
              <span style={{ color: 'var(--accent-green)' }}>CUSTOMER</span> — View-only access
            </div>

            <div className="form-actions">
              <button type="submit" className="btn btn-primary" disabled={loading}>
                {loading ? 'Creating…' : 'Create User'}
              </button>
              <button type="button" className="btn btn-ghost" onClick={() => navigate('/dashboard')}>
                Cancel
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}