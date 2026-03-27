# TCP 기반 메신저 시스템

> Java 21과 TCP 소켓 통신을 기반으로 구현한 멀티 모듈 메신저 시스템

---

## 프로젝트 개요

- **개발 기간**: 2026. 01. 28 ~ 2026. 02. 05
- **개발 인원**: 2명
- **아키텍처**: Client-Server 구조
- **통신 방식**: TCP Socket 기반
- **데이터 포맷**: JSON

---

## 기술 스택

### Backend

- Java 21

### Build & Structure

- Maven Multi Module
    - messenger-common
    - messenger-server
    - messenger-client

### Test & Packaging

- JUnit5
- Maven Shade Plugin

---

## 메시지 프로토콜

- Length-Prefix 기반 메시지 규격

```
message-length: [길이]
{JSON}
```

- 모든 요청은 sessionId 포함
- 서버에서 세션 검증 수행

---

## 공통 설계 요소

### 예외 처리

- 모든 실패 응답은 공통 포맷 사용

```json
{
  "code": "ERROR_CODE",
  "message": "에러 메시지"
}
```

### 보안

- 로그인 기반 인증
- 인증된 사용자만 기능 접근 가능

---

## 주요 기능

### 사용자 관리

- 로그인 / 로그아웃
- 세션 기반 인증 (sessionId)
- 전체 사용자 목록 및 온라인 상태 조회

---

### 채팅방 기능

- 채팅방 생성
- 채팅방 목록 조회
- 채팅방 입장 / 퇴장
- 참여자 목록 조회

---

### 메시징 기능

- 채팅방 메시지 전송 (Broadcast)
- 귓속말
- 메시지 히스토리 조회
- 메시지 전송 시 messageId 반환

---

### 디자인 패턴 적용

| 패턴             | 적용 목적             |
|----------------|-------------------|
| Observer       | UI와 메시지 수신 로직 분리  |
| Command        | 채팅 명령어 객체화        |
| Factory Method | 메시지 타입별 객체 생성     |
| Strategy       | 메시지 처리 로직 유연하게 교체 |

---

## 테스트

- JUnit5 기반 단위 테스트 작성

---

## 빌드 및 실행

```bash
# 빌드
mvn clean package

# 실행
java -jar messenger-server.jar
java -jar messenger-client.jar
```

---

## 프로젝트 핵심 포인트

- TCP 기반 메시지 프로토콜 직접 설계
- 세션 기반 인증 및 보안 처리 구현
- Queue 기반 비동기 아키텍처 적용
- 다양한 디자인 패턴 실전 적용
- 확장성을 고려한 멀티 모듈 구조 설계