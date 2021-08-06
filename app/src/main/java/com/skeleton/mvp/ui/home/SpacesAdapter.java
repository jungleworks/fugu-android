package com.skeleton.mvp.ui.home;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.skeleton.mvp.R;
import com.skeleton.mvp.activity.MainActivity;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.data.model.Space;
import com.skeleton.mvp.data.model.fcCommon.FcCommonResponse;
import com.skeleton.mvp.fragment.HomeFragment;
import com.skeleton.mvp.utils.FuguUtils;

import java.util.ArrayList;

import static com.skeleton.mvp.constant.FuguAppConstant.FONT_BOLD;
import static com.skeleton.mvp.constant.FuguAppConstant.FONT_REGULAR;

/**
 * Created by Rajat Dhamija
 * 02/01/18.
 */

public class SpacesAdapter extends RecyclerView.Adapter<SpacesAdapter.MyViewHolder> {

    private ArrayList<Space> spaceList = new ArrayList<>();
    private Context mContext;
    private MainActivity mainActivity = null;
    private Typeface boldFont,normalFont;

    public SpacesAdapter(ArrayList<Space> spaceList, Context mContext) {
        this.spaceList = spaceList;
        this.mContext = mContext;
        mainActivity = (MainActivity) mContext;
    }

    public void updateList(ArrayList<Space> spaceList) {
        this.spaceList = spaceList;
        boldFont = Typeface.createFromAsset(mContext.getAssets(), FONT_BOLD);
        normalFont = Typeface.createFromAsset(mContext.getAssets(), FONT_REGULAR);
    }

    @Override
    public SpacesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_business, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SpacesAdapter.MyViewHolder holder, final int pos) {
        final int position = holder.getAdapterPosition();
        final Space space = spaceList.get(position);
        holder.tvName.setText(space.getName());
        holder.ivSelected.setVisibility(View.VISIBLE);
        holder.tvSubtitle.setVisibility(View.GONE);
        holder.tvContactIcon.setText(FuguUtils.Companion.getFirstCharInUpperCase(space.getName()));
        holder.tvContactIcon.setTextSize(24);
        if (space.isCurrentBusiness()) {
            holder.tvName.setTypeface(boldFont, Typeface.BOLD);
            holder.ivSelected.setVisibility(View.VISIBLE);
        } else {
            holder.tvName.setTypeface(normalFont, Typeface.NORMAL);
            holder.ivSelected.setVisibility(View.GONE);
        }
        if (space.isHasAnyUnreadCount()) {
            holder.ivNotification.setVisibility(View.VISIBLE);
        } else {
            holder.ivNotification.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(v -> {
            if (mainActivity.isNetworkConnected()) {
                for (int i = 0; i < spaceList.size(); i++) {
                    Space space1 = spaceList.get(i);
                    if (i != position) {
                        space1.setCurrentBusiness(false);
                    } else {
                        space1.setCurrentBusiness(true);
                        mainActivity.changeBusiness(position, true, false, null);
                        FcCommonResponse fcCommonResponse = CommonData.getCommonResponse();
                        CommonData.setCommonResponse(fcCommonResponse);
                        for (int j = 0; j < fcCommonResponse.getData().getWorkspacesInfo().size(); j++) {
                            com.skeleton.mvp.fugudatabase.CommonData.setFullName(fcCommonResponse.getData().getWorkspacesInfo().get(j).getFuguSecretKey(),
                                    fcCommonResponse.getData().getWorkspacesInfo().get(j).getFullName());
                        }
                    }
                }
                notifyDataSetChanged();
            } else {
                mainActivity.showErrorMessage("Please check your Internet Connection!");
            }
        });
    }

    @Override
    public int getItemCount() {
        return spaceList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvContactIcon, tvName, tvSubtitle;
        private ImageView ivSelected, ivNotification;

        MyViewHolder(View itemView) {
            super(itemView);
            tvContactIcon = itemView.findViewById(R.id.tvContactIcon);
            tvName = itemView.findViewById(R.id.tvName);
            ivSelected = itemView.findViewById(R.id.ivSelected);
            ivNotification = itemView.findViewById(R.id.ivNotification);
            tvSubtitle = itemView.findViewById(R.id.tvSubtitle);
        }
    }

    public interface SelectBusiness {
        void selectBusiness(int position);

        void changeBusiness(int position, boolean isAnimation, boolean refreshCurrentPosition, HomeFragment.ChangeBusiness changeBusiness);
    }
}
