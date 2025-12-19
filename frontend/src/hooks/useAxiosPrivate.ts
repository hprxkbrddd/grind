import { useEffect, useRef } from "react";
import { axiosPrivate } from "../http/axios";
import { useAuth } from "./useAuth";
import { useRefreshToken } from "./useRefreshToken"
import type { AxiosError, InternalAxiosRequestConfig } from "axios";

export const useAxiosPrivate = () => {
    const refresh = useRefreshToken();
    const { auth } = useAuth();

    const accessTokenRef = useRef(auth?.accessToken);
    const refreshRef = useRef(refresh);

    useEffect(() => {
        const requestIntercept = axiosPrivate.interceptors.request.use(
            (config: InternalAxiosRequestConfig) => {
                if(!config?.headers['Authorization'] && accessTokenRef.current) {
                    config.headers['Authorization'] = `Bearer ${accessTokenRef.current}`;
                }
                return config;
            }, (error: AxiosError) => Promise.reject(error)
        );

        const responseIntercept = axiosPrivate.interceptors.response.use(
            response => response,
            async (error: AxiosError) => {
                if(error.config) {
                    const prevRequest: InternalAxiosRequestConfig & {sent?: boolean} = error.config;
                    if(error?.response?.status === 403 && !prevRequest?.sent) {
                        prevRequest.sent = true;
                        const newAccessToken = await refreshRef.current();
                        prevRequest.headers['Authorization'] = `Bearer ${newAccessToken}`;
                        return axiosPrivate(prevRequest);
                    }
                }
                return Promise.reject(error)
            }
        )

        return () => {
            axiosPrivate.interceptors.request.eject(requestIntercept);
            axiosPrivate.interceptors.response.eject(responseIntercept);
        }

    }, [])

    return axiosPrivate;
}
