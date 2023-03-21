package com.example.supermarket;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLClass {
    MyAdapter adapter;
    public Connection conClass() {
        String ip="192.168.1.100";
       // String ip="10.50.181.71";
        //String ip="10.60.29.223";
        String port="1433", database="Supermarket_customers", u="sa", pass="123456";
        StrictMode.ThreadPolicy p = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(p);
        Connection con = null;
        String conURL=null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            conURL = "jdbc:jtds:sqlserver://" + ip + ":" + port + ";" + "databaseName=" +  database + ";user=" + u + ";password=" + pass + ";";
            con= DriverManager.getConnection(conURL);
        } catch (Exception ex) {
            Log.e("set error", ex.getMessage());
        } return con;
    }



    public void getCategory(GridView list, int menu_id, Context context, MyAdapter adapter, Connection connect){
        List<Map<String,String>> data = new ArrayList<>();
        try {
            connect = new SQLClass().conClass();
            if (connect !=null) {
                String query ="select * from Items i inner join display d on i.item_id = d.item_id inner join" +
                        " Menu_group m on m.mID = d.mID where m.mID = " + menu_id;
                Statement st = connect.createStatement();
                ResultSet resultSet = st.executeQuery(query);

                while (resultSet.next()) {
                    Map<String, String> tab = new HashMap<>();
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
                    Uri tempUri = getImageUri(context,bmp,newName);
                    //   Log.i("Uri", "tempUri: " +tempUri);
                    String FileUri = String.valueOf(tempUri);
                    tab.put("image", FileUri);
                    data.add(tab);
                }
                String[] from ={"name", "price", "brand","location" , "image"};
                int[] to ={R.id.textCategory1_ItemName, R.id.textCategory1_ItemPrice,R.id.textCategory1_ItemBrand, R.id.textCategory1_ItemLoc ,R.id.textCategory1_ItemPhoto};
                adapter = new MyAdapter(context, data, R.layout.category_layout_detail, from, to);
                this.adapter = adapter;
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

    public MyAdapter getAdapter(){
        return this.adapter;
    }
}
