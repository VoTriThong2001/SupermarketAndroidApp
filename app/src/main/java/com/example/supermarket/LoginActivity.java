package com.example.supermarket;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    SimpleAdapter adapter;
    Connection connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView btn = findViewById(R.id.btnlogin);
        EditText Username = findViewById(R.id.username);
        EditText Password = findViewById(R.id.inputPassword);
        Log.i("LOGIN DEBUG", "LOGIN onCreate ran ");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String typedUsername=Username.getText().toString() , typedPassword=Password.getText().toString();
                if (typedUsername.isEmpty() || typedPassword.isEmpty()){
                    Toast.makeText(LoginActivity.this,"Login Failed!", Toast.LENGTH_SHORT).show();
                    Log.i("LOGIN DEBUG", "Login failed");
                }

                int loginCheck = getLogin(typedUsername, typedPassword);
                if (loginCheck == 0) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    Toast.makeText(LoginActivity.this,"Login Successful!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                else if (loginCheck == 1) {
                    Toast.makeText(LoginActivity.this,"Login Failed!", Toast.LENGTH_SHORT).show();
                    Log.i("LOGIN DEBUG", "Login failed with loginCheck=1");
                }
            }
        });
    }

    private int getLogin(String username, String pass){
        List<Map<String,String>> data = new ArrayList<Map<String,String>>();
        try {
            connect = new SQLClass().conClass();
            if (connect !=null) {
                String query ="select aID,cus_ID, Ac_name, Ac_password, Ac_type_id from Account a where a.Ac_name = '" + username+ "' and a.Ac_password='" +pass +"'";
                Statement st = connect.createStatement();
                ResultSet resultSet = st.executeQuery(query);

                while (resultSet.next()) {
                    //    Map<String, String> tab = new HashMap<String, String>();
                   String tempAcName = resultSet.getString("Ac_name");

                   String tempPass =  resultSet.getString("Ac_password");
                   String tempAcType =  resultSet.getString("Ac_type_id");
                    Log.i("LOGIN debug", "tempAcType: "+tempAcType);
                    Log.i("LOGIN DEBUG", "tempAcName: " +tempAcName);
                    Log.i("LOGIN DEBUG", "tempPass: " +tempPass);
                    if (tempAcName.trim().equals(username) && tempPass.trim().equals(pass)) {
                        if (Integer.parseInt(tempAcType) == 1)  {
                            Log.i("LOGIN debug", "NORMAL USER");
                            int tempUserID = resultSet.getInt("cus_ID");
                            Log.i("LOGIN debug", "tempUserID: "+tempUserID);
                            User user= new User();
                            user.setUserID(tempUserID);
                            user.setAID(0);
                        }
                        if (Integer.parseInt(tempAcType) == 2)  {
                            Log.i("LOGIN debug", "ADMIN");
                            int tempUserID = resultSet.getInt("aID");
                            Log.i("LOGIN debug", "tempaID: "+tempUserID);
                            User user= new User();
                            user.setAID(tempUserID);
                            user.setUserID(0);
                        }
                        //Succesfully obtained login info
                        Log.i("LOGIN DEBUG", "getLogin Returned 0");
                        return 0;
                    }
                    else {
                        //Failed to get login info
                        Log.i("LOGIN DEBUG", "getLogin Returned 1");
                        return 1;
                    }
                }
            }
            connect.close();
        }catch (Exception ex) {
            Log.e("Login db error", ex.getMessage());
        }
        return 1;
    }
}