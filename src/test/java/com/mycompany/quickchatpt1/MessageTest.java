/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.quickchatpt1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MessageTest.java
 *
 * Unit tests for Part 2, using the test data supplied in the PROG5121 POE brief.
 *
 * @author Samuel Wooko
 */
public class MessageTest {

    // Test data for message 1 and message 2 from the brief.
    private static final String RECIPIENT_1 = "+27718693002";
    private static final String MESSAGE_1 = "Hi Mike, can you join us for dinner tonight?";
    private static final String RECIPIENT_2 = "08575975889";
    private static final String MESSAGE_2 = "Hi Keegan, did you receive the payment?";

    // JUnit gives every test its own temporary folder, so tests never touch a real JSON file.
    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUp() {
        Message.clearSession();
        Message.setStoragePath(tempDir.resolve("messages.json").toString());
    }

    // ---------- Message length ----------

    @Test
    public void testMessageLength_Success() {
        Message message = new Message(0, RECIPIENT_1, MESSAGE_1);
        assertEquals("Message ready to send.", message.checkMessageLength());
    }

    @Test
    public void testMessageLength_Failure() {
        Message message = new Message(0, RECIPIENT_1, "a".repeat(260));
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                message.checkMessageLength());
    }

    @Test
    public void testMessageLength_Boundary() {
        assertEquals("Message ready to send.", Message.checkMessageLength("a".repeat(250)));
        assertEquals("Message exceeds 250 characters by 1; please reduce the size.",
                Message.checkMessageLength("a".repeat(251)));
    }

    // ---------- Recipient cell number ----------

    @Test
    public void testRecipientCell_Success() {
        Message message = new Message(0, RECIPIENT_1, MESSAGE_1);
        assertEquals("Cell phone number successfully captured.", message.checkRecipientCell());
    }

    @Test
    public void testRecipientCell_Failure() {
        Message message = new Message(1, RECIPIENT_2, MESSAGE_2);
        assertEquals("Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.",
                message.checkRecipientCell());
    }

    // ---------- Message hash ----------

    @Test
    public void testMessageHash_TestCase1() {
        Message message = new Message("0012345678", 0, RECIPIENT_1, MESSAGE_1);
        assertEquals("00:0:HITONIGHT", message.createMessageHash());
    }

    @Test
    public void testMessageHash_AllMessagesInLoop() {
        String[] ids = {"0012345678", "1098765432"};
        String[] recipients = {RECIPIENT_1, RECIPIENT_2};
        String[] texts = {MESSAGE_1, MESSAGE_2};
        String[] expectedHashes = {"00:0:HITONIGHT", "10:1:HIPAYMENT"};

        for (int i = 0; i < texts.length; i++) {
            Message message = new Message(ids[i], i, recipients[i], texts[i]);
            assertEquals(expectedHashes[i], message.createMessageHash());
        }
    }

    // ---------- Message ID ----------

    @Test
    public void testMessageID_IsCreated() {
        Message message = new Message(0, RECIPIENT_1, MESSAGE_1);
        assertTrue(message.checkMessageID());
        assertEquals(10, message.getMessageId().length());
        assertTrue(message.getMessageId().matches("\\d{10}"));
        assertEquals("Message ID generated: " + message.getMessageId(), message.getMessageIDMessage());
    }

    @Test
    public void testMessageID_TooLong() {
        Message message = new Message("12345678901", 0, RECIPIENT_1, MESSAGE_1);
        assertFalse(message.checkMessageID());
    }

    // ---------- Send, disregard and store ----------

    @Test
    public void testSentMessage_Send() {
        Message message = new Message(0, RECIPIENT_1, MESSAGE_1);
        assertEquals("Message successfully sent.", message.sentMessage(1));
    }

    @Test
    public void testSentMessage_Disregard() {
        Message message = new Message(1, RECIPIENT_2, MESSAGE_2);
        assertEquals("Press 0 to delete the message.", message.sentMessage(2));
    }

    @Test
    public void testSentMessage_Store() {
        Message message = new Message(0, RECIPIENT_1, MESSAGE_1);
        assertEquals("Message successfully stored.", message.sentMessage(3));
    }

    @Test
    public void testSentMessage_InvalidChoice() {
        Message message = new Message(0, RECIPIENT_1, MESSAGE_1);
        assertEquals("Invalid choice; please select 1, 2 or 3.", message.sentMessage(9));
    }

    // ---------- JSON storage ----------

    @Test
    public void testStoreMessage_WritesJsonFile() throws IOException {
        Message message = new Message("0012345678", 0, RECIPIENT_1, MESSAGE_1);
        message.sentMessage(3);

        String json = Files.readString(tempDir.resolve("messages.json"));
        assertTrue(json.contains("\"messageId\": \"0012345678\""));
        assertTrue(json.contains("\"messageHash\": \"00:0:HITONIGHT\""));
        assertTrue(json.contains(MESSAGE_1));
    }

    // ---------- Totals and printing ----------

    @Test
    public void testReturnTotalMessages_UsingBothTestMessages() {
        Message first = new Message(0, RECIPIENT_1, MESSAGE_1);
        Message second = new Message(1, RECIPIENT_2, MESSAGE_2);

        first.sentMessage(1);   // Send
        second.sentMessage(2);  // Disregard

        assertEquals(1, Message.returnTotalMessages());
    }

    @Test
    public void testPrintMessages_OnlyShowsSentMessagesInOrder() {
        Message first = new Message("0012345678", 0, RECIPIENT_1, MESSAGE_1);
        Message second = new Message("1098765432", 1, RECIPIENT_2, MESSAGE_2);
        first.sentMessage(1);
        second.sentMessage(2);

        String report = Message.printMessages();

        assertTrue(report.contains(MESSAGE_1));
        assertFalse(report.contains(MESSAGE_2));
        // Details must appear as: Message ID, Message Hash, Recipient, Message.
        int id = report.indexOf("Message ID:");
        int hash = report.indexOf("Message Hash:");
        int recipient = report.indexOf("Recipient:");
        int text = report.indexOf("Message: ");
        assertTrue(id < hash && hash < recipient && recipient < text);
    }

    @Test
    public void testPrintMessages_NothingSentYet() {
        assertEquals("No messages have been sent yet.", Message.printMessages());
    }
}
