import { useState } from 'react';
import { useAuth } from '../hooks/useAuth';
import { useTheme } from '../context/ThemeContext';

export default function Navbar({ title = 'Dashboard', lowStockCount = 0 }) {
  const { user, logout }    = useAuth();
  const { theme, toggle }   = useTheme();
  const [showNotif, setShowNotif] = useState(false);

  return (
    <nav style={{
      position: 'fixed', top: 0,
      left: 'var(--sidebar-w)', right: 0,
      height: 'var(--nav-h)', zIndex: 100,
      background: 'rgba(12,14,20,0.8)',
      backdropFilter: 'blur(16px)',
      borderBottom: '1px solid var(--border)',
      display: 'flex', alignItems: 'center',
      padding: '0 36px', justifyContent: 'space-between',
      transition: 'background 0.3s',
    }}>
      {/* Page title */}
      <span style={{ fontWeight: 700, fontSize: '1.05rem', letterSpacing: '-0.02em' }}>
        {title}
      </span>

      <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>

        {/* Theme toggle */}
        <button
          onClick={toggle}
          title={`Switch to ${theme === 'dark' ? 'light' : 'dark'} mode`}
          style={{
            width: 38, height: 38,
            borderRadius: 'var(--r)',
            background: 'var(--bg3)',
            border: '1px solid var(--border2)',
            color: 'var(--text2)',
            fontSize: '1rem',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
          }}
        >
          {theme === 'dark' ? '☀' : '☾'}
        </button>

        {/* Notification bell */}
        <div style={{ position: 'relative' }}>
          <button
            onClick={() => setShowNotif(v => !v)}
            style={{
              width: 38, height: 38,
              borderRadius: 'var(--r)',
              background: lowStockCount > 0 ? 'var(--amber-bg)' : 'var(--bg3)',
              border: `1px solid ${lowStockCount > 0 ? 'rgba(245,158,11,0.3)' : 'var(--border2)'}`,
              color: lowStockCount > 0 ? 'var(--amber)' : 'var(--text2)',
              fontSize: '1rem',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              position: 'relative',
            }}
          >
            🔔
            {lowStockCount > 0 && (
              <span style={{
                position: 'absolute', top: -4, right: -4,
                background: 'var(--red)',
                color: '#fff',
                borderRadius: '50%',
                width: 18, height: 18,
                fontSize: '0.65rem',
                fontWeight: 700,
                display: 'flex', alignItems: 'center', justifyContent: 'center',
                border: '2px solid var(--bg)',
              }}>{lowStockCount > 9 ? '9+' : lowStockCount}</span>
            )}
          </button>

          {/* Dropdown */}
          {showNotif && (
            <div style={{
              position: 'absolute', top: 46, right: 0,
              width: 300,
              background: 'var(--bg2)',
              border: '1px solid var(--border2)',
              borderRadius: 'var(--r-lg)',
              boxShadow: 'var(--shadow)',
              zIndex: 200,
              overflow: 'hidden',
            }}>
              <div style={{
                padding: '14px 16px',
                borderBottom: '1px solid var(--border)',
                fontWeight: 600, fontSize: '0.88rem',
                display: 'flex', justifyContent: 'space-between',
              }}>
                Notifications
                {lowStockCount > 0 && (
                  <span style={{
                    background: 'var(--amber-bg)', color: 'var(--amber)',
                    borderRadius: 20, padding: '2px 8px', fontSize: '0.72rem', fontWeight: 600,
                  }}>
                    {lowStockCount} alerts
                  </span>
                )}
              </div>

              {lowStockCount > 0 ? (
                <div style={{ padding: '12px 16px', display: 'flex', gap: 10, alignItems: 'flex-start' }}>
                  <span style={{ fontSize: '1.2rem' }}>⚠</span>
                  <div>
                    <div style={{ fontSize: '0.85rem', fontWeight: 600, marginBottom: 3 }}>
                      Low Stock Alert
                    </div>
                    <div style={{ fontSize: '0.8rem', color: 'var(--text2)' }}>
                      {lowStockCount} product{lowStockCount > 1 ? 's are' : ' is'} below reorder level.
                      Check Reports for details.
                    </div>
                  </div>
                </div>
              ) : (
                <div style={{ padding: '24px 16px', textAlign: 'center', color: 'var(--text2)', fontSize: '0.85rem' }}>
                  ✓ All caught up!
                </div>
              )}
            </div>
          )}
        </div>

        {/* User badge */}
        <div style={{
          display: 'flex', alignItems: 'center', gap: 10,
          background: 'var(--bg3)',
          border: '1px solid var(--border2)',
          borderRadius: 'var(--r)',
          padding: '6px 12px 6px 6px',
        }}>
          <div style={{
            width: 28, height: 28, borderRadius: '50%',
            background: 'linear-gradient(135deg, var(--accent), var(--purple))',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            fontSize: '0.75rem', fontWeight: 700, color: '#fff',
            flexShrink: 0,
          }}>
            {user?.username?.[0]?.toUpperCase() || 'U'}
          </div>
          <div>
            <div style={{ fontSize: '0.82rem', fontWeight: 600, lineHeight: 1.2 }}>{user?.username}</div>
            <div style={{ fontSize: '0.68rem', color: 'var(--text2)', lineHeight: 1 }}>{user?.role === "CUSTOMER" ? "STAFF" : user?.role}</div>
          </div>
        </div>

        <button
          onClick={logout}
          className="btn btn-ghost btn-sm"
        >
          Sign out
        </button>
      </div>
    </nav>
  );
}
