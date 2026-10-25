# elections

This is a Kotlin Multiplatform project targeting Web, Server.

* [/app/sharedLogic](./app/sharedLogic/src) is for the code that will be shared between app targets in the project.
  The most important subfolder is [commonMain](./app/sharedLogic/src/commonMain/kotlin). If preferred, you
  can add code to the platform-specific folders here too.

* [/app/webApp](./app/webApp) contains a React web application. It uses the Kotlin/JS library produced
  by the [sharedLogic](./app/sharedLogic) module.

* [/core](./core/src) is for the code that will be shared between all targets in the project.
  The most important subfolder is [commonMain](./core/src/commonMain/kotlin). If preferred, you
  can add code to the platform-specific folders here too.

* [/core](./core/src) also contains the elections domain (entities, repositories, services) and the
  serializable DTOs shared between the server and the clients.

* [/server](./server/src/main/kotlin) is for the Ktor server application (REST API, in-memory repositories
  and the BOE API client).

### API

| Method | Path                | Description                                                  |
|--------|---------------------|--------------------------------------------------------------|
| GET    | `/v1/parties`       | All parties, or filtered by `id`, `name` and `acronym` query params |
| GET    | `/v1/party/{id}`    | A single party                                               |
| POST   | `/v1/vote`          | Vote for a party: `{"partyId": "1", "userId": "user"}`       |
| GET    | `/v1/votes`         | Vote count for every party                                   |
| GET    | `/v1/boe-parties`   | BOE summary                                                  |

A Postman collection ([Elections.postman_collection.json](./Elections.postman_collection.json)) and a JMeter
test plan ([HTTP Request.jmx](./HTTP%20Request.jmx)) are included.

### Running the apps

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and options:

- Server: `./gradlew :server:run`
- Server tests: `./gradlew :server:test`
- Server with Docker: `docker compose up --build`
- Web app:
  1. Install [Node.js](https://nodejs.org/en/download) (which includes `npm`)
  2. Build and run the web application:
     ```shell
     npm run build:shared
     npm install
     npm run start
     ```

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…