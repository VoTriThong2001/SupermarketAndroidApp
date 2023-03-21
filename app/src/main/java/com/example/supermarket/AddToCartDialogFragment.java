package com.example.supermarket;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddToCartDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddToCartDialogFragment extends DialogFragment {
    Connection connect;
    private TextView mAddName, mAddPrice, mAddLoc, mAddBrand;
    private EditText mItemAmount;
    private static String itemName,itemPrice,itemLoc, itemBrand;
    private String newName, newPrice, newLoc, newBrand , newCartID, newItemID, newAmount;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddToCartDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddToCartDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddToCartDialogFragment newInstance(String param1, String param2) {
        AddToCartDialogFragment fragment = new AddToCartDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static AddToCartDialogFragment newInstance(String name, int price, String location, String brand){
        AddToCartDialogFragment frag = new AddToCartDialogFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("price", String.valueOf(price));
        args.putString("location", location);
        frag.setArguments(args);

        itemName = name;
        itemPrice = String.valueOf(price);
        itemLoc = location;
        itemBrand = brand;
        return frag;
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
        return inflater.inflate(R.layout.fragment_add_to_cart, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAddName = view.findViewById(R.id.txtAddCartItemName);
        mAddPrice = view.findViewById(R.id.txtAddCartItemPrice);
        mAddLoc = view.findViewById(R.id.txtAddCartItemLoc);
        mAddBrand = view.findViewById(R.id.txtAddCartItemBrand);
        mItemAmount = view.findViewById(R.id.txtAddCartItemAmount);

        mAddName.setText(itemName);
        mAddPrice.setText(itemPrice);
        mAddLoc.setText(itemLoc);
        mAddBrand.setText(itemBrand);
        mItemAmount.setText(String.valueOf(1));

        // getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Button btnCancel =view.findViewById(R.id.btnAddCartItem_Cancel);
        Button btnConfirm = view.findViewById(R.id.btnAddCartItem_Confirm);
        btnCancel.setOnClickListener(v -> {
            getDialog().dismiss();
        });

        btnConfirm.setOnClickListener(v -> {
            Log.i("Dialog", "mEditName: "+mAddName.getText());
            newName = String.valueOf(mAddName.getText());
            newPrice = String.valueOf(mAddPrice.getText());
            newLoc = String.valueOf(mAddLoc.getText());
            newBrand = String.valueOf(mAddBrand.getText());
            newAmount = String.valueOf(mItemAmount.getText());
            Log.i("Add Cart Dialog", "newName: " + newName);
            Log.i("Add Cart Dialog", "newPrice: " + newPrice);
            Log.i("Add Cart Dialog", "newLoc: " + newLoc);
            Log.i("Add Cart Dialog", "newBrand: " + newBrand);
            Log.i("Add Cart Dialog", "newAmount: " + newAmount);
            User a= new User();
            int cusID=a.getUserID();
                btnConfirm.setText("Edit item");
               addToCart(newName, newPrice, newLoc, newBrand, cusID);
                Toast.makeText(getActivity(),"Added item to cart!", Toast.LENGTH_SHORT).show();
                getDialog().dismiss();

        });
    }
    private void addToCart(String itemName, String itemPrice, String itemLoc, String itemBrand, int cusID){
        try {
            connect = new SQLClass().conClass();
            if (connect !=null) {
                String query1 ="insert into Cart (total_price, cusID) values(0, " +cusID+ ")";

                Statement st = connect.createStatement();
                st.executeUpdate(query1);

                String query2 ="select TOP 1 cartID from Cart inner join Customer c on c.cusID = cart.cartID" +
                        " where c.cusID = "+cusID+ " ORDER BY cartID DESC ";

                ResultSet resultSet = st.executeQuery(query2);
                while (resultSet.next()) {
                    newCartID = resultSet.getString("cartID");
                }

                Log.i("ADD debug", "newCartID: " +newCartID);

                String query3 ="select item_id from Items where name = '" +itemName+ "' and price = "+itemPrice+"" +
                        " and location = '" + itemLoc + "' and brand = '"+ itemBrand +"'";
                Log.i("ADD debug", "query3: " +query3);
                Statement st2 = connect.createStatement();
                ResultSet resultSet2 = st2.executeQuery(query3);
                while (resultSet2.next()) {
                    newItemID = resultSet2.getString("item_id");
                }
                Log.i("ADD debug", "newItemID: " +newItemID);

                String query4 ="insert into include (cartID, item_id, item_amount)" +
                        " values(" + newCartID + ", " + newItemID + ", "+ newAmount + " )";
                st.executeUpdate(query4);

                String query5= "update Cart set total_price = b.total_price from Cart c" +
                        " INNER JOIN (select c.cartID , inc.item_amount,i.price ,i.name , inc.item_amount*i.price as" +
                        " \"total_price\" from include inc inner join Items i on i.item_id = inc.item_id" +
                        " inner join Cart c on c.cartID = inc.cartID where c.cusID = "+cusID +
                        " group by c.cartID, inc.item_amount, i.price, i.name) b on c.cartID = b.cartID";
                st.executeUpdate(query5);
                connect.close();
            }
        }catch (Exception ex) {
            Log.e("set error", ex.getMessage());
        }
    }
}