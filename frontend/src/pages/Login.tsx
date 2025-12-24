import { Link, useNavigate } from 'react-router'
import { DescriptionSection } from '../components/auth/DescriptionSection'
import { FormButton } from '../components/ui/FormButton'
import { FormInput } from '../components/ui/FormInput'
import { GoBackButton } from '../components/ui/GoBackButton'
import { CheckBox } from '../components/ui/CheckBox'
import { useState } from 'react'
import { useAuth } from '../hooks/useAuth'
import {axiosPublic } from '../http/axios'
import { jwtDecode, type JwtPayload } from 'jwt-decode'

interface AppJwtPayload extends JwtPayload {
  roles: string[];
}

const LOGIN_URL = "/grind/keycloak/token"

export const Login = () => {
    const { setAuth } = useAuth();
    const navigate = useNavigate();
    const [user, setUser] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        try {
            const response = await axiosPublic.post(
                LOGIN_URL,
                JSON.stringify({ username: user, password })
            );
            
            console.log(response?.data);
            const accessToken = response?.data?.accessToken;
            const decoded = jwtDecode<AppJwtPayload>(accessToken);
            console.log(decoded);
            const roles = decoded.roles ?? [];
            setAuth({user, password, roles, accessToken});

            setUser('');
            setPassword('');
            alert('Авторизация успешна');
            navigate('/profile', {replace: true})

        } catch (err: any) {
            if(!err?.response)
                setError('Нет ответа от сервера');
            else if (err.response?.status === 400)
                setError('Отсутствует имя или пароль');
            else if (err.response?.status === 401)
                setError('Неавторизован');
            else
                setError('Авторизация провалена');
        } finally {
            setLoading(false);
        }
    };

    return(
        <main className="font-jetbrains flex min-h-screen">
            <section className="bg-secondary min-w-1/2">
                <div className="flex m-6.25 justify-between">
                    <GoBackButton href="/">Назад</GoBackButton>
                    <div className="flex gap-2">
                        <p className="text-primary-dark">Нет аккаунта?</p>
                        <Link className="underline underline-offset-4 text-primary" to="/register">Зарегистрируйтесь</Link>
                    </div>
                </div>
                <form className="text-primary-dark flex flex-col gap-4 mx-auto w-100 mt-75"
                    onSubmit={handleSubmit}>
                    <h1 className="text-5xl">Вход</h1>
                    <FormInput
                        type="text"
                        id="username"
                        onChange={(e) => setUser(e.target.value)}
                        value={user}>
                        Логин
                    </FormInput>
                    <FormInput 
                        type="password"
                        id="password"
                        onChange={(e) => setPassword(e.target.value)}
                        value={password}>
                        Пароль
                    </FormInput>
                    <div className="flex justify-between">
                        <div className="flex items-center gap-2">
                            <CheckBox/>
                            <p>Запомнить меня</p>
                        </div>
                        <a className="underline underline-offset-4 text-primary" href="#">Забыли пароль?</a>
                    </div>
                    <FormButton isLoading={loading} type="submit">Войти</FormButton>
                    <p className={`text-red-600 ${error ? "" : "hidden"}`}>
                        {error}
                    </p>
                </form>
            </section>
            <DescriptionSection/>
        </main>
    )
}
