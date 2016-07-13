## Smart-api-framework - a light, robust http api automation framework

Smart-api-framework is a light, robust http api automation framework based on [TestNG](http://testng.org/doc/index.html) and [HttpClient](http://hc.apache.org/httpcomponents-client-ga/).

* Tags: TestNG, HttpClient, XML, API, Automation, Test

## Features

* No Coding and easy to config
* Support get, post and put http method
* Support concurrent testing with testng
* Re-run failed test cases

## XML Structure
```xml
DataConfig -- config test url and httpMethod
    TestData
        Setup -- config setup url and httpMethod
            Param	-- config setup parameters
        Param -- config test parameters
        ExpectResult -- config expect result
            Contain	-- assert actual result contain specify string
            Pair -- assert actual result contain specify key-value
```