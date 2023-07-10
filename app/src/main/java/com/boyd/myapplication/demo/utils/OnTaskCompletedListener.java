package com.boyd.myapplication.demo.utils;

public interface OnTaskCompletedListener<C> {
    void onSuccess(C result);
    void onError(String errorMessage);
}
