# 폴더 구조

기준: user-join 장 끝

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
                    BadRequestException.java   새로
                    NotFoundException.java
                  GlobalExceptionHandler.java   고침
                util/
                  Resp.java
                SecurityConfig.java   새로
              user/
                User.java   고침
                UserController.java   새로
                UserRepository.java   새로
                UserRequest.java   새로
                UserResponse.java   새로
                UserService.java   새로
              BlogApplication.java
      resources/
        db/
          data.sql
        application.properties
  .gitignore
  build.gradle   고침
  gradlew
  gradlew.bat
  settings.gradle
gradlew
gradlew.bat
settings.gradle
```
