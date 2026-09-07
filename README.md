# Thought Nest

Spring Boot API with a Vite + React TypeScript client in `frontend/`.

## Run locally

1. Copy `.env.example` to `.env` and set PostgreSQL plus a long random `JWT_SECRET`.
2. Start the API: `./mvnw spring-boot:run`.
3. In a second terminal, run `cd frontend && cp .env.example .env && npm install && npm run dev`.

### No database available?

Run `SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run` to launch a local in-memory H2 database. Registration works and its OTP is written to the backend console instead of being emailed. This profile is for development only; it does not persist data after the backend stops.

Set `FRONTEND_URL` to the deployed client origin in production. Never commit `.env` files or credentials.
