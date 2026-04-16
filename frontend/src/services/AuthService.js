// import { api } from '../utilities/ApiUtils';
 
// export const AuthService = {
//   login:  (username, password) => api.post('/auth/login',  { username, password }),
//   logout: ()                   => api.post('/auth/logout', {}),
//   me:     ()                   => api.get('/auth/me'),
// };
import { apiRequest } from "../utilities/ApiUtils";
import { TOKEN_KEY } from "./Constants";

const AuthService = {
  register: async (data) => {
    return await apiRequest("/auth/register", "POST", {
      ...data,
      role: data.role.toUpperCase(), // 🔥 FIX
    });
  },

  login: async (username, password) => {
    const res = await apiRequest("/auth/login", "POST", {
      username,
      password,
    });

    // 🔥 SAVE TOKEN
    localStorage.setItem(TOKEN_KEY, res.token);

    return res;
  },

  logout: () => {
    localStorage.removeItem(TOKEN_KEY); // 🔥 FIX
  },

  isLoggedIn: () => {
    return !!localStorage.getItem(TOKEN_KEY);
  },
};

export default AuthService;