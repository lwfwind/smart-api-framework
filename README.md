一个依靠testng的测试框架，通过配置测试用例的测试数据，实现数据驱动模式
xml配置结构
DataConfig
	WebPath	-- 配置接口路径
	TestData
		Setup -- 调用其他接口
			WebPath	-- 其他接口的路径
			Param	-- 配置其他接口参数
		Param -- 配置接口参数
		ExpectResult -- 期望返回结果
			Contain	适用返回结果包含哪些字段
			Pair	适用返回结果包含key-value配对