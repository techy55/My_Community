package com.chaitanya.my_community.other;

public class UserDetails {
    public String Name;
    public String Email;
    public String userType;
    public String Gender;
    public String Phone;
    public String Date_of_Birth;
    public String Uid;
    public String ProfileImageUrl;

    //For Backing up and Restoring User Data
    public UserDetails(String name, String email, String userType, String gender, String phone, String date_of_Birth, String uid, String ProfileImageUrl) {
        Name = name;
        Email = email;
        this.userType = userType;
        Gender = gender;
        Phone = phone;
        Date_of_Birth = date_of_Birth;
        Uid = uid;
        this.ProfileImageUrl = ProfileImageUrl;
    }

    public UserDetails(String name, String email,  String userType, String gender, String phone, String uid) {
        Name = name;
        Email = email;
        this.userType = userType;
        Gender = gender;
        Phone = phone;
        Uid = uid;
    }

    public UserDetails() {
        //default constructor
    }

    //For in App Registration
    public UserDetails(String Name, String Email, String userType, String Gender, String Phone) {
        this.Name = Name;
        this.Email = Email;
        this.userType = userType;
        this.Gender = Gender;
        this.Phone = Phone;
    }

}
