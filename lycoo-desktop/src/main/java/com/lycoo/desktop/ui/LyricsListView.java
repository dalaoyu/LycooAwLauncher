package com.lycoo.desktop.ui;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.lycoo.commons.util.LogUtils;
import com.lycoo.desktop.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LyricsListView extends LinearLayout {

    private MyRecyclerView mRLView;
    private View mRootView;
    private LyAdapter mLyAdapter;

    public LyricsListView(Context context) {
        this(context,null);
    }

    public LyricsListView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LyricsListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mRootView= LayoutInflater.from(context).inflate(R.layout.layout_lyrics,null);
        mRLView=mRootView.findViewById(R.id.rlv_lyrics);
        addView(mRootView);
        mLyAdapter=new LyAdapter(context);
        mRLView.setLayoutManager(new LinearLayoutManager(context));
//        mRLView.setEmptyView(mRootView.findViewById(R.id.img_empty));
        mRLView.addItemDecoration(new DividerItemDecoration(context,DividerItemDecoration.VERTICAL));
        mRLView.setAdapter(mLyAdapter);
        //mHandler.sendEmptyMessage(100);

    }

    public void updateItemData(String data){
        if (data==null)
            return;
        mLyAdapter.addItem(data);
        mRLView.scrollToPosition(mLyAdapter.getsize()-1);
    }

    public void setEmptyView(boolean b){
        if (b){
            mRLView.setEmptyView(mRootView.findViewById(R.id.img_empty));
        }
    }

    public void clearAllData(){
        mLyAdapter.clearData();
        setEmptyView(false);
    }


    class  LyAdapter extends RecyclerView.Adapter<LyAdapter.LyHodler>{

        private Context mContext;
        private List<String> mListData=null;

        public LyAdapter(Context context){
            this.mContext=context;
            mListData=new ArrayList<>();
        }

        public int getsize(){
            return mListData!=null?mListData.size():0;
        }


        public void addItem(String item){

            mListData.add(item);
            notifyDataSetChanged();
        }

        public void clearData(){
            mListData.clear();
            notifyDataSetChanged();
        }
        @NonNull
        @Override
        public LyHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            return new LyHodler(LayoutInflater.from(mContext).inflate(R.layout.item_lyrics,null));
        }

        @Override
        public void onBindViewHolder(@NonNull LyHodler holder, int position) {
            holder.mTvName.setText(""+mListData.get(position));
        }

        @Override
        public int getItemCount() {
            return mListData!=null?mListData.size():0;
        }


        class LyHodler extends RecyclerView.ViewHolder{

            public TextView mTvName;
            private LinearLayout mRootLin;
            public LyHodler(@NonNull View itemView) {
                super(itemView);
                Resources resources = mContext.getResources();
                DisplayMetrics dm = resources.getDisplayMetrics();
                int screenWidth = dm.widthPixels;
                int screenHeight = dm.heightPixels;
                mTvName=itemView.findViewById(R.id.tv_lyricss);
                mRootLin=itemView.findViewById(R.id.root_lin);
                ViewGroup.LayoutParams params=new LayoutParams(screenWidth/2, ViewGroup.LayoutParams.WRAP_CONTENT);
                mRootLin.setLayoutParams(params);
                mRootLin.setPadding(6,6,6,6);
            }
        }
    }
}
