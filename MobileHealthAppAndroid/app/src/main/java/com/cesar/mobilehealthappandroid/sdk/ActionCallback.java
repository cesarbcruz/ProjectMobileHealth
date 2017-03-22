package com.cesar.mobilehealthappandroid.sdk;

public interface ActionCallback {
    public void onSuccess(Object data);

    public void onFail(int errorCode, String msg);
}
