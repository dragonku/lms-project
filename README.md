# LMS (Learning Management System)

교육관리시스템 - Sprint #0 데이터베이스 계층 구현

## 🎯 프로젝트 개요

본 프로젝트는 재직자 및 구직자를 위한 교육관리시스템(LMS)입니다. Clean Architecture를 기반으로 구현되며, Spring Boot + React 기술 스택을 사용합니다.

## 🏗️ 아키텍처

### Clean Architecture 4계층 구조
```
├── Domain (비즈니스 로직)
│   ├── entities/     # 엔티티
│   ├── repositories/ # 저장소 인터페이스
│   └── services/     # 도메인 서비스
├── Application (유스케이스)
│   ├── dto/          # 데이터 전송 객체
│   └── usecases/     # 애플리케이션 서비스
├── Infrastructure (기술 구현)
│   ├── database/     # 데이터베이스 구현
│   ├── external/     # 외부 API 구현
│   └── repositories/ # 저장소 구현
└── Presentation (표현 계층)
    ├── controllers/  # REST API 컨트롤러
    └── middleware/   # 미들웨어
```

## 🚀 시작하기

### 사전 요구사항
- Java 21
- Node.js 18+
- Docker & Docker Compose
- PostgreSQL 15

### 로컬 개발 환경 설정

1. **저장소 클론**
```bash
git clone https://github.com/dragonku/lms-project.git
cd lms-project
```

2. **데이터베이스 실행**
```bash
docker-compose up -d postgres
```

3. **백엔드 실행**
```bash
cd backend
./gradlew bootRun
```

4. **프론트엔드 실행**
```bash
cd frontend
npm install
npm start
```

## 🧪 테스트 실행

### 백엔드 테스트
```bash
cd backend
./gradlew test
```

### 테스트 결과
- **총 테스트**: 18개
- **성공률**: 100%
- **커버리지 리포트**: `backend/build/reports/jacoco/test/html/index.html`

## 📊 현재 구현 상태

### ✅ Sprint #0 User Story 2 완료
- [x] PostgreSQL 데이터베이스 연결
- [x] User Entity 구현 (권한 체계)
- [x] Course Entity 구현 (과정 관리)
- [x] Company Entity 구현 (협약사 관리)
- [x] JPA Repository 구현
- [x] 18개 테스트 작성 및 통과

### 🎯 주요 기능

#### 사용자 관리
- 4가지 사용자 유형: ADMIN, INSTRUCTOR, STUDENT, COMPANY_MANAGER
- 재직자/구직자 구분
- 회사 연관 관계 관리

#### 과정 관리
- 3가지 과정 유형: COMMON, EMPLOYEE, JOB_SEEKER
- 신청 가능 여부 자동 검증
- 상태별 과정 관리

#### 협약사 관리
- 4가지 승인 상태: PENDING, APPROVED, REJECTED, TERMINATED
- 직원 관리 기능
- 과정 신청 권한 제어

## 🛠️ 기술 스택

### Backend
- **Framework**: Spring Boot 3.2.0
- **Language**: Java 21
- **Database**: PostgreSQL 15
- **ORM**: JPA/Hibernate
- **Migration**: Flyway
- **Testing**: JUnit 5, AssertJ
- **Security**: Spring Security

### Frontend (예정)
- **Framework**: React 18
- **Language**: TypeScript
- **State Management**: Redux Toolkit
- **Styling**: styled-components
- **Testing**: Jest, React Testing Library

### DevOps
- **Containerization**: Docker
- **Orchestration**: Docker Compose
- **CI/CD**: GitHub Actions (예정)
- **Monitoring**: Prometheus + Grafana (예정)

## 📝 문서

- [아키텍처 문서](./architecture.md)
- [요구사항 명세서 (PRD)](./prd.md)
- [릴리스 계획](./release.md)

## 🤝 기여하기

1. 이슈 생성 또는 기존 이슈 선택
2. Feature 브랜치 생성 (`feature/sprint-X-user-story-Y`)
3. 변경사항 커밋
4. Pull Request 생성

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.

---

🤖 Generated with [Claude Code](https://claude.ai/code)