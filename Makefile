.PHONY: help build test run clean docker-build docker-run docker-stop install start logs

# Default target
.DEFAULT_GOAL := help

help: ## Display this help message
	@echo "Available targets:"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-20s\033[0m %s\n", $$1, $$2}'

install: ## Install dependencies (Maven wrapper download)
	./mvnw -v

build: ## Build the application
	./mvnw clean package -DskipTests

build-with-tests: ## Build the application with tests
	./mvnw clean install

test: ## Run unit tests
	./mvnw test

test-integration: ## Run integration tests
	./mvnw verify

test-all: ## Run all tests (unit + integration)
	./mvnw clean verify

run: ## Run the application locally
	./mvnw spring-boot:run

clean: ## Clean build artifacts
	./mvnw clean

format: ## Format code (if you add spotless plugin)
	./mvnw spotless:apply

lint: ## Check code style (if you add checkstyle plugin)
	./mvnw checkstyle:check

# Docker targets
docker-build: ## Build Docker image
	docker build -t template-home-service:latest .

docker-run: ## Run Docker container
	docker run -d -p 8080:8080 --name template-home-service template-home-service:latest

docker-stop: ## Stop and remove Docker container
	docker stop template-home-service || true
	docker rm template-home-service || true

# Docker Compose targets
start: ## Start all services (PostgreSQL, app, monitoring)
	docker-compose up -d

stop: ## Stop all services
	docker-compose down

restart: ## Restart all services
	docker-compose restart

logs: ## View logs from all services
	docker-compose logs -f

logs-app: ## View application logs only
	docker-compose logs -f app

start-db: ## Start PostgreSQL only
	docker-compose up -d postgres

start-monitoring: ## Start with monitoring stack (Prometheus, Grafana)
	docker-compose --profile monitoring up -d

# Development targets
dev-setup: install start-db ## Setup development environment
	@echo "Development environment ready!"
	@echo "Run 'make run' to start the application"

dev-clean: stop clean ## Clean development environment
	docker-compose down -v
	@echo "Development environment cleaned"

# Kubernetes/Helm targets
helm-install: ## Install with Helm
	helm install template-home-service ./helm/template-home-service \
		--namespace dev --create-namespace

helm-upgrade: ## Upgrade Helm release
	helm upgrade template-home-service ./helm/template-home-service \
		--namespace dev

helm-uninstall: ## Uninstall Helm release
	helm uninstall template-home-service --namespace dev

helm-lint: ## Lint Helm charts
	helm lint ./helm/template-home-service

# Utility targets
db-console: ## Connect to PostgreSQL console
	docker-compose exec postgres psql -U postgres -d templatedb

api-docs: ## Open API documentation in browser
	@echo "Opening http://localhost:8080/swagger-ui.html"
	@open http://localhost:8080/swagger-ui.html || xdg-open http://localhost:8080/swagger-ui.html || echo "Please open http://localhost:8080/swagger-ui.html in your browser"

health-check: ## Check application health
	@curl -s http://localhost:8080/actuator/health | jq . || echo "Application is not running or jq is not installed"

metrics: ## View Prometheus metrics
	@curl -s http://localhost:8080/actuator/prometheus | head -20
