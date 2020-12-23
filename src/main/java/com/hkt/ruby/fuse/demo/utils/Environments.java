package com.hkt.ruby.fuse.demo.utils;

public enum Environments {
    DEV("dev", "Development Environment"),
    UAT("uat", "UAT Environment");

    final String env;
    final String message;

    public String getEnv() {
        return env;
    }

    public String getMessage() {
        return message;
    }

    Environments(String env, String message) {
        this.env = env;
        this.message = message;
    }
}
