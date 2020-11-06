package com.example.bookbank;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.bookbank.activities.OwnerBooksActivity;
import com.example.bookbank.models.Book;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FilterStatusTestOwner {
    private Solo solo;
    @Rule
    public ActivityTestRule<OwnerBooksActivity> rule =
            new ActivityTestRule<>(OwnerBooksActivity.class, true, true);

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
        solo.assertCurrentActivity("Wrong Activity", OwnerBooksActivity.class);
        solo.pressSpinnerItem(0, 0);
        Boolean actual = solo.isSpinnerTextSelected(0, "Show All");
        assertEquals("NOT FOUND ERROR", true, actual);
        solo.clickOnButton("FILTER");

        OwnerBooksActivity activity0 = (OwnerBooksActivity) solo.getCurrentActivity();
        final ListView bookList0 = activity0.bookList;

        int showAllCount = 0;

        for (int i = 0; i < bookList0.getCount(); i++){
            Book tempBook = (Book) bookList0.getItemAtPosition(i);
            if (tempBook.getStatus().equals("Borrowed") || tempBook.getStatus().equals("Available")){
                showAllCount += 1;
            }
        }
        Log.d("tag", "HERE "+ String.valueOf(showAllCount));
        assertTrue(showAllCount > 0);
    }

    @Test
    public void checkStatusAvailable(){
        //Log.d("tag" , "Tes333t");
        solo.assertCurrentActivity("Wrong Activity", OwnerBooksActivity.class);
        solo.pressSpinnerItem(0, 1);
        Boolean actual = solo.isSpinnerTextSelected(0, "Available");
        assertEquals("NOT FOUND ERROR", true, actual);
        solo.clickOnButton("FILTER");

        OwnerBooksActivity activity0 = (OwnerBooksActivity) solo.getCurrentActivity();
        final ListView bookList0 = activity0.bookList;

        int AvailableCount = 0;

        for (int i = 0; i < bookList0.getCount(); i++){
            Book tempBook = (Book) bookList0.getItemAtPosition(i);
            if (tempBook.getStatus().equals("Available")){
                Log.d("tag", "HERE "+ String.valueOf(AvailableCount));
                AvailableCount += 1;
            }
        }
        Log.d("tag", "HERE44"+ String.valueOf(AvailableCount));

        //Since there is 0 available books at the current time of testing
        assertTrue(AvailableCount == 0);
    }

    @Test
    public void checkStatusBorrowed(){
        //Log.d("tag" , "Tes333t");
        solo.assertCurrentActivity("Wrong Activity", OwnerBooksActivity.class);
        solo.pressSpinnerItem(0, 2);
        Boolean actual = solo.isSpinnerTextSelected(0, "Borrowed");
        assertEquals("NOT FOUND ERROR", true, actual);
        solo.clickOnButton("FILTER");

        OwnerBooksActivity activity0 = (OwnerBooksActivity) solo.getCurrentActivity();
        final ListView bookList0 = activity0.bookList;

        int BorrowedCount = 0;

        for (int i = 0; i < bookList0.getCount(); i++){
            Book tempBook = (Book) bookList0.getItemAtPosition(i);
            if (tempBook.getStatus().equals("Borrowed")){
                Log.d("tag", "HERE "+ String.valueOf(BorrowedCount));
                BorrowedCount += 1;
            }
        }
        Log.d("tag", "HERE44"+ String.valueOf(BorrowedCount));

        //Since there is 1 borrowed books at the current time of testing
        assertTrue(BorrowedCount > 0);
    }

    @Test
    public void checkStatusRequested(){
        //Log.d("tag" , "Tes333t");
        solo.assertCurrentActivity("Wrong Activity", OwnerBooksActivity.class);
        solo.pressSpinnerItem(0, 3);
        Boolean actual = solo.isSpinnerTextSelected(0, "Requested");
        assertEquals("NOT FOUND ERROR", true, actual);
        solo.clickOnButton("FILTER");

        OwnerBooksActivity activity0 = (OwnerBooksActivity) solo.getCurrentActivity();
        final ListView bookList0 = activity0.bookList;

        int RequestedCount = 0;

        for (int i = 0; i < bookList0.getCount(); i++){
            Book tempBook = (Book) bookList0.getItemAtPosition(i);
            if (tempBook.getStatus().equals("Requested")){
                Log.d("tag", "HERE "+ String.valueOf(RequestedCount));
                RequestedCount += 1;
            }
        }
        Log.d("tag", "HERE44"+ String.valueOf(RequestedCount));

        //Since there is no requested books at the current time of testing
        assertTrue(RequestedCount == 0);
    }

    @Test
    public void checkStatusAccepted(){
        //Log.d("tag" , "Tes333t");
        solo.assertCurrentActivity("Wrong Activity", OwnerBooksActivity.class);
        solo.pressSpinnerItem(0, 4);
        Boolean actual = solo.isSpinnerTextSelected(0, "Accepted");
        assertEquals("NOT FOUND ERROR", true, actual);
        solo.clickOnButton("FILTER");

        OwnerBooksActivity activity0 = (OwnerBooksActivity) solo.getCurrentActivity();
        final ListView bookList0 = activity0.bookList;

        int AcceptedCount = 0;

        for (int i = 0; i < bookList0.getCount(); i++){
            Book tempBook = (Book) bookList0.getItemAtPosition(i);
            if (tempBook.getStatus().equals("Accepted")){
                Log.d("tag", "HERE "+ String.valueOf(AcceptedCount));
                AcceptedCount += 1;
            }
        }
        Log.d("tag", "HERE44"+ String.valueOf(AcceptedCount));

        //Since there is no requested books at the current time of testing
        assertTrue(AcceptedCount == 0);
    }
}
