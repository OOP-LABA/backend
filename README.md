# Studylance — Backend (Spring Boot)

Backend для учебной фриланс‑платформы: студенты публикуют задания, исполнители оставляют отклики, заказчик выбирает исполнителя, ведётся упрощённый учёт “залога”, есть профили/резюме, рейтинги/отзывы и админ‑модерация.

Важно: **верификации по email и мессенджера нет** (осознанно не реализуем).

## Технологии

- Java 17
- Spring Boot (Web, Security, Data JPA)
- PostgreSQL
- Liquibase (миграции и сиды)
- JWT (Bearer Token)
- Swagger UI (springdoc-openapi)

## Требования

- Java 17+
- PostgreSQL 14+ (желательно)

## Быстрый запуск локально

### 1) PostgreSQL

По умолчанию backend ожидает:

- DB: `studylance_db`
- User: `postgres`
- Password: `postgres`

Создай БД:

```sql
CREATE DATABASE studylance_db;
```

Если креды другие — поменяй их в `src/main/resources/application.yaml` или передай через env‑переменные (см. ниже).

### 2) Запуск приложения

```bash
cd backend
./mvnw spring-boot:run
```

API будет доступно на:

- `http://localhost:8080/api/v1`

Swagger UI:

- `http://localhost:8080/api/v1/swagger-ui/index.html`

При старте Liquibase автоматически создаст таблицы и применит миграции/сид‑данные.

## Конфигурация

Основные настройки в `src/main/resources/application.yaml`:

- `server.servlet.context-path: /api/v1`
- `spring.datasource.*` — подключение к PostgreSQL
- `jwt.secret`, `jwt.lifetime` — JWT
- `spring.servlet.multipart.*` — лимиты загрузки файлов (attachment)

### Переопределение через env (удобно для dev)

Пример запуска с другими параметрами БД:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/studylance_db \
SPRING_DATASOURCE_USERNAME=postgres \
SPRING_DATASOURCE_PASSWORD=postgres \
./mvnw spring-boot:run
```

## Аутентификация

- `POST /auth/register` — регистрация, возвращает JWT
- `POST /auth/login` — вход, возвращает JWT

Дальше передавай токен в заголовке:

```
Authorization: Bearer <token>
```

## Основные эндпоинты (кратко)

### Задачи (posts)

- `GET /posts` — список + фильтрация (`search`, `category`, `minGoal`, `maxGoal`, `sort`)
- `GET /posts/{id}` — детальная карточка
- `GET /posts/{id}/avatar` — отдача картинки‑вложения (хранится в БД)
- `POST /posts` — создание задачи:
  - `multipart/form-data` (рекомендуется): поля `title`, `content`, `category`, `goal`, `accountDetails`, файл `image`
  - также поддерживается `application/json` без файла
- `PUT /posts/{id}` — редактирование (owner/admin, owner только для `OPEN`)
- `DELETE /posts/{id}` — удаление (owner/admin, owner только для `OPEN`)
- `PATCH /posts/{id}/status` — смена статуса (`OPEN/IN_PROGRESS/DONE/CANCELLED`)

### Отклики (comments как offers)

- `POST /posts/{id}/comments` — оставить “offer”
- `POST /posts/{postId}/comments/{commentId}/accept` — принять оффер (назначает исполнителя, переводит в `IN_PROGRESS`, фиксирует deposit)

### Профили/резюме

- `GET /profiles/me` — мой профиль (auth)
- `PUT /profiles/me` — обновить профиль (auth)
- `GET /profiles/{username}` — публичный профиль
- `GET /profiles/{username}/reviews` — отзывы

### Отзывы

- `POST /posts/{id}/reviews` — оставить отзыв (после `DONE`)

### Жалобы и модерация

- `POST /complaints` — создать жалобу (auth)

### Admin (ROLE_ADMIN)

- `GET /admin/users`
- `POST /admin/users/{username}/ban`
- `POST /admin/users/{username}/unban`
- `GET /admin/complaints`
- `POST /admin/complaints/{id}/resolve`
- `DELETE /admin/posts/{id}` — удалить задачу (модерация)

## Subject / Categories

Категории (`categories`) не ограничены двумя сидовыми значениями.

При создании/редактировании задачи, если указанный `category` отсутствует — он **создаётся автоматически** (get‑or‑create).

## Как стать админом (через SQL)

По умолчанию регистрация выдаёт `ROLE_USER`.

Чтобы сделать пользователя админом:

```sql
INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'ROLE_ADMIN'
WHERE u.username = 'User1'
ON CONFLICT DO NOTHING;
```

После этого нужно перелогиниться, чтобы новый JWT содержал `ROLE_ADMIN`.

