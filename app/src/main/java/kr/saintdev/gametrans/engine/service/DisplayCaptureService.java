package kr.saintdev.gametrans.engine.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.UUID;

import kr.saintdev.gametrans.libs.permission.MediaProj;

import static android.app.Activity.RESULT_OK;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 * @Date 2018-07-04
 */

public class DisplayCaptureService extends Service {
    MediaProjectionManager projectionMgr = null;
    MediaProjection mediaProjection = null;
    BroadcastReceiver receiver = null;

    // Display
    Display display = null;     // 디스플레이
    int mDensity = 0;   // 디스플레이 밀도
    int mWidth = 0;
    int mHeight = 0;
    int mRotation = 0;

    // Handler
    OnOrientationChangeHandler orientationHandler = null;

    // MediaProjection API
    VirtualDisplay virtDisplay = null;  // 가상 디스플레이
    ImageReader imgReader = null;       // 이미지 리더

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String command = null;

        if(intent == null) {
            command = "release";
        } else {
            command = intent.getStringExtra("command");
        }

        try {
            switch (command) {
                case "init":
                    // MediaProjection 을 init 한다.
                    initMediaProjection();
                    break;
                case "capture":
                    // 현재 스크린을 캡쳐하여 브로드케스팅 한다.
                    broadcastCaptureImage();
                    break;
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        return START_STICKY;
    }



    @TargetApi(21)
    private void initMediaProjection() {
        // MediaProjection 정의
        this.projectionMgr = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        Intent projectionIntent = MediaProj.INSTANCE.getMediaProjectionIntent();
        WindowManager windowMgr = (WindowManager) getSystemService(WINDOW_SERVICE);

        if(projectionIntent != null && windowMgr != null) {
            // MediaProjection 을 정의 합니다.
            this.mediaProjection = projectionMgr.getMediaProjection(RESULT_OK, projectionIntent);

            // Display 정의
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            this.mDensity = metrics.densityDpi;
            this.display = windowMgr.getDefaultDisplay();

            // Handler 정의
            this.orientationHandler = new OnOrientationChangeHandler(this);
            if (orientationHandler.canDetectOrientation()) {
                // 디스플레이 방향 감지가 가능할 경우
                orientationHandler.enable();    // 활성화
            }
            this.mediaProjection.registerCallback(new OnMediaProjectionHandler(), null);

            // Create virtual display.
            createVirtDisplay();
        }
    }

    @TargetApi(21)
    private void broadcastCaptureImage() {
        // 캡쳐 서비스가 작동중인지 확인한다.
        if(imgReader != null) {
            // 캡쳐서비스가 꺼져있다.
            try {
                Image image = imgReader.acquireLatestImage();

                if (image != null) {
                    Image.Plane[] planes = image.getPlanes();
                    ByteBuffer buffer = planes[0].getBuffer();

                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int rowPadding = rowStride - pixelStride * mWidth;

                    // byte[] 을 Bitmap 으로 변환합니다.
                    Bitmap bitmap = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight, Bitmap.Config.ARGB_8888);
                    bitmap.copyPixelsFromBuffer(buffer);

                    image.close();

                    // Bitmap 을 File 에 Write 합니다.
                    File outputFile = getCacheDir();
                    String filename = UUID.randomUUID().toString().replaceAll("-", "");

                    FileOutputStream fos = new FileOutputStream(outputFile + "/" + filename);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.close();

                    // 번역기 화면을 띄웁니다.
                    Intent transService = new Intent(this, TranslateOverlayService.class);
                    transService.putExtra("path", outputFile + "/" + filename);
                    startService(transService);
                }
            } catch (Exception ex) {
                // 알 수 없는 오류
                ex.printStackTrace();
            }
        }
    }

    /*
        Display 의 방향이 변경되었을 때 핸들러.
     */
    class OnOrientationChangeHandler extends OrientationEventListener {
        public OnOrientationChangeHandler(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            final int rotation = display.getRotation();
            if (rotation != mRotation) {
                mRotation = rotation;
                try {
                    // 가상 디스플레이를 제거합니다.
                    if (virtDisplay != null) virtDisplay.release();
                    if (imgReader != null) imgReader.setOnImageAvailableListener(null, null);

                    // 가상 디스플레이를 다시 만듭니다.
                    createVirtDisplay();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
        MediaProjection 이 Stop 되었을 경우 핸들러
     */
    class OnMediaProjectionHandler extends MediaProjection.Callback {
        @Override
        public void onStop() {
            super.onStop();
        }
    }

    private void createVirtDisplay() {
        Point size = new Point();
        this.display.getSize(size);
        this.mWidth = size.x;
        this.mHeight = size.y;

        // ImageReader start.
        this.imgReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
        this.virtDisplay = mediaProjection.createVirtualDisplay("ScreenTranslate", mWidth, mHeight, mDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, imgReader.getSurface(), null, null);
    }

    @Override
    public void onDestroy() {
        if(this.receiver != null)   unregisterReceiver(this.receiver);
        if(mediaProjection != null) mediaProjection.stop();
        if(virtDisplay != null)     virtDisplay.release();

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static Intent getDisplayCaptureIntent(Context context) {
        return new Intent(context, DisplayCaptureService.class);
    }
}