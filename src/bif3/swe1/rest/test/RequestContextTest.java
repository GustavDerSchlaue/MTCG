package bif3.swe1.rest.test;

import javafx.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static bif3.swe1.rest.classes.RequestContext.handleRequest;
import static org.junit.jupiter.api.Assertions.*;

class RequestContextTest {

    @Test
    void testPOST() {
        ArrayList<String> messages = new ArrayList<>();
        Pair<ArrayList<String>,String> output = handleRequest("POST","/messages","First message", "", "", messages);
        String[] test = {"First message"};
        assertArrayEquals(test,output.getKey().toArray());
        assertTrue(output.getValue().contains("ID: 1"));
    }

    @Test
    void testGETAllEmpty() {
        ArrayList<String> messages = new ArrayList<>();
        Pair<ArrayList<String>,String> output = handleRequest("GET","/messages","", "", "", messages);
        String[] test = {};
        assertArrayEquals(test,output.getKey().toArray());
    }

    @Test
    void testGETAllNotEmpty() {
        ArrayList<String> messages = new ArrayList<>();
        messages.add("First message");
        messages.add("Second message");
        Pair<ArrayList<String>,String> output = handleRequest("GET","/messages","", "", "", messages);
        String[] test = {"First message", "Second message"};
        assertArrayEquals(test,output.getKey().toArray());
    }

    @Test
    void testGETFirst() {
        ArrayList<String> messages = new ArrayList<>();
        messages.add("First message");
        messages.add("Second message");
        Pair<ArrayList<String>,String> output = handleRequest("GET","/messages/1","", "", "", messages);
        assertTrue(output.getValue().contains("First message"));
    }

    @Test
    void testGETThird() {
        ArrayList<String> messages = new ArrayList<>();
        messages.add("First message");
        messages.add("Second message");
        messages.add("Third message");
        Pair<ArrayList<String>,String> output = handleRequest("GET","/messages/3","", "", "", messages);
        assertTrue(output.getValue().contains("Third message"));
    }

    @Test
    void testPUT() {
        ArrayList<String> messages = new ArrayList<>();
        messages.add("First message");
        messages.add("Second message");
        messages.add("Third message");
        Pair<ArrayList<String>,String> output = handleRequest("PUT","/messages/2","Penultimate message", "", "", messages);
        assertTrue(output.getValue().contains("Change successful"));

        Pair<ArrayList<String>,String> output2 = handleRequest("GET","/messages","", "", "", messages);
        String[] test = {"First message", "Penultimate message", "Third message"};
        assertArrayEquals(test,output2.getKey().toArray());
    }

    @Test
    void testDELETE() {
        ArrayList<String> messages = new ArrayList<>();
        messages.add("First message");
        messages.add("Second message");
        messages.add("Third message");
        Pair<ArrayList<String>,String> output = handleRequest("DELETE","/messages/2","", "", "", messages);
        assertTrue(output.getValue().contains("Removal successful"));

        Pair<ArrayList<String>,String> output2 = handleRequest("GET","/messages","", "", "", messages);
        String[] test = {"First message", "Third message"};
        assertArrayEquals(test,output2.getKey().toArray());
    }

    @Test
    void testError() {
        ArrayList<String> messages = new ArrayList<>();
        messages.add("First message");
        messages.add("Second message");
        messages.add("Third message");
        Pair<ArrayList<String>,String> output = handleRequest("DELETE","/messages/4","", "", "", messages);
        assertTrue(output.getValue().contains("id not found"));

        Pair<ArrayList<String>,String> output2 = handleRequest("GET","/messages","", "", "", messages);
        String[] test = {"First message", "Second message", "Third message"};
        assertArrayEquals(test,output2.getKey().toArray());
    }
}