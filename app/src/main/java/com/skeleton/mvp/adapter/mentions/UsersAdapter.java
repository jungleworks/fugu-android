package com.skeleton.mvp.adapter.mentions;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.skeleton.mvp.R;
import com.skeleton.mvp.model.Member;
import com.skeleton.mvp.util.Log;
import com.skeleton.mvp.util.TrimmedTextView;
import com.skeleton.mvp.utils.FuguUtils;


import java.util.Locale;

/**
 * Adapter to the mentions list shown to display the result of an '@' mention.
 */
public class UsersAdapter extends RecyclerArrayAdapter<Member, UsersAdapter.UserViewHolder> {

    /**
     * {@link Context}.
     */
    private final Context context;

    /**
     * Current search string typed by the user.  It is used highlight the query in the
     * search results.  Ex: @bill.
     */
    private String currentQuery;

    /**
     * {@link ForegroundColorSpan}.
     */
    private final ForegroundColorSpan colorSpan;

    public UsersAdapter(final Context context) {
        this.context = context;
        final int colorSpan = ContextCompat.getColor(context, R.color.office_chat_color);
        this.colorSpan = new ForegroundColorSpan(colorSpan);
    }


    /**
     * Setter for what user has queried.
     */
    public void setCurrentQuery(final String currentQuery) {
        //if (currentQuery!=null && !currentQuery.trim().equalsIgnoreCase("")) {
        if (currentQuery != null) {
            this.currentQuery = currentQuery.toLowerCase(Locale.US);
        }
    }

    /**
     * Create UI with views for user name and picture.
     */
    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.listitem_user_mention, parent, false);
        return new UserViewHolder(view);
    }

    /**
     * Display user name and picture.
     */
    @Override
    public void onBindViewHolder(final UserViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        if (position > RecyclerView.INVALID_TYPE) {
            Log.e("OnBind","OnBind");
            final Member member = getItem(position);

            if (member != null && (!member.getName().trim().equalsIgnoreCase(""))) {
//                holder.tvName.setText(member.getName(), TextView.BufferType.SPANNABLE);

                SpannableStringBuilder text = new SpannableStringBuilder();
                text.append(ellipsizeText(member.getName()));

                if(! TextUtils.isEmpty(member.getLeaveType()) && member.getLeaveType().toLowerCase().equals("work_from_home")){
                    ViewGroup.LayoutParams params = holder.tvName.getLayoutParams();
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    holder.tvName.setLayoutParams(params);
                    if(member.getStatus().equalsIgnoreCase("INVITED")){
                        text.append(smallText(" (Invited- WFH)"));
                    } else if(member.getUserType() == 6){
                        text.append(smallText(" (Guest- WFH)"));
                    } else{
                        text.append(smallText(" (on WFH)"));
                    }
                    holder.tvGuest.setVisibility(View.GONE);
                }
                else if(! TextUtils.isEmpty(member.getLeaveType()) && member.getLeaveType().toLowerCase().equals("absent")){
                    ViewGroup.LayoutParams params = holder.tvName.getLayoutParams();
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    holder.tvName.setLayoutParams(params);
                    if(member.getStatus().equalsIgnoreCase("INVITED")){
                        text.append(smallText(" (Invited- leave)"));
                    } else if(member.getUserType() == 6){
                        text.append(smallText(" (Guest- leave)"));
                    } else{
                        text.append(smallText(" (on leave)"));
                    }
                    holder.tvGuest.setVisibility(View.GONE);
                } else{
                    if (member.getStatus().equalsIgnoreCase("INVITED")) {
                        ViewGroup.LayoutParams params = holder.tvName.getLayoutParams();
                        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                        holder.tvName.setLayoutParams(params);
                        holder.tvGuest.setText(" (Pending)");
                        holder.tvGuest.setVisibility(View.VISIBLE);
                    } else if (member.getUserType() == 6) {
                        ViewGroup.LayoutParams params = holder.tvName.getLayoutParams();
                        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                        holder.tvName.setLayoutParams(params);
                        holder.tvGuest.setText(" (Guest)");
                        holder.tvGuest.setVisibility(View.VISIBLE);
                    } else {
                        holder.tvGuest.setVisibility(View.GONE);
                    }
                }
                holder.tvName.setText(text, TextView.BufferType.SPANNABLE);
                holder.tvName.requestLayout();
                holder.tvName.invalidate();
                highlightSearchQueryInUserName(holder.tvName.getText());

                holder.tvContactIcon.setText(FuguUtils.Companion.getFirstCharInUpperCase(member.getName()));
                holder.tvContactIcon.setVisibility(View.VISIBLE);



                if (!TextUtils.isEmpty(member.getImage())) {

                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.fugu_ic_channel_icon)
                            .error(R.drawable.fugu_ic_channel_icon)
                            .fitCenter()
                            .priority(Priority.HIGH)
                            .transforms(new CenterCrop(), new RoundedCorners(10));

                    Glide.with(context)
                            .asBitmap()
                            .apply(options)
                            .load(member.getImage())
                            .into(holder.ivContactIcon);

//                Glide.with(context).load(member.getImage()).asBitmap()
//                        .centerCrop()
//                        .placeholder(ContextCompat.getDrawable(context, R.drawable.fugu_ic_channel_icon))
//                        .error(ContextCompat.getDrawable(context, R.drawable.fugu_ic_channel_icon))
//                        .into(new BitmapImageViewTarget(holder.ivContactIcon) {
//                            @Override
//                            protected void setResource(Bitmap resource) {
//                                RoundedBitmapDrawable circularBitmapDrawable =
//                                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
//                                circularBitmapDrawable.setCircular(true);
//                                holder.ivContactIcon.setImageDrawable(circularBitmapDrawable);
//                            }
//                        });
                    holder.tvContactIcon.setVisibility(View.GONE);
                } else {
                    if (member.getUserId() % 5 == 1) {
                        holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ring_greyy));
                    } else if (member.getUserId() % 5 == 2) {
                        holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ring_teal));
                    } else if (member.getUserId() % 5 == 3) {
                        holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ring_red));
                    } else if (member.getUserId() % 5 == 4) {
                        holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ring_indigo));
                    } else {
                        holder.ivContactIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ring_red));
                    }
                }

                if (getItemCount() - 1 == position) {
                    holder.view.setVisibility(View.GONE);
                } else {
                    holder.view.setVisibility(View.VISIBLE);
                }

            }
        }
    }

    /**
     * Highlights the current search text in the mentions list.
     */
    private void highlightSearchQueryInUserName(CharSequence userName) {
        if (currentQuery != null && !currentQuery.trim().equalsIgnoreCase("")) {
            int searchQueryLocation = userName.toString().toLowerCase(Locale.US).indexOf(currentQuery);

            if (searchQueryLocation != -1) {
                Spannable userNameSpannable = (Spannable) userName;
                userNameSpannable.setSpan(
                        colorSpan,
                        searchQueryLocation,
                        searchQueryLocation + currentQuery.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    private CharSequence ellipsizeText(String text){
        SpannableString s = new SpannableString(text);
        s.setSpan(TrimmedTextView.EllipsizeRange.ELLIPSIS_AT_END, 0, s.length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return s;
    }

    private CharSequence smallText(String text){
        SpannableString s = new SpannableString(text);
        s.setSpan(new RelativeSizeSpan(0.7f), 0,text.length(), 0); // set size
        return s;
    }

    /**
     * View holder for user.
     */
    static class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView  tvContactIcon, tvGuest;
        private TextView tvName;
        private ImageView ivContactIcon;
        private View view;

        UserViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvGuest = itemView.findViewById(R.id.tvGuest);
            tvContactIcon = itemView.findViewById(R.id.tvContactIcon);
            ivContactIcon = itemView.findViewById(R.id.ivContactImage);
            view = itemView.findViewById(R.id.vLine);
        }
    }
}
