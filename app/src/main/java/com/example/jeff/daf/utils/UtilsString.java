package com.example.jeff.daf.utils;

/**
 * Created by Jeff on 10/06/2018.
 */

public class UtilsString {
    public static boolean stringVazia(String texto){

        if (texto == null || texto.trim().length() == 0){
            return true;
        }else{
            return false;
        }
    }
}
