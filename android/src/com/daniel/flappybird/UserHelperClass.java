package com.daniel.flappybird;


public class UserHelperClass implements Comparable<UserHelperClass> {

    String username,email;
    int score;

    public UserHelperClass() {

    }

    public UserHelperClass(String username, String email, int score) {
        this.username = username;
        this.email = email;
        this.score= score;

    }

    public UserHelperClass(int score) {

        this.score= score;

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public int compareTo(UserHelperClass us1) {
        if(this.getScore() > us1.getScore()){
            return 1;
        } else if(this.getScore() == us1.getScore()){
            return 0;
        } else {
            return -1;
        }
    }



}