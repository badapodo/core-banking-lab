# Assignment 07: Saga

## 학습 목표

수취인 검증·원자적 금융 확정·후속 업무를 상태 머신으로 관리한다.

## 사전 요구사항

Assignment 06; 실제 두 서비스; WireMock 검증/후속 서비스.

## 제공 API 및 인터페이스

POST `/api/v1/sagas` 202, GET `/api/v1/sagas/{id}` 200, POST `/api/v1/recovery/run` 200; `WorkflowService`, validation/followup 포트.

공통 요청·응답·오류 계약은 [api-contracts](../api-contracts.md), 데이터 소유권은 [architecture](../architecture.md)를 따른다.

정상 stateHistory에는 RECEIVED → VALIDATING → CORE_PENDING → COMMITTED → COMPLETED가 순서대로 포함된다. REJECTED는 금융 확정 전 검증 거절, RETRY_PENDING은 미완료 재시도, FOLLOWUP_PENDING은 자금 확정 후 후속 업무 미완료, MANUAL_REVIEW는 재시도 한도 초과이다. 상태 이력과 현재 상태를 자체 DB에 영속화한다.

## 구현 TODO

상태 이력·영속화·결과 확인·중복 억제·복구·후속 업무 재시도를 구현한다. 제공 서비스는 UnsupportedOperationException으로 남겨 두었다. 테스트용 자금 공급·장애 주입은 운영 API가 아니다.

## 테스트 목록

| Step | 시나리오 |
| --- | --- |
| 07-01 | 정상 상태 전이와 금융 효과 |
| 07-02 | 수취인 검증 실패 |
| 07-03 | 검증 타임아웃 |
| 07-04 | Core 호출 실패 |
| 07-05 | 커밋 응답 유실 후 결과 확인 |
| 07-06 | 동시 중복 Saga |
| 07-07 | 완료 상태 영속성 |
| 07-08 | 재시작 후 미완료 Saga 복구 |
| 07-09 | 후속 업무 실패가 확정 거래를 취소하지 않음 |

테스트는 각 Step을 독립 실행한다. Fixture는 선행 과제의 결과 파일을 재사용하지 않고 필요한 계좌와 자금을 매번 생성한다. 선행 기능의 구현은 사전 요구사항이다.

## 금융 불변식

확정 전 검증 실패는 자금 변화 없음. Core 커밋 후 응답 유실은 키 조회로 확인. 후속 업무 실패는 FOLLOWUP_PENDING이며 확정 거래를 취소하지 않는다.

## 실행 방법

```bash
./scripts/lab test 07-01
./scripts/lab verify 07
```

인프라만 점검: `./gradlew :banking-lab-tests:infrastructureTest -PlabInfrastructure=07`.

누적 검증은 01부터 07까지 실행한다. 최초 기능 테스트는 현재 501 TODO: A07로 실패해야 하며 ENVIRONMENT_ERROR와 구분한다.

## 완료 조건

해당 Assignment의 모든 Step과 누적 회귀 테스트 통과; 환경 오류 없음; 정합성 불변식 유지. API, 제공 테스트 및 초기 자금 계약을 바꾸어 통과시키지 않는다.

## 다음 Assignment

Assignment 08은 후속 업무를 금융 이벤트 소비로 연결한다.
