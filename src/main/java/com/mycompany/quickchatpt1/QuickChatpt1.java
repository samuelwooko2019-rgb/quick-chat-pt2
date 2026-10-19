/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.quickchatpt1;

import java.util.Scanner;

/**
 * QuickChatpt1.java
 *
 * Console-only entry point for the PROG POE. Part 1: registration and
 * login. Part 2: once logged in, the user can send messages from a menu.
 * No GUI / JOptionPane is used, per the assignment requirements.
 *
 * st10444847 Samuel Wooko
 */
public class QuickChatpt1 {

    private static final int MAX_LOGIN_ATTEMPTS = 3;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Login login = registerUser(scanner);

        // Messages can only be sent after a successful login.
        if (loginUser(scanner, login)) {
            runQuickChat(scanner);
        } else {
            System.out.println("Too many failed login attempts. Exiting.");
        }

        scanner.close();
    }

    // ----- Part 1: registration and login ----------------

    /**
     * Asks for the user's details 
     */
    private static Login registerUser(Scanner scanner) {
        Login login = new Login();
        boolean registeredSuccessfully = false;

        System.out.println("=== QuickChat: Registration ===");

        while (!registeredSuccessfully) {
            System.out.print("Enter first name: ");
            login.setFirstName(scanner.nextLine());

            System.out.print("Enter last name: ");
            login.setLastName(scanner.nextLine());

            System.out.print("Enter username (must contain '_' and be <= 5 characters): ");
            login.setUsername(scanner.nextLine());

            System.out.print("Enter password (min 8 chars, 1 capital, 1 number, 1 special char): ");
            login.setPassword(scanner.nextLine());

            System.out.print("Enter cell number (e.g. +27838968976): ");
            login.setCellNumber(scanner.nextLine());

            System.out.println(login.registerUser());

            if (login.isRegistered()) {
                System.out.println(login.checkCellPhoneNumberMessage());
                registeredSuccessfully = login.checkCellPhoneNumber();
            }

            if (!registeredSuccessfully) {
                System.out.println("Please re-enter your details to try again.");
            }
        }
        return login;
    }

    /**
     * Gives the user up to three attempts to log in. Returns true if the
     * login was successful.
     */
    private static boolean loginUser(Scanner scanner, Login login) {
        System.out.println("=== QuickChat: Login ===");

        for (int attempt = 0; attempt < MAX_LOGIN_ATTEMPTS; attempt++) {
            System.out.print("Enter username: ");
            String enteredUsername = scanner.nextLine();

            System.out.print("Enter password: ");
            String enteredPassword = scanner.nextLine();

            boolean success = login.loginUser(enteredUsername, enteredPassword);
            System.out.println(login.returnLoginStatus(success));

            if (success) {
                return true;
            }
        }
        return false;
    }

    // ---------------- Part 2: menu and sending messages ----------------

    /**
     * Shows the welcome message, asks how many messages will be entered, and
     * then keeps showing the menu until the user chooses to quit.
     */
    private static void runQuickChat(Scanner scanner) {
        System.out.println("Welcome to QuickChat.");

        int messageLimit = readPositiveNumber(scanner, "How many messages do you wish to enter? ");
        int messagesEntered = 0;
        boolean running = true;

        while (running) {
            System.out.println("\nPlease choose an option:");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");

            int option = readNumber(scanner, "Option: ");

            switch (option) {
                case 1:
                    messagesEntered = sendMessages(scanner, messageLimit, messagesEntered);
                    break;
                case 2:
                    System.out.println("Coming Soon.");
                    break;
                case 3:
                    running = false;
                    System.out.println("Thank you for using QuickChat. Goodbye.");
                    break;
                default:
                    System.out.println("Invalid option; please enter 1, 2 or 3.");
            }
        }
    }

    /**
     * Lets the user enter the messages they said they would enter, no more.
     * Returns how many messages have been entered so far.
     */
    private static int sendMessages(Scanner scanner, int messageLimit, int messagesEntered) {
        if (messagesEntered >= messageLimit) {
            System.out.println("You have already entered all " + messageLimit + " messages.");
            return messagesEntered;
        }

        for (int messageNumber = messagesEntered; messageNumber < messageLimit; messageNumber++) {
            System.out.println("\n--- Message " + (messageNumber + 1) + " of " + messageLimit + " ---");

            String recipient = readRecipient(scanner);
            String text = readMessageText(scanner);

            Message message = new Message(messageNumber, recipient, text);
            System.out.println(message.getMessageIDMessage());

            int choice = readChoice(scanner);
            System.out.println(message.sentMessage(choice));

            if (choice == 1) {
                System.out.println(message.getMessageDetails());
            } else if (choice == 2) {
                String input = scanner.nextLine();
                System.out.println(input.trim().equals("0") ? "Message deleted." : "Message disregarded.");
            }
        }

        System.out.println("\n=== Messages sent ===");
        System.out.println(Message.printMessages());
        System.out.println("Total messages sent: " + Message.returnTotalMessages());
        return messageLimit;
    }

    // ---------------- Input helpers ----------------

    /**
     * Asks for the recipient until a valid international cell number is entered.
     */
    private static String readRecipient(Scanner scanner) {
        while (true) {
            System.out.print("Enter recipient cell number (e.g. +27718693002): ");
            String recipient = scanner.nextLine().trim();
            System.out.println(Message.checkRecipientCell(recipient));
            if (Message.isValidRecipient(recipient)) {
                return recipient;
            }
        }
    }

    /**
     * Asks for the message until it is not empty and not longer than 250 characters.
     */
    private static String readMessageText(Scanner scanner) {
        while (true) {
            System.out.print("Enter your message (max 250 characters): ");
            String text = scanner.nextLine();
            if (text.isBlank()) {
                System.out.println("The message cannot be empty.");
            } else {
                System.out.println(Message.checkMessageLength(text));
                if (Message.isMessageLengthValid(text)) {
                    return text;
                }
            }
        }
    }

    /**
     * Asks what to do with the message: 1 = send, 2 = disregard, 3 = store.
     */
    private static int readChoice(Scanner scanner) {
        while (true) {
            System.out.println("What would you like to do with this message?");
            System.out.println("1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message to send later");
            int choice = readNumber(scanner, "Choice: ");
            if (choice >= 1 && choice <= 3) {
                return choice;
            }
            System.out.println("Invalid choice; please select 1, 2 or 3.");
        }
    }

    /**
     * Reads a whole number, asking again if the user types something else.
     */
    private static int readNumber(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    /**
     * Reads a whole number greater than zero.
     */
    private static int readPositiveNumber(Scanner scanner, String prompt) {
        int number = readNumber(scanner, prompt);
        while (number < 1) {
            System.out.println("Please enter a number greater than zero.");
            number = readNumber(scanner, prompt);
        }
        return number;
    }
}
