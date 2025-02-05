package com.cretin.www.autoupdateproject.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.SpanUtils;
import com.cretin.www.autoupdateproject.R;
import com.cretin.www.autoupdateproject.model.ListModel;
import com.cretin.www.cretinautoupdatelibrary.model.DownloadInfo;
import com.cretin.www.cretinautoupdatelibrary.model.TypeConfig;
import com.cretin.www.cretinautoupdatelibrary.utils.AppUpdateUtils;
import com.cretin.www.cretinautoupdatelibrary.utils.ResUtils;

import java.util.List;

public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.ViewHolder> {

    private Context context;
    private List<ListModel> data;
    public String jsonDataForce = "{\"versionCode\": 25,\"isForceUpdate\": 1,\"preBaselineCode\": 24,\"versionName\": \"v2.3.1\",\"downurl\": \"http://jokesimg.cretinzp.com/apk/app-release_231_jiagu_sign.apk\",\"updateLog\": \"1、优化细节和体验，更加稳定\n2、引入大量优质用户\r\n3、修复已知bug\n4、风格修改\",\"size\": \"31338250\",\"hasAffectCodes\": \"1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24\"}";
    public String jsonDataUnForce = "{\"versionCode\": 25,\"isForceUpdate\": 0,\"preBaselineCode\": 24,\"versionName\": \"v2.3.1\",\"downurl\": \"http://jokesimg.cretinzp.com/apk/app-release_231_jiagu_sign.apk\",\"updateLog\": \"1、优化细节和体验，更加稳定\n2、引入大量优质用户\r\n3、修复已知bug\n4、风格修改\",\"size\": \"31338250\",\"hasAffectCodes\": \"1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24\"}";

    public RecyclerviewAdapter(Context context, List<ListModel> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        SpannableStringBuilder spannableStringBuilder = new SpanUtils().append("UI样式类型：")
                .append(getUiTypeDesc(data.get(position).getUiTypeValue()) + "\n")
                .setForegroundColor(ResUtils.getColor(R.color.colorAccent))
                .append("强制更新：")
                .append((data.get(position).getSourceTypeVaule() == TypeConfig.DATA_SOURCE_TYPE_URL ? "根据接口返回，当前是开启" : data.get(position).isForceUpdate() ? "开启" : "关闭") + " \n")
                .setForegroundColor(ResUtils.getColor(R.color.colorAccent))
                .append("数据加载方式：")
                .append(getSourceType(data.get(position).getSourceTypeVaule()))
                .setForegroundColor(ResUtils.getColor(R.color.colorAccent))
                .create();
        holder.tv_theme.setText(spannableStringBuilder);

        if (position != 0) {
            holder.tv_index.setText((position) + "");
        } else {
            holder.tv_index.setText("");
        }


        holder.tv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(data.get(position));
            }
        });

        holder.tv_force.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                force(data.get(position));
            }
        });

        holder.tv_source_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sourceTypeChange(data.get(position));
            }
        });
    }

    //修改数据来源
    private void sourceTypeChange(ListModel listModel) {
        if (listModel.getSourceTypeVaule() == TypeConfig.DATA_SOURCE_TYPE_MODEL) {
            listModel.setSourceTypeVaule(TypeConfig.DATA_SOURCE_TYPE_JSON);
        } else if (listModel.getSourceTypeVaule() == TypeConfig.DATA_SOURCE_TYPE_JSON) {
            listModel.setSourceTypeVaule(TypeConfig.DATA_SOURCE_TYPE_URL);
        } else {
            listModel.setSourceTypeVaule(TypeConfig.DATA_SOURCE_TYPE_MODEL);
        }
        notifyDataSetChanged();
        Toast.makeText(context, "数据来源切换成功", Toast.LENGTH_SHORT).show();
    }

    //修改强制更新的类型
    private void force(ListModel listModel) {
        if (listModel.getSourceTypeVaule() == TypeConfig.DATA_SOURCE_TYPE_URL) {
            Toast.makeText(context, "当前类型是链接请求数据，是否强制更新由接口返回", Toast.LENGTH_SHORT).show();
            return;
        }
        listModel.setForceUpdate(!listModel.isForceUpdate());
        notifyDataSetChanged();
        Toast.makeText(context, "强制更新状态切换成功", Toast.LENGTH_SHORT).show();
    }

    //开始更新
    private void update(ListModel listModel) {
        if (listModel.getSourceTypeVaule() == TypeConfig.DATA_SOURCE_TYPE_JSON) {
            //JSON
            String jsonData = listModel.isForceUpdate() ? jsonDataForce : jsonDataUnForce;
            AppUpdateUtils.getInstance().getUpdateConfig().setUiThemeType(listModel.getUiTypeValue());
            AppUpdateUtils.getInstance().getUpdateConfig().setDataSourceType(listModel.getSourceTypeVaule());
            AppUpdateUtils.getInstance().checkUpdate(jsonData);
        } else if (listModel.getSourceTypeVaule() == TypeConfig.DATA_SOURCE_TYPE_MODEL) {
            DownloadInfo info = new DownloadInfo().setApkUrl("http://jokesimg.cretinzp.com/apk/app-release_231_jiagu_sign.apk")
                    .setFileSize(31338250)
                    .setProdVersionCode(25)
                    .setProdVersionName("2.3.1")
                    .setForceUpdateFlag(listModel.isForceUpdate() ? 1 : 0)
                    .setUpdateLog("1、优化细节和体验，更加稳定\n2、引入大量优质用户\r\n3、修复已知bug\n4、风格修改");
            AppUpdateUtils.getInstance().getUpdateConfig().setUiThemeType(listModel.getUiTypeValue());
            AppUpdateUtils.getInstance().getUpdateConfig().setDataSourceType(listModel.getSourceTypeVaule());
            AppUpdateUtils.getInstance().checkUpdate(info);
        } else if (listModel.getSourceTypeVaule() == TypeConfig.DATA_SOURCE_TYPE_URL) {
            //请求
            AppUpdateUtils.getInstance().getUpdateConfig().setUiThemeType(listModel.getUiTypeValue());
            AppUpdateUtils.getInstance().getUpdateConfig().setDataSourceType(listModel.getSourceTypeVaule());
            AppUpdateUtils.getInstance().checkUpdate();
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_theme;
        private TextView tv_update;
        private TextView tv_force;
        private TextView tv_source_type;
        private TextView tv_index;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_theme = itemView.findViewById(R.id.tv_theme);
            tv_update = itemView.findViewById(R.id.tv_update);
            tv_force = itemView.findViewById(R.id.tv_force);
            tv_source_type = itemView.findViewById(R.id.tv_source_type);
            tv_index = itemView.findViewById(R.id.tv_index);
        }
    }

    private String getUiTypeDesc(int uiType) {
        switch (uiType) {
            case TypeConfig.UI_THEME_AUTO:
                return "系统默认分配";
            case TypeConfig.UI_THEME_A:
                return "样式UI_THEME_A";
            case TypeConfig.UI_THEME_B:
                return "样式UI_THEME_B";
            case TypeConfig.UI_THEME_C:
                return "样式UI_THEME_C";
            case TypeConfig.UI_THEME_D:
                return "样式UI_THEME_D";
            case TypeConfig.UI_THEME_E:
                return "样式UI_THEME_E";
            case TypeConfig.UI_THEME_F:
                return "样式UI_THEME_F";
            case TypeConfig.UI_THEME_G:
                return "样式UI_THEME_G";
            case TypeConfig.UI_THEME_H:
                return "样式UI_THEME_H";
            case TypeConfig.UI_THEME_I:
                return "样式UI_THEME_I";
            case TypeConfig.UI_THEME_J:
                return "样式UI_THEME_J";
            case TypeConfig.UI_THEME_K:
                return "样式UI_THEME_K";
            case TypeConfig.UI_THEME_L:
                return "样式UI_THEME_L";
        }
        return "";
    }

    private String getSourceType(int sourceType) {
        switch (sourceType) {
            case TypeConfig.DATA_SOURCE_TYPE_JSON:
                return "开发者提供JSON";
            case TypeConfig.DATA_SOURCE_TYPE_MODEL:
                return "开发者提供数据Model";
            case TypeConfig.DATA_SOURCE_TYPE_URL:
                return "开发者配置请求链接";
        }
        return "";
    }
}