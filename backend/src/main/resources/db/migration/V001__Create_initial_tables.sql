-- V001__Create_initial_tables.sql
-- LMS 초기 테이블 생성 스크립트

-- 회사 테이블 (협약사)
CREATE TABLE companies (
    id BIGSERIAL PRIMARY KEY,
    business_number VARCHAR(12) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    representative_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(255),
    address VARCHAR(500),
    contract_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 사용자 테이블
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    user_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    company_id BIGINT REFERENCES companies(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 과정 테이블
CREATE TABLE courses (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    target_audience VARCHAR(100),
    duration_hours INTEGER NOT NULL,
    max_participants INTEGER,
    course_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 인덱스 생성
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_company_id ON users(company_id);
CREATE INDEX idx_companies_business_number ON companies(business_number);
CREATE INDEX idx_courses_course_type ON courses(course_type);
CREATE INDEX idx_courses_status ON courses(status);

-- 코멘트 추가
COMMENT ON TABLE companies IS '협약사 정보';
COMMENT ON TABLE users IS '사용자 정보 (관리자, 강사, 교육생)';
COMMENT ON TABLE courses IS '교육 과정 정보';

COMMENT ON COLUMN users.user_type IS 'ADMIN, INSTRUCTOR, STUDENT, COMPANY_MANAGER';
COMMENT ON COLUMN users.status IS 'ACTIVE, INACTIVE, PENDING, SUSPENDED';
COMMENT ON COLUMN companies.contract_status IS 'PENDING, APPROVED, REJECTED, TERMINATED';
COMMENT ON COLUMN courses.course_type IS 'EMPLOYEE, JOB_SEEKER, COMMON';
COMMENT ON COLUMN courses.status IS 'ACTIVE, INACTIVE, DRAFT';