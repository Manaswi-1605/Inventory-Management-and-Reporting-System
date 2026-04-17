import { useEffect, useRef } from 'react';

/**
 * Lightweight bar chart — no external dependency.
 * Props:
 *   data:   [{ label, value, color? }]
 *   title:  string
 *   height: number (default 200)
 */
export default function ChartComponent({ data = [], title, height = 200 }) {
  const canvasRef = useRef(null);

  useEffect(() => {
    if (!canvasRef.current || !data.length) return;
    const canvas = canvasRef.current;
    const ctx    = canvas.getContext('2d');
    const W = canvas.width;
    const H = canvas.height;

    // Clear
    ctx.clearRect(0, 0, W, H);

    const max      = Math.max(...data.map(d => d.value), 1);
    const barW     = Math.floor((W - 60) / data.length - 10);
    const padLeft  = 50;
    const padBot   = 36;
    const chartH   = H - padBot - 16;

    // Grid lines
    ctx.strokeStyle = '#1e2a3d';
    ctx.lineWidth   = 1;
    [0.25, 0.5, 0.75, 1].forEach(f => {
      const y = 16 + chartH * (1 - f);
      ctx.beginPath(); ctx.moveTo(padLeft, y); ctx.lineTo(W - 10, y); ctx.stroke();
      ctx.fillStyle = '#4a5568'; ctx.font = '10px DM Mono, monospace';
      ctx.textAlign = 'right';
      ctx.fillText(Math.round(max * f), padLeft - 6, y + 4);
    });

    // Bars
    data.forEach((d, i) => {
      const barH  = (d.value / max) * chartH;
      const x     = padLeft + i * (barW + 10);
      const y     = 16 + chartH - barH;
      const color = d.color || '#4f8eff';

      // Bar fill
      const grad = ctx.createLinearGradient(0, y, 0, y + barH);
      grad.addColorStop(0, color);
      grad.addColorStop(1, color + '55');
      ctx.fillStyle = grad;
      ctx.beginPath();
      ctx.roundRect(x, y, barW, barH, [4, 4, 0, 0]);
      ctx.fill();

      // Label
      ctx.fillStyle = '#8892a4'; ctx.font = '10px DM Sans, sans-serif';
      ctx.textAlign = 'center';
      const lx = x + barW / 2;
      ctx.fillText(d.label.length > 8 ? d.label.slice(0, 8) + '…' : d.label, lx, H - 8);

      // Value on top
      ctx.fillStyle = '#54509f'; ctx.font = 'bold 11px DM Mono, monospace';
      ctx.fillText(d.value, lx, y - 5);
    });
  }, [data]);

  if (!data.length) {
    return (
      <div style={{ padding: 32, textAlign: 'center', color: 'var(--text-secondary)', fontSize: '0.85rem' }}>
        No data available
      </div>
    );
  }

  return (
    <div>
      {title && (
        <div style={{ fontSize: '0.85rem', fontWeight: 600, marginBottom: 14, color: 'var(--text-secondary)' }}>
          {title}
        </div>
      )}
      <canvas
        ref={canvasRef}
        width={480}
        height={height}
        style={{ width: '100%', height: 'auto', display: 'block' }}
      />
    </div>
  );
}