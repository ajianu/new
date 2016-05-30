package com.example.news.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * Created by Administrator on 2016/5/3.
 */
public class BitmapUtils {

    public static Bitmap getBitmap(int resId, Activity activity) {
        int screenWidth=activity.getWindowManager().getDefaultDisplay().getWidth();
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeResource(activity.getResources(),resId,options);
        int imageWidth=options.outWidth;
        int inSampleSize=1;
        if(screenWidth<imageWidth){
            inSampleSize=imageWidth/screenWidth;
        }
        options.inSampleSize=inSampleSize;
        options.inJustDecodeBounds=false;

        Bitmap bitmap=BitmapFactory.decodeResource(activity.getResources(),resId,options);
        Bitmap copyBitmap=Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),bitmap.getConfig());

        Canvas canvas=new Canvas(copyBitmap);
        canvas.drawBitmap(bitmap,new Matrix(),new Paint());
        return copyBitmap;
    }
}
