# Kafka Integration - Multi-Instance Demo

## 🚀 Setup Instructions

### 1. Start Kafka & Zookeeper
```bash
docker-compose up -d
```

### 2. Start Server 1 (Port 8081)
Terminal 1:
```bash
./gradlew bootRun --args='--spring.profiles.active=server1'
```

### 3. Start Server 2 (Port 8082)
Terminal 2:
```bash
./gradlew bootRun --args='--spring.profiles.active=server2'
```

## 🧪 Testing (PowerShell examples)

### Create User on Server 1
```powershell
$response = Invoke-RestMethod -Uri http://localhost:8081/api/users -Method Post -Headers @{"Content-Type"="application/json"} -Body '{"name":"John Doe","email":"john@example.com"}'
$response
```

Expected:
- **Server 1 console:** `Publishing User Created Event: ... from server=8081`
- **Server 2 console:** `USER CREATED EVENT RECEIVED ON SERVER [8082]`

### Update User on Server 2
```powershell
$userId = $response.id
Invoke-RestMethod -Uri "http://localhost:8082/api/users/$userId" -Method Put -Headers @{"Content-Type"="application/json"} -Body '{"name":"Jane Doe","email":"jane@example.com"}'
```

Expected:
- **Server 2 console:** `Publishing User Updated Event: ... from server=8082`
- **Server 1 console:** `USER UPDATED EVENT RECEIVED ON SERVER [8081]`

### Delete User on Server 1
```powershell
Invoke-RestMethod -Uri "http://localhost:8081/api/users/$userId" -Method Delete
```

Expected:
- **Server 1 console:** `Publishing User Deleted Event: ... from server=8081`
- **Server 2 console:** `USER DELETED EVENT RECEIVED ON SERVER [8082]`

## CORS between 8081 and 8082

- Global CORS config (`CorsConfig`) allows browser pages on:
  - `http://localhost:8081`
  - `http://localhost:8082`
  - `http://localhost:5173`, `http://localhost:5174`
- to call any `/api/**` endpoint on either server.
- This means:
  - Swagger/UI on 8082 can call `http://localhost:8081/api/users` without CORS errors.
  - Swagger/UI on 8081 can call `http://localhost:8082/api/users` as well.
