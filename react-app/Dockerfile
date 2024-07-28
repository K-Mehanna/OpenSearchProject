FROM node:lts-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .

# ARG SPRING_URL=172.31.37.121
ARG SPRING_URL
ENV VITE_SPRING_URL=$SPRING_URL

RUN npm run build

FROM nginx:latest AS prod
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80/tcp
CMD ["/usr/sbin/nginx", "-g", "daemon off;"]