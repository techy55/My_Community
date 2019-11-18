package com.chaitanya.my_community.other;

public class DataModel {
    String available;
    String contact;
    String workhrs;
    String providerName;
    String name;
    String status;
    String date;
    String month;
    String amount;
    String desc;

    public DataModel(String name, String status, String date) {
        this.name = name;
        this.status = status;
        this.date=date;
    }

    public DataModel(String month, String amt, String temp, String te) {
        this.amount=amt;
        this.month=month;
    }

    public DataModel(String providerName, String available, String contact, String workhrs, String t) {
        this.providerName=providerName;
        this.available=available;
        this.contact=contact;
        this.workhrs=workhrs;
    }

    public DataModel(String name, String date) {
        this.name = name;
        this.date=date;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String  getDate() {
        return date;
    }

    public String getAmount() {
        return amount;
    }

    public String getMonth() {
        return month;
    }

    public String getAvailable() {
        return available;
    }

    public String getContact() {
        return contact;
    }

    public String getWorkhrs() {
        return workhrs;
    }

    public String getProviderName() {
        return providerName;
    }
}
