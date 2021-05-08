package com.example.scheduleapp;

import com.google.android.gms.maps.model.LatLng;

public class Activity {
    private String _timeStart = null;
    private String _timeEnd = null;
    private String _date = null;
    private String _title = null;
    private String _info = null;
    private String _place = null;
    private String _GGplace = null;
    private LatLng _location = null;
    private String _longi = null;
    private String _lati = null;
    private String _childDate = null;
    private String _repeat = "Today";

    public Activity() { }
    public Activity(String date, String title, String timeStart, String timeEnd, String place) {
        this._date = date;
        this._timeStart = timeStart;
        this._timeEnd = timeEnd;
        this._title = title;
        this._place = place;
    }

    public void set_time(String timeStart){
        this._timeStart = timeStart;
    }

    public String get_repeat() {
        return _repeat;
    }

    public void set_repeat(String _repeat) {
        this._repeat = _repeat;
    }

    public String get_childDate() {
        return _childDate;
    }

    public void set_childDate(String _childDate) {
        this._childDate = _childDate;
    }

    public String get_date() {
        return _date.toString();
    }

    public void set_date(String _date) {
        this._date = _date;
    }

    public void set_timeEnd(String _timeEnd){
        this._timeEnd = _timeEnd;
    }

    public String get_timeEnd() {
        return _timeEnd;
    }

    public String get_timeStart() { return _timeStart; }

    public void set_title(String _title) {
        this._title = _title;
    }

    public String get_title() {
        return _title;
    }

    public void set_info(String _info) {
        this._info = _info;
    }

    public void set_place(String _place) {
        this._place = _place;
    }

    public String get_info() {
        return _info;
    }

    public String get_place() {
        return _place;
    }

    public LatLng get_location() {return _location;}

    public void set_location(LatLng location) {this._location = location;}

    public String get_GGplace() {return _GGplace;}

    public void set_GGplace(String _GGplace) {this._GGplace = _GGplace;}

    public String get_longi() { return _longi; }

    public void set_longi(String _longi) { this._longi = _longi; }

    public String get_lati() { return _lati; }

    public void set_lati(String _lati) { this._lati = _lati; }
}