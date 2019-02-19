package com.nsb.android.a10kt;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Vibrator;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

public class MoreButton extends AppCompatImageButton {


    public MoreButton(Context context) {
        super(context);
    }

    public MoreButton(Context context,AttributeSet attrs) {
        super(context, attrs);
    }

    public MoreButton (Context context, AttributeSet attrs, int defStyleAttr) {
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

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this.getContext());
        View promptsView = li.inflate(R.layout.prompts, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this.getContext(), R.style.alertDialog);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);
        // set dialog message
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Go",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.reloadPage(userInput.getText().toString());
                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);


        // show it
        alertDialog.show();

        return true;
    }


}