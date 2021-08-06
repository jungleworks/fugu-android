package com.skeleton.mvp.data.network;

import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.data.model.CommonResponse;
import com.skeleton.mvp.data.model.allgroups.AllGroupsResponse;
import com.skeleton.mvp.data.model.checkemail.CheckEmailResponse;
import com.skeleton.mvp.data.model.creategroup.CreateGroupResponse;
import com.skeleton.mvp.data.model.editprofile.EditProfileResponse;
import com.skeleton.mvp.data.model.fcCommon.FcCommonResponse;
import com.skeleton.mvp.data.model.fcsignup.FcSignupResponse;
import com.skeleton.mvp.data.model.getInfo.GetInfoResponse;
import com.skeleton.mvp.data.model.getPublicInfo.GetPublicInfoResponse;
import com.skeleton.mvp.data.model.getdomains.GetDomainsResponse;
import com.skeleton.mvp.data.model.getnotifications.NotificationsResponse;
import com.skeleton.mvp.data.model.groupTasks.GetTaskDetailsResponse;
import com.skeleton.mvp.data.model.groupTasks.GetTaskResponse;
import com.skeleton.mvp.data.model.invitation.InvitationResponse;
import com.skeleton.mvp.data.model.live.LiveStreamResponse;
import com.skeleton.mvp.data.model.object.ObjectResponse;
import com.skeleton.mvp.data.model.onetoone.CreateChatResponse;
import com.skeleton.mvp.data.model.openandinvited.OpenAndInvited;
import com.skeleton.mvp.data.model.payment.CalculatePriceResponse;
import com.skeleton.mvp.data.model.payment.InitiatePaymentResponse;
import com.skeleton.mvp.data.model.scheduleMeets.GetMeetingsResponse;
import com.skeleton.mvp.data.model.searchgroupuser.SearchUserResponse;
import com.skeleton.mvp.data.model.setPassword.CommonResponseFugu;
import com.skeleton.mvp.data.model.setUserPassword.SetUserPasswordResponse;
import com.skeleton.mvp.model.alreadyInvited.AlreadyInvitedResponse;
import com.skeleton.mvp.model.editInfo.EditInfoResponse;
import com.skeleton.mvp.model.getAllMembers.AllMemberResponse;
import com.skeleton.mvp.model.googleSignin.GoogleSigninResponse;
import com.skeleton.mvp.model.gplusverification.GPlusVerification;
import com.skeleton.mvp.model.inviteContacts.GetUserContactsResponse;
import com.skeleton.mvp.model.pushNotification.PushNotifications;
import com.skeleton.mvp.model.searchPendingAndAccepted.SearchPendingAndAccepted;
import com.skeleton.mvp.model.searchedMessages.SearchMessagesResponse;
import com.skeleton.mvp.model.seenBy.SeenBy;
import com.skeleton.mvp.model.starredmessage.StarredMessagelResponse;
import com.skeleton.mvp.model.turncredsresponse.TurnCredsResponse;
import com.skeleton.mvp.model.userSearch.UserSearch;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

import static com.skeleton.mvp.constant.FuguAppConstant.APP_SECRET_KEY;
import static com.skeleton.mvp.ui.AppConstants.APP_VERSION;
import static com.skeleton.mvp.ui.AppConstants.DEVICE_TYPE;
import static com.skeleton.mvp.ui.AppConstants.EMAIL;
import static com.skeleton.mvp.ui.AppConstants.WORKSPACE;

/**
 * Developer: Click Labs
 * <p>
 * The API interface for your application
 */
public interface ApiInterface {

    // todo add these params to your API key constant file and remove them from here
//    String OFFICE_CHAT_PREFIX = "https://beta-api.fuguchat.com:3014/api/";
//    String FUGU_PREFIX = "https://beta-api.fuguchat.com:3000/api/";
    /**
     * The constant AUTHORIZATION.
     */
    String AUTHORIZATION = "access_token";
    /**
     * The constant CONTENT_LANG.
     */
    String CONTENT_LANG = "content-language";
    /**
     * The constant LIMIT.
     */
    String LIMIT = "limit";
    /**
     * The constant SKIP.
     */
    String SKIP = "skip";

    //todo Declare your API endpoints here

    /**
     * The constant GET_NOTIFICATIONS.
     */
    String GET_NOTIFICATIONS = "/driver/getNotifications";
    /**
     * The constant SIGNUP_BUSINESS.
     */
//    String SIGNUP_BUSINESS = "business/signup";
    String SIGNUP_WORKSPACE = "workspace/v1/signup";
    /**
     * The constant VERIFY_OTP.
     */
//    String VERIFY_OTP = "business/verifyOtp";
    String VERIFY_OTP = "workspace/v1/verifyOtp";

    String VALIDATE_OTP = "validate_login_otp";

    /**
     * The constant VERIFY_EditPhone OTP.
     */

    String VERIFY_EDITPHONE_OTP = "/api/user/changeContactNumber";


    /**
     * The constant SET_PASSWORD.
     */
//    String SET_PASSWORD = "business/setPassword";
    String SET_PASSWORD = "workspace/v1/setPassword";

    /**
     * The constant SET_USER_PASSWORD.
     */
    String SET_USER_PASSWORD = "user/setPassword";

    /**
     * The constant SET_NEW_USER_DETAILS.
     */
    String SET_NEW_USER_DETAILS = "user/updateUserAndWorkspaceDetails";

    /**
     * The constant GET_DOMAINS.
     */
    String GET_DOMAINS = "business/getDomains";
    /**
     * The constant USER_LOGIN.
     */
    String USER_LOGIN = "user/v1/userLogin";
    /**
     * The constant CHECK_EMAIL.
     */
    String CHECK_EMAIL = "workspace/checkEmail";
    String UPDATE_PROFILE = "user/editUserInfo";
    String GET_PROFILE = "user/getUserInfo";
    /**
     * The constant INVITE_USERS.
     */
    String INVITE_USERS = "user/inviteUser";
    String CALCULATE_PRICE = "calculateInviteTotalPrice";
    String INITIATE_PAYMENT = "payment/initiatePayment";
    String LOGOUT_USER = "user/userLogout";
    String EXIT_SPACE = "/api/workspace/leave";
    String EDIT_WORKSPACE_INFO = "/api/workspace/editInfo";
    String GROUP_CHAT_SEARCH = "chat/groupChatSearch";
    String CREATE_GROUP_CHAT = "chat/createGroupChat";
    String CREATE_ONE_TO_ONE_CHAT = "chat/createOneToOneChat";
    String ACCESS_TOKEN_LOGIN = "user/v1/loginViaAccessToken";
    String RESET_PASSWORD = "/api/user/resetPasswordRequest";
    String CREATE_WORKSPACE = "workspace/createWorkspace";
    String SWITCH_WORKSPACE = "workspace/switchWorkspace";
    String JOIN_WORKSPACE = "workspace/join";
    String OPEN_AND_INVITED = "workspace/getOpenAndInvited";
    String SEND_FEEDBACK = "user/sendFeedback";
    String GET_USER_CONTACTS = "user/getUserContacts";
    String REGISTER_NUMBER = "user/registerPhoneNumber";
    String GET_PUBLIC_INFO = "workspace/getPublicInfo";
    String GET_PUBLIC_INVITE = "workspace/publicInvite";
    String GOOGLE_SIGN_IN = "workspace/googleSignup";
    String USER_LOGIN_V2 = "user/v2/userLogin";
    String USER_LOGIN_NEW = "get_login_otp";
    String CHECK_IF_GOOGLE_USER_ALREADY_REGISTERED = "user/verifyAndRegisterGoogleUser";
    String SYNC_GOOGLE_CONTACTS = "workspace/syncGoogleContacts";


    @FormUrlEncoded
    @POST(USER_LOGIN_V2)
    Call<FcCommonResponse> userLoginV2(@Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceTYpe, @FieldMap Map<String, String> signinUserMap);

    @FormUrlEncoded
    @POST(USER_LOGIN_NEW)
    Call<FcCommonResponse> userLogin(@Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceTYpe, @FieldMap Map<String, String> signinUserMap);

    @FormUrlEncoded
    @POST(GOOGLE_SIGN_IN)
    Call<GoogleSigninResponse> googleSignIn(@Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceTYpe, @FieldMap Map<String, String> googleSignInMap);


    @FormUrlEncoded
    @POST(CHECK_IF_GOOGLE_USER_ALREADY_REGISTERED)
    Call<GPlusVerification> checkIfGoogleUserAlreadyRegistered(@Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceTYpe, @FieldMap Map<String, String> googleSignInMap);

    /**
     * Dummy sign in endpoint
     *
     * @param map the map of params to go along with reqquest
     * @return parsed common response object
     */
    @FormUrlEncoded
    @POST("/signIn")
    Call<CommonResponse> signIn(@FieldMap Map<String, String> map);

    /**
     * * Fetch notifications data from server
     *
     * @param authorization auth
     * @param contentlang   en
     * @param limit         limit
     * @param skip          skip
     * @return parsed notifications response object
     */
    @GET(GET_NOTIFICATIONS)
    Call<NotificationsResponse> getNotifications(@Header(AUTHORIZATION) String authorization,
                                                 @Header(CONTENT_LANG) String contentlang,
                                                 @Query(LIMIT) int limit,
                                                 @Query(SKIP) int skip);

    /**
     * Sign up business call.
     *
     * @param appVersion the app version
     * @param deviceTYpe the device t ype
     * @param signUpMap  the sign up map
     * @return the call
     */
    @FormUrlEncoded
    @POST(SIGNUP_WORKSPACE)
    Call<FcSignupResponse> signUp(@Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceTYpe, @FieldMap Map<String, String> signUpMap);

    /**
     * Verify otp call.
     *
     * @param appVersion   the app version
     * @param deviceTYpe   the device t ype
     * @param verifyOtpMap the verify otp map
     * @return the call
     */
    @FormUrlEncoded
    @POST(VERIFY_OTP)
    Call<FcCommonResponse> verifyOtp(@Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceTYpe, @FieldMap Map<String, String> verifyOtpMap);

    /**
     * Validate otp call.
     *
     * @param appVersion   the app version
     * @param deviceTYpe   the device t ype
     * @param validateOtpMap the verify otp map
     * @return the call
     */
    @FormUrlEncoded
    @POST(VALIDATE_OTP)
    Call<FcCommonResponse> validateOtp(@Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceTYpe, @FieldMap Map<String, String> validateOtpMap);

    @FormUrlEncoded
    @POST(VERIFY_OTP)
    Call<CommonResponse> verifyOtpGPlus(@Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceTYpe, @FieldMap Map<String, String> verifyOtpMap);

    @FormUrlEncoded
    @POST(REGISTER_NUMBER)
    Call<CommonResponse> registerPhoneNumber(@Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceTYpe, @FieldMap Map<String, String> verifyOtpMap);

    /**
     * Verify otp call.
     *
     * @param appVersion            the app version
     * @param deviceTYpe            the device t ype
     * @param verifyEditPhoneOtpMap the verify otp map
     * @return the call
     */
    @FormUrlEncoded
    @POST(VERIFY_EDITPHONE_OTP)
    Call<CommonResponse> verifyEdit_Phone_Otp(@Header(AUTHORIZATION) String accessToken, @Header(DEVICE_TYPE) String deviceTYpe, @Header(APP_VERSION) int appVersion, @FieldMap Map<String, String> verifyEditPhoneOtpMap);


    /**
     * Sets password.
     *
     * @param appVersion     the app version
     * @param deviceTYpe     the device t ype
     * @param setPasswordMap the set password map
     * @return the password
     */
    @FormUrlEncoded
    @POST(SET_PASSWORD)
    Call<FcCommonResponse> setPassword(@Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceTYpe, @FieldMap Map<String, String> setPasswordMap);


    /**
     * Sets password.
     *
     * @param appVersion     the app version
     * @param deviceTYpe     the device t ype
     * @param setUserPasswordMap the set password map
     * @return the password
     */
    @FormUrlEncoded
    @POST(SET_USER_PASSWORD)
    Call<SetUserPasswordResponse> setUserPassword(@Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceTYpe, @FieldMap Map<String, String> setUserPasswordMap);

    /**
     * Sets new user details.
     *
     * @param appVersion     the app version
     * @param deviceType     the device type
     * @param accessToken     the access token
     * @param setUserDetailsMap the set password map
     * @return the password
     */
    @FormUrlEncoded
    @POST(SET_NEW_USER_DETAILS)
    Call<ObjectResponse> setNewUserDetails(@Header(AUTHORIZATION) String accessToken, @Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceType, @FieldMap Map<String, String> setUserDetailsMap);

    /**
     * Sets new user details.
     *
     * @param appVersion         the app version
     * @param deviceType         the device type
     * @param getPublicInviteMap the set password map
     */
    @FormUrlEncoded
    @POST(GET_PUBLIC_INVITE)
    Call<CommonResponse> getPublicInvite(@Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceType, @FieldMap Map<String, String> getPublicInviteMap);

    /**
     * Gets workspace public info.
     *
     * @param appVersion the app version
     * @param deviceType the device type
     * @param workspace  the workspace to get info about
     */
    @GET(GET_PUBLIC_INFO)
    Call<GetPublicInfoResponse> getPublicInfo(@Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceType, @Query(WORKSPACE) String workspace);

    /**
     * Gets workspace public info.
     *
     * @param accessToken the access token
     * @param appVersion  the app version
     * @param deviceType  the device type
     * @param workspace   the workspace to get info about
     * @param userId      the user_unique_key of the user
     * @return the domains
     */
    @GET(GET_PUBLIC_INFO)
    Call<GetPublicInfoResponse> getPublicInfo(@Header(AUTHORIZATION) String accessToken, @Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceType, @Query(WORKSPACE) String workspace, @Query("user_unique_key") String userId);

    /**
     * Gets domains.
     *
     * @param appVersion the app version
     * @param deviceTYpe the device t ype
     * @param email      the email
     * @return the domains
     */
    @GET(GET_DOMAINS)
    Call<GetDomainsResponse> getDomains(@Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceTYpe, @Query(EMAIL) String email);

    /**
     * Sign in user call.
     *
     * @param appVersion    the app version
     * @param deviceTYpe    the device t ype
     * @param signinUserMap the signin user map
     * @return the call
     */
    @FormUrlEncoded
    @POST(USER_LOGIN)
    Call<FcCommonResponse> signInUser(@Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceTYpe, @FieldMap Map<String, String> signinUserMap);

    /**
     * Check email call.
     *
     * @param appVersion the app version
     * @param deviceTYpe the device t ype
     * @param email      the email
     * @return the call
     */
    @GET(CHECK_EMAIL)
    Call<CheckEmailResponse> checkEmail(@Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceTYpe, @Query(EMAIL) String email);

    @Multipart
    @POST(UPDATE_PROFILE)
    Call<EditProfileResponse> editProfile(@Header(AUTHORIZATION) String authorization, @Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceTYpe, @PartMap Map<String, RequestBody> editUserMap);


    @FormUrlEncoded
    @POST("workspace/addPublicEmailDomain")
    Call<CommonResponse> addPublicEmailDomains(@Header(AUTHORIZATION) String authorization, @Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceTYpe, @FieldMap Map<String, String> inviteUsersMap);

    /**
     * Invite users call.
     *
     * @param appVersion     the app version
     * @param deviceTYpe     the device t ype
     * @param inviteUsersMap the invite users map
     * @return the call
     */
    @FormUrlEncoded
    @POST(INVITE_USERS)
    Call<InvitationResponse> inviteUsers(@Header(AUTHORIZATION) String authorization, @Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceTYpe, @FieldMap Map<String, String> inviteUsersMap);

    @FormUrlEncoded
    @POST(CALCULATE_PRICE)
    Call<CalculatePriceResponse> calculatePrice(@Header(AUTHORIZATION) String accessToken, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceTYpe, @Header(APP_VERSION) int appVersion, @FieldMap Map<String, String> calculatePriceMap);

    @FormUrlEncoded
    @POST(INITIATE_PAYMENT)
    Call<InitiatePaymentResponse> initiatePayment(@Header(AUTHORIZATION) String accessToken, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceTYpe, @Header(APP_VERSION) int appVersion, @FieldMap Map<String, String> initiatePaymentMap);

    @FormUrlEncoded
    @POST(LOGOUT_USER)
    Call<CommonResponseFugu> logoutUser(@Header(AUTHORIZATION) String authorization, @Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceTYpe, @FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST(EXIT_SPACE)
    Call<CommonResponseFugu> exitSpace(@Header(AUTHORIZATION) String authorization, @Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceTYpe, @FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST(EDIT_WORKSPACE_INFO)
    Call<CommonResponseFugu> editWorkspaceInfo(@Header(AUTHORIZATION) String authorization, @Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceTYpe, @FieldMap Map<String, String> map);

    @GET(GROUP_CHAT_SEARCH)
    Call<SearchUserResponse> groupChatSearch(@Header(AUTHORIZATION) String accessToken, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceTYpe, @Header(APP_VERSION) int appVersion, @QueryMap Map<String, String> searchUserMap);

    @Multipart
    @POST(CREATE_GROUP_CHAT)
    Call<CreateGroupResponse> createGroup(@Header(AUTHORIZATION) String accessToken, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceTYpe, @Header(APP_VERSION) int appVersion, @PartMap Map<String, RequestBody> createGroupMap);

    @FormUrlEncoded
    @POST(CREATE_ONE_TO_ONE_CHAT)
    Call<CreateChatResponse> createOneToOneChat(@Header(AUTHORIZATION) String accessToken, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceTYpe, @Header(APP_VERSION) int appVersion, @FieldMap Map<String, String> createChatMap);

    @FormUrlEncoded
    @POST("users/editInfo")
    Call<EditInfoResponse> editInfo(@Header(AUTHORIZATION) String accessToken, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceTYpe, @Header(APP_VERSION) int appVersion, @FieldMap Map<String, String> editInfo);

    @GET("users/getInfo")
    Call<GetInfoResponse> getInfo(@Header(AUTHORIZATION) String accessToken, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceTYpe, @Header(APP_VERSION) int appVersion, @QueryMap Map<String, String> getInfo);

    @GET("/api/workspace/getInvitedUsers")
    Call<AlreadyInvitedResponse> getInvitedMembersInfo(@Header(AUTHORIZATION) String accessToken, @Header(DEVICE_TYPE) String deviceTYpe, @Header(APP_VERSION) int appVersion, @QueryMap Map<String, String> getInvitedMember);

    @FormUrlEncoded
    @POST("/api/user/changeContactNumberRequest")
    Call<CommonResponseFugu> getOTP(@Header(AUTHORIZATION) String accessToken, @Header(DEVICE_TYPE) String deviceTYpe, @Header(APP_VERSION) int appVersion, @FieldMap Map<String, String> getOTP);

    @FormUrlEncoded
    @POST("users/testPushNotification")
    Call<CommonResponseFugu> testPushNotification(@Header(AUTHORIZATION) String accessToken, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceTYpe, @Header(APP_VERSION) int appVersion, @FieldMap Map<String, Object> getInfo);

    @GET("chat/userSearch")
    Call<UserSearch> userSearch(@Header(AUTHORIZATION) String accessToken, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceTYpe, @Header(APP_VERSION) int appVersion, @QueryMap Map<String, Object> getInfo);

    @GET("chat/pendingAndAcceptedUserSearch")
    Call<SearchPendingAndAccepted> pendingAndAcceptedUserSearch(@Header(AUTHORIZATION) String accessToken, @Header(APP_SECRET_KEY) String appSecretKey, @Header(DEVICE_TYPE) int deviceTYpe, @Header(APP_VERSION) int appVersion, @QueryMap Map<String, Object> getInfo);

    @FormUrlEncoded
    @POST(ACCESS_TOKEN_LOGIN)
    Call<FcCommonResponse> accessTokenLogin(@Header(AUTHORIZATION) String accessToken, @Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceTYpe, @FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST(RESET_PASSWORD)
    Call<CommonResponseFugu> resetPassword(@Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceTYpe, @FieldMap Map<String, String> resetpasswordMap);

    @GET(GET_PROFILE)
    Call<com.skeleton.mvp.data.model.getUserInfo.GetInfoResponse> getUserInfo(@Header(AUTHORIZATION) String accessToken, @Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceTYpe, @QueryMap Map<String, String> params);

    @FormUrlEncoded
    @POST("chat/join")
    Call<CommonResponseFugu> joinGroup(@Header(AUTHORIZATION) String accessToken, @Header(APP_SECRET_KEY) String appSecretKey, @Header(FuguAppConstant.DEVICE_TYPE) int deviceType, @Header(FuguAppConstant.APP_VERSION) int appVersion, @FieldMap Map<String, String> joinGroupMap);

    @GET("chat/getChatGroups")
    Call<AllGroupsResponse> getGroups(@Header(AUTHORIZATION) String accessToken, @Header(APP_SECRET_KEY) String appSecretKey, @Header(FuguAppConstant.DEVICE_TYPE) int deviceType, @Header(FuguAppConstant.APP_VERSION) int appVersion, @QueryMap Map<String, String> allGroupMap);

    @FormUrlEncoded
    @POST("chat/leave")
    Call<CommonResponseFugu> leaveGroup(@Header(AUTHORIZATION) String accessToken, @Header(APP_SECRET_KEY) String appSecretKey, @Header(FuguAppConstant.DEVICE_TYPE) int deviceType, @Header(FuguAppConstant.APP_VERSION) int appVersion, @FieldMap Map<String, String> leaveMap);

    @FormUrlEncoded
    @POST("business/sendDomainsToEmail")
    Call<CommonResponse> sendDomains(@Header(AUTHORIZATION) String accessToken, @Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceTYpe, @FieldMap Map<String, String> resetpasswordMap);

    @FormUrlEncoded
    @POST(CREATE_WORKSPACE)
    Call<ObjectResponse> createWorkspace(@Header(AUTHORIZATION) String accessToken, @Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceTYpe, @FieldMap Map<String, String> createWorkspace);

    @FormUrlEncoded
    @POST(SWITCH_WORKSPACE)
    Call<CommonResponse> switchWorkspace(@Header(AUTHORIZATION) String accessToken, @Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceTYpe, @FieldMap Map<String, String> switchWorkspace);

    @FormUrlEncoded
    @POST(JOIN_WORKSPACE)
    Call<ObjectResponse> joinWorkspace(@Header(AUTHORIZATION) String accessToken, @Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceTYpe, @FieldMap Map<String, String> joinWorkspace);

    @GET(OPEN_AND_INVITED)
    Call<OpenAndInvited> getOpenAndInvited(@Header(AUTHORIZATION) String accessToken, @Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceTYpe);

    @FormUrlEncoded
    @POST(SEND_FEEDBACK)
    Call<CommonResponse> sendFeedback(@Header(AUTHORIZATION) String accessToken, @Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceTYpe, @FieldMap Map<String, String> feedbackMap);

    @GET(GET_USER_CONTACTS)
    Call<GetUserContactsResponse> getUserContacts(@Header(AUTHORIZATION) String accessToken, @Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceTYpe, @QueryMap Map<String, String> contactsMap);

    @GET("/api/conversation/searchMessages")
    Call<SearchMessagesResponse> getSearchMessages(@Header(AUTHORIZATION) String accessToken, @Header(APP_SECRET_KEY) String appSecretKey, @Header(FuguAppConstant.DEVICE_TYPE) int deviceType, @Header(FuguAppConstant.APP_VERSION) int appVersion, @QueryMap Map<String, String> searchMessagesMap);

    @GET("/api/workspace/getAllMembers")
    Call<AllMemberResponse> getAllMembers(@Header(AUTHORIZATION) String accessToken, @Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceTYpe, @QueryMap Map<String, String> params);

    @FormUrlEncoded
    @POST("user/resendInvitation")
    Call<CommonResponse> resendInvitation(@Header(AUTHORIZATION) String accessToken, @Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceTYpe, @FieldMap Map<String, String> switchWorkspace);

    @FormUrlEncoded
    @POST("user/revokeInvitation")
    Call<CommonResponse> revokeInvitation(@Header(AUTHORIZATION) String accessToken, @Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceTYpe, @FieldMap Map<String, String> switchWorkspace);

    @FormUrlEncoded
    @POST("user/updateDeviceToken")
    Call<CommonResponse> updateDeviceToken(@Header(AUTHORIZATION) String accessToken, @Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceTYpe, @FieldMap Map<String, String> switchWorkspace);

    @FormUrlEncoded
    @POST("user/checkInvitedContacts")
    Call<CommonResponse> checkInvitedContacts(@Header(AUTHORIZATION) String accessToken, @Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceTYpe, @FieldMap Map<String, String> switchWorkspace);

    @GET("/api/bot/fuguBot")
    Call<com.skeleton.mvp.retrofit.CommonResponse> sendBotMessage(@Header(AUTHORIZATION) String accessToken, @Header(APP_VERSION) int appVersion, @Header(DEVICE_TYPE) String deviceTYpe, @QueryMap Map<String, Object> messageMap);

    @FormUrlEncoded
    @POST("/api/conversation/starMessage")
    Call<CommonResponse> starMessage(@Header(AUTHORIZATION) String accessToken, @Header(APP_SECRET_KEY) String appSecretKey, @Header(FuguAppConstant.DEVICE_TYPE) int deviceType, @Header(FuguAppConstant.APP_VERSION) int appVersion, @FieldMap Map<String, String> starMessageMap);

    @GET("/api/conversation/getStarredMessages")
    Call<StarredMessagelResponse> getStarredMessages(@Header(AUTHORIZATION) String accessToken, @Header(APP_SECRET_KEY) String appSecretKey, @Header(FuguAppConstant.DEVICE_TYPE) int deviceType, @Header(FuguAppConstant.APP_VERSION) int appVersion, @QueryMap Map<String, String> starMessageMap);


    @FormUrlEncoded
    @POST("/api/chat/editMessage")
    Call<com.skeleton.mvp.retrofit.CommonResponse> editMessage(@Header(AUTHORIZATION) String accessToken, @Header(APP_SECRET_KEY) String appSecretKey, @Header(FuguAppConstant.DEVICE_TYPE) int deviceType, @Header(FuguAppConstant.APP_VERSION) int appVersion, @FieldMap Map<String, Object> messageMap);

    @FormUrlEncoded
    @POST("/api/conversation/inviteToConference")
    Call<com.skeleton.mvp.retrofit.CommonResponse> initiateVideoCall(@Header(AUTHORIZATION) String accessToken, @Header(APP_SECRET_KEY) String appSecretKey, @Header(FuguAppConstant.DEVICE_TYPE) int deviceType, @Header(FuguAppConstant.APP_VERSION) int appVersion, @FieldMap Map<String, Object> messageMap);

    @GET("/api/chat/getMessageSeenBy")
    Call<SeenBy> getMessageSeenBy(@Header(AUTHORIZATION) String accessToken, @Header(APP_SECRET_KEY) String appSecretKey, @Header(FuguAppConstant.DEVICE_TYPE) int deviceType, @Header(FuguAppConstant.APP_VERSION) int appVersion, @QueryMap Map<String, Object> editInfo);

    @FormUrlEncoded
    @POST("/api/conversation/verifyTurnCreds")
    Call<TurnCredsResponse> verifyTurnCreds(@Header(AUTHORIZATION) String accessToken, @Header(APP_SECRET_KEY) String appSecretKey, @Header(FuguAppConstant.DEVICE_TYPE) int deviceType, @Header(FuguAppConstant.APP_VERSION) int appVersion, @FieldMap Map<String, Object> editInfo);

    @GET("/api/user/getPushNotifications")
    Call<PushNotifications> getPushNotifications(@Header(AUTHORIZATION) String accessToken, @Header(APP_SECRET_KEY) String appSecretKey, @Header(FuguAppConstant.DEVICE_TYPE) int deviceType, @Header(FuguAppConstant.APP_VERSION) int appVersion, @QueryMap Map<String, String> getPushNotificationsMap);

    @FormUrlEncoded
    @POST("/api/conversation/updateConferenceCall")
    Call<CommonResponse> updateConferenceCall(@Header(AUTHORIZATION) String accessToken, @Header(FuguAppConstant.DEVICE_TYPE) int deviceType, @Header(FuguAppConstant.APP_VERSION) int appVersion, @FieldMap Map<String, String> inviteDetails);

    @FormUrlEncoded
    @POST("/api/stream/joinLiveStream")
    Call<LiveStreamResponse> joinLiveStream(@Header(AUTHORIZATION) String authorization, @Header(APP_SECRET_KEY) String appSecretKey, @Header(FuguAppConstant.DEVICE_TYPE) int deviceType, @Header(FuguAppConstant.APP_VERSION) int appVersion, @FieldMap Map<String, Object> streamMap);

    @FormUrlEncoded
    @POST("/api/task/assignTask")
    Call<com.skeleton.mvp.retrofit.CommonResponse> assignTask(@Header(AUTHORIZATION) String authorization, @Header(FuguAppConstant.DEVICE_TYPE) int deviceType, @Header(FuguAppConstant.APP_VERSION) int appVersion, @FieldMap Map<String, Object> taskInfo);

    @FormUrlEncoded
    @POST("/api/task/editTaskDetails")
    Call<GetTaskDetailsResponse> editTaskDetails(@Header(AUTHORIZATION) String authorization, @Header(FuguAppConstant.DEVICE_TYPE) int deviceType, @Header(FuguAppConstant.APP_VERSION) int appVersion, @FieldMap Map<String, Object> taskInfo);

    @FormUrlEncoded
    @POST("/api/task/submitTask")
    Call<com.skeleton.mvp.retrofit.CommonResponse> submitTask(@Header(AUTHORIZATION) String authorization, @Header(FuguAppConstant.DEVICE_TYPE) int deviceType, @Header(FuguAppConstant.APP_VERSION) int appVersion, @FieldMap Map<String, Object> taskData);

    @GET("/api/task/getTaskDetails")
    Call<GetTaskDetailsResponse> getTaskDetails(@Header(AUTHORIZATION) String authorization, @Header(FuguAppConstant.DEVICE_TYPE) int deviceType, @Header(FuguAppConstant.APP_VERSION) int appVersion, @QueryMap Map<String, Object> paramsMap);

    @GET("/api/task/getAssignedTask")
    Call<GetTaskResponse> getAssignedTasks(@Header(AUTHORIZATION) String authorization, @Header(FuguAppConstant.DEVICE_TYPE) int deviceType, @Header(FuguAppConstant.APP_VERSION) int appVersion, @QueryMap Map<String, Object> paramsMap);

    @FormUrlEncoded
    @POST("/api/meeting/scheduleMeeting")
    Call<com.skeleton.mvp.retrofit.CommonResponse> scheduleMeeting(@Header(AUTHORIZATION) String authorization, @Header(FuguAppConstant.DEVICE_TYPE) int deviceType, @Header(FuguAppConstant.APP_VERSION) int appVersion, @FieldMap Map<String, Object> meetInfo);

    @FormUrlEncoded
    @POST("/api/meeting/editMeeting")
    Call<com.skeleton.mvp.retrofit.CommonResponse> editMeeting(@Header(AUTHORIZATION) String authorization, @Header(FuguAppConstant.DEVICE_TYPE) int deviceType, @Header(FuguAppConstant.APP_VERSION) int appVersion, @FieldMap Map<String, Object> meetInfo);

    @GET("/api/meeting/getMeetings")
    Call<GetMeetingsResponse> getMeetings(@Header(AUTHORIZATION) String authorization, @Header(FuguAppConstant.DEVICE_TYPE) int deviceType, @Header(FuguAppConstant.APP_VERSION) int appVersion, @QueryMap Map<String, Object> params);

}
