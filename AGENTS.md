# Core Banking Lab Agent Notes

이 저장소는 학습용 스켈레톤입니다.

- `banking-lab-tests`의 테스트는 제공 계약이므로 수정하지 않습니다.
- Assignment 구현은 `banking-domain`, `banking-application`, `banking-infrastructure`에서 진행합니다.
- `banking-domain`은 Spring Data JPA와 PostgreSQL에 의존하지 않습니다.
- 환경 테스트는 통과해야 하고, 초기 Assignment 테스트 실패는 명확한 `TODO: A01`에서 시작해야 합니다.

## Assignment 02~12 확장 규칙

- 제공 테스트와 API는 학습 계약입니다. 실습 환경 확장 시에만 새 테스트·Fixture를 추가합니다.
- 서비스 스켈레톤의 정답 로직은 학습자가 구현합니다. 환경 구축 작업에서는 TODO를 유지합니다.
- 잔액과 원장은 Core의 같은 PostgreSQL 트랜잭션에서 변경합니다.
- Orchestrator와 파생 서비스는 Core DB를 직접 읽거나 쓰지 않습니다.
- Step 추가 시 lab-steps.csv와 해당 Assignment 문서를 함께 갱신합니다.
- 미래 과제는 기본 test에 포함하지 않습니다. 기능 실패와 환경 오류를 구분합니다.
