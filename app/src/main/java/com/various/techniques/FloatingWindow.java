package com.various.techniques;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;

public class FloatingWindow {
    WindowManager windowManager;
    WindowManager.LayoutParams layoutParams;

    public  FloatingWindow(Context context, int width, int height, int type, int flag, int pixelFormat, int gravity){
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams(width,height,type,flag,pixelFormat);
        layoutParams.gravity = gravity;

    }
    public void addView(View view){
        windowManager.addView(view,layoutParams);
    }
    public void removeView(View view){
        windowManager.removeView(view);
    }
    public WindowManager getWindowManager(){
        return windowManager;
    }


}
