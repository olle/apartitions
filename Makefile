
ifeq ($(shell command podman -v 2> /dev/null),)
    CNTR := docker
else
    CNTR := podman
endif

MVN:=./mvnw

.PHONY: run up down init-bucket clean c tidy spotless pretty format f

run: init-bucket
	${MVN} spring-boot:run -Dspring-boot.run.jvmArguments="-Djava.library.path=$(PWD)/lib"

up:
	${CNTR} compose up -d

down:
	${CNTR} compose down

init-bucket:
	@${CNTR} compose exec -T minio mc alias set local http://localhost:9000 minio password
	@${CNTR} compose exec -T minio mc rm local/archive --recursive --force 2> /dev/null || true
	@${CNTR} compose exec -T minio mc mb local/archive 2> /dev/null || true

clean c: up
	${MVN} clean
	${CNTR} compose down --volumes --remove-orphans

${MVN}:
	@echo "Please install the maven wrapper."
	@exit 1

tidy spotless pretty format f:
	${MVN} spotless:apply
	${MVN} com.github.ekryd.sortpom:sortpom-maven-plugin:sort
