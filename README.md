# SKKU TABLE - 축제 부스 예약 관리 시스템

[English](./README.en.md) | **한국어**

SKKU TABLE은 성균관대학교의 축제 부스 예약을 효율적으로 관리하기 위해 설계된 웹 애플리케이션입니다. 학생들은 다양한 축제의 부스를 탐색하고 예약할 수 있으며, 부스 운영자는 예약 및 시간대별 운영을 쉽게 관리할 수 있습니다.

## 🎯 주요 기능

- **축제 및 부스 탐색**: 다양한 축제 및 부스 정보 검색 및 탐색
- **실시간 예약 시스템**: 실시간 가용성을 갖춘 시간대 기반 부스 예약
- **사용자 관리**: Firebase 인증을 통한 안전한 사용자 관리
- **'좋아요' 시스템**: 좋아하는 축제 및 부스에 '좋아요' 표시
- **관리자 패널**: 축제 및 부스 관리를 위한 관리 기능
- **반응형 PWA**: 모바일 친화적인 프로그레시브 웹 앱

## 🏗️ 기술 스택

### 프론트엔드

- **프레임워크**: Next.js 15 (App Router)
- **언어**: TypeScript
- **스타일링**: Tailwind CSS
- **상태 관리**: Zustand
- **인증**: Firebase Auth
- **UI 컴포넌트**: Radix UI, Lucide React
- **PWA**: next-pwa

### 백엔드

- **프레임워크**: Spring Boot 3.5
- **언어**: Java 21
- **데이터베이스**: MySQL
- **ORM**: Spring Data JPA
- **마이그레이션**: Flyway
- **이미지 저장소**: Cloudinary
- **인증**: Firebase Admin SDK
- **API 테스트**: Bruno

### 인프라

- **컨테이너화**: Docker & Docker Compose
- **리버스 프록시**: Caddy
- **CI/CD**: GitHub Actions
- **배포**: Oracle Cloud

## 📁 프로젝트 구조

```
skku-table/
├── frontend/                 # Next.js 프론트엔드
│   ├── app/                 # App Router 구조
│   │   ├── (auth)/         # 인증 페이지
│   │   ├── (client)/       # 클라이언트 페이지
│   │   │   └── (main)/     # 메인 페이지
│   │   └── admin/          # 관리자 페이지
│   ├── components/         # 재사용 가능한 컴포넌트
│   ├── libs/              # 유틸리티 라이브러리
│   └── stores/            # Zustand 상태 관리
├── backend/               # Spring Boot 백엔드
│   └── src/main/java/com/skkutable/
│       ├── controller/    # REST API 컨트롤러
│       ├── service/       # 비즈니스 로직
│       ├── domain/        # JPA 엔티티
│       ├── repository/    # 데이터 액세스 레이어
│       └── dto/          # 데이터 전송 객체
├── api-testing/          # Bruno API 테스트 컬렉션
├── functions/            # Firebase Cloud Functions
└── docker-compose.yml    # 개발 환경 설정
```

## 🚀 로컬 개발 환경 설정

## 목차

## 프로젝트 설정

### 레포지토리 클론

```bash
git clone https://github.com/skku-table/skku-table.git
```

![clone-skku-table](/assets/how-to-set-up-local-dev-env/clone.png)

위 명령어를 실행하여 프로젝트를 클론합니다.

### 프로젝트 디렉토리 열기

```bash
code skku-table
```

또는

```bash
cursor skku-table
```

### 기존 Docker 컨테이너 및 볼륨 정리

![delete-container](/assets/how-to-set-up-local-dev-env/delete-container.png)
![delete-volume](/assets/how-to-set-up-local-dev-env/delete-volume.png)

위 이미지와 같이 기존 Docker 컨테이너 및 볼륨을 삭제합니다.

## 환경 설정

### 루트 `.env` 파일 생성

프로젝트 루트 디렉토리에 다음 형식으로 `.env` 파일을 생성합니다:

```plaintext
CLOUDINARY_API_KEY=YOUR_CLOUDINARY_API_KEY
CLOUDINARY_API_SECRET=YOUR_CLOUDINARY_API_SECRET
CLOUDINARY_CLOUD_NAME=YOUR_CLOUDINARY_CLOUD_NAME
MYSQL_DATABASE=skku-table-dev
MYSQL_ROOT_PASSWORD=YOUR_MYSQL_ROOT_PASSWORD
SPRING_DATASOURCE_URL=jdbc:mysql://database:3306/skku-table-dev?allowPublicKeyRetrieval=true&useSSL=false
SPRING_PROFILES_ACTIVE=dev
FIREBASE_SERVICE_ACCOUNT_KEY=YOUR_FIREBASE_SERVICE_ACCOUNT_KEY
```

### 프론트엔드 환경 변수 생성

`frontend/` 디렉토리에 다음 형식으로 `.env.local` 파일을 생성합니다:

```plaintext
NEXT_PUBLIC_API_URL=http://localhost:8080
NEXT_PUBLIC_API_KEY=YOUR_API_KEY
NEXT_PUBLIC_AUTH_DOMAIN=YOUR_AUTH_DOMAIN
NEXT_PUBLIC_PROJECT_ID=YOUR_PROJECT_ID
NEXT_PUBLIC_STORAGE_BUCKET=YOUR_STORAGE_BUCKET
NEXT_PUBLIC_MESSAGING_SENDER_ID=YOUR_MESSAGING_SENDER_ID
NEXT_PUBLIC_APP_ID=YOUR_APP_ID
NEXT_PUBLIC_MEASUREMENT_ID=YOUR_MEASUREMENT_ID
NEXT_PUBLIC_VAPID_PUBLIC_KEY=YOUR_VAPID_PUBLIC_KEY
NEXT_PUBLIC_ADMIN_SECRET=YOUR_ADMIN_SECRET
```

## 백엔드 설정 (MySQL + Spring Boot 애플리케이션)

`docker-compose.yml` 파일을 열어 백엔드를 실행합니다.

![docker-compose-yml](/assets/how-to-set-up-local-dev-env/docker-compose-yml.png)

`database` 서비스를 실행하려면 위의 `Run Service` 재생 버튼을 클릭합니다.
그러면 MySQL 컨테이너가 시작됩니다.
MySQL이 실행되면 Spring Boot 애플리케이션을 시작합니다.

`application` 서비스를 실행하려면 위의 `Run Service` 재생 버튼을 클릭합니다.
그러면 Spring Boot 애플리케이션이 시작됩니다.

좋습니다! 이제 백엔드가 실행 중입니다.
이제 Bruno를 열어 아래와 같이 로컬에서 실행 중인 백엔드 API를 테스트할 수 있습니다. (아래 설명된 시드 데이터에 따라 요청을 보내세요.)

![test-backend-api-with-bruno](/assets/how-to-set-up-local-dev-env/test-backend-api-with-bruno.png)

## 시드 데이터

백엔드가 로컬(노트북 또는 데스크톱)에서 실행될 때, 시드 데이터가 자동으로 데이터베이스에 로드됩니다.

시드 데이터는 레포지토리의 `backend/src/main/resources/db/seed/dev/R_seed_data.sql` 파일에 있습니다.

## 시드 데이터 관리

- `docker compose run --rm dev-flyway-clean` - 시드 데이터 삭제
- `docker compose run --rm dev-flyway-migrate` - 시드 데이터 적용
- `docker compose run --rm dev-flyway-info` - 마이그레이션 정보 표시

## 프론트엔드 설정 (Next.js)

프론트엔드 디렉토리로 이동하여 평소와 같이 개발 서버를 실행합니다.

```bash
cd frontend
pnpm install  # 필요한 경우
pnpm run dev
```

프론트엔드는 `http://localhost:3000`에서 실행되며 백엔드 API `http://localhost:8080`에 연결됩니다.

## 백엔드 개발 설정 (IDE 직접 실행)

IDE에서 직접 Spring Boot를 실행하려면 다음과 같이 구성합니다:

### 별도의 `.env` 파일 생성 (선택 사항)

백엔드만 실행하는 경우, 프로젝트 루트에 별도의 `.env` 파일을 생성할 수 있습니다:

```plaintext
CLOUDINARY_API_KEY=YOUR_CLOUDINARY_API_KEY
CLOUDINARY_API_SECRET=YOUR_CLOUDINARY_API_SECRET
CLOUDINARY_CLOUD_NAME=YOUR_CLOUDINARY_CLOUD_NAME
MYSQL_DATABASE=skku-table-dev
MYSQL_ROOT_PASSWORD=YOUR_MYSQL_ROOT_PASSWORD
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/skku-table-dev?allowPublicKeyRetrieval=true&useSSL=false
SPRING_PROFILES_ACTIVE=dev
FIREBASE_SERVICE_ACCOUNT_KEY=YOUR_FIREBASE_SERVICE_ACCOUNT_KEY
```

**참고:** `SPRING_DATASOURCE_URL`에 `localhost:3306`을 사용하세요 (Docker 컨테이너용인 `database:3306`이 아님).

### Spring Boot 프로필 구성

IntelliJ IDEA의 오른쪽 상단에 있는 `Run` 버튼 옆의 아이콘을 클릭한 다음 메뉴에서 `Edit Configurations...`를 선택합니다.

![edit-configurations](/assets/how-to-set-up-local-dev-env/edit-configurations.png)

![spring-boot-profile](/assets/how-to-set-up-local-dev-env/spring-boot-profile.png)

`Active Profiles` 섹션에 `dev`를 추가합니다.
`Environment variables` 섹션에 `.env` 파일의 경로를 추가합니다. (오른쪽의 폴더 아이콘을 클릭하여 `.env` 파일을 직접 선택할 수 있습니다.)

### 프로젝트 다시 빌드

IntelliJ IDEA에서 `Build` - `Rebuild Project`를 클릭합니다.
그러면 프로젝트가 다시 빌드됩니다.

### 백엔드 실행

평소와 같이 MySQL 컨테이너를 실행한 다음 백엔드를 시작합니다.
Spring은 dev 프로필을 적용하고 로컬 데이터베이스에 연결합니다.
시드 데이터도 적용됩니다.

## Java 코드 서식 지정

[Google 스타일 가이드 레포지토리](https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml)

![settings-code-style](/assets/how-to-set-up-local-dev-env/settings-code-style.png)

intellij-java-google-style.xml을 다운로드한 다음 IntelliJ IDEA에서 `Preferences` - `Code Style` - `Java`로 이동하여 `Schema` 탭을 클릭합니다.

위와 같이 톱니바퀴 아이콘이 표시됩니다. 클릭하여 다운로드한 파일을 선택합니다.

![import-scheme](/assets/how-to-set-up-local-dev-env/import-scheme.png)

![intellij-idea-code-style](/assets/how-to-set-up-local-dev-env/intellij-idea-code-style.png)

스키마 - 톱니바퀴 아이콘 - 다운로드한 파일 가져오기

## 코드 서식 적용

`Ctrl + Alt + L`(Mac의 경우 `Cmd + Alt + L`)을 눌러 코드 서식을 적용합니다.

## 📝 API 문서

API 문서는 `api-testing/` 디렉토리의 Bruno 테스트 컬렉션을 통해 제공됩니다. 컬렉션에는 다음에 대한 포괄적인 테스트 케이스가 포함됩니다:

- 사용자 인증 및 관리
- 축제 운영
- 부스 관리
- 예약 시스템 (v1 및 v2)
- 이미지 업로드 기능

## 🤝 기여하기

1. 레포지토리 포크
2. 기능 브랜치 생성 (`git checkout -b feature/amazing-feature`)
3. 변경 사항 커밋 (`git commit -m 'Add some amazing feature'`)
4. 브랜치에 푸시 (`git push origin feature/amazing-feature`)
5. Pull Request 열기

## 📄 라이선스

이 프로젝트는 MIT 라이선스에 따라 라이선스가 부여됩니다 - 자세한 내용은 [LICENSE](LICENSE) 파일을 참조하십시오.

## 🔧 개발 노트

### 주요 변경 사항 요약

1. **프론트엔드 환경 변수**: Next.js 환경 변수를 위해 `frontend/` 디렉토리에 별도의 `.env.local` 파일 생성
2. **백엔드 환경 변수**: Firebase 서비스 계정 키, MySQL 루트 암호 및 기타 새로운 환경 변수 추가
3. **데이터베이스 연결 URL**: `allowPublicKeyRetrieval=true&useSSL=false` 매개변수 추가
4. **프로덕션 환경**: CD 스크립트가 배포 중에 `.env.production` 파일을 동적으로 생성

### 개발 워크플로우

1. Docker Compose를 사용하여 MySQL 컨테이너 시작
2. Spring Boot 애플리케이션 실행 (Docker 또는 IDE를 통해)
3. Next.js 프론트엔드 개발 서버 실행
4. API 테스트 및 개발에 Bruno 사용

문제나 질문이 있는 경우 [Issues](https://github.com/skku-table/skku-table/issues) 섹션을 확인하거나 새 이슈를 생성하십시오.
