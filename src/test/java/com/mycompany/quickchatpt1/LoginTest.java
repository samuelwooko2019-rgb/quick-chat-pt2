/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.quickchatpt1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * LoginTest.java
 *
 * Unit tests for Part 1.
 *
 * @st10444847
 */
public class LoginTest {

    private Login login;

    @BeforeEach
    public void setUp() {
        login = new Login();
    }

    @Test
    public void testCheckUserName_CorrectlyFormatted() {
        login.setUsername("kyl_1");
        assertTrue(login.checkUserName());
    }

    @Test
    public void testCheckUserName_IncorrectlyFormatted() {
        login.setUsername("kyle!!!!!!!");
        assertFalse(login.checkUserName());
    }

    @Test
    public void testCheckPasswordComplexity_MeetsRequirements() {
        login.setPassword("Ch&&sec@ke99!");
        assertTrue(login.checkPasswordComplexity());
    }

    @Test
    public void testCheckPasswordComplexity_DoesNotMeetRequirements() {
        login.setPassword("password");
        assertFalse(login.checkPasswordComplexity());
    }

    @Test
    public void testCheckCellPhoneNumber_CorrectlyFormatted() {
        login.setCellNumber("+27838968976");
        assertTrue(login.checkCellPhoneNumber());
    }

    @Test
    public void testCheckCellPhoneNumber_IncorrectlyFormatted() {
        login.setCellNumber("08966553");
        assertFalse(login.checkCellPhoneNumber());
    }

    @Test
    public void testCellPhoneMessage_Success() {
        login.setCellNumber("+27838968976");
        assertEquals("Cell number successfully captured.", login.checkCellPhoneNumberMessage());
    }

    @Test
    public void testCellPhoneMessage_Failure() {
        login.setCellNumber("08966553");
        assertEquals("Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.", login.checkCellPhoneNumberMessage());
    }

    @Test
    public void testRegisterUser_UsernameIncorrectlyFormatted() {
        login.setUsername("kyle!!!!!!!");
        login.setPassword("Ch&&sec@ke99!");
        assertEquals("Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.", login.registerUser());
    }

    @Test
    public void testRegisterUser_PasswordDoesNotMeetComplexity() {
        login.setUsername("kyl_1");
        login.setPassword("password");
        assertEquals("Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.", login.registerUser());
    }

    @Test
    public void testRegisterUser_Success() {
        login.setUsername("kyl_1");
        login.setPassword("Ch&&sec@ke99!");
        String result = login.registerUser();
        assertTrue(result.contains("You have successfully registered."));
        assertTrue(login.isRegistered());
    }

    @Test
    public void testLoginUser_Successful() {
        login = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        login.registerUser();
        assertTrue(login.loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    public void testLoginUser_Failed() {
        login = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        login.registerUser();
        assertFalse(login.loginUser("kyl_1", "wrongPassword"));
    }

    @Test
    public void testReturnLoginStatus_Success() {
        login = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        login.registerUser();
        boolean success = login.loginUser("kyl_1", "Ch&&sec@ke99!");
        assertEquals("Welcome Kyle, Smith it is great to see you again.", login.returnLoginStatus(success));
    }

    @Test
    public void testReturnLoginStatus_Failure() {
        login = new Login("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        login.registerUser();
        boolean success = login.loginUser("kyl_1", "wrongPassword");
        assertEquals("Username or password incorrect, please try again.", login.returnLoginStatus(success));
    }
}
