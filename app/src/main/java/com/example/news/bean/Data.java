package com.example.news.bean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/1.
 */
public class Data {

    public ArrayList<NewsData> authors;
    public TopData category;

    /*
        新闻数据
         */
    public class NewsData{
        public String id;
        public String avatar;//图片
        public boolean followStatus;//状态
        public String intro;//描述
        public String nickname;//昵称
        public String subscriptionNum;//订阅号

    }
    /*
    顶部数据
     */
    public class TopData{
        public String id;
        public String categoryName;

        @Override
        public String toString() {
            return categoryName;
        }
    }
}
