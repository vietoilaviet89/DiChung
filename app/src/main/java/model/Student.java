package model;

import java.io.Serializable;

/**
 * Created by 123456789 on 4/3/2017.
 */

@SuppressWarnings("serial") //annotation để ẩn cảnh báo của compiler
public class Student implements Serializable{
    private String name;
    //transient là giải pháp khi không thể implements Serializable
    private StudentLatLng position;
    private String email;
    private String gender;
    private String major;
    private String dateOfBirth;
    private String description;
    private String phoneNumber;
    private boolean online;
    private String imageCode;



    public Student(){

    }

    public Student(String name, StudentLatLng position, String email, String gender, String major, String dateOfBirth, String description, String phoneNumber, boolean online) {
        this.name = name;
        this.position = position;
        this.email = email;
        this.gender = gender;
        this.major = major;
        this.dateOfBirth = dateOfBirth;
        this.description = description;
        this.phoneNumber = phoneNumber;
        this.online = online;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StudentLatLng getPosition() {
        return position;
    }

    public void setPosition(StudentLatLng position) {
        this.position = position;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getImageCode() {
        return imageCode;
    }

    public void setImageCode(String imageCode) {
        this.imageCode = imageCode;
    }
}
