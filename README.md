# JPA를 활용한 일정 관리 앱 서버
## 🏆GOAL
1. JPA를 활용하여 CRUD를 구현하고 이를 통해 객체 지향적으로 데이터 다루기
2. JPA를 활용하여 데이터베이스를 관리하고 영속성에 대해 이해하기
3. 회원가입, 로그인을 통해 인증/인가를 이해하고 JWT를 활용하기
4. RestTemplate을 통해 외부 정보를 호출하고 활용하기


### 🔥공통 조건
- 모든 테이블은 고유 식별자(ID)를 가진다.
- `3 Layer Architecture` 에 따라 각 Layer의 목적에 맞게 개발한다.
- CRUD 필수 기능은 모두 데이터베이스 연결 및  `JPA`를 사용해서 개발한다.
- `JDBC`와 `Spring Security`는 사용하지 않는다.
- 인증/인가 절차는 `JWT`를 활용하여 개발한다.
- JPA의 연관관계는 **양방향**으로 구현한다.


## API 명세서

|기능|Method|URL|request|response|상태코드|
|---|-------|-----|-----|-------|--------|
|일정 등록|POST|/schedules|요청 body|단건 응답 정보|201 Created|
|일정 전체 조회|GET|/schedules|요청 param (Pageable)|다건 응답 정보|200 OK|
|일정 단건 조회|GET|/schedules/{id}|요청 param|단건 응답 정보|200 OK|
|일정 수정|PUT|/schedules/{id}|요청 body|수정된 DTO|200 OK or 403 Forbidden|
|일정 삭제|DELETE|/schedules/{id}|요청 body|삭제 메시지|200 OK or 403 Forbidden|
|댓글 저장|POST|/schedules/{id}/comments|요청 body|저장된 DTO|201 Created|
|댓글 전체 조회|GET|/schedules/{id}/comments|요청 param|다건 응답 정보|200 OK|
|댓글 단건 조회|GET|/schedules/{id}/comments/{comment_id}|요청 param|단건 응답 조회|200 OK|
|댓글 수정|PUT|/schedules/{id}/comments/{comment_id}|요청 body|수정된 DTO|200 OK|
|댓글 삭제|DELETE|/schedules/{id}/comments/{comment_id}|요청 body|삭제 메시지|200 OK|
|유저 전체 조회|GET|/users|-|다건 응답 조회|200 OK|
|유저 단건 조회|GET|/users/{id}|요청 param|단건 응답 조회|200 OK|
|회원가입|POST|/users/register|요청 body|등록한 유저 DTO|201 Created or 400 Bad Request|
|로그인|POST|/users/login|요청 body|JWT 토큰|200 OK or 400 Bad Request or 401 Unauthorized|
|유저 수정|PUT|/users/{id}|요청 body|수정된 DTO|200 OK or 403 Forbidden|
|유저 삭제|DELETE|/users/{id}|요청 body|삭제 메시지|200 OK or 403 Forbidden|


## ERD
<img width="604" alt="image" src="https://github.com/user-attachments/assets/4ce215fe-7c89-4487-b0b4-47c92875fb90">


## SQL
```sql
create table schedule(
                         id int not null primary key auto_increment,
                         username varchar(100) not null,
                         title varchar(200),
                         content varchar(500),
                         created_at datetime default current_timestamp,
                         updated_at timestamp default current_timestamp on update current_timestamp
);

create table comment(
                        id int not null primary key auto_increment,
                        content varchar(300) not null,
                        username varchar(100) not null,
                        schedule_id int not null,
                        foreign key (schedule_id) references schedule(id)
);

create table user(
                     id int not null primary key auto_increment,
                     username varchar(100) not null,
                     email varchar(200) not null
);

create table manager(
                        id int not null primary key auto_increment,
                        schedule_id int not null,
                        user_id int not null,
                        foreign key (schedule_id) references schedule(id),
                        foreign key (user_id) references user(id)
);
```

## 필수 구현 단계
### 1단계

#### 조건
1. 일정을 저장, 단건 조회, 수정할 수 있습니다.
2. 일정은 `작성 유저명`, `할일 제목`, `할일 내용`, `작성일`, `수정일` 필드를 갖고 있습니다.

### 2단계
#### 조건

1. 일정에 댓글을 달 수 있습니다.
    1. 댓글과 일정은 연관관계를 가집니다.
2. 댓글을 저장, 단건 조회, 전체 조회, 수정, 삭제할 수 있습니다.
3. 댓글은 `댓글 내용`, `작성일`, `수정일`, `작성 유저명` 필드를 갖고 있습니다.


### 3단계
#### 조건

1. 일정을 Spring Data JPA의 `Pageable`과 `Page` 인터페이스를 활용하여 페이지네이션을 구현해주세요.
    1. `페이지 번호`와 `페이지 크기`를 쿼리 파라미터로 전달하여 요청하는 항목을 나타냅니다.
    2. `할일 제목`, `할일 내용`, `댓글 개수`, `일정 작성일`, `일정 수정일`, `일정 작성 유저명` 필드를 조회합니다.
    3. 디폴트 `페이지 크기`는 10으로 적용합니다.
2. 일정의 `수정일`을 기준으로 내림차순 정렬합니다.

### 4단계
#### 조건

1. 일정을 삭제할 때 일정의 댓글도 함께 삭제됩니다. 
    1. JPA의 영속성 전이 기능을 활용합니다.

### 5단계
#### 조건

1. 유저를 저장, 단건 조회, 전체 조회, 삭제할 수 있습니다. 
    1. 유저는 `유저명`, `이메일`, `작성일`, `수정일` 필드를 갖고 있습니다.
2. 일정은 이제 `작성 유저명` 필드 대신 `유저 고유 식별자` 필드를 가집니다.
3. 일정을 작성한 유저는 추가로 일정 담당 유저들을 배치할 수 있습니다. 
    1. 유저와 일정은 N:M 관계입니다. (`@ManyToMany` 사용 금지!)

### 6단계
#### 조건

1. 일정 단건 조회 시 담당 유저들의 `고유 식별자`, `유저명`, `이메일`이 추가로 포함됩니다.
2. 일정 전체 조회 시 담당 유저 정보가 포함되지 않습니다.
    1. JPA의 지연 로딩 기능을 활용합니다.

## 추가 구현 단계
### 7단계
#### 조건

1. 유저에 `비밀번호` 필드를 추가합니다.
    1. 비밀번호는 암호화되어야 합니다.
    2. 암호화를 위한 `PasswordEncoder`를 직접 만들어 사용합니다.
        - 참고 코드
            1. `build.gradle` 에 아래의 의존성을 추가해주세요.
                
                ```java
                implementation 'at.favre.lib:bcrypt:0.10.2'
                ```
                
            2. `config` 패키지가 없다면 추가하고, 아래의 클래스를 추가해주세요.
                
                ```java
                import at.favre.lib.crypto.bcrypt.BCrypt;
                import org.springframework.stereotype.Component;
                
                @Component
                public class PasswordEncoder {
                
                    public String encode(String rawPassword) {
                        return BCrypt.withDefaults().hashToString(BCrypt.MIN_COST, rawPassword.toCharArray());
                    }
                
                    public boolean matches(String rawPassword, String encodedPassword) {
                        BCrypt.Result result = BCrypt.verifyer().verify(rawPassword.toCharArray(), encodedPassword);
                        return result.verified;
                    }
                }
                ```
                
2. 유저 최초 생성(회원가입) 시 JWT를 발급 후 반환합니다.

### 8단계
#### 설명

1. JWT를 활용해 로그인 기능을 구현합니다.
2. 필터를 활용해 인증 처리를 할 수 있습니다.

#### 조건

1. `이메일`과 `비밀번호`를 활용해 로그인 기능을 구현합니다.
    1. 로그인 성공 시 JWT  발급 후 반환합니다.
2. 모든 요청에서 토큰을 활용하여 인증 처리를 합니다.
    1. 토큰은 `Header`에 추가합니다.
3. 회원가입과 로그인은 인증 처리에서 제외합니다.

#### ⚠️ 예외 처리

- 로그인 시 이메일과 비밀번호가 일치하지 않을 경우 401을 반환합니다.
- 토큰이 없는 경우 400을 반환합니다.
- 유효 기간이 만료된 토큰의 경우 401을 반환합니다.

### 9단계
#### 조건

1. 유저에 `권한`을 추가합니다.
    1. 권한은 `관리자`, `일반 사용자` 두 가지가 존재합니다.
    2. JWT를 발급할 때 유저의 권한 정보를 함께 넣어줍니다.
2. 일정 수정 및 삭제는 `관리자` 권한이 있는 유저만 할 수 있습니다.

#### ⚠️ 예외 처리

- 권한이 없는 유저의 경우 403을 반환합니다.

### 10단계
#### 조건

1. [**날씨 정보 데이터(링크)**](https://f-api.github.io/f-api/weather.json) API를 활용하여 오늘의 날씨를 조회할 수 있습니다.
    1. `RestTemplate`을 사용해 날씨를 조회합니다.
2. 일정에 `날씨` 필드를 추가합니다.
    1. 일정 생성 시에 날씨 정보를 생성일 기준으로 저장할 수 있습니다.

