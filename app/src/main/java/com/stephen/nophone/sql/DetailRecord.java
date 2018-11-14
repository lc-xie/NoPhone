package com.stephen.nophone.sql;

/**
 * Created by stephen on 18-11-6.
 * 数据库记录对象类
 */

public class DetailRecord {

    private int id;
    private String data;
    private String startTime;
    private String endTime;
    private long consume;

    public DetailRecord(String data, String startTime, String endTime, long consume) {
        this.data = data;
        this.startTime = startTime;
        this.endTime = endTime;
        this.consume = consume;
    }

    public DetailRecord(int id, String data, String startTime, String endTime, long consume) {
        this.id = id;
        this.data = data;
        this.startTime = startTime;
        this.endTime = endTime;
        this.consume = consume;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    long getConsume() {
        return consume;
    }

    public void setConsume(long consume) {
        this.consume = consume;
    }
}
