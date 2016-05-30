package com.example.news.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.news.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/5/1.
 */
public class RefreshListView extends ListView implements AbsListView.OnScrollListener,
        android.widget.AdapterView.OnItemClickListener {
    private int headerviewHeight;//view的高度
    private int footerViewHeight;
    private int startY = -1;// 滑动起点的y坐标

    private View headerView;
    private View footerView;
    private ImageView iv_arr;
    private ProgressBar pb_progress;
    private TextView tv_refresh;
    private TextView tv_time;

    private RotateAnimation animUp;
    private RotateAnimation animDown;

    private static final int STATE_PULL_REFRESH = 0;// 下拉刷新
    private static final int STATE_RELEASE_REFRESH = 1;// 松开刷新
    private static final int STATE_REFRESHING = 2;// 正在刷新
    private int mCurrrentState = STATE_PULL_REFRESH;// 当前状态


    public RefreshListView(Context context) {
        super(context);
        init();
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init(){
        initHeaderView();
        initFooterView();
    }

    private void initHeaderView() {
        headerView = View.inflate(getContext(), R.layout.refresh_header,null);
        iv_arr = (ImageView) headerView.findViewById(R.id.iv_arr);
        pb_progress = (ProgressBar) headerView.findViewById(R.id.pb_progress);
        tv_refresh = (TextView) headerView.findViewById(R.id.tv_refresh);
        tv_time = (TextView) headerView.findViewById(R.id.tv_time);
        this.addHeaderView(headerView);

        headerView.measure(0,0);
        headerviewHeight=headerView.getMeasuredHeight();
        headerView.setPadding(0,-headerviewHeight,0,0);
        System.out.println(headerviewHeight+":headerviewHeight");
        initArrowAnim();
        tv_time.setText("最后刷新时间:" + getCurrentTime());
    }
    /*
 * 初始化脚布局
 */
    private void initFooterView() {
        footerView = View.inflate(getContext(),
                R.layout.refresh_listview_footer, null);

        this.addFooterView(footerView);

        footerView.measure(0, 0);
        footerViewHeight = footerView.getMeasuredHeight();
        System.out.println(footerViewHeight+":footerViewHeight");
        footerView.setPadding(0, -footerViewHeight, 0, 0);// 隐藏

        this.setOnScrollListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (startY == -1) {// 确保startY有效
                    startY = (int) ev.getRawY();
                }

                if (mCurrrentState == STATE_REFRESHING) {// 正在刷新时不做处理
                    break;
                }

                int endY = (int) ev.getRawY();
                int dy = endY - startY;// 移动便宜量

                if (dy > 0 && getFirstVisiblePosition() == 0) {// 只有下拉并且当前是第一个item,才允许下拉
                    int padding = dy - headerviewHeight;// 计算padding
                    headerView.setPadding(0, padding, 0, 0);// 设置当前padding

                    if (padding > 0 && mCurrrentState != STATE_RELEASE_REFRESH) {// 状态改为松开刷新
                        mCurrrentState = STATE_RELEASE_REFRESH;
                        refreshState();
                    } else if (padding < 0 && mCurrrentState != STATE_PULL_REFRESH) {// 改为下拉刷新状态
                        mCurrrentState = STATE_PULL_REFRESH;
                        refreshState();
                    }

                    return true;
                }

                break;
            case MotionEvent.ACTION_UP:
                startY = -1;// 重置

                if (mCurrrentState == STATE_RELEASE_REFRESH) {
                    mCurrrentState = STATE_REFRESHING;// 正在刷新
                    headerView.setPadding(0, 0, 0, 0);// 显示
                    refreshState();
                } else if (mCurrrentState == STATE_PULL_REFRESH) {
                    headerView.setPadding(0, -headerviewHeight, 0, 0);// 隐藏
                }

                break;

            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 刷新下拉控件的布局
     */
    private void refreshState() {
        switch (mCurrrentState) {
            case STATE_PULL_REFRESH:
                tv_refresh.setText("下拉刷新");
                iv_arr.setVisibility(View.VISIBLE);
                pb_progress.setVisibility(View.INVISIBLE);
                iv_arr.startAnimation(animDown);
                break;
            case STATE_RELEASE_REFRESH:
                tv_refresh.setText("松开刷新");
                iv_arr.setVisibility(View.VISIBLE);
                pb_progress.setVisibility(View.INVISIBLE);
                iv_arr.startAnimation(animUp);
                break;
            case STATE_REFRESHING:
                tv_refresh.setText("正在刷新...");
                iv_arr.clearAnimation();// 必须先清除动画,才能隐藏
                iv_arr.setVisibility(View.INVISIBLE);
                pb_progress.setVisibility(View.VISIBLE);

                if (mListener != null) {
                    mListener.onRefresh();
                }
                break;

            default:
                break;
        }
    }

    /**
     * 初始化箭头动画
     */
    private void initArrowAnim() {
        // 箭头向上动画
        animUp = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animUp.setDuration(200);
        animUp.setFillAfter(true);

        // 箭头向下动画
        animDown = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animDown.setDuration(200);
        animDown.setFillAfter(true);

    }

    OnRefreshListener mListener;


    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    public interface OnRefreshListener {
        public void onRefresh();

        public void onLoadMore();// 加载下一页数据
    }

    /*
     * 收起下拉刷新的控件
     */
    public void onRefreshComplete(boolean success) {
        if (isLoadingMore) {// 正在加载更多...
            footerView.setPadding(0, -footerViewHeight, 0, 0);// 隐藏脚布局
            isLoadingMore = false;
        } else {
            mCurrrentState = STATE_PULL_REFRESH;
            tv_refresh.setText("下拉刷新");
            iv_arr.setVisibility(View.VISIBLE);
            pb_progress.setVisibility(View.INVISIBLE);
            tv_time.setText("最后刷新时间:" + getCurrentTime());
            headerView.setPadding(0, -headerviewHeight, 0, 0);// 隐藏

            /*if (success) {
                tv_time.setText("最后刷新时间:" + getCurrentTime());
            }*/
        }
    }

    /**
     * 获取当前时间
     */
    public String getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }

    private boolean isLoadingMore;


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE
                || scrollState == SCROLL_STATE_FLING) {

            if (getLastVisiblePosition() == getCount() - 1 && !isLoadingMore) {// 滑动到最后
                System.out.println("到底了.....");
                footerView.setPadding(0, 0, 0, 0);// 显示
                setSelection(getCount() - 1);// 改变listview显示位置

                isLoadingMore = true;

                if (mListener != null) {
                    mListener.onLoadMore();
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

    }

    OnItemClickListener mItemClickListener;

    @Override
    public void setOnItemClickListener(
            android.widget.AdapterView.OnItemClickListener listener) {
        super.setOnItemClickListener(this);

        mItemClickListener = listener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        if (mItemClickListener != null) {
            mItemClickListener.onItemClick(parent, view, position
                    - getHeaderViewsCount(), id);
        }
    }
}
