package com.cesar.mobilehealthappandroid.sdk.listeners;

public interface RealtimeListener {
  void onNotify(int battery, int steps, int distance, int calories);
}
