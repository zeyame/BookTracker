FROM node:20.14.0-alpine

WORKDIR /client-app

COPY ./package.json .

RUN npm install

COPY . .

EXPOSE 3000

CMD [ "npm", "start" ]




