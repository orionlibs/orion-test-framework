Yapily End-to-end Testing Framework
=============

Provides components needed to write JUnit-powered end-to-end tests.

## Features ##

* YAML-based test suite configuration.
* @Glue-annotated classes are scanned for @Glue-annotated instance and static methods and they are executed before all tests run. Useful for setting up the tests. All classes are scanned unless the "glue.scan.package" environment variable is set to the package prefix you desire.
* @Given-annotated classes are scanned for @Given-annotated instance and static methods and they are executed before each test runs. Tests affected are the ones that belong to classes annotated with "@ExtendWith(PerTestExecutionListener.class)". Each @Given-annotated class runs when it is configured to "point at" a particular test class. If it does not "point to" any class then the "given" methods run before every test. Useful for setting each test. All classes are scanned unless the "given.scan.package" environment variable is set to the package prefix you desire.
* Facility to make API calls.
