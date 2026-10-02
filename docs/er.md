# ER-диаграмма

Логическая модель данных экосистемы. Физически таблицы разъедутся по базам микросервисов: пользователь живёт в базе авторизации, состояние устройства — в базе его сервиса, телеметрия и записи камер хранятся отдельно.

```puml
@startuml
hide circle
skinparam linetype ortho
skinparam backgroundColor #FFFFFF

entity "User" as user {
  * id : uuid <<PK>>
  --
  email : varchar
  password_hash : varchar
  created_at : timestamptz
}

entity "House" as house {
  * id : uuid <<PK>>
  --
  * user_id : uuid <<FK>>
  name : varchar
  address : varchar
}

entity "Module" as module {
  * id : uuid <<PK>>
  --
  * house_id : uuid <<FK>>
  code : varchar
  name : varchar
}

entity "DeviceType" as device_type {
  * id : uuid <<PK>>
  --
  code : varchar
  name : varchar
}

entity "Device" as device {
  * id : uuid <<PK>>
  --
  * type_id : uuid <<FK>>
  * house_id : uuid <<FK>>
  * module_id : uuid <<FK>>
  serial_number : varchar
  name : varchar
  location : varchar
  status : varchar
}

entity "TelemetryData" as telemetry {
  * id : uuid <<PK>>
  --
  * device_id : uuid <<FK>>
  value : float
  unit : varchar
  recorded_at : timestamptz
}

entity "Recording" as recording {
  * id : uuid <<PK>>
  --
  * device_id : uuid <<FK>>
  object_key : varchar
  started_at : timestamptz
  ended_at : timestamptz
}

user ||--o{ house : владеет
house ||--o{ module : содержит
house ||--o{ device : содержит
module ||--o{ device : объединяет
device_type ||--o{ device : классифицирует
device ||--o{ telemetry : генерирует
device ||--o{ recording : пишет

@enduml
```
