# Smart-api-framework - a light, common http api automation framework

Smart-api-framework is a light, common http api automation framework based on [TestNG](http://testng.org/doc/index.html) and [HttpClient](http://hc.apache.org/httpcomponents-client-ga/).

* Tags: TestNG, HttpClient, XML, API, Automation, Test

## Features

* No Coding and easy to config
* Support restful web server such as get, post, put and delete http method
* Support concurrent testing with TestNG
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
        After -- reset environment ********
```
### Example
##### &nbsp;&nbsp; 1.Support get param's value from sql


```xml
<DataConfig url="V1/Students/login" httpMethod="post">
    <TestData name="data3" desc="登录成功">
        <Param name="username" value="#{sql1.mobile}">
            <Sql name="sql">select trim(mobile) as mobile from ebk_students where password =
                'e10adc3949ba59abbe56e057f20f883e'  and tx_sig_expiredtime> curdate()+86400;
            </Sql>
        </Param>
        <Param name="password" value="e10adc3949ba59abbe56e057f20f883e"></Param>
        <ExpectResult>
            <Pair>errorCode:200</Pair>
            <Pair>errorMsg:登录成功</Pair>
        </ExpectResult>
    </TestData>  
<DataConfig>
```
    
##### &nbsp;&nbsp; 2.Support Param's value from funcation where user defined  
```xml
<DataConfig url="V1/Students/login" httpMethod="post">
     <TestData name="data1" desc="用户不存在">
         <Param name="username">
             <Function clsName="test.java.LogicHandler" methodName="mobileGenerator"></Function>
         </Param>
         <Param name="password" value="e10adc3949ba59abbe56e057f20f883e"></Param>
         <ExpectResult>
             <Pair>errorCode:404</Pair>
             <Pair>errorMsg:用户不存在</Pair>
         </ExpectResult>
     </TestData>
</DataConfig>
```

##### &nbsp;&nbsp; 3.Contain can be used to create a matcher object that can match arbitrary character sequences against the regular expression  
```xml
<DataConfig url="V2/ClassRecords/classDetail/" httpMethod="get">
    <TestData name="GetClassDetailSuccess" desc="获取数据成功">
         <Param name="username" value="#{sql.mobile}">
             <Sql name="sql">select c.begin_time as begin_time,s.mobile as mobile ,password,c.id as cid from ebk_students as s
                      left join ebk_class_records as c ON s.id = c.sid limit 100;
             </Sql>
        </Param>
        <Param name="password" value="#{sql.password}" />
        <Param name="cid" value="#{sql.cid}" />
        <ExpectResult>
            <Pair>errorCode:200</Pair>
            <Pair>errorMsg:获取数据成功</Pair>
            <Contain>.*("id":"#{sql.cid}").*("begin_time":"#{sql.begin_time}").*</Contain>
        </ExpectResult>
    </TestData>
</DataConfig>
```

##### &nbsp;&nbsp; 4.Pair can be used to create a matcher object that can match arbitrary character sequences against the regular expression  
```xml
<DataConfig url="V2/ClassRecords/classDetail/" httpMethod="get">
    <TestData name="data1" desc="老师已在Skype上等你，快去上课吧">
        <Param name="username" value="#{sql.mobile}">
            <Sql name="sql">select s.id as id,s.mobile as mobile ,password,c.id as cid from ebk_students as s
                left join ebk_class_records as c ON s.id = c.sid where
                c.begin_time between unix_timestamp() and unix_timestamp()+360 and c.use_tool in("qq","skype") limit
                100;
            </Sql>
        </Param>
        <Param name="password" value="#{sql.password}"></Param>
        <Param name="cid" value="#{sql.cid}">
        </Param>
        <ExpectResult>
            <Pair>errorCode:410</Pair>
            <Pair>errorMsg:老师已在(QQ|Skype)上等你，快去上课吧</Pair>
        </ExpectResult>
    </TestData>
<DataConfig>
```

##### &nbsp;&nbsp; 5.Setup are mainly used to execute a certain steps of code before execution of test methods 
```xml
<DataConfig url="V1/ClassRecords/bookClass/" httpMethod="put">
  <TestData name="data1" desc="约课成功">
        <Setup name="setup1" url="V1/Students/login/" httpMethod="post">
            <Param name="username" value="#{sql1.mobile}">
                <Sql name="sql1">select id,mobile,password from ebk_students where status=1 and acoin>100 and level is
                    not null;
                </Sql>
            </Param>
            <Param name="password" value="#{sql1.password}"></Param>
        </Setup>
        <Param name="cid" value="#{sql4.id}">
            <Sql name="sql4">select id from ebk_class_records where status=0 and begin_time>unix_timestamp() and
                free_try=0 ;
            </Sql>
        </Param>
        <ExpectResult>
            <Pair>errorCode:200</Pair>
            <Pair>errorMsg:约课成功</Pair>
        </ExpectResult>
    </TestData>
</DataConfig>
```
##### &nbsp;&nbsp; 6.Before and After annotations are mainly used to execute a certain set of code before and after the execution of test methods 
```xml
<DataConfig url="V1/Students/login" httpMethod="post">
    <TestData name="data1" desc="更改手机号登录">
        <Before>
            <Function clsName="test.java.LogicHandler" methodName="changeStudentsMobile"/>
        </Before>
        <Param name="username" value="#{sql1=.mobile}">
            <Sql name="sql">select trim(mobile) as mobile,password from ebk_students where id=123456;
            </Sql>
        </Param>
        <Param name="password" value="#{sql.password}"></Param>
        <ExpectResult>
            <Pair>errorCode:200</Pair>
            <Pair>errorMsg:登录成功</Pair>
        </ExpectResult>
        <After>
            <Function clsName="test.java.LogicHandler" methodName="resertStudentMobile"/>
        </After>
    </TestData>
<DataConfig>
```

##### &nbsp;&nbsp; 7.Before and After also can use Sql annotations
```xml
<DataConfig url="V1/Students/login" httpMethod="post">
    <TestData name="data1" desc="更改手机号登录">
        <Before>
            <Sql>update ebk_students set mobile=18078788787 where id=123456;</Sql>
        </Before>
        <Param name="username" value="#{sql1=.mobile}">
            <Sql name="sql">select trim(mobile) as mobile,password from ebk_students where id=123456;
            </Sql>
        </Param>
        <Param name="password" value="#{sql.password}"></Param>
        <ExpectResult>
            <Pair>errorCode:200</Pair>
            <Pair>errorMsg:登录成功</Pair>
        </ExpectResult>
        <After>
            <Sql>update ebk_students set mobile5098986767 where id=123456;</Sql>
        </After>
    </TestData>
<DataConfig>
```
### Contributors
   Charlie <br/>
   Niki    [https://github.com/ZhangyuBaolu](https://github.com/ZhangyuBaolu)<br/>
   Wind    [https://github.com/lwfwind](https://github.com/lwfwind)<br/>