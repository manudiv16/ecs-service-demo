FROM openjdk:11 AS builder

ENV SBT_VERSION 1.5.5
RUN curl -L -o sbt-$SBT_VERSION.zip https://github.com/sbt/sbt/releases/download/v$SBT_VERSION/sbt-$SBT_VERSION.zip \
	&& unzip sbt-$SBT_VERSION.zip -d ops
WORKDIR /app
COPY . /app
RUN /ops/sbt/bin/sbt assembly


FROM openjdk:17-ea-22-jdk-oracle 
WORKDIR /app
RUN groupadd -g 999 appuser && \
	useradd -r -u 999 -g appuser appuser
USER appuser
COPY --from=builder --chown=appuser:appuser /app/target/scala-3.0.0/scala3-simple-assembly-0.1.0.jar .
EXPOSE 3000

HEALTHCHECK CMD curl -f http://localhost:3000/healthz || exit 1

CMD java -jar ./scala3-simple-assembly-0.1.0.jar
