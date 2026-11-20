# project-setup 코드 요약

## T1 Spring Boot 서버 띄우기
커밋: 597d5a5

| 파일 | 변경 |
|---|---|
| server-spring/settings.gradle | 새로 |
| server-spring/build.gradle | 새로 |
| server-spring/.gitignore | 새로 |
| server-spring/src/main/java/com/newtenco/blog/BlogApplication.java | 새로 |
| server-spring/src/main/resources/application.properties | 새로 |
| server-spring/gradlew, gradlew.bat, gradle/wrapper | 새로 (Gradle 9.2.1 wrapper 4개 파일 복사) |

```groovy
// server-spring/settings.gradle
rootProject.name = 'blog'
```

```groovy
// server-spring/build.gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '4.1.2'
    id 'io.spring.dependency-management' version '1.1.7'
}

group = 'com.newtenco'
version = '0.0.1-SNAPSHOT'

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-webmvc'
    compileOnly 'org.projectlombok:lombok'
    developmentOnly 'org.springframework.boot:spring-boot-devtools'
    annotationProcessor 'org.projectlombok:lombok'
}
```

```text
// server-spring/.gitignore
build/
.gradle/
.idea/
*.iml
out/
bin/
```

```java
// server-spring/src/main/java/com/newtenco/blog/BlogApplication.java
package com.newtenco.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
    }
}
```

```properties
// server-spring/src/main/resources/application.properties
server.port=8080
server.servlet.encoding.charset=UTF-8
server.servlet.encoding.force=true
```

## T2 루트에서 테스트 돌리기
커밋: 297d662
- 학생 코드 변경 없음

## T3 H2 메모리 DB 연결하고 콘솔 켜기
커밋: 0808bcf

| 파일 | 변경 |
|---|---|
| server-spring/build.gradle | 고침 |
| server-spring/src/main/resources/application.properties | 고침 |

```diff
diff --git a/server-spring/build.gradle b/server-spring/build.gradle
index bfe0a70..bc59eb1 100644
--- a/server-spring/build.gradle
+++ b/server-spring/build.gradle
@@ -18,8 +18,11 @@ repositories {
 }
 
 dependencies {
+    implementation 'org.springframework.boot:spring-boot-h2console'
+    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
     implementation 'org.springframework.boot:spring-boot-starter-webmvc'
     compileOnly 'org.projectlombok:lombok'
     developmentOnly 'org.springframework.boot:spring-boot-devtools'
+    runtimeOnly 'com.h2database:h2'
     annotationProcessor 'org.projectlombok:lombok'
 }
diff --git a/server-spring/src/main/resources/application.properties b/server-spring/src/main/resources/application.properties
index 52c388d..1b4a29a 100644
--- a/server-spring/src/main/resources/application.properties
+++ b/server-spring/src/main/resources/application.properties
@@ -1,3 +1,14 @@
 server.port=8080
 server.servlet.encoding.charset=UTF-8
 server.servlet.encoding.force=true
+
+spring.datasource.driver-class-name=org.h2.Driver
+spring.datasource.url=jdbc:h2:mem:test
+spring.datasource.username=sa
+spring.datasource.password=
+spring.h2.console.enabled=true
+
+spring.jpa.hibernate.ddl-auto=create
+spring.jpa.show-sql=true
+spring.jpa.properties.hibernate.format_sql=true
+spring.jpa.open-in-view=false
```
