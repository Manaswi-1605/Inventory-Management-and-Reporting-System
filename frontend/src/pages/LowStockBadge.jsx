export default function LowStockBadge({ quantity, reorderLevel }) {
  const isLow = quantity <= reorderLevel;
  return isLow ? (
    <span className="badge badge-amber badge-pulse">LOW STOCK</span>
  ) : (
    <span className="badge badge-green">✓ OK</span>
  );
}
