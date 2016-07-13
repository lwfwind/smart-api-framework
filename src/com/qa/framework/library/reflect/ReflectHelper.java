package com.qa.framework.library.reflect;

import com.qa.framework.library.base.StringHelper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by apple on 15/11/18.
 */
public class ReflectHelper {
    @SuppressWarnings("unchecked")
    public static void setMethod(Object obj, String fieldName, Object value, Class fieldClass) {
        Class cls = obj.getClass();
        try {
            Method method = cls.getDeclaredMethod("set" + StringHelper.capitalFirstLetter(fieldName), fieldClass);
            method.invoke(obj, value);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static void setMethod(Class cls, String fieldName, Object value, Class fieldClass) {
        try {
            Method method = cls.getDeclaredMethod("set" + StringHelper.capitalFirstLetter(fieldName), fieldClass);
            method.invoke(cls, value);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static void addMethod(Object parent, Object sub, String fieldName, Class subClass) {
        Class cls = parent.getClass();
        try {
            Method method = cls.getDeclaredMethod("add" + fieldName, subClass);
            method.invoke(parent, sub);
        } catch (NoSuchMethodException e) {
            Method method = null;
            try {
                method = cls.getDeclaredMethod("set" + fieldName, subClass);
                method.invoke(parent, sub);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
                ex.printStackTrace();
            }

        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static Object getMethod(Object obj, String field) {
        Class cls = obj.getClass();
        try {
            Method getMethod = cls.getDeclaredMethod("get" + StringHelper.capitalFirstLetter(field));
            return getMethod.invoke(obj);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static Method getMethod(Class<?> clazz, String name) {
        while (clazz != Object.class) {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals(name)) {
                    return method;
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != Object.class) {
            Collections.addAll(fields, clazz.getDeclaredFields());
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    public static String getObjectInfo(Object obj) {
        StringBuilder stringBuilder = new StringBuilder();
        Class<?> clazz = obj.getClass();
        List<Field> fields = getAllFields(clazz);
        Method method = getMethod(clazz, "toString");
        if (method != null) {
            try {
                return method.invoke(obj).toString();
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            for (Field field : fields) {
                try {
                    boolean accessFlag = field.isAccessible();
                    field.setAccessible(true);
                    String varName = field.getName();
                    Object varValue = field.get(obj);
                    stringBuilder.append(varName);
                    stringBuilder.append("=");
                    stringBuilder.append(varValue);
                    stringBuilder.append(", ");
                    field.setAccessible(accessFlag);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }

        return stringBuilder.toString();
    }

    public static String getCollectionsInfo(List<?> list) {
        if (list.size() == 0) {
            return "null";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (Object obj : list) {
            if (obj.getClass().isPrimitive()) {
                stringBuilder.append(obj.toString());
                stringBuilder.append(", ");
            } else {
                stringBuilder.append(getObjectInfo(obj));
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }
}
