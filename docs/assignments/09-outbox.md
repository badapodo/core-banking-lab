# Assignment 09: Transactional Outbox

## 학습 목표

금융 확정과 이벤트 기록을 한 DB 트랜잭션에 넣고 최종 전달한다.

## 사전 요구사항

Assignment 08; Core PostgreSQL + Kafka.

## 제공 API 및 인터페이스

`OutboxService`; GET `/api/v1/outbox?transferId=...`, POST `/api/v1/outbox/publish`, POST `/api/v1/outbox/replay` body `{transferId}`.

공통 요청·응답·오류 계약은 [api-contracts](../api-contracts.md), 데이터 소유권은 [architecture](../architecture.md)를 따른다.

Outbox 상태 계약은 PENDING, PUBLISHING, PUBLISHED이다. publish API는 처리 요청이 받아들여지면 200을 반환하며 최종 결과는 조건 기반 대기로 검증한다. 크래시 테스트는 브로커를 일시 정지한 상태에서 DB 커밋을 관찰하고 프로세스를 강제 종료한다. 중복 전달과 재시작 처리는 원 이벤트 ID를 재사용한다.

## 구현 TODO

Outbox 원자적 기록·발행·미완료 상태 재처리·원 이벤트 ID 보존을 구현한다. 제공 서비스는 UnsupportedOperationException으로 남겨 두었다. 테스트용 자금 공급·장애 주입은 운영 API가 아니다.

## 테스트 목록

| Step | 시나리오 |
| --- | --- |
| 09-01 | 이체와 Outbox 동시 커밋 |
| 09-02 | Outbox 기록 실패 시 전체 롤백 |
| 09-03 | DB 커밋 후 프로세스 종료 |
| 09-04 | Kafka 일시 장애 복구 |
| 09-05 | 미완료 이벤트 재발행 |
| 09-06 | 중복 발행에도 소비 효과 한 번 |
| 09-07 | Outbox 처리 상태 복구 |

테스트는 각 Step을 독립 실행한다. Fixture는 선행 과제의 결과 파일을 재사용하지 않고 필요한 계좌와 자금을 매번 생성한다. 선행 기능의 구현은 사전 요구사항이다.

## 금융 불변식

확정 거래당 이벤트 기록 1개; 금융 롤백이면 이벤트 없음. 중복 발행은 허용하지만 소비 효과는 한 번.

## 실행 방법

```bash
./scripts/lab test 09-01
./scripts/lab verify 09
```

인프라만 점검: `./gradlew :banking-lab-tests:infrastructureTest -PlabInfrastructure=09`.

누적 검증은 01부터 09까지 실행한다. 최초 기능 테스트는 현재 501 TODO: A09로 실패해야 하며 ENVIRONMENT_ERROR와 구분한다.

## 완료 조건

해당 Assignment의 모든 Step과 누적 회귀 테스트 통과; 환경 오류 없음; 정합성 불변식 유지. API, 제공 테스트 및 초기 자금 계약을 바꾸어 통과시키지 않는다.

## 다음 Assignment

Assignment 10은 실제 프로세스·연결 장애 후 복구를 검증한다.
