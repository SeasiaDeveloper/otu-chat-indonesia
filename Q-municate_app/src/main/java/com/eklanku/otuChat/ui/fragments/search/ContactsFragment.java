package com.eklanku.otuChat.ui.fragments.search;


import android.Manifest;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.eklanku.otuChat.ui.activities.main.MainActivity;
import com.eklanku.otuChat.ui.adapters.search.ContactsAdapter;
import com.eklanku.otuChat.ui.fragments.base.BaseFragment;
import com.eklanku.otuChat.R;
import com.connectycube.core.Consts;
import com.connectycube.core.EntityCallback;
import com.connectycube.core.exception.ResponseException;
import com.connectycube.core.request.PagedRequestBuilder;
import com.connectycube.users.ConnectycubeUsers;
import com.connectycube.users.model.ConnectycubeUser;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends BaseFragment implements MenuItemCompat.OnActionExpandListener {

    @Bind(R.id.rvContacts)
    RecyclerView mRvContacts;

    private static final int PER_PAGE = 200;
    private static final String[] NUMBER_PREFIX = {"0", "62"};
    private static final int COUNTRY_CODE = 62; // 91; 62;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final String TAG = ContactsFragment.class.getSimpleName();

    private static boolean mIsNewMessage = false;

    public static ContactsFragment newInstance() {
        mIsNewMessage = false;
        return new ContactsFragment();
    }

    public static ContactsFragment newInstance(boolean isNewMessage) {
        mIsNewMessage = isNewMessage;
        return new ContactsFragment();
    }

    private ArrayList<String> arrayPhone;
    private ArrayList<String> arrayName;
    int start_Limit = 0;
    ProgressDialog dialog;
    boolean isload = true;
    ContactsAdapter contactsAdapter;
    ArrayList<ContactsModel> contactsModels;

    private LinearLayoutManager linearLayoutManager;

    private boolean loading = false;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private int intCurrentPage = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        activateButterKnife(view);

        arrayPhone = new ArrayList<>();
        arrayName = new ArrayList<>();
        contactsModels = new ArrayList<>();


        readContacts();

       // contactsAdapter = new ContactsAdapter(contactsModels, getActivity());
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRvContacts.setLayoutManager(linearLayoutManager);
        mRvContacts.setItemAnimator(new DefaultItemAnimator());
        mRvContacts.setAdapter(contactsAdapter);

        mRvContacts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            readContacts();
                        }
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                readContacts();
            } else {
                getActivity().getSupportFragmentManager().popBackStack();
                //Toast.makeText(getActivity(), "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void initActionBar() {
        super.initActionBar();
        actionBarBridge.setActionBarUpButtonEnabled(true);
    }

    public void readContacts() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {

            if (start_Limit == 0) {
                dialog = ProgressDialog.show(getActivity(), null, "Please Wait...");
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        arrayPhone.clear();
                        ContentResolver cr = getActivity().getContentResolver();
                        Cursor cur;

                        String limit = String.valueOf(start_Limit) + ", " + PER_PAGE;

                        cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, "UPPER(" + ContactsContract.Contacts.DISPLAY_NAME + ") ASC LIMIT " + limit);
                        if (cur.getCount() > 0) {
                            arrayName.clear();
                            while (cur.moveToNext()) {
                                String id = cur.getString(cur.getColumnIndex(BaseColumns._ID));
                                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                            new String[]{id}, null);
                                    while (pCur.moveToNext()) {
                                        String phonenumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                        String contactname = pCur.getString(pCur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                                        phonenumber = phonenumber.replaceAll("[+()-]", "");
                                        phonenumber = phonenumber.replaceAll(" ", "");
                                        //phone = removeCountryCode(phone);
                                        for (String prefix : NUMBER_PREFIX) {
                                            if (phonenumber.startsWith(prefix))
                                                phonenumber = phonenumber.replaceFirst(prefix, "");
                                        }
                                        phonenumber = COUNTRY_CODE + phonenumber;
                                        if (!arrayPhone.contains(phonenumber.trim())) {
                                            if (!phonenumber.isEmpty()) {
                                                arrayPhone.add(phonenumber);
                                                arrayName.add(contactname);
                                            }
                                        }
                                    }
                                    pCur.close();
                                }
                            }
                        } else {
                            isload = false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isload) {
                                sendContact();
                            } else {
                                if (dialog.isShowing() != false && dialog != null) {
                                    dialog.dismiss();
                                }
                            }
                        }
                    });
                }

            }).start();

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //MainActivity.mainActivity.recreate();
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().getSupportFragmentManager().popBackStack();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void sendContact() {
        Log.v("Contacts", "Contacts: " + arrayPhone.toString());
        PagedRequestBuilder pagedRequestBuilder = new PagedRequestBuilder();
        pagedRequestBuilder.setPage(intCurrentPage);
        pagedRequestBuilder.setPerPage(arrayPhone.size());


        ConnectycubeUsers.getUsersByPhoneNumbers(arrayPhone, pagedRequestBuilder).performAsync(new EntityCallback<ArrayList<ConnectycubeUser>>() {
            @Override
            public void onSuccess(ArrayList<ConnectycubeUser> users, Bundle params) {
                dialog.dismiss();
                Log.i(TAG, ">>> Users: " + users.toString());
                Log.i(TAG, "currentPage: " + params.getInt(Consts.CURR_PAGE));
                Log.i(TAG, "perPage: " + params.getInt(Consts.PER_PAGE));
                Log.i(TAG, "totalPages: " + params.getInt(Consts.TOTAL_ENTRIES));
                //contactsModels.clear();
                Log.v("Phone Contacts", arrayPhone.toString());
                Log.v("QB Contacts", users.toString());

                if (!mIsNewMessage) {
                    for (int i = 0; i < arrayPhone.size(); i++) {
                        contactsModels.add(new ContactsModel(arrayPhone.get(i), arrayName.get(i), "0"));
                    }
                }

                if (users.size() > 0) {
                    for (int j = 0; j < users.size(); j++) {
                        if (arrayPhone.contains(users.get(j).getPhone())) {
                            int position = arrayPhone.indexOf(users.get(j).getPhone());
                            if (position >= 0) {
                                if (!mIsNewMessage) {
                                    contactsModels.get(position).setIsReg_type("1");
                                    contactsModels.get(position).setId_user(users.get(j).getId());
                                } else {
                                    contactsModels.add(new ContactsModel(arrayPhone.get(position), arrayName.get(position), "1", users.get(j).getId()));
                                }
                            }
                            //arrayPhone.remove(position);
                        }
                    }
                }
                contactsAdapter.notifyDataSetChanged();
                start_Limit = arrayName.size();
                if (arrayName.size() < PER_PAGE) {
                    loading = false;
                } else {
                    loading = true;
                }
            }

            @Override
            public void onError(ResponseException errors) {
                Log.e("Error", errors.getErrors().toString());
            }
        });
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        baseActivity.getSupportFragmentManager().popBackStack();
        return false;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        baseActivity.getSupportFragmentManager().popBackStack();
        return false;
    }


}
