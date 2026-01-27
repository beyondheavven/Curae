# Unit Testing Conventions and Rules

This document outlines the mandatory standards for writing unit tests across the entire application. All tests must adhere to these guidelines to ensure consistency, readability and maintainability.

---

## 1. Global Conventions

### 1.1 Framework & Tools

* **JUnit5** - the core testing framework.
* **Mockito** - for mocking dependencies (Services, Repositories)
* **AssertJ** - for fluent, readable assertions (`assertThat`)
* **Jacoco** - for code coverage reporting and quality gate.

### 1.2 Naming Conventions

Test names must be descriptive and self-documenting

**Class Names:** Must correspond to the class under test + `Test`

* `UserMapper` -> Test: `UserMapperTest`
* `UserController` -> Test: `UserControllerTest`

**Method Names:** Must follow the `should<ExpectedBehavior>When<Condition>`

* `shouldReturnUserResponseWhenCreateUserValid()`
* `shouldThrowExceptionWhenEmailIsDuplicate()`
* `shouldMapEntityToDto_WhenInputIsValid()`

### 1.3 Test Structure (AAA Pattern)

Every test must follow the Arrange-Act-Assert pattern.

1. **Arrange (Given):** Prepare data, fixtures and define Mock behavior.
2. **Act (When):** Call the actual method being tested.
3. **Assert (Then):** Verify the results (return values, exceptions or side effects).

**Example**:

```java
@Test
public void shouldDoSomething_whenConditionMet() {
    // Arrange
    User input = DataUtils.getJohnDoeTransient();
    given(service.process(input)).willReturn(true);

    // Act
    boolean result = controller.doSomething(input);

    // Assert
    assertThat(result).isTrue();
}
```

### 1.4 Assertions (AssertJ)

For all tests use `assertThat()` from **AssertJ**.

*Official Documentation:* [AssertJ Core Documentation](https://assertj.github.io/doc/)

**Common assertThat() examples**

```java
// Nullability Checks
assertThat(result).isNotNull();
assertThat(result).isNull();

// String Verification
assertThat(result.getName()).isEqualTo("John");
assertThat(result.getEmail()).contains("@gmail.com");

// Boolean Verification
assertThat(result.isActive()).isTrue();

// Collections Verification
assertThat(userList).hasSize(2);
assertThat(userList).extracting(User::getName).contains("John", "Mike");

// Exception verification
assertThatThrownBy(() -> service.process(null))
        .isInstanceOf(InvalidInputException.class)
        .hasMessage("Input can't be null");
```

---

## Controller Layer

This layer is responsible for handling HTTP requests, input validation, and mapping exceptions to HTTP status codes. We test in isolation using `@WebMvcTest`

### Frameworks & Annotations

* `@WebMvcTest(TargetController.class)`: Loads only the Web Layer (Controllers, Advice, Filters)
* `MockMvc`: Simulates HTTP requests without a real server
* `@MockitoBean`: Mocks the Service and Mapper dependencies

### Assertions

#### 1. Verify HTTP Status Codes

Check that the response status matches the expected outcome (200, 201, 400, 404, etc.).

```java
ResultActions result = mockMvc.perform(...);

result.andExpect(status().isOk()); // 200
result.andExpect(status().isBadRequest()); // 400
result.andExpect(status().isNotFound()); // 404
result.andExpect(status().isNoContent());
```

#### 2. Validate Response Body Fields

Ensure the JSON response contains the correct data structure and values.

```java
result
        .andExpect(jsonPath("$.id", is(1)))
        .

andExpect(jsonPath("$.email", is("john.doe@mail.com")))
        .

andExpect(jsonPath("$.firstName", is("John")))
```

#### 3. Check Headers (When Applicable)

If the endpoint returns specific headers, verify them.

```java
result.andExpect(header().

string("Location",containsString("/api/users/1")));
```

#### 4. Ensure Service Interactions

Verify that the service layer was called with the expected arguments. This is mandatory for `void` methods (like delete) and recommended for state-changing operations.

```java
verify(userService, times(1)).deleteUserById(1L);
```

### Core Scenarios to Cover

#### Positive Flow(Happy Path)

```java
@Test
public void shouldReturnResponseWhenCreateUserValid() throws Exception {
    UserCreationRequest request = DataUtils.getJohnDoeRequest();
    UserResponse response = DataUtils.getJohnDoeResponse();
    given(userMapper.toEntity(any(UserCreationRequest.class))).willReturn(DataUtils.getJohnDoeTransient());
    given(userService.createUser(any(User.class))).willReturn(DataUtils.getJohnDoePersisted());
    given(userMapper.toResponse(any(User.class))).willReturn(response);

    ResultActions result = mockMvc.perform(post(ApiPaths.USER_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));

    result.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email", is("john.doe@mail.com")));
}
```

#### Validation Errors (400 Bad Request)

Verify that invalid inputs trigger a validation failure and return `400 Bad Request`

```java
@Test
public void shouldReturn400WhenEmailIsInvalid() {
    UserCreationRequest invalidRequest = new UserCreationRequest("not-an-email", "John", "Doe", Role.PATIENT);

    ResultActions result = mockMvc.perform(post(ApiPaths.USER_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)));

    result.andDo(print())
            .andExpect(status().isBadRequest());
}
```

#### Exception Handling

Verify that Business Exceptions thrown by the Service layer are intercepted by the @ControllerAdvice and mapped to the correct HTTP status code.

**Scenario: Business Logic Error (400)**

```java
@Test
public void shouldReturn400WhenCreateUserWithDuplicateEmail() throws Exception {
    UserCreationRequest request = DataUtils.getJohnDoeRequest();
    given(userMapper.toEntity(any(UserCreationRequest.class))).willReturn(DataUtils.getJohnDoeTransient());
    given(userService.createUser(any(User.class)))
            .willThrow(new UserWithDuplicateEmailException("User with defined email already exists"));

    ResultActions result = mockMvc.perform(post(ApiPaths.USER_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));

    result.andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is(400)))
            .andExpect(jsonPath("$.message", is("User with defined email already exists")));
}
```

**Scenario: Resource Not Found (404)**

```java
@Test
public void shouldReturn404WhenGetUserByIdNotFound() throws Exception {
    given(userService.getUserById(anyLong()))
            .willThrow(new UserNotFoundException("User not found"));

    ResultActions result = mockMvc.perform(get(ApiPaths.USER_ENDPOINT + "/99"))
            .contentType(MediaType.APPLICATION_JSON);

    result.andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is (404)))
            .andExpect(jsonPath("$.message", is("User not found")));
}
```

**Empty Response (204 No Content)**

For operations that do not return a body, verify that the server returns `204 No Content` and ensure the service method was called.

```java
import com.vention.medfast.constants.ApiPaths;

@Test
public void shouldReturn204WhenDeleteUserExists() throws Exception {
    doNothing().when(userService).deleteUserById(1L);

    ResultActions result = mockMvc.perform(delete(ApiPaths.USER_ENDPOINT + "/1"));

    verify(userService, times(1)).deleteUserById(1L);
    result.andDo(print())
            .andExpect(status().isNoContent());
}
```

**Security Behavior**

If Authorization is required, verify that unauthorized users receive 401 or 403.

```java
@Test
public void shouldReturn403WhenUserIsNotAdmin() throws Exception {
    mockMvc.perform(delete(ApiPaths.USER_ENDPOINT + "/1"))
            .andExpect(status().isForbidden());
}
```

---

## Mappers

### Core scenarios to cover

#### Comprehensive Field Validation.

When testing object mapping, every mapped field must be explicitly validated to ensure data integrity and prevent regression.
**Example:**

```java
@Test
void shouldMapUserDtoToUserWhenInputIsValid() {
    User entity = new User(1L, "admin", "admin@example.com");

    UserDto result = userMapper.toDto(entity);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1);
    assertThat(result.getUsername()).isEqualTo("admin");
    assertThat(result.getEmail()).isEqualTo("admin@example.com");
}
```

#### Collection verification

When mapping collections validate:

1. The size of the resulting collection.
2. The content of specific elements within the collection.

```java
@Test
void shouldMapUserListToUserDTOListWhenListIsValid() {
    List<User> userList = List.of(new UserDto("John"), new UserDto("Doe"));

    List<UserDto> result = userMapper.toDtoList(userList);

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getName()).isEqualTo("John");
    assertThat(result.get(1).getName()).isEqualTo("Doe");
}
```

#### Null verification

```java
@Test
void shouldReturnNullWhenInputIsNull() {
    UserDto result = userMapper.toDto(null);

    assertThat(result).isNull();
}
```

---

## Coverage Requirements

We enforce code coverage standards using **Jacoco**.

### Coverage Thresholds

Each layer has specific coverage targets.

| Layer       | Minimum Coverage |
|-------------|------------------|
| Controllers | 90%              |
| Mappers     | 80%              |

**Enforcement**: Running `mvn verify` will fail automatically if coverage drops below these thresholds for any specific class.

### How to Run & Check Coverage

Run the following command in your terminal to execute tests and verify coverage rules:

```bash
mvn clean verify
```

* **BUILD SUCCESS:** All tests passed and coverage meets the minimum requirements.
* **BUILD FAILURE:** Check the logs for "Rule violated" to see which class missed the target.

### Visual Report

For a detailed analysis of which lines are covered or missed:

1. Run the tests or `mvn jacoco:report`.
2. Navigate to `target/site/jacoco/index.html`
3. Open the file in your browser
4. Green lines = Covered, Red lines = Missed.

---

## Test Template Classes

Below is a reusable template structures. Developers can copy this class, rename the targets, and adapt the data objects.

---

### Controller Test Template

**Key Components Included:**

1. **Setup:** Isolated web layer testing using @WebMvcTest.
2. **Mocking:** Service layer simulation using Mockito.
3. **Serialization:** Converting Objects to JSON strings using ObjectMapper.
4. **Execution:** Performing GET/POST/PUT/DELETE via MockMvc.
5. **Assertions:** verifying Status codes and JSON body content.
6. **Error Handling:** Testing how the controller handles Service exceptions.

```java

@WebMvcTest(TargetController.class)
class TargetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TargetService targetService;

    @MockitoBean
    private TargetMapper targetMapper;

    // POST request to create user
    @Test
    void shouldReturnResponseWhenCreateIsValid() throws Exception {
        // arrange: prepare data
        TargetRequest request = DataUtils.getValidRequest();
        TargetEntity entity = DataUtils.getTransientEntity();
        TargetResponse response = DataUtils.getValidResponse();

        // define mock behavior
        given(targetMapper.toEntity(any(TargetRequest.class))).willReturn(entity);
        given(targetService.create(any(TargetEntity.class))).willReturn(DataUtils.getPersistedEntity());
        given(targetMapper.toResponse(any(TargetEntity.class))).willReturn(response);

        // act: perform POST request using mockMvc
        ResultActions result = mockMvc.perform(post("/api/v1/targets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // assert: verify status and body
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("test@mail.com")));
    }

    // GET request to get user by id
    @Test
    void shouldReturnResponseWhenGetByIdExists() throws Exception {
        // arrange
        TargetEntity entity = DataUtils.getPersistedEntity();
        TargetResponse response = DataUtils.getValidResponse();

        given(targetService.getById(1L)).willReturn(entity);
        given(targetMapper.toResponse(entity)).willReturn(response);

        // act: perform get request
        ResultActions result = mockMvc
                .perform(get("/api/v1/targets/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON));

        // assert
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    // PUT request to update resource
    @Test
    void shouldReturnUpdatedResponseWhenUpdateValid() throws Exception {
        // arrange
        TargetRequest request = DataUtils.getValidRequest();
        TargetResponse response = DataUtils.getValidResponse();

        given(targetMapper.toEntity(any())).willReturn(DataUtils.getTransientEntity());
        given(targetService.update(any())).willReturn(DataUtils.getPersistedEntity());
        given(targetMapper.toResponse(any())).willReturn(response);

        // act
        ResultActions result = mockMvc.perform(put("/api/v1/targets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // assert
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L)));
    }

    // Exception handling
    @Test
    void shouldReturn400WhenDuplicateData() throws Exception {
        // arrange
        TargetRequest request = DataUtils.getValidRequest();

        // mock the Service to throw a Custom Exception
        given(targetService.create(any()))
                .willThrow(new DuplicateResourceException("Resource already exists"));

        // act
        ResultActions result = mockMvc.perform(post("/api/v1/targets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // assert error status and message
        result.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", is("Resource already exists")));
    }

    @Test
    void shouldReturn404WhenResourceNotFound() throws Exception {
        // mock the Service to throw NotFoundException
        given(targetService.getById(anyLong()))
                .willThrow(new ResourceNotFoundException("Target not found"));

        ResultActions result = mockMvc
                .perform(get("/api/v1/targets/99")
                        .contentType(MediaType.APPLICATION_JSON));

        result.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Target not found")));
    }

    // Delete user (void, no content)
    @Test
    void shouldReturn204WhenDeleteSuccessful() throws Exception {
        // arrange
        doNothing().when(targetService).deleteById(1L);

        // act
        ResultActions result = mockMvc.perform(delete("/api/v1/targets/1"));

        // assert (verify to ensure that Service was called
        verify(targetService, times(1)).deleteById(1L);
        result.andDo(print())
                .andExpect(status().isNoContent());
    }
}
```

---

### Mapper Test Template

**Key Components Included:**

1. **Setup:** Instantiating the mapper via Mappers.getMapper().
2. **Mapping Logic:** Verifying explicit Entity $\leftrightarrow$ DTO field transformation.
3. **Assertions:** Validating that every field matches the expected value using AssertJ.
4. **Null values:** Verifying that null inputs correctly return null.
5. **Collections:** Verifying List mapping, including handling empty and null lists.

```java
class TargetMapperTest {

    private TargetMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(TargetMapper.class);
    }

    // all fields are mapped correctly (Entity -> dto)
    @Test
    void shouldMapEntityToDtoWhenInputIsValid() {
        // arrange
        TargetEntity entity = new TargetEntity();
        entity.setId(1L);
        entity.setName("Test Name");
        entity.setEmail("test@example.com");

        // act
        TargetDto result = mapper.toDto(entity);

        // assert every field
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Test Name");
        assertThat(result.email()).isEqualTo("test@example.com");
    }

    // all fields are mapped correctly (dto -> Entity)
    @Test
    void shouldMapDtoToEntityWhenInputIsValid() {
        // arrange
        TargetDto dto = new TargetDto(2L, "Test Name", "test@example.com");

        // act
        TargetEntity result = mapper.toEntity(dto);

        // assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("Test Name");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    // null handling
    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.toDto(null)).isNull();
    }

    @Test
    void shouldReturnNullWhenDtoIsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void shouldReturnNullWhenListIsNull() {
        assertThat(mapper.toDtoList(null)).isNull();
    }

    // collections
    @Test
    void shouldReturnEmptyListWhenListIsEmpty() {
        List<TargetDto> result = mapper.toDtoList(Collections.emptyList());

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    void shouldMapListWhenListHasElements() {
        // arrange
        TargetEntity entity = new TargetEntity();
        entity.setId(3L);
        entity.setName("Charlie");

        // act
        List<TargetDto> result = mapper.toDtoList(List.of(entity));

        // assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Charlie");
    }
}
```

---

### DataUtils class used in examples

```java
public class DataUtils {

    public static User getJohnDoeTransient() {
        return User.builder().firstName("John").lastName("Doe").email("john.doe@mail.com").role(Role.PATIENT).build();
    }

    public static User getJohnDoePersisted() {
        return User.builder().id(1L).firstName("John").lastName("Doe").email("john.doe@mail.com").role(Role.PATIENT).build();
    }

    public static UserCreationRequest getJohnDoeRequest() {
        return new UserCreationRequest("john.doe@mail.com", "John", "Doe", Role.PATIENT);
    }

    public static UserResponse getJohnDoeResponse() {
        return new UserResponse(1L, "john.doe@mail.com", "John", "Doe", Role.PATIENT);
    }

    // ...
}
```
