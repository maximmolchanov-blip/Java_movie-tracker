# 🎬 Movie Tracker — Spring Boot App

Повноцінний застосунок для відстеження фільмів з інтеграцією TMDB API.

## Технологічний стек
- **Backend**: Java 17 + Spring Boot 3.2
- **Database**: H2 (in-memory, не потребує установки)
- **Frontend**: Thymeleaf + CSS
- **API**: TMDB (The Movie Database)
- **ORM**: Spring Data JPA

## Структура БД
- `movies` — фільми
- `genres` — жанри
- `movie_genres` — Many-to-Many зв'язок між фільмами і жанрами

## Швидкий старт

### 1. Отримай TMDB API ключ (безкоштовно)
1. Перейди на https://www.themoviedb.org/
2. Зареєструйся → Settings → API → Create (Developer)
3. Скопіюй API Key (v3 auth)

### 2. Вкажи ключ у конфігурації
Відкрий `src/main/resources/application.properties` і замін:
```
tmdb.api.key=YOUR_TMDB_API_KEY_HERE
```
на свій реальний ключ.

### 3. Запусти проект
```bash
./mvnw spring-boot:run
```
або в IDE просто запусти `MovieTrackerApplication.java`

### 4. Відкрий браузер
- **Головна сторінка**: http://localhost:8080
- **H2 Console (БД)**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:moviedb`
  - Username: `sa`
  - Password: (порожнє)

## REST API Endpoints

| Метод | URL | Опис |
|-------|-----|------|
| GET | `/api/movies` | Всі фільми |
| GET | `/api/movies?search=Batman` | Пошук за назвою |
| GET | `/api/movies?status=WATCHED` | Фільтр за статусом |
| GET | `/api/movies?genre=Action` | Фільтр за жанром |
| GET | `/api/movies/{id}` | Деталі фільму |
| POST | `/api/movies` | Створити фільм |
| PUT | `/api/movies/{id}` | Оновити фільм |
| PATCH | `/api/movies/{id}/status` | Змінити статус |
| DELETE | `/api/movies/{id}` | Видалити фільм |
| POST | `/api/movies/import/popular` | Імпорт популярних з TMDB |
| POST | `/api/movies/import/top-rated` | Імпорт топ-рейтингу з TMDB |
| GET | `/api/movies/genres` | Список жанрів |
| GET | `/api/movies/stats` | Статистика |

## Статуси перегляду
- `WANT_TO_WATCH` — Хочу подивитись
- `WATCHING` — Дивлюсь
- `WATCHED` — Переглянуто
- `DROPPED` — Закинуто

## Функціонал
✅ Імпорт фільмів з TMDB (популярні + топ-рейтинг)  
✅ Фільтрація за жанром, статусом, пошук за назвою  
✅ Зміна статусу перегляду  
✅ Видалення фільмів  
✅ Статистика на головній сторінці  
✅ REST API для всіх операцій  
✅ DTO шар  
✅ Зв'язані таблиці (Many-to-Many)  
