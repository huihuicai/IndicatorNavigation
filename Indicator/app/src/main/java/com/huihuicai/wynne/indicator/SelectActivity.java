package com.huihuicai.wynne.indicator;

import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.huihuicai.wynne.indicator.bean.FirstBean;
import com.huihuicai.wynne.indicator.bean.SecondBean;
import com.huihuicai.wynne.indicator.bean.ShowBean;
import com.huihuicai.wynne.indicator.bean.ThirdBean;
import com.huihuicai.wynne.indicator.view.MultiTreeSelector;

import java.util.ArrayList;
import java.util.List;

public class SelectActivity extends AppCompatActivity implements MultiTreeSelector.SelectListener {

    private MultiTreeSelector selector;
    private RecyclerView rvList;
    private LayoutInflater inflater;
    private List<FirstBean> mAllData = new ArrayList<>();
    private List<ShowBean> mShowList = new ArrayList<>();
    private int[] levelId = new int[3];
    private int mCurrentLevel = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        inflater = LayoutInflater.from(this);
        selector = (MultiTreeSelector) findViewById(R.id.select);
        rvList = (RecyclerView) findViewById(R.id.rv_list);
        selector.setSelectListener(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvList.setLayoutManager(manager);
        rvList.setAdapter(mAdapter);
        rvList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 2;
            }
        });
        initData();
        updateData(0);
    }

    private void initData() {
        List<SecondBean> secondList;
        List<ThirdBean> thirdList;
        for (int i = 0; i < 15; i++) {
            secondList = new ArrayList<>();
            for (int j = 0; j < 10; j++) {
                thirdList = new ArrayList<>();
                for (int k = 0; k < 5; k++) {
                    ThirdBean third = new ThirdBean(k, "first-" + i + "->second-" + j + "->third-" + k);
                    thirdList.add(third);
                }
                SecondBean second = new SecondBean(j, "first-" + i + "->second-" + j, thirdList);
                secondList.add(second);
            }
            FirstBean first = new FirstBean(i, "first-" + i, secondList);
            mAllData.add(first);
        }
    }

    private List<ShowBean> getLevelData(int level, int... id) {
        if (mAllData == null || level > id.length - 1) {
            return mShowList;
        }
        mShowList.clear();
        if (level == 0) {
            for (int i = 0; i < mAllData.size(); i++) {
                ShowBean bean = new ShowBean();
                bean.id = mAllData.get(i).getId();
                bean.name = mAllData.get(i).getName();
                mShowList.add(bean);
            }
        } else if (level == 1) {
            for (int i = 0; i < mAllData.size(); i++) {
                if (id[0] == mAllData.get(i).getId()) {
                    List<SecondBean> list = mAllData.get(i).getList();
                    if (list == null) {
                        break;
                    }
                    for (int j = 0; j < list.size(); j++) {
                        ShowBean bean = new ShowBean();
                        bean.id = list.get(j).getId();
                        bean.name = list.get(j).getName();
                        mShowList.add(bean);
                    }
                    break;
                }
            }
        } else if (level == 2) {
            for (int i = 0; i < mAllData.size(); i++) {
                if (id[0] == mAllData.get(i).getId()) {//遍历第一层
                    List<SecondBean> list = mAllData.get(i).getList();
                    if (list == null) {
                        break;
                    }
                    for (int j = 0; j < list.size(); j++) {//遍历第二层
                        if (id[1] == list.get(j).getId()) {
                            List<ThirdBean> group = list.get(j).getList();
                            if (group == null) {
                                break;
                            }
                            for (int k = 0; k < group.size(); k++) {//遍历第三层
                                ShowBean bean = new ShowBean();
                                bean.id = group.get(k).getId();
                                bean.name = group.get(k).getName();
                                mShowList.add(bean);
                            }
                            break;
                        }
                    }
                    break;
                }
            }
        }
        return mShowList;
    }

    private void updateData(int level) {
        getLevelData(level, levelId);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 回调控件中选择的方法
     */
    @Override
    public void select(boolean isCreate, int level) {
        mCurrentLevel = level;
        updateData(level);
    }

    public class Holder extends RecyclerView.ViewHolder {
        public TextView tv;

        public Holder(View itemView) {
            super(itemView);
            tv = (TextView) itemView;
        }
    }

    private RecyclerView.Adapter<Holder> mAdapter = new RecyclerView.Adapter<Holder>() {

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.item_list, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, final int position) {
            if (mShowList.size() <= position) {
                return;
            }
            final String selectStr = mShowList.get(position).name;
            holder.tv.setText(selectStr);
            holder.tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCurrentLevel >= 2) {
                        Toast.makeText(SelectActivity.this, "选择完成", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    levelId[mCurrentLevel] = mShowList.get(position).id;
                    selector.newTab(selectStr, "");
                }
            });
        }

        @Override
        public int getItemCount() {
            return mShowList.size();
        }
    };
}
