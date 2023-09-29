
# P2P Torrent

> 소켓 프로그래밍을 이용해 유저끼리 파일 공유하는 P2P 애플리케이션

## 기능 요구 사항

> 1. 로그인, 로그아웃, 회원 가입, 회원 탈퇴, 유저 본인 데이터 조회 기능
> 2. 파일 업로드 및 다운로드 기능
> 3. 유저 권한에 따른 파일 업로드 기능 제한
> 4. 서버에 업로드된 파일 목록 보기 및 키워드로 파일 검색 기능
> 5. 업로드된 파일 소유자가 파일 데이터를 조회, 수정 및 삭제 기능
> 6. 포인트 충전 및 파일 구매, 업로드 권한 구매, 다운로드 실패시 포인트 환불 기능
>

## 기술 스택

> - 언어 : Java
> - 네트워크 통신 : Socket
> - 네트워크 프로토콜 : Custom Socket Protocol 제작
> - 네트워크 보안 : 대칭키(AES) 방식으로 데이터 전송시 암호화, 수신시 복호화
> - 데이터베이스 : CSV 파일 형식으로 구현(FileStream으로 CRUD 구현)
> - URL Mapping : java reflection과 annotation을 활용해 디스패처 서블릿 패턴 구현
> - GUI : Swing


## Documents

### 1. Protocol    
:point_right: [자세히 보기](https://github.com/kyo705/Torrent-Service/wiki/Protocol)   

### 2. Class Diagram   
 :point_right: [자세히 보기](https://github.com/kyo705/Torrent-Service/wiki/Class-Diagram)   

### 3. Client Server URL API    
:point_right: [자세히 보기](https://github.com/kyo705/Torrent-Service/wiki/Client-Server-API)    



프로젝트 커밋 메시지 카테고리
-----------------------------------------
> - [INIT] — repository를 생성하고 최초에 파일을 업로드 할 때
> - [ADD] — 신규 파일 추가
> - [UPDATE] — 코드 변경이 일어날때
> - [REFACTOR] — 코드를 리팩토링 했을때
> - [FIX] — 잘못된 링크 정보 변경, 필요한 모듈 추가 및 삭제
> - [REMOVE] — 파일 제거
> - [STYLE] — 디자인 관련 변경사항
