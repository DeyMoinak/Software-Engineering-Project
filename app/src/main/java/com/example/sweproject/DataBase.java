package com.example.sweproject;

import static java.security.AccessController.getContext;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import androidx.annotation.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DataBase extends SQLiteOpenHelper {

    public boolean initialize = false;
    ArrayList<String[]> lst;
    public static final String STUDENT_TABLE = "STUDENT_TABLE";
    public static final String STUDENT_COLUMN_STUDENT_USERNAME = "STUDENT_USERNAME";
    public static final String TEACHER_COLUMN_PASSWORD = "PASSWORD";
    public static final String STUDENT_COLUMN_PASSWORD = "PASSWORD";
    public static final String STUDENT_COLUMN_TEACHER = "TEACHER";
    public static final String QUESTION_COLUMN_GRADE = "GRADE";
    public static final String TEACHER_COLUMN_GRADE = QUESTION_COLUMN_GRADE;
    public static final String STUDENT_COLUMN_GRADE = QUESTION_COLUMN_GRADE;
    public static final String STUDENT_COLUMN_ACTUAL_NAME = "ACTUAL_STUDENT_NAME";
    public static final String TEACHER_TABLE = "TEACHER_TABLE";
    public static final String TEACHER_COLUMN_TEACHER_USERNAME = "TEACHER_USERNAME";
    public static final String TEACHER_COLUMN_ACTUAL_NAME = "ACTUAL_TEACHER_NAME";
    public static final String QUESTION_COLUMN_QUESTION = "QUESTION";
    public static final String QUESTION_TABLE = QUESTION_COLUMN_QUESTION + "_TABLE";
    public static final String QUESTION_COLUMN_QUESTION_ID = QUESTION_COLUMN_QUESTION + "_ID";
    public static final String QUESTION_COLUMN_SUBJECT = "SUBJECT";
    public static final String QUESTION_COLUMN_CORRECT_ANSWER = "CORRECT_ANSWER";
    public static final String QUESTION_COLUMN_WRONG_ANSWER_1 = "WRONG_ANSWER1";
    public static final String QUESTION_COLUMN_WRONG_ANSWER_2 = "WRONG_ANSWER2";
    public static final String QUESTION_COLUMN_WRONG_ANSWER_3 = "WRONG_ANSWER3";
    public static final String QUESTION_COLUMN_STANDARD = "STANDARD";

    public DataBase(@Nullable Context context) {
        super(context, "studentAssess.db", null, 1);
    }


    //call to create database and populate question table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createQuestionTableString = "CREATE TABLE " + QUESTION_TABLE + " (" + QUESTION_COLUMN_QUESTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + QUESTION_COLUMN_SUBJECT + " TEXT, " + QUESTION_COLUMN_GRADE + " INTEGER, " + QUESTION_COLUMN_QUESTION + " TEXT, " + QUESTION_COLUMN_CORRECT_ANSWER + " TEXT, " + QUESTION_COLUMN_WRONG_ANSWER_1 + " TEXT, " + QUESTION_COLUMN_WRONG_ANSWER_2 + " TEXT, " + QUESTION_COLUMN_WRONG_ANSWER_3 + " TEXT, " + QUESTION_COLUMN_STANDARD + " TEXT)";
        String createTeacherTableString = "CREATE TABLE " + TEACHER_TABLE + " (" + TEACHER_COLUMN_TEACHER_USERNAME + " TEXT PRIMARY KEY, " + TEACHER_COLUMN_PASSWORD + " TEXT, " + TEACHER_COLUMN_GRADE + " INTEGER, " + TEACHER_COLUMN_ACTUAL_NAME + " TEXT)";
        String createStudentTableString = "CREATE TABLE " + STUDENT_TABLE + " (" + STUDENT_COLUMN_STUDENT_USERNAME + " TEXT PRIMARY KEY, " + STUDENT_COLUMN_PASSWORD + " TEXT, " + STUDENT_COLUMN_TEACHER + " TEXT, " + STUDENT_COLUMN_GRADE + " INTEGER, " + STUDENT_COLUMN_ACTUAL_NAME + " TEXT, " + "FOREIGN KEY(" + STUDENT_COLUMN_TEACHER + ") REFERENCES " + TEACHER_TABLE + "(" + TEACHER_COLUMN_TEACHER_USERNAME + ") )";
        String createPerformanceTableString = "CREATE TABLE PERFORMANCE_TABLE (TEACHER STRING, STUDENT STRING, SUBJECT STRING, PERFORMANCE REAL, FOREIGN KEY(TEACHER) REFERENCES TEACHER_TABLE(TEACHER_USERNAME), FOREIGN KEY(STUDENT) REFERENCES STUDENT_TABLE(STUDENT_USERNAME))";
        db.execSQL(createQuestionTableString);
        db.execSQL(createTeacherTableString);
        db.execSQL(createStudentTableString);
        db.execSQL(createPerformanceTableString);


        //String File_path = "C:\\Users\\aniru\\OneDrive\\Desktop\\UF\\CEN_3031\\Questions.csv"; //CHANGE ME!

        //ArrayList<String[]> lst = csv_parser(File_path);

        Log.v("message", "Number of tuples: " + String.valueOf(lst.size()));

        for (String[] question: lst){
            ContentValues cv = new ContentValues();
            cv.put(QUESTION_COLUMN_QUESTION, question[2]);
            cv.put(QUESTION_COLUMN_SUBJECT, question[0]);
            cv.put(QUESTION_COLUMN_CORRECT_ANSWER, question[3]);
            cv.put(QUESTION_COLUMN_WRONG_ANSWER_1, question[4]);
            cv.put(QUESTION_COLUMN_WRONG_ANSWER_2, question[5]);
            cv.put(QUESTION_COLUMN_WRONG_ANSWER_3, question[6]);
            cv.put(QUESTION_COLUMN_STANDARD, question[7]);
            cv.put(QUESTION_COLUMN_GRADE, Integer.parseInt(question[1]));
            long insert = db.insert(QUESTION_TABLE, null, cv);

            if (insert == -1){
                Log.v("message", "Insertion Error");
            }
        }
        //}
        /*catch (IOException e)
        {
            e.printStackTrace();
        }*/



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    //adds user (student/educator) to database
    public boolean addUser(boolean student, User user)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        long insert;

        if (student)
        {
            cv.put(STUDENT_COLUMN_STUDENT_USERNAME, user.getUsername());
            cv.put(STUDENT_COLUMN_PASSWORD, user.getPassword());
            cv.put(STUDENT_COLUMN_TEACHER, user.getTeacher_user());
            cv.put(STUDENT_COLUMN_GRADE, user.getGrade());
            cv.put(STUDENT_COLUMN_ACTUAL_NAME, user.getActual_name());
            insert = db.insert(STUDENT_TABLE, null, cv);
        }
        else
        {
            cv.put(TEACHER_COLUMN_TEACHER_USERNAME, user.getUsername());
            cv.put(TEACHER_COLUMN_PASSWORD, user.getPassword());
            cv.put(TEACHER_COLUMN_GRADE, user.getGrade());
            cv.put(TEACHER_COLUMN_ACTUAL_NAME, user.getActual_name());
            insert = db.insert(TEACHER_TABLE, null, cv);
        }
        db.close();
        if (insert == -1)
        {
            return false;
        }
        else
        {
            return true;
        }

    }

    public int validateUser(String username, String password, boolean stud_or_teach){
        //stud_or_teach == true if teacher, false if student
        int ret = -1;
        String query;

        SQLiteDatabase db = this.getReadableDatabase();

        //String query1;
        //query1 = "SELECT * FROM TEACHER_TABLE";
        //Cursor cursor1 = db.rawQuery(query1,null);
        //while (cursor1.moveToNext()){
         //   String temp = cursor1.getString(0);
        //    Log.i("message", temp);
        //}
        if (stud_or_teach){
            query = "SELECT * FROM TEACHER_TABLE";
        }
        else {
            query = "SELECT * FROM STUDENT_TABLE";
        }

        Cursor cursor = db.rawQuery(query,null);

        int grade;
        if (cursor.moveToFirst()){
            do {
                String uname = cursor.getString(0);
                String pword = cursor.getString(1);

                if (uname.equals(username) && pword.equals(password)){

                    if(!stud_or_teach){
                        ret = cursor.getInt(3); //return the grade of student
                    }
                    else{
                        ret = 3; //just some pos value to show teacher is correct
                    }

                    break;
                }
            }while (cursor.moveToNext());


        }

        db.close();
        cursor.close();

        return ret;
    }

    //add performance score if no score previously saved
    public boolean addPerformance(String student_username, float score, String subject)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String teacher_user = "";
        String query = "SELECT TEACHER FROM STUDENT_TABLE WHERE STUDENT_TABLE.STUDENT_USERNAME = '" + student_username + "'";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        if (cursor.getCount() == 1)
        {
            teacher_user = cursor.getString(0);
        }
        else
        {
            return false;
            //this means that there is somehow more than one student with the same username (BAD)
        }

        cursor.close();

        cv.put("TEACHER", teacher_user);
        cv.put("STUDENT", student_username);
        cv.put("PERFORMANCE", score);
        cv.put("SUBJECT", subject);

        long insert1 = db.insert("PERFORMANCE_TABLE", null, cv);

        db.close();
        if (insert1 == -1)
        {
            return false;
        }
        else
        {
            return true;
        }

    }

    //changes performance score if student is retaking assessment
    public boolean changePerformance(String student_username, float score, String subject)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("PERFORMANCE", score);

        long insert1 = db.update("PERFORMANCE_TABLE", cv, "PERFORMANCE_TABLE.STUDENT=? AND PERFORMANCE_TABLE.SUBJECT=?", new String[]{student_username,subject});

        db.close();
        if (insert1 == -1)
        {
            return false;
        }
        else
        {
            return true;
        }

    }

    public boolean checkAssessmentTaken(String student_username, String subject)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String teacher_user = "";
        String query = "SELECT * FROM PERFORMANCE_TABLE WHERE PERFORMANCE_TABLE.STUDENT = '" + student_username + "' AND PERFORMANCE_TABLE.SUBJECT = '" + subject + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() == 1)
        {
            db.close();
            cursor.close();
            return true;
        }
        else
        {
            db.close();
            cursor.close();
            return false;
        }

    }


    //Now returns 1 random question per standard
    public List<question> getrandQuestions(int grade, String subject)
    {
        List<question> returnList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        //look for all standards in the subject/grade
        String query = "SELECT * FROM QUESTION_TABLE"; // +ORDER BY random()
        Cursor cursor = db.rawQuery(query,null);

        //populate a list with all the standards in the subject/grade
        List<String> applicable_standards = new ArrayList<String>();
        if (cursor.getCount() == 0)
        {
            Log.v("message", "mmmmm");
        }
        //Log.v("message", "mmmmm");
        while(cursor.moveToNext()){
            Log.v("message", "mmmmm");
            if (cursor.getInt(2) == grade && cursor.getString(1).equals(subject)) {
                String inside = "question from table: " + cursor.getString(3);
                Log.i("message", inside);
                int questionID = cursor.getInt(0);
                String subject_toadd = cursor.getString(1);
                int grade_toadd = cursor.getInt(2);
                String question = cursor.getString(3);
                String correct_answer = cursor.getString(4);
                String wrong_answer1 = cursor.getString(5);
                String wrong_answer2 = cursor.getString(6);
                String wrong_answer3 = cursor.getString(7);
                String standard = cursor.getString(8);

                question _question = new question(questionID, subject_toadd, grade_toadd, question, correct_answer, wrong_answer1, wrong_answer2, wrong_answer3, standard);
                returnList.add(_question);
                String qtext = _question.getQuestion();
                Log.i("message", "question in list : " + qtext);

                String temp = "size of retList: " + Integer.toString(returnList.size());
                Log.i("message", temp);

                if (returnList.size() >= 10)
                    break;


            }
        }



        cursor.close();
        db.close();
        return returnList;
    }

    public List<scoreReport> getReportsForEachTeacher(String teacher_username)
    {
        List<scoreReport> returnList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT DISTINCT PERFORMANCE_TABLE.STUDENT, STUDENT_TABLE.ACTUAL_STUDENT_NAME , PERFORMANCE_TABLE.PERFORMANCE, PERFORMANCE_TABLE.SUBJECT FROM PERFORMANCE_TABLE, STUDENT_TABLE WHERE PERFORMANCE_TABLE.STUDENT = STUDENT_TABLE.STUDENT_USERNAME AND PERFORMANCE_TABLE.TEACHER = '" + teacher_username + "' ORDER BY STUDENT_TABLE.ACTUAL_STUDENT_NAME";

        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do
            {
                String student_user = cursor.getString(0);
                String student_name = cursor.getString(1);
                float score = cursor.getFloat(2);
                String subject = cursor.getString(3);

                scoreReport report = new scoreReport(student_user, student_name, score, subject);
                returnList.add(report);
            }
            while(cursor.moveToNext());
        }
        else
        {
            //nothing happened (this is BAD)
        }

        cursor.close();
        db.close();
        return returnList;
    }

    /*public static ArrayList<String[]> csv_parser (String File_path) {

        String line = "";
        String splitBy = ",";
        ArrayList<String[]> toret = new ArrayList<>();
        Log.v("message", "Testing if entered parser function");
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(File_path));
            while ((line = br.readLine()) != null)   //returns a Boolean value
            {
                String[] question = line.split(splitBy);
                question[2] = question[2].replace('&', ',');// use comma as separator

                toret.add(question);
            }
        }
        catch (IOException e)
        {
            Log.v("message", "Testing if failed!");
            e.printStackTrace();
        }

        return toret;
    }*/

}