package com.github.zgynhqf.rafy4j.utils.support;


import java.io.Serializable;

/**
 * 支持序列化的 Function
 *
 * @author miemie
 * @since 2018-05-12
 */
@FunctionalInterface
public interface SFunction<T, R> extends Serializable {

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     */
    R apply(T t);
}
