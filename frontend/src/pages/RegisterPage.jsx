import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTheme } from '../context/ThemeContext';
import api from '../utilities/ApiUtils';
import { ROLES } from '../utilities/Constants';
import { ValidationUtils } from '../utilities/ValidationUtils';
import '../styles/login.css';

export default function RegisterPage() {
  const navigate = useNavigate();
  const { theme, toggle } = useTheme();

  const [form, setForm] = useState({ username: '', email: '', password: '', role: 'STAFF' });
  const [errors, setErrors] = useState({});
  const [apiErr, setApiErr] = useState('');
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);

  const set = (f) => (e) => setForm((p) => ({ ...p, [f]: e.target.value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    const errs = ValidationUtils.validateRegister(form);
    if (Object.keys(errs).length) {
      setErrors(errs);
      return;
    }
    setErrors({});
    setApiErr('');
    setLoading(true);
    try {
      await api.post('/auth/register', {
        username: form.username,
        password: form.password,
        email: form.email,
        role: String(form.role).toUpperCase(),
      });
      setSuccess(true);
      setTimeout(() => navigate('/login'), 900);
    } catch (err) {
      const msg =
        err?.response?.data?.error ||
        err?.response?.data?.message ||
        err?.message ||
        'Registration failed.';
      setApiErr(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-root">
      <button className="login-theme-toggle" onClick={toggle} title="Toggle theme">
        {theme === 'dark' ? '☀' : '☾'}
      </button>

      <div className="login-brand">
        <div className="login-brand-logo">
          <div className="login-brand-dot" />
          InvenTrack
        </div>
        <h1>
          Create your <em>account</em>
        </h1>
        <p>
          Register a new user profile to access inventory, products, and reports.
        </p>
        <div className="login-brand-stats">
          <div>
            <div className="login-stat-value">Admin</div>
            <div className="login-stat-label">User mgmt</div>
          </div>
          <div>
            <div className="login-stat-value">Supplier</div>
            <div className="login-stat-label">Add products</div>
          </div>
          <div>
            <div className="login-stat-value">Customer</div>
            <div className="login-stat-label">View access</div>
          </div>
        </div>
      </div>

      <div className="login-form-panel" style={{ animation: 'fadeUp 0.5s ease both' }}>
        <h2>Create account</h2>
        <p className="sub">Fill the details below to register.</p>

        {apiErr && (
          <div className="login-error">
            <span>⚠</span> {apiErr}
          </div>
        )}

        {success && (
          <div
            className="login-success"
            style={{
              background: 'rgba(34,197,94,0.1)',
              border: '1px solid rgba(34,197,94,0.3)',
              color: 'var(--green)',
              borderRadius: 'var(--r)',
              padding: '12px 14px',
              fontSize: '0.85rem',
              marginBottom: 20,
            }}
          >
            ✓ Registered successfully. Redirecting to login…
          </div>
        )}

        <form onSubmit={handleSubmit} noValidate>
          <div className="form-group">
            <label>Username</label>
            <input type="text" value={form.username} onChange={set('username')} placeholder="Choose a username" autoFocus />
            {errors.username && <div className="error-msg">{errors.username}</div>}
          </div>

          <div className="form-group">
            <label>Email</label>
            <input type="email" value={form.email} onChange={set('email')} placeholder="you@company.com" />
            {errors.email && <div className="error-msg">{errors.email}</div>}
          </div>

          <div className="form-group">
            <label>Password</label>
            <input type="password" value={form.password} onChange={set('password')} placeholder="Min. 6 characters" />
            {errors.password && <div className="error-msg">{errors.password}</div>}
          </div>

          <div className="form-group">
            <label>Role</label>
            <select value={form.role} onChange={set('role')}>
              {/* {Object.values(ROLES).map((r) => (
                <option key={r} value={r}>
                  {r}
                </option>
              ))} */}
              {Object.values(ROLES).map((r) => (
                <option key={r} value={r}>
                  {r === "CUSTOMER" ? "STAFF" : r}
                </option>
              ))}
            </select>
            {errors.role && <div className="error-msg">{errors.role}</div>}
          </div>

          <button type="submit" className="login-btn" disabled={loading || success}>
            {loading ? 'Creating…' : 'Create Account →'}
          </button>
        </form>

        <div className="login-links">
          <button type="button" className="login-link" onClick={() => navigate('/login')}>
            ← Back to sign in
          </button>
        </div>
      </div>
    </div>
  );
}

