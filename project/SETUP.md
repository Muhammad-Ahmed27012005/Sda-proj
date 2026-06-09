# StreamFlixTv Setup Guide

## Requirements

- Java 25
- Maven or the included Maven wrapper
- MySQL 8+
- A RapidAPI IMDb key exported as `RAPIDAPI_KEY`

## MySQL

Create a user/database or let the JDBC URL create the database automatically:

```sql
CREATE DATABASE IF NOT EXISTS streamflixtv;
```

Set credentials before running:

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="yourpassword"
$env:RAPIDAPI_KEY="your-rapidapi-key"
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

## Upload a Test Video

1. Log in as the admin.
2. Open `/admin/videos`.
3. Upload an MP4 file and optional thumbnail.
4. Subscribe a user account to a plan before streaming.

## Test Streaming with Postman

1. `POST http://localhost:8080/api/auth/login`
2. Body:

```json
{ "email": "admin@streamflix.com", "password": "admin123" }
```

3. Copy the `token` value.
4. Send `GET http://localhost:8080/api/videos/stream/{id}` with headers:

```text
Authorization: Bearer <token>
Range: bytes=0-1048575
```

The response should be `206 Partial Content` with `Content-Type: video/mp4`.
