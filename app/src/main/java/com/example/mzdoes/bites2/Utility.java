package com.example.mzdoes.bites2;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jogic_9 on 4/18/18.
 */

public class Utility {
    public static void saveList(Context context, String key, List<Article> articleList) throws IOException {
        FileOutputStream fos = context.openFileOutput (key, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject (articleList);
        Log.d("Utility", "saveList: " + articleList.toString());
        oos.close ();
        fos.close ();

        try {
            Log.d("Utility", "saveList: " + Utility.readList(context, key));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void saveString(Context context, String key, String toSave) throws IOException {
        FileOutputStream fos = context.openFileOutput(key, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(toSave);
        oos.close();
        fos.close();
    }


    public static ArrayList<Article> readList(Context context, String key) throws IOException, ClassNotFoundException {
        FileInputStream fis = context.openFileInput (key);
        ObjectInputStream ois = new ObjectInputStream (fis);
        ArrayList<Article> articleList = (ArrayList<Article>) ois.readObject();
        return articleList;
    }

    public static String readString(Context context, String key) throws IOException, ClassNotFoundException {
        FileInputStream fis = context.openFileInput (key);
        ObjectInputStream ois = new ObjectInputStream (fis);
        String string = (String) ois.readObject();
        return string;
    }

}
