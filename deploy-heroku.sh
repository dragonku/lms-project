#!/bin/bash

# Heroku 배포 스크립트
echo "🚀 Heroku 배포를 시작합니다..."

# Heroku CLI 설치 확인
if ! command -v heroku &> /dev/null; then
    echo "📦 Heroku CLI를 설치합니다..."
    curl https://cli-assets.heroku.com/install.sh | sh
fi

# Heroku 로그인
echo "🔐 Heroku 로그인이 필요합니다..."
heroku login --interactive

# Heroku 앱 생성
APP_NAME="lms-backend-$(date +%s)"
echo "📱 Heroku 앱을 생성합니다: $APP_NAME"
heroku create $APP_NAME

# Java 빌드팩 설정
echo "☕ Java 빌드팩을 설정합니다..."
heroku buildpacks:set heroku/java -a $APP_NAME

# 환경 변수 설정
echo "⚙️ 환경 변수를 설정합니다..."
heroku config:set SPRING_PROFILES_ACTIVE=prod -a $APP_NAME

# 루트 디렉토리 설정 (Heroku는 루트에서만 배포 가능)
echo "📁 프로젝트 구조를 조정합니다..."
cp -r backend/* .
cp backend/.* . 2>/dev/null || true

# Git 설정
git add .
git commit -m "feat: Heroku 배포를 위한 프로젝트 구조 조정"

# Heroku Git 원격 저장소 추가
heroku git:remote -a $APP_NAME

# 배포
echo "🚀 Heroku에 배포합니다..."
git push heroku feature/sprint-2-user-story-2-security-settings:main

echo "✅ Heroku 배포가 완료되었습니다!"
echo "🔗 URL: https://$APP_NAME.herokuapp.com"
echo "🧪 테스트: curl https://$APP_NAME.herokuapp.com/api/health"