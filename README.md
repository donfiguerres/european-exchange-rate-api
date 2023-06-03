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
gradle bootRun
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
