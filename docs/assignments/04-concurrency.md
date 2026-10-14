# Assignment 04: Concurrency

## 학습 목표

동시 요청에서 자금 보존과 잔액·원장 정합성을 유지한다.

## 사전 요구사항

Assignment 03; 실제 PostgreSQL.

## 제공 API 및 인터페이스

Assignment 02 API 유지; `RETRYABLE_CONFLICT` 409 계약 추가.

공통 요청·응답·오류 계약은 [api-contracts](../api-contracts.md), 데이터 소유권은 [architecture](../architecture.md)를 따른다.

요청 시작은 CountDownLatch로 동기화하며 고정 sleep을 사용하지 않는다. 04-08은 테스트 전용 trigger가 PostgreSQL SQLSTATE 40P01을 한 번 발생시킨다. 서비스는 내부 재시도로 201을 반환하거나 전체 롤백 후 409 RETRYABLE_CONFLICT를 반환할 수 있다. 교차 방향 경합 테스트는 실제 DB 잠금 경합을 추가로 검증한다.

## 구현 TODO

동시성 전략과 오류 후 재시도를 구현한다. 잠금 종류와 Repository 호출 방식은 선택 사항이다. 제공 서비스는 UnsupportedOperationException으로 남겨 두었다. 테스트용 자금 공급·장애 주입은 운영 API가 아니다.

## 테스트 목록

| Step | 시나리오 |
| --- | --- |
| 04-01 | 동일 계좌 동시 출금 |
| 04-02 | 총 요청액이 잔액 초과 |
| 04-03 | 반대 방향 동시 이체 |
| 04-04 | 다수 계좌 순환 이체 |
| 04-05 | Lost Update 방지 |
| 04-06 | 경합 후 진행 가능성 |
| 04-07 | 병렬 처리 후 자금·원장 정합성 |
| 04-08 | PostgreSQL deadlock 오류 후 재시도 |

테스트는 각 Step을 독립 실행한다. Fixture는 선행 과제의 결과 파일을 재사용하지 않고 필요한 계좌와 자금을 매번 생성한다. 선행 기능의 구현은 사전 요구사항이다.

## 금융 불변식

초과 출금 금지; Lost Update 금지; 경합 후 진행 가능; 성공 거래별 원장 쌍 유지.

## 실행 방법

```bash
./scripts/lab test 04-01
./scripts/lab verify 04
```

누적 검증은 01부터 04까지 실행한다. 최초 기능 테스트는 현재 501 TODO: A04로 실패해야 하며 ENVIRONMENT_ERROR와 구분한다.

## 완료 조건

해당 Assignment의 모든 Step과 누적 회귀 테스트 통과; 환경 오류 없음; 정합성 불변식 유지. API, 제공 테스트 및 초기 자금 계약을 바꾸어 통과시키지 않는다.

## 다음 Assignment

Assignment 05는 동시에 도착하는 동일 요청까지 다룬다.
