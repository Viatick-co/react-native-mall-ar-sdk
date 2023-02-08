package com.jarvismallar;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.views.image.ReactImageView;

public class MainArViewManager extends SimpleViewManager<MainArView> implements LifecycleEventListener {

  public static final String REACT_CLASS = "ARView";
  ReactApplicationContext mCallerContext;

  private MainArView arview;

  public MainArViewManager(ReactApplicationContext reactContext) {
    mCallerContext = reactContext;
  }

  @Override
  public String getName() {
    return REACT_CLASS;
  }

  @NonNull
  @Override
  protected MainArView createViewInstance(@NonNull ThemedReactContext context) {
//    return new ReactImageView(context, Fresco.newDraweeControllerBuilder(), null, this.mCallerContext);
    context.addLifecycleEventListener(this);

    this.arview = new MainArView(context, context.getCurrentActivity());
    return this.arview;
  }

  @Override
  public void onHostResume() {
    Log.d("JarvisMallArModule", "onHostResume");

    if (this.arview != null) {
      this.arview.onResume();
    }
  }

  @ReactProp(name = "sdkKey")
  public void setSdkKey(MainArView view, @Nullable String sdkKey) {
    Log.d("JarvisMallArModule", "sdkkey: " + sdkKey);
    if (this.arview != null) {
      this.arview.setSdkKey(sdkKey);
    }
  }

  @Override
  public void onHostPause() {
    Log.d("JarvisMallArModule", "onHostPause");

    if (this.arview != null) {
      this.arview.onPause();
    }
  }

  @Override
  public void onHostDestroy() {
    Log.d("JarvisMallArModule", "onHostDestroy");

    if (this.arview != null) {
      this.arview.onDestroy();
    }
  }
}
