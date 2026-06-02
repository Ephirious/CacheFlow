# ===== Конфигурационные файлы NPM и Gradle ===== (чтобы понимать, какие зависимости скачивать)
FROM alpine AS skeleton
WORKDIR /staging

COPY . .
RUN find . -type f \
    -not -path "./buildSrc/*" \
    -not \( \
        -name "*.gradle.kts" \
        -or -name "*.properties" \
        -or -name "*.toml" \
        -or -name "gradlew" \
        -or -name "gradle-wrapper.jar" \
        -or -name "package.json" \
        -or -name "package-lock.json" \
    \) -delete


# Нужен, чтобы скачать npm, nodejs, git только 1 раз
FROM eclipse-temurin:17-jdk AS base
RUN apt-get update && apt-get install -y nodejs npm git && \
    rm -rf /var/lib/apt/lists/*


# ===== Скачивание зависимостей (Kotlin) =====
FROM base AS deps
WORKDIR /home/gradle/cache
ENV GRADLE_USER_HOME=/home/gradle/.gradle

COPY --from=skeleton /staging .

RUN chmod +x gradlew && ./gradlew help --no-daemon --info # Скачивает всё для котлина



# ===== СБОРКА =====
FROM base AS builder
WORKDIR /cacheflow
ENV GRADLE_USER_HOME=/root/.gradle

# Кэши из deps
COPY --from=deps /home/gradle/.gradle /root/.gradle

COPY . .

RUN chmod +x gradlew
RUN --mount=type=cache,target=/root/.gradle/caches/build-cache-1 \
    ./gradlew buildK2ts --no-daemon --build-cache

RUN --mount=type=cache,target=/root/.npm \
    npm install

RUN npm run build



# ===== ЗАПУСК =====
FROM nginx:stable-alpine AS frontend
WORKDIR /cacheflow

RUN rm -rf /usr/share/nginx/html/*

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /cacheflow/webApp/dist /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

