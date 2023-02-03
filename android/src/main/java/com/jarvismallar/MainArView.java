package com.jarvismallar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.indooratlas.android.sdk.IAARObject;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IAWayfindingRequest;
import com.jarvismallar.helpers.DisplayRotationHelper;
import com.jarvismallar.helpers.FullScreenHelper;
import com.jarvismallar.helpers.SnackbarHelper;
import com.jarvismallar.helpers.TrackingStateHelper;
import com.jarvismallar.rendering.BitmapSignRenderer;
import com.jarvismallar.rendering.BorderEffect;
import com.jarvismallar.rendering.ObjectRenderer;
import com.jarvismallar.wrapper.Api;
import com.jarvismallar.wrapper.Implementation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainArView extends FrameLayout implements GLSurfaceView.Renderer {

  private static final boolean ENABLE_BORDER_EFFECT = true; // disable to debug rendering issues


  private String sdkKey;

  private Activity currentActivity;

  private WayfindingSession wayfindingSession;
  private Api arWrapper;

  private GLSurfaceView surfaceView;

  private final SnackbarHelper messageSnackbarHelper = new SnackbarHelper();
  private DisplayRotationHelper displayRotationHelper;
  private TrackingStateHelper trackingStateHelper;

  public MainArView(@NonNull Context context) {
    super(context);

//    this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    // set padding and background color
    this.setPadding(16,16,16,16);
    this.setBackgroundColor(Color.parseColor("#5FD3F3"));

    // add default text view
    TextView text = new TextView(context);
    text.setText("Welcome to Android Fragments with React Native.");
    this.addView(text);
  }

  public MainArView(@NonNull Context context, Activity currentActivity) {
    super(context);

    this.currentActivity = currentActivity;
    this.trackingStateHelper = new TrackingStateHelper(currentActivity);

    // set padding and background color
//    this.setPadding(16,16,16,16);
    this.setBackgroundColor(Color.parseColor("#5FD3F3"));

    // add default text view
//    TextView text = new TextView(context);
//    text.setText("Welcome to Android Fragments with React Native.");
//    this.addView(text);

    this.mLocationManager  = IALocationManager.create(context);

    this.surfaceView = new GLSurfaceView(context);
    displayRotationHelper = new DisplayRotationHelper(/*context=*/ context);

    surfaceView.setPreserveEGLContextOnPause(true);
    surfaceView.setEGLContextClientVersion(2);
    surfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0); // Alpha used for plane blending.
    surfaceView.setRenderer(this);
    surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    surfaceView.setWillNotDraw(false);
    surfaceView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

    this.addView(this.surfaceView);

    this.arWrapper = Implementation.createArWrapper();

//    this.onResume();
    Log.d("JarvisMallArModule", "MainArView constructured done");
  }

  static class ArPOIRenderer {
    IAARObject object;
    BitmapSignRenderer renderer;
    float scale;
  }

  private List<ArPOIRenderer> arPois = new ArrayList<>();

  private final ObjectRenderer destinationObject = new ObjectRenderer();
  private final ObjectRenderer arrowObject = new ObjectRenderer();
  private final ObjectRenderer poiObject = new ObjectRenderer();
  private final BorderEffect borderEffect = new BorderEffect();
  private final BitmapSignRenderer.Cache bitmapSignRendererCache = new BitmapSignRenderer.Cache();

  // Temporary matrix allocated here to reduce number of allocations for each frame.
  private final float[] anchorMatrix = new float[16];
  private static final float[] TARGET_COLOR = new float[]{255, 255, 255, 255};
  private static final float[] ARROW_COLOR = new float[]{50, 128, 247, 255};
  private static final float[] WAYPOINT_COLOR = new float[]{95, 209, 195, 255};

  private static final String NO_CONVERGENCE_MESSAGE = "Walk 20 meters to any direction so we can orient you. Avoid pointing the camera at blank walls.";
  final float[] colorCorrectionRgba = new float[]{ 1, 1, 1, 1 };

  private IALocationManager mLocationManager;

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
  }

  @Override
  public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
    // Prepare the rendering objects. This involves reading shaders, so may throw an IOException.
    try {
      this.arWrapper.onSurfaceCreated(this.getContext());
      this.borderEffect.createOnGlThread(this.getContext());

      setupObject(destinationObject, "models/finish.obj", "models/finish.png");
      setupObject(arrowObject, "models/arrow_stylish.obj", "models/white2x2pixels.png");
      setupObject(poiObject, "models/andy.obj", "models/andy.png");

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void onSurfaceChanged(GL10 gl10, int width, int height) {
    this.displayRotationHelper.onSurfaceChanged(width, height);
    GLES20.glViewport(0, 0, width, height);
    this.borderEffect.onSurfaceChanged(width, height);
  }

  @Override
  public void onDrawFrame(GL10 gl10) {
    GLES20.glClearColor(0.1f, 0.1f, 0.1f, 0.0f); // set alpha to 0 to help post-processing
    // Clear screen to notify driver it should not load any pixels from previous frame.
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    List<ArPOIRenderer> curArPois = arPois; // atomic get

    for (ArPOIRenderer r : curArPois) {
      r.renderer.createOnGlThread(this.getContext()); // no-op if already initialized
    }

    if (!arWrapper.isRunning()) {
      return;
    }

    // Notify ARCore session that the view size changed so that the perspective matrix and
    // the video background can be properly adjusted.
    displayRotationHelper.updateSessionIfNeeded(arWrapper);

    try {
      arWrapper.onFrame();

      // ARCore depth API stuff
      float[] uvTransforms = arWrapper.getUpdatedUvTransformMatrix();
      if (uvTransforms != null) {
        destinationObject.setUvTransformMatrix(uvTransforms);
        arrowObject.setUvTransformMatrix(uvTransforms);
      }

      // Keep the screen unlocked while tracking, but allow it to lock when tracking stops.
      trackingStateHelper.updateKeepScreenOnFlag(arWrapper.shouldKeepScreenOn());

      // If not tracking, don't draw 3D objects, show tracking failure reason instead.
      String trackingFailureReason = arWrapper.getTrackingFailureReasonString();
      if (trackingFailureReason != null) {
        messageSnackbarHelper.showMessage(this.currentActivity, trackingFailureReason);
        return;
      }

      // Get projection matrix.
      float[] projmtx = new float[16];
      arWrapper.getProjectionMatrix(projmtx, 0.1f, 100.0f);

      // Get camera matrix and draw.
      float[] viewmtx = new float[16];
      arWrapper.getViewMatrix(viewmtx);

      // Compute lighting from average intensity of the image.
      // The first three components are color scaling factors.
      // The last one is the average pixel intensity in gamma space.
      arWrapper.getColorCorrection(colorCorrectionRgba);

      // No tracking error at this point. If we detected any plane, then hide the
      // message UI, otherwise show searchingPlane message.
      if (wayfindingSession.converged()) {
        messageSnackbarHelper.hide(this.currentActivity);
      } else {
        messageSnackbarHelper.showMessage(this.currentActivity, NO_CONVERGENCE_MESSAGE);
      }

      float [] invViewMat = new float[16];
      float [] imuToWorld = new float[16];
      arWrapper.getCameraToWorldMatrix(invViewMat);
      arWrapper.getImuToWorldMatrix(imuToWorld);
      wayfindingSession.onArFrame(imuToWorld, invViewMat, arWrapper.getHorizontalPlanes());

      if (!wayfindingSession.converged()) {
        return;
      }

      if (ENABLE_BORDER_EFFECT) {
        borderEffect.beginCapture();
      }

      float scaleFactor;
      for (IAARObject wp : wayfindingSession.getWaypoints()) {
        scaleFactor = 0.4f;
        if (wp.updateModelMatrix(anchorMatrix)) {
          arrowObject.updateModelMatrix(anchorMatrix, scaleFactor);
          arrowObject.draw(viewmtx, projmtx, colorCorrectionRgba, WAYPOINT_COLOR);
        }
      }

      for (ArPOIRenderer arPoi : curArPois) {
        if (!arPoi.object.updateModelMatrix(anchorMatrix)) {
          arPoi.renderer.draw(viewmtx, projmtx, anchorMatrix, arPoi.scale);
        }
      }

      if (wayfindingSession.getArSdk().getWayfindingTarget().updateModelMatrix(anchorMatrix)) {
        scaleFactor = 1;
        destinationObject.updateModelMatrix(anchorMatrix, scaleFactor);
        destinationObject.draw(viewmtx, projmtx, colorCorrectionRgba, TARGET_COLOR);
      }

      if (wayfindingSession.getArSdk().getWayfindingCompassArrow().updateModelMatrix(anchorMatrix)) {
        scaleFactor = 0.3f;
        arrowObject.updateModelMatrix(anchorMatrix, scaleFactor);
        arrowObject.draw(viewmtx, projmtx, colorCorrectionRgba, ARROW_COLOR);
      }

      if (ENABLE_BORDER_EFFECT) {
        borderEffect.endCapture();
        borderEffect.render();
      }

    } catch (Throwable t) {
      // Avoid crashing the application due to unhandled exceptions.
      Log.e("JarvisMallArModule", "Exception on the OpenGL thread");
    }
  }

  public void onDestroy() {
    this.mLocationManager.destroy();
  }

  public void onPause() {
    if (this.wayfindingSession != null) {
      this.wayfindingSession.destroy();
      this.wayfindingSession = null;
    }

    if (this.arWrapper.isRunning()) {
      // Note that the order matters - GLSurfaceView is paused first so that it does not try
      // to query the session. If Session is paused before GLSurfaceView, GLSurfaceView may
      // still call session.update() and get a SessionPausedException.
      this.displayRotationHelper.onPause();
      this.surfaceView.onPause();
      this.arWrapper.pause();
    }
  }

  public void onResume() {
    if (this.wayfindingSession != null) {
      this.wayfindingSession.destroy();
    }

    this.wayfindingSession = new WayfindingSession(
      mLocationManager,
      pois -> {
        // note: atomic in Java
        List<ArPOIRenderer> renderers = new ArrayList<>();
        for (WayfindingSession.ArPOI poi : pois) {
          ArPOIRenderer r = new ArPOIRenderer();
          r.renderer = bitmapSignRendererCache.get(poi.textureName, MainArView.this.currentActivity);
          r.object = wayfindingSession.getArSdk().createArPOI(
            poi.iaPoi.getLocation().latitude,
            poi.iaPoi.getLocation().longitude,
            poi.iaPoi.getFloor(),
            poi.heading,
            poi.elevation);
          r.scale = poi.scale;
          renderers.add(r);
        }
        arPois = renderers;
        if (!pois.isEmpty()) {
          WayfindingSession.ArPOI target = pois.get(0);
          IAWayfindingRequest request = new IAWayfindingRequest.Builder()
            .withFloor(target.iaPoi.getFloor())
            .withLatitude(target.iaPoi.getLocation().latitude)
            .withLongitude(target.iaPoi.getLocation().longitude)
            .build();
          wayfindingSession.setWayfindingTarget(request);
        }
      });

    Api.ResumeResult result = arWrapper.handleInstallFlowAndResume(this.currentActivity);
    switch (result.status) {
      case ERROR:
        messageSnackbarHelper.showError(this.currentActivity, result.errorMessage);
        return;
      case PENDING: return;
      case SUCCESS: break;
    }

    surfaceView.onResume();
    displayRotationHelper.onResume();
  }

  @Override
  public void onWindowFocusChanged(boolean hasWindowFocus) {
    super.onWindowFocusChanged(hasWindowFocus);

    FullScreenHelper.setFullScreenOnWindowFocusChanged(this.currentActivity, hasWindowFocus);
  }

  private void setupObject(ObjectRenderer obj, String modelName, String textureName) throws IOException {
    obj.createOnGlThread(/*context=*/ this.getContext(), modelName, textureName);
    obj.setBlendMode(ObjectRenderer.BlendMode.AlphaBlending);
    arWrapper.setupObject(this.getContext(), obj);
    obj.setMaterialProperties(0.0f, 2.0f, 0.5f, 6.0f);
  }

  public void setSdkKey(String sdkKey) {
    this.sdkKey = sdkKey;
  }
}
