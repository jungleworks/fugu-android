package com.skeleton.mvp.retrofit;


import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.data.model.addCalendarEvent.AddEventResponse;
import com.skeleton.mvp.data.model.groupTasks.GetTaskResponse;
import com.skeleton.mvp.model.FuguGetConversationsResponse;
import com.skeleton.mvp.model.FuguGetMessageResponse;
import com.skeleton.mvp.model.FuguUploadImageResponse;
import com.skeleton.mvp.model.botConfig.Example;
import com.skeleton.mvp.model.channelResponse.ChannelResponse;
import com.skeleton.mvp.model.editInfo.EditInfoResponse;
import com.skeleton.mvp.model.getChannelInfo.GetChannelInfoResponse;
import com.skeleton.mvp.model.getInfo.GetInfoResponse;
import com.skeleton.mvp.model.getmembers.GetMembersResponse;
import com.skeleton.mvp.model.group.GroupResponse;
import com.skeleton.mvp.model.homeNotifications.HomeNotificationsResponse;
import com.skeleton.mvp.model.innerMessage.ThreadedMessagesResponse;
import com.skeleton.mvp.model.media.MediaResponse;
import com.skeleton.mvp.model.searchgroupuser.SearchUserResponse;
import com.skeleton.mvp.model.threadMessage.LatestThreadedMessagesResponse;
import com.skeleton.mvp.model.unreadNotification.UnreadNotificationResponse;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

import static com.skeleton.mvp.constant.FuguAppConstant.APP_SECRET_KEY;
import static com.skeleton.mvp.constant.FuguAppConstant.APP_VERSION;
import static com.skeleton.mvp.constant.FuguAppConstant.DEVICE_TYPE;
import static com.skeleton.mvp.data.network.ApiInterface.AUTHORIZATION;

/**
 * ApiInterface
 */
public interface ApiInterface {

    @GET("/api/conversation/getMessages")
    Call<FuguGetMessageResponse> getMessages(@Header(AUTHORIZATION) String authorization, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceType, @Header(APP_VERSION) int appVersion, @QueryMap Map<String, Object> map);

    //change same as get messages
    @GET("/api/conversation/getConversations")
    Call<FuguGetConversationsResponse> getConversations(@Header(AUTHORIZATION) String authorization, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceType, @Header(APP_VERSION) int appVersion, @QueryMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("/api/users/userlogout")
    Call<CommonResponse> logOut(@Header(AUTHORIZATION) String authorization, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceType, @Header(APP_VERSION) int appVersion, @FieldMap Map<String, Object> map);

    @Multipart
    @POST("/api/conversation/uploadFile")
    Call<FuguUploadImageResponse> uploadFile(@Header(AUTHORIZATION) String authorization, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceType, @Header(APP_VERSION) int appVersion, @Part MultipartBody.Part file, @PartMap Map<String, RequestBody> map);

    @Multipart
    @POST("/api/attendance/verifyAttendanceCredentials")
    Call<CommonResponse> verifyAttendanceCredentials(@Header(AUTHORIZATION) String authorization, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceType, @Header(APP_VERSION) int appVersion, @PartMap Map<String, RequestBody> editUserMap);

    @Multipart
    @POST("/api/attendance/uploadDefaultImage")
    Call<CommonResponse> uploadDefaultImage(@Header(AUTHORIZATION) String authorization, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceType, @Header(APP_VERSION) int appVersion, @PartMap Map<String, RequestBody> editUserMap);

    @FormUrlEncoded
    @POST("/api/server/logException")
    Call<CommonResponse> sendError(@Header(AUTHORIZATION) String authorization, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceType, @Header(APP_VERSION) int appVersion, @FieldMap Map<String, Object> map);

    @GET("/api/chat/getMembers")
    Call<GetMembersResponse> getMembers(@Header(AUTHORIZATION) String authorization, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceType, @Header(APP_VERSION) int appVersion, @QueryMap Map<String, Object> getMembersMap);

    @FormUrlEncoded
    @POST("/api/chat/addMember")
    Call<GroupResponse> addMember(@Header(AUTHORIZATION) String authorization, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceType, @Header(APP_VERSION) int appVersion, @FieldMap Map<String, Object> addMemberMap);

    @FormUrlEncoded
    @POST("/api/chat/removeMember")
    Call<GroupResponse> removeMember(@Header(AUTHORIZATION) String authorization, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceType, @Header(APP_VERSION) int appVersion, @FieldMap Map<String, Object> removeMemberMap);

    @FormUrlEncoded
    @POST("/api/chat/leave")
    Call<GroupResponse> leaveGroup(@Header(AUTHORIZATION) String authorization, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceType, @Header(APP_VERSION) int appVersion, @FieldMap Map<String, Object> leaveMap);

    @FormUrlEncoded
    @POST("/api/chat/join")
    Call<CommonResponse> joinGroup(@Header(AUTHORIZATION) String authorization, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceType, @Header(APP_VERSION) int appVersion, @FieldMap Map<String, Object> joinGroupMap);

    @GET("/api/chat/groupChatSearch")
    Call<SearchUserResponse> groupChatSearch(@Header(AUTHORIZATION) String authorization, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceType, @Header(APP_VERSION) int appVersion, @QueryMap Map<String, Object> searchUserMap);

    @FormUrlEncoded
    @POST("/api/users/editInfo")
    Call<EditInfoResponse> editInfo(@Header(AUTHORIZATION) String authorization, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceType, @Header(APP_VERSION) int appVersion, @FieldMap Map<String, Object> editInfo);

    @FormUrlEncoded
    @POST("/api/conversation/updateStatus")
    Call<CommonResponse> updateConversationStatus(@Header(AUTHORIZATION) String authorization, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceType, @Header(APP_VERSION) int appVersion, @FieldMap Map<String, Object> editInfo);

    @GET("/api/users/getInfo")
    Call<GetInfoResponse> getInfo(@Header(AUTHORIZATION) String authorization, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceType, @Header(APP_VERSION) int appVersion, @QueryMap Map<String, Object> getInfo);

    @GET("/api/users/getUserChannelsInfo")
    Call<GetChannelInfoResponse> getUserChannelInfo(@Header(AUTHORIZATION) String authorization, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceType, @Header(APP_VERSION) int appVersion, @QueryMap Map<String, Object> getInfo);

    @GET("/api/chat/getChannelInfo")
    Call<ChannelResponse> getChannelInfo(@Header(AUTHORIZATION) String authorization, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceType, @Header(APP_VERSION) int appVersion, @QueryMap Map<String, Object> getInfo);

    @Multipart
    @POST("/api/chat/editInfo")
    Call<EditInfoResponse> editChannelInfo(@Header(AUTHORIZATION) String authorization, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceType, @Header(APP_VERSION) int appVersion, @PartMap Map<String, RequestBody> editInfo);

    @GET("/api/chat/getGroupInfo")
    Call<MediaResponse> getGroupInfo(@Header(AUTHORIZATION) String authorization, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceType, @Header(APP_VERSION) int appVersion, @QueryMap Map<String, Object> editInfo);

    @GET("/api/conversation/getLatestThreadMessage")
    Call<LatestThreadedMessagesResponse> getLatestThreadedMessages(@Header(AUTHORIZATION) String authorization, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceType, @Header(APP_VERSION) int appVersion, @QueryMap Map<String, Object> messageMap);

    @GET("/api/conversation/getThreadMessages")
    Call<ThreadedMessagesResponse> getThreadedMessages(@Header(AUTHORIZATION) String authorization, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceType, @Header(APP_VERSION) int appVersion, @QueryMap Map<String, Object> messageMap);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "/api/chat/clearChatHistory", hasBody = true)
    Call<CommonResponse> clearChatHistory(@Header(AUTHORIZATION) String authorization, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceType, @Header(APP_VERSION) int appVersion, @FieldMap Map<String, Object> clearChatMap);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "/api/chat/deleteMessage", hasBody = true)
    Call<CommonResponse> deleteMessage(@Header(AUTHORIZATION) String authorization, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceType, @Header(APP_VERSION) int appVersion, @FieldMap Map<String, Object> deleteMessage);

    @FormUrlEncoded
    @HTTP(method = "PUT", path = "/api/chat/changeFollowingStatus", hasBody = true)
    Call<CommonResponse> editFollowingStatus(@Header(AUTHORIZATION) String authorization, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceType, @Header(APP_VERSION) int appVersion, @FieldMap Map<String, Object> followingMap);

    @GET("/api/notification/getNotifications")
    Call<HomeNotificationsResponse> getNotifications(@Header(AUTHORIZATION) String authorization, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceType, @Header(APP_VERSION) int appVersion, @QueryMap Map<String, Object> messageMap);

    @FormUrlEncoded
    @POST("/api/notification/markReadAll")
    Call<CommonResponse> markAllNotificationsRead(@Header(AUTHORIZATION) String authorization, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceType, @Header(APP_VERSION) int appVersion, @FieldMap Map<String, Object> messageMap);

    @GET("/api/notification/getUnreadNotifications")
    Call<UnreadNotificationResponse> getUnreadNotifications(@Header(AUTHORIZATION) String authorization, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceType, @Header(APP_VERSION) int appVersion, @QueryMap Map<String, Object> messageMap);

    @FormUrlEncoded
    @POST("/api/conversation/sendMessage")
    Call<CommonResponse> sendMessage(@Header(AUTHORIZATION) String authorization, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceType, @Header(APP_VERSION) int appVersion, @FieldMap Map<String, Object> messageMap);

    @FormUrlEncoded
    @POST("/api/chat/editMessage")
    Call<com.skeleton.mvp.retrofit.CommonResponse> editMessage(@Header(AUTHORIZATION) String authorization, @Header(APP_SECRET_KEY) String appSecretKey, @Header(FuguAppConstant.DEVICE_TYPE) int deviceType, @Header(FuguAppConstant.APP_VERSION) int appVersion, @FieldMap Map<String, Object> messageMap);

    @GET("/api/conversation/getBotConfiguration")
    Call<Example> getBotConfiguration(@Header(AUTHORIZATION) String authorization, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceType, @Header(APP_VERSION) int appVersion, @QueryMap Map<String, Object> messageMap);

    @GET("/api/googleCalendar/getAuthorizeUrl")
    Call<CommonResponse> getCalenderAuthorizeUrl(@Header(AUTHORIZATION) String authorization, @Header(DEVICE_TYPE) int deviceType, @Header(APP_VERSION) int appVersion, @QueryMap Map<String, Object> authorizeInfo);

    @FormUrlEncoded
    @POST("/api/googleCalendar/submitAuthorizeCode")
    Call<CommonResponse> submitAuthorizeCode(@Header(AUTHORIZATION) String authorization, @Header(DEVICE_TYPE) int deviceType, @Header(APP_VERSION) int appVersion, @FieldMap Map<String, Object> authorizeInfo);

    @FormUrlEncoded
    @POST("/api/googleCalendar/addEvent")
    Call<AddEventResponse> addCalendarEvent(@Header(AUTHORIZATION) String authorization, @Header(DEVICE_TYPE) int deviceType, @Header(APP_VERSION) int appVersion, @FieldMap Map<String, Object> eventInfo);

}
