FROM node:24-alpine

ARG APP_VERSION=dev
ENV NODE_ENV=production \
    APP_VERSION=$APP_VERSION

WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci --omit=dev --no-audit --fund=false && npm cache clean --force
COPY src/ ./src/

USER node
EXPOSE 8080
CMD ["node", "src/server.js"]
