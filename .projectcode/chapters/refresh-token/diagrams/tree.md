# 폴더 구조

비교 기준: done/admin-roles. 새로 생긴 파일과 고친 파일을 표시했다.

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
                AdminController.java
                AdminService.java
              auth/
                AuthController.java   새로
                AuthRequest.java   새로
                AuthResponse.java   새로
                RefreshToken.java   새로
                RefreshTokenRepository.java   새로
                RefreshTokenService.java   새로
              board/
                Board.java
                BoardController.java
                BoardRepository.java
                BoardRequest.java
                BoardResponse.java
                BoardService.java
              core/
                filter/
                  JwtAuthorizationFilter.java
                handler/
                  errors/
                    BadRequestException.java
                    ForbiddenException.java
                    NotFoundException.java
                    UnauthorizedException.java
                  GlobalExceptionHandler.java
                util/
                  JwtUtil.java
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
                User.java
                UserController.java   고침
                UserRepository.java
                UserRequest.java
                UserResponse.java   고침
                UserService.java   고침
              BlogApplication.java
      resources/
        db/
          data.sql
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

