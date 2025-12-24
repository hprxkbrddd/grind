import axios from "axios";

const API_URL = "https://localhost:8080";

export const axiosPublic = axios.create({
    withCredentials: true,
    baseURL: API_URL,
    headers: {
      'Content-Type': 'application/json',
    },
});

export const axiosPrivate = axios.create({
    withCredentials: true,
    baseURL: API_URL,
    headers: {
      'Content-Type': 'application/json',
    },
});