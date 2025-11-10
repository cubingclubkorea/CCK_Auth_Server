# Account API 상세 문서

## 개요
사용자 인증, 계정 정보 조회/수정, 회원가입, 비밀번호 재발급 등 계정 관련 기능을 제공합니다.  
이 문서에서는 **인증이 필요한 주요 API** 예시를 다룹니다.

---

## 1. 인증 코드로 로그인 마무리
**POST** `/oauth2/token`  
OAuth2 인증 과정에서 발급받은 **Authorization Code**를 사용해 Access Token과 Refresh Token을 발급받습니다.

### 요청 예시
```http
POST /oauth2/token
Content-Type: application/x-www-form-urlencoded

grant_type=authorization_code&
code=abc123xyz456&
redirect_uri=https://localhost:3000/confirm&
client_id=your-client-id&
client_secret=your-client-secret
```

### 응답 예시
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR...",
  "refresh_token": "dGhpc2lzYXJlZnJlc2h0b2tlbg==",
  "expires_in": 3600,
  "token_type": "Bearer",
  "scope": "openid profile"
}
```

### 설명
- `code` : `/oauth2/authorize`에서 발급된 Authorization Code
- `access_token` : API 호출 시 사용 (WithCredentials 또는 Authorization Header)
- `refresh_token` : Access Token 갱신용
- `expires_in` : Access Token 유효기간 (초 단위)

---

### 주의사항: 해당 처리는 반드시 백엔드를 통해서 해야 함.(secret 노출 방지)

## 2. 본인 계정 정보 반환
**GET** `/api/auth/info`  
로그인된 사용자의 계정 정보를 반환합니다.

### Axios 요청 예시

```javascript
import axios from 'axios';

axios.get('/api/auth/info', { withCredentials: true });
```

### 응답 예시
```json
{
  "email": "user@example.com",
  "name": "홍길동",
  "enName": "Hong Gil-dong",
  "birthDate": "1990-01-01",
  "position": "CUSTOMER",
  "state": "Seoul",
  "gender": "MALE"
}
```

### 반환 객체 (`AccountDTO`)
| 필드        | 타입               | 설명                                       |
|-----------|------------------|------------------------------------------|
| email     | string           | 이메일                                      |
| name      | string           | 한글 이름                                    |
| enName    | string           | 영문 이름                                    |
| birthDate | date(yyyy-MM-dd) | 생년월일                                     |
| position  | enum(UserRole)   | 사용자 권한(ROOT, ADMIN, CUSTOMER, SCRAMBLER) |
| state     | string           | 지역 정보(Seoul, Busan 등)                    |
| gender    | enum(Gender)     | 성별(MALE, FEMALE, UNKNOWN)                |

---

## 3. 개인 정보 수정
**POST** `/api/auth/edit`  
로그인된 사용자의 비밀번호, 상태, 이름 등을 수정합니다.

### 요청 예시
```javascript
import axios from 'axios';

axios.post(
  '/api/auth/edit',
  {
    password: 'currentPassword123',
    newPassword: 'newPassword456',
    newPasswordCheck: 'newPassword456',
    state: 'Seoul'
  },
  {
    withCredentials: true,
    headers: {
      'Content-Type': 'application/json'
    }
  }
)
```

### 응답 예시
성공 시:
```http
HTTP/1.1 200 OK
```

실패 시:
```http
HTTP/1.1 400 Bad Request
Content-Type: application/json

{
  "error": "Password does not match"
}
```

### 요청 객체 (`AccountEditDTO`)
| 필드               | 타입     | 설명                    |
|------------------|--------|-----------------------|
| password         | string | 현재 비밀번호               |
| newPassword      | string | 새 비밀번호                |
| newPasswordCheck | string | 새 비밀번호 확인             |
| state            | string | 지역 정보(Seoul, Busan 등) |

####  newPassword랑 state 둘 중 하나만 선택할 것.

---