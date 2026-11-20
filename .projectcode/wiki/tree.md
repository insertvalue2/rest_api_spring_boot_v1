# 폴더 구조

기준: board-paging 장 끝

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
                BoardController.java   고침
                BoardRepository.java   고침
                BoardResponse.java   고침
                BoardService.java   고침
              core/
                handler/
                  errors/
                    NotFoundException.java
                  GlobalExceptionHandler.java
                util/
                  Resp.java
              user/
                User.java
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
