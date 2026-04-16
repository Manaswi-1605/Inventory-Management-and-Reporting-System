import { useEffect, useRef } from 'react';

export default function SummaryCard({ label, value, icon, color = 'accent', trend, delay = 0 }) {
  const colors = {
    accent: { bg: 'var(--accent-glow)', color: 'var(--accent)', border: 'rgba(99,102,241,0.2)' },
    green:  { bg: 'var(--green-bg)',   color: 'var(--green)',  border: 'rgba(16,185,129,0.2)' },
    amber:  { bg: 'var(--amber-bg)',   color: 'var(--amber)',  border: 'rgba(245,158,11,0.2)' },
    purple: { bg: 'var(--purple-bg)',  color: 'var(--purple)', border: 'rgba(167,139,250,0.2)' },
    red:    { bg: 'var(--red-bg)',     color: 'var(--red)',    border: 'rgba(239,68,68,0.2)' },
  };
  const c = colors[color] || colors.accent;

  return (
    <div
      className="card"
      style={{
        display: 'flex', flexDirection: 'column', gap: 14,
        animation: `fadeUp 0.45s ease ${delay}ms both`,
        transition: 'transform 0.2s, box-shadow 0.2s',
        cursor: 'default',
      }}
      onMouseEnter={e => {
        e.currentTarget.style.transform = 'translateY(-3px)';
        e.currentTarget.style.boxShadow = `0 8px 32px ${c.bg}`;
      }}
      onMouseLeave={e => {
        e.currentTarget.style.transform = '';
        e.currentTarget.style.boxShadow = '';
      }}
    >
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <span style={{ fontSize: '0.72rem', fontWeight: 600, textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--text2)' }}>
          {label}
        </span>
        <div style={{
          width: 36, height: 36, borderRadius: 10,
          background: c.bg,
          border: `1px solid ${c.border}`,
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          fontSize: '1rem',
          color: c.color,
        }}>{icon}</div>
      </div>

      <div style={{ fontSize: '2rem', fontWeight: 800, letterSpacing: '-0.04em', lineHeight: 1, color: 'var(--text)' }}>
        {value}
      </div>

      {trend && (
        <div style={{ fontSize: '0.78rem', color: 'var(--text2)', display: 'flex', alignItems: 'center', gap: 6 }}>
          {trend}
        </div>
      )}
    </div>
  );
}
