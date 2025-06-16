import { BASE_URL } from "../global-variables/BaseUrl";

export const authorizedFetch = async (url: string, options: RequestInit = {}) => {
    let token = sessionStorage.getItem("token");

    const applyAuth = (headers: HeadersInit = {}) => ({
        ...headers,
        Authorization: `Bearer ${token}`,
    });

    // First attempt
    let res = await fetch(url, {
        ...options,
        headers: applyAuth(options.headers),
    });

    // If unauthorized, try refresh
    if (res.status === 401) {
        const refreshRes = await fetch(`${BASE_URL}/api/users/refresh`, {
            method: "POST",
            credentials: "include",
        });

        if (refreshRes.ok) {
            const data = await refreshRes.json();
            sessionStorage.setItem("token", data.token);
            token = data.token;

            // Retry original request
            res = await fetch(url, {
                ...options,
                headers: applyAuth(options.headers),
            });
        } else {
            sessionStorage.clear();
            window.location.href = "/user/login"; // force logout
        }
    }

    return res;
};
