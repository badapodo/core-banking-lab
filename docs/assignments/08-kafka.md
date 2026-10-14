# Assignment 08: Kafka Messaging

## 학습 목표

중복 가능한 금융 이벤트를 독립 소비자 그룹으로 처리한다.

## 사전 요구사항

Assignment 05의 금융 핵심; PostgreSQL + Kafka + 파생 서비스.

## 제공 API 및 인터페이스

`TransferCommitted`, `ProjectionService`; GET `/api/v1/consumers/{consumer}/events/{eventId}`; audit/notifications 그룹.

공통 요청·응답·오류 계약은 [api-contracts](../api-contracts.md), 데이터 소유권은 [architecture](../architecture.md)를 따른다.

이벤트 schemaVersion은 1이며 eventId, transferId, sourceAccountId, targetAccountId, amount, sequence, occurredAt을 포함한다. topic은 실행마다 고유하고 테스트 producer는 같은 거래 ID를 Kafka key로 사용한다. sequence는 업무의 순서를 표현하며 도착 순서와 다를 수 있다. deliveryCount는 관측된 전달 횟수이고 effectCount와 구분한다. 중복 테스트는 deliveryCount ≥ 2를 기다린 뒤 효과가 1임을 확인한다. Listener Bean은 시작되지만 consume()은 TODO이므로 기능 테스트가 실패한다.

## 구현 TODO

08-02~08-08은 소비자 전용 Fixture topic을 사용한다. 실제 Core 발행 topic과 분리해 초기 이벤트 도착이나 자동 발행이 장애/역순/중복 시나리오에 끼어들지 않게 한다. Fixture 이벤트는 테스트가 생성한 확정 거래를 참조하며 금융 잔액을 추가 변경하지 않는다. 08-01은 실제 Core 발행 topic을 관찰한다.

확정 이벤트 발행·Kafka listener의 업무 처리·중복 효과 방지·재전달을 구현한다. 제공 서비스는 UnsupportedOperationException으로 남겨 두었다. 테스트용 자금 공급·장애 주입은 운영 API가 아니다.

## 테스트 목록

| Step | 시나리오 |
| --- | --- |
| 08-01 | 확정 이벤트 발행 |
| 08-02 | 정상 소비 |
| 08-03 | 중복 소비 |
| 08-04 | 소비 도중 DB 장애 |
| 08-05 | 미소비 이벤트 재전달 |
| 08-06 | 역순 이벤트 |
| 08-07 | 소비자 재시작 후 중복 효과 방지 |
| 08-08 | 독립 소비자 그룹 |

테스트는 각 Step을 독립 실행한다. Fixture는 선행 과제의 결과 파일을 재사용하지 않고 필요한 계좌와 자금을 매번 생성한다. 선행 기능의 구현은 사전 요구사항이다.

## 금융 불변식

두 소비자는 금융 잔액을 변경하지 않는다. 각 (consumer,eventId)의 effectCount는 1. 역순 도착에도 원 이벤트의 sequence를 보존한다.

## 실행 방법

```bash
./scripts/lab test 08-01
./scripts/lab verify 08
```

인프라만 점검: `./gradlew :banking-lab-tests:infrastructureTest -PlabInfrastructure=08`.

누적 검증은 01부터 08까지 실행한다. 최초 기능 테스트는 현재 501 TODO: A08로 실패해야 하며 ENVIRONMENT_ERROR와 구분한다.

## 완료 조건

해당 Assignment의 모든 Step과 누적 회귀 테스트 통과; 환경 오류 없음; 정합성 불변식 유지. API, 제공 테스트 및 초기 자금 계약을 바꾸어 통과시키지 않는다.

## 다음 Assignment

Assignment 09는 DB 커밋 후에도 이벤트를 유실하지 않는 Outbox로 강화한다.
