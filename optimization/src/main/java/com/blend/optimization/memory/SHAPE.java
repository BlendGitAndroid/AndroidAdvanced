package com.blend.optimization.memory;

import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class SHAPE {
    public static final int RECTANGLE=0;
    public static final int TRIANGLE=1;
    public static final int SQUARE=2;
    public static final int CIRCLE=3;


    @IntDef(flag=true,value={RECTANGLE,TRIANGLE,SQUARE,CIRCLE})
    @Target({ElementType.PARAMETER,ElementType.METHOD,ElementType.FIELD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Model{

    }

    private @Model int value=RECTANGLE;
    public void setShape(@Model int value){
        this.value=value;
    }
    @Model
    public int getShape(){
        return this.value;
    }
}
