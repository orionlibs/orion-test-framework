Yapily End-to-end Testing Framework
=============

Provides components needed to write JUnit-powered end-to-end tests.

## Features ##

* YAML-based test suite configuration.
* Facility to make API calls.
* Test-level or test class-level retry feature. Used for flaky tests (@Retry(attempts = 3, delayMs = 200)).
* Ability to stress-test tests by running them multiple times (@Stress(times = 10)).
* Ability to stop long-running tests (@TimeoutOverride(valueMs = 2000L)).
* Test-level or test class-level check of reachability of resources required for the test(s) to execute. If a resource is unreachable then the affected tests are skipped. (@RequiresResource(host = "https://dev.example.com", port = 443, timeoutMs = 300)).
* @Glue-annotated classes are scanned for @Glue-annotated instance and static methods and they are executed before all tests run. Useful for setting up the tests. All classes are scanned unless the "glue.scan.package" environment variable is set to the package prefix you desire.
* @Given-annotated classes are scanned for @Given-annotated instance and static methods and they are executed before each test runs. Tests affected are the ones that belong to classes annotated with "@ExtendWith(BeforeEachTestExecutionListener.class)". Each @Given-annotated class runs when it is configured to "point at" a particular test class. If it does not "point to" any class then the "given" methods run before every test. Useful for setting each test. All classes are scanned unless the "given.scan.package" environment variable is set to the package prefix you desire.
* @AfterEach-annotated classes are scanned for @AfterEach-annotated instance and static methods and they are executed after each test runs. Tests affected are the ones that belong to classes annotated with "@ExtendWith(AfterEachTestExecutionListener.class)". Each @AfterEach-annotated class runs when it is configured to "point at" a particular test class. If it does not "point to" any class then the "AfterEach" methods run after every test. Useful for releasing resources after each test. All classes are scanned unless the "after-each.scan.package" environment variable is set to the package prefix you desire.
* Mock server
* Load database fixtures or call data seed methods before the test
