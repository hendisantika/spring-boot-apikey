# Spring Boot API Key Authentication

This project demonstrates how to implement API key authentication in a Spring Boot application. It includes features
like rate limiting, role-based access control, and API key management.

## Features

- API key-based authentication
- Role-based access control (ROLE_CLIENT, ROLE_ADMIN)
- Rate limiting for API requests
- API key management (creation, validation, retrieval)
- H2 in-memory database for development and testing
- Metrics for monitoring API usage
- Comprehensive test coverage

## Prerequisites

- Java 21 or higher
- Gradle

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/hendisantika/spring-boot-apikey.git
cd spring-boot-apikey
```

### Build the Project

```bash
./gradlew build
```

### Run the Application

```bash
./gradlew bootRun
```

The application will start on port 8080 by default.

## Database

This project uses H2 in-memory database for development and testing. The H2 console is enabled and can be accessed at:

```
http://localhost:8080/h2-console
```

Connection details:

- JDBC URL: `jdbc:h2:mem:apikeydb`
- Username: `yu71`
- Password: `53cret`

## API Endpoints

### Public Endpoints

- No authentication required for endpoints starting with `/public/`

### Admin Endpoints

- `GET /admin/api-keys` - Get all API keys (requires ROLE_ADMIN)
- `POST /admin/api-keys` - Create new API keys (requires ROLE_ADMIN)
- `GET /admin/monitoring/api-keys/usage` - Get API key usage statistics (requires ROLE_ADMIN)

### Protected Endpoints

- `GET /api-keys` - Get the current user's API key
- All other endpoints require a valid API key

## Using API Keys

To access protected endpoints, include the API key in the request header:

```
X-API-Key: your-api-key
```

## Creating API Keys

To create a new API key, send a POST request to `/admin/api-keys` with the following JSON body:

```json
{
  "name": "My API Key",
  "role": "ROLE_CLIENT",
  "rateLimit": 1000
}
```

## Running Tests

```bash
./gradlew test
```

## API Examples with curl

### Creating a New API Key (Admin)

```bash
curl -X POST http://localhost:8080/admin/api-keys \
  -H "Content-Type: application/json" \
  -d '{
    "name": "My API Key",
    "role": "ROLE_CLIENT",
    "rateLimit": 1000
  }'
```

Response:

```json
{
  "key": "generated-uuid-here",
  "name": "My API Key",
  "expiresAt": "2025-06-04T14:30:00",
  "role": "ROLE_CLIENT",
  "rateLimit": 1000
}
```

### Accessing a Protected Endpoint

```bash
curl http://localhost:8080/api/resource \
  -H "X-API-Key: your-api-key"
```

### Accessing a Public Endpoint

```bash
curl http://localhost:8080/public/info
```

### Getting All API Keys (Admin)

```bash
curl http://localhost:8080/admin/api-keys \
  -H "X-API-Key: admin-api-key"
```

Response:

```json
[
  {
    "key": "api-key-1",
    "name": "Client API Key",
    "expiresAt": "2025-06-04T14:30:00",
    "role": "ROLE_CLIENT",
    "rateLimit": 1000
  },
  {
    "key": "api-key-2",
    "name": "Admin API Key",
    "expiresAt": "2025-06-04T14:30:00",
    "role": "ROLE_ADMIN",
    "rateLimit": 5000
  }
]
```

### Getting Your API Key (User)

```bash
curl http://localhost:8080/api-keys \
  -H "X-API-Key: your-api-key"
```

Response:

```json
{
  "key": "your-api-key",
  "name": "My API Key",
  "expiresAt": "2025-06-04T14:30:00",
  "role": "ROLE_CLIENT",
  "rateLimit": 1000
}
```

### Getting API Key Usage Statistics (Admin)

```bash
curl http://localhost:8080/admin/monitoring/api-keys/usage \
  -H "X-API-Key: admin-api-key"
```

Response:

```json
[
  {
    "name": "My API Key",
    "requests": 42,
    "rateLimit": 1000
  }
]
```

## Metrics

Metrics are available at:

```
http://localhost:8080/actuator/metrics
```

## Project Structure

The project follows a standard Spring Boot application structure:

```
src/main/kotlin/id/my/hendisantika/apikey/
├── controller/           # REST controllers
├── entity/               # JPA entities
├── exception/            # Custom exceptions
├── repository/           # Spring Data JPA repositories
├── security/             # Security configuration and filters
├── service/              # Business logic
└── SpringBootApikeyApplication.kt  # Main application class
```

### Key Components

- **ApiKeyAuthFilter**: Custom filter that intercepts requests and validates API keys
- **ApiKeyService**: Service for managing API keys, including validation and rate limiting
- **SecurityConfig**: Spring Security configuration
- **ApiKeyController**: Admin endpoints for API key management
- **UserApiKeyController**: User endpoints for API key retrieval

## Rate Limiting Implementation

This project implements a simple in-memory rate limiting mechanism:

- Each API key has a configurable rate limit (requests per hour)
- The `ApiKeyService` maintains a concurrent hash map to track request counts
- When a request is made, the service checks if the rate limit has been exceeded
- If the limit is exceeded, a `RateLimitExceededException` is thrown
- Rate limits are reset hourly

## Security Configuration

The application uses Spring Security with a custom authentication mechanism:

- Requests to `/public/**` endpoints are permitted without authentication
- Requests to `/admin/**` endpoints require `ROLE_ADMIN` authority
- All other endpoints require a valid API key
- Authentication is performed by the `ApiKeyAuthFilter`
- API keys are passed in the `X-API-Key` header

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Troubleshooting

### Common Issues

1. **401 Unauthorized**: Make sure you're including a valid API key in the `X-API-Key` header
2. **403 Forbidden**: The API key doesn't have the required role for the requested resource
3. **429 Too Many Requests**: The API key has exceeded its rate limit

## License

This project is licensed under the MIT License - see the LICENSE file for details.
