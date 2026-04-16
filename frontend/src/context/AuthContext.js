import { createContext, useContext, useState, useEffect } from 'react';
import { StorageUtils } from '../utilities/StorageUtils';
import api  from '../utilities/ApiUtils';

const AuthContext = createContext(null);

function normalizeRole(role) {
  if (!role) return '';
  const r = String(role).trim().toUpperCase();
  return r.startsWith('ROLE_') ? r.slice('ROLE_'.length) : r;
}

export function AuthProvider({ children }) {
  const [user,    setUser]    = useState(() => StorageUtils.getUser());
  const [token,   setToken]   = useState(() => StorageUtils.getToken());
  const [loading, setLoading] = useState(false);
  const [ready,   setReady]   = useState(false);

  useEffect(() => {
    if (token) {
      api.get('/auth/me')
        .then((u) => {
          const next = u ? { ...u, role: normalizeRole(u.role) } : null;
          setUser(next);
          if (next) StorageUtils.setUser(next);
        })
        .catch(() => { StorageUtils.clearAll(); setUser(null); setToken(null); })
        .finally(() => setReady(true));
    } else {
      setReady(true);
    }
  }, []);

  const login = async (username, password) => {
    setLoading(true);
    try {
      const data = await api.post('/auth/login', { username, password });
      const nextUser = { username: data.username, role: normalizeRole(data.role) };
      StorageUtils.setToken(data.token);
      StorageUtils.setUser(nextUser);
      setToken(data.token);
      setUser(nextUser);
      return { success: true };
    } catch (err) {
      return { success: false, error: err.message };
    } finally {
      setLoading(false);
    }
  };

  const logout = async () => {
    try { await api.post('/auth/logout', {}); } catch { }
    StorageUtils.clearAll();
    setUser(null);
    setToken(null);
  };

  const hasRole = (required) => {
    if (!user) return false;
    // Backend enum: ADMIN, SUPPLIER, CUSTOMER
    const hierarchy = { ADMIN: 3, SUPPLIER: 2, CUSTOMER: 1 };
    return (hierarchy[normalizeRole(user.role)] || 0) >= (hierarchy[normalizeRole(required)] || 0);
  };

  if (!ready) {
    return (
      <div style={{
        minHeight: '100vh', background: 'var(--bg)',
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        color: 'var(--text2)', fontFamily: 'var(--font)', fontSize: '0.9rem', gap: 12,
      }}>
        <span style={{ animation: 'spin 1s linear infinite', display: 'inline-block' }}>⟳</span>
        Loading…
      </div>
    );
  }

  return (
    <AuthContext.Provider value={{ user, token, loading, login, logout, hasRole, isAuthenticated: !!token }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuthContext = () => useContext(AuthContext);
