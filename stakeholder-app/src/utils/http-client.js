import axios from 'axios';
import authService from '../services/auth.service';

const instance = axios.create({
    baseURL: process.env.REACT_APP_API_BASE_URL 
});

instance.interceptors.request.use((config) => {
    const authUser = authService.getAuthUser();
    if (authUser && authUser.authToken) {
        config.headers['authorization'] = `Bearer ${authUser.authToken}`;
    }
    return config;
}, (error) => {
    return Promise.reject(error);
});

instance.interceptors.response.use((response) => {
    return response;
}, (error) => {
    if (error?.response?.status === 401) { 
        localStorage.removeItem('authToken');
        window.location.reload();
    } else {
        return Promise.reject(error.response);
    }
});

const get = (url, params, config = {}) => instance.get(url, { params, ...config });
const post = (url, data, config = {}) => instance.post(url, data, config);

const methods = { get, post };

export default methods;
