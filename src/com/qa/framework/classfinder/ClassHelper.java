package com.qa.framework.classfinder;

import com.qa.framework.classfinder.annotation.Impl;
import com.qa.framework.library.base.CollectionHelper;

import java.util.List;

public class ClassHelper {

    static {
    }

    /**
     * 查找实现类
     *
     * @param interfaceClass the interface class
     * @return the class
     */
    public static Class<?> findImplementClass(Class<?> interfaceClass) {
        Class<?> implementClass = null;
        // 判断接口上是否标注了 Impl 注解
        if (interfaceClass.isAnnotationPresent(Impl.class)) {
            // 获取强制指定的实现类
            implementClass = interfaceClass.getAnnotation(Impl.class).value();
        } else {
            // 获取该接口所有的实现类
            List<Class<?>> implementClassList = ClassFinder.getClassListBySuper(interfaceClass);
            if (CollectionHelper.isNotEmpty(implementClassList)) {
                // 获取第一个实现类
                implementClass = implementClassList.get(0);
            }
        }
        // 返回实现类对象
        return implementClass;
    }
}
