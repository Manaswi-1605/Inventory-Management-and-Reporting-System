import { NavLink } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

const NAV = [
  { to: '/dashboard',   icon: '⊞',  label: 'Dashboard',   role: null },
  { to: '/products',    icon: '📦', label: 'Products',    role: null },
  { to: '/reports',     icon: '📊', label: 'Reports',     role: null },
  { to: '/add-product', icon: '＋', label: 'Add Product', role: 'SUPPLIER' },
  // { to: '/add-user',    icon: '👤', label: 'Add User',    role: 'ADMIN' },
];

export default function Sidebar({ lowStockCount = 0 }) {
  const { user, hasRole, logout } = useAuth();

  return (
    <aside style={{
      position: 'fixed', top: 0, left: 0,
      width: 'var(--sidebar-w)',
      height: '100vh',
      background: 'var(--bg2)',
      borderRight: '1px solid var(--border)',
      display: 'flex', flexDirection: 'column',
      zIndex: 200,
      transition: 'background 0.3s',
    }}>

      {/* Logo */}
      <div style={{
        padding: '20px 20px',
        borderBottom: '1px solid var(--border)',
        display: 'flex', alignItems: 'center', gap: 10,
      }}>
        <div style={{
          width: 36, height: 36, borderRadius: 10,
          background: 'linear-gradient(135deg, var(--accent), var(--purple))',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          fontSize: '1.1rem', flexShrink: 0,
          boxShadow: '0 4px 12px var(--accent-glow)',
        }}>📦</div>
        <div>
          <div style={{ fontWeight: 800, fontSize: '1rem', letterSpacing: '-0.03em' }}>InvenTrack</div>
          <div style={{ fontSize: '0.68rem', color: 'var(--text2)' }}>Management System</div>
        </div>
      </div>

      {/* Nav */}
      <nav style={{ flex: 1, padding: '14px 12px', display: 'flex', flexDirection: 'column', gap: 3, overflowY: 'auto' }}>

        <div style={{ fontSize: '0.68rem', fontWeight: 600, color: 'var(--text3)', textTransform: 'uppercase', letterSpacing: '0.1em', padding: '6px 10px', marginBottom: 4 }}>
          Navigation
        </div>

        {NAV.map(({ to, icon, label, role }) => {
          if (role && !hasRole(role)) return null;
          const showBadge = to === '/reports' && lowStockCount > 0;
          return (
            <NavLink key={to} to={to} style={({ isActive }) => ({
              display: 'flex', alignItems: 'center', gap: 10,
              padding: '10px 12px', borderRadius: 'var(--r)',
              fontSize: '0.88rem', fontWeight: 500,
              textDecoration: 'none',
              color: isActive ? '#fff' : 'var(--text2)',
              background: isActive
                ? 'linear-gradient(135deg, var(--accent), var(--accent-h))'
                : 'transparent',
              boxShadow: isActive ? '0 4px 12px var(--accent-glow)' : 'none',
              transition: 'all 0.2s',
            })}>
              <span style={{ fontSize: '1rem', width: 20, textAlign: 'center', flexShrink: 0 }}>{icon}</span>
              <span style={{ flex: 1 }}>{label}</span>
              {showBadge && (
                <span style={{
                  background: 'var(--amber)',
                  color: '#fff',
                  borderRadius: 20,
                  padding: '1px 7px',
                  fontSize: '0.68rem',
                  fontWeight: 700,
                }}>{lowStockCount}</span>
              )}
            </NavLink>
          );
        })}
      </nav>

      {/* Bottom user strip */}
      <div style={{
        padding: '14px 16px',
        borderTop: '1px solid var(--border)',
        display: 'flex', alignItems: 'center', gap: 10,
      }}>
        <div style={{
          width: 34, height: 34, borderRadius: '50%',
          background: 'linear-gradient(135deg, var(--accent), var(--purple))',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          fontWeight: 700, fontSize: '0.85rem', color: '#fff', flexShrink: 0,
        }}>
          {user?.username?.[0]?.toUpperCase()}
        </div>
        <div style={{ flex: 1, minWidth: 0 }}>
          <div style={{ fontSize: '0.82rem', fontWeight: 600, whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
            {user?.username}
          </div>
          <div style={{ fontSize: '0.7rem', color: 'var(--text2)' }}>{user?.role === "CUSTOMER" ? "STAFF" : user?.role}</div>
        </div>
        <button
          onClick={logout}
          title="Sign out"
          style={{
            background: 'none', border: 'none',
            color: 'var(--text3)', fontSize: '1rem',
            cursor: 'pointer', padding: 4,
            transition: 'color 0.15s',
            borderRadius: 'var(--r)',
            width: 32, height: 32,
            display: 'flex', alignItems: 'center', justifyContent: 'center',
          }}
        >⏻</button>
      </div>
    </aside>
  );
}
