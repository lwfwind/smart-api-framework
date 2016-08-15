package com.qa.framework.core;

import com.qa.framework.bean.DataConfig;
import com.qa.framework.bean.TestData;
import com.qa.framework.exception.TestDataDescDuplicatedException;
import com.qa.framework.exception.TestDataNameDuplicatedException;
import com.qa.framework.library.base.IOHelper;
import com.qa.framework.library.base.XMLHelper;
import com.qa.framework.library.reflect.ReflectHelper;
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
    private DataConfig dataConfig;
    private String fileName;

    /**
     * Instantiates a new Data convertor.
     *
     * @param filePath the file path
     */
    public DataConvertor(String filePath) {
        this.fileName = IOHelper.getName(filePath);
        logger.info("convert data from xml-" + this.fileName);
        XMLHelper xmlHelper = new XMLHelper();
        Document document = xmlHelper.readXMLFile(filePath);
        dataConfig = new DataConfig();
        List attributes = document.getRootElement().attributes();
        if (attributes.size() != 0) {
            for (Object attribute : attributes) {
                Attribute attr = (Attribute) attribute;
                String attributeName = attr.getName();      //element对应对象的属性值
                String attributeValue = attr.getStringValue();
                ReflectHelper.setMethod(dataConfig, attributeName, attributeValue, String.class);
            }
        }
        List elements = document.getRootElement().elements();
        for (Object element : elements) {
            convert(dataConfig, (Element) element);
        }
        List<String> testDataNameList = new ArrayList<String>();
        List<String> testDataDescList = new ArrayList<String>();
        for (TestData testData : dataConfig.getTestDataList()) {
            if (!testDataNameList.contains(testData.getName())) {
                testDataNameList.add(testData.getName());
            } else {
                throw new TestDataNameDuplicatedException(this.fileName, testData.getName());
            }
            if (!testDataDescList.contains(testData.getDesc())) {
                testDataDescList.add(testData.getDesc());
            } else {
                throw new TestDataDescDuplicatedException(this.fileName, testData.getDesc());
            }
        }
    }


    /**
     * Gets data config.
     *
     * @return the data config
     */
    public DataConfig getDataConfig() {
        return dataConfig;
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
        } else if ("Pair".equalsIgnoreCase(element.getName())) {
            className = verifyPackage + "." + "PairExpectResult";
        } else {
            className = beanPackage + "." + element.getName();  //组成className的完整路径
        }
        try {
            Class clazz = Class.forName(className);          //获取className的class对象
            Object elementObj = clazz.newInstance();         //实例化
            List attributes = element.attributes();          //获取所有属性值, 如果含有属性, 将相应的属性设置到对应的实例中
            //添加子对象
            if (IExpectResult.class.isAssignableFrom(clazz)) {
                ReflectHelper.addMethod(parentObj, elementObj, "ExpectResultImp", IExpectResult.class);
            } else {
                ReflectHelper.addMethod(parentObj, elementObj, element.getName(), elementObj.getClass());
            }
            //设置xml的文件名到TestData类里
            if (element.getName().equalsIgnoreCase("TestData")) {
                ReflectHelper.setMethod(elementObj, "currentFileName", this.fileName, String.class);
            }
            //设置属性
            if (attributes.size() != 0) {
                for (Object attribute : attributes) {
                    Attribute attr = (Attribute) attribute;
                    String attributeName = attr.getName();     //element对应对象的属性值
                    String attributeValue = attr.getStringValue();
                    if ("Pair".equalsIgnoreCase(element.getName())){
                        String PairClsaa=beanPackage + "." +"Pair";;
                        Class Pairclazz = Class.forName(PairClsaa);          //获取className的class对象
                        Object PairObj = Pairclazz.newInstance();
                        ReflectHelper.setMethod(PairObj, attributeName, attributeValue, String.class);
                    }else {
                        ReflectHelper.setMethod(elementObj, attributeName, attributeValue, String.class);
                    }

                }
            }
            //处理sql与ExpectResult
            String nodeText = element.getText().trim();
            String[] texts = nodeText.split("\\n");            //getText()的形式可能为"\n  \n   \n",所以先根据"\\n"划分
            String sql = "";
            for (String text : texts) {
                if (!"".equalsIgnoreCase(text.trim())) {
                    Field[] fields = clazz.getDeclaredFields();
                    Field statementField = null;
                    boolean flag = false;                     //查看该类中有没有待statement字段的属性, 如果有, 则赋值
                    for (Field f : fields) {
                        if (f.getName().contains("Statement")) {
                            statementField = f;
                            if ((beanPackage + ".Sql").equals(className)) {
                                if (texts[texts.length - 1].equals(text)) {
                                    text = sql + " " + text;
                                    flag = true;
                                    break;
                                } else {
                                    sql = sql + " " + text;
                                }
                            } else {
                                flag = true;
                                break;
                            }
                        }
                    }
                    if (flag) {
                        ReflectHelper.setMethod(elementObj, statementField.getName(), text, String.class);
                    }
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
