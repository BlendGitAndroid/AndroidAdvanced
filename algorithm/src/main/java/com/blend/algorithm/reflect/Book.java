package com.blend.algorithm.reflect;

import java.util.List;

class Book<T> {

    private static final String TAG = "Book";

    private String name;
    private String author;
    private List<String> mStringList;
    private T t;

    public Book() {
    }

    public Book(String name, String author) {
        this.name = name;
        this.author = author;
    }

    public static String getTAG() {
        return TAG;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    private String declaredMethod(int index) {
        String string = null;
        switch (index) {
            case 0:
                string = "I am declaredMethod 1 !";
                break;
            case 1:
                string = "I am declaredMethod 2 !";
                break;
            case 2:
                string = "I am declaredMethod 3 !";
        }

        return string;
    }
}
