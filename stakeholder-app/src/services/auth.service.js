import http from "../utils/http-client";

const login = (data) => {
    return http.post('/auth/login', data, {
        transformResponse: [(result) => {
            const parsed = JSON.parse(result);
            localStorage.setItem('authToken', JSON.stringify(parsed));
            return parsed;
        }]
    });
}

const register = (data,config) => {
    console.log(config)
    return http.post('/auth/register', data, config);
}

const profile = () => {
    return http.get('/user');
}

const logout = () => {
    return http.get('/logout', null, {
        transformResponse: [(result) => {
            localStorage.removeItem('authToken');
            return JSON.parse(result);
        }]
    });
}

const getAuthUser = () => {
    return JSON.parse(localStorage.getItem('authToken'));
}  

const methods = { 
    login,
    register,
    profile,
    logout,
    getAuthUser
}

export default methods;