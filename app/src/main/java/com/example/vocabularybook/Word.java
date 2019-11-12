package com.example.vocabularybook;

public class Word {
    private String content;
    private String explain;
    private String sentence;
    public Word(){

    }

    public Word(String content, String explain, String sentence) {
        this.content = content;
        this.explain = explain;
        this.sentence = sentence;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }
}
