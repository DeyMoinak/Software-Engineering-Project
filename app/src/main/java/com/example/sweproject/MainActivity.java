package com.example.sweproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;




public class MainActivity extends AppCompatActivity {
    //@Override
    public static int gradeLevel = 0;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Make Florida Standards image view go to Florida Standards website
        ImageView FLStandards = (ImageView) findViewById(R.id.FLStandards);
        FLStandards.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://www.fldoe.org/academics/standards/"));
                startActivity(intent);
            }
        });

        //Test Login
        TextView username = (TextView) findViewById(R.id.username);
        TextView password = (TextView) findViewById(R.id.password);

        MaterialButton loginbutton = (MaterialButton) findViewById(R.id.loginbutton);
            loginbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (username.getText().toString().equals("username") && password.getText().toString().equals("password")) {
                        //correct
                        Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    } else {
                        //incorrect
                        Toast.makeText(MainActivity.this, "Login Unsuccessful", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        //Make "Create an Educator Account Button" go to an Educator Account Creation Screen
        Button createAnEducatorAccount = (Button)findViewById(R.id.createEducatorAccount);


        createAnEducatorAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //change the page to educator account creation
                startActivity(new Intent(MainActivity.this, EducatorAccountCreation.class));


            }
        });

        //Make "Learn" go to Student Homepage **Only if login successful (implement this functionality)**
        Button learn = (Button)findViewById(R.id.loginbutton);


        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataBase dataBaseHelper = new DataBase(MainActivity.this);

                int checkStudent = dataBaseHelper.validateUser(username.getText().toString(), password.getText().toString(), false);

                if(checkStudent != -1){
                    MainActivity.gradeLevel = checkStudent;
                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, StudentHomepage.class));
                }else{
                    Toast.makeText(MainActivity.this, "Login Unsuccessful", Toast.LENGTH_SHORT).show();
                }

            }
        });


        TextView edusername = (TextView) findViewById(R.id.educatorUsername);
        TextView edpassword = (TextView) findViewById(R.id.educatorPassword);

        //Make "Manage" go to Educator Homepage **Only if login successful (implement this functionality)**
        Button manage = (Button)findViewById(R.id.educatorLoginButton);

        manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DataBase dataBaseHelper = new DataBase(MainActivity.this);

                //this is causing an error
                int check = dataBaseHelper.validateUser(edusername.getText().toString(), edpassword.getText().toString(), true);

                if (check != -1) {
                    startActivity(new Intent(MainActivity.this, EducatorHomepage.class));
                }

            }
        });

    }
    }
