package com.example.scheduleapp;

public class jsonText {
    private static String text;

    public jsonText() {};

    public void setText(String text) {
        jsonText.text = text;
    }

    public String getText() {
        return text;
    }

    public void append(String mystring) {
        text = text + mystring;
    }
}
