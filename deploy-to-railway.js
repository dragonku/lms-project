#!/usr/bin/env node

/**
 * Railway 자동 배포 스크립트
 * GitHub 리포지토리를 Railway에 연결하고 배포합니다.
 */

const https = require('https');
const fs = require('fs');

console.log('🚀 Railway 배포 프로세스를 시작합니다...\n');

// Railway 설정 읽기
const config = JSON.parse(fs.readFileSync('.railway-config.json', 'utf8'));

console.log(`📦 프로젝트 설정:`);
console.log(`   이름: ${config.name}`);
console.log(`   리포지토리: ${config.repository}`);
console.log(`   브랜치: ${config.branch}`);
console.log(`   루트 디렉토리: ${config.rootDirectory}`);
console.log(`   Docker 파일: ${config.dockerfilePath}\n`);

// Railway 토큰 확인
const railwayToken = process.env.RAILWAY_TOKEN;

if (!railwayToken) {
    console.log('❌ RAILWAY_TOKEN 환경 변수가 설정되지 않았습니다.\n');
    console.log('🌐 수동 배포 방법:');
    console.log('1. https://railway.app 접속');
    console.log('2. GitHub 계정으로 로그인');
    console.log('3. "New Project" → "Deploy from GitHub repo"');
    console.log(`4. "${config.repository}" 리포지토리 선택`);
    console.log(`5. 브랜치: "${config.branch}" 선택`);
    console.log(`6. Root Directory: "${config.rootDirectory}" 설정`);
    console.log('\n📋 환경 변수 설정:');
    Object.entries(config.environmentVariables).forEach(([key, value]) => {
        console.log(`   ${key}=${value}`);
    });
    console.log('\n✅ 모든 설정 파일이 준비되었습니다!');
    console.log('📊 배포 후 다음 URL로 테스트하세요:');
    console.log('   https://[your-app].railway.app/api/health');
    console.log('   https://[your-app].railway.app/api/registration/validate-username/testuser');
    process.exit(0);
}

console.log('🔑 Railway 토큰이 설정되었습니다. API를 통해 배포를 시도합니다...');

// Railway API 요청 (예시 - 실제 구현 시 Railway API 문서 참조)
console.log('🚧 Railway API를 통한 자동 배포는 개발 중입니다.');
console.log('현재는 웹사이트를 통한 수동 배포를 권장합니다.\n');

console.log('🎯 빠른 배포 링크:');
console.log(`https://railway.app/new?template=https://github.com/${config.repository}&branch=${config.branch}`);