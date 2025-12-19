import { Navigate, Outlet, useLocation } from "react-router";
import { useAuth } from "../hooks/useAuth";

interface RequireAuthProps {
    allowedRoles: string[];
}

export const RequireAuth = ({allowedRoles}:RequireAuthProps) => {
    const { auth } = useAuth();
    const location = useLocation();

    return(
        auth?.roles?.find(role => allowedRoles?.includes(role))
            ? <Outlet />
            : auth?.user
                ? <Navigate to="/unauthorized" state={{from: location}} replace/>
                : <Navigate to="/login" state={{from: location}} replace/>
    )
}
