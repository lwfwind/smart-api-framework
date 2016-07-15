# Smart-api-framework - a light, robust http api automation framework

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
        Before -- preset environment such as database
        Setup -- config setup url and httpMethod, such as login action
            Param	-- config setup parameters
        Param -- config test parameters
        ExpectResult -- config expect result
            Contain	-- assert actual result contain specify string
            Pair -- assert actual result contain specify key-value
        After -- reset environment 
```

```xml
<DataConfig url="V2/ClassRecords/classDetail/" httpMethod="get">
    <TestData name="GetClassDetailSuccess" desc="获取数据成功">
        <Setup name="setup" url="V1/Students/login/" httpMethod="post">
            <Param name="username" value="#{sql.mobile}">
                <Sql name="sql">select s.id as id,s.mobile as mobile ,password,c.id as cid from ebk_students as s
                    left join ebk_class_records as c ON s.id = c.sid where
                    c.begin_time >unix_timestamp() and c.status=1 limit 100;
                </Sql>
            </Param>
            <Param name="password" value="#{sql.password}" />
        </Setup>
        <Param name="cid" value="#{sql.cid}" />
        <ExpectResult>
            <Pair>errorCode:200</Pair>
            <Pair>errorMsg:获取数据成功</Pair>
            <Contain>"id":"#{sql.cid}"</Contain>
        </ExpectResult>
    </TestData>
</DataConfig>
```

### Contributors:
    Charlie
    Niki    [https://github.com/ZhangyuBaolu](https://github.com/ZhangyuBaolu)
    Wind    [https://github.com/lwfwind](https://github.com/lwfwind)