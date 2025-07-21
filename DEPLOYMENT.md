# LMS 프로젝트 배포 가이드

## Railway 배포 (권장)

### 사전 준비
1. [Railway 계정 생성](https://railway.app)
2. GitHub 계정 연동

### 배포 단계

#### 1. Railway에 GitHub 리포지토리 연결
1. Railway 대시보드에서 "New Project" 클릭
2. "Deploy from GitHub repo" 선택
3. `dragonku/lms-project` 리포지토리 선택
4. Branch: `main` 또는 `feature/sprint-2-user-story-2-security-settings` 선택

#### 2. 프로젝트 설정
- Service Name: `lms-backend`
- Root Directory: `/backend` (중요!)
- Build Command: `./gradlew build -x test`
- Start Command: 자동 감지됨 (Dockerfile 사용)

#### 3. 환경 변수 설정
Railway 대시보드에서 Variables 탭에 다음 추가:
```
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
DATABASE_URL=postgresql://user:password@hostname:port/database
```

#### 4. 도메인 설정
- Railway에서 자동으로 생성된 도메인 사용
- 또는 커스텀 도메인 연결

### 배포 파일 확인
✅ `railway.json` - Railway 설정 파일
✅ `backend/Dockerfile` - Docker 컨테이너 설정
✅ Health check endpoint: `/api/health`

## 대안 배포 옵션

### Heroku 배포
1. Heroku CLI 설치
2. `heroku create lms-backend-app`
3. `git push heroku main`
4. `Procfile` 사용됨

### Render 배포
1. [Render 계정 생성](https://render.com)
2. GitHub 리포지토리 연결
3. `render.yaml` 설정 자동 적용

## 배포 후 확인사항

### Health Check
```bash
curl https://your-domain/api/health
```

### API 테스트
```bash
# 사용자 등록 테스트
curl -X POST https://your-domain/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"password123"}'

# 아이디 중복 검사 테스트
curl https://your-domain/api/registration/validate-username/testuser
```

### 로그 확인
- Railway: 대시보드에서 Logs 탭 확인
- Heroku: `heroku logs --tail`
- Render: 대시보드에서 로그 확인

## 문제 해결

### 공통 이슈
1. **빌드 실패**: Java 21 지원 확인
2. **Health check 실패**: `/api/health` 엔드포인트 구현 확인
3. **데이터베이스 연결**: 환경 변수 설정 확인

### Railway 특화 이슈
1. **Root Directory**: `/backend` 설정 확인
2. **Dockerfile 경로**: `backend/Dockerfile` 확인
3. **포트 설정**: Railway가 자동으로 `PORT` 환경 변수 제공

## 성능 최적화

### 프로덕션 설정
- JVM 옵션: 메모리 최적화 적용됨
- G1GC: 가비지 컬렉션 최적화
- Health check: 30초 간격

### 모니터링
- Railway 대시보드에서 CPU/메모리 사용량 확인
- 로그를 통한 애플리케이션 상태 모니터링