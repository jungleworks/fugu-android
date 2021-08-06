package com.skeleton.mvp.ui.fcinvite;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hbb20.CountryCodePicker;
import com.mikepenz.itemanimators.ScaleUpAnimator;
import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.FuguConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.activity.AlreadyInvitedMembersActivity;
import com.skeleton.mvp.activity.MainActivity;
import com.skeleton.mvp.activity.MultipleInviteActivity;
import com.skeleton.mvp.adapter.CustomListAdapter;
import com.skeleton.mvp.adapter.InviteOnBoardGroupsAdapter;
import com.skeleton.mvp.adapter.InviteOnBoardGroupsSelectedAdapter;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.data.model.CommonResponse;
import com.skeleton.mvp.data.model.fcCommon.FcCommonResponse;
import com.skeleton.mvp.data.model.invitation.InvitationResponse;
import com.skeleton.mvp.data.network.ApiError;
import com.skeleton.mvp.data.network.CommonParams;
import com.skeleton.mvp.data.network.ResponseResolver;
import com.skeleton.mvp.data.network.RestClient;
import com.skeleton.mvp.model.ContactsList;
import com.skeleton.mvp.model.GroupContacts;
import com.skeleton.mvp.model.InvitedMembers;
import com.skeleton.mvp.model.inviteContacts.GetUserContactsResponse;
import com.skeleton.mvp.model.inviteContacts.Group;
import com.skeleton.mvp.model.inviteContacts.UserGroups;
import com.skeleton.mvp.model.inviteContacts.WorkspaceContact;
import com.skeleton.mvp.payment.CalculatePaymentActivity;
import com.skeleton.mvp.ui.base.BaseActivity;
import com.skeleton.mvp.ui.intro.IntroActivity;
import com.skeleton.mvp.util.ExtendedEditText;
import com.skeleton.mvp.util.Log;
import com.skeleton.mvp.util.ValidationUtil;
import com.skeleton.mvp.utils.SectionedGridRecyclerViewAdapter;
import com.skeleton.mvp.utils.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;

import static com.skeleton.mvp.constant.FuguAppConstant.SESSION_EXPIRE;
import static com.skeleton.mvp.ui.AppConstants.ANDROID;
import static com.skeleton.mvp.ui.AppConstants.EMAILS;
import static com.skeleton.mvp.ui.AppConstants.EXTRA_ALREADY_MEMBER;
import static com.skeleton.mvp.ui.AppConstants.WORKSPACE_ID;

public class InviteOnboardActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_CONTACTS = 1234;
    private static final int RC_SIGN_IN = 1212;
    private static final String TAG = "InviteMembersActivity";
    private TextView tvSkip;
    private ImageView ivBack;
    private androidx.appcompat.widget.Toolbar toolbar;
    private LayoutInflater layoutInflater;
    private LinearLayout llEmails, llCheckBox;
    private ArrayList<ExtendedEditText> emailEts = new ArrayList<>();
    private ArrayList<CountryCodePicker> countryCodePickers = new ArrayList<>();
    private ArrayList<String> emails = new ArrayList<>();
    private ArrayList<String> numbers = new ArrayList<>();
    private ArrayList<String> countryCodes = new ArrayList<>();
    private AppCompatButton btnSendInvite;
    private FcCommonResponse fcCommonResponse;
    private boolean showCheckBox = true;
    private HashMap<ExtendedEditText, Boolean> addMap = new HashMap<>();
    private String email = "", text = "";
    private CheckBox cbInvite;
    private String domains;
    private ArrayList<InvitedMembers> invitesMembersList = new ArrayList<>();
    private TextView tvSeeInvites;
    private CustomListAdapter adapter;
    Cursor cursor;
    int counter;
    JSONObject phoneContactsObject = new JSONObject();
    JSONArray phoneContactsArray = new JSONArray();
    GetUserContactsResponse getUserContactsResponse;
    private ArrayList<ContactsList> contactsLists = new ArrayList<>();
    private ArrayList<ContactsList> workspaceContactsList = new ArrayList<>();
    private ArrayList<GroupContacts> workspaceGroupsContactsList = new ArrayList<>();
    private ArrayList<GroupContacts> workspaceGroupsSelectedArrayList = new ArrayList<>();
    private ArrayList<ContactsList> allContacts = new ArrayList<>();
    private boolean isPermissionGranted = false;
    private TextView tvInviteMultiple;
    private RecyclerView rvInviteOnBoard;
    private RecyclerView rvSelectedGroups;
    InviteOnBoardGroupsAdapter inviteOnBoardGroupsAdapter;
    InviteOnBoardGroupsSelectedAdapter inviteOnBoardGroupsSelectedAdapter;
    private ArrayList<Long> channelIdArrayList = new ArrayList<>();
    private LinkedHashMap<Long, GroupContacts> groupMap = new LinkedHashMap<>();
    private ArrayList<GroupContacts> groupContactsArrayList = new ArrayList<>();
    private View vRvSeperator;
    private TextView tvOnboardText, tvSendInvitationsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_onboard);
        initViews();
        listeners();
        if (!getIntent().hasExtra(EXTRA_ALREADY_MEMBER)) {
            apiGetUserContacts("ALL");

        } else {
            rvInviteOnBoard.setVisibility(View.GONE);
            tvOnboardText.setVisibility(View.GONE);

            tvSendInvitationsText.setVisibility(View.VISIBLE);
            if (com.skeleton.mvp.fugudatabase.CommonData.getWorkspaceContacts() == null) {
                apiGetUserContacts("ALL");
            } else {
                workspaceContactsList = com.skeleton.mvp.fugudatabase.CommonData.getWorkspaceContacts();
            }
        }
    }

    private void apiGetUserContacts(String contact_type) {
        showLoading();
        CommonParams commonParams = new CommonParams.Builder()
                .add("workspace_id", CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getWorkspaceId())
                .add("contact_type", contact_type)
                .build();
        RestClient.getApiInterface(true).getUserContacts((CommonData.getCommonResponse().getData().getUserInfo().getAccessToken()), BuildConfig.VERSION_CODE, ANDROID, commonParams.getMap())
                .enqueue(new ResponseResolver<GetUserContactsResponse>() {
                    @Override
                    public void onSuccess(final GetUserContactsResponse getUserContactsResponse) {
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
//                                for (int i = 0; i < getUserContactsResponse.getData().getContacts().size(); i++) {
//                                    contactsLists.add(new ContactsList(getUserContactsResponse.getData().getContacts().get(i).getFullName(),
//                                            getUserContactsResponse.getData().getContacts().get(i).getEmail()));
//                                }
                                com.skeleton.mvp.fugudatabase.CommonData.setUserContacts(contactsLists);
                                //ArrayList<WorkspaceContact>finalworkspaceContacts= (ArrayList<WorkspaceContact>) getUserContactsResponse.getData().getFinalWorkspaceContacts();
                                final ArrayList<WorkspaceContact> workspaceContacts = (ArrayList<WorkspaceContact>) getUserContactsResponse.getData().getWorkspaceContacts();
                                final ArrayList<UserGroups> workspaceGroupsContacts = (ArrayList<UserGroups>) getUserContactsResponse.getData().getUserGroups();
                                workspaceContactsList.clear();
                                workspaceGroupsContactsList.clear();
                                for (int i = 0; i < workspaceContacts.size(); i++) {
                                    if (!TextUtils.isEmpty(workspaceContacts.get(i).getEmail()) && !workspaceContacts.get(i).getEmail().contains("@fuguchat.com") && !workspaceContacts.get(i).getEmail().contains("@junglework.auth")) {
                                        if (!workspaceContacts.get(i).getEmail().equals(CommonData.getCommonResponse().getData().getUserInfo().getEmail())) {
                                            workspaceContactsList.add(new ContactsList(workspaceContacts.get(i).getFullName(),
                                                    workspaceContacts.get(i).getEmail(), false));
                                        }
                                    }
                                    if (!TextUtils.isEmpty(workspaceContacts.get(i).getContactNumber())) {
                                        if (!workspaceContacts.get(i).getContactNumber().equals(CommonData.getCommonResponse().getData().getUserInfo().getContactNumber())) {
                                            workspaceContactsList.add(new ContactsList(workspaceContacts.get(i).getFullName(),
                                                    workspaceContacts.get(i).getContactNumber(), false));
                                        }
                                    }

                                }
                                for (int i = 0; i < workspaceGroupsContacts.size(); i++) {

                                    for (int j = 0; j < workspaceGroupsContacts.get(i).getGroups().size(); j++) {
                                        Group group = workspaceGroupsContacts.get(i).getGroups().get(j);
                                        workspaceGroupsContactsList.add(new GroupContacts(
                                                group.getBusinessName(),
                                                group.getCustomLabel(),
                                                false,
                                                group.getChannelId().toString(),
                                                group.getChannelImage().getChannelImageUrl(),
                                                group.getChannelImage().getChannelThumbnailUrl(),
                                                group.getAppSecretKey(),
                                                group.getEmails(),
                                                group.getContactNumbers(),
                                                1));

                                    }
                                }
                                runOnUiThread(() -> {
                                    inviteOnBoardGroupsAdapter = new InviteOnBoardGroupsAdapter(workspaceGroupsContactsList, InviteOnboardActivity.this);
                                    inviteOnBoardGroupsSelectedAdapter = new InviteOnBoardGroupsSelectedAdapter(groupMap, workspaceGroupsSelectedArrayList, InviteOnboardActivity.this);
                                    if (!getIntent().hasExtra(EXTRA_ALREADY_MEMBER)) {
                                        rvSelectedGroups.setItemAnimator(new ScaleUpAnimator());
                                        rvInviteOnBoard.setVisibility(View.VISIBLE);
                                        rvSelectedGroups.setVisibility(View.VISIBLE);
                                    }
                                    GridLayoutManager gridLayoutManager = new GridLayoutManager(InviteOnboardActivity.this, 4);
                                    rvInviteOnBoard.setHasFixedSize(true);
                                    SimpleAdapter mAdapter = new SimpleAdapter(workspaceGroupsContactsList, InviteOnboardActivity.this);
                                    List<SectionedGridRecyclerViewAdapter.Section> sections = new ArrayList<>();
                                    //Sections
                                    int start = 0;
                                    for (int i = 0; i < workspaceGroupsContacts.size(); i++) {
                                        sections.add(new SectionedGridRecyclerViewAdapter.Section(start, workspaceGroupsContacts.get(i).getWorkspaceName()));
                                        start += workspaceGroupsContacts.get(i).getGroups().size();
                                    }
                                    rvInviteOnBoard.setLayoutManager(gridLayoutManager);
                                    SectionedGridRecyclerViewAdapter.Section[] dummy = new SectionedGridRecyclerViewAdapter.Section[sections.size()];
                                    SectionedGridRecyclerViewAdapter mSectionedAdapter = new
                                            SectionedGridRecyclerViewAdapter(InviteOnboardActivity.this, R.layout.section, R.id.section_text, rvInviteOnBoard, inviteOnBoardGroupsAdapter);
                                    mSectionedAdapter.setSections(sections.toArray(dummy));
                                    rvSelectedGroups.setLayoutManager(new LinearLayoutManager(InviteOnboardActivity.this, RecyclerView.HORIZONTAL, false));
                                    rvInviteOnBoard.setAdapter(mSectionedAdapter);
                                    rvSelectedGroups.setAdapter(inviteOnBoardGroupsSelectedAdapter);
                                    if (workspaceGroupsContacts.size() > 0) {
                                        if (!getIntent().hasExtra(EXTRA_ALREADY_MEMBER)) {
                                            rvInviteOnBoard.setVisibility(View.VISIBLE);
                                            tvOnboardText.setVisibility(View.VISIBLE);
                                            tvSendInvitationsText.setVisibility(View.GONE);

                                        }
                                    } else {
                                        rvInviteOnBoard.setVisibility(View.GONE);
                                        tvOnboardText.setVisibility(View.GONE);
                                        tvSendInvitationsText.setVisibility(View.VISIBLE);
                                    }
                                });
                                com.skeleton.mvp.fugudatabase.CommonData.setWorkspaceContacts(workspaceContactsList);
                                allContacts.clear();
                                allContacts.addAll(workspaceContactsList);
                                allContacts.addAll(contactsLists);
                                runOnUiThread(() -> {
                                    adapter = new CustomListAdapter(InviteOnboardActivity.this,
                                            R.layout.simple_dropdown_two_line, allContacts);
                                    emailEts.get(0).setAdapter(adapter);

                                    emailEts.get(1).setAdapter(adapter);
                                });
                            }
                        }.start();
                        hideLoading();
                    }

                    @Override
                    public void onError(ApiError error) {
                        hideLoading();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        hideLoading();
                    }
                });
    }

    //Initialize views for InviteOnboardActivity
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvSkip = findViewById(R.id.tvSkip);
        tvSeeInvites = findViewById(R.id.tvSeeInvites);
        emailEts.add(findViewById(R.id.etOne));
        emailEts.add(findViewById(R.id.etTwo));
        tvInviteMultiple = findViewById(R.id.tvInviteMultiple);
        rvInviteOnBoard = findViewById(R.id.rvInviteOnBoard);
        rvSelectedGroups = findViewById(R.id.rvSelectedGroups);
        vRvSeperator = findViewById(R.id.vRvSeperator);
        tvOnboardText = findViewById(R.id.tvOnboardText);
        tvSendInvitationsText = findViewById(R.id.tvSendInvitationsText);
        tvInviteMultiple.setOnClickListener(v -> startActivity(new Intent(InviteOnboardActivity.this, MultipleInviteActivity.class)));
        new Thread() {
            @Override
            public void run() {
                super.run();

                runOnUiThread(() -> {
                    allContacts.clear();
                    allContacts.addAll(contactsLists);
                    allContacts.addAll(workspaceContactsList);
                    adapter = new CustomListAdapter(InviteOnboardActivity.this,
                            R.layout.simple_dropdown_two_line, allContacts);
                    emailEts.get(0).setAdapter(adapter);

                    emailEts.get(1).setAdapter(adapter);
                    emailEts.get(0).setOnItemClickListener((parent, view, position, id) -> {
                        String[] selectedItem = parent.getItemAtPosition(position).toString().split("-");
                        if (TextUtils.isDigitsOnly(selectedItem[selectedItem.length - 1])) {
                            emailEts.get(0).setText(selectedItem[selectedItem.length - 1]);
                            emailEts.get(0).setSelection(selectedItem[selectedItem.length - 1].length());
                            countryCodePickers.get(0).setCountryForPhoneCode(Integer.parseInt(selectedItem[0]));
                        } else {
                            emailEts.get(0).setText(parent.getItemAtPosition(position).toString());
                            emailEts.get(0).setSelection(parent.getItemAtPosition(position).toString().length());
                        }
                    });
                    emailEts.get(1).setOnItemClickListener((parent, view, position, id) -> {
                        String[] selectedItem = parent.getItemAtPosition(position).toString().split("-");
                        if (TextUtils.isDigitsOnly(selectedItem[selectedItem.length - 1])) {
                            emailEts.get(1).setText(selectedItem[selectedItem.length - 1]);
                            emailEts.get(1).setSelection(selectedItem[selectedItem.length - 1].length());
                            countryCodePickers.get(1).setCountryForPhoneCode(Integer.parseInt(selectedItem[0]));
                        } else {
                            emailEts.get(1).setText(parent.getItemAtPosition(position).toString());
                            emailEts.get(1).setSelection(parent.getItemAtPosition(position).toString().length());
                        }
                    });
                });
            }
        }.start();


        countryCodePickers.add(findViewById(R.id.etCountryCode));
        countryCodePickers.add(findViewById(R.id.etCountryCode2));
        llEmails = findViewById(R.id.llEmails);
        ivBack = findViewById(R.id.ivBack);
        cbInvite = findViewById(R.id.cbInvite);
        llCheckBox = findViewById(R.id.llCheckBox);
        CheckBox cbInvite = findViewById(R.id.cbInvite);
        btnSendInvite = findViewById(R.id.btnSendInvite);
        layoutInflater = getLayoutInflater();
        if (CommonData.getEmail() != null) {
            email = CommonData.getEmail();

        }
        String[] domain = email.split("@");
        if (!TextUtils.isEmpty(email)) {
            try {
                domains = domain[1];
                text = "Let other people sign up with their verified " + domain[1] + " email address";
            } catch (Exception e) {
                llCheckBox.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }
        SpannableStringBuilder str = new SpannableStringBuilder(text);
        try {
            str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), getString(R.string.prefix_email).length(), getString(R.string.prefix_email).length() + domain[1].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cbInvite.setText(str);
        if (getIntent().hasExtra(EXTRA_ALREADY_MEMBER)) {
            ivBack.setVisibility(View.VISIBLE);
            ivBack.setOnClickListener(this);
            tvSkip.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(cbInvite.getText().toString().trim())) {
                llCheckBox.setVisibility(View.VISIBLE);
            } else {
                llCheckBox.setVisibility(View.GONE);
            }
        } else {
            ivBack.setVisibility(View.GONE);
            ivBack.setOnClickListener(null);
            tvSkip.setVisibility(View.VISIBLE);
            if (CommonData.getSignupResponse() != null) {
                for (int i = 0; i < CommonData.getSignupResponse().getData().getDisallowWorkspaceEmail().size(); i++) {
                    try {
                        if (domain[1].equalsIgnoreCase(CommonData.getSignupResponse().getData().getDisallowWorkspaceEmail().get(i))) {
                            showCheckBox = false;
                        }
                    } catch (Exception e) {

                    }
                }
            }
        }
        if (getIntent().hasExtra(EXTRA_ALREADY_MEMBER)) {
            showCheckBox = false;
        }
        if (showCheckBox) {
            if (!TextUtils.isEmpty(email)) {
                if (!TextUtils.isEmpty(cbInvite.getText().toString().trim())) {
                    llCheckBox.setVisibility(View.VISIBLE);
                } else {
                    llCheckBox.setVisibility(View.GONE);
                }
            } else {
                llCheckBox.setVisibility(View.GONE);
            }
        } else {
            llCheckBox.setVisibility(View.GONE);
        }
    }


    public void inflateMenuItem() {
        toolbar.inflateMenu(R.menu.invited_menu);
    }

    //Set clicklisteners and TextWatcher
    private void listeners() {
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.invited) {
                startActivity(new Intent(InviteOnboardActivity.this, AlreadyInvitedMembersActivity.class));
            }
            return false;

        });
        emailEts.get(0).addTextChangedListener(new MyTextWatcher(0));
        emailEts.get(1).addTextChangedListener(new MyTextWatcher(1));
        addMap.put(emailEts.get(0), true);
        addMap.put(emailEts.get(1), false);
        btnSendInvite.setOnClickListener(this);
        tvSkip.setOnClickListener(this);
        tvSeeInvites.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvSkip:
                finishAffinity();
                Intent intent = new Intent(InviteOnboardActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            case R.id.btnSendInvite:
                emails.clear();
                numbers.clear();
                for (int i = 0; i < emailEts.size(); i++) {
                    if (!TextUtils.isEmpty(emailEts.get(i).getText().toString().trim())) {
                        String regexStr = "^[0-9]*$";
                        if (emailEts.get(i).getText().toString().trim().matches(regexStr)) {
                            numbers.add("+" + countryCodePickers.get(i).getSelectedCountryCode().trim() + "-" + emailEts.get(i).getText().toString().trim());
                            countryCodes.add(countryCodePickers.get(i).getSelectedCountryNameCode().trim());
                        } else {
                            emails.add(emailEts.get(i).getText().toString().trim());
                        }
                    }
                }
                emailValidatorAndInvite();
                break;
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tvSeeInvites:
                startActivity(new Intent(InviteOnboardActivity.this, AlreadyInvitedMembersActivity.class));
                break;
            default:
                break;
        }
    }

    //Validate email list and call inviation api
    private void emailValidatorAndInvite() {
        Boolean invite = false;
        String suffix;
        for (int j = 0; j < emails.size(); j++) {
            if (!ValidationUtil.checkEmail(emails.get(j))) {
                invite = false;
                switch (j) {
                    case 0:
                        suffix = "st";
                        break;
                    case 1:
                        suffix = "nd";
                        break;
                    case 2:
                        suffix = "rd";
                        break;
                    default:
                        suffix = "th";
                        break;
                }
                if (emails.size() == 1) {
                    showErrorMessage("Please enter a valid email");
                } else {
                    showErrorMessage("Please enter a valid email at " + (j + 1) + suffix + " position");
                }
                return;
            } else {
                invite = true;
            }
        }
        ArrayList<String> groupEmails = new ArrayList<>();
        ArrayList<GroupContacts> groupContacts = new ArrayList<>(groupMap.values());
        for (int i = 0; i < groupContacts.size(); i++) {
            for (int j = 0; j < groupContacts.get(i).getEmails().size(); j++) {
                if (!groupContacts.get(i).getEmails().get(j).equals(CommonData.getCommonResponse().getData().getUserInfo().getEmail())
                        && (!groupContacts.get(i).getEmails().get(j).contains("@fuguchat")
                        && !groupContacts.get(i).getEmails().get(j).equals(CommonData.getCommonResponse().getData().getUserInfo().getEmail()))) {
                    groupEmails.add(groupContacts.get(i).getEmails().get(j));
                    invite = true;
                } else {
                    if (!TextUtils.isEmpty(groupContacts.get(i).getPhoneNo().get(j))) {
                        try {
                            PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.createInstance(InviteOnboardActivity.this);
                            countryCodes.add(phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(groupContacts.get(i).getPhoneNo().get(j).split("-")[0])));
                            numbers.add(groupContacts.get(i).getPhoneNo().get(j));
                            invite = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        emails.addAll(groupEmails);
        if (emails.size() == 0 && numbers.size() > 0) {
            invite = true;
        } else if (emails.size() == 0 && numbers.size() == 0 && llCheckBox.getVisibility() == View.VISIBLE && cbInvite.isChecked()) {
            addPublicEmailDomains();
            finishAffinity();
            Intent intent = new Intent(InviteOnboardActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (emails.size() == 0 && numbers.size() == 0 && (!cbInvite.isChecked() || llCheckBox.getVisibility() == View.GONE)) {
            showErrorMessage("Please enter a valid email address/phone number");
        }

        if (invite) {
            showLoading();
            if (isNetworkConnected()) {
                apiInviteMember(emails);
                if (cbInvite.isChecked()) {
                    addPublicEmailDomains();
                }
            } else {
                showErrorMessage(R.string.error_internet_not_connected);
            }
        }
    }

    private void addPublicEmailDomains() {
        FcCommonResponse fcCommonResponse = CommonData.getCommonResponse();
        CommonParams.Builder commonParams = new CommonParams.Builder();
        commonParams.add("workspace_id", fcCommonResponse.getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getWorkspaceId());
        commonParams.add("email_domain", domains);
        RestClient.getApiInterface(true).addPublicEmailDomains(fcCommonResponse.getData().getUserInfo().getAccessToken(), BuildConfig.VERSION_CODE, ANDROID, commonParams.build().getMap())
                .enqueue(new ResponseResolver<CommonResponse>() {
                    @Override
                    public void onSuccess(CommonResponse commonResponse) {

                    }

                    @Override
                    public void onError(ApiError error) {

                    }

                    @Override
                    public void onFailure(Throwable throwable) {

                    }
                });
    }

    /**
     * Invite members api and Intent to HomActivity on success
     *
     * @param emails emails Array list
     */
    private void apiInviteMember(ArrayList<String> emails) {
        JSONArray emailJsonArray = new JSONArray(emails);
        JSONArray contactArray = new JSONArray();

        for (int i = 0; i < numbers.size(); i++) {
            JSONObject contactObject = new JSONObject();
            try {
                contactObject.put("contact_number", numbers.get(i));
                contactObject.put("country_code", countryCodes.get(i));
                contactArray.put(contactObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        FcCommonResponse fcCommonResponse = CommonData.getCommonResponse();
        CommonParams.Builder commonParams = new CommonParams.Builder();
        if (emails.size() > 0) {
            commonParams.add(EMAILS, emailJsonArray);
        }
        if (numbers.size() > 0) {
            commonParams.add("contact_numbers", contactArray);
        }

        commonParams.add(WORKSPACE_ID, fcCommonResponse.getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getWorkspaceId());
        Log.e("map", commonParams.build().getMap().toString());
        RestClient.getApiInterface(true).inviteUsers(fcCommonResponse.getData().getUserInfo().getAccessToken(), BuildConfig.VERSION_CODE, ANDROID, commonParams.build().getMap())
                .enqueue(new ResponseResolver<InvitationResponse>() {
                    @Override
                    public void onSuccess(InvitationResponse invitationResponse) {
                        hideLoading();
                        try {
                            for (int i = 0; i < emailEts.size(); i++) {
                                emailEts.get(i).setText("");
                            }
                            showErrorMessage("Invitation sent successfully", () -> {
                                finishAffinity();
                                View view = InviteOnboardActivity.this.getCurrentFocus();
                                if (view != null) {
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                }
                                Intent intent = new Intent(InviteOnboardActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            });
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onError(ApiError error) {
                        hideLoading();
                        if (error.getStatusCode() == SESSION_EXPIRE) {
                            CommonData.clearData();
                            FuguConfig.clearFuguData(InviteOnboardActivity.this);
                            finishAffinity();
                            startActivity(new Intent(InviteOnboardActivity.this, IntroActivity.class));
                        } else if(error.getStatusCode() == 402) {
                            startActivity(new Intent(InviteOnboardActivity.this, CalculatePaymentActivity.class));
                            finish();
                        } else {
                            showErrorMessage(error.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        hideLoading();
                    }
                });
    }

    public void setRecyclerViewOnAddingGroups(GroupContacts groupContacts) {
        channelIdArrayList = new ArrayList<>(groupMap.keySet());
        if (groupMap.size() != 0) {
            if (groupMap.get(Long.valueOf(groupContacts.getChannelId())) != null) {
                groupMap.remove(Long.valueOf(groupContacts.getChannelId()));
                inviteOnBoardGroupsSelectedAdapter.updatelist(groupMap);
                channelIdArrayList.remove(Long.valueOf(groupContacts.getChannelId()));
                if (channelIdArrayList.size() == 0) {
                    rvSelectedGroups.setVisibility(View.GONE);
                    vRvSeperator.setVisibility(View.GONE);
                }
                inviteOnBoardGroupsSelectedAdapter.notifyDataSetChanged();
                try {
                    rvSelectedGroups.smoothScrollToPosition(inviteOnBoardGroupsSelectedAdapter.getItemCount() - 1);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                addGroupToList(groupContacts);
            }
        } else {
            addGroupToList(groupContacts);
        }
    }

    public void addGroupToList(GroupContacts groupContacts) {
        channelIdArrayList.add(Long.valueOf(groupContacts.getChannelId()));
        groupMap.put(Long.valueOf(groupContacts.getChannelId()), groupContacts);
        rvSelectedGroups.setVisibility(View.VISIBLE);
        vRvSeperator.setVisibility(View.VISIBLE);
        inviteOnBoardGroupsSelectedAdapter.updatelist(groupMap);
        inviteOnBoardGroupsSelectedAdapter.notifyItemInserted(groupMap.size() - 1);
        try {
            rvSelectedGroups.smoothScrollToPosition(inviteOnBoardGroupsSelectedAdapter.getItemCount() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void updateSelectedGroupAdapter(ArrayList<GroupContacts> groupContactsArrayList, LinkedHashMap<Long, GroupContacts> groupMap) {
        channelIdArrayList.clear();
        for (Long key : groupMap.keySet()) {
            channelIdArrayList.add(key);
        }
        if (groupContactsArrayList.size() > 0) {
            channelIdArrayList.remove(groupContactsArrayList.get(0).getChannelId());
        }
        inviteOnBoardGroupsAdapter.updateList(channelIdArrayList);
        inviteOnBoardGroupsAdapter.notifyDataSetChanged();

    }


    //MyTextWatcher to add view as soon as user types a character in last added edit text
    private class MyTextWatcher implements TextWatcher {
        private int position;

        private MyTextWatcher(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (emailEts.get(emailEts.size() - 1) != null && emailEts.get(emailEts.size() - 1).isFocused()
                    && (!addMap.containsKey(emailEts.get(emailEts.size() - 1))
                    || (addMap.containsKey(emailEts.get(emailEts.size() - 1)) && !addMap.get(emailEts.get(emailEts.size() - 1))))) {
                View view;
                addMap.put(emailEts.get(emailEts.size() - 1), true);
                view = layoutInflater.inflate(R.layout.item_invite, llEmails, false);
                view.findViewById(R.id.etEmail).clearFocus();
                llEmails.addView(view);
                ExtendedEditText etEmail = llEmails.getChildAt(position + 1).findViewById(R.id.etEmail);
                CountryCodePicker etCountryCode = llEmails.getChildAt(position + 1).findViewById(R.id.etCountryCode3);
                emailEts.add(etEmail);
                countryCodePickers.add(etCountryCode);
                emailEts.get(position + 1).addTextChangedListener(new MyTextWatcher(position + 1));
                emailEts.get(position + 1).setAdapter(adapter);
                emailEts.get(position + 1).setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int positionn, long id) {
                        String[] selectedItem = parent.getItemAtPosition(positionn).toString().split("-");
                        if (TextUtils.isDigitsOnly(selectedItem[selectedItem.length - 1])) {
                            emailEts.get(position + 1).setText(selectedItem[selectedItem.length - 1]);
                            emailEts.get(position + 1).setSelection(selectedItem[selectedItem.length - 1].length());
                            countryCodePickers.get(position + 1).setCountryForPhoneCode(Integer.parseInt(selectedItem[0]));
                        } else {
                            emailEts.get(position + 1).setText(parent.getItemAtPosition(positionn).toString());
                            emailEts.get(position + 1).setSelection(parent.getItemAtPosition(positionn).toString().length());
                        }
                    }
                });
            }
            String regexStr = "^[0-9]*$";
            if (s.length() > 0) {
                if (s.toString().matches(regexStr)) {
                    countryCodePickers.get(position).setVisibility(View.VISIBLE);
                    emailEts.get(position).setPadding(0, 18, 7, 18);
                } else {
                    countryCodePickers.get(position).setVisibility(View.GONE);
                    emailEts.get(position).setPadding(40, 18, 7, 18);
                }
            } else if (s.length() == 0) {
                countryCodePickers.get(position).setVisibility(View.GONE);
                emailEts.get(position).setPadding(40, 18, 7, 18);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        View view = InviteOnboardActivity.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            isPermissionGranted = true;
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    allContacts.clear();
                    allContacts.addAll(workspaceContactsList);
                    runOnUiThread(() -> {
                        adapter = new CustomListAdapter(InviteOnboardActivity.this,
                                R.layout.simple_dropdown_two_line, allContacts);
                        emailEts.get(0).setAdapter(adapter);

                        emailEts.get(1).setAdapter(adapter);

                    });

                }
            }.start();
        }
    }
}
