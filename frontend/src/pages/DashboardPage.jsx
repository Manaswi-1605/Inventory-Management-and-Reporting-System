import { useEffect } from 'react';
import { Link } from 'react-router-dom';
import Navbar from '../components/Navbar';
import Sidebar from '../components/Sidebar';
import SummaryCard from '../components/SummaryCard';
import ChartComponent from '../components/ChartComponent';
import LowStockBadge from '../components/LowStockBadge';
import { useProducts } from '../hooks/useProducts';
import { useToast } from '../context/ToastContext';
import { FormatUtils } from '../utilities/FormatUtils';

export default function DashboardPage() {
  const { products, loading, lowStockProducts } = useProducts();
  const toast = useToast();

  const totalValue = products.reduce((acc, p) => acc + p.quantity * p.unitPrice, 0);

  // Real-time low stock notification
  useEffect(() => {
    if (!loading && lowStockProducts.length > 0) {
      toast.warning(
        'Low Stock Alert',
        `${lowStockProducts.length} product${lowStockProducts.length > 1 ? 's are' : ' is'} below reorder level.`
      );
    }
  }, [loading]);

  // Category chart
  const categoryMap = {};
  products.forEach(p => { categoryMap[p.category] = (categoryMap[p.category] || 0) + 1; });
  const chartData = Object.entries(categoryMap).map(([label, value]) => ({ label, value }));

  return (
    <div className="page-layout">
      <Sidebar lowStockCount={lowStockProducts.length} />
      <div className="main-content">
        <Navbar title="Dashboard" lowStockCount={lowStockProducts.length} />
        <div className="page-inner">

          <div className="page-header">
            <h1 className="page-title">Overview</h1>
            <p className="page-subtitle">Your inventory at a glance — {new Date().toLocaleDateString('en-IN', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}</p>
          </div>

          {/* Summary cards */}
          <div className="summary-grid">
            <SummaryCard
              label="Total Products"
              value={loading ? '…' : FormatUtils.number(products.length)}
              icon="📦" color="accent" delay={0}
            />
            <SummaryCard
              label="Inventory Value"
              value={loading ? '…' : FormatUtils.currency(totalValue)}
              icon="₹" color="green" delay={80}
            />
            <SummaryCard
              label="Low Stock Items"
              value={loading ? '…' : lowStockProducts.length}
              icon="⚠" color={lowStockProducts.length > 0 ? 'amber' : 'green'} delay={160}
              trend={lowStockProducts.length > 0
                ? <span style={{ color: 'var(--amber)' }}>⬇ Needs restocking</span>
                : <span style={{ color: 'var(--green)' }}>✓ All stocked</span>
              }
            />
            <SummaryCard
              label="Categories"
              value={loading ? '…' : Object.keys(categoryMap).length}
              icon="🏷" color="purple" delay={240}
            />
          </div>

          {/* Bottom panels */}
          <div className="dash-grid">

            {/* Chart */}
            <div className="dash-panel">
              <div className="dash-panel-title">
                Products by Category
                <Link to="/products">View all →</Link>
              </div>
              <ChartComponent data={chartData} height={200} />
            </div>

            {/* Low stock list */}
            <div className="dash-panel">
              <div className="dash-panel-title">
                Low Stock Alerts
                {lowStockProducts.length > 0 && (
                  <span className="badge badge-amber badge-pulse">{lowStockProducts.length} items</span>
                )}
              </div>

              {lowStockProducts.length === 0 ? (
                <div style={{ color: 'var(--green)', fontSize: '0.88rem', paddingTop: 8, display: 'flex', alignItems: 'center', gap: 8 }}>
                  <span>✓</span> All products are sufficiently stocked.
                </div>
              ) : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
                  {lowStockProducts.slice(0, 6).map(p => (
                    <div key={p.id} style={{
                      display: 'flex', alignItems: 'center', justifyContent: 'space-between',
                      padding: '10px 14px',
                      background: 'var(--bg3)',
                      borderRadius: 'var(--r)',
                      border: '1px solid var(--border)',
                      transition: 'background 0.15s',
                    }}>
                      <div>
                        <div style={{ fontSize: '0.88rem', fontWeight: 600 }}>{p.name}</div>
                        <div style={{ fontSize: '0.75rem', color: 'var(--text2)' }}>{p.category}</div>
                      </div>
                      <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                        <span style={{ fontFamily: 'var(--mono)', fontSize: '0.82rem', color: 'var(--text2)' }}>
                          {p.quantity}/{p.reorderLevel}
                        </span>
                        <LowStockBadge quantity={p.quantity} reorderLevel={p.reorderLevel} />
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
