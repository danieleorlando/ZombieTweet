package com.zombietweet.android.model;

/**
 * Created by Daniele on 11/05/2014.
 */
public class Schedule {

    private String uid;
    private String created_at;
    private String end_date;
    private String hashtag;
    private String start_date;
    private String subject;
    private String id;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String get_id() {
        return id;
    }

    public void set_id(String id) {
        this.id = id;
    }

    public Schedule(String uid, String created_at, String end_date, String hashtag, String start_date, String subject, String id) {
        this.uid = uid;
        this.created_at = created_at;
        this.end_date = end_date;
        this.hashtag = hashtag;
        this.start_date = start_date;
        this.subject = subject;
        this.id = id;
    }

    public Schedule() {
    }

    public class ScheduleHolder {
        private Schedule[] schedules;
    }
}