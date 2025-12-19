import { Link, useNavigate } from 'react-router'
import { DescriptionSection } from '../components/auth/DescriptionSection'
import { FormButton } from '../components/ui/FormButton'
import { FormInput } from '../components/ui/FormInput'
import { GoBackButton } from '../components/ui/GoBackButton'
import { CheckBox } from '../components/ui/CheckBox'
import { useState } from 'react'
import { useAuth } from '../hooks/useAuth'
import { useAxiosPrivate } from '../hooks/useAxiosPrivate'

const LOGIN_URL = "/login"

export const Login = () => {
    const { setAuth } = useAuth();
    const navigate = useNavigate();
    const [user, setUser] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const axiosPrivate = useAxiosPrivate();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        try {
            const response = await axiosPrivate.post(
                LOGIN_URL,
                JSON.stringify({ user, password })
            );
            
            console.log(response?.data);
            const accessToken = response?.data?.accessToken;
            const roles = response?.data?.roles;
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
        <main className="font-jetbrains flex h-dvh overflow-hidden">
            <section className="bg-secondary min-w-1/2">
                <div className="flex m-6.25 justify-between">
                    <GoBackButton href="/">Назад</GoBackButton>
                    <div className="flex gap-2">
                        <p className="text-primary-dark">Нет аккаунта?</p>
                        <Link className="underline underline-offset-4 text-primary" to="/register">Зарегистрируйтесь</Link>
                    </div>
                </div>
                <form className="text-primary-dark flex flex-col gap-4 max-w-100 mx-auto mt-75"
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
