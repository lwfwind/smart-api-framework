# Smart-api-framework - a light, common http api automation framework

Smart-api-framework is a light, common http api automation framework based on [TestNG](http://testng.org/doc/index.html) and [HttpClient](http://hc.apache.org/httpcomponents-client-ga/).

* Tags: TestNG, HttpClient, XML, API, Automation, Test, Keyword-driver

## Features

* Support keyword-driver, No Coding and easy to config
* Support restful web server such as get, post, put and delete http method
* Support concurrent
* Re-run failed test cases
* Easy integration with CI system

## XML Structure
```xml
DataConfig -- config test url and httpMethod
    TestData
        Before -- preset environment such as database
        Setup -- config setup url and httpMethod, such as login action
            Param -- config setup parameters
        Header -- config request headers 
            Header -- config Header parameters
            Cookie -- config Cookie parameters
        Param -- config test parameters
        ExpectResults -- config expect result
            Contain	-- assert actual result contain specify string
            Pair -- assert actual result contain specify key-value
            AssertTrue -- assert expression is true
        After -- reset environment
```
## Example
### &nbsp;&nbsp; Support function/sql action in before/after
```xml
<DataConfig url="V1/Students/login" httpMethod="post">
    <TestData name="data1" desc="更改手机号登录">
        <Before>
            <Function clsName="test.java.LogicHandler" methodName="changeStudentsMobile"/>
            <Sql>update ebk_students set mobile=18078788787 where id=123456;</Sql>
        </Before>
        <Param name="username" value="#[sql1.mobile]">
            <Sql name="sql">select trim(mobile) as mobile,password from ebk_students where id=123456;
            </Sql>
        </Param>
        <Param name="password" value="#[sql.password]" />
        <ExpectResults>
            <Pair>errorCode:200</Pair>
            <Pair>errorMsg:登录成功</Pair>
        </ExpectResults>
        <After>
            <Function clsName="test.java.LogicHandler" methodName="resertStudentMobile"/>
            <Sql>update ebk_students set mobile=888888888 where id=123456;</Sql>
        </After>
    </TestData>
<DataConfig>
```

### &nbsp;&nbsp; Support execute setup action before execution of test method
```xml
<DataConfig url="V1/ClassRecords/bookClass/" httpMethod="put">
  <TestData name="data1" desc="约课成功">
        <Setup name="setup1" url="V1/Students/login/" httpMethod="post">
            <Param name="username" value="#[sql1.mobile]">
                <Sql name="sql1">select id,mobile,password from ebk_students where status=1 and acoin>100 
                and level is not null;
                </Sql>
            </Param>
            <Param name="password" value="#[sql1.password]" />
        </Setup>
        <Param name="cid" value="#[sql4.id]">
            <Sql name="sql4">select id from ebk_class_records where status=0 and begin_time>unix_timestamp()
             and free_try=0 ;
            </Sql>
        </Param>
        <ExpectResults>
            <Pair>errorCode:200</Pair>
            <Pair>errorMsg:约课成功</Pair>
        </ExpectResults>
    </TestData>
</DataConfig>
```

### &nbsp;&nbsp; Support get param's value from setup action response
```xml
<DataConfig url="V1/ClassRecords/bookClass/" httpMethod="put">
  <TestData name="data1" desc="约课成功">
        <Setup name="setup1" url="V1/Students/login/" httpMethod="post">
            <Param name="username" value="#[sql1.mobile]">
                <Sql name="sql1">select id,mobile,password from ebk_students where status=1 and acoin>100 
                and level is not null;
                </Sql>
            </Param>
            <Param name="password" value="#[sql1.password]" />
        </Setup>
        <Param name="cid" value="#[setup1.id]" />
        <ExpectResults>
            <Pair>errorCode:200</Pair>
            <Pair>errorMsg:约课成功</Pair>
        </ExpectResults>
    </TestData>
</DataConfig>
```

### &nbsp;&nbsp; Support to get param's value from sql/function
```xml
<DataConfig url="V1/Students/login" httpMethod="post">
    <TestData name="data3" desc="登录成功">
        <Param name="username" value="#[sql1.mobile]">
            <Sql name="sql">select trim(mobile) as mobile from ebk_students where password =
                'e10adc3949ba59abbe56e057f20f883e'  and tx_sig_expiredtime> curdate()+86400;
            </Sql>
        </Param>
        <Param name="password" value="e10adc3949ba59abbe56e057f20f883e" />
        <Param name="code">
            <Function clsName="test.java.LogicHandler" methodName="codeGenerator" />
        </Param>
        <ExpectResults>
            <Pair>errorCode:200</Pair>
            <Pair>errorMsg:登录成功</Pair>
        </ExpectResults>
    </TestData>  
<DataConfig>
```

### &nbsp;&nbsp; Support Pair/Contain/AssertTrue type for expect results
```xml
<DataConfig url="V1/Students/login" httpMethod="post">
    <TestData name="data3" desc="登录成功">
        <ExpectResults>
            <Pair>errorCode:#[code]</Pair>
            <Contain>.*("id":"#[sql.mobile]").*</Contain>
            <AssertTrue>"#[code]"=="#[sql.mobile]"</AssertTrue>
            <Sql name="sql">select trim(mobile) as mobile from ebk_students where password =
                            'e10adc3949ba59abbe56e057f20f883e'  and tx_sig_expiredtime> curdate()+86400;
                        </Sql>
            <Function name="code" clsName="test.java.LogicHandler" methodName="codeGenerator" />
        </ExpectResults>
    </TestData>  
<DataConfig>
```

### &nbsp;&nbsp; Support sql/function for expect results
```xml
<DataConfig url="V1/Students/login" httpMethod="post">
    <TestData name="data3" desc="登录成功">
        <ExpectResults>
            <Pair>errorCode:#[code]</Pair>
            <Contain>.*("id":"#[sql.mobile]").*</Contain>
            <Sql name="sql">select trim(mobile) as mobile from ebk_students where password =
                            'e10adc3949ba59abbe56e057f20f883e'  and tx_sig_expiredtime> curdate()+86400;
                        </Sql>
            <Function name="code" clsName="test.java.LogicHandler" methodName="codeGenerator" />
        </ExpectResults>
    </TestData>  
<DataConfig>
```

### &nbsp;&nbsp; Support regular expression for expect result in contain/pair both 
```xml
<DataConfig url="V2/ClassRecords/classDetail/" httpMethod="get">
    <TestData name="GetClassDetailSuccess" desc="获取数据成功">
         <Param name="username" value="#[sql.mobile]">
             <Sql name="sql">select c.begin_time as begin_time,s.mobile as mobile ,password,c.id as cid 
             from ebk_students as s left join ebk_class_records as c ON s.id = c.sid limit 100;
             </Sql>
        </Param>
        <Param name="password" value="#[sql.password]" />
        <Param name="cid" value="#[sql.cid]" />
        <ExpectResults>
            <Pair>errorCode:200</Pair>
            <Pair>errorMsg:老师已在(QQ|Skype)上等你，快去上课吧</Pair>
            <Contain>.*("id":"#[sql.cid]").*("begin_time":"#[sql.begin_time]").*</Contain>
        </ExpectResults>
    </TestData>
</DataConfig>
```

### &nbsp;&nbsp; Support execute repeated times(invocationCount)
```xml
<DataConfig url="V1/Students/login" httpMethod="post" invocationCount="2000">
    <TestData name="data1" desc="更改手机号登录">
        <Param name="username" value="#[sql1.mobile]">
            <Sql name="sql">select trim(mobile) as mobile,password from ebk_students where id=123456;
            </Sql>
        </Param>
        <Param name="password" value="#[sql.password]" />
        <ExpectResults>
            <Pair>errorCode:200</Pair>
            <Pair>errorMsg:登录成功</Pair>
        </ExpectResults>
    </TestData>
<DataConfig>
```

### &nbsp;&nbsp; Support request headers
```xml
<DataConfig url="V1/Students/login" httpMethod="post">
    <TestData name="data1" desc="更改手机号登录">
        <Headers>
            <Header name="Content-Type" value="application/x-www-form-urlencoded;charset=UTF-8" />
            <Cookie name="PHPSESSIONID" value="xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx" />
        </Headers>
        <Param name="key" value="value" />
        <ExpectResults>
            <Pair>errorCode:200</Pair>
            <Pair>errorMsg:成功</Pair>
        </ExpectResults>
    </TestData>
<DataConfig>
```

Demo project please refer to  [smart-api-automation-example](https://github.com/lwfwind/smart-api-automation-example)

## Contributors
   Charlie [https://github.com/zhuyecao321](https://github.com/zhuyecao321)<br/>
   Niki    [https://github.com/ZhangyuBaolu](https://github.com/ZhangyuBaolu)<br/>
   Wind    [https://github.com/lwfwind](https://github.com/lwfwind)<br/>