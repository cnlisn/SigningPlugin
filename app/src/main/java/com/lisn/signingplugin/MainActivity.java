package com.lisn.signingplugin;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private ImageView iv;
    private int IVheight;
    private int IVwidth;
    private GestureDetector detector;

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CrashHandler.getInstance().init(getApplicationContext());
        Button bt_lx = (Button) findViewById(R.id.bt_lx);
        bt_lx.setOnClickListener(this);
        iv = (ImageView) findViewById(R.id.iv);
        IVheight = dip2px(MainActivity.this, 120);
        IVwidth = dip2px(MainActivity.this, 80);


        //创建手势检测器
        detector = new GestureDetector(MainActivity.this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float minMove = 120;         //最小滑动距离
                float minVelocity = 0;      //最小滑动速度
                float beginX = e1.getX();
                float endX = e2.getX();
                float beginY = e1.getY();
                float endY = e2.getY();

                if (beginX - endX > minMove && Math.abs(velocityX) > minVelocity) {   //左滑
                    Toast.makeText(MainActivity.this, velocityX + "左滑", Toast.LENGTH_SHORT).show();
                } else if (endX - beginX > minMove && Math.abs(velocityX) > minVelocity) {   //右滑
                    Toast.makeText(MainActivity.this, velocityX + "右滑", Toast.LENGTH_SHORT).show();
                } else if (beginY - endY > minMove && Math.abs(velocityY) > minVelocity) {   //上滑
                    Toast.makeText(MainActivity.this, velocityX + "上滑", Toast.LENGTH_SHORT).show();
                } else if (endY - beginY > minMove && Math.abs(velocityY) > minVelocity) {   //下滑
                    Toast.makeText(MainActivity.this, velocityX + "下滑", Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        });

        //region Description
        /*iv.setOnTouchListener(new View.OnTouchListener() {
            int startX;
            int startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getX();
                        startY = (int) event.getY();
                        Log.e(TAG, "onTouch: X="+startX+" Y=" +startY);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int endX = (int) event.getX();
                        int endY = (int) event.getY();
                        setLayout(startX,startY,endX,endY);
                        startX = (int) event.getX();
                        startY = (int) event.getY();
                        Log.e(TAG, "onTouch: X="+startX+" Y=" +startY);
                        break;
                    case MotionEvent.ACTION_UP:
                        break;

                    default:
                        break;
                }
                return false;
            }
        });*/
        //endregion
        iv = (ImageView) findViewById(R.id.iv);
        iv.setOnTouchListener(shopCarSettleTouch);
    }


    private View.OnTouchListener shopCarSettleTouch = new View.OnTouchListener() {
        int lastX, lastY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int ea = event.getAction();
            DisplayMetrics dm = getResources().getDisplayMetrics();
            int screenWidth = dm.widthPixels;
//            int screenHeight = dm.heightPixels - 100;//需要减掉图片的高度
            int screenHeight = dm.heightPixels;//需要减掉图片的高度
            switch (ea) {
                case MotionEvent.ACTION_DOWN:
                    lastX = (int) event.getRawX();//获取触摸事件触摸位置的原始X坐标
                    lastY = (int) event.getRawY();
                case MotionEvent.ACTION_MOVE:
                    //event.getRawX();获得移动的位置
                    int dx = (int) event.getRawX() - lastX;
                    int dy = (int) event.getRawY() - lastY;
                    int l = v.getLeft() + dx;
                    int b = v.getBottom() + dy;
                    int r = v.getRight() + dx;
                    int t = v.getTop() + dy;

                    //下面判断移动是否超出屏幕
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
                    Log.e(TAG, "onTouch: " +l+"=="+t+"=="+r+"=="+b);
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

    public void setLayout(int SX, int SY, int EX, int EY) {
        RelativeLayout.LayoutParams pa = (RelativeLayout.LayoutParams) iv.getLayoutParams();
        int leftMargin = pa.leftMargin;
        int topMargin = pa.topMargin;
        int rightMargin = pa.rightMargin;
        int bottomMargin = pa.bottomMargin;
        int x = EX - SX;
        int y = EY - SY;
        Log.e(TAG, "setLayout: " + leftMargin + x + "==" + topMargin + y + "==" + rightMargin + x + "==" + bottomMargin + y);
//        pa.setMargins(leftMargin+x,topMargin+y,rightMargin+x,bottomMargin+y);
//        iv.setLayoutParams(pa);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_lx) {
            Intent intent = new Intent(this, SigningActivity.class);
//            startActivityForResult(intent,188);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 188) {
            Log.e("---", "onActivityResult: " + data.getStringExtra("qm_path") + "===" + data.getStringExtra("Sp_path"));
        }
    }
}
