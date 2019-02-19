package com.nsb.android.a10kt;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    public static ImageView logoIcon;

    // The WordBlock containing all info for this "game"
    public static WordBlock wb;

    // The TextView that contains the main displayed text
    public static TextView tv, dir;

    // ScrollView contains 'screenshotable' text content
    public static LinearLayout screenshot;

    // The selected word that the user will edit
    public static EditText oldWord;

    // The user specified word that will replace the selected one in the current
    // text block
    public static EditText newWord;

    public static Context mainContext;

    // The number of the selected word in the word array
    public static int selected = 1;

    //Firebase Realtime Database reference variable
    public static DatabaseReference mDatabase, mDatabase2;
    public static DatabaseReference data;

    // Array of everything in text...
    // (each word and punctuation mark will have its own position)
    public static ArrayList<String> txt;

    public static String defaultName = "10kt";
    public static String wbName = defaultName;

    public static ChildEventListener listen;

    public static ValueEventListener listen2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth.getInstance().signInAnonymously();
        initializeUI();
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            reloadPage(wbName);
        }else{
            tv.setText("Server Error - Attempting Reload...");
            this.recreate();
        }

    }


    public void initializeUI(){

        mainContext = this;

        tv = (TextView) findViewById(R.id.the_text);
        dir = (TextView) findViewById(R.id.tvWB);
        dir.setText("{" + wbName + "}");
        screenshot = (LinearLayout) findViewById(R.id.linearLayout3);
        logoIcon = (ImageView) findViewById(R.id.logo);
        oldWord = (EditText) findViewById(R.id.selected_word);
        newWord = (EditText) findViewById(R.id.user_word);

        newWord.setLongClickable(false);
        newWord.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    newWord.clearFocus();
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(newWord.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    String word = newWord.getText().toString();
                    newWord.setText(Utils.makeValid(newWord.getText().toString(), selected));
                }
                return false;
            }
        });
    }

    public static void goToWB(final String name){

        mDatabase = FirebaseDatabase.getInstance().getReference().child(name);
        listen = mDatabase.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    txt = (ArrayList<String>)dataSnapshot.getValue();
                    wb = new WordBlock(txt);
                    Utils.initWords(wb.getStructured());
                    if (oldWord.getText().toString().matches("Select Word")){
                        //DO NOTHING
                    }else {
                        oldWord.setText(wb.getWord(selected));
                    }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed, log a message
                Log.w("ERROR: ", databaseError.toException());
            }
        });


        mDatabase.child("txt").addListenerForSingleValueEvent(listen2 = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    txt = (ArrayList<String>)dataSnapshot.getValue();
                    wb = new WordBlock(txt);
                    Utils.initWords(wb.getStructured());
                    if (oldWord.getText().toString().matches("Select Word")){
                        //DO NOTHING
                    }else {
                        oldWord.setText(wb.getWord(selected));
                    }
                }else{
                    Toast.makeText(MainActivity.mainContext,
                            "WordBlock '" + name + "' Invalid", Toast.LENGTH_SHORT).show();
                    wbName = defaultName;
                    reloadPage(wbName);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed, log a message
                Log.w("ERROR: ", databaseError.toException());
            }
        });

    }

    public static void typeToServer(String s, int select){

        mDatabase.child("txt").child(Integer.toString(Utils.validateSelected(select,
                wb.getPunctuationArray()))).setValue(Utils.makeValid(s, select));
    }

    public static void reloadPage(final String name){

        wbName = name;

        if (listen != null) {
            mDatabase.removeEventListener(listen);
        }

        if (listen2 != null) {
            mDatabase.removeEventListener(listen2);
        }

        goToWB(wbName);
        String txt = "{" + MainActivity.wbName + "}";
        dir.setText(txt);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takeScreenshot();
                } else {
                    return;
                }
                return;
            }
        }
    }

    public static void takeScreenshot() {
        Date now = new Date();
        Random r = new Random();
        String img = android.text.format.DateFormat.format("yyMMddHHmmss", now).toString() +
                (Integer.toString(r.nextInt(9999)));
        View v1 = screenshot;

        try {
            // create bitmap screen capture
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);
            saveImageToExternal(img, bitmap);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

    public static void saveImageToExternal(String imgName, Bitmap bm) throws IOException {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Pictures/10kT");
        myDir.mkdirs();

        String fName = wbName + imgName +".png";
        File file = new File (myDir, fName);
        if (file.exists ()) {
            file.delete ();
        }else{
            file.createNewFile();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
            Toast.makeText(mainContext,
                    "Saved to Pictures/10kT", Toast.LENGTH_SHORT).show();
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        mainContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

    }


}

