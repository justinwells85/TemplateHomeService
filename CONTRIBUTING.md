# Contributing to Template Home Service

First off, thank you for considering contributing to Template Home Service! It's people like you that make this template better for everyone.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Workflow](#development-workflow)
- [Coding Standards](#coding-standards)
- [Commit Guidelines](#commit-guidelines)
- [Pull Request Process](#pull-request-process)
- [Testing Guidelines](#testing-guidelines)
- [Documentation](#documentation)

## Code of Conduct

This project adheres to a code of conduct. By participating, you are expected to uphold this code. Please be respectful and constructive in all interactions.

## Getting Started

### Prerequisites

- Java 21 or higher
- Docker and Docker Compose
- Git

### Setting Up Your Development Environment

1. **Fork the repository**
   ```bash
   # Fork via GitHub UI, then clone your fork
   git clone https://github.com/YOUR_USERNAME/TemplateHomeService.git
   cd TemplateHomeService
   ```

2. **Add upstream remote**
   ```bash
   git remote add upstream https://github.com/justinwells85/TemplateHomeService.git
   ```

3. **Start PostgreSQL**
   ```bash
   docker-compose up -d postgres
   ```

4. **Build and test**
   ```bash
   ./mvnw clean install
   ```

5. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

## Development Workflow

1. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make your changes**
   - Write clean, readable code
   - Add tests for new functionality
   - Update documentation as needed

3. **Test your changes**
   ```bash
   ./mvnw clean verify
   ```

4. **Commit your changes** (see [Commit Guidelines](#commit-guidelines))

5. **Push to your fork**
   ```bash
   git push origin feature/your-feature-name
   ```

6. **Create a Pull Request** via GitHub UI

## Coding Standards

### Java Code Style

- Follow standard Java conventions
- Use meaningful variable and method names
- Keep methods small and focused (< 20 lines when possible)
- Avoid deep nesting (max 3 levels)

### Lombok Usage

- Use `@Data`, `@Builder`, `@RequiredArgsConstructor` for data classes
- Prefer constructor injection over field injection
- Use `@Slf4j` for logging

### Example

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.debug("Fetching user by id: {}", id);
        return userRepository.findById(id)
                .map(UserResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
```

### Package Structure

```
com.example.templatehomeservice
├── config          # Configuration classes
├── controller      # REST controllers
├── exception       # Custom exceptions and handlers
├── model           # Domain models and DTOs
│   └── dto        # Data Transfer Objects
├── repository      # Data access layer
└── service         # Business logic
```

## Commit Guidelines

We follow [Conventional Commits](https://www.conventionalcommits.org/) specification.

### Commit Message Format

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types

- **feat**: A new feature
- **fix**: A bug fix
- **docs**: Documentation changes
- **style**: Code style changes (formatting, semicolons, etc.)
- **refactor**: Code refactoring
- **test**: Adding or updating tests
- **chore**: Maintenance tasks (dependencies, build config)
- **perf**: Performance improvements
- **ci**: CI/CD changes

### Examples

```
feat(user): add email validation to user creation

Add regex-based email validation when creating new users.
Includes integration tests for invalid email formats.

Closes #123
```

```
fix(database): resolve connection pool leak

Fixed HikariCP connection leak that occurred during
high-load scenarios. Increased max pool size to 20.

Fixes #456
```

## Pull Request Process

1. **Update documentation** if you've changed APIs or added features

2. **Ensure all tests pass**
   ```bash
   ./mvnw clean verify
   ```

3. **Update the README.md** with details of changes if applicable

4. **Follow the PR template** (will be auto-populated)

5. **Request review** from maintainers

6. **Address review feedback** promptly

### PR Title Format

Use the same format as commit messages:
```
feat(scope): description
```

### PR Description Should Include

- Summary of changes
- Motivation and context
- Related issues (Closes #123)
- Screenshots (if applicable)
- Checklist of completed tasks

## Testing Guidelines

### Unit Tests

- Test business logic in isolation
- Use Mockito for mocking dependencies
- Aim for >80% code coverage

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        // Arrange
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        UserResponse response = userService.getUserById(1L);

        // Assert
        assertThat(response.getId()).isEqualTo(1L);
        verify(userRepository, times(1)).findById(1L);
    }
}
```

### Integration Tests

- Use Testcontainers for database tests
- Extend `AbstractIntegrationTest` base class
- Test full request/response cycle

```java
class UserControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateUser() throws Exception {
        UserRequest request = new UserRequest("john", "john@example.com", "John", "Doe");

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is("john")));
    }
}
```

### Test Naming Convention

```
methodName_stateUnderTest_expectedBehavior
```

## Documentation

### Code Documentation

- Add JavaDoc for public APIs
- Document complex algorithms
- Explain "why" not "what" in comments

### API Documentation

- Use Swagger/OpenAPI annotations
- Provide example requests/responses
- Document error codes

```java
@Operation(summary = "Get user by ID", description = "Retrieve a specific user by their ID")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
    @ApiResponse(responseCode = "404", description = "User not found")
})
@GetMapping("/{id}")
public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
    return ResponseEntity.ok(userService.getUserById(id));
}
```

### README Updates

Update the README when you:
- Add new features
- Change configuration requirements
- Modify deployment procedures
- Add new environment variables

## Questions?

Feel free to:
- Open an issue for discussion
- Ask in pull request comments
- Contact maintainers directly

Thank you for contributing! 🎉
