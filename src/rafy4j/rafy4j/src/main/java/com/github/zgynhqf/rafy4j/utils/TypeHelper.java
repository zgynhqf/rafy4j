package com.github.zgynhqf.rafy4j.utils;

import com.sun.javafx.scene.control.behavior.OptionalBoolean;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

/**
 * 反射辅助类型。
 * @author: huqingfang
 * @date: 2018-12-26 01:40
 **/
public class TypeHelper {
    /**
     * 判断指定的类型是否为原生类型或其封装体。
     *
     * @param clazz
     * @return
     */
    public static boolean isPrimitiveTypeOrClass(Class<?> clazz) {
        return getPrimitiveType(clazz) != null;
    }

    /**
     * 判断指定的类型是否为几个原生类型或其对应的封装类型。
     *
     * @param clazz
     * @param type
     * @return
     */
    public static boolean isPrimitiveTypeOrClass(Class<?> clazz, PrimitiveType type) {
        return isPrimitiveType(clazz, type) ||
                isPrimitiveWarpClass(clazz, type);
    }

    /**
     * 判断指定的类型是否为几个原生类型对应的封装类型。如 Integer、Double
     *
     * @param clazz
     * @param type
     * @return
     */
    public static boolean isPrimitiveWarpClass(Class<?> clazz, PrimitiveType type) {
        return isPrimitiveTypeOrClassCore(clazz, type, false);
    }

    /**
     * 判断指定的类型是否为几个原生类型。如 int、double
     *
     * @param clazz
     * @param type
     * @return
     */
    public static boolean isPrimitiveType(Class<?> clazz, PrimitiveType type) {
        return isPrimitiveTypeOrClassCore(clazz, type, true);
    }

    /**
     *
     * @param clazz
     * @param type
     * @param checkPrimitive 是检查原生类型，还是检查引用类型。
     * @return
     */
    private static boolean isPrimitiveTypeOrClassCore(Class<?> clazz, PrimitiveType type, boolean checkPrimitive) {
        switch (type) {
            case BOOLEAN:
                return checkPrimitive ? clazz == boolean.class : clazz == Boolean.class;
            case CHAR:
                return checkPrimitive ? clazz == char.class : clazz == Character.class;
            case BYTE:
                return checkPrimitive ? clazz == byte.class : clazz == Byte.class;
            case SHORT:
                return checkPrimitive ? clazz == short.class : clazz == Short.class;
            case INTEGER:
                return checkPrimitive ? clazz == int.class : clazz == Integer.class;
            case LONG:
                return checkPrimitive ? clazz == long.class : clazz == Long.class;
            case FLOAT:
                return checkPrimitive ? clazz == float.class : clazz == Float.class;
            case DOUBLE:
                return checkPrimitive ? clazz == double.class : clazz == Double.class;
            case VOID:
                return checkPrimitive ? clazz == void.class : clazz == Void.class;
        }
        return false;
    }

    /**
     * 获取继承层次列表，从子类到基类
     * @param from
     * @param exceptTypes
     * @return
     */
    public static Iterable<Class<?>> getHierarchy(Class<?> from, Class<?>... exceptTypes) {
        boolean needExcept = exceptTypes.length > 0;

        List<Class<?>> res = new ArrayList<>();

        Class<?> current = from;
        while (current != null && (!needExcept || !inExcept(current, exceptTypes))) {
            res.add(current);
            current = current.getSuperclass();
        }

        return res;
    }

    private static boolean inExcept(Class<?> current, Class<?>[] exceptTypes) {
        for (int i = 0, c = exceptTypes.length; i < c; i++) {
            Class<?> exceptType = exceptTypes[i];

            //如果是泛型定义，则需要 current 类型是这个泛型的实例也可以。
//            if (exceptType.getTypeParameters().length > 0)
//            {
//                if (current.IsGenericType && current.GetGenericTypeDefinition() == exceptType) return true;
//            }
//            else
//            {
            if (exceptType == current) return true;
//            }
        }

        return false;
    }

    /**
     * 如果是 Optional 泛型类型，则返回内部的真实类型。
     * @param targetType
     * @return
     */
    public static Class<?> ignoreOptional(Class<?> targetType) {
        if (targetType == OptionalBoolean.class) return boolean.class;
        if (targetType == OptionalInt.class) return int.class;
        if (targetType == OptionalDouble.class) return double.class;
        if (targetType == OptionalLong.class) return long.class;
        if (targetType == Optional.class) return Object.class;
        return targetType;
    }

    /**
     * 判断某个类型是否为 Nullable 泛型类型。
     * @param targetType 需要判断的目标类型。
     * @return
     */
    public static boolean isOptional(Class<?> targetType) {
        return targetType == Optional.class ||
                targetType == OptionalBoolean.class ||
                targetType == OptionalInt.class ||
                targetType == OptionalDouble.class ||
                targetType == OptionalLong.class;
    }

    /**
     * 转换为指定的类型。
     *
     * @param desiredType
     * @param value
     * @param <T>
     * @return
     */
    public static <T> T coerceValue(Class<?> desiredType, Object value) {
        if (value == null) return (T) getDefaultValue(desiredType);

        return (T) coerceValueCore(desiredType, value);
    }

    private static Object coerceValueCore(Class<?> desiredType, Object value) {
        Class<?> valueClass = value.getClass();

        //类型匹配时，直接返回值。
        if (desiredType.isAssignableFrom(valueClass)) return value;

        //字符串类型，直接使用 ToString 进行转换。
        if (desiredType == String.class) return value.toString();

        //处理 Optional 类型。
        if (isOptional(desiredType) && desiredType != Optional.class) {
            desiredType = ignoreOptional(desiredType);
        }

        //处理枚举类型
        if (desiredType.isEnum()) {
            return parseEnum(desiredType, value.toString());
        }
        if (valueClass.isEnum()) {
            if (isPrimitiveTypeOrClass(desiredType, PrimitiveType.INTEGER) ||
                    isPrimitiveTypeOrClass(desiredType, PrimitiveType.LONG)
            ) {
                Enum enumValue = (Enum) value;
                return enumValue.ordinal();
            }
        }

        //处理数字类型。（空字符串转换为数字 0）
        boolean isEmptyString = value instanceof String && StringUtils.isBlank((String) value);
        if (isEmptyString && isPrimitiveTypeOrClass(desiredType)) return getDefaultValue(desiredType);

        //处理 UUID
        if (desiredType == UUID.class && value instanceof String && !isEmptyString) {
            return UUID.fromString((String) value);
        }

        //处理时间
        if (desiredType == Date.class && valueClass == LocalDateTime.class) {
            return TimeHelper.toDate((LocalDateTime) value);
        }
        if (desiredType == LocalDateTime.class && valueClass == Date.class) {
            return TimeHelper.toLocalDateTime((Date) value);
        }

//            if (value instanceof JValue) {
//                value = ((JValue) value).getValue();
//            }

        //默认转换：
        //没找到别的类型，暂时从 spring 框架中找了一个。
        TypeConverter converter = new SimpleTypeConverter();
        return converter.convertIfNecessary(value, desiredType);
    }

    /**
     * 获取指定类型的默认值。
     *
     * @param targetType Type of the target.
     * @return
     */
    public static Object getDefaultValue(Class targetType) {
        PrimitiveType primitiveType = getPrimitiveType(targetType);
        if (primitiveType != null) {
            switch (primitiveType) {
                case BOOLEAN:
                    return Boolean.FALSE;
                case CHAR:
                    return Character.MIN_VALUE;
                case BYTE:
                    return Byte.valueOf((byte) 0);
                case SHORT:
                    return Short.valueOf((short) 0);
                case INTEGER:
                    return 0;
                case LONG:
                    return 0L;
                case FLOAT:
                    return 0f;
                case DOUBLE:
                    return 0d;
                case VOID:
                    break;
            }
        }
        return null;
    }

    /**
     * 获取指定类型对应的原生类型枚举。
     * Integer、int 都会返回 PrimitiveType.Integer
     * 如果未找到，则返回 null
     *
     * @param targetType
     * @return
     */
    public static PrimitiveType getPrimitiveType(Class targetType) {
        PrimitiveType[] values = PrimitiveType.values();
        for (int i = 0; i < values.length; i++) {
            PrimitiveType value = values[i];
            if (isPrimitiveTypeOrClass(targetType, value)) return value;
        }
        return null;
    }

    public static Enum parseEnum(Class<?> enumType, String name) {
        Object[] enumConstants = enumType.getEnumConstants();
        for (int i = 0; i < enumConstants.length; i++) {
            Object enumConstant = enumConstants[i];
            if (enumConstant.toString().equalsIgnoreCase(name)) return (Enum) enumConstant;
        }
        return null;
    }

    public static <T> T as(Object value, Class<T> clazz) {
        if (clazz.isInstance(value)) return (T) value;
        return null;
    }

    /**
     * 获取指定类型下所有的某类成员，包括该类的父类中声明的。
     *
     * @param type
     * @param memberGetter
     * @param <T>
     * @return
     */
    public static <T> List<T> getMembers(Class<?> type, Function<Class<?>, T[]> memberGetter) {
        List<T> res = new ArrayList<>();
        getMembers(type, memberGetter, res);
        return res;
    }

    private static <T> void getMembers(Class<?> type, Function<Class<?>, T[]> memberGetter, List<T> res) {
        T[] parentFields = memberGetter.apply(type);
        for (int i = 0; i < parentFields.length; i++) {
            T member = parentFields[i];
            res.add(member);
        }

        Class<?> superclass = type.getSuperclass();
        if (superclass != null) {
            getMembers(superclass, memberGetter, res);
        }
    }
}
