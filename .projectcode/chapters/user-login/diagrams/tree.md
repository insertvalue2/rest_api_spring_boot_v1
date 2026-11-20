# 폴더 구조

비교 기준: done/user-join. 새로 생긴 파일과 고친 파일을 표시했다.

```
gradle/
  wrapper/
    gradle-wrapper.jar
    gradle-wrapper.properties
server-spring/
  gradle/
    wrapper/
      gradle-wrapper.jar
      gradle-wrapper.properties
  src/
    main/
      java/
        com/
          newtenco/
            blog/
              board/
                Board.java
                BoardController.java
                BoardRepository.java
                BoardResponse.java
                BoardService.java
              core/
                handler/
                  errors/
                    BadRequestException.java
                    NotFoundException.java
                    UnauthorizedException.java   새로
                  GlobalExceptionHandler.java   고침
                util/
                  JwtUtil.java   새로
                  Resp.java
                SecurityConfig.java   고침
              user/
                User.java
                UserController.java   고침
                UserRepository.java   고침
                UserRequest.java   고침
                UserResponse.java   고침
                UserService.java   고침
              BlogApplication.java
      resources/
        db/
          data.sql
        application.properties   고침
  .gitignore
  build.gradle   고침
  gradlew
  gradlew.bat
  settings.gradle
gradlew
gradlew.bat
settings.gradle
```

