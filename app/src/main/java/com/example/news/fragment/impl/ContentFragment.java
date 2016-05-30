package com.example.news.fragment.impl;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.news.MainActivity;
import com.example.news.R;
import com.example.news.bean.Data;
import com.example.news.fragment.BaseFragment;
import com.example.news.global.GlobalContent;
import com.example.news.pager.TabDetailPager;
import com.example.news.view.NoScrollViewPager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;


/**
 * Created by Administrator on 2016/5/1.
 */
public class ContentFragment extends BaseFragment {

    private TabPageIndicator indicator;
    private NoScrollViewPager vp_content;
    private ImageView iv_icon;
    private ArrayList<Data> datas;//顶部数据
    private ArrayList<TabDetailPager> detailPagers;

    public ContentFragment(Activity activity) {
        super(activity);
    }

    @Override
    public View initView() {
        View view=View.inflate(activity, R.layout.fragment_content,null);
        iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
        indicator = (TabPageIndicator)view.findViewById(R.id.indicator);
        vp_content = (NoScrollViewPager) view.findViewById(R.id.vp_content);
        return view;
    }

    @Override
    public void initData() {
        getDataFromServer();
        iv_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity= (MainActivity) activity;
                ((MainActivity) activity).menu.toggle();
            }
        });

    }
    /*
        从服务端请求数据
     */
    public void getDataFromServer() {
        HttpUtils utils=new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, GlobalContent.SERVER_URL, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result=responseInfo.result;
                parseData(result);
            }
            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(activity,"请求失败",Toast.LENGTH_SHORT).show();
            }
        });
    }
    /*
        解析数据
     */
    private void parseData(String result) {
        Gson gson=new Gson();
        datas = new ArrayList<>();
        datas =gson.fromJson(result,new TypeToken<ArrayList<Data>>(){}.getType());//解析数据

        detailPagers = new ArrayList<>();
        for (int i=0;i<datas.size();i++){
            TabDetailPager detailPager=new TabDetailPager(activity);
            detailPagers.add(detailPager);
        }

        ContentPagerAdapter adapter=new ContentPagerAdapter();
        vp_content.setAdapter(adapter);
        indicator.setVisibility(View.VISIBLE);
        indicator.setViewPager(vp_content);

        vp_content.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                detailPagers.get(position).initData();

            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        detailPagers.get(0).initData();//初始化数据
    }

    class ContentPagerAdapter extends PagerAdapter{
        @Override
        public int getCount() {
            return datas.size();
        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return datas.get(position).category.categoryName;
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(detailPagers.get(position).mRootView);
            return detailPagers.get(position).mRootView;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    public int getPosition(){
        return vp_content.getCurrentItem();
    }

}
