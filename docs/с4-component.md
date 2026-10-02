# Диаграмма компонентов

Отопление, освещение и ворота устроены одинаково. Отличается набор команд. Наблюдение, авторизация и интеграция устройств раскрыты отдельно.

## Отопление

```puml
@startuml
!include <C4/C4_Component>
skinparam backgroundColor #FFFFFF

Container(web, "Веб-приложение", "SPA", "Интерфейс умного дома")
Container(auth, "Авторизация", "Микросервис", "Проверка токена")
Container(adapter, "Интеграция устройств", "Адаптер протоколов", "Команды устройствам")
ContainerDb(heatingDb, "БД отопления", "PostgreSQL", "Состояние отопления")

Container_Boundary(heating, "Отопление") {
    Component(api, "API", "HTTP", "Принимает команду от веб-приложения")
    Component(token, "Проверка токена", "Клиент", "Проверяет токен в авторизации")
    Component(handler, "Обработчик команд", "Сервис", "Включить, выключить, задать температуру")
    Component(client, "Клиент интеграции", "Клиент", "Отправляет команду на устройства")
}

Rel(web, api, "Управляет", "HTTPS")
Rel(api, token, "Передаёт токен")
Rel(token, auth, "Проверяет токен", "HTTPS")
Rel(api, handler, "Команда")
Rel(handler, heatingDb, "Читает и пишет", "SQL")
Rel(handler, client, "Отправляет команду")
Rel(client, adapter, "Команда", "HTTPS")
@enduml
```

## Освещение

```puml
@startuml
!include <C4/C4_Component>
skinparam backgroundColor #FFFFFF

Container(web, "Веб-приложение", "SPA", "Интерфейс умного дома")
Container(auth, "Авторизация", "Микросервис", "Проверка токена")
Container(adapter, "Интеграция устройств", "Адаптер протоколов", "Команды устройствам")
ContainerDb(lightDb, "БД освещения", "PostgreSQL", "Состояние света")

Container_Boundary(light, "Освещение") {
    Component(api, "API", "HTTP", "Принимает команду от веб-приложения")
    Component(token, "Проверка токена", "Клиент", "Проверяет токен в авторизации")
    Component(handler, "Обработчик команд", "Сервис", "Включить или выключить свет")
    Component(client, "Клиент интеграции", "Клиент", "Отправляет команду на устройства")
}

Rel(web, api, "Управляет", "HTTPS")
Rel(api, token, "Передаёт токен")
Rel(token, auth, "Проверяет токен", "HTTPS")
Rel(api, handler, "Команда")
Rel(handler, lightDb, "Читает и пишет", "SQL")
Rel(handler, client, "Отправляет команду")
Rel(client, adapter, "Команда", "HTTPS")
@enduml
```

## Ворота

```puml
@startuml
!include <C4/C4_Component>
skinparam backgroundColor #FFFFFF

Container(web, "Веб-приложение", "SPA", "Интерфейс умного дома")
Container(auth, "Авторизация", "Микросервис", "Проверка токена")
Container(adapter, "Интеграция устройств", "Адаптер протоколов", "Команды устройствам")
ContainerDb(gatesDb, "БД ворот", "PostgreSQL", "Состояние ворот")

Container_Boundary(gates, "Ворота") {
    Component(api, "API", "HTTP", "Принимает команду от веб-приложения")
    Component(token, "Проверка токена", "Клиент", "Проверяет токен в авторизации")
    Component(handler, "Обработчик команд", "Сервис", "Открыть или закрыть ворота")
    Component(client, "Клиент интеграции", "Клиент", "Отправляет команду на устройства")
}

Rel(web, api, "Управляет", "HTTPS")
Rel(api, token, "Передаёт токен")
Rel(token, auth, "Проверяет токен", "HTTPS")
Rel(api, handler, "Команда")
Rel(handler, gatesDb, "Читает и пишет", "SQL")
Rel(handler, client, "Отправляет команду")
Rel(client, adapter, "Команда", "HTTPS")
@enduml
```

## Наблюдение

```puml
@startuml
!include <C4/C4_Component>
skinparam backgroundColor #FFFFFF

Container(web, "Веб-приложение", "SPA", "Интерфейс умного дома")
Container(auth, "Авторизация", "Микросервис", "Проверка токена")
ContainerDb(videoDb, "Хранилище записей", "Object storage", "Архив видео")
System_Ext(cameras, "Камеры", "Видеопоток")

Container_Boundary(video, "Наблюдение") {
    Component(api, "API", "HTTP", "Принимает запрос на просмотр")
    Component(token, "Проверка токена", "Клиент", "Проверяет токен в авторизации")
    Component(stream, "Видеопоток", "Сессия", "Отдаёт живое видео с камеры")
    Component(archive, "Архив", "Сервис", "Сохраняет и отдаёт записи")
}

Rel(web, api, "Смотрит", "HTTPS")
Rel(api, token, "Передаёт токен")
Rel(token, auth, "Проверяет токен", "HTTPS")
Rel(api, stream, "Запрос живого видео")
Rel(stream, cameras, "Видеопоток", "RTSP")
Rel(api, archive, "Запрос записи")
Rel(archive, videoDb, "Хранит записи")
@enduml
```

## Авторизация

```puml
@startuml
!include <C4/C4_Component>
skinparam backgroundColor #FFFFFF

Container(web, "Веб-приложение", "SPA", "Вход пользователя")
Container(heating, "Отопление", "Микросервис")
Container(light, "Освещение", "Микросервис")
Container(gates, "Ворота", "Микросервис")
Container(video, "Наблюдение", "Микросервис")
ContainerDb(authDb, "БД авторизации", "PostgreSQL", "Учётные записи")

Container_Boundary(auth, "Авторизация") {
    Component(api, "API", "HTTP", "Вход и проверка токена")
    Component(issuer, "Выдача токена", "Сервис", "Создаёт и проверяет токен")
    Component(users, "Учётные записи", "Репозиторий", "Ищет пользователя")
}

Rel(web, api, "Вход", "HTTPS")
Rel(heating, api, "Проверяет токен", "HTTPS")
Rel(light, api, "Проверяет токен", "HTTPS")
Rel(gates, api, "Проверяет токен", "HTTPS")
Rel(video, api, "Проверяет токен", "HTTPS")
Rel(api, issuer, "Запрос")
Rel(issuer, users, "Ищет пользователя")
Rel(users, authDb, "Читает и пишет", "SQL")
@enduml
```

## Интеграция устройств

```puml
@startuml
!include <C4/C4_Component>
skinparam backgroundColor #FFFFFF

Container(heating, "Отопление", "Микросервис")
Container(light, "Освещение", "Микросервис")
Container(gates, "Ворота", "Микросервис")
System_Ext(devices, "Датчики и реле", "Отопление, свет и ворота")

Container_Boundary(adapter, "Интеграция устройств") {
    Component(api, "API команд", "HTTP", "Принимает команду от сервиса")
    Component(translator, "Транслятор", "Сервис", "Переводит команду во внутреннюю модель")
    Component(mqtt, "Адаптер MQTT", "Протокол", "Отправляет команду устройству партнёра")
}

Rel(heating, api, "Команда", "HTTPS")
Rel(light, api, "Команда", "HTTPS")
Rel(gates, api, "Команда", "HTTPS")
Rel(api, translator, "Команда")
Rel(translator, mqtt, "Сообщение устройству")
Rel(mqtt, devices, "Протокол партнёра", "MQTT")
@enduml
```
