package com.nsb.android.a10kt;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CopyButton extends AppCompatImageButton {


    public CopyButton(Context context) {
        super(context);
    }

    public CopyButton(Context context,AttributeSet attrs) {
        super(context, attrs);
    }

    public CopyButton (Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        Vibrator vib = (Vibrator) this.getContext().getSystemService(Service.VIBRATOR_SERVICE);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                vib.vibrate(5);
                this.playSoundEffect(android.view.SoundEffectConstants.CLICK);
                return true;

            case MotionEvent.ACTION_UP:
                vib.vibrate(15);
                this.playSoundEffect(android.view.SoundEffectConstants.CLICK);
                return true;
        }
        return false;
    }

    @Override
    public boolean performClick() {
        super.performClick();

        Activity mainact = (Activity) MainActivity.mainContext;

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(mainact,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

                //Denied. Request the permission
                ActivityCompat.requestPermissions(mainact,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                return false;



        } else {
            MainActivity.takeScreenshot();
            return true;
        }
    }



}
