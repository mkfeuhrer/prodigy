package com.gcc.common;

@FunctionalInterface
public interface Callable<T> {
    public void call(T arg);
}
