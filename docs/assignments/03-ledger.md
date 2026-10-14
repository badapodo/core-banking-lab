# Assignment 03: Ledger

## 학습 목표

은행의 고객 예금 부채 계정 관점에서 복식 원장을 기록하고 잔액을 대사한다.

## 사전 요구사항

Assignment 02; funding_baselines Fixture.

## 제공 API 및 인터페이스

`LedgerService`, `LedgerEntry`; GET `/api/v1/accounts/{id}/ledger`, POST `/api/v1/reconciliation/core`.

공통 요청·응답·오류 계약은 [api-contracts](../api-contracts.md), 데이터 소유권은 [architecture](../architecture.md)를 따른다.

초기 자금은 운영 거래가 아니라 테스트 전용 opening baseline이다. Fixture가 accounts와 funding_baselines에 같은 값을 원자적으로 넣는다. 대사식은 `balance = opening_balance + CREDIT 합계 - DEBIT 합계`이다. 이후 이체만 ledger_entries에 넣어 초기 금액을 중복 가산하지 않는다. 확정 이체는 원장 쌍과 잔액 변경이 모두 성공한 상태이다.

## 구현 TODO

원장 쌍의 원자적 기록·조회·대사를 구현한다. 원장 수정/삭제 API는 제공하지 않는다. 제공 서비스는 UnsupportedOperationException으로 남겨 두었다. 테스트용 자금 공급·장애 주입은 운영 API가 아니다.

## 테스트 목록

| Step | 시나리오 |
| --- | --- |
| 03-01 | 정상 이체의 원장 기록 |
| 03-02 | 거래별 차변·대변 일치 |
| 03-03 | 원장 쓰기 실패 시 전체 롤백 |
| 03-04 | 확정 거래별 정확히 한 원장 쌍 |
| 03-05 | 거래 내역 조회 |
| 03-06 | 원장 불변성 |
| 03-07 | 초기 자금과 원장 대사 |

테스트는 각 Step을 독립 실행한다. Fixture는 선행 과제의 결과 파일을 재사용하지 않고 필요한 계좌와 자금을 매번 생성한다. 선행 기능의 구현은 사전 요구사항이다.

## 금융 불변식

고객 예금 부채는 차변(DEBIT)이 감소, 대변(CREDIT)이 증가한다. 내부 이체는 출금 계좌 DEBIT와 입금 계좌 CREDIT이며 거래별 합계가 같다.

## 실행 방법

```bash
./scripts/lab test 03-01
./scripts/lab verify 03
```

누적 검증은 01부터 03까지 실행한다. 최초 기능 테스트는 현재 501 TODO: A03로 실패해야 하며 ENVIRONMENT_ERROR와 구분한다.

## 완료 조건

해당 Assignment의 모든 Step과 누적 회귀 테스트 통과; 환경 오류 없음; 정합성 불변식 유지. API, 제공 테스트 및 초기 자금 계약을 바꾸어 통과시키지 않는다.

## 다음 Assignment

Assignment 04는 같은 금융 불변식을 동시 요청으로 검증한다.
