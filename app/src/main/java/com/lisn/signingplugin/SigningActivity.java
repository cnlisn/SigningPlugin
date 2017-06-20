package com.lisn.signingplugin;
//package com.tdkj.signing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.pm.PackageManager;
import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.os.Build;

import com.lisn.signingplugin.FakeR;

//import com.tdkj.signing.SigningPlugin;
public class SigningActivity extends Activity {

    private ImageView iv;
    private Bitmap baseBitmap;
    private Canvas canvas;
    private Paint paint;
    private String qm_path;
    private FakeR R;
    private String titleColor = "#a266c4";
    private String TAG = "----";
    private SurfaceView surfaceView;
    boolean isVisibility;

    private void title() {
        LinearLayout ll = (LinearLayout) findViewById(R.getId("id", "ll_title"));
        ImageView iv = (ImageView) findViewById(R.getId("id", "back"));
        TextView tv = (TextView) findViewById(R.getId("id", "tv_title"));
        tv.setText("电子签名");
        String sColor = getIntent().getStringExtra("titleColor");
        if (!TextUtils.isEmpty(sColor)) {
            titleColor = sColor;
        }
        ll.setBackgroundColor(Color.parseColor(titleColor));
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SigningPlugin.cbCtx.error("null");
                finish();
            }
        });
        ImageView iv_info = (ImageView) findViewById(R.getId("id", "iv_info"));
        iv_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVisibility) {
                    isVisibility = false;
                    ViewGroup.LayoutParams lp = surfaceView.getLayoutParams();
                    lp.height = dip2px(SigningActivity.this, 120);
                    lp.width = dip2px(SigningActivity.this, 80);
                    surfaceView.setLayoutParams(lp);
                } else {
                    isVisibility = true;
                    ViewGroup.LayoutParams lp = surfaceView.getLayoutParams();
                    lp.height = 1;
                    lp.width = 1;
                    surfaceView.setLayoutParams(lp);
                }
            }
        });
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private int flag88 = 88;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == flag88) {
                final int width = iv.getWidth();
                final int height = iv.getHeight();
                baseBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                iv.setImageBitmap(baseBitmap);
                canvas = new Canvas(baseBitmap);
                canvas.drawColor(Color.WHITE);
                canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                iv.setOnTouchListener(new View.OnTouchListener() {
                    int startX;
                    int startY;

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                startX = (int) event.getX();
                                startY = (int) event.getY();
                                canvas.drawPoint(startX, startY, paint);
                                iv.setImageBitmap(baseBitmap);
                                break;
                            case MotionEvent.ACTION_MOVE:
                                int newX = (int) event.getX();
                                int newY = (int) event.getY();
                                canvas.drawLine(startX, startY, newX, newY, paint);
                                startX = (int) event.getX();
                                startY = (int) event.getY();
                                iv.setImageBitmap(baseBitmap);
                                break;
                            case MotionEvent.ACTION_UP:
                                break;

                            default:
                                break;
                        }
                        return true;
                    }
                });

                surfaceView.setOnTouchListener(new View.OnTouchListener() {
                    int lastX, lastY, l, b, r, t;

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        int ea = event.getAction();
                        int screenWidth = width;
                        int screenHeight = height;
                        switch (ea) {
                            case MotionEvent.ACTION_DOWN:
                                lastX = (int) event.getRawX();
                                lastY = (int) event.getRawY();
                            case MotionEvent.ACTION_MOVE:
                                int dx = (int) event.getRawX() - lastX;
                                int dy = (int) event.getRawY() - lastY;
                                l = v.getLeft() + dx;
                                b = v.getBottom() + dy;
                                r = v.getRight() + dx;
                                t = v.getTop() + dy;

                                if (l < 0) {
                                    l = 0;
                                    r = l + v.getWidth();
                                }
                                if (t < 0) {
                                    t = 0;
                                    b = t + v.getHeight();
                                }
                                if (r > screenWidth) {
                                    r = screenWidth;
                                    l = r - v.getWidth();
                                }
                                if (b > screenHeight) {
                                    b = screenHeight;
                                    t = b - v.getHeight();
                                }
                                v.layout(l, t, r, b);
                                Log.e(TAG, "onTouch: " + l + "==" + t + "==" + r + "==" + b);
                                lastX = (int) event.getRawX();
                                lastY = (int) event.getRawY();
                                v.postInvalidate();
                                break;
                            case MotionEvent.ACTION_UP:

                                break;
                        }
                        return true;
                    }
                });
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        R = new FakeR(this);
        setContentView(R.getId("layout", "activity_signing"));


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.e(TAG, "initView: " + Build.VERSION.SDK_INT + "==" + Build.VERSION_CODES.M);
            initView();
        } else {
            hasPermission();
            Log.e(TAG, "hasPermission: " + Build.VERSION.SDK_INT + "==" + Build.VERSION_CODES.M);
        }
        title();
    }

    private void initView() {
        iv = (ImageView) findViewById(R.getId("id", "iv"));
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
        paint.setStrokeCap(Paint.Cap.ROUND);// 形状
        paint.setStrokeWidth(8);
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        handler.sendEmptyMessageDelayed(flag88, 500);
        //region Description
//        iv.setOnTouchListener(new View.OnTouchListener() {
//            int startX;
//            int startY;
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        startX = (int) event.getX();
//                        startY = (int) event.getY();
//                        canvas.drawPoint(startX, startY, paint);
//                        iv.setImageBitmap(baseBitmap);
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        int newX = (int) event.getX();
//                        int newY = (int) event.getY();
//                        canvas.drawLine(startX, startY, newX, newY, paint);
//                        startX = (int) event.getX();
//                        startY = (int) event.getY();
//                        iv.setImageBitmap(baseBitmap);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        break;
//
//                    default:
//                        break;
//                }
//                return true;
//            }
//        });
        //endregion

        if (findFrontFacingCamera() != -1) {
            InitSurfaceView();
        } else {
            Toast.makeText(this, "手机没有前置摄像头", Toast.LENGTH_SHORT).show();
        }
    }


    public void clear(View view) {
        canvas.drawColor(Color.WHITE);
        iv.setImageBitmap(baseBitmap);
    }

    public void save(View view) {
        try {
            SaveImage();
        } catch (Exception e) {
        }

    }

    private void SaveImage() {
        try {
            String path = this.getFilesDir().getPath();
//            File file1 = new File(String.valueOf(Environment.getExternalStorageDirectory()) + File.separator + "Qm");
            File file1 = new File(path + File.separator + "Qm");
            if (!file1.exists()) {
                file1.mkdirs();
            }
            String child = System.currentTimeMillis() + ".jpg";
            qm_path = (file1.getAbsolutePath() + File.separator + child);
            File file = new File(file1, child);

            FileOutputStream stream = new FileOutputStream(file);
            baseBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.close();
            Toast.makeText(this, "签名保存成功", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            //            intent.setAction(intent.ACTION_MEDIA_MOUNTED);
            intent.setAction(intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(Environment.getExternalStorageDirectory()));
            sendBroadcast(intent);
//            SigningPlugin.cbCtx.success(qm_path + "##" + Sp_path);
            Log.e(TAG, "SaveImage: " + qm_path + "--" + Sp_path);
            finish();

        } catch (Exception e) {
            Log.e(TAG, "SaveImage: " + e.toString());
            Toast.makeText(this, "签名保存失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 0x005;


    private void hasPermission() {
        if (ContextCompat.checkSelfPermission(SigningActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(SigningActivity.this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(SigningActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    SigningActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        } else {
            initView();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                initView();
                Record();
            } else {
                Toast.makeText(SigningActivity.this, "请开启应用所需相关权限", Toast.LENGTH_SHORT).show();
                finish();
            }
            return;
        }
    }

    private View.OnTouchListener TouchListener = new View.OnTouchListener() {
        int lastX, lastY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int ea = event.getAction();
            DisplayMetrics dm = getResources().getDisplayMetrics();
            int screenWidth = dm.widthPixels;
//            int screenHeight = dm.heightPixels - 100;//需要减掉图片的高度
            int screenHeight = dm.heightPixels - v.getHeight();
            switch (ea) {
                case MotionEvent.ACTION_DOWN:
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                case MotionEvent.ACTION_MOVE:
                    int dx = (int) event.getRawX() - lastX;
                    int dy = (int) event.getRawY() - lastY;
                    int l = v.getLeft() + dx;
                    int b = v.getBottom() + dy;
                    int r = v.getRight() + dx;
                    int t = v.getTop() + dy;

                    if (l < 0) {
                        l = 0;
                        r = l + v.getWidth();
                    }
                    if (t < 0) {
                        t = 0;
                        b = t + v.getHeight();
                    }
                    if (r > screenWidth) {
                        r = screenWidth;
                        l = r - v.getWidth();
                    }
                    if (b > screenHeight) {
                        b = screenHeight;
                        t = b - v.getHeight();
                    }
                    v.layout(l, t, r, b);
                    Log.e(TAG, "onTouch: " + l + "==" + t + "==" + r + "==" + b);
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    v.postInvalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return true;
        }
    };

    /**
     * 找前置摄像头,没有则返回-1
     *
     * @return cameraId
     */
    private int findFrontFacingCamera() {
        int cameraId = -1;
        //获取摄像头个数
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    private SurfaceHolder mSurfaceHolder;

    private void InitSurfaceView() {
        surfaceView = (SurfaceView) findViewById(R.getId("id", "camera_show_view"));
        mSurfaceHolder = surfaceView.getHolder();
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.setFixedSize(1280, 720);
        mSurfaceHolder.setKeepScreenOn(true);
        SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mSurfaceHolder = holder;
                initCamera();
                Record();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

                mSurfaceHolder = null;
                if (mediaRecorder != null) {
                    mediaRecorder.release();
                    mediaRecorder = null;
                }
                if (myCamera != null) {
                    myCamera.release();
                    myCamera = null;
                }
            }
        };
        mSurfaceHolder.addCallback(callback);
//        surfaceView.setOnTouchListener(TouchListener);
    }

    private Camera myCamera;

    //初始化Camera设置
    public void initCamera() {
        if (myCamera == null) {
            try {
                myCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);//前置摄像头
                Camera.Parameters myParameters = myCamera.getParameters();
                myCamera.setParameters(myParameters);
                myCamera.setDisplayOrientation(90);
                myCamera.setPreviewDisplay(mSurfaceHolder);
                myCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("----", "initCamera: " + e);
            }
        }
    }

    private MediaRecorder mediaRecorder;
    private String Sp_path;

    private void Record() {
        synchronized (this) {
            try {
                if (myCamera == null) {
                    initCamera();
                }
                if (mediaRecorder != null) {
                    mediaRecorder.release();
                    mediaRecorder = null;
                }
                if (mediaRecorder == null) {
                    String path = this.getFilesDir().getPath();
//                    File file1 = new File(String.valueOf(Environment.getExternalStorageDirectory()) + File.separator + "Sp");
                    File file1 = new File(path + File.separator + "Sp");
                    if (!file1.exists()) {
                        file1.mkdirs();
                    }
                    String child = System.currentTimeMillis() + ".mp4";
                    Sp_path = (file1.getAbsolutePath() + File.separator + child);
                    File videoFile = new File(file1, child);
                    Log.e("---", "Record: "+videoFile.getAbsolutePath() );
                    Log.e("---", "Record: "+SigningActivity.this.getFilesDir() );
                    mediaRecorder = new MediaRecorder();

                    myCamera.unlock();
                    mediaRecorder.setCamera(myCamera);
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mediaRecorder.setVideoSize(320, 240);
                    mediaRecorder.setVideoFrameRate(5);
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
                    if(Build.VERSION.SDK_INT>=24) { //判读版本是否在7.0以上
                        Uri uriForFile = FileProvider.getUriForFile(SigningActivity.this, BuildConfig.APPLICATION_ID+".fileprovider", videoFile);
                        Log.e("---", "getPath: "+uriForFile.getPath() );
                        Log.e("---", "getEncodedPath: "+uriForFile.getEncodedPath() );
                        Log.e("---", "getAuthority: "+uriForFile.getAuthority() );
                        Log.e("---", "getLastPathSegment: "+uriForFile.getLastPathSegment() );
//                        06-20 11:02:48.953 26990-26990/com.lisn.signingplugin E/---: getPath: /Sp/1497927768935.mp4
//                        06-20 11:02:48.953 26990-26990/com.lisn.signingplugin E/---: getEncodedPath: /Sp/1497927768935.mp4
//                        06-20 11:02:48.953 26990-26990/com.lisn.signingplugin E/---: getAuthority: com.lisn.signingplugin.fileprovider
//                        06-20 11:02:48.953 26990-26990/com.lisn.signingplugin E/---: getLastPathSegment: 1497927768935.mp4
                        mediaRecorder.setOutputFile(uriForFile.getPath());
                    }else {
                        mediaRecorder.setOutputFile(videoFile.getAbsolutePath());
                    }
                    mediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
                    mediaRecorder.setOrientationHint(270);
                }
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            SigningPlugin.cbCtx.error("null");
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}