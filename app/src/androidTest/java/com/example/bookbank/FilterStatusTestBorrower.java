package com.example.bookbank;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.bookbank.activities.BorrowedBooksActivity;
import com.example.bookbank.models.Book;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FilterStatusTestBorrower {
    private Solo solo;
    @Rule
    public ActivityTestRule<BorrowedBooksActivity> rule =
            new ActivityTestRule<>(BorrowedBooksActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }

    /**
     * Gets the Activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }

    @Test
    public void checkStatusShowAll(){
        //Log.d("tag" , "Tes333t");
        solo.assertCurrentActivity("Wrong Activity", BorrowedBooksActivity.class);
        solo.pressSpinnerItem(0, 0);
        Boolean actual = solo.isSpinnerTextSelected(0, "Show All");
        assertEquals("NOT FOUND ERROR", true, actual);
        solo.clickOnButton("FILTER");

        BorrowedBooksActivity activity0 = (BorrowedBooksActivity) solo.getCurrentActivity();
        final ListView bookList0 = activity0.bookList;

        int totalCount = bookList0.getCount();
        ArrayList<Book> totalBookList = new ArrayList<Book>();

        for (int i = 0; i < bookList0.getCount(); i++){
            Book tempBook = (Book) bookList0.getItemAtPosition(i);
            totalBookList.add(tempBook);
        }
        assertTrue(totalBookList.size() == totalCount);
    }

    @Test
    public void checkStatusBorrowed(){
        solo.assertCurrentActivity("Wrong Activity", BorrowedBooksActivity.class);
        solo.pressSpinnerItem(0, 1);
        Boolean actual = solo.isSpinnerTextSelected(0, "Borrowed");
        assertEquals("NOT FOUND ERROR", true, actual);
        solo.clickOnButton("FILTER");

        BorrowedBooksActivity activity0 = (BorrowedBooksActivity) solo.getCurrentActivity();
        final ListView bookList0 = activity0.bookList;
        ArrayList<Book> borrowedBookList = new ArrayList<Book>();

        int BorrowedCount = 0;
        for (int i = 0; i < bookList0.getCount(); i++){
            Book tempBook = (Book) bookList0.getItemAtPosition(i);
            if (tempBook.getStatus().equals("Borrowed")){
                borrowedBookList.add(tempBook);
                BorrowedCount += 1;
            }
        }
        assertTrue(BorrowedCount == borrowedBookList.size());
        for(int i = 0; i<borrowedBookList.size(); i ++){
            Book tempBook = (Book) bookList0.getItemAtPosition(i);
            assertTrue(tempBook.getStatus().equals("Borrowed"));
        }
    }

    @Test
    public void checkStatusRequested(){
        //Log.d("tag" , "Tes333t");
        solo.assertCurrentActivity("Wrong Activity", BorrowedBooksActivity.class);
        solo.pressSpinnerItem(0, 2);
        Boolean actual = solo.isSpinnerTextSelected(0, "Requested");
        assertEquals("NOT FOUND ERROR", true, actual);
        solo.clickOnButton("FILTER");

        BorrowedBooksActivity activity0 = (BorrowedBooksActivity) solo.getCurrentActivity();
        final ListView bookList0 = activity0.bookList;
        int RequestedCount = 0;
        ArrayList<Book> RequestBookList = new ArrayList<Book>();

        for (int i = 0; i < bookList0.getCount(); i++){
            Book tempBook = (Book) bookList0.getItemAtPosition(i);
            if (tempBook.getStatus().equals("Requested")){
                RequestedCount += 1;
                RequestBookList.add(tempBook);
            }
        }
        assertTrue(RequestedCount == RequestBookList.size());

        for (int i = 0; i < RequestBookList.size(); i ++){
            Book tempBook = (Book) bookList0.getItemAtPosition(i);
            assertTrue(tempBook.getStatus().equals("Requested"));

        }
    }

    @Test
    public void checkStatusAccepted(){
        //Log.d("tag" , "Tes333t");
        solo.assertCurrentActivity("Wrong Activity", BorrowedBooksActivity.class);
        solo.pressSpinnerItem(0, 3);
        Boolean actual = solo.isSpinnerTextSelected(0, "Accepted");
        assertEquals("NOT FOUND ERROR", true, actual);
        solo.clickOnButton("FILTER");

        BorrowedBooksActivity activity0 = (BorrowedBooksActivity) solo.getCurrentActivity();
        final ListView bookList0 = activity0.bookList;
        ArrayList<Book> AcceptBookList = new ArrayList<Book>();

        int AcceptedCount = 0;

        for (int i = 0; i < bookList0.getCount(); i++){
            Book tempBook = (Book) bookList0.getItemAtPosition(i);
            if (tempBook.getStatus().equals("Accepted")){
                AcceptBookList.add(tempBook);
                AcceptedCount += 1;
            }
        }
        assertTrue(AcceptedCount == AcceptBookList.size());

        for (int i = 0; i < AcceptBookList.size(); i ++){
            Book tempBook = (Book) bookList0.getItemAtPosition(i);
            assertTrue(tempBook.getStatus().equals("Accepted"));
        }
    }
}
