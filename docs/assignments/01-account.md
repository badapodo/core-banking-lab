# Assignment 01: Account

## API Contract

| ID | API | Expected |
| --- | --- | --- |
| ACC-01 | `POST /api/v1/accounts` | Create account, `201` |
| ACC-02 | `GET /api/v1/accounts/{id}` | Load account, `200` |
| ACC-03 | `GET /api/v1/accounts/{id}` | Unknown account, `404` |
| ACC-04 | `GET /api/v1/accounts/{id}` | Invalid UUID, `400` |

Create requests use an empty JSON object:

```json
{}
```

Initial balance must be `0`, and initial status must be `ACTIVE`.

## Steps

| Step | Test | Goal |
| --- | --- | --- |
| 01-01 | Create account | `201`, UUID returned |
| 01-02 | Initial state | balance `0`, status `ACTIVE` |
| 01-03 | Unique IDs | no duplicate UUIDs |
| 01-04 | Persistence | created account can be loaded |
| 01-05 | Unknown account | `404` |
