package com.nsb.android.a10kt;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class Utils {

    public static Integer[] getIndices(String s, char c) {
        int pos = s.indexOf(c, 0);
        List<Integer> indices = new ArrayList<Integer>();
        while (pos != -1) {
            indices.add(pos);
            pos = s.indexOf(c, pos + 1);
        }
        return (Integer[]) indices.toArray(new Integer[0]);
    }

    @SuppressLint("DefaultLocale")
    public static void initWords(String text) {

        String t = "\t";
        t += text.replace("\\", "");

        MainActivity.tv.setMovementMethod(LinkMovementMethod.getInstance());
        MainActivity.tv.setText(t.trim(), BufferType.SPANNABLE);
        Spannable spans = (Spannable) MainActivity.tv.getText();
        Integer[] indices = getIndices(MainActivity.tv.getText().toString(), ' ');
        int start = 0;
        int end = 0;
        for (int i = 0; i <= indices.length; i++) {

            ClickableSpan clickSpan = getClickableSpan(i);
            end = (i < indices.length ? indices[i] : spans.length());
            spans.setSpan(clickSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            start = end + 1;
        }
        MainActivity.tv.invalidate();
    }


    private static ClickableSpan getClickableSpan(final int i) {

        return new ClickableSpan() {

            @Override
            public void onClick(View widget) {

                try {
                    MainActivity.selected = i;
                    TextView tv2 = (TextView) widget;

                    String s = tv2.getText().subSequence(tv2.getSelectionStart(),
                            tv2.getSelectionEnd()).toString();
                    s = s.replaceAll("[\\d\\W&&[^']]", "");

                    MainActivity.oldWord.setText(s);

                } catch(StringIndexOutOfBoundsException e) {
                    Log.e(getClass().getName(),"StringIndexOutOfBoundsException");
                }
            }

            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);

            }
        };
    }

    @SuppressLint("DefaultLocale")
    public static String makeValid(String string, int select) {

        //make all but first letter lower case -- sorry, people with CaMeLcAsE last names!
        string = string.substring(0, 1)
                + (string.substring(1, string.length()).toLowerCase());
        //replace all digits, non-word characters, quotes with NOTHING ("")
        string = string.replaceAll("[\\d\\W&&[^']]", "");

        string = string.replace("_", "");

        if (string.length() > 18) {
            string = string.substring(0, 17);
        }

        if (string.length() == 0){
            string = "word";
        }

        return string;

    }

    public static int validateSelected(int num, ArrayList<Integer> iArray){
        int validated = num;
        int x;

        //Deal with 0th place or wB with none/one punctuation
        if (iArray.size() <= 1 || num <= 1 || num < iArray.get(0)){
            return validated;
        }

        for (int i = 0; i < iArray.size(); i++){
            if (i == 0){
                x = 1;
            }else{
                x = (i + 1);
            }
            try {
                if (num >= (iArray.get(i) - x) && num < iArray.get(x) - x) {
                    validated += x;
                    return validated;
                }
                if (num >= iArray.get(iArray.size() - 1)) {
                    validated += x;
                }
            }catch (IndexOutOfBoundsException e){
                Log.e("ERROR", "IndexOutOfBoundsException: " + Integer.toString(validated));
            }
        }

        return validated;
    }

}
