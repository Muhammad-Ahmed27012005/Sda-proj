# StreamFlixTv Setup Guide

## Requirements

- Java 25
- Maven or the included Maven wrapper
- MySQL 8+

## MySQL

Create a user/database or let the JDBC URL create the database automatically:

```sql
CREATE DATABASE IF NOT EXISTS streamflixtv;
```

Set credentials before running:

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="yourpassword"
```

## Run

```powershell
cd project
.\mvnw.cmd spring-boot:run
```

Open `http://localhost:8080`.

## Default Admin

The app seeds this account on startup:

- Email: `admin@streamflix.com`
- Password: `admin123`

## Add Movies

Place MP4 files in:

```text
C:\Users\Daver\Documents\GitHub\Sda-proj\project\upload\movies
```

Refresh the home page. The app automatically creates a library entry for each file and uses the filename as the movie title.

## Test Streaming with Postman

1. `POST http://localhost:8080/api/auth/login`
2. Body:

```json
{ "email": "admin@streamflix.com", "password": "admin123" }
```

3. Copy the `token` value if you want to test as an authenticated user. Public local playback also works without a token.
4. Send `GET http://localhost:8080/api/videos/stream/{id}` with headers:

```text
Authorization: Bearer <token>
Range: bytes=0-1048575
```

The response should be `206 Partial Content` with `Content-Type: video/mp4`.
