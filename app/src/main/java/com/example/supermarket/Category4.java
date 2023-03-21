package com.example.supermarket;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SearchView;
import java.sql.Connection;

public class Category4 extends AppCompatActivity {
    MyAdapter adapter;
    Connection connect;
    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category4);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        User user = new User();

        //Add button
        Button addButton = findViewById(R.id.btnAdd);
        if (user.isAdmin()==false) {
            addButton.setVisibility(View.INVISIBLE);
        }
        else if (user.isAdmin()==true) {
            addButton.setVisibility(View.VISIBLE);
        }
        addButton.setOnClickListener(v -> {
            showAddDialog();
        });

        GridView list = findViewById(R.id.Act_Category4Items);
        list.setOnItemClickListener((parent, view, position, id) -> {
            if (user.isAdmin()==true) {
                Log.i("Gridview DEBUG", "onItemClick position=" + position);
                String item = parent.getItemAtPosition(position).toString();
                // Log.i("Gridview DEBUG", "ItemName=" + itemName);
                String []array=item.split("[,{}]");
                String itemPrice=array[1].substring(6);
                String itemName=array[2].substring(6);
                String itemLocation=array[3].substring(10);
                String itemBrand=array[4].substring(7);
           /* for (int i=1; i<=3;i++){
                Log.i("Gridview DEBUG", array[i]);
            }*/
                Log.i("Gridview DEBUG", itemPrice);
                Log.i("Gridview DEBUG", itemName);//substring
                Log.i("Gridview DEBUG", itemLocation);
                showEditDialog(itemName,itemPrice,itemLocation, itemBrand);
            }
            else if (!user.isAdmin()){
                Log.i("Gridview DEBUG", "onItemClick position=" + position);
                String item = parent.getItemAtPosition(position).toString();
                Log.i("Gridview DEBUG", "Item=" + item);
                String []array=item.split("[,{}]");
                String itemPrice=array[1].substring(6);
                String itemName=array[2].substring(6);
                String itemLocation=array[3].substring(10);
                String itemBrand=array[4].substring(7);

           /* for (int i=1; i<=3;i++){
                Log.i("Gridview DEBUG", array[i]);
            }*/
                Log.i("Gridview DEBUG", itemPrice);
                Log.i("Gridview DEBUG", itemName);//substring
                Log.i("Gridview DEBUG", itemLocation);
                Log.i("Gridview DEBUG", itemBrand);
                showAddToCartDialog(itemName,itemPrice,itemLocation,itemBrand);
            }
        });
        SQLClass a = new SQLClass();
        a.getCategory(list,4,context, adapter, connect);
        adapter = a.getAdapter();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(this, MainActivity.class);
        startActivity(myIntent);
        return true;
    }

    private void showAddDialog(){
        FragmentManager fm = getSupportFragmentManager();
        AddItemDialogFragment addItemDialogFragment = AddItemDialogFragment.newInstance();
        addItemDialogFragment.show(fm, "fragment_add_name");
    }

    private void showEditDialog(String itemName, String itemPrice, String itemLocation, String itemBrand) {
        FragmentManager fm = getSupportFragmentManager();
        EditItemDialogFragment editNameDialogFragment = EditItemDialogFragment.newInstance(itemName,Integer.parseInt(itemPrice), itemLocation, itemBrand);
        editNameDialogFragment.show(fm, "fragment_edit_name");
    }

    private void showAddToCartDialog(String itemName, String itemPrice, String itemLocation, String itemBrand) {
        FragmentManager fm = getSupportFragmentManager();
        AddToCartDialogFragment addToCartDialogFragment = AddToCartDialogFragment.newInstance(itemName,Integer.parseInt(itemPrice), itemLocation, itemBrand);
        addToCartDialogFragment.show(fm, "fragment_add_to_cart");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type here to search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query.trim());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText.trim());
                return false;
            }
        });
        return true;
    }

}