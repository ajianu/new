package com.example.news.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2016/5/1.
 */
public abstract class BaseFragment extends Fragment {
    public Activity activity;
    public View fragmentView;

    public BaseFragment(Activity activity) {
        this.activity = activity;
        fragmentView=initView();
        initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return fragmentView;
    }

    public abstract View initView();

    public void initData(){

    }
}
