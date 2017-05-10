package com.qa.framework.core;

import com.library.common.IOHelper;
import com.library.common.ReflectHelper;
import com.library.common.XmlHelper;
import com.qa.framework.InstanceFactory;
import com.qa.framework.bean.Function;
import com.qa.framework.bean.Global;
import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 将xml中的数据转化成对应的bean类
 */
public class GlobalConvertor {
    private static final Logger logger = Logger.getLogger(GlobalConvertor.class);

    public GlobalConvertor() {
        List<String> files = IOHelper.listFilesInDirectoryRecursive(System.getProperty("user.dir"), "global.xml");
        if (files.size() > 0) {
            String filePath = files.get(0);
            logger.info("convert data from xml-" + filePath);
            XmlHelper xmlHelper = new XmlHelper();
            Document document = xmlHelper.readXMLFile(filePath);
            Global global = InstanceFactory.getGlobal();
            List elements = document.getRootElement().elements();
            for (Object element : elements) {
                convert(global, (Element) element);
            }
            if(InstanceFactory.getGlobal().getBefore() != null && InstanceFactory.getGlobal().getBefore().getFunctions() != null) {
                for (Function function : InstanceFactory.getGlobal().getBefore().getFunctions()) {
                    ParamValueProcessor.executeFunction(function);
                }
            }
        }
    }


    /**
     * Convertor. 根据element名字生成相应的bean
     *
     * @param parentObj the parentObj 该element父类对应的对象
     * @param element   the element
     */
    private void convert(Object parentObj, Element element) {
        String className = null;
        String beanPackage = "com.qa.framework.bean";
        className = beanPackage + "." + element.getName();
        try {
            Class clazz = Class.forName(className);          //获取className的class对象
            Object elementObj = clazz.newInstance();         //实例化
            List attributes = element.attributes();          //获取所有属性值, 如果含有属性, 将相应的属性设置到对应的实例中
            //添加子对象
            ReflectHelper.addMethod(parentObj, elementObj, element.getName(), elementObj.getClass());
            //设置属性
            if (attributes.size() != 0) {
                for (Object attribute : attributes) {
                    Attribute attr = (Attribute) attribute;
                    String attributeName = attr.getName();     //element对应对象的属性值
                    String attributeValue = attr.getStringValue();
                    ReflectHelper.setMethod(elementObj, attributeName, attributeValue);
                }
            }
            //处理sql
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
