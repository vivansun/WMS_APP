package com.plusone.pwms.model;

import android.app.Activity;
import android.app.Application;

import java.util.HashMap;
import java.util.Map;

public class MyApplication extends Application {
  private UserAndWarehouse userAndWarehouse;

  private ClientWarehouse clientWarehouse;

  private Map<String, Activity> activityMap = new HashMap<>();

  public UserAndWarehouse getUserAndWarehouse() {
    return userAndWarehouse;
  }

  public void setUserAndWarehouse(UserAndWarehouse userAndWarehouse) {
    this.userAndWarehouse = userAndWarehouse;
  }

  public ClientWarehouse getClientWarehouse() {
    return clientWarehouse;
  }

  public void setClientWarehouse(ClientWarehouse clientWarehouse) {
    this.clientWarehouse = clientWarehouse;
  }

  public Map<String, Activity> getActivityMap() {
    return activityMap;
  }

  public void setActivityMap(Map<String, Activity> activityMap) {
    this.activityMap = activityMap;
  }
}
