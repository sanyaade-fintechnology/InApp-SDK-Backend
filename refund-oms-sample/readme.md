# OMS example for refunds

OMS stands for Outgoing Messaging System. This system enables payleven to send out notifications about asynchronous
payment events to integrators and merchants. The notifications are sent as a JSON formatted HTTP-POST request to a
configured target URL.

## Getting started

The sample is a Spring Boot application. Therefore you only have to execute

`mvn package && java -jar target/refund-oms-sample-1.0-SNAPSHOT.jar`

## Configuration

In the application.properties file you will find some default values for applicationId and apiKey.
If you want to test with your own data please adjust the following values:

* `payleven.applicationIdentifier` - Set here the identifier you configured during your registration.
* `payleven.apiKey` - Set here the API key you received via email after the registration.

## Packages

The full sample contains a standalone application. For your own integration focus on
the following packages:

* `controllers` - This package contains the sample receiver including HMAC verification and response creation.
* `hmac` - This package contains all classes to create and validate an HMAC.

To get more information about HMACs please refer to our [OMS Documentation Page](http://developer.payleven.com/docs/OMS/In-App/index.html#/hmac).

