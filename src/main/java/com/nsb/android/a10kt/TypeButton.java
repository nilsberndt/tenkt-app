package com.nsb.android.a10kt;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class TypeButton extends AppCompatImageButton {


    public TypeButton(Context context) {
        super(context);
    }

    public TypeButton(Context context,AttributeSet attrs) {
        super(context, attrs);
    }

    public TypeButton (Context context, AttributeSet attrs, int defStyleAttr) {
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
        if (MainActivity.newWord.getText().toString().matches("")){
            MainActivity.newWord.setText("word");
        }

        MainActivity.typeToServer(MainActivity.newWord.getText().toString(),
                MainActivity.selected);

        return true;
    }


}
