package com.skeleton.mvp.ui.addBusiness;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.data.model.getdomains.GetDomainsResponse;
import com.skeleton.mvp.data.network.ApiError;
import com.skeleton.mvp.data.network.ResponseResolver;
import com.skeleton.mvp.data.network.RestClient;
import com.skeleton.mvp.ui.domain.DomainActivity;

import java.util.ArrayList;

import static com.skeleton.mvp.ui.AppConstants.ANDROID;

/**
 * Created by Rajat Dhamija
 * 02/01/18.
 */

public class EmailAdapter extends RecyclerView.Adapter<EmailAdapter.MyViewHolder> {

    private ArrayList<String> emailList = new ArrayList<>();
    private Context mContext;
    private AddBusinessActivity addBusinessActivity;

    public EmailAdapter(ArrayList<String> emailList, Context mContext) {
        this.emailList = emailList;
        this.mContext = mContext;
        addBusinessActivity = (AddBusinessActivity) mContext;
    }

    @Override
    public EmailAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_email, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final EmailAdapter.MyViewHolder holder, final int pos) {
        final int position = holder.getAdapterPosition();
        holder.tvEmail.setText(emailList.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBusinessActivity.showLoading();
                apiGetDomains(emailList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return emailList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvEmail;

        MyViewHolder(View itemView) {
            super(itemView);
            tvEmail = itemView.findViewById(R.id.tvEmail);
        }
    }

    /**
     * api get domains
     */
    private void apiGetDomains(final String email) {
        RestClient.getApiInterface(true).getDomains(BuildConfig.VERSION_CODE, ANDROID, email).enqueue(new ResponseResolver<GetDomainsResponse>() {
            @Override
            public void onSuccess(GetDomainsResponse getDomainsResponse) {
                addBusinessActivity.hideLoading();
                CommonData.setGetDomainsResponse(getDomainsResponse);
                CommonData.setEmail(email);
                boolean isAlreadyLoggedIn=false;
                if (getDomainsResponse.getData().size() == 1) {
//                    for (int i = 0; i < CommonData.getFinalSignInResponse().getData().size(); i++) {
////                        if (getDomainsResponse.getData().get(0).getDomain().equals(CommonData.getFinalSignInResponse().getData().get(i).getDomain())) {
////                            isAlreadyLoggedIn = true;
////                        }
//                    }
                    if (isAlreadyLoggedIn){
                        addBusinessActivity.showErrorMessage("You are already logged in with same business account!");
                        isAlreadyLoggedIn=false;
                    }else {
//                        Intent signinIntent = new Intent(addBusinessActivity, FinalSigninActivity.class);
//                        CommonData.setEmail(email);
//                        signinIntent.putExtra(EXTRA_DOMAIN, getDomainsResponse.getData().get(0).getDomain());
//                        addBusinessActivity.startActivity(signinIntent);
                    }

                } else {
                    Intent signinIntent = new Intent(addBusinessActivity, DomainActivity.class);
                    CommonData.setEmail(email);
                    addBusinessActivity.startActivity(signinIntent);
                }
//                addBusinessActivity.startActivity(new Intent(addBusinessActivity, DomainActivity.class));
                addBusinessActivity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }

            @Override
            public void onError(ApiError error) {
                addBusinessActivity.hideLoading();
                addBusinessActivity.showErrorMessage(error.getMessage());
            }

            @Override
            public void onFailure(Throwable throwable) {
                addBusinessActivity.hideLoading();
            }
        });
    }


}
