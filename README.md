# Exchange Rate API

![actions workflow](https://github.com/donfiguerres/european-exchange-rate-api/actions/workflows/ci.yml/badge.svg)

## Introduction

This project provides a RESTful API to access and convert exchange rates. You
can do the following using this API

- Retrieve the current rate for a specific date.
- Convert an amount from one currency to another for a specific date
- Get historical data statistics such as the highest and average rate over
    a certain period.

## Building the Application

A gradle wrapper is already included in this project. You perform the build by
running the following command in the project's root directory.

```bash
./gradlew build
```

If you already have gradle installed then you can use that as well.

```bash
gradle build
```

## Running the Application

Run the application using the following command:

```bash
./gradlew bootRun
```

This will start the application and it will be accessible at
<http://localhost:8080>.

## OpenAPI Specification

This project uses `springdoc-openapi` to automatically generate API
documentation according to the OpenAPI specification. You can access this
documentation by running the project and visiting the following URL:

```text
http://localhost:8080/api-docs
```

For the UI version, visit:

```text
http://localhost:8080/swagger-ui.html
```

## Data Source

The data source used by this API is the CSV formatted historical Euro foreign
exchange reference rates data published by the European Central Bank at
<https://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist.zip>.

See also
<https://www.ecb.europa.eu/stats/policy_and_exchange_rates/euro_reference_exchange_rates/html/index.en.html>

## Development

In the development of this API, several architectural decisions have been made
to create a robust, maintainable, and easily extensible system. The following
are some key considerations and approaches that have been employed:

### Architecture

The system follows a layered architecture, with clear separation of concerns
among different components of the system. This separation makes the system more
manageable, maintainable, and testable. The layers include:

- Controller Layer: Handles HTTP requests and responses. It uses services to
perform the business logic and returns the results to the client.
- Service Layer: Contains the business logic of the application. It is
responsible for coordinating tasks and delegating work to data providers.
- Provider Layer: Responsible for providing data to the service layer. It
communicates with external data sources and parses the data into a format that
the service layer can use.

### Dependency Injection

Dependency Injection (DI) is used to manage dependencies between components. DI
contributes to the flexibility and testability of the system as dependencies
can be easily substituted or mocked during testing.

### Interface-driven Design

The system is designed with an interface-driven approach, which allows for
higher flexibility and interchangeability of components. For example, the
`DataDownloader` and `DataParser` interfaces can have different implementations
depending on the data source or format.

### Separation of Data Downloading and Parsing

The responsibilities of downloading data and parsing data are separated into
distinct classes (`DataDownloader` and `DataParser`). This separation follows the
Single Responsibility Principle, making each class easier to understand,
maintain, and test.

### Logging and Error Handling

Appropriate logging and error handling mechanisms have been implemented. In
case of any unexpected situation (e.g., a change in the data source format),
the system logs a warning message and continues functioning. This ensures the
robustness of the system.

### Unit Testing

Unit tests have been written for the components of the system to ensure their
correctness. Mocking is used to isolate the component under test and create
controlled test scenarios. Logging is also tested to ensure appropriate warning
messages are logged under specific conditions.

### Behavior Driven Development

A gherkin file is included and is located at
`test/resources/features/currency_exchange_rates.feature` to help facilitate
behavior driven development.

### Project Structure

The project is structured in a logical and intuitive manner. It includes the
following packages:

- `com.europeanexchangerates.exchangeapi.controller`: Contains the controllers
    that handle HTTP requests.
- `com.europeanexchangerates.exchangeapi.service`: Contains the services that
    perform business logic.
- `com.europeanexchangerates.exchangeapi.provider`: Contains the providers that
    fetch and parse data from external sources.
- `com.europeanexchangerates.exchangeapi.util`: Contains utility classes and
    interfaces, like data downloaders and parsers.
- `com.europeanexchangerates.exchangeapi.dto`: Contains data transfer objects
    (DTOs) that define the data structure.
- `com.europeanexchangerates.exchangeapi.exception`: Contains custom exception
    classes for specific error scenarios.
- `com.europeanexchangerates.exchangeapi.test`: Contains unit tests for the
    components.

This structure ensures that related classes are grouped together, making it
easy to navigate and understand the codebase. Future additions and
modifications can be made following this structure.

## Future Considerations

Some of the potential future considerations might include integration with a
database for persistent storage, implementation of caching for performance
improvement, and expansion to additional data sources. The current architecture
has been designed with such scalability in mind, ensuring that the system can
grow and adapt to changing needs.
