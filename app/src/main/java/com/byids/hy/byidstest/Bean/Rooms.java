package com.byids.hy.byidstest.Bean;

import java.io.Serializable;

/**
 * Created by asus on 2016/1/19.
 */
public class Rooms implements Serializable {
    private String roomAttr;


    private String roomName;


    private String roomDBName;


    public void setRoomAttr(String roomAttr){

        this.roomAttr = roomAttr;

    }

    public String getRoomAttr(){

        return this.roomAttr ;

    }

    public void setRoomName(String roomName){

        this.roomName = roomName;

    }

    public String getRoomName(){

        return this.roomName;

    }

    public void setRoomDBName(String roomDBName){

        this.roomDBName = roomDBName;

    }

    public String getRoomDBName(){

        return this.roomDBName;

    }
}
