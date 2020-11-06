package com.example.bookbank;

import android.widget.EditText;
import android.widget.ListView;

import com.example.bookbank.activities.SearchUsernameActivity;

import androidx.test.rule.ActivityTestRule;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.platform.app.InstrumetationRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test class for SearchUsernameActivity.
 */

public class SearchUserTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<SearchUsernameActivity> rule =
            new ActivityTestRule<>(SearchUsernameActivity.class, true, true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void checkList() {
        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", SearchUsernameActivity.class);
        //Get view for EditText and enter a keyword to search
        solo.enterText((EditText) solo.getView(R.id.search_user_field), "testemail");
        solo.clickOnButton("search_user_button"); //Select Search Username Button
        solo.clearEditText((EditText) solo.getView(R.id.search_user_field)); //Clear the EditText
        /* True if there is a text: testemail@email.com on the screen, wait at least 2 seconds and find
        minimum one match. */
        assertTrue(solo.waitForText("Test", 1, 2000));


        //Get view for EditText and enter a keyword to search
        solo.enterText((EditText) solo.getView(R.id.search_user_field), "efimoff");
        solo.clickOnButton("search_user_button"); //Select Search Username Button
        solo.clearEditText((EditText) solo.getView(R.id.search_user_field)); //Clear the EditText
        //True if there is no text: testemail@email.com on the screen
        assertFalse(solo.searchText("testemail@email.com"));
    }

    @Test
    public void checkUserItem(){
        solo.assertCurrentActivity("Wrong Activity", SearchUsernameActivity.class);
        solo.enterText((EditText) solo.getView(R.id.search_user_field), "testemail");
        solo.clickOnButton("search_user_button"); //Select Search Username Button
        solo.clearEditText((EditText) solo.getView(R.id.search_user_field)); //Clear the EditText
        solo.waitForText("testemail@email.com", 1, 2000);
        // Get SearchUsernameActivity to access its variables and methods.
        SearchUsernameActivity activity = (SearchUsernameActivity) solo.getCurrentActivity();
        final ListView userList = activity.userList; // Get the listview
        String user = (String) userList.getItemAtPosition(0); // Get item from first position
        assertEquals("Test", user);
    }

    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }



}
