package com.project.uml_project.ingeProjet.utils;

public class Result<T> {

    private T data;
    private String error;

    private Result(T data) {
        this.data = data;
        this.error = null;
    }

    private Result(String error) {
        this.error = error;
    }

    public T value() {
        return this.data;
    }

    public String message() {
        return this.error;
    }

    public boolean succeeded() {
        return this.error == null;
    }

    public boolean failed() {
        return this.error != null;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(data);
    }

    public static <T> Result<T> failure(String message) {
        return new Result<>(message);
    }

}
