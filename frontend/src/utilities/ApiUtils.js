// import { API_BASE_URL, TOKEN_KEY } from './Constants';
 
// const getToken = () => localStorage.getItem(TOKEN_KEY);
 
// const headers = (extra = {}) => ({
//   'Content-Type': 'application/json',
//   ...(getToken() ? { Authorization: `Bearer ${getToken()}` } : {}),
//   ...extra,
// });
 
// const handleResponse = async (res) => {
//   const data = await res.json().catch(() => ({}));
//   if (!res.ok) throw new Error(data.error || `HTTP ${res.status}`);
//   return data;
// };
 
// export const api = {
//   get: (path) =>
//     fetch(`${API_BASE_URL}${path}`, { headers: headers() }).then(handleResponse),
 
//   post: (path, body) =>
//     fetch(`${API_BASE_URL}${path}`, {
//       method: 'POST',
//       headers: headers(),
//       body: JSON.stringify(body),
//     }).then(handleResponse),
 
//   put: (path, body) =>
//     fetch(`${API_BASE_URL}${path}`, {
//       method: 'PUT',
//       headers: headers(),
//       body: JSON.stringify(body),
//     }).then(handleResponse),
 
//   delete: (path) =>
//     fetch(`${API_BASE_URL}${path}`, {
//       method: 'DELETE',
//       headers: headers(),
//     }).then(handleResponse),
// };
// import { API_BASE_URL, TOKEN_KEY } from "./Constants";

// // Generic request function
// export const apiRequest = async (endpoint, method = "GET", body = null) => {
//   const token = localStorage.getItem(TOKEN_KEY);

//   const headers = {
//     "Content-Type": "application/json",
//   };

//   // 🔐 attach token if exists
//   if (token) {
//     headers["Authorization"] = `Bearer ${token}`;
//   }

//   const res = await fetch(`${API_BASE_URL}${endpoint}`, {
//     method,
//     headers,
//     body: body ? JSON.stringify(body) : null,
//   });

//   if (!res.ok) {
//     throw new Error("API Error: " + res.status);
//   }

//   return res.json();
// };
import axios from "axios";
import { API_BASE_URL, TOKEN_KEY } from "./Constants";

const api = axios.create({
  baseURL: API_BASE_URL,
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY);
  if (token) {
    config.headers = config.headers ?? {};
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Make axios behave like fetch-wrapper: return `data` directly everywhere.
api.interceptors.response.use(
  (response) => response.data,
  (error) => Promise.reject(error)
);

export default api;

// Optional helper
export const apiRequest = async (url, method = "GET", data = null) => {
  const res = await api({
    url,
    method,
    data,
  });
  return res;
};