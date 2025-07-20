# LMS 프로젝트 릴리스 계획서

## 개요
본 문서는 교육관리시스템(LMS) 프로젝트의 릴리스 계획을 정의합니다. 
PRD의 모든 기능 요구사항(SFR-001 ~ SFR-027)을 기반으로 3주 단위 릴리스와 1주일 단위 스프린트로 구성되었습니다.

## 기술 스택
- **Frontend**: React
- **Backend**: Spring Boot  
- **Database**: PostgreSQL
- **Testing**: Jest, JUnit
- **CI/CD**: GitHub Actions
- **Monitoring**: Prometheus, Grafana

## 릴리스 개요

| Release | 기간 | 주요 기능 | Sprint 구성 | SFR 매핑 |
|---------|------|-----------|-------------|----------|
| 0.1 | Week 1-3 | 아키텍처 & 기반 시설 | #0, #1, #2 | SFR-001, SFR-011 |
| 0.2 | Week 4-6 | 사용자 관리 & 인증 | #3, #4, #5 | SFR-005, SFR-008, SFR-009, SFR-012 |
| 0.3 | Week 7-9 | 과정 관리 & 수강 신청 | #6, #7, #8 | SFR-007, SFR-010, SFR-014, SFR-018 |
| 0.4 | Week 10-12 | 교육 관리 & 평가 시스템 | #9, #10, #11 | SFR-019, SFR-024, SFR-025, SFR-026, SFR-027 |
| 0.5 | Week 13-15 | 협약사 관리 & 통계 | #12, #13, #14 | SFR-013, SFR-015, SFR-016, SFR-017, SFR-020, SFR-021, SFR-022, SFR-023 |
| 1.0 | Week 16-18 | 공통기능 & 최적화 | #15, #16, #17 | SFR-002, SFR-003, SFR-004, SFR-006 |

## SFR 요구사항 매핑 테이블

| SFR ID | 기능 명칭 | Sprint | User Story | Status |
|---------|-----------|--------|------------|--------|
| SFR-001 | 시스템 표준 준수 | #2 | 반응형 웹 구현 | ⏳ Pending |
| SFR-002 | 팝업 관리 | #16 | 팝업 관리 시스템 | ⏳ Pending |
| SFR-003 | 게시판 시스템 | #15 | 공통 게시판 시스템 | ⏳ Pending |
| SFR-004 | 설문 시스템 | #16 | 설문 결과 시각화 | ⏳ Pending |
| SFR-005 | 사용자 권한 관리 | #5 | 권한 관리 시스템 | ⏳ Pending |
| SFR-006 | 사업 소개 및 홍보 | #16 | 사업 소개 페이지 | ⏳ Pending |
| SFR-007 | 과정 소개 및 신청 | #7 | 과정 소개 페이지 | ⏳ Pending |
| SFR-008 | 회원가입 | #3 | 기본 회원가입 구현 | ⏳ Pending |
| SFR-009 | 협약사 가입 | #4 | 협약사 가입 프로세스 | ⏳ Pending |
| SFR-010 | 마이페이지 | #8 | 마이페이지 구현 | ⏳ Pending |
| SFR-011 | 보안 기능 | #2, #5 | 보안 기본 설정, 2차 인증 | ⏳ Pending |
| SFR-012 | 회원 관리 | #5 | 회원 관리 기능 | ⏳ Pending |
| SFR-013 | 협약사 관리 | #12 | 협약사 관리 시스템 | ⏳ Pending |
| SFR-014 | 강사 관리 | #6 | 강사 관리 시스템 | ⏳ Pending |
| SFR-015 | 담당자 관리 | #13 | 담당자 관리 시스템 | ⏳ Pending |
| SFR-016 | 시설 및 장비 관리 | #13 | 시설 및 장비 관리 | ⏳ Pending |
| SFR-017 | 통계 관리 | #14 | 통계 시스템 구현 | ⏳ Pending |
| SFR-018 | 과정 관리 | #6 | 과정 등록 및 관리 | ⏳ Pending |
| SFR-019 | 수강생 관리 | #9 | 수강 신청 승인/반려 | ⏳ Pending |
| SFR-020 | 양성과정 신청서 관리 | #13 | 양성과정 신청서 관리 | ⏳ Pending |
| SFR-021 | 협약사 정보 관리 | #12 | 협약사 정보 관리 | ⏳ Pending |
| SFR-022 | 협약사 직원 관리 | #12 | 직원 관리 시스템 | ⏳ Pending |
| SFR-023 | 협약사 교육 관리 | #12 | 교육 관리 시스템 | ⏳ Pending |
| SFR-024 | 문제 등록/관리 | #10 | 문제 등록/관리 시스템 | ⏳ Pending |
| SFR-025 | 시험 출제 | #10 | 시험 출제 시스템 | ⏳ Pending |
| SFR-026 | 시험 응시 | #11 | 시험 응시 시스템 | ⏳ Pending |
| SFR-027 | 자동 채점 | #11 | 자동 채점 시스템 | ⏳ Pending |

---

## Release 0.1 - 아키텍처 & 기반 시설

### Sprint #0 - 아키텍처 검증 (Week 1)
**목표**: 클린 아키텍처 기반 프로젝트 구조 설정 및 검증

#### User Story 1: 프로젝트 기반 구조 설정
- **Given**: 새로운 LMS 프로젝트가 시작됨
- **When**: 개발자가 클린 아키텍처 기반으로 프로젝트 구조를 설정할 때
- **Then**: Domain, Application, Infrastructure, Presentation 계층이 분리된 구조가 생성됨
- **Acceptance Criteria**:
  - 클린 아키텍처 4계층 분리 구조 생성
  - Spring Boot + React 기반 프로젝트 설정
  - Docker 개발 환경 구축
  - 기본 빌드 도구 및 의존성 설정
- **Status**: ✅ **Completed**

#### User Story 2: 데이터베이스 연결 및 기본 Entity 생성
- **Given**: PostgreSQL 데이터베이스가 준비됨
- **When**: 개발자가 User, Course, Company Entity를 생성하고 데이터베이스에 연결할 때
- **Then**: 테이블들이 생성되고 JPA Repository를 통해 CRUD가 가능함
- **Acceptance Criteria**:
  - PostgreSQL 데이터베이스 설정 및 연결 구성 ✅
  - User Entity 및 관련 도메인 모델 생성 ✅
  - Course Entity 및 관련 도메인 모델 생성 ✅
  - Company Entity 및 관련 도메인 모델 생성 ✅
  - JPA Repository 인터페이스 생성 ✅
  - 기본 CRUD 테스트 및 검증 (18개 테스트 모두 통과) ✅
- **Status**: ✅ **Completed**

#### User Story 3: TDD 환경 설정 및 샘플 테스트 작성
- **Given**: Jest(Frontend), JUnit(Backend) 테스트 환경이 설정됨
- **When**: 개발자가 UserService에 대한 테스트를 작성할 때
- **Then**: Red-Green-Refactor 사이클이 정상 동작하고 80% 이상 커버리지가 확보됨
- **Acceptance Criteria**:
  - JUnit 5 + Mockito + AssertJ 테스트 스택 구성
  - TestContainers로 통합 테스트 환경 구축
  - TDD로 CreateUserUseCase 구현 완료
  - TDD로 UserController API 구현 완료
  - Jacoco 테스트 커버리지 80% 목표 설정
  - TDD 가이드 문서 작성 완료
- **Status**: ✅ Completed

#### User Story 4: CI/CD 파이프라인 기본 설정
- **Given**: GitHub Actions 워크플로우가 필요함
- **When**: 개발자가 코드를 푸시할 때
- **Then**: 자동으로 빌드, 테스트, 린트 검사가 실행됨
- **Status**: ✅ Completed

### Sprint #1 - 아키텍처 검증 및 기본 Entity (Week 2)
**목표**: 데이터베이스 연결, 기본 Entity 생성, TDD 환경 구축

#### User Story 1: 데이터베이스 연결 및 기본 Entity 생성
- **Given**: PostgreSQL 데이터베이스가 준비됨
- **When**: 개발자가 User, Course, Company Entity를 생성하고 데이터베이스에 연결할 때
- **Then**: 테이블이 생성되고 JPA Repository를 통해 CRUD가 가능함
- **Acceptance Criteria**:
  - PostgreSQL 연결 성공
  - User, Course, Company 기본 테이블 생성
  - JPA Repository 정상 동작
- **Status**: ⏳ Pending

#### User Story 2: TDD 환경 설정 및 샘플 테스트 작성
- **Given**: Jest(Frontend), JUnit(Backend) 테스트 환경이 설정됨
- **When**: 개발자가 UserService에 대한 테스트를 작성할 때
- **Then**: Red-Green-Refactor 사이클이 정상 동작하고 80% 이상 커버리지가 확보됨
- **Acceptance Criteria**:
  - 단위 테스트 환경 구축
  - 테스트 커버리지 80% 이상
  - CI/CD 테스트 자동화
- **Status**: ⏳ Pending

#### User Story 3: 기본 도메인 모델 설계
- **Given**: 클린 아키텍처 기반 도메인 모델이 필요함
- **When**: 개발자가 User, Course, Company 도메인을 설계할 때
- **Then**: Entity, Repository, Service 계층이 분리되어 구현됨
- **Acceptance Criteria**:
  - Domain Entity 정의
  - Repository Interface 정의
  - Service Layer 구현
- **Status**: ⏳ Pending

### Sprint #2 - 웹 표준 준수 및 보안 기본 설정 (Week 3)
**목표**: SFR-001 시스템 표준 준수, SFR-011 보안 기능 기본 구현

#### User Story 1: 반응형 웹 표준 구현 (SFR-001)
- **Given**: Spring Framework 기반 구현과 HTML5, CSS3 웹 표준이 필요함
- **When**: 사용자가 PC, 모바일, 태블릿으로 접속할 때
- **Then**: 모든 디바이스에서 정상적으로 화면이 표시됨
- **Acceptance Criteria**:
  - 모바일 화면에서 가로 스크롤바가 발생하지 않는다
  - W3C 웹 표준 유효성 검사를 통과한다
  - Active-X 등 비표준 기술을 사용하지 않는다
  - 공공기관 표준화 지침 준수
- **Status**: ⏳ Pending

#### User Story 2: 기본 보안 설정 구현 (SFR-011)
- **Given**: 보안 요구사항이 정의됨
- **When**: 사용자가 시스템을 사용할 때
- **Then**: 기본 보안 기능이 동작함
- **Acceptance Criteria**:
  - 일정 시간 활동 없을 시 자동 세션 타임아웃
  - 세션 타임아웃 시간(예: 30분)을 설정할 수 있다
  - 개인정보 조회에 대한 로그 관리
- **Status**: ⏳ Pending

#### User Story 3: CI/CD 파이프라인 구현
- **Given**: GitHub Actions 워크플로우가 필요함
- **When**: 개발자가 코드를 푸시할 때
- **Then**: 자동으로 빌드, 테스트, 보안 검사가 실행됨
- **Acceptance Criteria**:
  - 자동 빌드 및 테스트
  - 코드 품질 검사 (ESLint, SpotBugs)
  - 보안 취약점 스캔
- **Status**: ⏳ Pending

---

## Release 0.2 - 사용자 관리 & 인증

### Sprint #3 - 회원가입 시스템 (Week 4)
**목표**: SFR-008 회원가입 기능 완전 구현

#### User Story 1: 본인인증 기반 회원가입 구현 (SFR-008)
- **Given**: 신규 사용자가 회원가입을 원함
- **When**: 사용자가 본인인증을 통해 회원가입을 진행할 때
- **Then**: 재직자/구직자 유형에 따라 적절한 양식이 제공됨
- **Acceptance Criteria**:
  - 본인인증을 통한 가입 (주민등록번호 수집 시 고지 의무 준수)
  - 재직자/구직자 유형 구분 가입
  - 재직자는 소속사 입력 필수, 구직자는 별도 양식 작성
- **Status**: ⏳ Pending

#### User Story 2: 아이디 중복 검사 및 유효성 검증 (SFR-008)
- **Given**: 회원가입 시 아이디 중복 확인이 필요함
- **When**: 사용자가 중복된 아이디로 가입을 시도할 때
- **Then**: 즉시 오류 메시지가 표시됨
- **Acceptance Criteria**:
  - 중복된 아이디로 가입을 시도하면 즉시 오류 메시지를 표시한다
  - 실시간 아이디 중복 검사
  - 아이디 유효성 규칙 적용
- **Status**: ⏳ Pending

#### User Story 3: 재직자 승인 프로세스 구현 (SFR-008)
- **Given**: 재직자는 소속사 담당자 승인이 필요함
- **When**: 재직자가 회원가입을 완료할 때
- **Then**: 승인 전까지 일부 기능 사용이 제한됨
- **Acceptance Criteria**:
  - 재직자 회원은 소속사 담당자 승인 전까지 일부 기능 사용이 제한된다
  - 승인 대기 상태 관리
  - 승인/반려 알림 기능
- **Status**: ⏳ Pending

### Sprint #4 - 협약사 가입 시스템 (Week 5)
**목표**: SFR-009 협약사 가입 기능 완전 구현

#### User Story 1: 협약사 가입 프로세스 구현 (SFR-009)
- **Given**: 기업이 교육 위탁을 위해 협약을 원함
- **When**: 기업 담당자가 협약 신청을 할 때
- **Then**: 회사 정보와 담당자 정보가 등록됨
- **Acceptance Criteria**:
  - 협약을 원하는 기업 담당자가 교육 위탁 목적으로 협약 신청
  - 회사 정보 입력 및 담당자 등록
  - 과정 담당자(관리자)의 승인 후 협약사로 등록 완료
- **Status**: ⏳ Pending

#### User Story 2: 사업자등록번호 중복 방지 (SFR-009)
- **Given**: 협약사 중복 가입을 방지해야 함
- **When**: 이미 등록된 사업자등록번호로 가입을 시도할 때
- **Then**: 중복 가입이 방지됨
- **Acceptance Criteria**:
  - 사업자등록번호를 기준으로 중복 가입을 방지한다
  - 중복 시도 시 명확한 오류 메시지 제공
- **Status**: ⏳ Pending

#### User Story 3: 협약사 승인 프로세스 (SFR-009)
- **Given**: 관리자 승인이 필요함
- **When**: 관리자가 승인 처리 전일 때
- **Then**: 협약사 기능이 비활성화됨
- **Acceptance Criteria**:
  - 관리자가 승인 처리 전까지 협약사 기능은 비활성화된다
  - 승인 대기 상태 관리
  - 승인 완료 후 협약사 기능 활성화
- **Status**: ⏳ Pending

### Sprint #5 - 사용자 권한 관리 (Week 6)
**목표**: SFR-005, SFR-011, SFR-012 권한 및 회원 관리 시스템 구현

#### User Story 1: 사용자 권한 관리 시스템 구현 (SFR-005)
- **Given**: 관리자, 강사, 교육생 등 사용자 유형이 정의됨
- **When**: 강사가 관리자 메뉴에 접근을 시도할 때
- **Then**: 접근이 차단됨
- **Acceptance Criteria**:
  - 시스템 사용자 유형(관리자, 강사, 교육생 등)별로 권한을 차등 부여
  - 강사 계정은 관리자 메뉴에 접근할 수 없다
  - 역할 기반 접근 제어(RBAC) 구현
- **Status**: ⏳ Pending

#### User Story 2: 관리자 2차 인증 시스템 구현 (SFR-011)
- **Given**: 관리자 보안이 강화되어야 함
- **When**: 관리자가 로그인할 때
- **Then**: 2차 인증이 요구됨 (설정 가능)
- **Acceptance Criteria**:
  - 관리자 로그인 시 2차 인증 기능
  - 로그인 2차 인증 사용 여부를 설정할 수 있다
  - 개인정보 조회에 대한 IP 제한 또는 로그 관리
- **Status**: ⏳ Pending

#### User Story 3: 회원 관리 기능 구현 (SFR-012)
- **Given**: 관리자가 회원을 관리해야 함
- **When**: 관리자가 회원 정보를 조회/수정할 때
- **Then**: 전체 회원 정보에 대한 CRUD 기능이 제공됨
- **Acceptance Criteria**:
  - 전체 회원 정보 조회/수정/탈퇴/승인 처리
  - 회원별 학습 이력 및 증명서 발급 관리
  - 탈퇴한 회원의 개인정보는 즉시 파기된다
- **Status**: ⏳ Pending

---

## Release 0.3 - 과정 관리 & 수강 신청

### Sprint #6 - 과정 관리 시스템 (Week 7)
**목표**: 교육 과정 등록 및 관리 기능 구현

#### User Story 1: 과정 등록 및 관리 (SFR-018)
- **Given**: 교과목 및 교육 과정이 필요함
- **When**: 관리자가 과정을 등록할 때
- **Then**: 과정 정보가 등록되고 조회/수정/삭제가 가능함
- **Status**: ⏳ Pending

#### User Story 2: 시간표 관리 시스템
- **Given**: 차수 및 시간표 등록이 필요함
- **When**: 관리자가 시간표를 엑셀로 업로드할 때
- **Then**: 양식이 올바르면 일괄 등록되고, 틀리면 오류 메시지가 표시됨
- **Status**: ⏳ Pending

#### User Story 3: 강사 관리 시스템 (SFR-014)
- **Given**: 강사 정보 관리가 필요함
- **When**: 관리자가 강사를 삭제하려 할 때
- **Then**: 배정된 강의가 있으면 삭제 불가 메시지가 표시됨
- **Status**: ⏳ Pending

### Sprint #7 - 과정 소개 및 신청 (Week 8)
**목표**: 사용자별 과정 조회 및 신청 기능 구현

#### User Story 1: 과정 소개 페이지 구현 (SFR-007)
- **Given**: 사용자가 과정 정보를 확인하고 싶어함
- **When**: 재직자/구직자가 과정 목록을 조회할 때
- **Then**: 권한에 따라 신청 가능한 과정이 구분되어 표시됨
- **Status**: ⏳ Pending

#### User Story 2: 과정 신청 시스템
- **Given**: 로그인한 사용자가 과정 신청을 원함
- **When**: 사용자가 '신청' 버튼을 클릭할 때
- **Then**: 신청이 완료되고 마이페이지에서 '승인 대기' 상태를 확인할 수 있음
- **Status**: ⏳ Pending

#### User Story 3: 비로그인 사용자 제한
- **Given**: 로그인하지 않은 사용자가 접근함
- **When**: 비로그인 사용자가 과정 상세 페이지를 볼 때
- **Then**: '신청' 버튼이 보이지 않음
- **Status**: ⏳ Pending

### Sprint #8 - 마이페이지 및 수강 관리 (Week 9)
**목표**: 개인별 수강 현황 관리 기능 구현

#### User Story 1: 마이페이지 구현 (SFR-010)
- **Given**: 사용자가 자신의 수강 현황을 확인하고 싶어함
- **When**: 사용자가 마이페이지에 접속할 때
- **Then**: 신청, 대기, 수강중, 수료 등 상태별 현황이 조회됨
- **Status**: ⏳ Pending

#### User Story 2: 수강 취소 기능
- **Given**: 사용자가 수강을 취소하고 싶어함
- **When**: 관리자 승인 이후 취소를 시도할 때
- **Then**: 교육생이 임의로 취소할 수 없음
- **Status**: ⏳ Pending

#### User Story 3: 증명서 발급 시스템
- **Given**: 수료한 사용자가 증명서를 원함
- **When**: 사용자가 수료증을 출력할 때
- **Then**: 진위 확인 번호가 포함된 수료증이 발급됨
- **Status**: ⏳ Pending

---

## Release 0.4 - 교육 관리 & 평가 시스템

### Sprint #9 - 수강생 관리 시스템 (Week 10)
**목표**: 수강 신청자 관리 및 출석 관리 구현

#### User Story 1: 수강 신청 승인/반려 (SFR-019)
- **Given**: 수강 신청자들의 승인 처리가 필요함
- **When**: 관리자가 수강 신청을 검토할 때
- **Then**: 승인/반려 처리가 가능하고 신청자에게 알림이 전송됨
- **Status**: ⏳ Pending

#### User Story 2: 출석 관리 시스템
- **Given**: 수강생의 출석 관리가 필요함
- **When**: 강사가 출석을 체크할 때
- **Then**: 출석률이 자동 계산되고 수료 기준과 연동됨
- **Status**: ⏳ Pending

#### User Story 3: 수료/미수료 처리
- **Given**: 수강 완료 후 수료 처리가 필요함
- **When**: 출석률이나 점수가 미달인 상태에서 수료 처리를 시도할 때
- **Then**: 경고 메시지가 표시됨
- **Status**: ⏳ Pending

### Sprint #10 - 평가 시스템 구축 (Week 11)
**목표**: 시험 문제 관리 및 출제 시스템 구현

#### User Story 1: 문제 등록/관리 시스템 (SFR-024)
- **Given**: 시험 문제 관리가 필요함
- **When**: 관리자가 문제를 등록할 때
- **Then**: 선택형, 단답형 등 다양한 유형의 문항이 등록됨
- **Status**: ⏳ Pending

#### User Story 2: 문제 버전 관리
- **Given**: 출제된 문제의 수정이 필요함
- **When**: 이미 출제된 문제를 수정하려 할 때
- **Then**: 수정이 불가능하며 새로운 버전으로 생성해야 함
- **Status**: ⏳ Pending

#### User Story 3: 시험 출제 시스템 (SFR-025)
- **Given**: 문제은행에서 시험지를 생성해야 함
- **When**: 관리자가 시험을 출제할 때
- **Then**: 문항 선택, 재시험 기준 설정, 미리보기 기능이 제공됨
- **Status**: ⏳ Pending

### Sprint #11 - 시험 응시 및 채점 (Week 12)
**목표**: 시험 응시 환경 및 자동 채점 시스템 구현

#### User Story 1: 시험 응시 시스템 (SFR-026)
- **Given**: 교육생이 시험에 응시해야 함
- **When**: PC 및 모바일에서 시험에 응시할 때
- **Then**: 인터넷 연결이 끊어져도 이전 답안이 복원됨
- **Status**: ⏳ Pending

#### User Story 2: 재시험 시스템
- **Given**: 기준 점수 미달 시 재응시가 필요함
- **When**: 재시험 기준 점수 미만일 때
- **Then**: 재응시 기회가 제공됨
- **Status**: ⏳ Pending

#### User Story 3: 자동 채점 시스템 (SFR-027)
- **Given**: 시험 후 채점이 필요함
- **When**: 시험이 완료될 때
- **Then**: 선택형, 단답형 등이 자동으로 채점됨 (띄어쓰기, 대소문자 구분)
- **Status**: ⏳ Pending

---

## Release 0.5 - 협약사 관리 & 통계

### Sprint #12 - 협약사 기능 구현 (Week 13)
**목표**: 협약사 담당자용 관리 기능 구현

#### User Story 1: 협약사 정보 관리 (SFR-021)
- **Given**: 협약사 담당자가 회사 정보를 관리해야 함
- **When**: 담당자가 회사 정보를 수정할 때
- **Then**: 관리자 승인 전까지 이전 정보가 유지됨
- **Status**: ⏳ Pending

#### User Story 2: 직원 관리 시스템 (SFR-022)
- **Given**: 협약사가 소속 직원을 관리해야 함
- **When**: 담당자가 직원 정보를 관리할 때
- **Then**: 정보 수정, 탈퇴, 승인 처리 및 비밀번호 초기화가 가능함
- **Status**: ⏳ Pending

#### User Story 3: 교육 관리 시스템 (SFR-023)
- **Given**: 협약사가 직원 교육을 관리해야 함
- **When**: 담당자가 직원 수강 신청을 할 때
- **Then**: 개별/일괄 신청 및 엑셀 일괄 처리가 가능함
- **Status**: ⏳ Pending

### Sprint #13 - 관리자 고급 기능 (Week 14)
**목표**: 시설 관리, 담당자 관리 등 고급 관리 기능 구현

#### User Story 1: 시설 및 장비 관리 (SFR-016)
- **Given**: 강의실, 훈련장비 관리가 필요함
- **When**: 관리자가 장비 사용 현황을 조회할 때
- **Then**: 월별/연별 사용 현황 및 유지보수 이력이 확인됨
- **Status**: ⏳ Pending

#### User Story 2: 담당자 관리 시스템 (SFR-015)
- **Given**: 내부 담당자 관리가 필요함
- **When**: 담당자 권한이 회수될 때
- **Then**: 관리자 페이지 접근이 불가능해짐
- **Status**: ⏳ Pending

#### User Story 3: 양성과정 신청서 관리 (SFR-020)
- **Given**: 구직자 양성과정 신청서 관리가 필요함
- **When**: 관리자가 신청서를 다운로드할 때
- **Then**: 여러 신청서를 ZIP 파일로 일괄 다운로드 가능함
- **Status**: ⏳ Pending

### Sprint #14 - 통계 및 사업 관리 (Week 15)
**목표**: 통계 시스템 및 사업 관리 기능 구현

#### User Story 1: 통계 시스템 구현 (SFR-017)
- **Given**: 협약사, 회원, 교육 실적 통계가 필요함
- **When**: 관리자가 통계를 조회할 때
- **Then**: 그래프와 함께 엑셀 다운로드 기능이 제공됨
- **Status**: ⏳ Pending

#### User Story 2: 엑셀 다운로드 기능
- **Given**: 통계 데이터의 로우데이터가 필요함
- **When**: '엑셀 다운로드'를 클릭할 때
- **Then**: 현재 필터링된 조건 기준으로 데이터가 추출됨
- **Status**: ⏳ Pending

#### User Story 3: 사업 관리 시스템 (SFR-027)
- **Given**: 사업 관련 문서 및 데이터 관리가 필요함
- **When**: 관리자가 예산 집행 현황을 조회할 때
- **Then**: 연도별, 사업별 예산 현황이 표시됨
- **Status**: ⏳ Pending

---

## Release 1.0 - 고급 기능 & 최적화

### Sprint #15 - 공통 게시판 시스템 (Week 16)
**목표**: SFR-003 게시판 시스템 완전 구현

#### User Story 1: 공통 게시판 시스템 구현 (SFR-003)
- **Given**: 공지사항, QnA, FAQ 게시판이 필요함
- **When**: 관리자가 공지사항을 등록할 때
- **Then**: HTML 에디터를 통해 내용을 작성하고 파일 업로드가 가능함
- **Acceptance Criteria**:
  - 공지사항, QnA, FAQ 등 게시판 기능 제공
  - 파일 업로드 및 다운로드 기능 제공
  - HTML 에디터를 통한 내용 작성 지원
- **Status**: ⏳ Pending

#### User Story 2: 게시판 권한 관리 (SFR-003)
- **Given**: 게시판별 접근 권한이 필요함
- **When**: 권한 없는 사용자가 게시판에 접근할 때
- **Then**: 적절한 권한 제어가 적용됨
- **Acceptance Criteria**:
  - 권한 없는 사용자는 공지사항 등록/수정/삭제 버튼이 보이지 않는다
  - QnA 게시판에서 비밀글 작성 시, 작성자와 관리자만 내용을 조회할 수 있다
- **Status**: ⏳ Pending

#### User Story 3: 시스템 성능 최적화
- **Given**: 대량의 데이터 처리 성능이 중요함
- **When**: 사용자가 통계나 대용량 데이터를 조회할 때
- **Then**: 3초 이내 응답 시간이 보장됨
- **Acceptance Criteria**:
  - 데이터베이스 쿼리 최적화
  - 프론트엔드 번들 최적화
  - 캐싱 전략 구현
- **Status**: ⏳ Pending

### Sprint #16 - 팝업, 설문, 사업소개 시스템 (Week 17)
**목표**: SFR-002, SFR-004, SFR-006 공통 기능 완성

#### User Story 1: 팝업 관리 시스템 구현 (SFR-002)
- **Given**: 교육과정 안내 및 주요 공지를 위한 팝업이 필요함
- **When**: 관리자가 팝업을 설정할 때
- **Then**: 모든 환경에서 팝업 기능이 정상 동작함
- **Acceptance Criteria**:
  - 교육과정 안내 및 주요 공지를 위한 팝업 기능 제공
  - 이미지, 링크, HTML 소스 삽입 등 에디터 기능 지원
  - 모바일 환경에서도 팝업 기능 지원
  - 관리자가 팝업 표시 기간을 설정할 수 있다
  - '오늘 하루 보지 않기' 기능이 정상 동작한다
- **Status**: ⏳ Pending

#### User Story 2: 설문 시스템 완성 (SFR-004)
- **Given**: 과정평가, 수요조사 등 다양한 설문이 필요함
- **When**: 관리자가 설문을 생성하고 결과를 확인할 때
- **Then**: 완전한 설문 시스템이 제공됨
- **Acceptance Criteria**:
  - 과정평가, 수요조사 등 다양한 설문 기능 제공
  - 5점 척도, 선다형, 단답형 등 다양한 문항 유형 지원
  - 설문 결과에 대한 통계 기능 제공
  - 설문 대상(예: 특정 과정 수료생)을 지정하여 설문을 발송할 수 있다
  - 설문 결과가 그래프 등 시각적인 형태로 표시된다
- **Status**: ⏳ Pending

#### User Story 3: 사업 소개 및 홍보 페이지 (SFR-006)
- **Given**: 국가인적자원개발컨소시엄 사업 홍보가 필요함
- **When**: 관리자가 페이지 내용을 수정할 때
- **Then**: 사업 소개 페이지가 완성됨
- **Acceptance Criteria**:
  - 국가인적자원개발컨소시엄 사업 및 참여 절차 소개
  - 발주사 소개(연혁, 목표, 위치 등) 페이지 구성
  - 관리자가 해당 페이지의 내용을 직접 수정할 수 있다
- **Status**: ⏳ Pending

### Sprint #17 - 시스템 안정화 및 배포 (Week 18)
**목표**: 최종 테스트, 문서화, 배포 준비

#### User Story 1: 통합 테스트 및 E2E 테스트
- **Given**: 전체 시스템 검증이 필요함
- **When**: 실제 사용자 시나리오를 테스트할 때
- **Then**: 모든 핵심 기능이 연동되어 정상 동작함
- **Status**: ⏳ Pending

#### User Story 2: 운영 문서 작성
- **Given**: 시스템 운영을 위한 문서가 필요함
- **When**: 운영자가 시스템을 관리할 때
- **Then**: 설치, 설정, 운영 가이드가 제공됨
- **Status**: ⏳ Pending

#### User Story 3: 사용자 매뉴얼 작성
- **Given**: 사용자 교육용 매뉴얼이 필요함
- **When**: 신규 사용자가 시스템을 사용할 때
- **Then**: 역할별 사용자 매뉴얼이 제공됨
- **Status**: ⏳ Pending

#### User Story 4: 프로덕션 배포
- **Given**: 완성된 시스템의 배포가 필요함
- **When**: 시스템을 프로덕션 환경에 배포할 때
- **Then**: 무중단 배포가 성공하고 모니터링이 활성화됨
- **Status**: ⏳ Pending

---

## 품질 보증 체크리스트

### 각 Sprint 완료 시 확인사항
- [ ] TDD 사이클 준수 (Red-Green-Refactor)
- [ ] 테스트 커버리지 80% 이상
- [ ] SOLID 원칙 적용
- [ ] 클린 아키텍처 준수
- [ ] 코드 리뷰 완료
- [ ] 보안 취약점 검토
- [ ] 성능 테스트 통과
- [ ] 문서 업데이트

### 각 Release 완료 시 확인사항
- [ ] 모든 Sprint User Story 완료
- [ ] 통합 테스트 통과
- [ ] PRD 요구사항 검증
- [ ] 스테이징 환경 배포 성공
- [ ] 사용자 승인 테스트 완료

---

## 리스크 관리

### 기술적 리스크
- **아키텍처 복잡성**: Sprint #0에서 충분한 검증 필요
- **성능 이슈**: Sprint #15에서 집중적 최적화
- **보안 취약점**: 각 Sprint마다 보안 검토 필수

### 일정 리스크
- **요구사항 변경**: 2주차마다 스테이크홀더 리뷰
- **기술적 난이도**: 각 Sprint 시작 전 기술 검토
- **리소스 부족**: Sprint 계획 시 여유분 20% 확보

### 완화 전략
- 매주 회고를 통한 프로세스 개선
- 지속적 통합으로 조기 이슈 발견
- 페어 프로그래밍으로 지식 공유

---

*본 계획서는 프로젝트 진행에 따라 지속적으로 업데이트됩니다.*