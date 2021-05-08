package com.example.scheduleapp;

public class Diary{
    private String _date = null;
    private String _content = null;
    public Diary(String date, String content) {
        this._date = date;
        this._content = content;
    }

    public String get_date() { return _date; }

    public void set_date(String date) { this._date = date; }

    public String get_content() { return _content; }

    public void set_content(String content) { this._content = content; }
}
