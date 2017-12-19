package com.example.davidg.exbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.davidg.exbook.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SignInActivity";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Toast toast;

    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mSignInButton;
    private Button mSignUpButton;

    private final String FIU_EMAIL_DOMAIN = "@fiu.edu";
    private final int MIN_EMAIL_SIZE = FIU_EMAIL_DOMAIN.length();
    private final int MIN_PASSWORD_SIZE = 8;
    private final int MAX_PASSWORD_SIZE = 20;
    private final String REQUIRED = "Required"; //TODO: make it @string

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // Views
        mEmailField = findViewById(R.id.field_email);
        mPasswordField = findViewById(R.id.field_password);
        mSignInButton = findViewById(R.id.button_sign_in);
        mSignUpButton = findViewById(R.id.button_sign_up);

        // Click listeners
        mSignInButton.setOnClickListener(this);
        mSignUpButton.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }

    private void signIn() {
        Log.d(TAG, "signIn");
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(SignInActivity.this, "Sign In Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signUp() {
        Log.d(TAG, "signUp");
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(SignInActivity.this, "Sign Up Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onAuthSuccess(FirebaseUser user) {
        String username = usernameFromEmail(user.getEmail());

        // Write new user
        writeNewUser(user.getUid(), username, user.getEmail());

        // Go to MainActivity
        startActivity(new Intent(SignInActivity.this, MainActivity.class));
        finish();
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(mEmailField.getText().toString())) {
            mEmailField.setError("Required");
            result = false;
        } else {
            mEmailField.setError(null);
        }

        if (TextUtils.isEmpty(mPasswordField.getText().toString())) {
            mPasswordField.setError("Required");
            result = false;
        } else {
            mPasswordField.setError(null);
        }

        return result;
    }

    // [START basic_write]
    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);

        mDatabase.child("users").child(userId).setValue(user);
    }
    // [END basic_write]

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_sign_in) {
            signIn();
        } else if (i == R.id.button_sign_up) {
            if (checkEmailPassRequirements()){
                signUp();
            }
        }
    }

    private boolean checkEmailPassRequirements(){

        setEditingEnabled(false); //is this needed?
        final String email = mEmailField.getText().toString();
        final String password = mPasswordField.getText().toString();
        boolean validEmail = checkEmailRequirements(email);
        if(!validEmail){
            mEmailField.setError(REQUIRED);
        }
        // check password only if email is valid
        boolean validPassword = validEmail ? checkPasswordRequirements(password) : false;

        if(validEmail && !validPassword){
            mPasswordField.setError(REQUIRED);
        }

        setEditingEnabled(true); //is this needed?
        return validEmail && validPassword;
    }

    private boolean checkEmailRequirements(String email){
        int size = email.length();

        if(size > 32){
            showToast("Total length of email cannot exceeed 32 characters", Toast.LENGTH_LONG);
            return false;
        }
        else if(size <= MIN_EMAIL_SIZE){ //email contains at least 1 character
            showToast("Not a valid email.",Toast.LENGTH_LONG);
            return false;
        }
        else { // email has correct size.

            if(!email.substring(size-MIN_EMAIL_SIZE,size).equals(FIU_EMAIL_DOMAIN)){ //check that email ends in @fiu.edu
                showToast("Email must be an FIU email (@fiu.edu).",Toast.LENGTH_LONG);
                return false;
            }

            String username = email.substring(0,size-MIN_EMAIL_SIZE);
            //check that characters in the email are not special characters. Only letters and numbers allowed
            for(int i = 0; i < username.length(); i++){
                char c = username.charAt(i);
                if(!Character.isLetterOrDigit(c)){ // if it is not a letter or a digit
                    showToast("Email must contain only letters and digits.",Toast.LENGTH_LONG);
                    return false;
                }
            }

        } // end of else

        return true;
    }

    private boolean checkPasswordRequirements(String password){
        StringBuffer buffer = new StringBuffer();
        boolean hasNumber = false;
        boolean hasUpper = false;
        boolean hasLower = false;


        int size = password.length();

        if(size < MIN_PASSWORD_SIZE || size > MAX_PASSWORD_SIZE){
            showToast("Password must be between 8-20 characters long. Characters found: " + size,Toast.LENGTH_LONG);
            return false;
        }
        //check that password contains at least 1 number, 1 lower case char and 1 lower case char
        for(int i  = 0; i < size && !(hasNumber && hasLower && hasUpper); i++){ //if all 3 conditions become true exit loop
            char c = password.charAt(i);
            // check for a specific condition only if it hasn't become true yet. i.e. we haven't found a char that is a digit yet
            hasLower = hasLower ? hasLower : Character.isLowerCase(c);
            hasUpper = hasUpper ? hasUpper : Character.isUpperCase(c);
            hasNumber = hasNumber ? hasNumber : Character.isDigit(c);
        }

        if(!hasNumber){
            buffer.append("Password must contain at least one (1) digit. ");
        }
        if(!hasUpper){
            buffer.append("Password must contain at least one (1) uppercase character. ");
        }
        if(!hasLower){
            buffer.append("Password must contain at least one (1) lowercase character. ");
        }

        if(!buffer.toString().isEmpty()){ //if there are error messages
            showToast(buffer.toString(),Toast.LENGTH_LONG);
        }

        return hasLower && hasUpper && hasNumber;
    }


    private void setEditingEnabled(boolean enabled) {
        mEmailField.setEnabled(enabled);
        mPasswordField.setEnabled(enabled);
    }

    void showToast(String text, int duration)
    {
        if(toast != null)
        {
            toast.cancel();
        }
        toast = Toast.makeText(this, text, duration);
        toast.show();

    }

}
