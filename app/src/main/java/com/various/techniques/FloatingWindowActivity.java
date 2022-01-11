package com.various.techniques;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

public class FloatingWindowActivity extends AppCompatActivity implements View.OnClickListener{

    private Button startFloating, dismissBtn;
    private WindowManager windowManager;
    private ActivityManager activityManager;
    private WindowManager.LayoutParams layoutParams;
    private View floatingLayout;
    private TextView textView;
    private Boolean stopHandle;
    private Object usageStats;
    private boolean h = true;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floating_window);
        startFloating = (Button) findViewById(R.id.startFloating);
        startFloating.setOnClickListener(this);

        LayoutInflater layoutInflater = getLayoutInflater();
        floatingLayout = layoutInflater.inflate(R.layout.floating_view,null);
        dismissBtn = (Button) floatingLayout.findViewById(R.id.dismissBtn);
        textView = (TextView) floatingLayout.findViewById(R.id.textView);
        stopHandle = false;

        this.activityManager = (ActivityManager) getSystemService("activity");
        if (Build.VERSION.SDK_INT >= 21) {
            usageStats = getSystemService("usagestats");
        }

        getTasks();


        dismissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToMain = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(backToMain);
                windowManager.removeView(floatingLayout);
                stopHandle = true;

            }
        });
    }

    public void getTasks(){

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                textView.setText(printForegroundTask());
                System.out.println("checking");
                if(stopHandle)
                    handler.removeCallbacks(this);
                else
                    handler.postDelayed(this,1000);
            }
        },1000);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.startFloating:
                createFloatingWindow();
                onBackPressed();
                break;
            default:
                break;

        }
    }

    private String printForegroundTask(){
        if (Build.VERSION.SDK_INT < 21) {
            List<ActivityManager.RunningTaskInfo> runningTasks = this.activityManager.getRunningTasks(1);
            return runningTasks.get(0).topActivity.getPackageName() + "\n" + runningTasks.get(0).topActivity.getClassName();
        }
        long currentTimeMillis = System.currentTimeMillis();
        UsageEvents queryEvents = ((UsageStatsManager) this.usageStats).queryEvents(currentTimeMillis - ((long) (this.h ? 600000 : 60000)), currentTimeMillis);
        String className = null;
        String packageName = null;
        while (queryEvents.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
            queryEvents.getNextEvent(event);
            switch (event.getEventType()) {
                case 1:
                    packageName = event.getPackageName();
                    className = event.getClassName();
                    break;
                case 2:
                    if (!event.getPackageName().equals(packageName)) {
                        break;
                    } else {
                        packageName = null;
                        break;
                    }
            }
        }
        if (packageName != null) {
            return packageName + "\n" + className;
        }
        return null;
    }

//    private String printForegroundTask(){
//        UsageStats usageStats = null;
//        if(Build.VERSION.SDK_INT < 21){
//            return ((ActivityManager.RunningAppProcessInfo) ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses().get(0)).processName;
//        }
//        else{
//            UsageStatsManager usageStatsManager = (UsageStatsManager)getSystemService(Context.USAGE_STATS_SERVICE);
//            long current_time = System.currentTimeMillis();
//            List appList = usageStatsManager.queryUsageStats(0,(current_time-1000000),current_time);
//            if(appList != null && appList.size() >0){
//                TreeMap treeMap = new TreeMap();
//                Iterator iter = appList.iterator();
//                while(iter.hasNext()){
//                    usageStats = (UsageStats)iter.next();
//                    treeMap.put(Long.valueOf(usageStats.getLastTimeUsed()),usageStats);
//                }
//                if(treeMap != null && !treeMap.isEmpty()){
//                    return ((UsageStats)treeMap.get(treeMap.lastKey())).getPackageName();
//                }
//            }
//        }
//        return "No data availlable";
//    }
    private void createFloatingWindow(){
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams(1200,500, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                 WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
        layoutParams.gravity = Gravity.TOP|Gravity.LEFT;

//        Button btn=new Button(getApplicationContext());
//        btn.setAlpha((float)0.5);

        windowManager.addView(floatingLayout,layoutParams);

    }

}