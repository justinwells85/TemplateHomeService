# Template Home Service

A production-ready Spring Boot microservice template with comprehensive observability, security, and deployment features.

## Features

### Core Technologies
- **Java 21** - Latest LTS version with modern language features
- **Spring Boot 3.3.4** - Enterprise-grade application framework
- **Maven** - Dependency management and build tool
- **PostgreSQL** - Production-ready relational database

### Database Management
- **Flyway** - Database version control and migrations
- **Hibernate/JPA** - ORM with optimized configuration
- **HikariCP** - High-performance connection pooling

### Testing
- **JUnit 5** - Modern testing framework
- **Testcontainers** - Integration testing with real PostgreSQL containers
- **MockMvc** - REST API testing
- **Mockito** - Unit testing with mocks

### API & Documentation
- **OpenAPI 3.0** - API specification
- **Swagger UI** - Interactive API documentation at `/swagger-ui.html`
- **API Docs** - JSON/YAML specification at `/api-docs`

### Observability
- **Spring Boot Actuator** - Production-ready endpoints
- **Prometheus Metrics** - Time-series metrics collection
- **OpenTelemetry** - Distributed tracing
- **Structured JSON Logging** - Logstash-encoded logs for ELK stack

### Security & Best Practices
- **Input Validation** - Bean Validation (JSR-380)
- **Exception Handling** - Global exception handler with proper HTTP status codes
- **Security Headers** - Production security configurations
- **Distroless Docker Image** - Minimal attack surface

### Container & Orchestration
- **Multi-stage Docker Build** - Optimized image size
- **Distroless Base Image** - Enhanced security
- **Kubernetes Helm Charts** - Production-ready deployment templates
- **Horizontal Pod Autoscaling** - Auto-scaling based on CPU/memory
- **Health Probes** - Liveness and readiness checks

### CI/CD
- **GitHub Actions** - Automated testing and deployment
- **Security Scanning** - OWASP dependency checks
- **Code Quality** - SonarCloud integration
- **Multi-environment Deployment** - Staging and production pipelines

## Quick Start

### Prerequisites
- Java 21
- Maven 3.9+
- Docker (for containerization)
- PostgreSQL (or use Testcontainers)

### Local Development

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-org/template-home-service.git
   cd template-home-service
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run with PostgreSQL**

   Option A: Use Docker Compose (recommended)
   ```bash
   docker-compose up -d postgres
   mvn spring-boot:run
   ```

   Option B: Use local PostgreSQL
   ```bash
   export DATABASE_URL=jdbc:postgresql://localhost:5432/templatedb
   export DATABASE_USERNAME=postgres
   export DATABASE_PASSWORD=postgres
   mvn spring-boot:run
   ```

4. **Access the application**
   - Application: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - API Docs: http://localhost:8080/api-docs
   - Health: http://localhost:8080/actuator/health
   - Metrics: http://localhost:8080/actuator/prometheus

### Running Tests

```bash
# Unit tests
mvn test

# Integration tests
mvn verify

# All tests with coverage
mvn clean verify
```

## Project Structure

```
template-home-service/
├── .github/
│   └── workflows/          # GitHub Actions CI/CD pipelines
├── helm/
│   └── template-home-service/  # Kubernetes Helm charts
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/templatehomeservice/
│   │   │       ├── config/          # Configuration classes
│   │   │       ├── controller/      # REST controllers
│   │   │       ├── exception/       # Exception handlers
│   │   │       ├── model/           # Domain models and DTOs
│   │   │       ├── repository/      # Data access layer
│   │   │       └── service/         # Business logic
│   │   └── resources/
│   │       ├── db/migration/        # Flyway SQL migrations
│   │       ├── application.yml      # Main configuration
│   │       ├── application-test.yml # Test configuration
│   │       └── logback-spring.xml   # Logging configuration
│   └── test/
│       └── java/
│           └── com/example/templatehomeservice/
│               ├── integration/     # Integration tests
│               └── service/         # Unit tests
├── Dockerfile                       # Multi-stage Docker build
├── pom.xml                         # Maven configuration
└── README.md
```

## API Endpoints

### User Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/users` | Get all users |
| GET | `/api/v1/users/{id}` | Get user by ID |
| GET | `/api/v1/users/username/{username}` | Get user by username |
| POST | `/api/v1/users` | Create a new user |
| PUT | `/api/v1/users/{id}` | Update a user |
| DELETE | `/api/v1/users/{id}` | Delete a user |

### Actuator Endpoints

| Endpoint | Description |
|----------|-------------|
| `/actuator/health` | Application health status |
| `/actuator/health/liveness` | Kubernetes liveness probe |
| `/actuator/health/readiness` | Kubernetes readiness probe |
| `/actuator/info` | Application information |
| `/actuator/prometheus` | Prometheus metrics |
| `/actuator/metrics` | Metrics information |

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DATABASE_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/templatedb` |
| `DATABASE_USERNAME` | Database username | `postgres` |
| `DATABASE_PASSWORD` | Database password | `postgres` |
| `SERVER_PORT` | Application port | `8080` |
| `ENVIRONMENT` | Environment name | `local` |
| `OTEL_EXPORTER_OTLP_ENDPOINT` | OpenTelemetry collector endpoint | `http://localhost:4317` |
| `JAVA_OPTS` | JVM options | `-XX:MaxRAMPercentage=75.0` |

### Database Migrations

Flyway migrations are located in `src/main/resources/db/migration/`. Migrations are applied automatically on startup.

To create a new migration:
1. Create a new file: `V{version}__{description}.sql` (e.g., `V2__add_user_roles.sql`)
2. Write your SQL DDL/DML
3. Restart the application

## Docker

### Build Image

```bash
docker build -t template-home-service:latest .
```

### Run Container

```bash
docker run -d \
  -p 8080:8080 \
  -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/templatedb \
  -e DATABASE_USERNAME=postgres \
  -e DATABASE_PASSWORD=postgres \
  template-home-service:latest
```

### Docker Compose

```bash
docker-compose up -d
```

## Kubernetes Deployment

### Deploy with Helm

```bash
# Install
helm install template-home-service ./helm/template-home-service \
  --namespace production \
  --create-namespace

# Upgrade
helm upgrade template-home-service ./helm/template-home-service \
  --namespace production

# Uninstall
helm uninstall template-home-service --namespace production
```

### Custom Values

Create a `values-production.yaml`:

```yaml
replicaCount: 3

image:
  repository: ghcr.io/your-org/template-home-service
  tag: "1.0.0"

resources:
  limits:
    cpu: 2000m
    memory: 2Gi
  requests:
    cpu: 1000m
    memory: 1Gi

ingress:
  enabled: true
  className: nginx
  hosts:
    - host: api.example.com
      paths:
        - path: /
          pathType: Prefix
```

Deploy:
```bash
helm install template-home-service ./helm/template-home-service \
  -f values-production.yaml \
  --namespace production
```

## Monitoring & Observability

### Prometheus Metrics

The service exposes Prometheus metrics at `/actuator/prometheus`. Configure Prometheus to scrape:

```yaml
scrape_configs:
  - job_name: 'template-home-service'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['template-home-service:8080']
```

### OpenTelemetry Tracing

Configure the OpenTelemetry collector endpoint:

```yaml
otel:
  exporter:
    otlp:
      endpoint: http://otel-collector:4317
```

### Structured Logging

Logs are output in JSON format (production) or plain text (development). Configure via Spring profiles:

- `prod`, `staging`: JSON logs
- `dev`, `local`, `test`: Plain text logs

Example JSON log:
```json
{
  "timestamp": "2025-11-29T10:15:30.123Z",
  "level": "INFO",
  "logger": "com.example.templatehomeservice.service.UserService",
  "message": "User created successfully with id: 123",
  "application": "template-home-service",
  "environment": "production",
  "traceId": "abc123",
  "spanId": "def456"
}
```

## CI/CD Pipeline

### Workflows

1. **CI (ci.yml)** - Runs on every push and PR
   - Unit tests
   - Integration tests
   - Security scanning
   - Code quality analysis

2. **CD (cd.yml)** - Runs on main branch and tags
   - Build Docker image
   - Push to container registry
   - Deploy to staging (main branch)
   - Deploy to production (tags)

3. **PR Checks (pr-checks.yml)** - Runs on PRs
   - Code formatting
   - Static analysis
   - Docker build test
   - Helm chart linting

### Required Secrets

Configure these secrets in GitHub:
- `SONAR_TOKEN` - SonarCloud token
- `KUBE_CONFIG_STAGING` - Kubernetes config for staging
- `KUBE_CONFIG_PRODUCTION` - Kubernetes config for production

## Development

### Adding a New Entity

1. Create the entity class in `model/`
2. Create the repository interface in `repository/`
3. Create the service class in `service/`
4. Create the controller in `controller/`
5. Create DTOs in `model/dto/`
6. Add Flyway migration in `db/migration/`
7. Write tests in `test/`

### Code Style

Follow standard Java conventions:
- Use Lombok to reduce boilerplate
- Prefer constructor injection over field injection
- Use meaningful variable names
- Add JavaDoc for public APIs
- Keep methods small and focused

## Troubleshooting

### Application won't start

Check database connectivity:
```bash
psql -h localhost -U postgres -d templatedb
```

### Tests failing

Ensure Docker is running (for Testcontainers):
```bash
docker ps
```

### Out of Memory

Adjust JVM settings:
```bash
export JAVA_OPTS="-Xmx1g -XX:MaxRAMPercentage=75.0"
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.

## Support

For issues and questions:
- Create an issue in GitHub
- Contact: support@example.com
- Documentation: https://docs.example.com

## Acknowledgments

- Spring Boot Team
- Testcontainers Project
- OpenTelemetry Community
