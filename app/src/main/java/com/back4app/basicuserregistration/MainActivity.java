package com.back4app.basicuserregistration;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity {

    TextView tv_loggeduser;
    Button bt_logout;

    EditText et_email;
    EditText et_password;
    Button bt_login;
    Button bt_register;
    ProgressDialog progressDialog;

    String email;
    String pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);

        tv_loggeduser = (TextView) findViewById(R.id.tv_loggeduser);

        bt_register = (Button) findViewById(R.id.bt_register);
        bt_login = (Button) findViewById(R.id.bt_login);

        bt_logout = (Button) findViewById(R.id.bt_logout);

        progressDialog = new ProgressDialog(MainActivity.this);


        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Please Wait");
                progressDialog.setTitle("Registering...");
                progressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            email = (et_email.getText().toString());
                            pwd = (et_password.getText().toString());
                            userRegister(email, pwd);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Please Wait");
                progressDialog.setTitle("Logging in...");
                progressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            email = (et_email.getText().toString());
                            pwd = (et_password.getText().toString());
                            userLogin(email, pwd);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        bt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                progressDialog.setMessage("Please Wait");
                progressDialog.setTitle("Logging out...");
                progressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            userLogout();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    void alertDisplayer(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    void userRegister(final String username, final String password){
        if (ParseUser.getCurrentUser() != null) {
            ParseUser.getCurrentUser().logOut();
        }
        ParseUser user = new ParseUser();
        // username is the same as email
        // add your own code for ensuring a valid email address
        user.setUsername(username);
        user.setEmail(username);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // Add your own feedback
                    progressDialog.dismiss();
                    alertDisplayer("Register Successful", "Verify your email: " + username);

                    String text;
                    text = "Hello, " + ParseUser.getCurrentUser().getUsername() + ". Email verified: ";
                    if(ParseUser.getCurrentUser().getBoolean("emailVerified"))
                        text = text + "true";
                    else
                        text = text + "false";

                    tv_loggeduser.setText(text);
                } else {
                    progressDialog.dismiss();
                    alertDisplayer("Register Fail", e.getMessage()+" Please Try Again");
                }
            }
        });
    }

    void userLogin(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (parseUser != null) {
                    if(ParseUser.getCurrentUser().getBoolean("emailVerified")){
                        progressDialog.dismiss();
                        alertDisplayer("Login Successful", "Welcome " + parseUser.getUsername());

                        // Now you may add code to enable all functionalities for your user
                        String text;
                        text = "Hello, " + ParseUser.getCurrentUser().getUsername() + ". Email verified";

                        tv_loggeduser.setText(text);
                    }
                    else{
                        progressDialog.dismiss();
                        alertDisplayer("Verification Failed", "Check your email: " + parseUser.getUsername());

                        // Add the limited functionalities for non-verified users
                        String text;
                        text = "Hello, " + ParseUser.getCurrentUser().getUsername() + ". Email NOT verified";

                        tv_loggeduser.setText(text);
                    }

                } else {
                    progressDialog.dismiss();
                    alertDisplayer("Login Failed", e.getMessage() + " Please Try Again");
                }
            }
        });
    }

    void userLogout(){
        if (ParseUser.getCurrentUser() != null) {
            ParseUser.getCurrentUser().logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    progressDialog.dismiss();
                    alertDisplayer("Logout Successful","");
                    tv_loggeduser.setText("");
                }
            });
        }
        else {
            progressDialog.dismiss();
            alertDisplayer("Logout Failed","No users logged in");
        }
    }
}
