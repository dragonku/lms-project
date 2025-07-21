#!/bin/bash

# Render 배포 안내 스크립트
echo "🌟 Render 배포 안내"
echo ""

echo "📋 Render 배포 단계:"
echo "1. https://render.com 접속"
echo "2. GitHub 계정으로 로그인"
echo "3. 'New' → 'Web Service' 선택"
echo "4. 리포지토리 연결: dragonku/lms-project"
echo "5. 브랜치: feature/sprint-2-user-story-2-security-settings"
echo ""

echo "⚙️ Render 설정:"
echo "- Name: lms-backend"
echo "- Region: Oregon (US West)"
echo "- Branch: feature/sprint-2-user-story-2-security-settings"
echo "- Root Directory: backend"
echo "- Runtime: Docker"
echo "- Build Command: (자동 감지)"
echo "- Start Command: (자동 감지)"
echo ""

echo "🔧 환경 변수 설정:"
echo "- SPRING_PROFILES_ACTIVE: prod"
echo "- SERVER_PORT: 8080"
echo ""

echo "📊 Render 장점:"
echo "✅ 무료 플랜 제공"
echo "✅ Docker 완벽 지원"
echo "✅ 자동 SSL/HTTPS"
echo "✅ GitHub 자동 배포"
echo ""

echo "🔗 배포 후 테스트:"
echo "- Health Check: https://[app-name].onrender.com/api/health"
echo "- 아이디 검증: https://[app-name].onrender.com/api/registration/validate-username/test"
echo ""

echo "✨ Render는 Railway 대신 사용할 수 있는 훌륭한 대안입니다!"