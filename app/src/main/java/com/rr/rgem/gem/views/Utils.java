package com.rr.rgem.gem.views;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created by chris on 9/8/2016.
 */
public class Utils {
    public static void toast(Context context, CharSequence text){
        int duration = android.widget.Toast.LENGTH_SHORT;

        android.widget.Toast toast = android.widget.Toast.makeText(context, text, duration);
        toast.show();
    }

    public static boolean fileExists(File file)
    {
        return file.exists();
    }

    public static File getFileFromName(String fileName, Context context)
    {
        ContextWrapper wrapper = new ContextWrapper(context);
        File directory = context.getDir("imageDir", Context.MODE_PRIVATE);
        File path = new File(directory, fileName);
        return path;
    }

    public static void writeToFile(Bitmap image, File file)
    {
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String formatNumber(long number)
    {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(number);
    }
}
