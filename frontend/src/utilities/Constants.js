export const API_BASE_URL = 'http://localhost:8080';
 
export const ROLES = {
  ADMIN:    'ADMIN',
  SUPPLIER: 'SUPPLIER',
  CUSTOMER: 'CUSTOMER',
};
 
export const TOKEN_KEY    = 'inv_token';
export const USER_KEY     = 'inv_user';
 
export const LOW_STOCK_THRESHOLD = 10;
 
export const CATEGORIES = [
  'Electronics',
  'Stationery',
  'Furniture',
  'Clothing',
  'Food & Beverages',
  'Tools & Hardware',
  'Medical',
  'Other',
];
 
export const REPORT_TYPES = {
  SUMMARY:    'summary',
  LOW_STOCK:  'low-stock',
  VALUE:      'value',
  CATEGORIES: 'categories',
};