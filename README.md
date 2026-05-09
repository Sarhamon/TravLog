# TravLog

여행의 **계획 → 일정 → 이력**을 단계별로 기록하는 Spring Boot 학습 프로젝트입니다. 가기 전에 큰 그림(계획)을 그리고, 구체적인 일자/장소(일정)를 채우고, 다녀온 뒤 실제 결과(이력)를 남깁니다.

## 도메인 모델

3단계로 명시적으로 분리한 모델입니다.

| 단계 | 의미 | 주요 필드 |
|------|------|-----------|
| **Plan (계획)** | 가기 전 단계의 큰 그림 | 제목, 목적지, 기간, 예산, 메모 |
| **Schedule (일정)** | 계획에 종속된 일자/장소/시간 단위 항목 | 날짜, 시간, 장소, 메모 |
| **History (이력)** | 일정에 대한 실제 결과 기록 | 상태(DONE/SKIPPED/CHANGED), 실제 장소, 실제 시간, 후기 |

`Plan 1 — N Schedule 1 — 1 History` 관계이며, 상위 엔티티 삭제 시 하위는 cascade로 함께 삭제됩니다.

## 기술 스택

- **Java 17 / Gradle (Groovy DSL)**
- **Spring Boot 4.0.x** (Web MVC, Data JPA, Validation)
- **Mustache** — 서버사이드 템플릿
- **DB** — 개발 H2(인메모리) / 운영 MySQL, JPA Auditing(`createdAt`/`updatedAt`)
- **Lombok** — `@Getter`/`@Setter`/`@RequiredArgsConstructor` 부분 적용
- **Bootstrap 5** (CDN) — 헤더/푸터 partial + 카드/폼/테이블 스타일
- **JUnit 5** + Mockito — Service는 `@DataJpaTest`, Controller는 `@WebMvcTest`

## 실행

기본 프로파일은 `dev`(H2 인메모리)로, 별도 설정 없이 즉시 실행됩니다.

```bash
./gradlew bootRun
```

기본 포트는 8080입니다.

### 프로파일

| 프로파일 | DB | ddl-auto | 설정 파일 |
|----------|----|----------|-----------|
| `dev` (기본) | H2 인메모리 | `create-drop` | [application-dev.properties](src/main/resources/application-dev.properties) |
| `prod` | MySQL | `update` | [application-prod.properties](src/main/resources/application-prod.properties) |

운영 프로파일로 띄우려면 환경변수 3개를 설정하고 프로파일을 활성화합니다.

```bash
export DB_URL='jdbc:mysql://localhost:3306/travlog?useUnicode=true&characterEncoding=utf8'
export DB_USERNAME='travlog'
export DB_PASSWORD='...'
SPRING_PROFILES_ACTIVE=prod ./gradlew bootRun
```

비밀번호는 환경변수로 주입하므로 저장소에 커밋되지 않습니다. 실제 운영 배포에서는 `ddl-auto=validate` + Flyway/Liquibase 같은 마이그레이션 도구 사용을 권장합니다.

| URL | 설명 |
|-----|------|
| http://localhost:8080/ | 대시보드 (통계 + 진행중/다가올/다녀온 위젯) |
| http://localhost:8080/plans | 계획 목록 (검색/필터 — 키워드, 출발일 범위, 상태) |
| http://localhost:8080/plans/new | 새 계획 등록 |
| http://localhost:8080/plans/{id} | 계획 상세 + 일정 추가/수정/삭제 |
| http://localhost:8080/plans/{id}/schedules/{scheduleId}/history | 이력 기록 |
| http://localhost:8080/h2-console | H2 콘솔 (JDBC URL: `jdbc:h2:mem:travlog`) |

## 주요 기능

- **계획/일정/이력 CRUD** — 폼 기반 입력, PRG 패턴
- **검증** — Bean Validation 어노테이션 + 교차 검증
  - 계획: 도착일 ≥ 출발일
  - 일정: 날짜가 계획 기간 내
- **검색/필터** — Spring Data JPA `Specification`으로 동적 조건 조합
- **대시보드** — 오늘 기준으로 진행중/다가올/다녀온 분류 및 이력 상태 카운트
- **글로벌 예외 처리** — `@ControllerAdvice`로 도메인 NotFoundException → 친절한 404 페이지

## 테스트

```bash
./gradlew test
```

- `@DataJpaTest` — Service/Repository는 실제 H2와 JPA로 검증
- `@WebMvcTest` + `@MockitoBean` — Controller는 검증 동작/뷰 이름/리다이렉트 확인

## 디렉토리 구조

```
src/main/java/com/travlog/travlog/
├── common/              # BaseEntity, FormErrors, GlobalExceptionHandler, JpaAuditingConfig
├── dashboard/           # DashboardController, DashboardView
├── plan/                # Plan, PlanForm, PlanSearchCondition, PlanSpecs, ...
├── schedule/            # Schedule, ScheduleForm, ScheduleController, ...
├── history/             # History, HistoryStatus, HistoryForm, ...
└── TravlogApplication.java

src/main/resources/
├── application.properties
└── templates/
    ├── layout/          # header.mustache, footer.mustache (공통 레이아웃)
    ├── error/           # 404.mustache, 500.mustache
    ├── plan/            # list, form (생성/수정 공용), detail
    ├── schedule/        # edit
    ├── history/         # form
    └── dashboard.mustache
```

## 비고

학습용 프로젝트로, 인증/사용자 관리는 의도적으로 포함하지 않았습니다.
