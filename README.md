# Порты для микросервисов

каждый микросервис должен крутиться на указанном порту

- **gateway** - 8080
- **auth (в rest конфигурации)** - 8086
- **notification** - 8082
- **core** - 8083
- **statistics** - 8084

# Порты Docker контейнеров

- **Postgres** - 5432
- **Keycloak** - 8085
- **Kafka** - 9092
- **Kafdrop (UI для кафки)** - 9000
- **Clickhouse HTTP** - 8123
- **Clickhouse TCP** - 9001
- **Redis** - 6379

# Docker compose

Для каждого микросервиса должен быть написан `Dockerfile` для сборки образа самого микросервиса. По готовности всех микросервисов напишем общий `docker-compose.yml` для сборки всех образов разом.<br>
**Перед сборкой образа замените конфигурацию подключения к бд.** <br>
В пропертях укажите не константу, а имя внешней переменной и через нее передайте конфигурацию в `Dockerfile` (актуально только для этапа сборки образа)

# Команда для стягивания образа бд с заданием нужных параметров контейнера через докер 

```
docker run -d --name grind-db -e POSTGRES_DB=grind -e POSTGRES_USER=grind -e POSTGRES_PASSWORD=grind -p 5432:5432 postgres:17
```
или запускайте все нужные контейнеры через `compose.yaml` в корне проекта

- **адрес бд** - `localhost:5432/grind`
- **имя пользователя** - `grind`
- **пароль** - `grind`
- **название контейнера** - `grind-db`

# Эндпоинты Security

## Получение токена
- Эндпоинт
  - `/grind/keycloak/token`
- Поля тела запроса JSON
  - `username`
  - `password`
- Поля ответа JSON
  - `access_token` - сам JWT
  - `expires_in` - когда истекает 
  - `refresh_expires_in`
  - `refresh_token`
  - `id_token`
  - `not-before-policy`
  - `session_state`

## Проверка токена
- Эндпоинт
  - `/grind/keycloak/instrospect`
- Поля тела запроса
  - заголовок `Authorization` с токеном
- Поля тела ответа JSON
  - `active` - активен ли токен
  - `sub` - id владельца
  - `username` - имя владельца
  - `exp` - момент истечения токена
  - `iat` - момент запроса токена
  - `email`
  - `scope`
  - `token-type`
  - `client-id`

## Регистрация
- Эндпоинт
  - `/grind/keycloak/register`
- Поля тела запроса JSON
  - `username`
  - `password`
  - `email`
  - `firstName`
  - `lastName`
  - `isEnabled`
