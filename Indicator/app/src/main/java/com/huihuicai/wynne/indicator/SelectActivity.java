package com.huihuicai.wynne.indicator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        inflater = LayoutInflater.from(this);
        selector = (MultiTreeSelector) findViewById(R.id.select);
        rvList = (RecyclerView) findViewById(R.id.rv_list);
        rvList.setAdapter(mAdapter);
        initData();
    }

    private void initData() {
        List<SecondBean> secondList;
        List<ThirdBean> thirdList;
        for (int i = 0; i < 15; i++) {
            secondList = new ArrayList<>();
            for (int j = 0; j < 10; j++) {
                thirdList = new ArrayList<>();
                for (int k = 0; k < 5; k++) {
                    ThirdBean third = new ThirdBean(k, "third-" + k);
                    thirdList.add(third);
                }
                SecondBean second = new SecondBean(j, "second-" + j, thirdList);
                secondList.add(second);
            }
            FirstBean first = new FirstBean(i, "first-" + i, secondList);
            mAllData.add(first);
        }
    }

    private List<ShowBean> getLevelData(int level, int... id) {
        if (mAllData == null || level + 1 != id.length) {
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
                }
            }
        } else if (level == 2) {
            for (int i = 0; i < mAllData.size(); i++) {
                if (id[0] == mAllData.get(i).getId()) {
                    List<SecondBean> list = mAllData.get(i).getList();
                    if (list == null) {
                        break;
                    }
                    for (int j = 0; j < list.size(); j++) {
                        List<ThirdBean> group = list.get(j).getList();
                        if (group == null) {
                            break;
                        }
                        if (id[1] == group.get(j).getId()) {
                            for (int k = 0; k < list.size(); k++) {
                                ShowBean bean = new ShowBean();
                                bean.id = list.get(k).getId();
                                bean.name = list.get(k).getName();
                                mShowList.add(bean);
                            }
                        }
                    }
                }
            }
        }
        return mShowList;
    }

    @Override
    public void select(int level) {
        getLevelData(level, levelId);
        mAdapter.notifyDataSetChanged();
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
        public void onBindViewHolder(Holder holder, int position) {
            final String selectStr = mShowList.size() > position ? mShowList.get(position).name : "";
            holder.tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
