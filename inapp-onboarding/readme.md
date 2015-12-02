# payleven In-App Onboarding API

This project provides a sample application to illustrate backend to backend communication to payleven's In-App Onboarding
API. The sample includes the possibility to create different types of merchants.

## Getting started

The sample is a Spring Boot application. Therefore you only have to execute

`mvn package && java -jar target/inapp-onboarding-1.0-SNAPSHOT.jar`

## Configuration

In the application.properties file you will find some default values for applicationId, apikey and an onboarding url.
If you want to test with your own data please adjust the following values:

* `payleven.onboarding.url` - target URL for REST requests. Please ask our support (developer@payleven.com) which URL you should use.
* `payleven.applicationIdentifier` - Set here the identifier you configured during your registration.
* `payleven.apiKey` - Set here the API key you received via email after the registration.

## Packages

The full sample contains a standalone application with web view and in-memory database. For your own integration focus on
the following packages:

* `sender` - This package contains all classes to create a REST request for the onboarding. The sender uses the classes from the `mac` package to create and check HMACs.
* `hmac` - This package contains all classes to create and validate an HMAC.

To get more information about HMACs, please refer to our [Documentation Page](https://developer.payleven.com/docs/In-App/index.html#/hmac).

