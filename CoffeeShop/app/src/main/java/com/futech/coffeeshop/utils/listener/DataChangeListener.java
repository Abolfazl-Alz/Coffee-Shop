package com.futech.coffeeshop.utils.listener;

public interface DataChangeListener {
    void onChange();

    void onError(String msg);
}
