import { axiosPublic } from '../http/axios';
import { useAuth } from './useAuth';

export const useRefreshToken = () => {
    const { setAuth } = useAuth();

    const refresh = async () => {
        const response = await axiosPublic.get('/refresh', {
            withCredentials: true
        });
        setAuth(prev => {
            if (!prev) return prev;
            return {...prev, accessToken: response.data.accessToken}
        });
    }
    return refresh; 
}