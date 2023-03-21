package com.example.supermarket;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReceiptFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReceiptFragment extends Fragment {
    SimpleAdapter adapter;
    Connection connect;
    GridView list;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public ReceiptFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReceiptFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReceiptFragment newInstance(String param1, String param2) {
        ReceiptFragment fragment = new ReceiptFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_receipt, container, false);

        GridView list1 = view.findViewById(R.id.ReceiptGrid);
        User user = new User();
        int customerID = user.getUserID();
        getReceipt(list1,customerID);
        return view;
    }

    private void getReceipt(GridView list, int customer_id) {

            List<Map<String, String>> data = new ArrayList<Map<String, String>>();
            try {
                connect = new SQLClass().conClass();
                if (connect != null) {
                    String query = "select r.rID ,sum(h.item_amount) as \"item_amount\", sum(h.item_amount*i.price)" +
                            " as \"total_price\" from have h inner join Items i on i.item_id = h.item_id inner join" +
                            " Receipts r on r.rID = h.rID where r.cusID = " + customer_id +" group by r.rID";

                    Statement st = connect.createStatement();
                    ResultSet resultSet = st.executeQuery(query);

                    while (resultSet.next()) {
                        Map<String, String> tab = new HashMap<String, String>();
                            tab.put("rID", resultSet.getString("rID"));
                            tab.put("item_amount", resultSet.getString("item_amount"));
                            tab.put("total_price", resultSet.getString("total_price"));
                            data.add(tab);

                        Log.i("Data", "sql query added to data list");
                    }
                    String[] fromBody = {"rID", "item_amount","total_price"};
                    int[] toBody = {R.id.textReceipt_ID, R.id.textReceipt_Amount, R.id.textReceipt_TotalPrice};
                    adapter = new SimpleAdapter(getActivity(), data, R.layout.recipe_layout, fromBody, toBody);
                    list.setAdapter(adapter);
                    connect.close();
                }
            } catch (Exception ex) {
                Log.e("set error", ex.getMessage());
            }
        }
    }

