package com.example.supermarket;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class AddItemDialogFragment extends DialogFragment{
    Connection connect;
    private EditText mAddName, mAddPrice, mAddLoc, mAddAmount, mAddBrand;
    private Spinner mAddMenuGroup;
    private String newItemID;
    private static String itemName,itemPrice,itemLoc,itemBrand, itemAmount, menuGroup;
    private static String[] items ={"hot","food","dairy","beverage","kitchenware","cleaners"};

    AddItemDialogFragment(){

    }


    public static AddItemDialogFragment newInstance(){
        AddItemDialogFragment frag = new AddItemDialogFragment();
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_item,container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAddName = view.findViewById(R.id.txtAddItemName);
        mAddPrice = view.findViewById(R.id.txtAddItemPrice);
        mAddLoc = view.findViewById(R.id.txtAddItemLoc);
        mAddBrand = view.findViewById(R.id.txtAddItemBrand);
        mAddAmount = view.findViewById(R.id.txtAddItemAmount);

        mAddMenuGroup = view.findViewById(R.id.spinnerMenuGroup);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAddMenuGroup.setAdapter(adapter);
        mAddMenuGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        menuGroup=String.valueOf(1);
                        break;
                    case 1:
                        menuGroup=String.valueOf(2);
                        break;
                    case 2:
                        menuGroup=String.valueOf(3);
                        break;
                    case 3:
                        menuGroup=String.valueOf(4);
                        break;
                    case 4:
                        menuGroup=String.valueOf(5);
                        break;
                    case 5:
                        menuGroup=String.valueOf(6);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                menuGroup=String.valueOf(1);
            }
        });


       // getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Button btnCancel =view.findViewById(R.id.btnAddItem_Cancel);
        Button btnConfirm = view.findViewById(R.id.btnAddItem_Confirm);

        btnCancel.setOnClickListener(v -> {
            getDialog().dismiss();
        });

        btnConfirm.setOnClickListener(v -> {
            Log.i("Dialog", "mAddName: "+mAddName.getText());
            itemName = String.valueOf(mAddName.getText());
            itemPrice = String.valueOf(mAddPrice.getText());
            itemLoc = String.valueOf(mAddLoc.getText());
            itemBrand = String.valueOf(mAddBrand.getText());
            itemAmount = String.valueOf(mAddAmount.getText());

            addItem(itemName, itemPrice, itemLoc, itemBrand, itemAmount, menuGroup);
            Toast.makeText(getActivity(),"Added item, please refresh!", Toast.LENGTH_SHORT).show();
            getDialog().dismiss();
        });
    }

    private void addItem(String itemName, String itemPrice, String itemLoc, String itemBrand, String itemAmount, String menuGroup){
        try {
            connect = new SQLClass().conClass();
            if (connect !=null) {
                String query1 ="insert into Items (name, price,location , brand , amount) values('" + itemName +
                        "', " + itemPrice + ", '" + itemLoc + "', '" + itemBrand  + "', " + itemAmount + ")";
                Statement st = connect.createStatement();
                st.executeUpdate(query1);

                String query2 ="select max(item_id) as 'item_id' from Items";
                ResultSet resultSet = st.executeQuery(query2);
                while (resultSet.next()) {
                    newItemID = resultSet.getString("item_id");
                }

                Log.i("ADD debug", "newItemID: " +newItemID);
                Log.i("ADD debug", "menuGroup: " +menuGroup);

                String query3 ="insert into display (mID, item_id) values(" + menuGroup + ", " + newItemID + ")";
                st.executeUpdate(query3);
                connect.close();
            }
        }catch (Exception ex) {
            Log.e("set error", ex.getMessage());
        }
    }
}
