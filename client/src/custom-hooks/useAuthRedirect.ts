import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import { BASE_URL } from "../global-variables/BaseUrl";

export const useAuthRedirect = () => {
  const navigate = useNavigate();

  useEffect(() => {
    const token = sessionStorage.getItem("token");

    if (!token) {
      navigate("/user/login");
      return;
    }

    try {
      const { exp } = jwtDecode(token) as { exp: number };
      const now = Date.now() / 1000;

      if (exp < now) {
        // Token expired, try refreshing
        fetch(`${BASE_URL}/api/users/refresh`, {
          method: "POST",
          credentials: "include", // Send HTTP-only cookies!
        })
          .then(async (res) => {
            if (!res.ok) throw new Error("Refresh failed");

            const data = await res.json();
            sessionStorage.setItem("token", data.token); // Store new access token
          })
          .catch(() => {
            sessionStorage.clear();
            navigate("/user/login");
          });
      }
    } catch (e) {
      sessionStorage.clear();
      navigate("/user/login");
    }
  }, [navigate]);
};
