# Assignment 11: Reconciliation

## 학습 목표

금융 원장과 이벤트·소비 기록의 불일치를 탐지하고 파생 데이터를 복구한다.

## 사전 요구사항

Assignment 09~10; Core와 파생 서비스; 실제 DB와 Kafka.

## 제공 API 및 인터페이스

POST `/api/v1/reconciliation/core`; GET `/api/v1/reconciliation/snapshot`; POST `/api/v1/reconciliation/distributed?repair=false|true`; `CoreEvidencePort`.

공통 요청·응답·오류 계약은 [api-contracts](../api-contracts.md), 데이터 소유권은 [architecture](../architecture.md)를 따른다.

파생 서비스는 Core DB에 접속하지 않고 읽기 전용 HTTP snapshot과 replay 요청 포트를 사용한다. report는 status(CONSISTENT/INCONSISTENT), discrepancies(문자열 목록), repaired를 반환한다. 누락 항목에는 MISSING_OUTBOX/MISSING_CONSUMPTION와 관련 ID, 중복 효과에는 DUPLICATE_EFFECT와 eventId를 포함한다. 잘못된 금융 핵심 값은 탐지·보고하고 원본 금융 기록을 자동 덮어쓰지 않는다.

## 구현 TODO

Core HTTP 증거 조회·누락/중복 탐지·재발행 요청·파생 데이터 복구를 구현한다. 제공 서비스는 UnsupportedOperationException으로 남겨 두었다. 테스트용 자금 공급·장애 주입은 운영 API가 아니다.

## 테스트 목록

| Step | 시나리오 |
| --- | --- |
| 11-01 | 금융 잔액·원장 대사 |
| 11-02 | 확정 거래·Outbox 누락 탐지 |
| 11-03 | 이벤트·소비 기록 대사 |
| 11-04 | 감사 데이터 누락 탐지 |
| 11-05 | 중복 파생 효과 탐지 |
| 11-06 | 누락 파생 데이터 재처리 |
| 11-07 | 대사·복구 재실행 멱등성 |

테스트는 각 Step을 독립 실행한다. Fixture는 선행 과제의 결과 파일을 재사용하지 않고 필요한 계좌와 자금을 매번 생성한다. 선행 기능의 구현은 사전 요구사항이다.

## 금융 불변식

확정 원장이 진실 원천. 대사는 금융 잔액·원장을 임의 수정하지 않는다. 반복 복구는 멱등이며 원본 eventId를 유지한다.

## 실행 방법

```bash
./scripts/lab test 11-01
./scripts/lab verify 11
```

인프라만 점검: `./gradlew :banking-lab-tests:infrastructureTest -PlabInfrastructure=11`.

누적 검증은 01부터 11까지 실행한다. 최초 기능 테스트는 현재 501 TODO: A11로 실패해야 하며 ENVIRONMENT_ERROR와 구분한다.

## 완료 조건

해당 Assignment의 모든 Step과 누적 회귀 테스트 통과; 환경 오류 없음; 정합성 불변식 유지. API, 제공 테스트 및 초기 자금 계약을 바꾸어 통과시키지 않는다.

## 다음 Assignment

Assignment 12는 같은 불변식을 재현 가능한 부하 아래 관측한다.
