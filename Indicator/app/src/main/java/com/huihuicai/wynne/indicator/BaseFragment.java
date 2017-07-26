package com.huihuicai.wynne.indicator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by ybm on 2017/6/28.
 */

public class BaseFragment extends Fragment {

    private TextView rootView;

    public static BaseFragment getInstance(String content) {
        Bundle bundle = new Bundle();
        bundle.putString("content", content);
        BaseFragment fragment = new BaseFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = new TextView(container.getContext());
        rootView.setGravity(Gravity.CENTER);
        rootView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String content = bundle.getString("content");
            rootView.setText(content);
        }
    }
}
