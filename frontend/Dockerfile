FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

RUN apt-get update && apt-get install -y nodejs npm git && rm -rf /var/lib/apt/lists/*

COPY . .
RUN chmod +x gradlew

RUN --mount=type=cache,id=gradle-cache,target=/root/.gradle \
    --mount=type=cache,id=gradle-build-cache,target=/app/.gradle \
    --mount=type=cache,id=npm-cache,target=/root/.npm \
    ./gradlew kotlinUpgradePackageLock --no-daemon && \
    ./gradlew buildK2ts --build-cache --configuration-cache --parallel --no-daemon --stacktrace && \
    npm install && \
    npm run build

FROM nginx:stable-alpine AS frontend
WORKDIR /frontend
RUN rm -rf /usr/share/nginx/html/*
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/webApp/dist /usr/share/nginx/html
EXPOSE 80

CMD ["/bin/sh", "-c", "envsubst < /usr/share/nginx/html/env.js > /usr/share/nginx/html/env.js.tmp && mv /usr/share/nginx/html/env.js.tmp /usr/share/nginx/html/env.js && nginx -g 'daemon off;'"]