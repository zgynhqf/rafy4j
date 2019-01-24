package com.github.zgynhqf.rafy4j.utils;

/**
 * @author: huqingfang
 * @date: 2019-01-23 11:54
 **/

import com.github.zgynhqf.rafy4j.utils.support.SFunction;
import com.github.zgynhqf.rafy4j.utils.support.SerializedLambda;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * copy from mybatis plus.
 *
 * <p>
 * Lambda 解析工具类
 * </p>
 *
 * @author HCL
 * @since 2018-05-10
 */
public final class LambdaUtils {
    /**
     * SerializedLambda 反序列化缓存
     */
    private static final Map<Class, WeakReference<SerializedLambda>> FUNC_CACHE = new ConcurrentHashMap<>();

    /**
     * <p>
     * 解析 lambda 表达式
     * </p>
     *
     * @param func 需要解析的 lambda 对象
     * @param <T>  类型，被调用的 Function 对象的目标类型
     * @return 返回解析后的结果
     */
    public static <T> SerializedLambda resolve(SFunction<T, ?> func) {
        Class clazz = func.getClass();
        return Optional.ofNullable(FUNC_CACHE.get(clazz))
                .map(WeakReference::get)
                .orElseGet(() -> {
                    SerializedLambda lambda = SerializedLambda.resolve(func);
                    FUNC_CACHE.put(clazz, new WeakReference<>(lambda));
                    return lambda;
                });
    }

    /**
     * 字符串 is
     */
    public static final String IS = "is";

    public static String resolveFieldName(String getterName) {
        if (getterName.startsWith("get")) {
            getterName = getterName.substring(3);
        } else if (getterName.startsWith(IS)) {
            getterName = getterName.substring(2);
        }
        // 小写第一个字母
        return NameUtils.firstToLowerCase(getterName);
    }

    public static <T> String resolveFieldName(SFunction<T, ?> func) {
        SerializedLambda lambda = resolve(func);
        return resolveFieldName(lambda.getImplMethodName());
    }
}
