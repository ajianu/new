package com.example.news.pager;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.news.MainActivity;
import com.example.news.R;
import com.example.news.bean.Data;
import com.example.news.global.GlobalContent;
import com.example.news.view.RefreshListView;
import com.example.news.view.TopNewsViewPager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.viewpagerindicator.CirclePageIndicator;
import java.util.ArrayList;


/**
 * Created by Administrator on 2016/5/1.
 */
public class TabDetailPager{
    public Activity activity;
    public View mRootView;
    private CirclePageIndicator indicator;
    private TextView tv_title;//轮播图title
    private TopNewsViewPager vp_news;//轮播图viewpager

    private ArrayList<Data.NewsData> newsDatas;//新闻数据

    private boolean isMore=false;//增加更多数据？
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    int currentItem=vp_news.getCurrentItem();
                    if(currentItem==3){
                        currentItem=-1;
                    }
                    vp_news.setCurrentItem(++currentItem);
                    sendEmptyMessageDelayed(0,3000);
                    break;
                case 1://下拉刷新
                    lv_list.onRefreshComplete(false);
                    break;
                case 2://加载更多
                    lv_list.onRefreshComplete(true);
                    listAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    private String[] topText=new String[]{
            "第一张","第二张","第三张","第四张",
    };
    private int[] arr=new int[]{
            R.mipmap.a1, R.mipmap.a2, R.mipmap.a3, R.mipmap.a4
    };
    private RefreshListView lv_list;
    private MyAdapter listAdapter;//listview---adapter


    public TabDetailPager(Activity activity) {
        this.activity = activity;
        mRootView=initView();
    }

    public View initView() {
        View view=View.inflate(activity, R.layout.tab_detail_pager,null);
        lv_list = (RefreshListView) view.findViewById(R.id.lv_list);

        //add topnews
        View topnewsView=View.inflate(activity, R.layout.list_header_topnews,null);
        vp_news = (TopNewsViewPager) topnewsView.findViewById(R.id.vp_news);
        tv_title = (TextView) topnewsView.findViewById(R.id.tv_title);
        indicator = (CirclePageIndicator) topnewsView.findViewById(R.id.indicator);
        lv_list.addHeaderView(topnewsView);

        lv_list.setOnRefreshListener(new RefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(activity,"下拉刷新",Toast.LENGTH_SHORT).show();
                handler.sendEmptyMessageDelayed(1,2000);
            }
            @Override
            public void onLoadMore() {
                getMoreDataFromServer();//获取更多数据
            }
        });
        vp_news.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        handler.removeMessages(0);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        handler.sendEmptyMessageDelayed(0,3000);
                        break;
                    case MotionEvent.ACTION_UP:
                        handler.sendEmptyMessageDelayed(0,3000);
                        break;
                }
                return false;
            }
        });
        return view;
    }

    public void initData(){
        getDataFromServer();

        vp_news.setAdapter(new TopNewsPagerAdapter());
        indicator.setViewPager(vp_news);
        indicator.setSnap(true);
        indicator.onPageSelected(0);// 让指示器重新定位到第一个点
        handler.sendEmptyMessageDelayed(0,3000);
        tv_title.setText(topText[0]);//
        vp_news.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                tv_title.setText(topText[position]);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });//设置轮播图textview
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
                isMore=false;
                parseData(result,isMore);
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
    private void parseData(String result,boolean isMore) {
        MainActivity mainActivity= (MainActivity) activity;
        int currentPosition=mainActivity.getContentFragment().getPosition();
        Gson gson=new Gson();
        ArrayList<Data> datas = new ArrayList<>();
        datas =gson.fromJson(result,new TypeToken<ArrayList<Data>>(){}.getType());//解析数据
        if (isMore){
            newsDatas.addAll(datas.get(currentPosition).authors);
        }else{
            newsDatas = datas.get(currentPosition).authors;
            listAdapter = new MyAdapter();
            lv_list.setAdapter(listAdapter);
        }


    }

    /*
        从服务端请求更多数据
     */
    public void getMoreDataFromServer() {
        HttpUtils utils=new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, GlobalContent.SERVER_URL, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result=responseInfo.result;
                isMore=true;
                parseData(result,isMore);
                handler.sendEmptyMessageDelayed(2,1000);

            }
            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(activity,"请求失败",Toast.LENGTH_SHORT).show();
                lv_list.onRefreshComplete(true);
            }
        });
    }
    /*
    viewpager  --adapter
     */
    class TopNewsPagerAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return arr.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView iv=new ImageView(activity);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            //iv.setImageResource(arr[position]);
            iv.setImageBitmap(com.example.news.utils.BitmapUtils.getBitmap(arr[position],activity));
            container.addView(iv);
            return iv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    /*
    listview --adapter
     */
    class MyAdapter extends BaseAdapter{
        private BitmapUtils utils;
        public MyAdapter() {
               utils=new BitmapUtils(activity);
        }
        @Override
        public int getCount() {
            return newsDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView==null){
                holder=new ViewHolder();
                convertView=View.inflate(activity,R.layout.list_news_item,null);
                holder.iv_avatar= (ImageView) convertView.findViewById(R.id.iv_avatar);
                holder.tv_nickname= (TextView) convertView.findViewById(R.id.tv_nickname);
                holder.tv_intro= (TextView) convertView.findViewById(R.id.tv_intro);
                holder.tv_subscriptionNum= (TextView) convertView.findViewById(R.id.tv_subscriptionNum);
                convertView.setTag(holder);
            }else{
                holder= (ViewHolder) convertView.getTag();
            }
            Data.NewsData newsData=newsDatas.get(position);
            utils.display(holder.iv_avatar,newsData.avatar);//设置图片

            holder.tv_nickname.setText(newsData.nickname);
            holder.tv_intro.setText(newsData.intro);
            holder.tv_subscriptionNum.setText(newsData.subscriptionNum+"跟帖");
            return convertView;
        }
    }
    static class ViewHolder{
        ImageView iv_avatar;
        TextView tv_nickname;
        TextView tv_intro;
        TextView tv_subscriptionNum;
    }
}
