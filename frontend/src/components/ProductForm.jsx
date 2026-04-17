import { useState, useEffect } from 'react';
import { ValidationUtils } from '../utilities/ValidationUtils';

const EMPTY = {
  name: '', sku: '', description: '', categoryId: '', supplierId: '',
  quantity: '', unitPrice: '', reorderLevel: 10,
};
const ID_OPTIONS = [1, 2, 3, 4];

function buildInitialForm(initial) {
  if (!initial) return { ...EMPTY };
  return {
    ...EMPTY, ...initial,
    categoryId: initial?.category?.id ?? initial?.categoryId ?? '',
    supplierId: initial?.supplier?.id ?? initial?.supplierId ?? '',
    unitPrice:  initial?.unitPrice ?? initial?.price ?? '',
  };
}

// ✅ Hoisted — stable reference, React never remounts this
function Field({ label, field, form, errors, setField, type = 'text', ...rest }) {
  return (
    <div className="form-group">
      <label>{label}</label>
      <input
        type={type}
        value={form[field] ?? ''}
        onChange={setField(field)}
        {...rest}
      />
      {errors[field] && <div className="error-msg">{errors[field]}</div>}
    </div>
  );
}

export default function ProductForm({ initial = null, onSubmit, onCancel, loading = false }) {
  const [form,   setForm]   = useState(() => buildInitialForm(initial));
  const [errors, setErrors] = useState({});

  useEffect(() => {
    setForm(buildInitialForm(initial));
    setErrors({});
  }, [initial]);

  const setField = (field) => (e) => setForm((f) => ({ ...f, [field]: e.target.value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    const errs = ValidationUtils.validateProduct(form);
    if (Object.keys(errs).length) { setErrors(errs); return; }
    setErrors({});
    await onSubmit({
      ...form,
      categoryId:   Number(form.categoryId),
      supplierId:   Number(form.supplierId),
      quantity:     Number(form.quantity),
      unitPrice:    Number(form.unitPrice),
      reorderLevel: Number(form.reorderLevel),
    });
  };

  return (
    <form onSubmit={handleSubmit} className="form-card">
      <div className="form-grid-2">
        <Field label="Product Name *" field="name"     form={form} errors={errors} setField={setField} placeholder="e.g. Wireless Mouse" />
        <Field label="SKU"            field="sku"      form={form} errors={errors} setField={setField} placeholder="e.g. WM-001" />

        {/* selects stay inline — no Field wrapper needed here */}
        <div className="form-group">
          <label>Category ID *</label>
          <select value={form.categoryId ?? ''} onChange={setField('categoryId')}>
            <option value="">Select Category ID</option>
            {ID_OPTIONS.map((id) => <option key={id} value={id}>{id}</option>)}
          </select>
          {errors.categoryId && <div className="error-msg">{errors.categoryId}</div>}
        </div>

        <div className="form-group">
          <label>Supplier ID *</label>
          <select value={form.supplierId ?? ''} onChange={setField('supplierId')}>
            <option value="">Select Supplier ID</option>
            {ID_OPTIONS.map((id) => <option key={id} value={id}>{id}</option>)}
          </select>
          {errors.supplierId && <div className="error-msg">{errors.supplierId}</div>}
        </div>

        <Field label="Quantity *"       field="quantity"     type="number" min="0" placeholder="0"    form={form} errors={errors} setField={setField} />
        <Field label="Unit Price (₹) *" field="unitPrice"    type="number" min="0" step="0.01" placeholder="0.00" form={form} errors={errors} setField={setField} />
        <Field label="Reorder Level"    field="reorderLevel" type="number" min="0" placeholder="10"   form={form} errors={errors} setField={setField} />

        <div className="form-group" style={{ gridColumn: '1 / -1' }}>
          <label>Description</label>
          <input
            type="text"
            value={form.description ?? ''}
            onChange={setField('description')}
            placeholder="Short description"
          />
          {errors.description && <div className="error-msg">{errors.description}</div>}
        </div>
      </div>

      <div className="form-actions">
        <button type="submit" className="btn btn-primary" disabled={loading}>
          {loading ? 'Saving…' : (initial ? 'Update Product' : 'Add Product')}
        </button>
        {onCancel && (
          <button type="button" className="btn btn-ghost" onClick={onCancel}>Cancel</button>
        )}
      </div>
    </form>
  );
}