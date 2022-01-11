package com.various.techniques;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button floatingWindowBtn, screenshotStartBtn,
            cookieThiefBtn,printNetworkInterfaces;
    FloatingWindow floatingWindow,floatingWindow_;
    TextView textView;
    boolean start;
    boolean canStart;
    Intent captureIntent;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        floatingWindowBtn = (Button) findViewById(R.id.floatingWindowBtn);
        screenshotStartBtn = (Button) findViewById(R.id.screenShot);
        printNetworkInterfaces = (Button) findViewById(R.id.networkInterfaces);
        cookieThiefBtn = (Button) findViewById(R.id.cookieThief);

        floatingWindowBtn.setOnClickListener(this);
        screenshotStartBtn.setOnClickListener(this);
        printNetworkInterfaces.setOnClickListener(this);
        cookieThiefBtn.setOnClickListener(this);

        textView = (TextView) findViewById(R.id.infoBoard);
        textView.setBackgroundColor(Color.GREEN);
        textView.setTextColor(Color.BLACK);

        //screenshot flags
        start = false;
        canStart = false;
        captureIntent = null;
        //screenshot flags end
        checkPermissions();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent;
        switch(id){
            case R.id.floatingWindowBtn:
                cout("Floating window btn clicked");
                startActivity(new Intent(this,FloatingWindowActivity.class));
                onBackPressed();
                break;

            case R.id.screenShot:
                cout("Screenshot window btn clicked");
                screenShot();
                break;

            case R.id.networkInterfaces:
                cout("Nentwork interfaces btn clicked");
                printNetworkInterfaces();
                break;
            case R.id.cookieThief:
                cout("Cookie Thief btn clicked");
                startActivity(new Intent(getApplicationContext(),CookieTheft.class));
        }
    }

    public void checkPermissions(){
        ApplicationInfo applicationInfo = null;
        try {
             applicationInfo = getApplicationContext().getPackageManager().getApplicationInfo(getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if(!Settings.canDrawOverlays(this)){
            Intent overlayPerms = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getPackageName()));
            startActivity(overlayPerms);
        }
        int k = ((AppOpsManager) getSystemService(Context.APP_OPS_SERVICE)).checkOpNoThrow("android:get_usage_stats",applicationInfo.uid,applicationInfo.packageName);
        if(k==0)
            cout("perms granted");

        else
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            cout(Integer.toString(k));

    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions,int [] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            System.out.println("permissions:"+permissions[0]+"was "+grantResults[0]);
        }

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != 1 || data == null) {
            canStart = false;
            return;
        }
        if(resultCode == Activity.RESULT_OK)
        {
            canStart = true;
            captureIntent = CaptureManager.getStartIntent(this,resultCode,data);
        }

    }

    public void printNetworkInterfaces()  {
        StringBuilder stringBuilder = new StringBuilder();
        NetworkInterfaces networkInterface = new NetworkInterfaces();
        for(int i =0; i<networkInterface.getInterfaces().size(); i++)
        {
            stringBuilder.append(networkInterface.getInterfaces().get(i)+"\n");
        }
        textView.setText(stringBuilder);
    }
    public void screenShot(){
        floatingWindow = new FloatingWindow(getApplicationContext(),150,150, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT, Gravity.TOP);
        floatingWindow_ = new FloatingWindow(getApplicationContext(),150,150, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT, Gravity.TOP|Gravity.LEFT);
        Button btnStart = new Button(getApplicationContext());
        Button btnStop = new Button(getApplicationContext());
        btnStop.setBackgroundColor(Color.BLACK);
        btnStop.setTextColor(Color.WHITE);
        btnStop.setText("Back to main menu");
        btnStart.setBackgroundColor(Color.GREEN);
        btnStart.setTextColor(Color.WHITE);
        btnStart.setText("Start Screen Capture");
        floatingWindow.addView(btnStart);
        floatingWindow_.addView(btnStop);
        startMediaProjectionRequest();

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopMediaProjection();
                floatingWindow.removeView(btnStart);
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                floatingWindow_.removeView(btnStop);

            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start = !start;
                if(start)
                {
                    if(canStart) {
                        btnStart.setBackgroundColor(Color.RED);
                        btnStart.setText("Stop Screen capture");
                        startMediaProjection();

                    }

                }
                else{
                    btnStart.setBackgroundColor(Color.GREEN);
                    btnStart.setText("Start Screen capture");
                    stopMediaProjection();
                }


            }
        });

    }
    public void startMediaProjectionRequest(){
        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)
        {
            MediaProjectionManager mediaProjectionManager =
                    (MediaProjectionManager) getApplicationContext().getSystemService(
                            Context.MEDIA_PROJECTION_SERVICE);
            startActivityForResult(
                    mediaProjectionManager.createScreenCaptureIntent(), 1);
        }
        else
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

    }
    public void startMediaProjection(){
        startService(captureIntent);
    }
    public void stopMediaProjection(){
        startService(CaptureManager.getStopIntent(this));
    }
    public void cout(String str){
        System.out.println(str);
    }
}