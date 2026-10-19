/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.quickchatpt1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Message.java
 *
 * Represents one QuickChat message (PROG - Part 2). It creates the
 * message ID and hash validates the recipient and message length, lets the
 * user send, store or disregard the message and stores messages as JSON.
 *
 * Reference for saving objects to a JSON file with Gson (storeMessage method):
 * Baeldung (2024) 'Save Data to a JSON File with Gson'. Available at:
 * https://www.baeldung.com/gson-save-file (Accessed: 19 October 2026).
 * Adapted to write the list of stored messages with pretty printing.
 *
 *Samuel Wooko
 */
public final class Message {

    private static final int MAX_MESSAGE_LENGTH = 250;
    private static final int MESSAGE_ID_LENGTH = 10;
    private static final Random RANDOM = new Random();

    // Lists that hold the messages program is running.
    private static final List<Message> sentMessages = new ArrayList<>();
    private static final List<Message> disregardedMessages = new ArrayList<>();
    private static final List<Message> storedMessages = new ArrayList<>();
    private static int totalMessagesSent = 0;
    private static String storagePath = "messages.json";

    // Details of this message (these are the fields written to the JSON file).
    private final String messageId;
    private final int messageNumber;
    private final String recipient;
    private final String messageText;
    private String messageHash;

    /**
     * Creates a message with a randomly generated ten-digit message ID.
     */
    public Message(int messageNumber, String recipient, String messageText) {
        this(generateMessageId(), messageNumber, recipient, messageText);
    }

    /**
     * (used for testing the hash).
     */
    public Message(String messageId, int messageNumber, String recipient, String messageText) {
        this.messageId = messageId;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText == null ? "" : messageText;
        this.messageHash = createMessageHash();
    }

    // -- Message ID ----------

    /**
     * Builds a ten-digit ID one random digit at a time.ID is kept as a String and not a number.
     */
    private static String generateMessageId() {
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < MESSAGE_ID_LENGTH; i++) {
            id.append(RANDOM.nextInt(10));
        }
        return id.toString();
    }

    /**
     * Checks that the message ID is made of digits and is no more than ten
     * characters long.
     */
    public Boolean checkMessageID() {
        return messageId != null
                && !messageId.isEmpty()
                && messageId.length() <= MESSAGE_ID_LENGTH
                && messageId.matches("\\d+");
    }

    /**
     * Returns the message shown after the ID has been created.
     */
    public String getMessageIDMessage() {
        return "Message ID generated: " + messageId;
    }

    // -- Recipient andmessage length -----------

    /**
     * Checks a recipie number by reusing the Part 1 cell phone check.
     */
    public static boolean isValidRecipient(String cellNumber) {
        Login phoneChecker = new Login();
        phoneChecker.setCellNumber(cellNumber);
        return phoneChecker.checkCellPhoneNumber();
    }

    /**
     * Returns the success or failure message for a recipient phone number.
     */
    public static String checkRecipientCell(String cellNumber) {
        if (isValidRecipient(cellNumber)) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.";
    }

    /**
      Checks this message's recipient cell number.
     */
    public String checkRecipientCell() {
        return checkRecipientCell(recipient);
    }

    /**
     * Returns the success or failure message for a message's length. A message
     * of 250 characters or fewer is accepted.
     */
    public static String checkMessageLength(String text) {
        int length = text == null ? 0 : text.length();
        if (length <= MAX_MESSAGE_LENGTH) {
            return "Message ready to send.";
        }
        return "Message exceeds 250 characters by " + (length - MAX_MESSAGE_LENGTH)
                + "; please reduce the size.";
    }

    /**
     * Checks the length of this message.
     */
    public String checkMessageLength() {
        return checkMessageLength(messageText);
    }

    /**
     * Returns true if the text is not longer than 250 characters.
     */
    public static boolean isMessageLengthValid(String text) {
        return text != null && text.length() <= MAX_MESSAGE_LENGTH;
    }

    // ---------------- Message hash ----------------

    /**
     * Creates the message hash: the first two digits of the message ID, the
     * message number, then the first and last words of the message, all in
     * capital letters. Example: 00:0:HITONIGHT
     */
    public String createMessageHash() {
        String firstTwoDigits = messageId.length() >= 2 ? messageId.substring(0, 2) : messageId;
        String[] words = messageText.trim().split("\\s+");
        String firstWord = removePunctuation(words[0]);
        String lastWord = removePunctuation(words[words.length - 1]);
        messageHash = (firstTwoDigits + ":" + messageNumber + ":" + firstWord + lastWord).toUpperCase();
        return messageHash;
    }

    /**
     * Removes anything that is not a letter or digit (for example the "?" in
     * "tonight?") so that only the word itself ends up in the hash.
     */
    private static String removePunctuation(String word) {
        return word.replaceAll("[^A-Za-z0-9]", "");
    }

    // ---------------- Send, store or disregard ----------------

    /**
     * ccarries out the user's choice for this message:
     * 1 = send, 2 = disregard, 3 = store to send later.
     */
    public String sentMessage(int choice) {
        switch (choice) {
            case 1:
                sentMessages.add(this);
                totalMessagesSent++;
                return "Message successfully sent.";
            case 2:
                disregardedMessages.add(this);
                return "Press 0 to delete the message.";
            case 3:
                if (storeMessage()) {
                    return "Message successfully stored.";
                }
                return "Message could not be stored.";
            default:
                return "Invalid choice; please select 1, 2 or 3.";
        }
    }

    /**
     * Stores this message and writes all stored messages to the JSON file.
     * Returns true if the file was written successfully.
     */
    public boolean storeMessage() {
        if (!storedMessages.contains(this)) {
            storedMessages.add(this);
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(storagePath))) {
            gson.toJson(storedMessages, writer);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // ---------------- Reports ----------------

    /**
     * Returns the details of this message in the required order:
     * Message ID, Message Hash, Recipient, Message.
     */
    public String getMessageDetails() {
        return "Message ID: " + messageId + "\n"
                + "Message Hash: " + messageHash + "\n"
                + "Recipient: " + recipient + "\n"
                + "Message: " + messageText;
    }

    /**
     * Returns all the messages sent while the program has been running.
     */
    public static String printMessages() {
        if (sentMessages.isEmpty()) {
            return "No messages have been sent yet.";
        }
        StringBuilder report = new StringBuilder();
        for (Message message : sentMessages) {
            report.append(message.getMessageDetails()).append("\n\n");
        }
        return report.toString().trim();
    }

    /**
     * Returns the total number of messages sent.
     */
    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    // ---------------- Session helpers ----------------

    /**
     * Clears every list and the sent counter (used to start each unit test
     * from a clean state).
     */
    public static void clearSession() {
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        totalMessagesSent = 0;
    }

    /**
     * Changes where the JSON file is saved (unit tests use a temporary file).
     */
    public static void setStoragePath(String path) {
        storagePath = path;
    }

    // ---------------- Getters ----------------

    public String getMessageId() {
        return messageId;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getMessageHash() {
        return messageHash;
    }

    public static List<Message> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public static List<Message> getDisregardedMessages() {
        return new ArrayList<>(disregardedMessages);
    }

    public static List<Message> getStoredMessages() {
        return new ArrayList<>(storedMessages);
    }
}
