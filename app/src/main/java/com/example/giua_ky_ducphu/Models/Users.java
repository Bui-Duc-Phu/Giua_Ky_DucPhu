package com.example.giua_ky_ducphu.Models;


public class Users {
    private String userName;
    private String userID;
    private String email;
    private String typeAccount;
    private String password;

    public Users() {
    }

    public Users(String userName, String userID, String email, String typeAccount, String password) {
        this.userName = userName;
        this.userID = userID;
        this.email = email;
        this.typeAccount = typeAccount;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTypeAccount() {
        return typeAccount;
    }

    public void setTypeAccount(String typeAccount) {
        this.typeAccount = typeAccount;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Users{" +
                "userName='" + userName + '\'' +
                ", userID='" + userID + '\'' +
                ", email='" + email + '\'' +
                ", typeAccount='" + typeAccount + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}

