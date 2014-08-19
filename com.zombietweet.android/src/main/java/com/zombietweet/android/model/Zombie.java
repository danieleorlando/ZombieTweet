package com.zombietweet.android.model;

/**
 * Created by Daniele on 13/05/2014.
 */
public class Zombie {

    String author;
    String avatar;
    String created_at;
    String id;
    String oid;
    String screen_name;
    String text;

    public Zombie(String author, String avatar, String created_at, String id, String oid, String screen_name, String text) {
        this.author = author;
        this.avatar = avatar;
        this.created_at = created_at;
        this.id = id;
        this.oid = oid;
        this.screen_name = screen_name;
        this.text = text;
    }

    public Zombie() {
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
