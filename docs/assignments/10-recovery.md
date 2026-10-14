# Assignment 10: Recovery

## 학습 목표

장애 후 미완료 업무를 식별하고 복구하거나 운영자에게 넘긴다.

## 사전 요구사항

Assignment 07~09; PostgreSQL + Kafka + 3개 Java 프로세스 + WireMock.

## 제공 API 및 인터페이스

Saga 조회와 POST `/api/v1/recovery/run`; lastError, attempts, MANUAL_REVIEW 계약.

공통 요청·응답·오류 계약은 [api-contracts](../api-contracts.md), 데이터 소유권은 [architecture](../architecture.md)를 따른다.

Fixture는 WireMock으로 503, 지연, 연결 reset, Core 커밋 이후 응답 유실을 주입한다. 10-05는 프록시가 Core의 201을 수신했다는 latch 신호를 기다리고 응답을 보류한 채 Orchestrator 프로세스를 종료한다. DB 연결 장애는 pg_terminate_backend로 기존 연결을 종료한다. 장시간 미완료 상태는 테스트에서 updated_at을 과거로 설정해 재현한다. 재시도 한도는 lab.retry-limit=3, 호출 타임아웃은 lab.http-timeout-millis=1000이다.

## 구현 TODO

재시작 스캔·재시도 한도·결과 불확실성 해소·운영자 개입 상태를 구현한다. 제공 서비스는 UnsupportedOperationException으로 남겨 두었다. 테스트용 자금 공급·장애 주입은 운영 API가 아니다.

## 테스트 목록

| Step | 시나리오 |
| --- | --- |
| 10-01 | Orchestrator 재시작 |
| 10-02 | Kafka 소비자 재시작 |
| 10-03 | DB 연결 종료 후 복구 |
| 10-04 | 서비스 네트워크 단절 |
| 10-05 | 미확정 응답 중 프로세스 강제 종료 |
| 10-06 | 장시간 미완료 작업 탐지 |
| 10-07 | 재시도 한도 초과 |
| 10-08 | 운영자 개입 거래 식별 |

테스트는 각 Step을 독립 실행한다. Fixture는 선행 과제의 결과 파일을 재사용하지 않고 필요한 계좌와 자금을 매번 생성한다. 선행 기능의 구현은 사전 요구사항이다.

## 금융 불변식

Core 확정은 한 번; 중간 장애는 영속 상태로 복구; 확정 원장 삭제나 후속 실패로 자금 역전 없음.

## 실행 방법

```bash
./scripts/lab test 10-01
./scripts/lab verify 10
```

인프라만 점검: `./gradlew :banking-lab-tests:infrastructureTest -PlabInfrastructure=10`.

누적 검증은 01부터 10까지 실행한다. 최초 기능 테스트는 현재 501 TODO: A10로 실패해야 하며 ENVIRONMENT_ERROR와 구분한다.

## 완료 조건

해당 Assignment의 모든 Step과 누적 회귀 테스트 통과; 환경 오류 없음; 정합성 불변식 유지. API, 제공 테스트 및 초기 자금 계약을 바꾸어 통과시키지 않는다.

## 다음 Assignment

Assignment 11은 누락·중복 파생 데이터를 별도 대사한다.
