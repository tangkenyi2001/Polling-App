# Database Schema

## Users
| column      | type      | notes                  |
|-------------|-----------|------------------------|
| id          | PK        |                        |
| email       | text      | UNIQUE                 |
| password    | text      | hashed                 |
| createdAt   | timestamp |                        |
| lastLoginAt | timestamp |                        |

## Polls
| column     | type      | notes                              |
|------------|-----------|-------------------------------------|
| id         | PK        |                                     |
| ownerId    | FK        | → Users.id                          |
| question   | text      |                                     |
| type       | enum      | 'MCQ' \| 'RATING' \| 'WORDCLOUD'    |
| createdAt  | timestamp |                                     |
| expiryDate | timestamp |                                     |

## Poll subtypes (1:1 with Polls — shared PK/FK pattern)

Each of these uses `pollId` as **both** primary key and foreign key, so the DB
guarantees at most one config row per poll.

### MCQPoll
| column               | type    | notes                     |
|----------------------|---------|---------------------------|
| pollId               | PK, FK  | → Polls.id                |
| allowMultipleAnswers | boolean |                           |

### RatingPoll
| column    | type   | notes       |
|-----------|--------|-------------|
| pollId    | PK, FK | → Polls.id  |
| minRating | int    |             |
| maxRating | int    |             |

### WordCloudPoll
| column   | type   | notes       |
|----------|--------|-------------|
| pollId   | PK, FK | → Polls.id  |
| maxWords | int    |             |

## MCQOption (1:many with Polls)

MCQ is the one poll type with a variable-length child collection (the
choices), so it gets its own table instead of living inside `MCQPoll`.

| column | type | notes      |
|--------|------|------------|
| id     | PK   |            |
| pollId | FK   | → Polls.id |
| value  | text |            |

## Responses
| column      | type      | notes      |
|-------------|-----------|------------|
| id          | PK        |            |
| pollId      | FK        | → Polls.id |
| userId      | FK, null  | → Users.id — nullable to allow anonymous respondents |
| responseTime| timestamp |            |

## Response subtypes (1:1 with Responses)

Same shared-PK/FK pattern as the poll subtypes.

### MCQResponse
| column      | type   | notes            |
|-------------|--------|------------------|
| responseId  | PK, FK | → Responses.id   |
| mcqOptionId | FK     | → MCQOption.id   |

### RatingResponse
| column     | type   | notes                                             |
|------------|--------|----------------------------------------------------|
| responseId | PK, FK | → Responses.id                                    |
| rating     | int    | should fall within the poll's minRating/maxRating (app-level check) |

### WordCloudResponse
| column     | type   | notes          |
|------------|--------|----------------|
| responseId | PK, FK | → Responses.id |
| text       | text   |                |

## Notes / invariants not enforced by the schema itself

- Exactly one of `MCQPoll` / `RatingPoll` / `WordCloudPoll` should exist per
  `Polls.id`, matching `Polls.type`. Create the poll and its subtype row in
  one transaction.
- Exactly one of `MCQResponse` / `RatingResponse` / `WordCloudResponse` should
  exist per `Responses.id`, matching the parent poll's type.
- `RatingResponse.rating` should be validated against the parent
  `RatingPoll.minRating`/`maxRating` at the application layer.
