package com.example.supermarket;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class EditItemDialogFragment extends DialogFragment {
    Connection connect;
    private EditText mEditName, mEditPrice, mEditLoc, mEditBrand;
    private static String itemName,itemPrice,itemLoc, itemBrand;
    private String newName, newPrice, newLoc, newBrand , newItemID;
    public EditItemDialogFragment(){

    }

    public static EditItemDialogFragment newInstance(String name, int price, String location, String brand){
        EditItemDialogFragment frag = new EditItemDialogFragment();
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


    public static EditItemDialogFragment newInstance(){
        EditItemDialogFragment frag = new EditItemDialogFragment();
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_item,container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEditName = view.findViewById(R.id.txtEditItemName);
        mEditPrice = view.findViewById(R.id.txtEditItemPrice);
        mEditLoc = view.findViewById(R.id.txtEditItemLoc);
        mEditBrand = view.findViewById(R.id.txtEditItemBrand);

        mEditName.setText(itemName);
        mEditPrice.setText(itemPrice);
        mEditLoc.setText(itemLoc);
        mEditBrand.setText(itemBrand);

       // getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Button btnCancel =view.findViewById(R.id.btnEditItem_Cancel);
        Button btnConfirm = view.findViewById(R.id.btnEditItem_Confirm);
        Button btnClear = view.findViewById(R.id.btnEditItem_Clear);
        btnCancel.setOnClickListener(v -> {
            getDialog().dismiss();
        });
        btnConfirm.setOnClickListener(v -> {
            Log.i("Dialog", "mEditName: "+mEditName.getText());
            newName = String.valueOf(mEditName.getText());
            newPrice = String.valueOf(mEditPrice.getText());
            newLoc = String.valueOf(mEditLoc.getText());
            newBrand = String.valueOf(mEditBrand.getText());
            //IF command to prevent special characters
            if (newBrand.isEmpty() && newName.isEmpty() && newLoc.isEmpty() && newPrice.isEmpty()) {
                    deleteItem();
                    Toast.makeText(getActivity(),"Deleted the item, please refresh!", Toast.LENGTH_SHORT).show();
                    getDialog().dismiss();
                }
            else {
                btnConfirm.setText("Edit item");
                editItem(newName, newPrice, newLoc, newBrand);
                Toast.makeText(getActivity(),"Updated the item, please refresh!", Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
            }
        });
        btnClear.setOnClickListener(v -> {
            mEditName.setText(null);
            mEditPrice.setText(null);
            mEditLoc.setText(null);
            mEditBrand.setText(null);
        });
    }
    private void editItem(String newName, String newPrice, String newLoc, String newBrand){
        try {
            connect = new SQLClass().conClass();
            if (connect !=null) {
                String query ="UPDATE Items Set price = " + newPrice + ", name = '" + newName + "', location = '" + newLoc +"', brand='" + newBrand + "' WHERE price = "+itemPrice+ " and name = '"+itemName+"' and location = '"+itemLoc+"' and brand = '"+itemBrand+"'";
                Statement st = connect.createStatement();
                st.executeQuery(query);
                connect.close();
            }
        }catch (Exception ex) {
            Log.e("set error", ex.getMessage());
        }
    }
    private void deleteItem() {

        try {
            connect = new SQLClass().conClass();
            if (connect !=null) {
                String query ="select * from Items WHERE price = "+itemPrice+ " and name = '"+itemName+"' and location = '"+itemLoc+"' and brand = '"+itemBrand+"'";
                Statement st = connect.createStatement();
                ResultSet resultSet = st.executeQuery(query);
                while (resultSet.next()) {
                   newItemID = resultSet.getString("item_id");
                }

                String query2 ="DELETE FROM display WHERE item_id = "+newItemID;
                st.executeUpdate(query2);

                String query3 ="DELETE FROM Items WHERE price = "+itemPrice+ " and name = '"+itemName+"' and location = '"+itemLoc+"' and brand = '"+itemBrand+"'";
                st.executeUpdate(query3);
                connect.close();
            }
        }catch (Exception ex) {
            Log.e("set error", ex.getMessage());
        }
    }
}
