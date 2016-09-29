package com.rr.rgem.gem.controllers;

import com.rr.rgem.gem.R;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jacob on 2016/09/21.
 */

public class Validation
{
    private static int MIN_PASSWORD_LENGTH = 8;
    private static String DATE_FORMAT = "dd-MM-yyyy";
    private static String PASSWORD_CHARACTERS = "[A-Za-z0-9()-._`~@#$&*]+";
    private static String NUMERIC_CHARACTERS = "[0-9 ]+";
    private static String ALPHABETIC_CHARACTERS = "[A-Za-z ]+";
    private static String ALPHANUMERIC_CHARACTERS = "[A-Za-z0-9 ]+";
    private static String CURRENCY_CHARACTERS = "[0-9 ,.]+";
    private static Pattern NUMERIC_PATTERN = Pattern.compile("[0-9]+.[0-9]+");

    public static enum PasswordStrength
    {
        Weak, Medium, Strong
    }


    public static boolean isValidMobile(String phone)
    {
        if(isEmpty(phone))
            return false;
        if (phone.length() < 10 || phone.length() > 14)
            return false;
        if (!isNumeric(phone))
            return false;
        return true;
    }

    public static boolean isValidDate(String date)
    {
        if(isEmpty(date))
            return false;

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(date.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    public static boolean isValidPassword(String password)
    {
        if(isEmpty(password))
            return false;
        if (password.length() < MIN_PASSWORD_LENGTH)
            return false;

        Pattern pattern = Pattern.compile(PASSWORD_CHARACTERS);;
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }


    public static PasswordStrength getPasswordStrength(String password)
    {
        int score = 0;
        if(isEmpty(password))
            return null;
        if (password.length() >= 10)
            ++score;
        if (password.matches(".*[0-9]+.*"))
            ++score;
        if (password.matches(".*[a-z]+.*"))
            ++score;
        if (password.matches(".*[A-Z]+.*"))
            ++score;
        if (password.matches(".*[()-._`~@#$&*]+.*"))
            ++score;

        if (score <= 3)
            return PasswordStrength.Weak;
        else if (score == 4)
            return PasswordStrength.Medium;
        else if (score == 5)
            return PasswordStrength.Strong;
        else
            return null;
    }

    public static boolean isNumeric (String phrase)
    {
        if(isEmpty(phrase))
            return false;

        Pattern pattern = Pattern.compile(NUMERIC_CHARACTERS);;
        Matcher matcher = pattern.matcher(phrase);
        return matcher.matches();
    }

    public static boolean isAlphabetic (String phrase)
    {
        if(isEmpty(phrase))
            return false;

        Pattern pattern = Pattern.compile(ALPHABETIC_CHARACTERS);;
        Matcher matcher = pattern.matcher(phrase);
        return matcher.matches();
    }

    public static boolean isAlphaNumeric (String phrase)
    {
        if(isEmpty(phrase))
            return false;

        Pattern pattern = Pattern.compile(ALPHANUMERIC_CHARACTERS);;
        Matcher matcher = pattern.matcher(phrase);
        return matcher.matches();
    }

    public static boolean isValidCurrency(String currency)
    {
        if(isEmpty(currency))
            return false;

        Pattern pattern = Pattern.compile(CURRENCY_CHARACTERS);;
        Matcher matcher = pattern.matcher(currency);
        return matcher.matches();
    }

    public static boolean areMatching(String firstValue, String secondValue)
    {
        if(firstValue==null && secondValue == null) return true;
        if(firstValue==null && secondValue != null) return false;
        if(firstValue!=null && secondValue == null) return false;

        if (firstValue.equals(secondValue))
            return true;
        else
            return false;
    }
    public static boolean isEmpty(String value){
        return value == null || value.equals("");
    }
    public static boolean isValidNumber(String value){
        boolean result = false;

        return NUMERIC_PATTERN.matcher(value).matches();


    }
}
