# OAuth Client API 상세 문서

## 개요

OAuth 클라이언트 생성, 조회 및 Secret 갱신 기능을 제공합니다.\
이 문서에서는 OAuth 클라이언트 관리 API의 엔드포인트를
설명합니다.

------------------------------------------------------------------------

## 1. 클라이언트 목록 조회

**GET** `/api/oauth-client/`\
현재 로그인된 사용자의 클라이언트 목록을 조회합니다.

### 응답 예시

``` json
[
    {
        "id": 4,
        "clientId": "cck_ranking",
        "clientName": "cck_ranking",
        "clientUri": "https://ranking.cubingclub.com",
        "clientDescription": "CCK competition's record uploader, viewer",
        "clientOwner": null,
        "redirectUri": "https://ranking.cubingclub.com/confirm",
        "postLogoutRedirectUri": "https://ranking.cubingclub.com",
        "clientType": "NORMAL",
        "clientSecretExpiresAt": "2026-08-19T00:28:27.714552",
        "createdAt": "2025-08-19T00:28:27.598693",
        "updatedAt": "2025-08-19T00:28:27.714552",
        "active": true
    },
    {
        "id": 5,
        "clientId": "cck_recorder",
        "clientName": "cck_recorder",
        "clientUri": "",
        "clientDescription": "CCK record upload IoT object",
        "clientOwner": null,
        "redirectUri": null,
        "postLogoutRedirectUri": "",
        "clientType": "IOT",
        "clientSecretExpiresAt": "2026-08-19T00:28:27.80946",
        "createdAt": "2025-08-19T00:28:27.739975",
        "updatedAt": "2025-08-19T00:28:27.80946",
        "active": true
    }
]
```

### 응답

-   성공: `200 OK` + `List<OAuthClientResponseDTO>`

------------------------------------------------------------------------

## 2. 클라이언트 생성

**POST** `/api/oauth-client/`  
클라이언트를 생성하는 기능입니다.  
해당 작업 이후, **관리자 승인**이 있어야만 SSO 로그인을 사용할 수 있습니다.

### 요청 파라미터
| 필드                          | 타입      | 설명                              |
|-----------------------------|---------|---------------------------------|
| clientId                    | String  | 클라이언트 식별자 지정                    |
| clientName                  | String  | 클라이언트 이름                        |
| clientUri                   | String  | 클라이언트 uri                       |
| clientDescription           | String  | 클라이언트에 대한 설명                    |
| redirectUri                 | String  | 코드를 받을 uri                      |
| postLogoutRedirectUri       | String  | 로그아웃 이후 돌아갈 Uri                 |
| requireAuthorizationConsent | boolean | 정책 동의 여부                        |
| clientType                  | String  | 일반 클라이언트인지, IOT인지 표시(인증 방식 달라짐) |

### 요청 본문

``` json
{
    "clientId": "CCK_Payment",
    "clientName": "CCK_Payment",
    "clientUri": "https://payment.cubingclub.com",
    "clientDescription": "CCK competition register, payment site",
    "redirectUri": "https://payment.cubingclub.com/login/confirm",
    "postLogoutRedirectUri": "https://payment.cubingclub.com",
    "requireAuthorizationConsent": true,
    "clientType": "NORMAL"
}
```

### 응답 예시

``` json
{
    "id": 8,
    "clientId": "CCK_Payment",
    "clientSecret": null,
    "clientOwner": { ... },
    "clientName": "CCK_Payment",
    "clientUri": "https://payment.cubingclub.com",
    "clientDescription": "CCK competition register, payment site",
    "redirectUri": "https://payment.cubingclub.com/login/confirm",
    "postLogoutRedirectUri": "https://payment.cubingclub.com",
    "requireAuthorizationConsent": true,
    "clientType": "NORMAL",
    "clientSecretExpiresAt": null,
    "createdAt": "2025-08-19T00:48:25.5954925",
    "updatedAt": "2025-08-19T00:48:25.5954925",
    "active": false
}
```

### 응답

-   성공: `200 OK` + `OAuthClientResponseDTO`
-   실패: `403 FORBIDDEN` (권한 없음)

------------------------------------------------------------------------

## 3. 클라이언트 Secret 갱신

**POST** `/api/oauth-client/secret`

승인된 Client에 대해 비밀번호를 발급/갱신하는 API입니다.
### 요청 파라미터
| 필드        | 타입      | 설명           |
|-----------|---------|--------------|
| clientId  | String  | 클라이언트 식별자 지정 |
| expireDay | Integer | Secret 만료일자  |

### 요청 본문

``` json
{
  "clientId": "new-client",
  "expireDay": 90
}
```

### 응답 예시

``` json
{
    "clientId": "CCK_Payment",
    "clientSecret": "DcfJuhjenpSEscIkXtRwfkvZFFOm5gqX",//해당 부분에서 1회만 secret 노출
    "clientName": "CCK_Payment",
    "clientUri": "https://payment.cubingclub.com",
    "redirectUri": "https://payment.cubingclub.com/login/confirm",
    "postLogoutRedirectUri": "https://payment.cubingclub.com",
    "clientType": "NORMAL",
    "clientSecretExpiresAt": "2026-08-19T00:51:40.7401705"
}
```

### 응답

-   성공: `200 OK` + 새로운 Secret 정보
-   실패:
    -   `400 BAD REQUEST`: 관리자가 승인하지 않은 클라이언트\
    -   `403 FORBIDDEN`: 권한 없음

------------------------------------------------------------------------
