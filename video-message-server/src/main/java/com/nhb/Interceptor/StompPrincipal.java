package com.nhb.Interceptor;

import java.security.Principal;

import java.io.Serializable;


public class StompPrincipal implements Principal, Serializable {
    private static final long serialVersionUID = 1L; // 建议显式声明

    private final String name; // 用户ID，如 "5"

    public StompPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    // 可选：重写 equals/hashCode（推荐）
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StompPrincipal that = (StompPrincipal) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}