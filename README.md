# Spring Boot Base Project

A comprehensive Spring Boot base project with JWT authentication, OAuth2, RBAC (Role-Based Access Control), and Swagger documentation. This project serves as a solid foundation for building enterprise-grade Spring applications.

## 🚀 Features

- **Spring Boot 4.0.6** with Java 17
- **Spring Security** with JWT authentication
- **OAuth2** integration (Google, GitHub)
- **RBAC** (Role-Based Access Control) system
- **Swagger/OpenAPI 3** documentation
- **H2 Database** for development (MySQL support included)
- **JPA/Hibernate** for data persistence
- **Global exception handling**
- **CORS** configuration
- **Docker ready** (configuration included)
- **Rate limiting** and API throttling
- **Comprehensive logging** with audit trails
- **Email service** for notifications
- **Health checks** and monitoring endpoints
- **Redis caching** support
- **Custom validators** for data validation
- **API versioning** strategy
- **Profile-based configurations** (dev, test, prod, docker)
- **Comprehensive testing** (unit and integration tests)

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Git

## 🛠️ Quick Start

### 1. Clone the Repository

```bash
git clone <repository-url>
cd basePeoject
```

### 2. Build and Run

```bash
# Using Maven Wrapper
./mvnw clean install
./mvnw spring-boot:run

# Or using Maven
mvn clean install
mvn spring-boot:run
```

### 3. Access the Application

- **Application URL**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console
- **API Docs**: http://localhost:8080/api-docs

## 🔐 Authentication & Authorization

### Default Credentials

- **Username**: `admin`
- **Password**: `admin123`
- **Role**: `ADMIN`

### JWT Authentication

The application uses JWT tokens for authentication. Include the token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

### OAuth2 Integration

Configure OAuth2 providers by setting the following environment variables:

```bash
# Google OAuth2
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret

# GitHub OAuth2
GITHUB_CLIENT_ID=your-github-client-id
GITHUB_CLIENT_SECRET=your-github-client-secret
```

## 📚 API Endpoints

### Authentication

| Method | Endpoint | Description | Public |
|--------|----------|-------------|--------|
| POST | `/api/auth/signin` | User login | ✅ |
| POST | `/api/auth/signup` | User registration | ✅ |
| POST | `/api/auth/refresh` | Refresh JWT token | ✅ |
| GET | `/api/auth/me` | Get current user | ❌ |

### User Management

| Method | Endpoint | Description | Required Role |
|--------|----------|-------------|---------------|
| GET | `/api/users` | Get all users | ADMIN |
| GET | `/api/users/{id}` | Get user by ID | ADMIN/OWNER |
| GET | `/api/users/username/{username}` | Get user by username | ADMIN |
| GET | `/api/users/role/{roleName}` | Get users by role | ADMIN |
| DELETE | `/api/users/{id}` | Delete user | ADMIN |

### Health Check & Monitoring

| Method | Endpoint | Description | Public |
|--------|----------|-------------|--------|
| GET | `/api/health` | Basic health check | ✅ |
| GET | `/api/health/detailed` | Detailed health information | ❌ |
| GET | `/api/health/readiness` | Kubernetes readiness probe | ✅ |
| GET | `/api/health/liveness` | Kubernetes liveness probe | ✅ |
| GET | `/api/health/metrics` | Application metrics | ❌ |

### OAuth2

| Method | Endpoint | Description | Public |
|--------|----------|-------------|--------|
| GET | `/oauth2/user` | Get OAuth2 user info | ❌ |
| GET | `/oauth2/success` | OAuth2 success callback | ✅ |
| GET | `/oauth2/failure` | OAuth2 failure callback | ✅ |

## 🏗️ Project Structure

```
src/main/java/org/averdev/basepeoject/
├── common/                 # Common utilities and shared components
│   ├── response/           # Standardized API responses
│   │   ├── ApiResponse.java
│   │   └── PagedResponse.java
│   ├── constants/          # Application constants
│   │   └── ApplicationConstants.java
│   ├── enums/              # Application enums
│   │   ├── UserRole.java
│   │   └── Permission.java
│   ├── interfaces/         # Common interfaces
│   │   ├── Auditable.java
│   │   └── BaseService.java
│   ├── audit/              # Audit and base entities
│   │   ├── BaseEntity.java
│   │   └── JpaAuditConfig.java
│   └── logging/            # Logging components
│       ├── RequestLoggingFilter.java
│       └── CorrelationIdFilter.java
├── config/                 # Configuration classes
│   ├── SecurityConfig.java
│   ├── CacheConfig.java
│   ├── SwaggerConfig.java
│   ├── ApiVersionConfig.java
│   └── RateLimitConfig.java
├── security/               # Security components
│   ├── JwtTokenProvider.java
│   ├── JwtAuthenticationFilter.java
│   └── CustomUserDetailsService.java
├── exception/              # Exception handling
│   ├── GlobalExceptionHandler.java
│   ├── ErrorResponse.java
│   ├── ResourceNotFoundException.java
│   └── ResourceAlreadyExistsException.java
├── aspect/                 # AOP aspects
│   ├── AuditLogAspect.java
│   ├── RateLimitAspect.java
│   ├── RateLimitFilter.java
│   └── RateLimitService.java
├── annotation/             # Custom annotations
│   └── RateLimit.java
├── validation/             # Custom validators
│   ├── annotation/
│   │   ├── StrongPassword.java
│   │   ├── UniqueEmail.java
│   │   └── UniqueUsername.java
│   └── validator/
│       ├── StrongPasswordValidator.java
│       ├── UniqueEmailValidator.java
│       └── UniqueUsernameValidator.java
├── controller/             # REST controllers
│   ├── AuthController.java
│   ├── UserController.java
│   ├── OAuth2Controller.java
│   └── HealthController.java
├── dto/                    # Data Transfer Objects
│   ├── LoginRequest.java
│   ├── SignUpRequest.java
│   └── JwtAuthenticationResponse.java
├── entity/                 # JPA entities (extend BaseEntity)
│   ├── User.java
│   ├── Role.java
│   └── Permission.java
├── repository/             # JPA repositories
│   ├── UserRepository.java
│   ├── RoleRepository.java
│   └── PermissionRepository.java
└── service/                # Business logic
    ├── AuthService.java
    ├── UserService.java
    └── EmailService.java
└── BasePeojectApplication.java
```

## 🔧 Configuration

### Database Configuration

The application uses H2 database by default. To switch to MySQL, update `application.properties`:

```properties
# MySQL Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/your_database
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
```

### JWT Configuration

```properties
app.jwt.secret=your-secret-key
app.jwt.expiration=86400000  # 24 hours
app.jwt.refresh-expiration=604800000  # 7 days
```

### CORS Configuration

```properties
spring.web.cors.allowed-origins=http://localhost:3000,http://localhost:4200
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*
spring.web.cors.allow-credentials=true
```

## 🎯 Usage Examples

### 1. User Registration

```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "password123",
    "roles": ["USER"]
  }'
```

### 2. User Login

```bash
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "john_doe",
    "password": "password123"
  }'
```

### 3. Access Protected Endpoint

```bash
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer <your-jwt-token>"
```

## 🔒 Security Features

- **JWT-based authentication** with refresh tokens
- **Role-based access control** (RBAC)
- **OAuth2 integration** for social login
- **Password encryption** using BCrypt
- **CORS configuration** for cross-origin requests
- **Global exception handling** for security-related errors
- **Rate limiting** to prevent API abuse
- **Custom validators** for data integrity

## 🚀 Advanced Features

### Rate Limiting
- **Global rate limiting**: 60 requests/minute, 1000/hour, 10000/day
- **Custom rate limiting**: Use `@RateLimit` annotation on methods
- **Redis-based storage** for distributed rate limiting

### Audit Logging
- **Comprehensive API logging** with user context
- **Request/response tracking** with timing information
- **Separate audit logs** for security events
- **Profile-based log levels** (dev, test, prod)

### Caching
- **Redis integration** for distributed caching
- **Configurable TTL** for different cache types
- **Cache warming** strategies
- **Cache statistics** and monitoring

### Email Service
- **Welcome emails** for new users
- **Password reset notifications**
- **Account security alerts**
- **Template-based emails** with Thymeleaf

### Health Checks
- **Kubernetes-ready** probes (readiness/liveness)
- **Database connectivity** monitoring
- **System metrics** collection
- **Detailed health** information for admins

### Custom Validation
- **Strong password validation** with regex patterns
- **Unique username/email** validation
- **Custom constraint annotations**
- **Internationalization** support

## 🐳 Docker Support

The project includes comprehensive Docker configuration:

```bash
# Build and run with Docker Compose
docker-compose up -d

# Build individual image
docker build -t spring-base-project .

# Run standalone container
docker run -p 8080:8080 spring-base-project
```

### Docker Compose Services
- **app**: Spring Boot application
- **mysql**: MySQL database
- **redis**: Redis cache
- **nginx**: Reverse proxy (optional)

## 🧪 Testing

The project includes comprehensive test coverage:

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=AuthControllerTest

# Run with coverage
./mvnw test jacoco:report
```

### Test Categories
- **Unit Tests**: Service and utility classes
- **Integration Tests**: Full API flows
- **Security Tests**: Authentication and authorization
- **Validation Tests**: Custom validators

## 📊 Monitoring & Observability

### Actuator Endpoints
- **Health**: `/actuator/health`
- **Info**: `/actuator/info`
- **Metrics**: `/actuator/metrics`
- **Custom Health**: `/api/health/*`

### Logging Configuration
- **Profile-based** log levels
- **Separate audit logs**
- **Structured logging** with JSON format
- **Log rotation** policies

## 🔧 Environment Profiles

### Development (`dev`)
- H2 in-memory database
- Verbose logging enabled
- Swagger UI available
- Hot reload support

### Test (`test`)
- H2 database with test data
- Minimal logging
- Disabled Swagger
- Fast startup

### Production (`prod`)
- MySQL database
- Optimized logging
- Disabled Swagger
- Security hardening

### Docker (`docker`)
- Container-optimized
- External services (MySQL, Redis)
- Structured logging
- Health checks enabled

## 📈 Performance Features

- **Connection pooling** with HikariCP
- **Redis caching** for frequently accessed data
- **Batch processing** for database operations
- **Lazy loading** for JPA entities
- **Rate limiting** to prevent abuse
- **Async processing** capabilities

## 🛡️ Security Hardening

- **JWT token expiration** management
- **Password strength** validation
- **Rate limiting** per IP/user
- **CORS restrictions** in production
- **SQL injection** prevention with JPA
- **XSS protection** with proper encoding
- **Audit logging** for security events

## 🧪 Testing

Run the test suite:

```bash
./mvnw test
```

## 📝 Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `GOOGLE_CLIENT_ID` | Google OAuth2 client ID | - |
| `GOOGLE_CLIENT_SECRET` | Google OAuth2 client secret | - |
| `GITHUB_CLIENT_ID` | GitHub OAuth2 client ID | - |
| `GITHUB_CLIENT_SECRET` | GitHub OAuth2 client secret | - |
| `OAUTH2_ISSUER_URI` | OAuth2 issuer URI | https://accounts.google.com |

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

If you encounter any issues or have questions, please:

1. Check the [Swagger documentation](http://localhost:8080/swagger-ui.html)
2. Review the application logs
3. Create an issue in the repository

## 🔄 Version History

- **v1.0.0** - Initial release with JWT, OAuth2, RBAC, and Swagger

---

**Note**: This is a base project designed to be extended. Feel free to customize and adapt it to your specific requirements.
# baseSpringProject
