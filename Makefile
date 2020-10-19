INFRA_FOLDER="$(shell pwd)/infra/configurations/"

.PHONY: install run-front run-back docker-build docker-tag docker-push check-clean-archi test

install:
	cd frontend && npm install
run-front:
	cd frontend && npm start
run-back:
	docker-compose up -d
	cd backend && ./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.config.additional-location=$(INFRA_FOLDER)" -Dspring-boot.run.profiles="local"
docker-build:
	docker build --no-cache -f infra/docker/DockerfileBuildApp . -t monitorfish-app:$(VERSION)
docker-tag:
	docker tag monitorfish-app:$(VERSION) docker.pkg.github.com/mtes-mct/monitorfish/monitorfish-app:$(VERSION)
docker-push:
	docker push docker.pkg.github.com/mtes-mct/monitorfish/monitorfish-app:$(VERSION)
check-clean-archi:
	cd backend/tools && ./check-clean-architecture.sh
test: check-clean-archi
	cd backend && ./mvnw test
restart-app:
	cd infra && sudo docker-compose up -d --build app