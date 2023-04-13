FROM bellsoft/liberica-openjre-alpine as builder
COPY maven /app
WORKDIR /app
RUN java -Djarmode=layertools -jar ${project.artifactId}-${project.version}.jar extract

# the second stage of our build will copy the extracted layers
FROM bellsoft/liberica-openjdk-debian
ENV LANG=ru_RU.UTF-8 LANGUAGE=ru_RU:ru LC_ALL=ru_RU.UTF-8 TZ=Europe/Moscow
WORKDIR app
COPY --from=builder app/dependencies/ ./
COPY --from=builder app/spring-boot-loader/ ./
COPY --from=builder app/snapshot-dependencies/ ./
COPY --from=builder app/invitro-dependencies/ ./
COPY --from=builder app/application/ ./

ENV JAVA_OPTS=""
ENTRYPOINT java ${JAVA_OPTS} org.springframework.boot.loader.JarLauncher
