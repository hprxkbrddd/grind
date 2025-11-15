@startgantt
printscale daily
Project starts the 14th of October 2025

[Разработка архитектуры проекта] as [Arch] requires 14 days
[Определение требований к работе сервиса] as [A1] requires 3 days and is colored in #ccde77/#596132
[Выбор архитектурного стиля] as [B1] requires 1 days and starts at [A1]'s end and is colored in #ccde77/#596132
[Проектирование ресурсов и определение операций (Продумывание эндпоинтов)] as [B2] requires 7 days and starts at [B1]'s end and is colored in #fce803/#ff8d03
[Проектирование модели данных] as [B3] requires 7 days and starts at [B1]'s end and is colored in #ffffff/#ff8d03
[Построение диаграммы размещения] as [B4] requires 7 days and starts at [B1]'s end and is colored in #ff8d03/#804601
[Определение содержания экранов веб-приложения] as [B5] requires 7 days and starts at [B1]`s end and is colored in #82c4d1/#fce803

[Формирование общей картины] as [A2] requires 3 days and is colored in #ccde77/#596132
[A2] starts at [B2]'s end
[A2] starts at [B3]'s end
[A2] starts at [B4]'s end
[A2] starts at [B5]'s end

2025/11/10 is colored in salmon
[Написан сервис-прослойка к базе данных] happens on 2025-11-10
[В базе данных созданы таблицы для ключевых сущностей приложения] happens on 2025-11-10
[Созданы макеты экранов] happens on 2025-11-10
[Поднят сервер авторизации] happens on 2025-11-10
[Написана библиотека разграничения доступа] happens on 2025-11-10

2025/11/24 is colored in salmon
[Написан сервис бизнес-логики] happens on 2025-11-24
[Написаны миграции для бд] happens on 2025-11-24
[Поднят Gateway] happens on 2025-11-24
[Поднята Kafka] happens on 2025-11-24
[Написано приложение на React] happens on 2025-11-24

[Реализация проекта] as [Dev] requires 30 days and starts at [Arch]'s end

[Разработка Frontend`а] as [Front] requires 42 days and starts at [Dev]'s start
[Создание макетов экранов] as [Front2] requires 14 days and starts at [Front]`s start and is colored in #82c4d1/#1ed4f7
[Написание React компонентов] as [Front3] requires 14 days and starts at [Front2]`s end and is colored in #82c4d1/#1ed4f7

[Разработка Backend'a] as [Back] requires 42 days and starts at [Dev]'s start

[Создание сущностей в базе данных] as [DB1] requires 3 days and starts at [Back]'s start and is colored in #ffffff/#000000
[Разработка Read-API] as [DB2] requires 11 days and starts at [DB1]'s end and is colored in #ffffff/#000000
[Написание миграций] as [DB3] requires 4 days and starts at [DB2]'s end and is colored in #ffffff/#000000
[Реализация репликации в бд] as [DB4] requires 10 days and starts at [DB3]'s end and is colored in #ffffff/#ff8d03

[Написание сервиса бизнес-логики] as [BL1] requires 14 days and starts at [DB2]'s end and is colored in #fce803/#9c8f05

[Конфигурация сервера авторизации] as [Sec1] requires 3 days and starts at [Back]'s start and is colored in #ff8d03/#804601
[Написание библиотеки для генерации бинов Spring Security] as [Sec2] requires 11 days and starts at [Sec1]'s end and is colored in #ff8d03/#804601
[Конфигурация Gateway] as [GW] requires 2 days and starts at [Sec2]'s end and is colored in #ff8d03/#804601
[Конфигурация Kafka] as [KFK] requires 8 days and starts at [GW]'s end and is colored in #ff8d03/#804601
[Модификация Core для взаимодействия его с Kafka] as [CoreKFK] requires 4 days and starts at [KFK]'s end and is colored in #ff8d03/#804601

[Роли в проекте] as [Roles] requires 7 days
[Сырцов А.С.] requires 7 days and is colored in #fce803/#9c8f05
[Егорушкин Н.И.] requires 7 days and is colored in #ff8d03/#804601
[Коуров Н.А.] requires 7 days and is colored in #82c4d1/#1ed4f7
[Лазаренков Я.Д.] requires 7 days and is colored in #ffffff/#000000
[Мозговой штурм] requires 7 days and is colored in #ccde77/#596132
@endgantt