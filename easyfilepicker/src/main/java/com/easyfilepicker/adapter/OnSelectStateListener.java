package com.easyfilepicker.adapter;

public interface OnSelectStateListener<T> {
    void OnSelectStateChanged(boolean state, T file);
}
