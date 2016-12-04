package net.gusakov.customlauncher;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.Serializable;

public class AppDetail implements Comparable,Serializable {
    CharSequence label;
    CharSequence name;
    Drawable icon;
    private boolean published;
    private int position;
    Intent launcherIntent;

    AppDetail(){
        published=false;
        position=-1;
    }

    public boolean isPublished(){
        return published;
    }
    public void published(){
        published=true;
    }
    public void unpublished(){
        published=false;
    }

    public void setPosition(int position){
        this.position=position;
    }
    public int getPosition(){
        return position;
    }

    @Override
    public int compareTo(Object o) {
        if (o == null) {
            return 1;
        }
        AppDetail that=(AppDetail)o;
        if(this.position == -1){
            return 1;

        }else if(that.position== -1) {
            return -1;
        }else{
            return this.position-that.position;
        }
    }
}