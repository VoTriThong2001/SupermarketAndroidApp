package com.example.supermarket;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    MyAdapter adapter;
    Connection connect;
    private String mParam1;
    private String mParam2;
    private Button btnCategory1, btnCategory2, btnCategory3, btnCategory4, btnCategory5, btnCategory6;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        btnCategory1 = view.findViewById(R.id.btnCategory1);
        btnCategory1.setOnClickListener(view1 -> openCategory1());

        btnCategory2 = view.findViewById(R.id.btnCategory2);
        btnCategory2.setOnClickListener(view1 -> openCategory2());

        btnCategory3 = view.findViewById(R.id.btnCategory3);
        btnCategory3.setOnClickListener(view1 -> openCategory3());

        btnCategory4 = view.findViewById(R.id.btnCategory4);
        btnCategory4.setOnClickListener(view1 -> openCategory4());

        btnCategory5 = view.findViewById(R.id.btnCategory5);
        btnCategory5.setOnClickListener(view1 -> openCategory5());

        btnCategory6 = view.findViewById(R.id.btnCategory6);
        btnCategory6.setOnClickListener(view1 -> openCategory6());


        GridView list1 = view.findViewById(R.id.Category1Items);
        getCategory(list1,1);


        GridView list2 = view.findViewById(R.id.Category2Items);
        getCategory(list2,2);

        GridView list3 = view.findViewById(R.id.Category3Items);
        getCategory(list3,3);

        GridView list4 = view.findViewById(R.id.Category4Items);
        getCategory(list4,4);

        GridView list5 = view.findViewById(R.id.Category5Items);
        getCategory(list5,5);

        GridView list6 = view.findViewById(R.id.Category6Items);
        getCategory(list6,6);

        return view;
    }

    private void openCategory1() {
        Intent intent = new Intent(getActivity(), Category1.class);
        startActivity(intent);
    }
    private void openCategory2() {
        Intent intent = new Intent(getActivity(), Category2.class);
        startActivity(intent);
    }
    private void openCategory3() {
        Intent intent = new Intent(getActivity(), Category3.class);
        startActivity(intent);
    }

    private void openCategory4() {
        Intent intent = new Intent(getActivity(), Category4.class);
        startActivity(intent);
    }
    private void openCategory5() {
        Intent intent = new Intent(getActivity(), Category5.class);
        startActivity(intent);
    }
    private void openCategory6() {
        Intent intent = new Intent(getActivity(), Category6.class);
        startActivity(intent);
    }
    private void Logout() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }


    private void getCategory(GridView list, int menu_id){
        List<Map<String,String>> data = new ArrayList<Map<String,String>>();
        try {
            connect = new SQLClass().conClass();
            if (connect !=null) {
                String query ="select TOP 5 * from Items i inner join display d on i.item_id = d.item_id inner join Menu_group m on m.mID = d.mID where m.mID = " + menu_id;
                Statement st = connect.createStatement();
                ResultSet resultSet = st.executeQuery(query);

                while (resultSet.next()) {
                    Map<String, String> tab = new HashMap<String, String>();
                    tab.put("name", resultSet.getString("name"));
                    tab.put("price", resultSet.getString("price"));
                    tab.put("brand", resultSet.getString("brand"));
                    tab.put("location", resultSet.getString("location"));

                    String[] name = resultSet.getString("name").split(" ");
                    int nameSplits = name.length;
                    String newName = "";
                    for (int i=0; i<nameSplits; i ++) {
                        newName += name[i];
                    }
                    Log.i("FILE NAME", "newName: " +newName);
                   Blob imageBlob = (resultSet.getBlob("image"));
                    int blobLength = (int) imageBlob.length();
                    byte[] imageByte = imageBlob.getBytes(1,blobLength);
                    Bitmap bmp= BitmapFactory.decodeByteArray(imageByte, 0 , blobLength);
                    Uri tempUri = getImageUri(getActivity(),bmp,newName);
                 //   Log.i("Uri", "tempUri: " +tempUri);
                    String FileUri = String.valueOf(tempUri);
                    tab.put("image", FileUri);
                    data.add(tab);
                }
                String[] from ={"name", "price","brand", "location", "image"};
                int[] to ={R.id.textCategory1_ItemName, R.id.textCategory1_ItemPrice, R.id.textCategory1_ItemBrand,R.id.textCategory1_ItemLoc, R.id.textCategory1_ItemPhoto};
                adapter = new MyAdapter(getActivity(), data, R.layout.category_layout, from, to);

                list.setAdapter(adapter);
                connect.close();
            }
        }catch (Exception ex) {
            Log.e("set error", ex.getMessage());
        }
    }


    public Uri getImageUri(Context context,Bitmap bitmap, String title) throws IOException {
        File tempDir= context.getCacheDir();
        tempDir=new File(tempDir.getAbsolutePath()+"/.temp/");
        Log.i("FILE", "tempDir path: " + tempDir.getAbsolutePath());
        boolean wasSuccessful = tempDir.mkdir();
       /* if (!wasSuccessful) {
            Log.i("FILE", "could not make directory");
        }*/
        File tempFile = File.createTempFile(title, ".png", tempDir);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        byte[] bitmapData = bytes.toByteArray();

        //write the bytes in file

        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(bitmapData);
        fos.flush();
        fos.close();
        context.getCacheDir().deleteOnExit();
        return Uri.fromFile(tempFile);
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        deleteCache(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                Logout();
                return true;
            }
            return false;
        });
    }
}

