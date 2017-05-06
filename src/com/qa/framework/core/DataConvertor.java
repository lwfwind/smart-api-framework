package com.qa.framework.core;

import com.library.common.IOHelper;
import com.library.common.ReflectHelper;
import com.library.common.XmlHelper;
import com.qa.framework.bean.TestCase;
import com.qa.framework.bean.TestSuite;
import com.qa.framework.exception.TestCaseDescDuplicatedException;
import com.qa.framework.exception.TestCaseNameDuplicatedException;
import com.qa.framework.verify.IExpectResult;
import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 将xml中的数据转化成对应的bean类
 */
public class DataConvertor {
    private static final Logger logger = Logger.getLogger(DataConvertor.class);
    private TestSuite testSuite;
    private String fileName;

    /**
     * Instantiates a new Data convertor.
     *
     * @param filePath the file path
     */
    public DataConvertor(String filePath) {
        this.fileName = IOHelper.getName(filePath);
        logger.info("convert data from xml-" + this.fileName);
        XmlHelper xmlHelper = new XmlHelper();
        Document document = xmlHelper.readXMLFile(filePath);
        testSuite = new TestSuite();
        List attributes = document.getRootElement().attributes();
        if (attributes.size() != 0) {
            for (Object attribute : attributes) {
                Attribute attr = (Attribute) attribute;
                String attributeName = attr.getName();      //element对应对象的属性值
                String attributeValue = attr.getStringValue();
                ReflectHelper.setMethod(testSuite, attributeName, attributeValue, String.class);
            }
        }
        List elements = document.getRootElement().elements();
        for (Object element : elements) {
            convert(testSuite, (Element) element);
        }
        List<String> testDataNameList = new ArrayList<String>();
        List<String> testDataDescList = new ArrayList<String>();
        for (TestCase testCase : testSuite.getTestCaseList()) {
            if (!testDataNameList.contains(testCase.getName())) {
                testDataNameList.add(testCase.getName());
            } else {
                logger.info("TestDate 的名字重复" + this.fileName + ":" + testCase.getName());
                throw new TestCaseNameDuplicatedException(this.fileName, testCase.getName());
            }
            if (!testDataDescList.contains(testCase.getDesc())) {
                testDataDescList.add(testCase.getDesc());
            } else {
                logger.info("TestDate 的描述重复" + this.fileName);
                throw new TestCaseDescDuplicatedException(this.fileName, testCase.getDesc());
            }
        }
    }


    /**
     * Gets data config.
     *
     * @return the data config
     */
    public TestSuite getTestSuite() {
        return testSuite;
    }

    /**
     * Convertor. 根据element名字生成相应的bean
     *
     * @param parentObj the parentObj 该element父类对应的对象
     * @param element   the element
     */
    public void convert(Object parentObj, Element element) {
        String className = null;
        String beanPackage = "com.qa.framework.bean";
        String verifyPackage = "com.qa.framework.verify";
        if ("Contain".equalsIgnoreCase(element.getName())) {
            className = verifyPackage + "." + "ContainExpectResult";
        } else if ("Pair".equalsIgnoreCase(element.getName()) && element.getParent().getName().equals("ExpectResults")) {
            className = verifyPackage + "." + "PairExpectResult";
        } else if ("AssertTrue".equalsIgnoreCase(element.getName())) {
            className = verifyPackage + "." + "AssertTrueExpectResult";
        } else {
            className = beanPackage + "." + element.getName();  //组成className的完整路径
        }
        try {
            Class clazz = Class.forName(className);          //获取className的class对象
            Object elementObj = clazz.newInstance();         //实例化
            List attributes = element.attributes();          //获取所有属性值, 如果含有属性, 将相应的属性设置到对应的实例中
            //添加子对象
            if (IExpectResult.class.isAssignableFrom(clazz)) {
                ReflectHelper.addMethod(parentObj, elementObj, "ExpectResult", IExpectResult.class);
            } else {
                ReflectHelper.addMethod(parentObj, elementObj, element.getName(), elementObj.getClass());
            }
            //设置xml的文件名到TestData类里
            if (element.getName().equalsIgnoreCase("TestCase")) {
                ReflectHelper.setMethod(elementObj, "currentFileName", this.fileName);
            }
            //设置属性
            if (attributes.size() != 0) {
                for (Object attribute : attributes) {
                    Attribute attr = (Attribute) attribute;
                    String attributeName = attr.getName();     //element对应对象的属性值
                    String attributeValue = attr.getStringValue();
                    ReflectHelper.setMethod(elementObj, attributeName, attributeValue);
                }
            }
            //处理sql与ExpectResult
            String nodeText = element.getText().trim().replaceAll("\\n", " ");
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                //查看该类中有没有待statement字段的属性, 如果有, 则赋值
                if (field.getName().contains("Statement")) {
                    ReflectHelper.setMethod(elementObj, field.getName(), nodeText, String.class);
                }
            }
            //递归
            List subElements = element.elements();
            if (subElements.size() != 0) {
                for (Object subElement : subElements) {
                    convert(elementObj, (Element) subElement);
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
