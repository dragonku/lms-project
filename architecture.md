# LMS 프로젝트 아키텍처 문서

## 1. 아키텍처 개요

### 1.1 아키텍처 원칙
본 프로젝트는 **Clean Architecture**를 기반으로 설계되며, 다음 원칙을 준수합니다:

- **관심사의 분리 (Separation of Concerns)**
- **의존성 역전 원칙 (Dependency Inversion Principle)**
- **단일 책임 원칙 (Single Responsibility Principle)**
- **개방-폐쇄 원칙 (Open-Closed Principle)**
- **테스트 가능성 (Testability)**

### 1.2 전체 시스템 구조
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Backend       │    │   Database      │
│   (React)       │◄──►│   (Spring Boot) │◄──►│   (PostgreSQL)  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌─────────────────┐
                    │   Monitoring    │
                    │ (Prometheus +   │
                    │   Grafana)      │
                    └─────────────────┘
```

## 2. Clean Architecture 4계층 구조

### 2.1 계층별 책임과 규칙

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                       │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │                  Application Layer                      │ │
│  │  ┌─────────────────────────────────────────────────────┐ │ │
│  │  │                 Domain Layer                        │ │ │
│  │  │                 (Core Business)                     │ │ │
│  │  └─────────────────────────────────────────────────────┘ │ │
│  └─────────────────────────────────────────────────────────┘ │
│                    Infrastructure Layer                     │
└─────────────────────────────────────────────────────────────┘
```

#### 2.1.1 Domain Layer (도메인 계층)
**위치**: `src/main/java/com/lms/domain/`
**책임**:
- 비즈니스 로직과 규칙 구현
- 엔티티 및 값 객체 정의
- 도메인 서비스 구현
- 리포지토리 인터페이스 정의

**구성 요소**:
```
domain/
├── entities/           # 핵심 비즈니스 엔티티
│   ├── User.java
│   ├── Course.java
│   └── Company.java
├── repositories/       # 리포지토리 인터페이스
│   ├── UserRepository.java
│   ├── CourseRepository.java
│   └── CompanyRepository.java
└── services/          # 도메인 서비스
    ├── UserDomainService.java
    └── CourseDomainService.java
```

**의존성 규칙**:
- 외부 계층에 의존하지 않음 (가장 안정적)
- 순수한 Java 코드로만 구성
- 프레임워크 독립적

#### 2.1.2 Application Layer (애플리케이션 계층)
**위치**: `src/main/java/com/lms/application/`
**책임**:
- 유스케이스 구현
- 트랜잭션 관리
- DTO 정의
- 비즈니스 워크플로우 조정

**구성 요소**:
```
application/
├── dto/               # 데이터 전송 객체
│   ├── request/
│   │   ├── CreateUserRequest.java
│   │   └── CreateCourseRequest.java
│   └── response/
│       ├── UserResponse.java
│       └── CourseResponse.java
└── usecases/          # 유스케이스 구현
    ├── user/
    │   ├── CreateUserUseCase.java
    │   ├── UpdateUserUseCase.java
    │   └── DeleteUserUseCase.java
    └── course/
        ├── CreateCourseUseCase.java
        └── EnrollCourseUseCase.java
```

**의존성 규칙**:
- Domain Layer만 의존
- 비즈니스 로직을 조정하되 포함하지 않음

#### 2.1.3 Infrastructure Layer (인프라스트럭처 계층)
**위치**: `src/main/java/com/lms/infrastructure/`
**책임**:
- 외부 시스템과의 연동
- 데이터베이스 구현
- 외부 API 호출
- 파일 시스템 접근

**구성 요소**:
```
infrastructure/
├── database/          # 데이터베이스 구현
│   ├── config/
│   │   └── JpaConfig.java
│   └── entities/
│       ├── UserJpaEntity.java
│       └── CourseJpaEntity.java
├── repositories/      # 리포지토리 구현
│   ├── UserRepositoryImpl.java
│   ├── CourseRepositoryImpl.java
│   └── CompanyRepositoryImpl.java
└── external/          # 외부 API 연동
    ├── EmailService.java
    └── FileStorageService.java
```

**의존성 규칙**:
- Domain과 Application Layer에 의존
- 구체적인 기술 구현 담당

#### 2.1.4 Presentation Layer (표현 계층)
**위치**: `src/main/java/com/lms/presentation/`
**책임**:
- HTTP 요청/응답 처리
- 입력 검증
- 인증/인가
- API 문서화

**구성 요소**:
```
presentation/
├── controllers/       # REST API 컨트롤러
│   ├── UserController.java
│   ├── CourseController.java
│   └── AuthController.java
├── middleware/        # 미들웨어
│   ├── AuthenticationFilter.java
│   ├── ExceptionHandler.java
│   └── ValidationHandler.java
└── config/           # 프레젠테이션 설정
    ├── SecurityConfig.java
    └── WebConfig.java
```

**의존성 규칙**:
- Application Layer에만 의존
- 웹 프레임워크 종속적

## 3. 데이터베이스 설계

### 3.1 ERD (Entity Relationship Diagram)
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│    companies    │    │      users      │    │     courses     │
├─────────────────┤    ├─────────────────┤    ├─────────────────┤
│ id (PK)         │◄──┐│ id (PK)         │    │ id (PK)         │
│ business_number │   └│ company_id (FK) │    │ title           │
│ name            │    │ username        │    │ description     │
│ representative  │    │ password        │    │ duration_hours  │
│ contract_status │    │ email           │    │ course_type     │
│ created_at      │    │ user_type       │    │ status          │
│ updated_at      │    │ status          │    │ created_at      │
└─────────────────┘    │ created_at      │    │ updated_at      │
                       │ updated_at      │    └─────────────────┘
                       └─────────────────┘           │
                                │                    │
                                │    ┌─────────────────┐
                                └───►│  enrollments    │
                                     ├─────────────────┤
                                     │ id (PK)         │
                                     │ user_id (FK)    │
                                     │ course_id (FK)  │
                                     │ status          │
                                     │ enrolled_at     │
                                     └─────────────────┘
```

### 3.2 테이블 설계 원칙
- **정규화**: 3NF까지 정규화 적용
- **인덱싱**: 자주 검색되는 컬럼에 인덱스 적용
- **제약조건**: 데이터 무결성 보장을 위한 제약조건 설정
- **감사**: 생성일시, 수정일시 자동 관리 (JPA Auditing)

### 3.3 마이그레이션 전략
- **Flyway** 사용한 버전 관리
- **순차적 마이그레이션**: V001, V002, V003... 형태
- **롤백 계획**: 각 마이그레이션별 롤백 스크립트 작성

## 4. API 설계

### 4.1 REST API 설계 원칙
- **RESTful**: HTTP 메소드와 상태 코드 적절히 활용
- **일관성**: 명명 규칙과 응답 형식 통일
- **버전 관리**: URL 경로에 버전 포함 (/api/v1/)
- **보안**: JWT 토큰 기반 인증

### 4.2 API 구조
```
/api/v1
├── /auth                # 인증 관련
│   ├── POST /login
│   ├── POST /logout
│   └── POST /refresh
├── /users               # 사용자 관리
│   ├── GET /users
│   ├── POST /users
│   ├── GET /users/{id}
│   ├── PUT /users/{id}
│   └── DELETE /users/{id}
├── /courses             # 과정 관리
│   ├── GET /courses
│   ├── POST /courses
│   ├── GET /courses/{id}
│   ├── PUT /courses/{id}
│   └── DELETE /courses/{id}
└── /companies           # 협약사 관리
    ├── GET /companies
    ├── POST /companies
    ├── GET /companies/{id}
    ├── PUT /companies/{id}
    └── DELETE /companies/{id}
```

### 4.3 응답 형식 표준화
```json
{
  "success": true,
  "message": "요청이 성공적으로 처리되었습니다",
  "data": {
    // 실제 응답 데이터
  },
  "timestamp": "2023-11-20T10:30:00Z",
  "path": "/api/v1/users"
}
```

## 5. 보안 아키텍처

### 5.1 인증 및 인가
- **JWT (JSON Web Token)** 기반 인증
- **Role-Based Access Control (RBAC)** 권한 관리
- **Refresh Token** 을 통한 토큰 갱신

### 5.2 보안 계층
```
┌─────────────────────────────────────────┐
│           Security Filters               │
├─────────────────────────────────────────┤
│  1. CORS Filter                         │
│  2. Authentication Filter (JWT)         │
│  3. Authorization Filter (Role-based)   │
│  4. Rate Limiting Filter                │
└─────────────────────────────────────────┘
           │
┌─────────────────────────────────────────┐
│         Spring Security                 │
├─────────────────────────────────────────┤
│  - Password Encoding (BCrypt)           │
│  - CSRF Protection                      │
│  - Session Management                   │
└─────────────────────────────────────────┘
```

### 5.3 데이터 보안
- **개인정보 암호화**: AES-256 암호화
- **비밀번호 해싱**: BCrypt 알고리즘
- **SQL Injection 방어**: PreparedStatement 사용
- **XSS 방어**: 입력값 검증 및 이스케이핑

## 6. 테스트 전략

### 6.1 테스트 피라미드
```
      ┌─────────────┐
     /   E2E Tests   \      (Few)
    /   Integration   \
   /     Tests         \    (Some)
  /_____________________\
 /      Unit Tests       \  (Many)
/_________________________\
```

### 6.2 테스트 유형별 전략

#### 6.2.1 Unit Tests (단위 테스트)
- **대상**: Domain Layer의 엔티티 및 서비스
- **도구**: JUnit 5, AssertJ, Mockito
- **커버리지**: 90% 이상
- **실행**: 빌드시 자동 실행

#### 6.2.2 Integration Tests (통합 테스트)
- **대상**: Repository, API 엔드포인트
- **도구**: @DataJpaTest, @WebMvcTest
- **데이터베이스**: H2 인메모리 DB
- **실행**: CI/CD 파이프라인

#### 6.2.3 E2E Tests (종단간 테스트)
- **대상**: 사용자 시나리오
- **도구**: Selenium, Cypress
- **환경**: 스테이징 환경
- **실행**: 배포 전 자동 실행

### 6.3 TDD (Test-Driven Development) 사이클
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Red       │───►│   Green     │───►│  Refactor   │
│ (실패하는    │    │ (통과하는    │    │ (리팩토링)   │
│  테스트)     │    │  코드)      │    │             │
└─────────────┘    └─────────────┘    └─────────────┘
       ▲                                      │
       └──────────────────────────────────────┘
```

## 7. 성능 및 확장성

### 7.1 성능 목표
- **응답 시간**: 95% 요청이 2초 이내
- **처리량**: 1,000 RPS (Requests Per Second)
- **동시 사용자**: 10,000명
- **가용성**: 99.9% (연간 8.7시간 다운타임)

### 7.2 성능 최적화 전략

#### 7.2.1 데이터베이스 최적화
- **인덱싱**: 자주 검색되는 컬럼에 인덱스
- **쿼리 최적화**: N+1 문제 해결 (Fetch Join)
- **커넥션 풀링**: HikariCP 사용
- **읽기 전용 복제본**: 읽기 성능 향상

#### 7.2.2 캐싱 전략
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  Browser    │    │ Application │    │  Database   │
│   Cache     │◄──►│   Cache     │◄──►│   (Redis)   │
│  (HTTP)     │    │ (Caffeine)  │    │             │
└─────────────┘    └─────────────┘    └─────────────┘
```

- **L1 Cache**: 애플리케이션 내 Caffeine 캐시
- **L2 Cache**: Redis 분산 캐시
- **HTTP Cache**: 브라우저 캐시 활용

#### 7.2.3 확장성 설계
- **수평적 확장**: 로드 밸런서 + 다중 인스턴스
- **마이크로서비스 준비**: 도메인별 서비스 분리 가능한 구조
- **무상태 설계**: 세션 정보를 외부 저장소에 보관

## 8. 모니터링 및 로깅

### 8.1 모니터링 아키텍처
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ Application │───►│ Prometheus  │───►│   Grafana   │
│  Metrics    │    │  (Metrics)  │    │(Dashboard)  │
└─────────────┘    └─────────────┘    └─────────────┘
       │
       ▼
┌─────────────┐    ┌─────────────┐
│    Logs     │───►│   ELK/EFK   │
│ (Logback)   │    │   Stack     │
└─────────────┘    └─────────────┘
```

### 8.2 메트릭 수집
- **시스템 메트릭**: CPU, 메모리, 디스크, 네트워크
- **애플리케이션 메트릭**: 응답 시간, 처리량, 에러율
- **비즈니스 메트릭**: 사용자 수, 과정 등록 수, 수료율

### 8.3 로깅 전략
- **구조화된 로깅**: JSON 형태로 로그 출력
- **로그 레벨**: ERROR, WARN, INFO, DEBUG
- **보안**: 개인정보는 로그에 기록하지 않음
- **보관 정책**: 30일간 보관 후 아카이브

## 9. 배포 및 운영

### 9.1 CI/CD 파이프라인
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│    Code     │───►│    Build    │───►│    Test     │───►│   Deploy    │
│   Commit    │    │  (Gradle)   │    │  (JUnit)    │    │  (Docker)   │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
```

### 9.2 환경 관리
- **개발 환경**: 로컬 Docker Compose
- **테스트 환경**: CI/CD 자동 배포
- **스테이징 환경**: 프로덕션과 동일한 환경
- **프로덕션 환경**: 고가용성 클러스터

### 9.3 배포 전략
- **Blue-Green 배포**: 무중단 배포
- **카나리 배포**: 점진적 트래픽 전환
- **롤백 계획**: 이전 버전으로 즉시 복구 가능

## 10. 코딩 표준 및 규칙

### 10.1 코딩 스타일
- **Google Java Style Guide** 준수
- **Lombok** 사용으로 보일러플레이트 코드 최소화
- **Builder 패턴** 활용한 객체 생성
- **Optional** 활용한 null 안전성

### 10.2 패키지 구조 규칙
```
com.lms
├── domain.*           # 도메인 계층
├── application.*      # 애플리케이션 계층
├── infrastructure.*   # 인프라스트럭처 계층
├── presentation.*     # 표현 계층
└── config.*          # 공통 설정
```

### 10.3 명명 규칙
- **클래스**: PascalCase (UserService)
- **메소드/변수**: camelCase (findUserById)
- **상수**: UPPER_SNAKE_CASE (MAX_RETRY_COUNT)
- **패키지**: lowercase (com.lms.domain)

### 10.4 주석 및 문서화
- **Javadoc**: 모든 public 메소드에 작성
- **비즈니스 로직**: 복잡한 비즈니스 규칙은 주석으로 설명
- **TODO**: 향후 개선 사항은 TODO 주석으로 표시

## 11. 버전 관리 및 브랜치 전략

### 11.1 Git Flow 전략
```
main
├── develop
│   ├── feature/sprint-0-user-story-1
│   ├── feature/sprint-0-user-story-2
│   └── feature/sprint-1-authentication
├── release/v0.1.0
└── hotfix/critical-bug-fix
```

### 11.2 커밋 메시지 규칙
```
<type>(<scope>): <subject>

<body>

<footer>
```

**Type**: feat, fix, docs, style, refactor, test, chore
**Scope**: user, course, company, auth 등
**Subject**: 50자 이내 요약

### 11.3 코드 리뷰 프로세스
1. **Pull Request 생성**
2. **자동 테스트 실행** (CI)
3. **코드 리뷰** (최소 1명 승인)
4. **Merge** (Squash and Merge)

## 12. 향후 확장 계획

### 12.1 마이크로서비스 전환
- **사용자 서비스**: 사용자 관리 독립 서비스
- **과정 서비스**: 교육 과정 관리 서비스
- **평가 서비스**: 시험 및 평가 서비스
- **알림 서비스**: 이메일/SMS 알림 서비스

### 12.2 기술 스택 발전
- **Container Orchestration**: Kubernetes 도입
- **Service Mesh**: Istio 도입 검토
- **Event Streaming**: Apache Kafka 도입
- **API Gateway**: Spring Cloud Gateway

### 12.3 성능 최적화
- **CDN**: 정적 자원 배포 최적화
- **Database Sharding**: 데이터베이스 분산
- **Search Engine**: Elasticsearch 도입
- **Cache Strategy**: 다층 캐시 전략

이 아키텍처는 현재 요구사항을 만족하면서도 향후 확장성을 고려한 설계입니다. 각 단계별로 점진적으로 개선해 나갈 예정입니다.