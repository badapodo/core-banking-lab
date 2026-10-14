# Assignment 02: Transfer

## 학습 목표

단일 Core 트랜잭션에서 계좌 간 이체를 원자적으로 확정한다.

## 사전 요구사항

Assignment 01; PostgreSQL; 테스트 전용 초기 자금 Fixture.

## 제공 API 및 인터페이스

`TransferService`, `TransferCommand`, `TransferResult`; POST `/api/v1/transfers`, GET `/api/v1/transfers/{id}`.

공통 요청·응답·오류 계약은 [api-contracts](../api-contracts.md), 데이터 소유권은 [architecture](../architecture.md)를 따른다.

## 구현 TODO

검증·잔액 이동·거래 저장·오류 매핑을 구현한다. 원장 과제 이후에도 잔액과 원장은 같은 트랜잭션이다. 제공 서비스는 UnsupportedOperationException으로 남겨 두었다. 테스트용 자금 공급·장애 주입은 운영 API가 아니다.

## 테스트 목록

| Step | 시나리오 |
| --- | --- |
| 02-01 | 정상 이체 및 조회 |
| 02-02 | 잔액 부족 |
| 02-03 | 존재하지 않는 계좌 |
| 02-04 | 동일 계좌 |
| 02-05 | 0원 및 음수 |
| 02-06 | 잔액 기록 실패 시 전체 롤백 |
| 02-07 | 다수 이체 전후 자금 보존 |

테스트는 각 Step을 독립 실행한다. Fixture는 선행 과제의 결과 파일을 재사용하지 않고 필요한 계좌와 자금을 매번 생성한다. 선행 기능의 구현은 사전 요구사항이다.

## 금융 불변식

잔액 음수 금지; 성공은 출금·입금 모두 반영; 실패 시 변경 없음; 총액 보존.

## 실행 방법

```bash
./scripts/lab test 02-01
./scripts/lab verify 02
```

누적 검증은 01부터 02까지 실행한다. 최초 기능 테스트는 현재 501 TODO: A02로 실패해야 하며 ENVIRONMENT_ERROR와 구분한다.

## 완료 조건

해당 Assignment의 모든 Step과 누적 회귀 테스트 통과; 환경 오류 없음; 정합성 불변식 유지. API, 제공 테스트 및 초기 자금 계약을 바꾸어 통과시키지 않는다.

## 다음 Assignment

Assignment 03은 동일한 확정 트랜잭션에 원장을 추가한다.
