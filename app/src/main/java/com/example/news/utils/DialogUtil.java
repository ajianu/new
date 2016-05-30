package com.example.news.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.news.R;

/**
 * Created by Administrator on 2016/5/6.
 */
public class DialogUtil {

    private static AlertDialog mAlertDialog;
    public static View getView(Context context,int layoutId){
        LayoutInflater inflater=(LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout=inflater.inflate(layoutId,null);
        return layout;
    }
    //界面跳转
    public static void startActivity(Context context,Class desti){
        Intent intent=new Intent();
        intent.setClass(context, desti);
        context.startActivity(intent);
        //关闭当前的Activity
        ((Activity)context).finish();
    }

    public static void showDialog(final Context context,String message){

        View dialogView=null;
        AlertDialog.Builder builder=new AlertDialog.Builder(context,R.style.Theme_AppCompat);
        dialogView=getView(context, R.layout.dialog_view);

        ImageButton btnOkView= (ImageButton) dialogView.findViewById(R.id.btn_dialog_ok);
        ImageButton btnCancelView= (ImageButton) dialogView.findViewById(R.id.btn_dialog_cancel);
        TextView txtMessageView= (TextView) dialogView.findViewById(R.id.text_dialog_message);
        txtMessageView.setText(message);
        btnOkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        btnCancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAlertDialog!=null) {
                    mAlertDialog.cancel();
                }
            }
        });

        //为dialog设置view
        builder.setView(dialogView);
        mAlertDialog=builder.create();
        //显示对话框
        mAlertDialog.show();
    }
}
