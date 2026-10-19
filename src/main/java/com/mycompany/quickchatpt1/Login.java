/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.quickchatpt1;

import java.util.regex.Pattern;

/**
 * Login.java
 *
 * Handles registr and login validation for the QuickChat application
 * ( Part 1).
 *
 * Reference for cell phone regular expression approach:
 * GeeksforGeeks (2023) 'Regular Expressions in Java to Extract Words from a
 * given String'. Available at: https://www.geeksforgeeks.org/regular-expressions-in-java/
 * (Accessed: 16 September 2026). Adapted here to validate an international
 * cell phone number format rather than to extract words.
 *
 * @st10444847 samuel wooko
 */
public class Login {

    private String username;
    private String password;
    private String cellNumber;
    private String firstName;
    private String lastName;
    private boolean registered = false;

    private static final Pattern PASSWORD_UPPERCASE = Pattern.compile(".*[A-Z].*");
    private static final Pattern PASSWORD_DIGIT = Pattern.compile(".*[0-9].*");
    private static final Pattern PASSWORD_SPECIAL = Pattern.compile(".*[^a-zA-Z0-9].*");
    private static final Pattern CELLPHONE_PATTERN = Pattern.compile("^\\+\\d{1,3}\\d{1,10}$");

    public Login() {
    }

    public Login(String firstName, String lastName, String username, String password, String cellNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.cellNumber = cellNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCellNumber() {
        return cellNumber;
    }

    public void setCellNumber(String cellNumber) {
        this.cellNumber = cellNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isRegistered() {
        return registered;
    }

    /**
     * Checks that the usernames must contains an underscore and is no more than
     * five chtrs long.
     */
    public Boolean checkUserName() {
        if (username == null) {
            return false;
        }
        return username.length() <= 5 && username.contains("_");
    }

    /**
     * Checks that the password is at least eight chts long and
     * contains a capital letter, a number, and a special chtr.
     */
    public Boolean checkPasswordComplexity() {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasUpper = PASSWORD_UPPERCASE.matcher(password).matches();
        boolean hasDigit = PASSWORD_DIGIT.matcher(password).matches();
        boolean hasSpecial = PASSWORD_SPECIAL.matcher(password).matches();
        return hasUpper && hasDigit && hasSpecial;
    }

    /**
     * Checks that the cell phone number containsan international country
     * code and is correctly formatted using a regular expression(+27).
     */
    public Boolean checkCellPhoneNumber() {
        if (cellNumber == null) {
            return false;
        }
        return CELLPHONE_PATTERN.matcher(cellNumber).matches();
    }

    /**
     * Returns the message describing whether the cell phone number was
     * captured successfully. Kept separate from registerUser() because the
     * assignment brief scopes registerUser() to username and password only.
     */
    public String checkCellPhoneNumberMessage() {
        if (checkCellPhoneNumber()) {
            return "Cell number successfully captured.";
        }
        return "Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.";
    }

    /**
     * Validates the username and password, and returns the appropriate
     * registration message. If both are valid, the account details are
     * considered registered.
     */
    public String registerUser() {
        if (!checkUserName()) {
            return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
        }
        if (!checkPasswordComplexity()) {
            return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        }
        registered = true;
        return "Username successfully captured.\nPassword successfully captured.\nYou have successfully registered.";
    }

    /**
     * Verifies   the supplied login details match the details stored
     * when the use registered.
     */
    public Boolean loginUser(String enteredUsername, String enteredPassword) {
        if (!registered || username == null || password == null) {
            return false;
        }
        return username.equals(enteredUsername) && password.equals(enteredPassword);
    }

    /**
     * Returns 
     */
    public String returnLoginStatus(boolean loginSuccessful) {
        if (loginSuccessful) {
            return "Welcome " + firstName + ", " + lastName + " it is great to see you again.";
        }
        return "Username or password incorrect, please try again.";
    }
}
