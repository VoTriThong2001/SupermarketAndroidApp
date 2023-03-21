package com.example.supermarket;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartFragment extends Fragment {
    SimpleAdapter adapter;
    Connection connect;
    TextView viewFinalPrice;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
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
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        GridView list1 = view.findViewById(R.id.CartGrid);
        viewFinalPrice= view.findViewById(R.id.CartFinalPrice);


        User user = new User();
        int customerID = user.getUserID();
        getCart(list1,customerID);
        return view;
    }
    private void getCart(GridView list, int customer_id) {
        String finalprice;

        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        try {
            connect = new SQLClass().conClass();
            if (connect != null) {
                String query = "select c.cartID , inc.item_amount,i.price ,i.name ," +
                        " inc.item_amount*i.price as \"total_price\" from include inc inner join" +
                        " Items i on i.item_id = inc.item_id inner join Cart c on c.cartID = inc.cartID" +
                        " where c.cusID = 1 group by c.cartID, inc.item_amount, i.price, i.name";

                Statement st = connect.createStatement();
                ResultSet resultSet = st.executeQuery(query);

                while (resultSet.next()) {
                    Map<String, String> tab = new HashMap<String, String>();
                    tab.put("item_amount", resultSet.getString("item_amount"));
                    tab.put("name", resultSet.getString("name"));
                    tab.put("price", resultSet.getString("price"));
                    tab.put("total_price", resultSet.getString("total_price"));
                    data.add(tab);

                    Log.i("Data", "sql query added to data list");
                }

                String query2 = "select c.cartID , sum(inc.item_amount) as \"item_amount\"," +
                        "sum( inc.item_amount*i.price) as \"final_price\" from include inc inner join" +
                        " Items i on i.item_id = inc.item_id inner join Cart c on c.cartID = inc.cartID" +
                        " where c.cusID = "+customer_id+" group by c.cartID";
                Statement st2 = connect.createStatement();
                ResultSet resultSet2 = st2.executeQuery(query2);
                while (resultSet2.next()) {
                    finalprice = resultSet2.getString("final_price");
                    Log.i("Final Price", "finalprice: "+finalprice);
                    viewFinalPrice.setText(finalprice);
                }


                String[] fromBody = {"name", "item_amount","price", "total_price"};
                int[] toBody = {R.id.textCart_ItemName, R.id.textCart_Amount, R.id.textCart_Price, R.id.textCart_TotalPrice};
                adapter = new SimpleAdapter(getActivity(), data, R.layout.cart_layout, fromBody, toBody);
                list.setAdapter(adapter);
                connect.close();
            }
        } catch (Exception ex) {
            Log.e("set error", ex.getMessage());
        }
    }
}