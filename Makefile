
ifeq ($(shell command podman -v 2> /dev/null),)
    CNTR := docker
else
    CNTR := podman
endif

MVN:=./mvnw

.PHONY: run up down
run:
	${MVN} spring-boot:run

.PHONY: up
up:
	${CNTR} compose up -d

.PHONY: down
down:
	${CNTR} compose down

.PHONY: clean c
clean c: up
	${MVN} clean
	${CNTR} compose down --volumes --remove-orphans

${MVN}:
	@echo "Please install the maven wrapper."
	@exit 1

.PHONY: tidy spotless pretty format f
tidy spotless pretty format f:
	${MVN} spotless:apply
	${MVN} com.github.ekryd.sortpom:sortpom-maven-plugin:sort
