# TDD (Test-Driven Development) 가이드

## TDD란?

Test-Driven Development는 테스트가 주도하는 개발 방법론으로, 다음 3단계 사이클을 반복합니다:

```
RED → GREEN → REFACTOR → RED → GREEN → REFACTOR...
```

### 1. RED Phase (빨간불)
- **실패하는 테스트** 작성
- 아직 구현되지 않은 기능에 대한 테스트
- 컴파일 에러나 테스트 실패 확인

### 2. GREEN Phase (초록불)
- **테스트를 통과하는 최소한의 코드** 작성
- 빠르게 테스트를 통과시키는 것이 목표
- 코드의 완성도보다는 테스트 통과에 집중

### 3. REFACTOR Phase (리팩토링)
- **코드 개선 및 최적화**
- 테스트가 통과하는 상태에서 코드 품질 향상
- 중복 제거, 가독성 향상, 설계 개선

## 프로젝트 TDD 환경

### 1. 테스트 도구 설정
```gradle
// JUnit 5 + Mockito + AssertJ
testImplementation 'org.springframework.boot:spring-boot-starter-test'
testImplementation 'org.testcontainers:junit-jupiter'
testImplementation 'org.testcontainers:postgresql'
```

### 2. 계층별 테스트 전략

#### Domain Layer (단위 테스트)
```java
@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseTest {
    @Mock private UserRepository userRepository;
    @Mock private CompanyRepository companyRepository;
    
    // TDD로 UseCase 구현
}
```

#### Presentation Layer (API 테스트)
```java
@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private CreateUserUseCase createUserUseCase;
    
    // TDD로 API 엔드포인트 구현
}
```

#### Integration Test (통합 테스트)
```java
@SpringBootTest
@Import(TestContainersConfig.class)
class TddDemoIntegrationTest {
    // 전체 스택 E2E 테스트
}
```

### 3. 테스트 설정 클래스

#### TddTestConfig
- JPA Auditing용 고정 시간 제공
- 테스트 격리 보장
- Mock 객체 관리

#### TestContainersConfig
- 실제 PostgreSQL 컨테이너 사용
- 통합 테스트 환경 구성

## TDD 실전 예제

### 1. 사용자 생성 UseCase TDD

#### RED Phase: 실패하는 테스트 작성
```java
@Test
void should_ReturnUserResponse_When_CreateAdminUser() {
    // Given
    CreateUserRequest request = CreateUserRequest.builder()
            .username("admin")
            .email("admin@lms.com")
            .build();
    
    // When - 아직 구현되지 않음 (컴파일 에러 발생)
    UserResponse response = createUserUseCase.execute(request);
    
    // Then
    assertThat(response).isNotNull();
    assertThat(response.getUsername()).isEqualTo("admin");
}
```

#### GREEN Phase: 테스트 통과하는 최소 코드
```java
@Service
public class CreateUserUseCase {
    public UserResponse execute(CreateUserRequest request) {
        // 최소 구현으로 테스트 통과
        return UserResponse.builder()
                .username(request.getUsername())
                .build();
    }
}
```

#### REFACTOR Phase: 코드 개선
```java
@Service
@RequiredArgsConstructor
@Transactional
public class CreateUserUseCase {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    
    public UserResponse execute(CreateUserRequest request) {
        // 비즈니스 로직 추가
        validateCreateUserRequest(request);
        
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                // ... 전체 구현
                .build();
        
        User savedUser = userRepository.save(user);
        return UserResponse.from(savedUser);
    }
}
```

### 2. API 컨트롤러 TDD

#### RED Phase: API 테스트 작성
```java
@Test
void should_Return201Created_When_CreateUserRequest() throws Exception {
    // Given
    CreateUserRequest request = // ...
    
    // When & Then - 아직 컨트롤러 구현되지 않음
    mockMvc.perform(post("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
}
```

#### GREEN Phase: 컨트롤러 구현
```java
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @RequestBody CreateUserRequest request) {
        // 최소 구현
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("", null));
    }
}
```

## TDD 실행 명령어

### Gradle Tasks
```bash
# 단위 테스트 실행
./gradlew testDomain

# 계층별 테스트 실행
./gradlew testApplication
./gradlew testInfrastructure
./gradlew testPresentation

# 통합 테스트 실행
./gradlew integrationTest

# TDD Red Phase 확인
./gradlew tddRed

# 전체 테스트 + 커버리지
./gradlew test jacocoTestReport
```

### 테스트 커버리지 확인
```bash
# 커버리지 리포트 생성
./gradlew jacocoTestReport

# 리포트 위치
build/reports/jacoco/test/html/index.html
```

## TDD 베스트 프랙티스

### 1. 테스트 네이밍
```java
// 패턴: should_[ExpectedBehavior]_When_[StateCondition]
@Test
void should_ReturnUserResponse_When_CreateValidUser() {}

@Test
void should_ThrowException_When_DuplicateEmail() {}
```

### 2. Given-When-Then 구조
```java
@Test
void should_CreateUser_When_ValidRequest() {
    // Given: 테스트 데이터 준비
    CreateUserRequest request = // ...
    
    // When: 테스트 실행
    UserResponse response = createUserUseCase.execute(request);
    
    // Then: 결과 검증
    assertThat(response).isNotNull();
}
```

### 3. 테스트 격리
- 각 테스트는 독립적으로 실행
- `@Transactional` 사용으로 데이터 롤백
- Mock 객체로 외부 의존성 격리

### 4. 실패 메시지 명확성
```java
assertThat(response.getUsername())
    .as("사용자명이 요청값과 일치해야 함")
    .isEqualTo("admin");
```

## Clean Architecture와 TDD

### 계층별 TDD 적용
1. **Domain Layer**: 비즈니스 로직 TDD
2. **Application Layer**: UseCase TDD
3. **Infrastructure Layer**: Repository 구현 TDD
4. **Presentation Layer**: API 엔드포인트 TDD

### 의존성 방향
```
Presentation → Application → Domain
Infrastructure → Domain
```

### 테스트 전략
- **단위 테스트**: Domain, Application Layer
- **통합 테스트**: Infrastructure Layer
- **E2E 테스트**: Presentation Layer

이 가이드를 따라 Clean Architecture와 TDD를 결합하여 품질 높은 소프트웨어를 개발할 수 있습니다.