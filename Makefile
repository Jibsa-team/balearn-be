# Variables
COMPOSE_REDIS_ELK=docker-compose -f docker-compose-redis-elk.yml
COMPOSE_APP=docker-compose -f docker-compose-app.yml

.PHONY: all redis-elk app start-redis-elk start-app stop-redis-elk stop-app restart-redis-elk restart-app logs-redis-elk logs-app clean-redis-elk clean-app reload-nginx

# Start all services
all: start-redis-elk start-app
	@echo "All services started!"

# Redis + ELK commands
redis-elk: start-redis-elk
	@echo "Redis + ELK services started!"

start-redis-elk:
	$(COMPOSE_REDIS_ELK) up -d
	@echo "Redis + ELK services are running."

stop-redis-elk:
	$(COMPOSE_REDIS_ELK) down
	@echo "Redis + ELK services stopped."

restart-redis-elk: stop-redis-elk start-redis-elk
	@echo "Redis + ELK services restarted."

logs-redis-elk:
	$(COMPOSE_REDIS_ELK) logs -f

clean-redis-elk:
	$(COMPOSE_REDIS_ELK) down -v
	@echo "Redis + ELK services and volumes cleaned."

# Blue/Green App + Nginx commands
app: start-app
	@echo "Blue/Green App services started!"

start-app:
	$(COMPOSE_APP) up -d
	@echo "Blue/Green App services are running."

stop-app:
	$(COMPOSE_APP) down
	@echo "Blue/Green App services stopped."

restart-app: stop-app start-app
	@echo "Blue/Green App services restarted."

logs-app:
	$(COMPOSE_APP) logs -f

clean-app:
	$(COMPOSE_APP) down -v
	@echo "Blue/Green App services and volumes cleaned."

# Nginx reload command
reload-nginx:
	docker exec -it balearn-nginx nginx -s reload
	@echo "Nginx configuration reloaded."

# Full environment cleanup
clean: clean-redis-elk clean-app
	@echo "All services and volumes cleaned."
