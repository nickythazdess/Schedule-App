package com.example.scheduleapp;

import java.util.ArrayList;

public class ActivityDiary {
    private ArrayList<Diary> listDiary;
    private static volatile ActivityDiary INSTANCE ;
    private ActivityDiary(){ listDiary = new ArrayList<>(); }

    public static synchronized ActivityDiary getInstance(){
        if (INSTANCE == null) {
            INSTANCE = new ActivityDiary();
        }
        return INSTANCE;
    }

    public void setListDiary(ArrayList<Diary> _listDiary) {
        listDiary = _listDiary;
    }

    public boolean checkExist(String date) {
        for (Diary act : listDiary) {
            if (act.get_date().equals(date)) return true;
        }
        return false;
    }

    public void addDiary(String date, String content){ listDiary.add(new Diary(date,content)); }

    public Diary getDiary(String date){
        for (Diary myDiary:listDiary)
        {
            if (myDiary.get_date().equals(date)) return myDiary;
        }
        return null;
    }

    public void Sort() {
        for (int i = 0; i < listDiary.size() - 1; i++)
            for (int j = i+1; j < listDiary.size(); j++)
            {
                Diary act1 = listDiary.get(i);
                Diary act2 = listDiary.get(j);
                if (act1.get_date().compareTo(act2.get_date()) <= 0) {
                    String date = act1.get_date();
                    String content = act1.get_content();

                    act1.set_date(act2.get_date());
                    act1.set_content(act2.get_content());
                    act2.set_date(date);
                    act2.set_content(content);
                }
            }
    }

    public ArrayList<Diary> getDiaryList(){
        return listDiary;
    }
}
