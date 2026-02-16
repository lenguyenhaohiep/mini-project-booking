## What's changed

### Business flows
#### Generate availabilities
![generation_flow.png](assets/generation_flow.png)
- Get unplanned time slots.
- Sort and merge overlap time slots.
- Extend slots to maximal duration - minutes (14 in this test) as availability could end after the slot end, if no overlapping with the next slot, appointments or availabilities (business logic confirmed before implementation).
- Get appointments that are in slots’ range.
- Get planned availabilities that are in slots’ range.
- Combine appointments and availabilities as occupied ranges.
- Sort and merge occupied ranges.
- Subtract occupied ranges from extended slots to get free ranges.
- Generate appointments with duration of 15 mins from free ranges.
#### Create appointments
- Validate request (time range, partitioner, patient).
- Ensure that no overlapping appointments for the given time range.
- Change status of availability to UNAVAILABLE.
- Create appointment with status BOOKED.
      
### Layer separation and models
- Update entities to support status life cycle
    - TimeSlot (NEW. MODIFIED, PLANNED) - MODIFIED for future support
    - Availability (FREE, UNAVAILABLE)
    - Appointment (BOOKED, CANCELLED) - CANCELLED for future support
    - `Instant` is used instead of `LocalDateTime`, so dates will be in UTC.
- Add a new model package for business logic, it’s better to have different models (for business) and entities (for db persistence), but. For this project scope, entities are still used to handle business logic.

### API
- New endpoint is added to create appointments `POST /appointments` ProAppointmentController. 
  - Currently, It allows creating appointment given start and end date without checking if start is in the past for demo purposes.
  - If the patient already has the overlap appointment with another practitioner, appointment will not be made. Note: currently, UI didn't handle this case, if you book the same slot for the same patient with a different practitioner, error displayed `An unexpected error has occurred.` in UI and need to refresh the page, and by doing so, appointment list will not displayed correctly. it's UI issue.

```
POST /appointments
{
    "patientId": "2",
    "practitionerId": "4",
    "startDate":"2021-02-10T09:30:00Z",
    "endDate": "2021-02-10T09:45:00Z"
}
```
dates should be in UTC format `YYYY-MM-DDTHH:mm:ssZ`

- Http code 202, 201, 400, 409, 500 supports
- DTO for API to isolate business logic and API transfer.
- Global exception handler to handle all exceptions and consistent error message. Error code is exception class for demo purpose only.

### Code
- Use bean constructor injection instead of `@Autowired`
- Unit test method name `given_when_then` for better semantic understanding
- Newer versions updated (gradle, spring boot, java)
- To avoid lots of changes when reviewing, some classes are not changed, eg. (class `ProAvailabilityServiceTest`, name of provided tests remains unchanged).

### Limit
- Tests do not cover all classes, only class related to the two main workflows are done.
- Appointment cancelled, timeslot modified are not handled.

### Assumptions
- The flows might not reflect the actual business logic
- Assumptions with implemented workflows
  - Free time ranges ìf the duration is not enough. eg. The Slot starts at 11.am, there is an availability at 11:25 added by practitioner, one availability 11:00-11:15 is generated and a gap from 11:15-11:25.
  - Patients could book appointment if non overlapping with other appointments.
  - Patients could book more than one appointment with the same practitioner.


## Run and test
### Project setup

Java 21 required, built locally with the version details

```
java 21.0.9 2025-10-21 LTS
Java(TM) SE Runtime Environment (build 21.0.9+7-LTS-338)
Java HotSpot(TM) 64-Bit Server VM (build 21.0.9+7-LTS-338, mixed mode, sharing)
```

### Run spring boot server

Build and start the server.

```
./gradlew build
./gradlew bootRun
```

Try it out with Swagger UI (http://localhost:8080/swagger-ui/index.html)

### H2 Console UI

H2 in memory db console could be accessed by http://localhost:8080/h2-console
Provide this connection details.

```
Driver Class:	org.h2.Driver
JDBC URL:	jdbc:h2:mem:example
User Name:	sa
Password:	password
```

### Testing

To run unit test

```
./gradlew test
```

## Improvements
### Api
- API versioning eg. `/v1/availabilities`
- Endpoint get should support pagination eg. `/v1/availabilities?practitionerId=1&$skip=100&$top=10`
- Create appointments `POST /appointments` should not allow past appointments.
- Endpoint availabilities support filter to return the slots starting from current time.
- Add correlationId for each request for observability.

### Entity
- Better id with `uuid` (if db sharding).
- Some technical fields such as `created_at`, `modified_at`, `created_by`, `modified_by` for better audit.
- Currently, entities and business models are identical, but for the bigger project scope with potential evolutions, it’s better to use separated business models for business logic, and MapStruct to convert to and from entities. It’s useful when entities could have more technical fields (eg. version).

### Feature
- Non-booked availabilities cleanup: scheduled job to delete or to mark past unbooked availabilities.

### Architecture
- Support async processing if high concurrency
    - Async api to accept the booking request.
    - Publish a message for handle asynchronously to a broker (kafka, rabitmq…).
    - A consumer to handle the request and persist into db.
    - An new api for status checking, and front-end could use polling strategy to get the status.
- Using clean/hexagonal architecture to isolate business domain, get more flexibility if infrastructure swap, eg from H2 to mongodb; core domain business will not be affected.
- The application is heavy read, could adopt the pattern CQRS.

### Testing
- Could use `testcontainers` for testing with real db.