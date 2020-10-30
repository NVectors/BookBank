package com.example.bookbank.helperClasses;

import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.sql.Struct;

public class InputValidator {

    public static boolean validateInputs(boolean[] inputs) {
        boolean valid = true;
        for (boolean i : inputs) {
            valid = valid && i;
        }
        return valid;
    }

    public static boolean phoneNumber(EditText box, TextView errorBox) {
        String regex = "^((\\+\\d{1,2}|1)[\\s.-]?)?\\(?[2-9](?!11)\\d{2}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$|^$";
        boolean test = box.getText().toString().matches(regex);
        if (!test) {
            // if empty, set error message to warn the user
            errorBox.setText("Field must be a valid phone number format");
        } else {
            errorBox.setText("");
        }
        return test;
    }

    public static boolean fieldsMatch(EditText box1, EditText box2, TextView errorBox) {
        boolean test = box1.getText().toString().equals(box2.getText().toString());
        if (!test) {
            // if empty, set error message to warn the user
            errorBox.setText("Password don't match");
        } else {
            errorBox.setText("");
        }
        return test;
    }

    public static boolean isEmail(EditText box, TextView errorBox) {
        // regex
        // https://www.freeformatter.com/java-regex-tester.html#ad-output
        String regex = "^[-a-z0-9~!$%^&*_=+}{\\'?]+(\\.[-a-z0-9~!$%^&*_=+}{\\'?]+)*@([a-z0-9_][-a-z0-9_]*(\\.[-a-z0-9_]+)*\\.(aero|arpa|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|mobi|[a-z][a-z])|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,5})?$";
        boolean test = box.getText().toString().matches(regex);
        if (!test) {
            // if empty, set error message to warn the user
            errorBox.setText("Field must be a valid email format");
        } else {
            errorBox.setText("");
        }
        return test;
    }

    public static boolean isNumber(EditText box, TextView errorBox) {
        String regex = "^-?[0-9]+(\\.[0-9]{1,})?$";
        boolean test = box.getText().toString().matches(regex);
        if (!test) {
            // if empty, set error message to warn the user
            errorBox.setText("Field must be a number");
        } else {
            errorBox.setText("");
        }
        return test;
    }

    public static boolean isNegativeNumber(EditText box, TextView errorBox) {
        String regex = "^-[0-9]+(\\.[0-9]{1,})?$";
        boolean test = box.getText().toString().matches(regex);
        if (!test) {
            // if empty, set error message to warn the user
            errorBox.setText("Field must be a negative number");
        } else {
            errorBox.setText("");
        }
        return test;
    }

    public static boolean isPositiveNumber(EditText box, TextView errorBox) {
        String regex = "^[0-9]+(\\.[0-9]{1,})?$";
        boolean test = box.getText().toString().matches(regex);
        if (!test) {
            // if empty, set error message to warn the user
            errorBox.setText("Field must be a positive number");
        } else {
            errorBox.setText("");
        }
        return test;
    }

    public static boolean isCurrency(EditText box, TextView errorBox) {
        String regex = "^[0-9]+(\\.[0-9]{1,2})?$";
        boolean test = box.getText().toString().matches(regex);
        if (!test) {
            // if empty, set error message to warn the user
            errorBox.setText("Field must be a number, max 2 decimal places");
        } else {
            errorBox.setText("");
        }
        return test;
    }

    public static boolean notEmpty(EditText box, TextView errorBox) {
        // check to see if form field is empty
        boolean test = box.getText().toString().trim().length() != 0;
        if (!test) {
            // if empty, set error message to warn the user
            errorBox.setText("Field is required");
        } else {
            errorBox.setText("");
        }
        return test;
    }

    public static boolean isIsbn(EditText box, TextView errorBox) {
        String regex = "^[0-9]{10,13}$";
        boolean test = box.getText().toString().matches(regex);
        if (!test) {
            // if empty, set error message to warn the user
            errorBox.setText("Invalid ISBN number");
        } else {
            errorBox.setText("");
        }
        return test;
    }
}
