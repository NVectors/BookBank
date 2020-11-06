package com.example.bookbank;

import com.example.bookbank.models.Book;
import com.example.bookbank.models.BookPhotograph;
import com.example.bookbank.models.Notification;
import com.example.bookbank.models.Request;
import com.example.bookbank.models.User;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BookBankModelTests {
    /*
    * Test Book Model setters and getters
    * */
    @Test
    public void testBookGettersAndSetters() {
        Book book = new Book(
                "xyz",
                "The Hobbit",
                "Tolken",
                Long.parseLong("1325675687699"),
                "hobbit goes on adventure",
                "Available",
                "def",
                "abc"
        );
        // test getters
        assertEquals(book.getId(), "xyz");
        assertEquals(book.getTitle(), "The Hobbit");
        assertEquals(book.getAuthor(), "Tolken");
        assertEquals(book.getIsbn().toString(),"1325675687699");
        assertEquals(book.getDescription(), "hobbit goes on adventure");
        assertEquals(book.getStatus(), "Available");
        assertEquals(book.getOwnerId(), "def");
        assertEquals(book.getBorrowerId(), "abc");

        // test setters
        book.setTitle("Harry Potter");
        book.setAuthor("Harriet");
        book.setIsbn(Long.parseLong("1245754567875"));
        book.setDescription("kid is a wizard");
        book.setStatus("Requested");
        book.setOwnerId("yer");
        book.setBorrowerId("tomre");
        assertEquals(book.getId(), "xyz");
        assertEquals(book.getTitle(), "Harry Potter");
        assertEquals(book.getAuthor(), "Harriet");
        assertEquals(book.getIsbn().toString(),"1245754567875");
        assertEquals(book.getDescription(), "kid is a wizard");
        assertEquals(book.getStatus(), "Requested");
        assertEquals(book.getOwnerId(), "yer");
        assertEquals(book.getBorrowerId(), "tomre");
    }

    /*
     * Test Book Photograph Model setters and getters
     * */
    @Test
    public void testBookPhotographGettersAndSetters() {
        BookPhotograph photo = new BookPhotograph(
                "xyz",
                "Https:someAddress.com",
                "abc"
        );
        // test getters
        assertEquals(photo.getId(), "xyz");
        assertEquals(photo.getImageSourceUrl(), "Https:someAddress.com");
        assertEquals(photo.getBookId(), "abc");

        // test setters
        photo.setId("hji");
        photo.setImageSourceUrl("http:google.com");
        photo.setBookId("cvb");
        assertEquals(photo.getId(), "hji");
        assertEquals(photo.getImageSourceUrl(), "http:google.com");
        assertEquals(photo.getBookId(), "cvb");
    }

    /*
     * Test Notification Model setters and getters
     * */
    @Test
    public void testNotificationGettersAndSetters() {
        Notification notification = new Notification(
                "xyz",
                "someone borrowed your book"
        );
        // test getters
        assertEquals(notification.getId(), "xyz");
        assertEquals(notification.getMessage(), "someone borrowed your book");

        // test setters
        notification.setId("hji");
        notification.setMessage("new message");
        assertEquals(notification.getId(), "hji");
        assertEquals(notification.getMessage(), "new message");
    }

    /*
     * Test Request Model setters and getters
     * */
    @Test
    public void testRequestGettersAndSetters() {
        Request request = new Request(
                "xyz",
                "abc",
                "cde",
                "jkl",
                "Requested",
                123.45,
                12.34
        );
        // test getters
        assertEquals(request.getId(), "xyz");
        assertEquals(request.getBookId(), "abc");
        assertEquals(request.getRequesterId(), "cde");
        assertEquals(request.getOwnerId(), "jkl");
        assertEquals(request.getStatus(), "Requested");
        assertEquals(request.getLatitude(), 123.45, 0);
        assertEquals(request.getLongitude(), 12.34, 0);

        // test setters
        request.setId("abc");
        request.setBookId("hui");
        request.setRequesterId("cvb");
        request.setOwnerId("lou");
        request.setStatus("Approved");
        request.setLatitude(66.7);
        request.setLongitude(87.67);

        assertEquals(request.getId(), "abc");
        assertEquals(request.getBookId(), "hui");
        assertEquals(request.getRequesterId(), "cvb");
        assertEquals(request.getOwnerId(), "lou");
        assertEquals(request.getStatus(), "Approved");
        assertEquals(request.getLatitude(), 66.7, 0);
        assertEquals(request.getLongitude(), 87.67, 0);
    }

    /*
     * Test User Model setters and getters
     * */
    @Test
    public void testUserGettersAndSetters() {
        User user = new User(
                "xyz",
                "temp@gmail.com",
                "pass1234",
                "james berkley",
                "54 woods street",
                "7801231234"
        );
        // test getters
        assertEquals(user.getId(), "xyz");
        assertEquals(user.getEmail(), "temp@gmail.com");
        assertEquals(user.getPassword(), "pass1234");
        assertEquals(user.getFullname(), "james berkley");
        assertEquals(user.getAddress(), "54 woods street");
        assertEquals(user.getPhoneNumber(), "7801231234");

        // test setters
        user.setId("abc");
        user.setEmail("new@gmail.com");
        user.setPassword("pass9867");
        user.setFullname("Tim Brown");
        user.setAddress("Declined");
        user.setPhoneNumber("7809876123");

        assertEquals(user.getId(), "abc");
        assertEquals(user.getEmail(), "new@gmail.com");
        assertEquals(user.getPassword(), "pass9867");
        assertEquals(user.getFullname(), "Tim Brown");
        assertEquals(user.getAddress(), "Declined");
        assertEquals(user.getPhoneNumber(), "7809876123");
    }
}
