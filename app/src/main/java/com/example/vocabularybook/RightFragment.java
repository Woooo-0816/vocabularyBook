package com.example.vocabularybook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class RightFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.right_fragment, container, false);
        return view;
    }

    public void refresh(Word word) {
        TextView explain = getView().findViewById(R.id.explain);
        TextView sentence = getView().findViewById(R.id.sentence);
        explain.setText(word.getExplain());
        sentence.setText(word.getSentence());
    }
    public void clear(){
        TextView explain = getView().findViewById(R.id.explain);
        TextView sentence = getView().findViewById(R.id.sentence);
        explain.setText("");
        sentence.setText("");
    }

}