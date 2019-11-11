package com.example.vocabularybook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.ArrayList;

public class WordAdapter extends ArrayAdapter<Word> {
    private int resourceId;
    private ArrayList<Word> wordList;

    public WordAdapter(Context context, int resource, ArrayList<Word> objects) {
        super(context, resource, objects);
        wordList = objects;
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Word word = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView ENG_txt = view.findViewById(R.id.ENG_txt);
        if (word != null)
            ENG_txt.setText(word.getContent());
        return view;
    }

}
