# Assignment 12: Financial Workload

## 학습 목표

합성 또는 명시적으로 공급된 데이터로 재현 가능한 금융 워크로드를 측정한다.

## 사전 요구사항

Assignment 05 이상 금융 핵심; PostgreSQL; 외부 데이터는 선택적.

## 제공 API 및 인터페이스

`DatasetAdapter`, `TransactionTypeMapper`, `WorkloadGenerator`, `WorkloadService`; POST `/api/v1/workloads`.

공통 요청·응답·오류 계약은 [api-contracts](../api-contracts.md), 데이터 소유권은 [architecture](../architecture.md)를 따른다.

요청 예: `{"seed":42,"accounts":8,"requests":20,"concurrency":2,"initialBalance":1000,"dataset":"synthetic"}`. 합성 실행은 요청한 계좌의 opening baseline을 만들고 provenance=SYNTHETIC을 기록한다. 같은 seed는 같은 논리 계좌 매핑을 제공한다. 외부 데이터가 없으면 DATASET_UNAVAILABLE 400이다. DatasetAdapter Fixture의 TRANSFER 두 건을 매핑하고 UNSUPPORTED 한 건은 EXCLUDED로 분류한다. 실제 데이터셋은 포함하지 않는다. 생성기는 일회성 Stream을 새 adapter 인스턴스로 다시 읽을 수 있어야 한다. 측정 규칙은 [workload-guide](../workload-guide.md)를 참조한다.

## 구현 TODO

초기 자금·식별자 매핑 포트는 InitialBalancePolicy, AccountIdentifierMapper이며 측정 구조는 WorkloadMetricsCollector와 MeasurementEnvironment이다. 생성기·유형 매퍼·측정 서비스의 스켈레톤도 TODO로 제공한다.

데이터 유형/계좌 매핑·초기 자금 정책·seed 기반 생성·부하 실행·측정값 수집을 구현한다. 제공 서비스는 UnsupportedOperationException으로 남겨 두었다. 테스트용 자금 공급·장애 주입은 운영 API가 아니다.

## 테스트 목록

| Step | 시나리오 |
| --- | --- |
| 12-01 | 외부 데이터 없는 합성 워크로드 |
| 12-02 | 동일 seed의 재현 가능한 매핑 |
| 12-03 | 측정 지표 계약 |
| 12-04 | 부하 후 금융 정합성 |
| 12-05 | 외부 데이터 미제공 시 명시적 오류 |
| 12-06 | 합성 데이터 어댑터와 생성기 계약 |

테스트는 각 Step을 독립 실행한다. Fixture는 선행 과제의 결과 파일을 재사용하지 않고 필요한 계좌와 자금을 매번 생성한다. 선행 기능의 구현은 사전 요구사항이다.

## 금융 불변식

관측값과 합성/추정값의 출처 구분; 모든 계좌의 초기 잔액 명시; 부하 후 총액과 원장 정합성 유지. 성능 임계값은 측정 환경과 별도 합의한다.

## 실행 방법

```bash
./scripts/lab test 12-01
./scripts/lab verify 12
```

누적 검증은 01부터 12까지 실행한다. 최초 기능 테스트는 현재 501 TODO: A12로 실패해야 하며 ENVIRONMENT_ERROR와 구분한다.

## 완료 조건

해당 Assignment의 모든 Step과 누적 회귀 테스트 통과; 환경 오류 없음; 정합성 불변식 유지. API, 제공 테스트 및 초기 자금 계약을 바꾸어 통과시키지 않는다.

## 다음 Assignment

모놀리스와 분리 서비스에 같은 불변식 및 생성기 계약을 재사용한다.
