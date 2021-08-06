package com.skeleton.mvp.service;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.service.chooser.ChooserTarget;
import android.service.chooser.ChooserTargetService;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.skeleton.mvp.R;
import com.skeleton.mvp.activity.MainActivity;
import com.skeleton.mvp.data.db.ChatDatabase;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.data.model.creategroup.MembersInfo;
import com.skeleton.mvp.model.FuguConversation;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


@SuppressLint("NewApi")
public class ExternalShareChooserService extends ChooserTargetService {

    private String targetName;
    private Icon targetIcon;
    private Bitmap targetImageBitmap;
    private int targetCount =0;
    @Override
    public List<ChooserTarget> onGetChooserTargets(ComponentName targetActivityName,
                                                   IntentFilter matchedFilter) {
        ComponentName componentName = new ComponentName(getPackageName(),
                MainActivity.class.getCanonicalName());

        LinkedHashMap<Long, FuguConversation>  conversationMap = ChatDatabase.INSTANCE.getConversationMap(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFuguSecretKey());
        ArrayList<FuguConversation> conversationList = new ArrayList<>(conversationMap.values());
        // The list of Direct Share items. The system will show the items the way they are sorted
        // in this list.
        ArrayList<ChooserTarget> targets = new ArrayList<>();
        for (int i = 0; i < conversationList.size(); ++i) {
            //DummyContact contact = DummyContact.byId(i);
            Bundle extras = new Bundle();
            Gson gson = new Gson();
            String membersInfoAsString = gson.toJson(conversationList.get(i).getMembersInfo());
            extras.putString("targetLabel",conversationList.get(i).getLabel());
            extras.putLong("targetChannelId",conversationList.get(i).getChannelId());
            if(conversationList.get(i).getThumbnailUrl().compareTo("") == 0){
                extras.putString("targetThumbnailUrl",conversationList.get(i).getThumbnailUrl());
                try{
                    targetImageBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.profile_placeholder);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            else{
                extras.putString("targetThumbnailUrl",conversationList.get(i).getThumbnailUrl());
                try {
                    URL url = new URL(conversationList.get(i).getThumbnailUrl());
                    targetImageBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch(IOException e) {
                    targetImageBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.profile_placeholder);
                }
            }

            extras.putInt("targetChatType",conversationList.get(i).getChat_type());
            extras.putLong("targetLastSentById",conversationList.get(i).getLast_sent_by_id());
            extras.putInt("targetUnreadCount",conversationList.get(i).getUnreadCount());
            extras.putString("targetNotifications",conversationList.get(i).getNotifications());
            extras.putString("targetMembersInfo",membersInfoAsString);
            extras.putInt("targetOtherUserType",conversationList.get(i).getOtherUserType());
            extras.putLong("targetMyUserId",Long.valueOf(CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getUserId()));
            //extras.putInt(DummyContact.ID, i);

            if(targetCount >= 4){
                targetCount = 0;
                break;
            }

            ArrayList<MembersInfo> membersInfos = conversationList.get(i).getMembersInfo();
            if (!TextUtils.isEmpty(conversationList.get(i).getCustomLabel())) {
                String name = "";
                for (int j = 0; j < membersInfos.size(); j++) {
                    if (i > 0) {
                        name = name + ", ";
                    }
                    name = name.concat(membersInfos.get(j).getFullName());
                }
                if(CommonData.getCommonResponse().getData().getWorkspacesInfo().size() <= 1){
                    targetName = name;
                }
                else{
                    targetName = name + " (" +
                            CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getWorkspaceName() + ")";
                }
            } else {
                if(CommonData.getCommonResponse().getData().getWorkspacesInfo().size() <= 1){
                    targetName = conversationList.get(i).getLabel();
                } else{
                    targetName = conversationList.get(i).getLabel() + "(" +
                            CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getWorkspaceName() + ")";
                }

            }


            targetIcon = Icon.createWithBitmap(getCroppedBitmap(targetImageBitmap));

            if(conversationList.get(i).getChat_type() != 7) {
                targetCount++;
                targets.add(new ChooserTarget(
                        // The name of this target.
                        targetName,
                        // The icon to represent this target.
                        targetIcon,
                        // The ranking score for this target (0.0-1.0); the system will omit items with
                        // low scores when there are too many Direct Share items.
                        1f,
                        // The name of the component to be launched if this target is chosen.
                        componentName,
                        // The extra values here will be merged into the Intent when this target is
                        // chosen.
                        extras));
            }
        }
        return targets;
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public String getURLForResource (int resourceId) {
        return Uri.parse("android.resource://"+R.class.getPackage().getName()+"/" +resourceId).toString();
    }

}
