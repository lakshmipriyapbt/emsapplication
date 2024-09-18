FROM node:18-alpine3.20
WORKDIR /reactapp
COPY package.json /reactapp
COPY package-lock.json /reactapp

RUN npm install
COPY . .
RUN npm run build
EXPOSE 3000
CMD ["npm","start"]
