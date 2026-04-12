import { BadgeCheck, UserRoundPlus } from 'lucide-react'
import { useState, type FormEvent } from 'react'
import { Link, useNavigate } from 'react-router'
import { gatewayApi, getApiErrorMessage } from '../api/gateway'
import { ActionButton } from '../components/app/ActionButton'
import { Field } from '../components/app/Field'
import { Panel } from '../components/app/Panel'

const USERNAME_REGEX = /^[A-Za-zА-Яа-яЁё0-9_-]{3,24}$/
const NAME_REGEX = /^(?=.{1,24}$)[A-Za-zА-Яа-яЁё]*(-[A-Za-zА-Яа-яЁё]*)?$/
const PASSWORD_REGEX =
  /^[A-Za-z0-9!@#$%^&*()_+\-=[\]{};:'",.<>?/\\|]{6,24}$/
const EMAIL_REGEX = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/

export function Register() {
  const navigate = useNavigate()
  const [form, setForm] = useState({
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
    firstName: '',
    lastName: '',
  })
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  function updateField<Key extends keyof typeof form>(key: Key, value: string) {
    setForm((current) => ({
      ...current,
      [key]: value,
    }))
  }

  function validateForm() {
    if (!USERNAME_REGEX.test(form.username)) {
      return 'Username должен быть длиной 3-24 символа и состоять из букв, цифр, "_" или "-"'
    }

    if (!NAME_REGEX.test(form.firstName) || !NAME_REGEX.test(form.lastName)) {
      return 'Имя и фамилия должны содержать только буквы и быть не длиннее 24 символов'
    }

    if (!EMAIL_REGEX.test(form.email)) {
      return 'Email имеет неверный формат'
    }

    if (!PASSWORD_REGEX.test(form.password)) {
      return 'Пароль должен быть длиной 6-24 символа'
    }

    if (form.password !== form.confirmPassword) {
      return 'Пароли не совпадают'
    }

    return ''
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const validationError = validateForm()

    if (validationError) {
      setError(validationError)
      return
    }

    setBusy(true)
    setError('')

    try {
      await gatewayApi.auth.register({
        username: form.username,
        password: form.password,
        email: form.email,
        firstName: form.firstName,
        lastName: form.lastName,
        isEnabled: true,
      })

      navigate('/login', { replace: true })
    } catch (submissionError) {
      setError(getApiErrorMessage(submissionError))
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="mx-auto grid max-w-5xl gap-6 lg:grid-cols-[1fr_0.95fr]">
      <Panel
        eyebrow="Registration"
        icon={<UserRoundPlus className="h-5 w-5" />}
        title="Регистрация пользователя"
        description="Форма соответствует `RegistrationDTO` из `gateway`, но подана как обычный понятный onboarding без ощущения, что вы заполняете raw JSON."
        tone="warm"
      >
        <form className="grid gap-4 md:grid-cols-2" onSubmit={handleSubmit}>
          <Field
            label="Username"
            hint="3-24 символа: буквы, цифры, `_` или `-`"
            name="username"
            value={form.username}
            onChange={(event) => updateField('username', event.target.value)}
            required
          />
          <Field
            label="Email"
            hint="Нужен для Keycloak-профиля"
            name="email"
            type="email"
            value={form.email}
            onChange={(event) => updateField('email', event.target.value)}
            required
          />
          <Field
            label="First name"
            hint="До 24 символов"
            name="firstName"
            value={form.firstName}
            onChange={(event) => updateField('firstName', event.target.value)}
            required
          />
          <Field
            label="Last name"
            hint="До 24 символов"
            name="lastName"
            value={form.lastName}
            onChange={(event) => updateField('lastName', event.target.value)}
            required
          />
          <Field
            label="Password"
            hint="Минимум 6 символов"
            name="password"
            type="password"
            value={form.password}
            onChange={(event) => updateField('password', event.target.value)}
            required
          />
          <Field
            label="Confirm password"
            name="confirmPassword"
            type="password"
            value={form.confirmPassword}
            onChange={(event) =>
              updateField('confirmPassword', event.target.value)
            }
            required
          />

          {error ? (
            <div className="md:col-span-2 rounded-2xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">
              {error}
            </div>
          ) : null}

          <div className="md:col-span-2 flex flex-wrap gap-3">
            <ActionButton type="submit" busy={busy}>
              Создать пользователя
            </ActionButton>
            <Link
              className="rounded-2xl border border-primary/30 px-4 py-2 text-sm font-semibold text-primary-dark"
              to="/login"
            >
              Уже есть аккаунт
            </Link>
          </div>
        </form>
      </Panel>

      <section className="rounded-[32px] bg-[linear-gradient(180deg,_rgba(64,121,140,0.12),_rgba(255,255,255,0.96))] p-6 shadow-[0_18px_70px_rgba(31,54,61,0.08)]">
        <p className="text-sm uppercase tracking-[0.32em] text-primary">
          DTO Check
        </p>
        <div className="mt-5 space-y-3 text-sm text-slate-600">
          <p>`POST /grind/keycloak/register`</p>
          <p>Поля: `username`, `password`, `email`, `firstName`, `lastName`, `isEnabled`</p>
          <p>
            После успешной регистрации пользователь сразу может пройти в `/login`
            и получить JWT для защищённых маршрутов.
          </p>
        </div>
        <div className="mt-8 rounded-2xl border border-primary/12 bg-white/80 p-4 text-sm leading-6 text-slate-600">
          <div className="flex items-center gap-2 font-semibold text-slate-800">
            <BadgeCheck className="h-4 w-4 text-primary" />
            Что делает форма
          </div>
          <p className="mt-2">
            Проверяет формат полей на клиенте, а затем отправляет уже аккуратный
            `RegistrationDTO` без переименований и костылей между frontend и gateway.
          </p>
        </div>
      </section>
    </div>
  )
}
