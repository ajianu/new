package com.example.news;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.KeyEvent;

import com.example.news.fragment.impl.ContentFragment;
import com.example.news.utils.DialogUtil;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainActivity extends Activity {
    private final String LEFTMENU="leftmenu";
    private final String CONTENT="content";
    public SlidingMenu menu;
    public final static int ID_DIALOG_EXIT_APP=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        initMenu();
        initFragment();
    }

    private void initMenu() {
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        // 设置触摸屏幕的模式
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        // 设置滑动菜单视图的宽度
        menu.setBehindOffset(300);
        // 设置渐入渐出效果的值
        menu.setFadeDegree(0.35f);
        /**
         * SLIDING_WINDOW will include the Title/ActionBar in the content
         * section of the SlidingMenu, while SLIDING_CONTENT does not.
         */
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        //为侧滑菜单设置布局
        menu.setMenu(R.layout.left_menu);
    }

    private void initFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        //ft.replace(R.id.fl_left_menu, new LeftFragment(this), LEFTMENU);
        ft.replace(R.id.fl_content, new ContentFragment(this), CONTENT);
        ft.commit();
    }

    public ContentFragment getContentFragment(){
        FragmentManager fm = getFragmentManager();
        ContentFragment fragment= (ContentFragment) fm.findFragmentByTag(CONTENT);
        return fragment;
    }


    private void showConfirmDialog(int id){
        switch (id){
            case ID_DIALOG_EXIT_APP:
                DialogUtil.showDialog(MainActivity.this, "亲！真的要残忍的离开吗？");
                break;
        }
    }

    public boolean onKeyDown(int keyCode,KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
           /* AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("提示信息");
            builder.setMessage("亲，真的要残忍的离开吗？");
            builder.setPositiveButton("真的", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNegativeButton("骗你的", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.create().show();
            */

            showConfirmDialog(ID_DIALOG_EXIT_APP);
        }
        return super.onKeyDown(keyCode, event);
    }

}
