# Assignment 05: Idempotency

## 학습 목표

중복 금융 요청의 효과를 한 번만 적용한다.

## 사전 요구사항

Assignment 04; PostgreSQL 내 멱등성 기록.

## 제공 API 및 인터페이스

POST `/api/v1/transfers`의 `Idempotency-Key`; GET `/api/v1/transfers/by-key/{key}`.

공통 요청·응답·오류 계약은 [api-contracts](../api-contracts.md), 데이터 소유권은 [architecture](../architecture.md)를 따른다.

## 구현 TODO

키·요청 지문·응답의 영속화와 금융 트랜잭션 원자성을 구현한다. 제공 서비스는 UnsupportedOperationException으로 남겨 두었다. 테스트용 자금 공급·장애 주입은 운영 API가 아니다.

## 테스트 목록

| Step | 시나리오 |
| --- | --- |
| 05-01 | 동일 키·동일 요청 |
| 05-02 | 동일 키·다른 요청 충돌 |
| 05-03 | 동시 중복 요청 |
| 05-04 | 클라이언트가 응답을 잃은 후 재시도 |
| 05-05 | 실제 프로세스 재시작 후 재시도 |
| 05-06 | 멱등성 기록 실패 시 금융 롤백 |

테스트는 각 Step을 독립 실행한다. Fixture는 선행 과제의 결과 파일을 재사용하지 않고 필요한 계좌와 자금을 매번 생성한다. 선행 기능의 구현은 사전 요구사항이다.

## 금융 불변식

같은 키·같은 요청은 최초와 동일한 201 응답 본문 및 transferId. 다른 요청은 409 IDEMPOTENCY_CONFLICT. 금융 효과와 원장은 한 번.

## 실행 방법

```bash
./scripts/lab test 05-01
./scripts/lab verify 05
```

누적 검증은 01부터 05까지 실행한다. 최초 기능 테스트는 현재 501 TODO: A05로 실패해야 하며 ENVIRONMENT_ERROR와 구분한다.

## 완료 조건

해당 Assignment의 모든 Step과 누적 회귀 테스트 통과; 환경 오류 없음; 정합성 불변식 유지. API, 제공 테스트 및 초기 자금 계약을 바꾸어 통과시키지 않는다.

## 다음 Assignment

Assignment 06의 HTTP 재시도에도 같은 키를 전달한다.
