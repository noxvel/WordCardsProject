package com.vseznaikastas.wordcards;

/**
 * Created by admin on 17.06.13.
 */
public class Word {
    private long id;
    private String word;
    private String translate;


    public Word(long id, String word, String translate){
        this.id = id;
        this.word = word;
        this.translate = translate;
    }

    public Word(String word, String translate){
        this.word = word;
        this.translate = translate;
    }

    public Word(long id){
        this.id = id;
    }

    public long getId(){
        return id;
    }

    public String getStringId(){
        return String.valueOf(id);
    }

    public String getWord(){
        return word;
    }

    public String getTranslate(){
        return translate;
    }

    public void setId(long id){
        this.id = id;
    }

    public void setWors(String word){
        this.word = word;
    }

    public void setTranslate(String translate){
        this.translate = translate;
    }
}
