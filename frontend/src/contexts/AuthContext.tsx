import { createContext, useState, type ReactNode } from "react";

interface AuthProviderProps {
  children: ReactNode;
}

type AuthState = {
  user: string,
  password: string,
  roles?: string[],
  accessToken: string;
} | null;

interface AuthContextType {
  auth: AuthState;
  setAuth: React.Dispatch<React.SetStateAction<AuthState>>;
}

export const AuthContext = createContext<AuthContextType | null>(null);

export const AuthProvider = ({children}: AuthProviderProps) => {
    const [auth, setAuth] = useState<AuthState | null>(null);

    return(
        <AuthContext.Provider value={{auth, setAuth}}>
            {children}
        </AuthContext.Provider>
    )
}