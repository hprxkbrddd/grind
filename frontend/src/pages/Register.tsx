import { Link, useNavigate } from 'react-router'
import { DescriptionSection } from '../components/auth/DescriptionSection'
import { FormButton } from '../components/ui/FormButton'
import { FormInput } from '../components/ui/FormInput'
import { GoBackButton } from '../components/ui/GoBackButton'
import { useState } from 'react'
import { axiosPublic } from '../http/axios'

const USER_REGEX = /^[A-Za-zА-Яа-яЁё0-9_-]{3,24}$/
const PASSWORD_REGEX =/^[A-Za-z0-9!@#$%^&*()_+\-=\[\]{};:'",.<>?\/\\|]{6,24}$/
const EMAIL_REGEX = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/
const REGISTER_URL = "/register"

export const Register = () => {
    const [user, setUser] = useState('');
    const [validName, setValidName] = useState(true);

    const [email, setEmail] = useState('');
    const [validEmail, setValidEmail] = useState(true);

    const [password, setPassword] = useState('');
    const [validPassword, setValidPassword] = useState(true);

    const [matchPassword, setMatchPassword] = useState('');
    const [validMatch, setValidMatch] = useState(true);

    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);

        const navigate = useNavigate();
        const resUser = USER_REGEX.test(user);
        setValidName(resUser);
        const resEmail = EMAIL_REGEX.test(email);
        setValidEmail(resEmail);
        const resPassword = PASSWORD_REGEX.test(password);
        setValidPassword(resPassword);
        const match = password === matchPassword;
        setValidMatch(match);
        if(!validName || !validEmail || !validPassword || !validMatch) {
            setLoading(false);
            return;
        }
        try {
            const response = await axiosPublic.post(
                REGISTER_URL,
                JSON.stringify({ user, email, password }),
            );

            console.log(response?.data);

            alert('Регистрация успешна');
            navigate('/login', {replace: true});

        } catch (err: any) {
            if(!err?.response)
                setError('Нет ответа от сервера');
            else if (err.response?.status === 409)
                setError('Имя занято');
            else
                setError('Регистрация провалена');
        } finally {
            setLoading(false); 
        }
    }

    return(
        <main className="font-jetbrains flex h-dvh overflow-hidden">
            <section className="bg-secondary min-w-1/2">
                <div className="flex m-6.25 justify-between">
                    <GoBackButton href="/">Назад</GoBackButton>
                    <div className="flex gap-2">
                        <p className="text-primary-dark">Есть аккаунт?</p>
                        <Link className="underline underline-offset-4 text-primary" to="/login">Войдите</Link>
                    </div>
                </div>
                <form className="text-primary-dark flex flex-col gap-4 max-w-100 mx-auto mt-75"
                    onSubmit={handleSubmit}>
                    <h1 className="text-5xl">Регистрация</h1>
                    <FormInput
                        type="text"
                        id="username"
                        autoComplete="off"
                        onChange={(e) => setUser(e.target.value)}>
                        Логин
                    </FormInput>
                    <p className={`text-red-600 ${validName ? "hidden" : ""}`}>
                        Имя должно быть 3-24 символов и может состоять только из цифр, букв, тире или подчёркивания
                    </p>
                    <FormInput
                        type="email"
                        id="email"
                        onChange={(e) => setEmail(e.target.value)}>
                        Почта
                    </FormInput>
                    <p className={`text-red-600 ${validEmail ? "hidden" : ""}`}>
                        Неверный формат
                    </p>
                    <FormInput
                        type="password"
                        id="password"
                        onChange={(e) => setPassword(e.target.value)}>
                        Пароль
                    </FormInput>
                    <p className={`text-red-600 ${validPassword ? "hidden" : ""}`}>
                        Пароль должен быть 6-24 символов
                    </p>
                    <FormInput
                        type="password"
                        id="matchPassword"
                        onChange={(e) => setMatchPassword(e.target.value)}>
                        Подтверждение пароля
                    </FormInput>
                    <p className={`text-red-600 ${validMatch ? "hidden" : ""}`}>
                        Пароли не совпадают
                    </p>
                    <FormButton isLoading={loading} type="submit">Зарегистрироваться</FormButton>
                    <p className={`text-red-600 ${error ? "" : "hidden"}`}>
                        {error}
                    </p>
                </form>
            </section>
            <DescriptionSection/>
        </main>
    )
}
