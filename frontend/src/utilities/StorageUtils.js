import { TOKEN_KEY, USER_KEY } from './Constants';
 
export const StorageUtils = {
  setToken:   (token) => localStorage.setItem(TOKEN_KEY, token),
  getToken:   ()      => localStorage.getItem(TOKEN_KEY),
  removeToken:()      => localStorage.removeItem(TOKEN_KEY),
 
  setUser:    (user)  => localStorage.setItem(USER_KEY, JSON.stringify(user)),
  getUser:    ()      => { try { return JSON.parse(localStorage.getItem(USER_KEY)); } catch { return null; } },
  removeUser: ()      => localStorage.removeItem(USER_KEY),
 
  clearAll:   ()      => { localStorage.removeItem(TOKEN_KEY); localStorage.removeItem(USER_KEY); },
}