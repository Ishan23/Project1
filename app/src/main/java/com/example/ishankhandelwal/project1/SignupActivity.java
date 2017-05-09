package com.example.ishankhandelwal.project1;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    protected EditText usernameEditText;
    protected EditText numberEditText;
    protected EditText passwordEditText;
    protected EditText emailEditText;
    protected Button signUpButton;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabase;
    private String mUserId;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);



        // Initialize Firebase Auth and Database Reference
        mFirebaseAuth = FirebaseAuth.getInstance();



        usernameEditText = (EditText)findViewById(R.id.editText_username);
        numberEditText = (EditText)findViewById(R.id.editText_phone);
        passwordEditText = (EditText)findViewById(R.id.editText_password);
        emailEditText = (EditText)findViewById(R.id.editText_email);
        signUpButton = (Button)findViewById(R.id.sign_up_button);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog pDialog = new ProgressDialog(SignupActivity.this);
                pDialog.setMessage("Loading...");
                pDialog.show();

                final String username = usernameEditText.getText().toString();
                final String phone = numberEditText.getText().toString();
                 String password = passwordEditText.getText().toString();
                 String email = emailEditText.getText().toString();


                password = password.trim();
                email = email.trim();

                final String Email = email;

                if (password.isEmpty() || email.isEmpty() || username.isEmpty() || phone.isEmpty()) {
                    pDialog.hide();
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                    builder.setMessage(R.string.signup_error_message)
                            .setTitle(R.string.signup_error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>()
                    {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {


                                        try
                                        {
                                            mFirebaseUser = mFirebaseAuth.getCurrentUser();
                                            mUserId = mFirebaseUser.getUid();

                                            Contact contact = new Contact();
                                            contact.setUserID(mUserId);
                                            contact.setName(username);
                                            contact.setContactNumber(phone);

                                            mDatabase = FirebaseDatabase.getInstance().getReference();
                                            mDatabase.child("users").child(mUserId).child("user_info").push().child("username").setValue(username);
                                            mDatabase.child("users").child(mUserId).child("user_info").push().child("phone").setValue(phone);

                                            /*mDatabase.child("user_directory").child(mUserId).push().child("username").setValue(username);
                                            mDatabase.child("user_directory").child(mUserId).push().child("phone").setValue(phone);*/

                                            mDatabase.child("user_directory").push().setValue(contact);

                                        }catch (NullPointerException e)
                                        {
                                            Log.e("mUserId", "Throws null pointer exception");
                                        }

                                        pDialog.hide();
                                        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                        intent.putExtra("username",username);
                                        intent.putExtra("email",Email);
                                        intent.putExtra("phone",phone);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    } else {
                                        pDialog.hide();
                                        AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                                        builder.setMessage(task.getException().getMessage())
                                                .setTitle(R.string.login_error_title)
                                                .setPositiveButton(android.R.string.ok, null);
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                    }
                                }


                    });
                }
            }
        });
    }
}
