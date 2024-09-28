import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

export const useAuthRedirect = () => {
  const navigate = useNavigate();

  useEffect(() => {
    const token = sessionStorage.getItem("token");
    
    // If no token, redirect to login
    if (!token) {
      navigate("/user/login");
    }
  }, [navigate]); // Runs only when `navigate` changes (i.e., on initial render)
};
