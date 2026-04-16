import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { useTheme } from '../context/ThemeContext';
import { ValidationUtils } from '../utilities/ValidationUtils';
import '../styles/login.css';

export default function LoginPage() {
  const navigate = useNavigate();
  const { login, loading } = useAuth();
  const { theme, toggle }  = useTheme();

  const [form,   setForm]   = useState({ username: '', password: '' });
  const [errors, setErrors] = useState({});
  const [apiErr, setApiErr] = useState('');

  const set = (f) => (e) => setForm(p => ({ ...p, [f]: e.target.value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    const errs = ValidationUtils.validateLogin(form);
    if (Object.keys(errs).length) { setErrors(errs); return; }
    setErrors({}); setApiErr('');
    const result = await login(form.username, form.password);
    if (result.success) navigate('/dashboard');
    else setApiErr('Invalid username or password. Please try again.');
  };

  return (
    <div className="login-root">

      {/* Theme toggle */}
      <button className="login-theme-toggle" onClick={toggle} title="Toggle theme">
        {theme === 'dark' ? '☀' : '☾'}
      </button>

      {/* Left branding */}
      <div className="login-brand">
        <div className="login-brand-logo">
          <div className="login-brand-dot" />
          InvenTrack
        </div>
        <h1>
          Smart <em>Inventory</em><br />
          Management
        </h1>
        <p>
          Monitor stock levels, generate insightful reports, and keep your
          supply chain running smoothly — all from one dashboard.
        </p>
        <div className="login-brand-stats">
          <div>
            <div className="login-stat-value">Real‑time</div>
            <div className="login-stat-label">Monitoring</div>
          </div>
          <div>
            <div className="login-stat-value">Auto</div>
            <div className="login-stat-label">Alerts</div>
          </div>
          <div>
            <div className="login-stat-value">Full</div>
            <div className="login-stat-label">Reports</div>
          </div>
        </div>
      </div>

      {/* Right form */}
      <div className="login-form-panel" style={{ animation: 'fadeUp 0.5s ease both' }}>
        <h2>Welcome back</h2>
        <p className="sub">Sign in to your account to continue.</p>

        {apiErr && (
          <div className="login-error">
            <span>⚠</span> {apiErr}
          </div>
        )}

        <form onSubmit={handleSubmit} noValidate>
          <div className="form-group">
            <label>Username</label>
            <input
              type="text"
              value={form.username}
              onChange={set('username')}
              placeholder="Enter your username"
              autoFocus
            />
            {errors.username && <div className="error-msg">{errors.username}</div>}
          </div>

          <div className="form-group">
            <label>Password</label>
            <input
              type="password"
              value={form.password}
              onChange={set('password')}
              placeholder="••••••••"
            />
            {errors.password && <div className="error-msg">{errors.password}</div>}
          </div>

          <button type="submit" className="login-btn" disabled={loading}>
            {loading ? 'Signing in…' : 'Sign In →'}
          </button>
        </form>

        <div className="login-links">
          <button type="button" className="login-link" onClick={() => navigate('/register')}>
            Create an account
          </button>
        </div>
      </div>
    </div>
  );
}
