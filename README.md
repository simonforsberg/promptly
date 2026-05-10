
# Promptly

A Spring Boot middleware service that connects users to external AI APIs via OpenRouter, with features like selectable personalities and conversation memory.

Built with **Java 25**, **Spring Boot 4**, **Maven**, and **OpenRouter**.

![promptly-coder](promptly-coder.png)

---

## Features

- **Minimalistic Chat UI**: Simple web interface with markdown rendering and syntax-highlighted code blocks. Vibe-coded with Claude.
- **RESTful API**: Clean API endpoints for integration.
- **Session-based Memory**: Maintains conversation context using unique session IDs.
- **Selectable Features and Personalities**: Choose between different AI behaviors (**Assistant**, **Coder**, **Ron Burgundy**).
- **Resiliency**: Automatic retry with exponential backoff for network errors and rate limits.
- **Thread-safe**: Robust session handling using `ConcurrentHashMap`.
- **API Documentation**: Integrated OpenAPI/Swagger UI.

---

## Requirements

- **Java 25**
- **Maven**
- **OpenRouter API Key**: Obtain one for free at [openrouter.ai](https://openrouter.ai).

---

## Setup & Running

### 1. Clone the repository
```bash
git clone https://github.com/simonforsberg/promptly.git
cd promptly
```

### 2. Configure Environment Variables
The application requires an `AI_API_KEY` to communicate with OpenRouter.

**Terminal:**
```bash
export AI_API_KEY=your_api_key_here
```

**IntelliJ IDEA:**
1. Go to `Run` -> `Edit Configurations`.
2. Select `PromptlyApplication`.
3. Add `AI_API_KEY=your_api_key_here` to **Environment variables**.

### 3. Run the application
**Maven Wrapper:**
```bash
./mvnw spring-boot:run
```

**IDE:**
Run the `PromptlyApplication` class directly.

### 4. Access the application
- **Chat UI**: [http://localhost:8080](http://localhost:8080)
- **Swagger Documentation**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## Configuration

You can override default settings in `src/main/resources/application.properties` or via environment variables:

| Property       | Environment Variable | Description              | Default                        |
|----------------|----------------------|--------------------------|--------------------------------|
| `ai.base-url`  | `AI_BASE_URL`        | LLM provider base URL    | `https://openrouter.ai/api/v1` |
| `ai.api-key`   | `AI_API_KEY`         | OpenRouter API key       | *(required)*                   |
| `ai.model`     | `AI_MODEL`           | AI model to use          | `openai/gpt-4o-mini`           |

---

## Personalities

The `personality` field in the API request changes the system prompt:

| Key            | Role         | Behavior                                                         |
|----------------|--------------|------------------------------------------------------------------|
| `assistant`    | Assistant    | Helpful and supportive assistant, answers clearly and concisely. |
| `coder`        | Coder        | Software engineer and coding assistant, gives technical advice.  |
| `ron-burgundy` | Ron Burgundy | Chat with the legendary news anchor. Stay classy!                |

---

## Testing

The project includes unit and integration tests using **JUnit 5**, **Mockito**, and **MockMvc**.

### Running Tests
To execute all tests, run:
```bash
./mvnw test
```

### Coverage
- **Unit Tests**: Business logic, personality mapping, and conversation memory.
- **Integration Tests**: API controllers and exception handling.
- **Mocking**: Unit tests use **Mockito** to mock dependencies.
  External API calls in integration tests are stubbed using **WireMock**.

---

## Error Handling

Promptly uses a centralized exception handling mechanism with `GlobalExceptionHandler` to ensure consistent API responses.

### Error Response Format
All errors follow a standard structure:
```json
{
  "timestamp": "2026-05-10T12:45:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Detailed error message",
  "path": "/api/v1/chat"
}
```

### Common Status Codes
- **400 Bad Request**: Validation errors (e.g., missing message or personality).
- **503 Service Unavailable**: AI service connection issues, communication errors, or circuit breaker activation.
- **500 Internal Server Error**: Unexpected application errors.

### Resiliency
The service implements **automatic retries with exponential backoff** for transient network errors and rate limits (429 Too Many Requests) when communicating with the AI provider.

---

## API Reference

### Chat Endpoint
`POST /api/v1/chat`

**Request Body:**
```json
{
  "sessionId": "unique-session-id",
  "personality": "ron-burgundy",
  "message": "Who are you?"
}
```

**Success Response (200 OK):**
```json
{
  "sessionId": "unique-session-id",
  "reply": "I don't know how to tell you this, but I'm kind of a big deal."
}
```

**Error Response (400 Bad Request):**
```json
{
  "timestamp": "2026-05-08T17:25:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "message: must not be blank",
  "path": "/api/v1/chat"
}
```

**Error Response (503 Service Unavailable):**
Occurs when the AI service is unreachable, communication fails, or when the circuit breaker is open.
```json
{
  "timestamp": "2026-05-08T17:25:00Z",
  "status": 503,
  "error": "AI Service Unavailable",
  "message": "AI service is currently unavailable. Please try again later.",
  "path": "/api/v1/chat"
}
```