package com.gcc.common;

public class Arguments {
    @SafeVarargs
    public static <T> T [] args(T ...args) {
        return args;
    }
}
