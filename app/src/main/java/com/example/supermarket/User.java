package com.example.supermarket;

public class User {
    private static int userID;
    private static int aID;
    public User() {

    }

    public User(int userID){
        this.userID = userID;
    }

    public int getUserID() {
        return this.userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getAID(){
        return this.aID;
    }

    public void setAID(int aID){
        this.aID= aID;
    }

    public boolean isAdmin(){
         if (this.aID ==1) {
             return true;
         }
         else
             return false;
    }
}
