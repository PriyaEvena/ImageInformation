package com.example.priya.imageinfo;

import java.io.Serializable;

/**
 * Created by Priya on 12-01-2018.
 */

public class Template implements Serializable {
    private String Name;
    private int Link;
    private String Description;

    public Template(String Name,int Link,String Description){
        this.Name=Name;
        this.Link=Link;
        this.Description=Description;
    }

    public String getName(){
        return Name;
    }
    public int getLink(){
        return Link;
    }
    public String getDescription(){
        return Description;
    }
}
