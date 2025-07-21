# Railway 배포 상태 확인 및 테스트 가이드

## 🔍 배포 상태 확인

### 1. Railway 대시보드 접속
- **URL**: https://railway.app
- GitHub 계정으로 로그인

### 2. 배포 상태 확인
1. **프로젝트 선택**: `lms-backend` 또는 생성된 프로젝트명
2. **Deployments 탭**: 최신 배포 상태 확인
3. **빌드 로그**: Ubuntu 이미지 다운로드 → Java 17 설치 → Gradle 빌드

### 3. 배포 트리거 (필요시)
- **자동 배포**: Git push시 자동 트리거됨 ✅
- **수동 배포**: "Deploy" 버튼 클릭
- **재배포**: "Redeploy" 버튼으로 강제 재시작

## 📊 현재 설정 상태

### Docker 설정
```json
{
  "dockerfilePath": "backend/Dockerfile.ubuntu",
  "healthcheckPath": "/api/health"
}
```

### 환경 변수 (Variables 탭에서 설정)
```
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
```

### 빌드 체인
```
Ubuntu 22.04 → Java 17 설치 → Gradle 빌드 → Spring Boot 실행
```

## 🧪 배포 후 테스트

### 1. Health Check
```bash
curl https://[your-domain].railway.app/api/health
```
**예상 응답**: `{"status":"UP"}`

### 2. User Story 2: 아이디 검증 API
```bash
# 아이디 중복 검사
curl https://[your-domain].railway.app/api/registration/validate-username/admin

# 실시간 유효성 검증
curl https://[your-domain].railway.app/api/registration/quick-validate-username?username=test123

# 아이디 추천
curl https://[your-domain].railway.app/api/registration/recommend-username
```

### 3. 사용자 등록 테스트
```bash
curl -X POST https://[your-domain].railway.app/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser2024",
    "email": "test@example.com",
    "password": "SecurePass123!",
    "role": "STUDENT"
  }'
```

## 🚨 문제 해결

### 빌드 실패시
1. **로그 확인**: Deployments → Build Logs
2. **Dockerfile 확인**: Ubuntu 이미지 다운로드 성공 여부
3. **Java 설치**: `apt-get install openjdk-17-jdk` 성공 여부

### 런타임 오류시
1. **환경 변수**: Variables 탭에서 `SPRING_PROFILES_ACTIVE=prod` 확인
2. **포트 설정**: `SERVER_PORT=8080` 확인
3. **Health Check**: `/api/health` 엔드포인트 응답 확인

### 네트워크 문제시
1. **도메인 확인**: Railway 자동 생성 도메인 사용
2. **HTTPS 사용**: Railway는 자동으로 HTTPS 제공
3. **CORS 설정**: 필요시 Spring Boot CORS 설정 확인

## ✅ 배포 성공 지표

1. **빌드 완료**: ✅ "Build successful"
2. **컨테이너 실행**: ✅ "Container started"
3. **Health Check**: ✅ 200 OK 응답
4. **API 응답**: ✅ JSON 형태 정상 응답
5. **User Story 2**: ✅ 아이디 검증 기능 정상 작동

## 📱 모니터링

### Railway 대시보드
- **메트릭**: CPU, 메모리 사용량
- **로그**: 애플리케이션 로그 실시간 확인
- **도메인**: 자동 생성된 HTTPS 도메인

### 성능 지표
- **응답 시간**: < 500ms
- **메모리 사용량**: < 512MB
- **CPU 사용량**: < 50%

Railway에서 Ubuntu 기반 빌드가 성공적으로 진행될 것입니다! 🎉