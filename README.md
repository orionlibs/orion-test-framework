End-to-end Testing Framework
=============

Provides components needed to write JUnit-powered end-to-end tests.

## Features ##

* YAML-based test suite configuration.
* @Glue-annotated classes are scanned for @Glue-annotated instance and static methods and they are executed before all tests run. Useful for setting up the tests. All classes are scanned unless the "glue.scan.package" environment variable is set to the package prefix you desire.
* Facility to make API calls.
