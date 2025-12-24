import { Link, useNavigate } from 'react-router'
import { DescriptionSection } from '../components/auth/DescriptionSection'
import { FormButton } from '../components/ui/FormButton'
import { FormInput } from '../components/ui/FormInput'
import { GoBackButton } from '../components/ui/GoBackButton'
import { useState } from 'react'
import { axiosPublic } from '../http/axios'

const USER_REGEX = /^[A-Za-zА-Яа-яЁё0-9_-]{3,24}$/
const NAME_REGEX = /^(?=.{1,24}$)[A-Za-zА-Яа-яЁё]*(-[A-Za-zА-Яа-яЁё]*)?$/
const PASSWORD_REGEX =/^[A-Za-z0-9!@#$%^&*()_+\-=\[\]{};:'",.<>?\/\\|]{6,24}$/
const EMAIL_REGEX = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/
const REGISTER_URL = "/register"

export const Register = () => {
    const [user, setUser] = useState('');
    const [validUser, setValidUser] = useState(true);
    const [email, setEmail] = useState('');
    const [validEmail, setValidEmail] = useState(true);
    const [password, setPassword] = useState('');
    const [validPassword, setValidPassword] = useState(true);
    const [matchPassword, setMatchPassword] = useState('');
    const [validMatch, setValidMatch] = useState(true);
    const [firstName, setFirstName] = useState('');
    const [validFirstName, setValidFirstName] = useState(true);
    const [lastName, setLastName] = useState('');
    const [validLastName, setValidLastName] = useState(true);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const navigate = useNavigate();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);

        const resUser = USER_REGEX.test(user);
        setValidUser(resUser);
        const resFirstName = NAME_REGEX.test(firstName);
        setValidFirstName(resFirstName);
        const resLastName = NAME_REGEX.test(lastName);
        setValidLastName(resLastName);
        const resEmail = EMAIL_REGEX.test(email);
        setValidEmail(resEmail);
        const resPassword = PASSWORD_REGEX.test(password);
        setValidPassword(resPassword);
        const match = password === matchPassword;
        setValidMatch(match);

        if(!validUser || !validFirstName || !validLastName || !validEmail || !validPassword || !validMatch) {
            setLoading(false);
            return;
        }
        try {
            const response = await axiosPublic.post(
                REGISTER_URL,
                JSON.stringify({
                    username: user,
                    password: password,
                    email: email,
                    firstname: firstName,
                    lastname: lastName,
                    isEnabled: true
                    })
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
        <main className="font-jetbrains flex">
            <section className="bg-secondary min-w-1/2 min-h-screen">
                <div className="flex m-6.25 justify-between">
                    <GoBackButton href="/">Назад</GoBackButton>
                    <div className="flex gap-2">
                        <p className="text-primary-dark">Есть аккаунт?</p>
                        <Link className="underline underline-offset-4 text-primary" to="/login">Войдите</Link>
                    </div>
                </div>
                <form className="text-primary-dark flex flex-col gap-4 mx-auto w-100 mt-75"
                    onSubmit={handleSubmit}>
                    <h1 className="text-5xl">Регистрация</h1>
                    <FormInput
                        type="text"
                        id="username"
                        autoComplete="off"
                        onChange={(e) => setUser(e.target.value)}>
                        Логин
                    </FormInput>
                    <p className={`text-red-600 ${validUser ? "hidden" : ""}`}>
                        Логин должен быть 3-24 символов и может состоять только из цифр, букв, тире или подчёркивания
                    </p>
                    <FormInput
                        type="text"
                        id="firstname"
                        autoComplete="off"
                        onChange={(e) => setFirstName(e.target.value)}>
                        Имя
                    </FormInput>
                    <p className={`text-red-600 ${validFirstName ? "hidden" : ""}`}>
                        Имя должно быть до 24 символов и может состоять только из букв
                    </p>
                    <FormInput
                        type="text"
                        id="lastname"
                        autoComplete="off"
                        onChange={(e) => setLastName(e.target.value)}>
                        Фамилия
                    </FormInput>
                    <p className={`text-red-600 ${validLastName ? "hidden" : ""}`}>
                        Фамилия должна быть до 24 символов и может состоять только из букв
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
