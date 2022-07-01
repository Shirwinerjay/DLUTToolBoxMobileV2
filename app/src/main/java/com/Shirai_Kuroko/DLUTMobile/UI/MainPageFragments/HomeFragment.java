package com.Shirai_Kuroko.DLUTMobile.UI.MainPageFragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.Shirai_Kuroko.DLUTMobile.MainActivity;
import com.Shirai_Kuroko.DLUTMobile.R;
import com.Shirai_Kuroko.DLUTMobile.Entities.GridAppID;
import com.Shirai_Kuroko.DLUTMobile.UI.ServiceManagement.AppGridManageActivity;
import com.Shirai_Kuroko.DLUTMobile.UI.InnerBrowsers.BrowserActivity;
import com.Shirai_Kuroko.DLUTMobile.Helpers.ConfigHelper;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    MainGridAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MainActivity.SetActionBarTitle("首页");
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
    public int CatagorySelected = Color.argb(180,228,245,255);
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GridView mgv = requireView().findViewById(R.id.main_grid);
        adapter = new MainGridAdapter();
        if(mgv!=null)
        {
            mgv.setAdapter(adapter);
        }
        else {
            Toast.makeText(requireActivity(),"错误：未检测到主界面GridView",Toast.LENGTH_SHORT).show();
        }
        CardView cv = requireView().findViewById(R.id.HomeCardView);
        if(cv!=null)
        {
            cv.setCardBackgroundColor(CatagorySelected);
        }
    }



    class MainGridAdapter extends BaseAdapter
    {
        private List<GridAppID> mList;

        public MainGridAdapter()
        {
            this.mList= ConfigHelper.GetGridIDList(requireContext());
        }

        public void showallgriditems() {
            ArrayList<GridAppID> gai =ConfigHelper.GetGridIDList(requireActivity());
            gai.add(new GridAppID(-1));
            mList = gai;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
        @SuppressLint({"InflateParams", "SetTextI18n"})
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GridItemView gridItemView;
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.main_grid_item, null);
                // 实例化一个封装类GridItemView，并实例化它的域
                gridItemView = new GridItemView();
                gridItemView.Icon=convertView.findViewById(R.id.main_grid_icon);
                gridItemView.Name=convertView.findViewById(R.id.main_grid_name);
                // 将GridItemView对象传递给convertView
                convertView.setTag(gridItemView);
            } else {
                // 从convertView中获取ListItemView对象
                gridItemView = (GridItemView) convertView.getTag();
            }

            // 获取到mList中指定索引位置的资源
            int id = mList.get(position).getId();
            if(id!=-1)
            {
                convertView.setOnClickListener(v -> {
                    Intent intent=new Intent(requireContext(), BrowserActivity.class);
                    intent.putExtra("App_ID",String.valueOf(id));
                    startActivity(intent);
                });
                // 将资源传递给GridItemView的两个域对象
                Glide.with(requireActivity()).load(ConfigHelper.getmlist(requireContext()).get(id).getIcon()).into(gridItemView.Icon);
                gridItemView.Name.setText(ConfigHelper.getmlist(requireContext()).get(id).getAppName());
            }
            else
            {
                convertView.setOnClickListener(v -> {
                    Intent intent=new Intent(requireContext(), AppGridManageActivity.class);
                    startActivity(intent);
                });
                Glide.with(requireActivity()).load(R.drawable.app_center_pre).into(gridItemView.Icon);
                gridItemView.Name.setText("管理服务");
            }
            return convertView;
        }
    }

    static class GridItemView {
        ImageView Icon;
        TextView Name;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapter!=null)
        {
            adapter.showallgriditems();
        }
    }
}