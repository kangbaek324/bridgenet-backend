# BridgeNet (CrossChain Bridge)

개발기간: 2025년 10월 ~ 2025년 12월

개발인원: 1인 개발

> 블록체인상의 환전소, 서로 다른 블록체인 간 자산을 전송할 수 있는 크로스체인 브릿지 서비스입니다.

---

## 목차
- [Flow](#flow)
- [Features](#-features)
- [Stack](#️-stack)
- [Architecture](#-architecture)
- [실행 방법](#-실행-방법)
- [API Endpoints](#-api-endpoints)
- [Repository](#-repository)

---

## Flow
이 서비스는 다음과 같은 사용자 플로우를 가집니다.

사용자가 A체인에 컨트랙트로 자산 전송 → 백엔드에서 이벤트 감지 → B체인으로 자동 자산 전송
- 요청 트랜잭션과, 다른체인으로 발행한 **트랜잭션**을 **추적**하는 Worker가 존재
- 트랜잭션이 필요한 **컨펌 수를** **충족**했을때 트랜잭션 존재여부에 따라 서버에서 처리
  
![제목 없는 비디오 - Clipchamp로 제작](https://github.com/user-attachments/assets/37296308-a783-439c-8f8e-8de7778a6e21)

---

## 💫 Features

### Auth
- 논스값 발급
- 회원가입
- 로그인
- 액세스토큰 재발급

### Chain
- 체인 리스트 조회
- 체인 관리 (추가 / 수정 / 삭제)
- 체인 활성화 / 비활성화
- 체인 랭킹 조회
- 컨트랙트 잔액 조회
- 컨트랙트 화이트리스트 등록

### RPC
- RPC 목록 조회
- RPC 관리 (추가 / 수정 / 삭제)

### Bridge
- 교환 요청 조회 (전체 / 상세)
- 교환 요청 수동 처리
- 교환 처리 옵션 설정 (자동 / 수동)
- 유저별 교환 기록 조회

---

## 🛠️ Stack
### Backend
- Languages: Java
- Frameworks: Spring Boot
- Database: MySQL
- Blockchain: Web3j

### SmartContract
- Languages: Solidity
- Frameworks: Hardhat
- Library: OpenZeppelin

---

## 📂 Architecture
- System

  <img width="1331" height="673" alt="image(1)" src="https://github.com/user-attachments/assets/7a08c90d-23e7-4742-b4f0-4b24d94f63e4" />

- ERD

  <img width="3853" height="3599" alt="mermaid-diagram-2026-04-07-160825" src="https://github.com/user-attachments/assets/ab80e292-9f20-41a4-a90e-e6a53af860e8" />

---

## 🚀  How To Run

### 사전 요구사항
- Docker, Docker Compose

### 환경변수 설정
프로젝트 루트에 `.env` 파일을 생성하고 아래 값을 설정합니다.

```env
DB_URL=jdbc:mysql://mysql:3306/bridgenet
DB_USERNAME=root
DB_PASSWORD=<MySQL 비밀번호>
JWT_SECRET=<JWT 시크릿 키>
ETHEREUM_PRIVATE_KEY=<이더리움 개인키>
```

### 실행

```bash
docker compose up -d
```

서버가 정상 실행되면 `http://localhost:8081` 에서 접근할 수 있습니다.

---

## 📡 API Endpoints

API 명세는 Swagger UI에서 확인할 수 있습니다: `http://localhost:8081/swagger-ui.html`

---

## 📁 Repository

SmartContract Repository <a href="https://github.com/kangbaek324/bridgenet-smartcontract">here</a> </br>
FrontEnd Repository <a href="https://github.com/kangbaek324/bridgenet-frontend">here</a>
