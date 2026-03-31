# BridgeNet (CrossChain Bridge)

개발기간: 2025년 10월 ~ 2025년 12월

개발인원: 1인 개발

> 블록체인상의 환전소, 서로 다른 블록체인 간 자산을 전송할 수 있는 크로스체인 브릿지 서비스입니다.

## Flow
이 서비스는 다음과 같은 사용자 플로우를 가집니다.

사용자가 A체인에 컨트랙트로 자산 전송 → 백엔드에서 이벤트 감지 → B체인으로 자동 자산 전송

![제목 없는 비디오 - Clipchamp로 제작](https://github.com/user-attachments/assets/37296308-a783-439c-8f8e-8de7778a6e21)


## 💫 Features

### Auth
- 회원가입
- 로그인

### Bridge
- 네이티브 토큰 교환
- 교환 가능 체인 리스트 
- 교환 기록 조회 
- 교환 옵션 설정 (수동, 자동 처리)
- 스마트컨트랙트 코인 충전 
- 스마트컨트랙트 잔액 조회
- 교환 통계


## 📂 Architecture
- System
 
  <img width="1331" height="673" alt="image(1)" src="https://github.com/user-attachments/assets/7a08c90d-23e7-4742-b4f0-4b24d94f63e4" />
  
- ERD

  <img width="2369" height="1386" alt="image(2)" src="https://github.com/user-attachments/assets/ea5e2006-8d34-4cad-a0ef-d7d75aff8e49" />

## 🛠️ Stack
### Backend
- Languages: Java
- Frameworks: Spring Boot
- Database: Mysql 
- Blockchain: Web3j

### SmartContract
- Languages: Solidity
- Frameworks: Hardhat
- Library: OpenZeppelin

## 📁 Repository

SmartContract Repository <a href="https://github.com/kangbaek324/bridgenet-smartcontract">here</a> </br>
FrontEnd Repository <a href="https://github.com/kangbaek324/bridgenet-frontend">here</a>
