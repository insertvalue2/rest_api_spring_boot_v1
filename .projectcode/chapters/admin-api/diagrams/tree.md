# 폴더 구조

비교 기준: done/user-update. 새로 생긴 파일과 고친 파일을 표시했다.

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
              admin/
                AdminController.java   새로
                AdminService.java   새로
              board/
                Board.java
                BoardController.java
                BoardRepository.java
                BoardRequest.java
                BoardResponse.java
                BoardService.java
              core/
                filter/
                  JwtAuthorizationFilter.java   고침
                handler/
                  errors/
                    BadRequestException.java
                    ForbiddenException.java
                    NotFoundException.java
                    UnauthorizedException.java
                  GlobalExceptionHandler.java
                util/
                  JwtUtil.java   고침
                  Resp.java
                  RespFilter.java
                SecurityConfig.java   고침
              reply/
                Reply.java
                ReplyController.java
                ReplyRepository.java
                ReplyRequest.java
                ReplyResponse.java
                ReplyService.java
              user/
                User.java   고침
                UserController.java
                UserRepository.java
                UserRequest.java
                UserResponse.java
                UserService.java
              BlogApplication.java
      resources/
        db/
          data.sql   고침
        application.properties
  .gitignore
  build.gradle
  gradlew
  gradlew.bat
  settings.gradle
gradlew
gradlew.bat
settings.gradle
```

