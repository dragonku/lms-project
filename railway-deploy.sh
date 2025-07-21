#!/bin/bash

# Railway 자동 배포 스크립트
# 사용법: ./railway-deploy.sh

echo "🚀 Railway 배포를 시작합니다..."

# 현재 브랜치 확인
current_branch=$(git branch --show-current)
echo "현재 브랜치: $current_branch"

# 최신 변경사항 확인
echo "📦 최신 변경사항을 푸시합니다..."
git push origin $current_branch

echo "✅ 코드가 GitHub에 푸시되었습니다."
echo ""
echo "🌐 Railway 웹사이트에서 배포를 완료하세요:"
echo "1. https://railway.app 접속"
echo "2. 'New Project' 클릭"
echo "3. 'Deploy from GitHub repo' 선택"
echo "4. 'dragonku/lms-project' 리포지토리 선택"
echo "5. 브랜치: '$current_branch' 선택"
echo "6. 설정:"
echo "   - Root Directory: /backend"
echo "   - Dockerfile Path: backend/Dockerfile"
echo ""
echo "📋 환경 변수 설정 (Variables 탭):"
echo "   SPRING_PROFILES_ACTIVE=prod"
echo "   SERVER_PORT=8080"
echo ""
echo "🔗 배포 후 테스트 URL:"
echo "   https://[your-app].railway.app/api/health"
echo "   https://[your-app].railway.app/api/registration/validate-username/testuser"
echo ""
echo "🎉 모든 설정 파일이 준비되었습니다!"