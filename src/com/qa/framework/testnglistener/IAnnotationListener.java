package com.qa.framework.testnglistener;

import org.testng.IAnnotationTransformer2;
import org.testng.annotations.IConfigurationAnnotation;
import org.testng.annotations.IDataProviderAnnotation;
import org.testng.annotations.IFactoryAnnotation;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;


public class IAnnotationListener implements IAnnotationTransformer2 {

    public void transform(ITestAnnotation annotation, Class testClass,
                          Constructor testConstructor, Method testMethod) {
    }

    @Override
    public void transform(IConfigurationAnnotation annotation, Class aClass, Constructor constructor, Method method) {

    }

    /**
     * Transform an IDataProvider annotation.
     *
     * @param method The method annotated with the IDataProvider annotation.
     */
    public void transform(IDataProviderAnnotation annotation, Method method) {
    }

    /**
     * Transform an IFactory annotation.
     *
     * @param method The method annotated with the IFactory annotation.
     */
    public void transform(IFactoryAnnotation annotation, Method method) {
    }
}
