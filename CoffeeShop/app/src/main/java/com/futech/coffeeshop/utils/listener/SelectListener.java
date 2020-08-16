package com.futech.coffeeshop.utils.listener;

public interface SelectListener<T> {

    void onSelect(T data, boolean isOnline);

    void onError(String msg);

}
