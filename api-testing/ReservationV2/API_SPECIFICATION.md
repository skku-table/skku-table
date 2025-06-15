# API 명세서 - Timeslot 기능

## 개요

타임슬롯 기반 예약 시스템을 위한 REST API 명세서입니다. 부스별로 시간대를 나누어 예약을 관리할 수 있습니다.

---

## 인증 및 권한

### 역할별 권한

- **USER**: 예약 생성/취소, 본인 예약 조회
- **HOST**: 타임슬롯 관리, 예약 관리 (본인이 관리하는 부스)
- **ADMIN**: 모든 기능 접근 가능

### 인증 방식

- 세션 기반 인증 (JSESSIONID 쿠키)
- 로그인 후 서버에서 발급된 JSESSIONID 쿠키를 통해 인증
- `Cookie: JSESSIONID={sessionId}`

### 권한별 API 접근 매트릭스

| API                  | USER | HOST | ADMIN | 비인증 |
| -------------------- | ---- | ---- | ----- | ------ |
| 타임슬롯 생성        | ❌   | ✅   | ✅    | ❌     |
| 타임슬롯 수정        | ❌   | ✅   | ✅    | ❌     |
| 타임슬롯 삭제        | ❌   | ✅   | ✅    | ❌     |
| 타임슬롯 조회        | ✅   | ✅   | ✅    | ✅     |
| 예약 생성            | ✅   | ✅   | ✅    | ❌     |
| 예약 취소            | ✅   | ✅   | ✅    | ❌     |
| 내 예약 조회         | ✅   | ✅   | ✅    | ❌     |
| 타임슬롯별 예약 현황 | ❌   | ✅   | ✅    | ❌     |

### 인증 실패 처리

**401 Unauthorized 응답**

```json
{
  "timestamp": "2024-01-01T10:00:00.000+00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "로그인이 필요합니다",
  "path": "/booths/1/timeslots"
}
```

**403 Forbidden 응답**

```json
{
  "timestamp": "2024-01-01T10:00:00.000+00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "접근 권한이 없습니다",
  "path": "/booths/1/timeslots"
}
```

---

## 1. TimeSlot API

### Base URL

```
/booths/{boothId}/timeslots
```

### 1.1 타임슬롯 생성

**POST** `/booths/{boothId}/timeslots`

타임슬롯을 생성합니다.

#### 권한

- HOST, ADMIN

#### Path Parameters

| 파라미터 | 타입 | 필수 | 설명    |
| -------- | ---- | ---- | ------- |
| boothId  | Long | O    | 부스 ID |

#### Request Body

```json
{
  "startTime": "2024-01-01T10:00:00",
  "endTime": "2024-01-01T11:00:00",
  "maxCapacity": 10
}
```

| 필드        | 타입          | 필수 | 설명           | 제약사항                      |
| ----------- | ------------- | ---- | -------------- | ----------------------------- |
| startTime   | LocalDateTime | O    | 시작 시간      | ISO 8601 형식                 |
| endTime     | LocalDateTime | O    | 종료 시간      | ISO 8601 형식, startTime 이후 |
| maxCapacity | Integer       | O    | 최대 수용 인원 | 1 이상                        |

#### Response

**201 Created**

```json
{
  "id": 1,
  "boothId": 1,
  "boothName": "카페 부스",
  "startTime": "2024-01-01T10:00:00",
  "endTime": "2024-01-01T11:00:00",
  "maxCapacity": 10,
  "currentCapacity": 0,
  "availableCapacity": 10,
  "status": "AVAILABLE",
  "createdAt": "2024-01-01T09:00:00",
  "updatedAt": "2024-01-01T09:00:00"
}
```

#### Error Responses

**400 Bad Request** - 잘못된 요청 데이터

```json
{
  "timestamp": "2024-01-01T10:00:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "startTime은 endTime보다 이전이어야 합니다",
  "path": "/booths/1/timeslots",
  "validationErrors": {
    "startTime": "시작 시간은 필수입니다",
    "maxCapacity": "최대 수용 인원은 1명 이상이어야 합니다"
  }
}
```

**403 Forbidden** - 권한 없음

```json
{
  "timestamp": "2024-01-01T10:00:00.000+00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "해당 부스의 타임슬롯을 관리할 권한이 없습니다",
  "path": "/booths/1/timeslots"
}
```

**404 Not Found** - 부스를 찾을 수 없음

```json
{
  "timestamp": "2024-01-01T10:00:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Booth not found: 999",
  "path": "/booths/999/timeslots"
}
```

**409 Conflict** - 동일한 시간대에 이미 타임슬롯 존재

```json
{
  "timestamp": "2024-01-01T10:00:00.000+00:00",
  "status": 409,
  "error": "Conflict",
  "message": "해당 시간대에 이미 타임슬롯이 존재합니다",
  "path": "/booths/1/timeslots"
}
```

### 1.2 타임슬롯 수정

**PATCH** `/booths/{boothId}/timeslots/{timeSlotId}`

기존 타임슬롯을 수정합니다.

#### 권한

- HOST, ADMIN

#### Path Parameters

| 파라미터   | 타입 | 필수 | 설명        |
| ---------- | ---- | ---- | ----------- |
| boothId    | Long | O    | 부스 ID     |
| timeSlotId | Long | O    | 타임슬롯 ID |

#### Request Body

```json
{
  "startTime": "2024-01-01T10:30:00",
  "endTime": "2024-01-01T11:30:00",
  "maxCapacity": 15,
  "status": "AVAILABLE"
}
```

| 필드        | 타입          | 필수 | 설명           | 제약사항                |
| ----------- | ------------- | ---- | -------------- | ----------------------- |
| startTime   | LocalDateTime | X    | 시작 시간      | ISO 8601 형식           |
| endTime     | LocalDateTime | X    | 종료 시간      | ISO 8601 형식           |
| maxCapacity | Integer       | X    | 최대 수용 인원 | 1 이상                  |
| status      | String        | X    | 타임슬롯 상태  | AVAILABLE, FULL, CLOSED |

#### Response

**200 OK**

```json
{
  "id": 1,
  "boothId": 1,
  "boothName": "카페 부스",
  "startTime": "2024-01-01T10:30:00",
  "endTime": "2024-01-01T11:30:00",
  "maxCapacity": 15,
  "currentCapacity": 5,
  "availableCapacity": 10,
  "status": "AVAILABLE",
  "createdAt": "2024-01-01T09:00:00",
  "updatedAt": "2024-01-01T10:00:00"
}
```

### 1.3 타임슬롯 삭제

**DELETE** `/booths/{boothId}/timeslots/{timeSlotId}`

타임슬롯을 삭제합니다.

#### 권한

- HOST, ADMIN

#### Path Parameters

| 파라미터   | 타입 | 필수 | 설명        |
| ---------- | ---- | ---- | ----------- |
| boothId    | Long | O    | 부스 ID     |
| timeSlotId | Long | O    | 타임슬롯 ID |

#### Response

**204 No Content**

#### Error Responses

- **403 Forbidden**: 권한 없음
- **404 Not Found**: 타임슬롯을 찾을 수 없음
- **409 Conflict**: 예약이 있는 타임슬롯은 삭제 불가

### 1.4 부스 타임슬롯 전체 조회

**GET** `/booths/{boothId}/timeslots`

특정 부스의 모든 타임슬롯을 조회합니다.

#### 권한

- 모든 사용자 (인증 불필요)

#### Path Parameters

| 파라미터 | 타입 | 필수 | 설명    |
| -------- | ---- | ---- | ------- |
| boothId  | Long | O    | 부스 ID |

#### Response

**200 OK**

```json
[
  {
    "id": 1,
    "boothId": 1,
    "boothName": "카페 부스",
    "startTime": "2024-01-01T10:00:00",
    "endTime": "2024-01-01T11:00:00",
    "maxCapacity": 10,
    "currentCapacity": 3,
    "availableCapacity": 7,
    "status": "AVAILABLE",
    "createdAt": "2024-01-01T09:00:00",
    "updatedAt": "2024-01-01T09:00:00"
  },
  {
    "id": 2,
    "boothId": 1,
    "boothName": "카페 부스",
    "startTime": "2024-01-01T11:00:00",
    "endTime": "2024-01-01T12:00:00",
    "maxCapacity": 10,
    "currentCapacity": 10,
    "availableCapacity": 0,
    "status": "FULL",
    "createdAt": "2024-01-01T09:00:00",
    "updatedAt": "2024-01-01T10:30:00"
  }
]
```

### 1.5 예약 가능한 타임슬롯 조회

**GET** `/booths/{boothId}/timeslots/available`

특정 부스의 예약 가능한 타임슬롯만 조회합니다.

#### 권한

- 모든 사용자 (인증 불필요)

#### Path Parameters

| 파라미터 | 타입 | 필수 | 설명    |
| -------- | ---- | ---- | ------- |
| boothId  | Long | O    | 부스 ID |

#### Response

**200 OK**

```json
[
  {
    "id": 1,
    "boothId": 1,
    "boothName": "카페 부스",
    "startTime": "2024-01-01T10:00:00",
    "endTime": "2024-01-01T11:00:00",
    "maxCapacity": 10,
    "currentCapacity": 3,
    "availableCapacity": 7,
    "status": "AVAILABLE",
    "createdAt": "2024-01-01T09:00:00",
    "updatedAt": "2024-01-01T09:00:00"
  }
]
```

---

## 2. Reservation V2 API

### Base URL

```
/v2/reservations
```

### 2.1 예약 생성

**POST** `/v2/reservations`

타임슬롯 기반 예약을 생성합니다.

#### 권한

- USER, HOST, ADMIN (인증 필요)

#### Request Body

```json
{
  "userId": 1,
  "boothId": 1,
  "festivalId": 1,
  "timeSlotId": 1,
  "numberOfPeople": 4,
  "paymentMethod": "CARD",
  "fcmToken": "fcm_token_string"
}
```

| 필드           | 타입    | 필수 | 설명        | 제약사항 |
| -------------- | ------- | ---- | ----------- | -------- |
| userId         | Long    | O    | 사용자 ID   |          |
| boothId        | Long    | O    | 부스 ID     |          |
| festivalId     | Long    | O    | 축제 ID     |          |
| timeSlotId     | Long    | O    | 타임슬롯 ID |          |
| numberOfPeople | Integer | O    | 예약 인원   | 1 이상   |
| paymentMethod  | String  | X    | 결제 방법   |          |
| fcmToken       | String  | X    | FCM 토큰    |          |

#### Response

**201 Created**

```json
{
  "id": 1,
  "userId": 1,
  "boothId": 1,
  "festivalId": 1,
  "numberOfPeople": 4,
  "paymentMethod": "CARD",
  "createdAt": "2024-01-01T09:30:00",
  "timeSlotId": 1,
  "timeSlotStartTime": "2024-01-01T10:00:00",
  "timeSlotEndTime": "2024-01-01T11:00:00"
}
```

#### Error Responses

**400 Bad Request** - 잘못된 요청 데이터

```json
{
  "timestamp": "2024-01-01T10:00:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "예약 인원은 1명 이상이어야 합니다",
  "path": "/v2/reservations",
  "validationErrors": {
    "numberOfPeople": "예약 인원은 1명 이상이어야 합니다",
    "timeSlotId": "타임슬롯 ID는 필수입니다"
  }
}
```

**403 Forbidden** - 권한 없음 또는 이미 예약한 타임슬롯

```json
{
  "timestamp": "2024-01-01T10:00:00.000+00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "이미 해당 타임슬롯에 예약이 존재합니다",
  "path": "/v2/reservations"
}
```

**404 Not Found** - 타임슬롯/부스/축제를 찾을 수 없음

```json
{
  "timestamp": "2024-01-01T10:00:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "TimeSlot not found: 999",
  "path": "/v2/reservations"
}
```

**409 Conflict** - 타임슬롯 수용 인원 부족

```json
{
  "timestamp": "2024-01-01T10:00:00.000+00:00",
  "status": 409,
  "error": "Conflict",
  "message": "남은 자리가 부족합니다. 잔여 인원: 2명",
  "path": "/v2/reservations"
}
```

### 2.2 예약 취소

**DELETE** `/v2/reservations/{reservationId}`

기존 예약을 취소합니다.

#### 권한

- USER, HOST, ADMIN (본인 예약만)

#### Path Parameters

| 파라미터      | 타입 | 필수 | 설명    |
| ------------- | ---- | ---- | ------- |
| reservationId | Long | O    | 예약 ID |

#### Response

**204 No Content**

#### Error Responses

- **403 Forbidden**: 권한 없음 (타인의 예약)
- **404 Not Found**: 예약을 찾을 수 없음

### 2.3 내 예약 목록 조회

**GET** `/v2/reservations/my`

사용자의 모든 예약을 조회합니다.

#### 권한

- USER, HOST, ADMIN (인증 필요)

#### Response

**200 OK**

```json
[
  {
    "id": 1,
    "userId": 1,
    "boothId": 1,
    "festivalId": 1,
    "numberOfPeople": 4,
    "paymentMethod": "CARD",
    "createdAt": "2024-01-01T09:30:00",
    "timeSlotId": 1,
    "timeSlotStartTime": "2024-01-01T10:00:00",
    "timeSlotEndTime": "2024-01-01T11:00:00"
  }
]
```

### 2.4 타임슬롯별 예약 현황 조회

**GET** `/v2/reservations/timeslots/{timeSlotId}`

특정 타임슬롯의 모든 예약을 조회합니다.

#### 권한

- HOST, ADMIN

#### Path Parameters

| 파라미터   | 타입 | 필수 | 설명        |
| ---------- | ---- | ---- | ----------- |
| timeSlotId | Long | O    | 타임슬롯 ID |

#### Response

**200 OK**

```json
[
  {
    "id": 1,
    "userId": 1,
    "boothId": 1,
    "festivalId": 1,
    "numberOfPeople": 4,
    "paymentMethod": "CARD",
    "createdAt": "2024-01-01T09:30:00",
    "timeSlotId": 1,
    "timeSlotStartTime": "2024-01-01T10:00:00",
    "timeSlotEndTime": "2024-01-01T11:00:00"
  }
]
```

---

## 3. 데이터 모델

### TimeSlot

```json
{
  "id": "Long - 타임슬롯 ID",
  "boothId": "Long - 부스 ID",
  "boothName": "String - 부스 이름",
  "startTime": "LocalDateTime - 시작 시간",
  "endTime": "LocalDateTime - 종료 시간",
  "maxCapacity": "Integer - 최대 수용 인원",
  "currentCapacity": "Integer - 현재 예약 인원",
  "availableCapacity": "Integer - 예약 가능 인원",
  "status": "TimeSlotStatus - 타임슬롯 상태",
  "createdAt": "LocalDateTime - 생성 시간",
  "updatedAt": "LocalDateTime - 수정 시간"
}
```

### TimeSlotStatus (Enum)

- `AVAILABLE`: 예약 가능
- `FULL`: 만석
- `CLOSED`: 예약 마감

### Reservation (Updated)

```json
{
  "id": "Long - 예약 ID",
  "userId": "Long - 사용자 ID",
  "boothId": "Long - 부스 ID",
  "festivalId": "Long - 축제 ID",
  "timeSlotId": "Long - 타임슬롯 ID",
  "numberOfPeople": "Integer - 예약 인원",
  "paymentMethod": "String - 결제 방법",
  "createdAt": "LocalDateTime - 예약 생성 시간",
  "timeSlotStartTime": "LocalDateTime - 타임슬롯 시작 시간",
  "timeSlotEndTime": "LocalDateTime - 타임슬롯 종료 시간"
}
```

---

## 4. 필드별 상세 설명

### TimeSlot 필드 상세

| 필드                | 설명             | 주의사항                                 |
| ------------------- | ---------------- | ---------------------------------------- |
| `id`                | 타임슬롯 고유 ID | 읽기 전용                                |
| `boothId`           | 부스 ID          | 타임슬롯이 속한 부스                     |
| `boothName`         | 부스 이름        | 조회 시에만 포함                         |
| `startTime`         | 시작 시간        | ISO 8601 형식, endTime보다 이전이어야 함 |
| `endTime`           | 종료 시간        | ISO 8601 형식, startTime보다 이후여야 함 |
| `maxCapacity`       | 최대 수용 인원   | 1 이상의 정수                            |
| `currentCapacity`   | 현재 예약 인원   | 읽기 전용, 예약 생성/취소 시 자동 계산   |
| `availableCapacity` | 잔여 수용 인원   | 읽기 전용, maxCapacity - currentCapacity |
| `status`            | 타임슬롯 상태    | AVAILABLE/FULL/CLOSED                    |
| `createdAt`         | 생성 시간        | 읽기 전용                                |
| `updatedAt`         | 수정 시간        | 읽기 전용                                |

### Reservation 필드 상세

| 필드                | 설명               | 주의사항                           |
| ------------------- | ------------------ | ---------------------------------- |
| `id`                | 예약 고유 ID       | 읽기 전용                          |
| `userId`            | 예약자 ID          | 현재 로그인한 사용자와 일치해야 함 |
| `boothId`           | 부스 ID            | timeSlot이 속한 부스와 일치해야 함 |
| `festivalId`        | 축제 ID            | 부스가 속한 축제와 일치해야 함     |
| `timeSlotId`        | 타임슬롯 ID        | 예약하려는 타임슬롯                |
| `numberOfPeople`    | 예약 인원          | 1 이상, 타임슬롯 잔여 인원 이하    |
| `paymentMethod`     | 결제 방법          | 선택사항 (CARD, CASH, FREE 등)     |
| `timeSlotStartTime` | 타임슬롯 시작 시간 | 조회 시에만 포함                   |
| `timeSlotEndTime`   | 타임슬롯 종료 시간 | 조회 시에만 포함                   |

### 타임슬롯 상태 전이

```
AVAILABLE → FULL (currentCapacity = maxCapacity일 때)
FULL → AVAILABLE (예약 취소로 currentCapacity < maxCapacity일 때)
AVAILABLE → CLOSED (HOST/ADMIN이 수동 마감)
CLOSED → AVAILABLE (HOST/ADMIN이 다시 열기)
FULL → CLOSED (HOST/ADMIN이 수동 마감)
```

---

## 4. 에러 코드

### HTTP Status Codes

- **200 OK**: 성공적인 조회/수정
- **201 Created**: 성공적인 생성
- **204 No Content**: 성공적인 삭제
- **400 Bad Request**: 잘못된 요청 데이터
- **401 Unauthorized**: 인증 필요
- **403 Forbidden**: 권한 없음
- **404 Not Found**: 리소스를 찾을 수 없음
- **409 Conflict**: 비즈니스 규칙 위반 (중복 예약, 수용 인원 초과 등)
- **500 Internal Server Error**: 서버 내부 오류

### 비즈니스 규칙

1. **타임슬롯 중복**: 같은 부스에 동일한 시간대의 타임슬롯은 생성할 수 없습니다.
2. **수용 인원 검증**: 예약 인원이 타임슬롯의 잔여 수용 인원을 초과할 수 없습니다.
3. **중복 예약 방지**: 사용자는 동일한 타임슬롯에 중복 예약할 수 없습니다.
4. **권한 검증**: HOST는 본인이 관리하는 부스의 타임슬롯만 관리할 수 있습니다.
5. **예약 취소**: 사용자는 본인의 예약만 취소할 수 있습니다.

---

## 5. Frontend 개발 가이드

### 5.1 일반적인 사용 시나리오

#### 시나리오 1: 사용자가 부스 예약하기

**단계별 API 호출**

1. **부스 선택 및 타임슬롯 조회**

   ```javascript
   // 예약 가능한 타임슬롯 조회
   const response = await fetch("/booths/1/timeslots/available");
   const availableSlots = await response.json();
   ```

2. **예약 생성**

   ```javascript
   const reservationData = {
     userId: currentUser.id,
     boothId: selectedBooth.id,
     festivalId: selectedFestival.id,
     timeSlotId: selectedTimeSlot.id,
     numberOfPeople: 4,
     paymentMethod: "CARD",
   };

   const response = await fetch("/v2/reservations", {
     method: "POST",
     headers: {
       "Content-Type": "application/json",
       Cookie: `JSESSIONID=${sessionId}`,
     },
     body: JSON.stringify(reservationData),
   });
   ```

3. **예약 확인**
   ```javascript
   const myReservations = await fetch("/v2/reservations/my", {
     headers: { Cookie: `JSESSIONID=${sessionId}` },
   });
   ```

#### 시나리오 2: HOST가 타임슬롯 관리하기

1. **타임슬롯 생성**

   ```javascript
   const timeSlotData = {
     startTime: "2024-01-01T10:00:00",
     endTime: "2024-01-01T11:00:00",
     maxCapacity: 10,
   };

   const response = await fetch("/booths/1/timeslots", {
     method: "POST",
     headers: {
       "Content-Type": "application/json",
       Cookie: `JSESSIONID=${sessionId}`,
     },
     body: JSON.stringify(timeSlotData),
   });
   ```

2. **예약 현황 확인**
   ```javascript
   const reservations = await fetch("/v2/reservations/timeslots/1", {
     headers: { Cookie: `JSESSIONID=${sessionId}` },
   });
   ```

### 5.2 에러 처리 가이드

```javascript
async function createReservation(reservationData) {
  try {
    const response = await fetch("/v2/reservations", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Cookie: `JSESSIONID=${sessionId}`,
      },
      body: JSON.stringify(reservationData),
    });

    if (!response.ok) {
      const errorData = await response.json();

      switch (response.status) {
        case 400:
          // 입력 데이터 유효성 검사 실패
          displayValidationErrors(errorData.validationErrors);
          break;
        case 401:
          // 인증 필요
          redirectToLogin();
          break;
        case 403:
          // 권한 없음 또는 중복 예약
          showErrorMessage(errorData.message);
          break;
        case 404:
          // 리소스를 찾을 수 없음
          showErrorMessage("요청한 데이터를 찾을 수 없습니다.");
          break;
        case 409:
          // 수용 인원 부족
          showErrorMessage(errorData.message);
          refreshTimeSlotInfo(); // 타임슬롯 정보 새로고침
          break;
        default:
          showErrorMessage("알 수 없는 오류가 발생했습니다.");
      }
      return null;
    }

    return await response.json();
  } catch (error) {
    console.error("Network error:", error);
    showErrorMessage("네트워크 오류가 발생했습니다.");
    return null;
  }
}
```

### 5.3 상태 관리 가이드

#### 타임슬롯 상태 처리

```javascript
function getTimeSlotStatusText(status) {
  switch (status) {
    case "AVAILABLE":
      return "예약 가능";
    case "FULL":
      return "만석";
    case "CLOSED":
      return "예약 마감";
    default:
      return "알 수 없음";
  }
}

function getTimeSlotStatusColor(status) {
  switch (status) {
    case "AVAILABLE":
      return "#4CAF50"; // 녹색
    case "FULL":
      return "#F44336"; // 빨간색
    case "CLOSED":
      return "#9E9E9E"; // 회색
    default:
      return "#757575";
  }
}
```

#### 실시간 데이터 업데이트

```javascript
// 타임슬롯 정보 주기적 업데이트
function startTimeSlotPolling(boothId) {
  return setInterval(async () => {
    try {
      const response = await fetch(`/booths/${boothId}/timeslots`);
      const timeSlots = await response.json();
      updateTimeSlotUI(timeSlots);
    } catch (error) {
      console.error("Failed to refresh timeslots:", error);
    }
  }, 30000); // 30초마다 업데이트
}
```

### 5.4 인증 상태 관리

```javascript
// 세션 확인
async function checkAuthStatus() {
  try {
    const response = await fetch("/auth/status", {
      credentials: "include",
    });
    return response.ok;
  } catch (error) {
    return false;
  }
}

// 권한 확인
function hasPermission(userRole, requiredRole) {
  const roleHierarchy = {
    USER: 1,
    HOST: 2,
    ADMIN: 3,
  };

  return roleHierarchy[userRole] >= roleHierarchy[requiredRole];
}
```

### 5.5 데이터 표시 형식

#### 날짜/시간 포맷팅

```javascript
function formatDateTime(dateTimeString) {
  const date = new Date(dateTimeString);
  return date.toLocaleString("ko-KR", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  });
}

function formatTimeRange(startTime, endTime) {
  const start = new Date(startTime);
  const end = new Date(endTime);

  return `${start.toLocaleTimeString("ko-KR", {
    hour: "2-digit",
    minute: "2-digit",
  })} - ${end.toLocaleTimeString("ko-KR", {
    hour: "2-digit",
    minute: "2-digit",
  })}`;
}
```

### 5.6 성능 최적화 팁

1. **데이터 캐싱**: 자주 조회되는 타임슬롯 정보는 로컬 스토리지에 캐시
2. **지연 로딩**: 필요할 때만 예약 상세 정보 로드
3. **배치 요청**: 여러 부스의 타임슬롯을 한번에 조회하는 API 활용 고려
4. **낙관적 업데이트**: UI 즉시 업데이트 후 서버 확인

---

## 6. 자주 묻는 질문 (FAQ)

### Q1: 동시에 같은 타임슬롯에 예약하면 어떻게 되나요?

A: 서버에서 비관적 락(Pessimistic Lock)을 사용하여 동시성을 제어합니다. 먼저 처리된 요청이 성공하고, 나머지는 409 Conflict 에러가 발생합니다.

### Q2: 예약 후 타임슬롯 상태는 언제 업데이트되나요?

A: 예약 생성/취소 즉시 currentCapacity와 status가 자동으로 업데이트됩니다.

### Q3: HOST는 다른 HOST의 부스 타임슬롯을 관리할 수 있나요?

A: 아니요. HOST는 본인이 생성한 부스의 타임슬롯만 관리할 수 있습니다.

### Q4: 타임슬롯 시간 범위에 제한이 있나요?

A: 부스의 운영 시간(startDateTime ~ endDateTime) 내에서만 타임슬롯을 생성할 수 있습니다.

### Q5: 예약 시간과 타임슬롯 시간이 다를 수 있나요?

---

## 7. 트러블슈팅

### 문제: 타임슬롯 생성 시 409 에러

**원인**: 동일한 시간대에 이미 타임슬롯이 존재
**해결**: 기존 타임슬롯 조회 후 겹치지 않는 시간으로 변경

### 문제: 예약 생성 시 403 에러 (중복 예약)

**원인**: 동일한 사용자가 같은 타임슬롯에 이미 예약
**해결**: 내 예약 목록을 확인하고 중복 예약 방지 로직 추가

### 문제: 타임슬롯 상태가 실시간으로 업데이트되지 않음

**원인**: 클라이언트에서 데이터를 캐시하고 있거나 새로고침이 필요
**해결**: 주기적인 폴링 또는 사용자 액션 후 데이터 새로고침

### 문제: 세션 만료로 인한 401 에러

**원인**: JSESSIONID 쿠키가 만료되거나 없음
**해결**: 로그인 페이지로 리다이렉트 후 재로그인

---

## 8. 개발 시 체크리스트

### Frontend 개발자용 체크리스트

#### 기본 기능

- [ ] 타임슬롯 목록 조회 및 표시
- [ ] 예약 가능한 타임슬롯만 필터링
- [ ] 예약 생성 폼 및 유효성 검사
- [ ] 예약 취소 기능
- [ ] 내 예약 목록 표시

#### 에러 처리

- [ ] 네트워크 에러 처리
- [ ] 401/403 에러 시 적절한 사용자 안내
- [ ] 400 에러 시 유효성 검사 메시지 표시
- [ ] 409 에러 시 데이터 새로고침

#### 사용자 경험

- [ ] 로딩 상태 표시
- [ ] 성공/실패 메시지 표시
- [ ] 타임슬롯 상태별 색상 구분
- [ ] 잔여 인원 정보 표시
- [ ] 날짜/시간 포맷팅

#### 권한 관리

- [ ] 로그인 상태 확인
- [ ] 역할별 UI 표시/숨김
- [ ] HOST용 관리 기능 구분

#### 성능 최적화

- [ ] 불필요한 API 호출 최소화
- [ ] 데이터 캐싱 구현
- [ ] 실시간 업데이트 구현

이 종합적인 API 명세서를 통해 타임슬롯 기반 예약 시스템을 성공적으로 구현할 수 있습니다.
