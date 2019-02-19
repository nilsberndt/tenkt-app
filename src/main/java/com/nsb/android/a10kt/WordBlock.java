package com.nsb.android.a10kt;

import java.util.ArrayList;

public class WordBlock {

    // Array of everything in text...
    // (each word and punctuation mark will have its own position)
    public static ArrayList<String> textArray;

    // Words in the WB (no punctuation)
    // Used for reference and editing functions
    // without having to disturb the punctuation
    public static ArrayList<String> words;

    public static ArrayList<Integer> punctuation;

    // String putting together new or original words
    // with the original punctuation placement

    private static String structured;


    // Constructor
    public WordBlock(ArrayList<String> t) {
        //super();

        textArray = t;
        words = new ArrayList<String>();
        punctuation = new ArrayList<Integer>();

        setWords(textArray);


        buildStructure(textArray);

    }


    /*private ArrayList<String> splitText(String t, boolean intoWords) {

        ArrayList<String> every = new ArrayList<String>();

        for (int i = 0; i < t.length(); i++) {
            String character = Character.toString(t.charAt(i));

            if (character.matches("(?!')\\p{P}")) {
                if (intoWords == true) {

                } else {
                    every.add(" " + character + " ");
                }
            } else {
                every.add(character);
            }
        }

        String text = "";
        for (int i = 0; i < every.size(); i++) {
            text += every.get(i);
        }
        ArrayList<String> result = new ArrayList<String>(Arrays.asList(text
                .split("\\s+")));

        for (int i = 0; i < result.size(); i++) {
            result.get(i).replaceAll("\\s+", "");
            if (result.get(i).isEmpty()) {
                result.remove(i);
            }
        }

        return result;
    }*/

    private void buildStructure(ArrayList<String> t) {

        String noSlash = "";
        String struct = "";



        for (int i = 0; i < t.size(); i++){

            noSlash = t.get(i).replace("\\", "");

            if (i == 0 || noSlash.matches("\\p{Punct}")){
                struct += noSlash;
            }
            else{
                struct += " " + noSlash;
            }
        }

        structured = struct;

    }


    private void capitalize(){

        String string = textArray.get(0);
        textArray.set(0, string.substring(0,1).toUpperCase() +
                string.substring(1,string.length()));

        for (int i = 0; i < textArray.size(); i++){

            if (textArray.get(i).toString().matches("[.!]") && i != (textArray.size()-1)){
                string = textArray.get(i+1);
                textArray.set((i+1), string.substring(0,1).toUpperCase() +
                        string.substring(1,string.length()));
            }else if (getPunctuationArray().size() == 0 && i != (textArray.size())){
                string = textArray.get(i);
                textArray.set((i), string.substring(0,1).toUpperCase() +
                        string.substring(1,string.length()));
            }
        }
    }

    public ArrayList<String> getWords() {

        return words;
    }

    public String getWord(int pos) {

        return words.get(pos);
    }

    public ArrayList<Integer> getPunctuationArray() {

        return punctuation;
    }

    public String getPunctuation(int pos) {

        return punctuation.get(pos).toString();
    }

    public ArrayList<String> getTextArray() {

        return textArray;
    }

    public String getTextArrayElement(int i){
        return textArray.get(i);
    }

    public String getStructured() {
        buildStructure(textArray);
        return structured;
    }

    public void setWords(ArrayList<String> t) {


        for (int i = 0; i < t.size(); i++){
            if (t.get(i).matches("\\p{Punct}")){
                punctuation.add(i);
            }
            else{
                //Add to 'words' array
                words.add(t.get(i));
            }
        }


        capitalize();
    }

    public void setWord(int pos, String word) {
        words.set(pos, word);
    }

    public void setStructured(String s) {

        structured = s;
    }


}
