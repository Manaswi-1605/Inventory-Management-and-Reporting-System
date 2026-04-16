import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { InventoryProvider } from './context/InventoryContext.jsx';
import { ThemeProvider } from './context/ThemeContext.js';
import { ToastProvider } from './context/ToastContext.jsx';
import { useAuth } from './hooks/useAuth';

import LoginPage      from './pages/LoginPage.jsx';
import RegisterPage   from './pages/RegisterPage.jsx';
import DashboardPage  from './pages/DashboardPage.jsx';
import ProductsPage   from './pages/ProductsPage.jsx';
import AddProductPage from './pages/AddProductPage.jsx';
import ReportsPage    from './pages/ReportsPage.jsx';
import AddUserPage    from './pages/AddUserPage.jsx';

import './styles/global.css';

function Protected({ children, requiredRole }) {
  const { isAuthenticated, hasRole } = useAuth();
  if (!isAuthenticated) return <Navigate to="/login" replace />;
  if (requiredRole && !hasRole(requiredRole)) return <Navigate to="/dashboard" replace />;
  return children;
}

function AppRoutes() {
  const { isAuthenticated } = useAuth();
  return (
    <Routes>
      <Route path="/login" element={
        isAuthenticated ? <Navigate to="/dashboard" replace /> : <LoginPage />
      } />
      <Route path="/register" element={
        isAuthenticated ? <Navigate to="/dashboard" replace /> : <RegisterPage />
      } />
      <Route path="/dashboard"   element={<Protected><DashboardPage /></Protected>} />
      <Route path="/products"    element={<Protected><ProductsPage /></Protected>} />
      <Route path="/add-product" element={<Protected requiredRole="SUPPLIER"><AddProductPage /></Protected>} />
      <Route path="/reports"     element={<Protected><ReportsPage /></Protected>} />
      <Route path="/add-user"    element={<Protected requiredRole="ADMIN"><AddUserPage /></Protected>} />
      <Route path="*"            element={<Navigate to={isAuthenticated ? '/dashboard' : '/login'} replace />} />
    </Routes>
  );
}

export default function App() {
  return (
    <ThemeProvider>
      <ToastProvider>
        <BrowserRouter>
          <AuthProvider>
            <InventoryProvider>
              <AppRoutes />
            </InventoryProvider>
          </AuthProvider>
        </BrowserRouter>
      </ToastProvider>
    </ThemeProvider>
  );
}
