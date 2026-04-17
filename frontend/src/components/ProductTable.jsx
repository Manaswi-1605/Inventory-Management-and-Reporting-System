import LowStockBadge from './LowStockBadge';
import { FormatUtils } from '../utilities/FormatUtils';

export default function ProductTable({
  products = [],
  loading = false,
  onEdit,
  onDelete,
  canEdit = false,
  canDelete = false,
}) {
  if (loading) {
    return (
      <div className="table-wrapper">
        <div className="empty-state">
          <div className="empty-icon">⏳</div>
          <h3>Loading products...</h3>
        </div>
      </div>
    );
  }

  if (!products.length) {
    return (
      <div className="table-wrapper">
        <div className="empty-state">
          <div className="empty-icon">📦</div>
          <h3>No products found</h3>
          <p>Try changing search or add your first product.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="table-wrapper">
      <table className="data-table">
        <thead>
          <tr>
            <th>Product</th>
            <th>Category</th>
            <th>Supplier</th>
            <th>Qty</th>
            <th>Price</th>
            <th>Value</th>
            <th>Status</th>
            {(canEdit || canDelete) && <th>Actions</th>}
          </tr>
        </thead>
        <tbody>
          {products.map((p) => (
            <tr key={p.id}>
              <td className="product-name-cell">
                {p.name}
                <span>{p.sku || '—'}</span>
              </td>
              <td>
                <span className="category-pill">{p.category || '—'}</span>
              </td>
              <td>{p.supplier || '—'}</td>
              <td style={{ fontFamily: 'DM Mono, monospace' }}>{p.quantity}</td>
              <td style={{ fontFamily: 'DM Mono, monospace' }}>
                {FormatUtils.currency(p.unitPrice)}
              </td>
              <td style={{ fontFamily: 'DM Mono, monospace', fontWeight: 600 }}>
                {FormatUtils.currency(p.quantity * p.unitPrice)}
              </td>
              <td>
                <LowStockBadge quantity={p.quantity} reorderLevel={p.reorderLevel} />
              </td>
              {(canEdit || canDelete) && (
                <td className="actions-cell">
                  {canEdit && (
                    <button className="btn btn-ghost btn-sm" onClick={() => onEdit?.(p)}>
                      Edit
                    </button>
                  )}
                  {canDelete && (
                    <button className="btn btn-danger btn-sm" onClick={() => onDelete?.(p)}>
                      Delete
                    </button>
                  )}
                </td>
              )}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
