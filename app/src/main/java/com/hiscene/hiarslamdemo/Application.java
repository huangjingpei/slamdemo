package com.hiscene.hiarslamdemo;

import org.mediasoup.droid.Logger;
import org.mediasoup.droid.MediasoupClient;
import org.webrtc.ContextUtils;

public class Application extends android.app.Application {

  @Override
  public void onCreate() {
    super.onCreate();

    Logger.setLogLevel(Logger.LogLevel.LOG_DEBUG);
    Logger.setDefaultHandler();
    MediasoupClient.initialize(getApplicationContext());
    ContextUtils.initialize(getApplicationContext());
  }
}
