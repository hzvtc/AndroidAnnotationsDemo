package com.fjq.androidannotationsdemo;

import android.content.ClipboardManager;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.LongClick;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.AnimationRes;
import org.androidannotations.annotations.res.DrawableRes;
import org.androidannotations.annotations.res.TextRes;

@Fullscreen //全屏
@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
    @ViewById
    Button button2;

    @ViewById
    Button button1;

    @ViewById(R.id.textView)   //指定id的注入
            TextView textView;

    @ViewById
    ProgressBar progressBar;

    //获取系统service的方法(取代原来的clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);)
    @SystemService
    ClipboardManager clipboardManager;

    @Click({R.id.button, R.id.button2,R.id.button3,R.id.button4})
    public void simpleButtonOnClicked(View view) {
        switch (view.getId()) {
            case R.id.button: {
                textView.setText("Button1 is Clicked!");
            }
            break;
            case R.id.button2: {
                textView.setText("Button2 is Clicked!");
            }
            break;
            case R.id.button3:{
                CharSequence content = clipboardManager.getText();
                if (content!=null){
                    Toast.makeText(getApplicationContext(),"剪贴板内容: " + content, Toast.LENGTH_SHORT).show();
                }

            }
            break;
            case R.id.button4:{
                Toast.makeText(getApplicationContext(),"滚动条开始了!",Toast.LENGTH_SHORT).show();
                progressBarWorks();
            }
            break;
        }
    }

    @LongClick({R.id.button2})
    public void buttonOnLongClicked(View view){
        switch (view.getId()){
            case R.id.button:{
                textView.setText("Button1 is LongClicked!");//由于没注册，所以不可能被触发
            }
            break;
            case R.id.button2:{
                textView.setText("Button2 is LongClicked!");//可触发
            }
            break;
        }
    }

    //==============================================关于线程的注解================================================
    //相当于一个新的任务AsyncTask或者新线程Thread
    @Background
    public void progressBarWorks(){
        //相当于一个新的线程中执行: @Background
        int i = 1;
        while (i <= 10){
            Log.e("progress","进度: " + i);
            try {
                Thread.sleep(1000);
                updateProgressBar(i);
                //直接progressBar.setProgress(i);也可以的，所以@Background注解内部可能实现了handler机制
                i++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    //指代UI线程
    @UiThread
    public void updateProgressBar(int i){
        progressBar.setProgress(i);
        if (i == 10){
            Toast.makeText(getApplicationContext(), "滚动条结束",Toast.LENGTH_SHORT).show();
        }
    }

    //===================================================关于资源的注解=========================================

    @AnimationRes(R.anim.rotate)
    Animation animationRotate;

    @ViewById
    ImageView imageView;

    @DrawableRes(R.drawable.btn_dots)
    Drawable myphoto;

    @TextRes(R.string.app_name)
    CharSequence text;
    @Click({R.id.button5,R.id.button6,R.id.button7})
    public void animationButtonOnClicked(View view){
        switch (view.getId()){
            case R.id.button5:{
                imageView.startAnimation(animationRotate);
            }
            break;
            case R.id.button6:{
                imageView.setImageDrawable(myphoto);
            }
            break;
            case R.id.button7:{
                Toast.makeText(getApplicationContext(),text.toString(),Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }

    //=======================================关于几个事件的先后顺序===============================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("FirstToLast", "onCreate");

        //可省略！
        //setContentView(R.layout.activity_my);

        //progressBar.setMax(100);  报错，空指针异常
        //因为在onCreate()被调用的时候，@ViewById还没有被set，也就是都为null
        //所以如果你要对组件进行一定的初始化，那么你要用@AfterViews注解
    }

    @AfterViews
    public void init(){
        Log.e("FirstToLast","init");
        progressBar.setMax(10);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("FirstToLast","onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("FirstToLast","onStart");
    }
}
