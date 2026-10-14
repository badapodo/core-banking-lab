# Assignment 06: Service Boundary

## 학습 목표

Core와 Orchestrator를 독립 실행하고 HTTP 경계를 유지한다.

## 사전 요구사항

Assignment 05; PostgreSQL과 별도 Java 프로세스 2개.

## 제공 API 및 인터페이스

Orchestrator의 POST/GET `/api/v1/transfers`; `CoreBankingPort`, `HttpCoreBankingAdapter`.

공통 요청·응답·오류 계약은 [api-contracts](../api-contracts.md), 데이터 소유권은 [architecture](../architecture.md)를 따른다.

## 구현 TODO

HTTP 호출·타임아웃·불확실 응답 처리·오류 변환을 구현한다. 제공 서비스는 UnsupportedOperationException으로 남겨 두었다. 테스트용 자금 공급·장애 주입은 운영 API가 아니다.

## 테스트 목록

| Step | 시나리오 |
| --- | --- |
| 06-01 | 독립 서비스의 기존 이체 계약 |
| 06-02 | Core HTTP 호출 확인 |
| 06-03 | Core 서비스 오류 |
| 06-04 | 연결 실패 |
| 06-05 | 호출 타임아웃 |
| 06-06 | Core 커밋 후 응답 유실 |
| 06-07 | 서비스 경계의 중복 요청 |
| 06-08 | DB 접근 권한 및 모듈 경계 |

테스트는 각 Step을 독립 실행한다. Fixture는 선행 과제의 결과 파일을 재사용하지 않고 필요한 계좌와 자금을 매번 생성한다. 선행 기능의 구현은 사전 요구사항이다.

## 금융 불변식

Core만 accounts/transfers/ledger/idempotency/outbox를 변경한다. Orchestrator는 자체 workflow DB만 접근하며 Core DB 접속이 권한으로 차단된다.

## 실행 방법

```bash
./scripts/lab test 06-01
./scripts/lab verify 06
```

인프라만 점검: `./gradlew :banking-lab-tests:infrastructureTest -PlabInfrastructure=06`.

누적 검증은 01부터 06까지 실행한다. 최초 기능 테스트는 현재 501 TODO: A06로 실패해야 하며 ENVIRONMENT_ERROR와 구분한다.

## 완료 조건

해당 Assignment의 모든 Step과 누적 회귀 테스트 통과; 환경 오류 없음; 정합성 불변식 유지. API, 제공 테스트 및 초기 자금 계약을 바꾸어 통과시키지 않는다.

## 다음 Assignment

Assignment 07은 업무 절차 상태를 Orchestrator DB에 기록한다.
