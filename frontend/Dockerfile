FROM eclipse-temurin:17-jdk AS builder
WORKDIR /cacheflow

RUN apt-get update && \
    apt-get install -y nodejs npm git && \
    rm -rf /var/lib/apt/lists/*

COPY package.json package-lock.json* ./
COPY webApp/package.json webApp/

RUN --mount=type=cache,target=/root/.npm \
    npm install

COPY . .

RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew buildK2ts --no-daemon --build-cache

RUN npm run build

FROM nginx:stable-alpine AS frontend
WORKDIR /cacheflow

RUN rm -rf /usr/share/nginx/html/*

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /cacheflow/webApp/dist /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

