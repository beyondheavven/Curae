# Unit Testing Mandatory Conventions - Mappers

## Naming Conventions
* **Class Names**  must be named corresponding to the mapper class they test, **XxxMapperTest** or **XxxMapperImplTest**. Example: 
```java
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)  
public interface UserMapper { }

// Corresponding Test Class Name
public class UserMapperTest { }
```
* **Method names** must follow the pattern: should < ExpectedBehavior >When< Condition >(). Example:
```java
@Test
void shouldMapEntityToDtoWhenInputIsValid() {}
```

## Test Structure
Every test method should follow AAA (Arrange-Act-Assert) pattern.

* **Arrange**: Set up the test data(fixtures), objects that the testing method depends on.
* **Act**: Call the specific method being tested.
* **Assert**: Check if the result matches what you expected, using AssertJ.

#### Example:
```java
@Test
void shouldMapUserDtoToUserWhenInputIsValid() {
    // Arrange
    UserDTO dto = new UserDto("john_doe", 20, "john20@example.com");
    
    // Act
    UserEntity result = userMapper.toEntity(dto);
    
    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("john_doe");
    assertThat(result.getAge()).isEqualTo(20);
    assertThat(result.getEmail()).isEqualTo("john20@example.com");
}
```

## Assertions
### For all tests use `assertThat()` from **AssertJ**.

*Official Documentation:* [AssertJ Core Documentation](https://assertj.github.io/doc/)

**Common assertThat() examples**

```java
// Nullability Checks
assertThat(result).isNotNull();
assertThat(result).isNull();

// String Verification
assertThat(result.getName()).isEqualTo("John");
assertThat(result.getEmail()).contains("@gmail.com");

// Numeric Verification
assertThat(result.getAge()).isEqualTo(25);
assertThat(result.getPrice()).isPositive();

// Boolean Verification
assertThat(result.isActive()).isTrue();
```

### Comprehensive Field Validation.
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

### Collection verification
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

### Null verification
```java
@Test
void shouldReturnNullWhenInputIsNull() {
    UserDto result = userMapper.toDto(null);
    
    assertThat(result).isNull();
}
```

## Coverage Requirements

### 1. The 80% Rule
We ensure the code coverage using **Jacoco**
* **Target:** All Mapper classes must achieve **at least 80% coverage**
* **Enforcement:**: `mvn verify` will **fail automatically** if coverage drops below this threshold.

#### How to View Reports
Run the following command in your terminal:
```bash
mvn clean verify
```
* If the command succeeds (BUILD SUCCESS), your coverage is sufficient.
* If it fails, check the logs for "Rule violated".

You can also have a look at the **Visual Report** after running the tests, a detailed HTML report is generated. If not, in Intellij IDEA, run in Maven->Plugins->jacoco:report
* **Location**: `target/site/jacoco/index.html`
* **Open in browser**
* **What to look for**: for the specific class implementation inside, green lines are covered, red lines are missed.

## Reference Example
Below is an example of a simple Mapper Test that achieves 100% coverage.

**Class**: UserMapperTest.java (without Mocking)
```java
class UserMapperTest {

    private UserMapper mapper;

    @BeforeEach
    void setUp(){
        mapper = Mappers.getMapper(UserMapper.class);
    }

    @Test
    void shouldMapUserToUserDtoWhenUserIsValid() {
        User user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@example.com");

        UserDto result = mapper.toDto(user);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("John");
        assertThat(result.email()).isEqualTo("john@example.com");
    }

    @Test
    void shouldMapUserDtoToUserWhenUserDtoIsValid() {
        UserDto dto = new UserDto(2L, "John", "john@example.com");

        User result = mapper.toEntity(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("John");
        assertThat(result.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void shouldReturnNullWhenUserIsNull() {
        assertThat(mapper.toDto(null)).isNull();
    }

    @Test
    void shouldReturnNullWhenUserDtoIsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void shouldReturnNullWhenUserListIsNull() {
        assertThat(mapper.toDtoList(null)).isNull();
    }

    @Test
    void shouldReturnEmptyListWhenUserListIsEmpty() {
        List<UserDto> result = mapper.toDtoList(Collections.emptyList());

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }


    @Test
    void shouldMapListWhenUserListHasElements() {
        User user = new User();
        user.setId(3L);
        user.setName("Charlie");
        user.setEmail("charlie@example.com");

        List<UserDto> result = mapper.toDtoList(List.of(user));

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).name()).isEqualTo("Charlie");
    }

}
```
