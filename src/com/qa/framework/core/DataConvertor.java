package com.qa.framework.core;

import com.qa.framework.bean.DataConfig;
import com.qa.framework.library.base.XMLHelper;
import com.qa.framework.library.reflect.ReflectHelper;
import com.qa.framework.verify.IExpectResult;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 将xml中的数据转化成对应的bean类
 */
public class DataConvertor {

    private DataConfig dataConfig;

    public DataConvertor(String filePath) {
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
            convertor(dataConfig, (Element) element);
        }
    }


    public DataConfig getDataConfig() {
        return dataConfig;
    }

    /**
     * Convertor. 根据element名字生成相应的bean
     *
     * @param obj     the obj 该element父类对应的对象
     * @param element the element
     */

    public void convertor(Object obj, Element element) {
        String className = null;
        String MothodName = null;
        String beanPackage = "com.qa.bean";
        String expectPackage = "com.qa.verify";
        if ("Contain".equalsIgnoreCase(element.getName())) {
            className = expectPackage + "." + "ContainExpectResult";
        } else if ("Pair".equalsIgnoreCase(element.getName())) {
            className = expectPackage + "." + "PairExpectResult";
        } else {
            className = beanPackage + "." + element.getName();  //组成className的完整路径
        }
        try {
            Class clazz = Class.forName(className);          //获取className的class对象
            Object elementObj = clazz.newInstance();             //实例化
            List attributes = element.attributes();          //获取所有属性值, 如果含有属性, 将相应的属性设置到对应的实例中
            if (IExpectResult.class.isAssignableFrom(clazz)) {
                ReflectHelper.addMethod(obj, elementObj, "ExpectResultImp", IExpectResult.class);
            } else {
                ReflectHelper.addMethod(obj, elementObj, element.getName(), elementObj.getClass());
            }

            if (attributes.size() != 0) {
                for (Object attribute : attributes) {
                    Attribute attr = (Attribute) attribute;
                    String attributeName = attr.getName();      //element对应对象的属性值
                    String attributeValue = attr.getStringValue();
                    ReflectHelper.setMethod(elementObj, attributeName, attributeValue, String.class);
                }
            }
            String nodeText = element.getText().trim();
            String[] texts = nodeText.split("\\n");//getText()的形式可能为"\n  \n   \n",所以先根据"\\n"划分
            String sql = "";
            for (String text : texts) {
                if (!"".equalsIgnoreCase(text.trim())) {         //如果text不为""的话, 遍历element对应的class, 寻找包含字段statement的属性, 再调用set方法
                    Field[] fields = clazz.getDeclaredFields();
                    Field statementField = null;
                    boolean flag = false;                                   //查看该类中有没有待statement字段的属性, 如果有, 则赋值
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
            List subElements = element.elements();  //如果subElements不为0, 说明object包含有其他的对象
            if (subElements.size() != 0) {
                for (Object subElement : subElements) {
                    convertor(elementObj, (Element) subElement);
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
