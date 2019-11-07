package com.yueyou.adreader.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.graphics.BitmapFactory.decodeFile;

/**
 * Created by zy on 2017/4/19.
 */

public class Image {

    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h){
        try {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Matrix matrix = new Matrix();
            float scaleWidth = ((float) w / width);
            float scaleHeight = ((float) h / height);
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                    matrix, true);
            return newBmp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void compressImage(String imgName, int w, int h, String outImgName) {
        bmpToFile(compressImage(imgName, w, h), outImgName);
    }

    public static String compressImageToBase64(String imgName, int w, int h, String outImageName){
        Bitmap bitmap = compressImage(imgName, w, h);
        String base64 = bitmapToBase64(bitmap);
        bmpToFile(bitmap, outImageName);
        return base64;
    }

    private static Bitmap compressImage(String imgName, int w, int h){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        decodeFile(imgName, options);
        int inSampleSize = calculateInSampleSize(options, w, h);
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = decodeFile(imgName, options);
        return bitmap;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight){
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static void bmpToFile(Bitmap bitmap, String fileName){
        try {
            FILE.delete(fileName);
            FileOutputStream out = new FileOutputStream(fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (!bitmap.isRecycled())
                bitmap.recycle();
        }
    }

    private static String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
