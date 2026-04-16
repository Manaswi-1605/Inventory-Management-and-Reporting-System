export default function LowStockBadge({ quantity, reorderLevel }) {
  const isLow = quantity <= reorderLevel;

  if (isLow) {
    return (
      <span className="low-stock-badge">
        LOW STOCK
      </span>
    );
  }
  return (
    <span className="ok-badge">
      ✓ OK
    </span>
  );
}