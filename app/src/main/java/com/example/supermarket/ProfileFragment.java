package com.example.supermarket;

import static java.lang.Math.log10;
import static java.lang.Math.pow;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class ProfileFragment extends Fragment {
    ProgressBar progressBar;
    Connection connect;
    int counter = 0;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private double progr;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        User user = new User();
        int customerID = user.getUserID();
        int aID = user.getAID();
        Log.i("Profile Debug", "aID: " + aID);
        Log.i("Profile Debug", "customerID: " + customerID);
        if (user.isAdmin() == false) {
            getCustomer(view, customerID);
        } else {
            getAdmin(view, aID);
        }
        // Inflate the layout for this fragment
        return view;
    }

    private void getAdmin(View view, int aID) {
        try {
            connect = new SQLClass().conClass();
            TextView Name = view.findViewById(R.id.fullnametxt);
            TextView Address = view.findViewById(R.id.addresstxt);
            TextView Phone = view.findViewById(R.id.phonetxt);
            TextView Email = view.findViewById(R.id.emailtxt);
            TextView membershipPoint = view.findViewById(R.id.view_membership_point);
            ProgressBar progressBar = view.findViewById(R.id.progressBar);
            TextView MBLevel = view.findViewById(R.id.membershiplevel);
            int tempMemPoint;
            int tempLevel, firstLvlDigit, length;
            if (connect != null) {
                String query = "select * from Admin where aID = " + aID;
                Statement st = connect.createStatement();
                ResultSet resultSet = st.executeQuery(query);
                if (resultSet == null) {
                    Log.e("resultSet", "Result set null ");
                }
                while (resultSet.next()) {
                    Name.setText(resultSet.getString("name"));
                    Address.setText(resultSet.getString("address"));
                    Phone.setText(resultSet.getString("phone"));
                    Email.setText(resultSet.getString("email"));

                    tempMemPoint = 0;
                    tempLevel = 0;
                    length = (int) (Math.log10(tempLevel) + 1);
                    if (length <= 2) {
                        MBLevel.setText("Level 0");
                    } else {
                        firstLvlDigit = tempLevel / ((int) (pow(10, (int) log10(tempLevel))));
                        MBLevel.setText("Level " + String.valueOf(firstLvlDigit));
                    }

                    // Log.d("Test", "vipPoints: " +tempMemPoint);
                    if (tempMemPoint > 100) {
                        tempMemPoint = Integer.parseInt(resultSet.getString("vipPoints")) % 100;
                        membershipPoint.setText(String.valueOf(tempMemPoint) + "/100");
                        progressBar.setProgress(tempMemPoint);
                    } else if (tempMemPoint <= 100) {
                        membershipPoint.setText(String.valueOf(tempMemPoint) + "/100");
                        progressBar.setProgress(tempMemPoint);
                    }

                }
                connect.close();
            }
        } catch (Exception ex) {
            Log.e("Customer Set error", ex.getMessage());
        }
    }

    private void getCustomer(View view, int customer_id) {
        try {
            connect = new SQLClass().conClass();
            TextView Name = view.findViewById(R.id.fullnametxt);
            TextView Address = view.findViewById(R.id.addresstxt);
            TextView Phone = view.findViewById(R.id.phonetxt);
            TextView Email = view.findViewById(R.id.emailtxt);
            TextView membershipPoint = view.findViewById(R.id.view_membership_point);
            ProgressBar progressBar = view.findViewById(R.id.progressBar);
            TextView MBLevel = view.findViewById(R.id.membershiplevel);
            int tempMemPoint;
            int tempLevel, firstLvlDigit, length;
            if (connect != null) {
                String query = "select * from Customer c inner join Membership m on c.cusID = m.cusID where c.cusID = " + customer_id;
                Statement st = connect.createStatement();
                ResultSet resultSet = st.executeQuery(query);
                if (resultSet == null) {
                    Log.e("resultSet", "Result set null ");
                }
                while (resultSet.next()) {
                    Name.setText(resultSet.getString("name"));
                    Address.setText(resultSet.getString("address"));
                    Phone.setText(resultSet.getString("phone"));
                    Email.setText(resultSet.getString("email"));

                    tempMemPoint = Integer.parseInt(resultSet.getString("vipPoints"));
                    tempLevel = Integer.parseInt(resultSet.getString("vipPoints"));
                    length = (int) (Math.log10(tempLevel) + 1);
                    if (length <= 2) {
                        MBLevel.setText("Level 0");
                    } else {
                        firstLvlDigit = tempLevel / ((int) (pow(10, (int) log10(tempLevel))));
                        MBLevel.setText("Level " + String.valueOf(firstLvlDigit));
                    }


                    // Log.d("Test", "vipPoints: " +tempMemPoint);
                    if (tempMemPoint > 100) {
                        tempMemPoint = Integer.parseInt(resultSet.getString("vipPoints")) % 100;
                        membershipPoint.setText(String.valueOf(tempMemPoint) + "/100");
                        progressBar.setProgress(tempMemPoint);
                    } else if (tempMemPoint <= 100) {
                        membershipPoint.setText(String.valueOf(tempMemPoint) + "/100");
                        progressBar.setProgress(tempMemPoint);
                    }

                }
                connect.close();
            }
        } catch (Exception ex) {
            Log.e("Customer Set error", ex.getMessage());
        }
    }
}