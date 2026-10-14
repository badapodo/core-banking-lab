# Core Banking Lab

Java 21, Spring Boot 3, Gradle multi-module, PostgreSQL, Kafka, Testcontainers and Flyway 기반 금융 시스템 구현 실습 환경입니다.

API 계약과 실패하는 테스트를 제공하며, 학습자가 금융 업무 로직을 직접 완성합니다. Assignment 01은 보존하고 02~12를 추가했습니다. 계좌 잔액과 원장은 서비스 분리 후에도 하나의 Core 트랜잭션에서 변경합니다.

## 실행

```bash
./scripts/lab doctor
./scripts/lab test 01-01
./scripts/lab test 02-01
./scripts/lab test 03-02
./scripts/lab verify 05
./scripts/lab verify all
./scripts/lab status
```

lab.properties의 activeAssignment를 학습 중인 과제로 설정합니다. verify AA는 01부터 AA까지 누적 검증하며, verify all은 활성 범위를 사용합니다. status는 실제 실행 결과와 소스 변경 여부를 표시합니다.

환경 테스트는 통과해야 합니다. 기능 테스트는 초기 501 TODO: Axx 응답으로 실패하며, 정답 비즈니스 로직은 구현하지 않았습니다. 기본 Gradle test는 미래 과제를 실행하지 않습니다.

## 문서

- [Assignments](docs/lab-catalog.md): 과제별 API, Step 및 학습 문서
- [API 계약](docs/api-contracts.md): 요청·응답·오류·이벤트·상태
- [아키텍처](docs/architecture.md): 모듈 의존성과 금융 원자성 경계
- [테스트 가이드](docs/testing-guide.md): 실행, 독립성, 결과 해석
- [워크로드](docs/workload-guide.md): 데이터 출처와 측정 계약
- [검증 기록](docs/verification.md): 작업 전후 실행 결과 및 한계

## 모듈

banking-domain → banking-application → banking-infrastructure와 banking-api가 Core를 구성합니다. transfer-orchestrator와 banking-projections는 domain 계약만 공유하는 별도 실행 서비스입니다. banking-lab-tests는 운영 로직과 분리된 테스트·Fixture·장애 주입 환경입니다.

학습 시작점은 AccountServiceImpl 및 각 서비스의 *Skeleton과 HTTP adapter TODO입니다. 테스트에 맞춰 구현하고 누적 회귀로 검증합니다.
