# Backend Restful API for WEB DGNL PROJECT using Java Spring Boot
## Technologies used:
- Java 23
- Spring Boot 3.4.2
- PostgreSQL 17
- Redis-server 7.4.2
## Project Structure - DGNL_BACKEND
### Root Directory
- `.mvn/` - Maven wrapper files.
- `.vscode/` - VS Code-specific configurations.
- `logs/` - Directory for storing log files.
- `target/` - Compiled output and build artifacts.

### Source Code (`src/`)
#### `src/main/java/com/dgnl_backend/project/dgnl_backend/`
- `configs/` - Configuration classes for the application.
- `controllers/` - Handles HTTP requests and defines API endpoints.
  - `auth/` - Authentication-related controllers.
- `dtos/` - Data Transfer Objects for request and response handling.
- `exceptions/` - Custom exception handling classes.
- `repositories/` - Interfaces for database interactions.
- `schemas/` - Entity definitions and database models.
- `security/` - Security configurations (authentication, authorization).
- `services/` - Business logic and service layer.
- `utils/` - Utility/helper functions.
- `DgnlBackendApplication.java` - Main Spring Boot application entry point.

#### `src/main/resources/`
- `META-INF/` - Metadata files for configuration.
- `static/` - Static assets like images, CSS, and JavaScript.
- `templates/` - HTML templates for rendering views.
- `application.properties` - Spring Boot configuration file.

### Test Directory
- `test/` - Unit and integration tests.

### Other Files
- `.gitignore` - Git ignore rules.
- `.gitattributes` - Git attributes configuration.
- `pom.xml` - Maven project dependencies and configurations.
- `README.md` - Project documentation.
- `rest.http` - API request testing file.
- `mvnw` / `mvnw.cmd` - Maven wrapper scripts.

## Endpoints
### `/api` - Public api for all users to call
#### `/user` - User-related endpoints

- `/{userId}`
  - **GET** : Get user info by user ID.
    - **Path Parameters**:
      - `userId` (string, required): The ID of the user.
    - **Response**:
      - **200 OK**: Returns user details.
      - **404 Not Found**: User not found.

---

- `/login`
  - **POST** : Login with username/email and password.
    - **Request Body**:
      ```json
      {
          "username": "string",
          "password": "string"
      }
      ```
    - **Responses**:
      - **200 OK** (Valid info, JWT token exists in database):
        ```json
        {
          "data": {
            "token": "string"
          },
          "message": "Login successful"
        }
        ```
      - **200 OK** (Valid info, JWT token not in database):
        ```json
        {
          "data": {
            "token": null
          },
          "message": "OTP sent to your email. Please verify to complete the login process."
        }
        ```
      - **404 Not Found** (User not found):
        ```json
        {
          "data": null,
          "message": "User not found"
        }
        ```
      - **401 Unauthorized** (Invalid password):
        ```json
        {
          "data": null,
          "message": "Wrong password"
        }
        ```
      - **409 Conflict** (User is disabled):
        ```json
        {
          "data": null,
          "message": "User is disabled"
        }
        ```

---

- `/register`
  - **POST** : Register a new user.
    - **Request Body**:
      ```json
      {
          "username": "string",
          "email": "string",
          "password": "string",
          "genderId": "int",
          "yob": "int",
          "mob": "int",
          "dob": "int",
          "gradeLv": "int",
          "roleId": "int"
      }
      ```
    - **Responses**:
      - **200 OK** (Successful registration):
        ```json
        {
          "data": null,
          "message": "User registered successfully. Please check your email to activate."
        }
        ```
        - The system then sends an activation email.
      - **400 Bad Request** (Username or email already exists):
        ```json
        {
          "data": null,
          "message": "Username or email already exists. Please try again."
        }
        ```

#### `/verification` - Verification-related endpoints

- `/otp/{username}`
  - **GET** : Verify an OTP for a given username.
    - **Query Parameters**:
      - `otp` (string, required): The one-time password to verify.
    - **Path Parameters**:
      - `username` (string, required): The username associated with the OTP.
    - **Response**:
      - **200 OK**:
        ```json
        {
          "data": {
            "token": "string"
          },
          "message": "Login successful"
        }
        ```
      - **400 Bad Request** (Invalid OTP):
        ```json
        {
          "data": null,
          "message": "Invalid OTP code"
        }
        ```
      - **400 Bad Request** (Expired OTP):
        ```json
        {
          "data": null,
          "message": "OTP is Expired"
        }
        ```
      - **404 Not Found** (User not found):
        ```json
        {
          "data": null,
          "message": "User not found"
        }
        ```

---

- `/account`
  - **GET** : Verify an account activation token.
    - **Query Parameters**:
      - `token` (string, required): The activation token sent via email.
    - **Response**:
      - **200 OK**:
        ```json
        {
          "data": null,
          "message": "Account verified successfully"
        }
        ```
      - **400 Bad Request** (Invalid token):
        ```json
        {
          "data": null,
          "message": "Invalid or Expired token"
        }
        ```
      - **404 Not Found** (User not found):
        ```json
        {
          "data": null,
          "message": "User not found"
        }
        ```

---

- `/email/account`
  - **GET** : Resend verification email.
    - **Query Parameters**:
      - `email` (string, required): The email address to resend the verification link.
    - **Response**:
      - **200 OK**:
        ```json
        {
          "data": null,
          "message": "Verification email sent successfully"
        }
        ```
      - **400 Bad Request** (Invalid email or already verified):
        ```json
        {
          "data": null,
          "message": "Email not registered or already verified"
        }
        ```


### `/auth` - Authenticated user endpoints
### `/teacher` - Teachers endpoint
### `/admin` - Admins endpoint
