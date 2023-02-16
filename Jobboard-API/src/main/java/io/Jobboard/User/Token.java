package io.Jobboard.User;

import java.lang.Math;

public class Token {

    private String token;

    Token(String token){
        this.token = token;
    }

    public static String genToken(){

        String token = "";

        for (int i = 0; i < 16; i++){

            int currentCharVal = 65;
            currentCharVal += (Math.random()*100 % 26);
            token += (char)currentCharVal;

        }

        return token;

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
