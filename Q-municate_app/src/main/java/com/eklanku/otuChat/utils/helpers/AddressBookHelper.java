package com.eklanku.otuChat.utils.helpers;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.connectycube.users.model.ConnectycubeAddressBookContact;
import com.connectycube.users.model.ConnectycubeAddressBookResponse;
import com.connectycube.users.model.ConnectycubeUser;
import com.eklanku.otuChat.App;
import com.eklanku.otuChat.ui.activities.contacts.ContactsModel;
import com.quickblox.q_municate_core.qb.helpers.QBFriendListHelper;

import java.util.ArrayList;
import java.util.regex.Pattern;

import rx.Observable;
import rx.functions.Func1;

public class AddressBookHelper {
    public static final String REG_TYPE_REGISTERED = "1";
    public static final String REG_TYPE_NOT_REGISTERED = "0";

    private static final int COUNTRY_CODE = 62;

    private static volatile AddressBookHelper instance;
    private ServiceManager serviceManager;

    private AddressBookHelper() {
        serviceManager = ServiceManager.getInstance();
    }

    public static AddressBookHelper getInstance() {
        if (instance == null) {
            synchronized (AddressBookHelper.class) {
                if (instance == null) {
                    instance = new AddressBookHelper();
                }
            }
        }
        return instance;
    }

    public Observable<ConnectycubeAddressBookResponse> updateAddressBookContactList(DbHelper mDbHelper, String currentUserPhone) {
        return uploadAddressBookObservable(mDbHelper, currentUserPhone);
    }

    public Observable<Boolean> updateRosterContactList(QBFriendListHelper friendListHelper, DbHelper mDbHelper, String currentUserPhone) {
        return getRegisteredContactsWithoutCurrent(currentUserPhone).flatMap((Func1<ArrayList<ConnectycubeUser>, Observable<Boolean>>) connectycubeUsers -> {
            for (ConnectycubeUser user : connectycubeUsers) {
                if (!mDbHelper.isConnectycubeUser(user.getPhone())) {
                    mDbHelper.updateContact(user.getPhone(), user.getId());
                }
                String fullName = mDbHelper.getNamebyNumber(user.getPhone());
                user.setFullName(fullName);
            }
            return subscribeToAllUsersObservable(friendListHelper, connectycubeUsers);
        });
    }

    public Observable<ArrayList<ConnectycubeUser>> getRegisteredContactsWithoutCurrent(String currentUserPhone) {
        return serviceManager.getRegisteredContacts().map(connectycubeUsers -> {
            for (ConnectycubeUser user : connectycubeUsers) {
                if (user.getPhone().equals(currentUserPhone)) {
                    connectycubeUsers.remove(user);
                    break;
                }
            }
            return connectycubeUsers;
        });
    }

    public Observable<Boolean> updateContactListAndRoster(QBFriendListHelper friendListHelper, DbHelper mDbHelper, String currentUserPhone) {
        return uploadAddressBookObservable(mDbHelper, currentUserPhone).flatMap((Func1<ConnectycubeAddressBookResponse, Observable<ArrayList<ConnectycubeUser>>>)
                connectycubeAddressBookResponse -> getRegisteredContactsWithoutCurrent(currentUserPhone)).flatMap((Func1<ArrayList<ConnectycubeUser>, Observable<Boolean>>)
                connectycubeUsers -> {
                    for (ConnectycubeUser user : connectycubeUsers) {
                        if (!mDbHelper.isConnectycubeUser(user.getPhone())) {
                            mDbHelper.insertContact(new ContactsModel(user.getLogin(), user.getFullName(), REG_TYPE_REGISTERED, user.getId()));
                        }
                    }
                    return subscribeToAllUsersObservable(friendListHelper, connectycubeUsers);
                });
    }

    public void uploadAddressBook() {
        contactListObservable().flatMap((Func1<ArrayList<ConnectycubeAddressBookContact>, Observable<ConnectycubeAddressBookResponse>>)
                localContacts -> serviceManager.uploadAddressBook(localContacts));
    }

    public void deleteAddressBook() {
        serviceManager.getAddressBook().flatMap((Func1<ArrayList<ConnectycubeAddressBookContact>, Observable<ConnectycubeAddressBookResponse>>)
                contacts -> serviceManager.deleteAddressBook(contacts)).onErrorResumeNext(e -> Observable.empty()).subscribe();
    }

    private Observable<ConnectycubeAddressBookResponse> uploadAddressBookObservable(DbHelper mDbHelper, String currentUserPhone) {
        return contactListObservable().flatMap((Func1<ArrayList<ConnectycubeAddressBookContact>, Observable<ConnectycubeAddressBookResponse>>)
                localContacts -> {
                    for (ConnectycubeAddressBookContact contact : localContacts) {
                        if (!mDbHelper.isPhoneNumberExists(contact.getPhone()) && !contact.getPhone().equals(currentUserPhone)) {
                            mDbHelper.insertContact(new ContactsModel(contact.getPhone(), contact.getName(), REG_TYPE_NOT_REGISTERED));
                        }
                    }
                    return serviceManager.uploadAddressBook(localContacts);
                });
    }

    private Observable<Boolean> subscribeToAllUsersObservable(QBFriendListHelper friendListHelper, ArrayList<ConnectycubeUser> connectycubeUsers) {
        return Observable.defer(() -> {
            friendListHelper.subscribeToAllUsers(connectycubeUsers);
            return Observable.just(true);
        });
    }

    private Observable<ArrayList<ConnectycubeAddressBookContact>> contactListObservable() {
        return Observable.defer(() -> {
            ArrayList<ConnectycubeAddressBookContact> contacts = getContactList();
            return Observable.just(contacts);
        });
    }

    private ArrayList<ConnectycubeAddressBookContact> getContactList() {
        ArrayList<ConnectycubeAddressBookContact> contacts = new ArrayList<>();

        Cursor phones = App.getInstance().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (phones != null) {
            while (phones.moveToNext()) {
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phoneNumber = normalizePhoneNumber(phoneNumber);
                phoneNumber = addCountryCodeIfNeed(phoneNumber);
                contacts.add(new ConnectycubeAddressBookContact(name, phoneNumber));
            }
            phones.close();
        }
        return contacts;
    }

    private String normalizePhoneNumber(String number) {
        String pattern = "[^A-Za-z0-9]";
        Pattern replace = Pattern.compile(pattern);
        return replace.matcher(number).replaceAll("");
    }

    private String addCountryCodeIfNeed(String phoneNumber) {
        if (phoneNumber.startsWith("0")) {
            phoneNumber = phoneNumber.replaceFirst("0", String.valueOf(COUNTRY_CODE));
        } else if (!phoneNumber.startsWith(String.valueOf(COUNTRY_CODE))) {
            phoneNumber = COUNTRY_CODE + phoneNumber;
        }
        return phoneNumber;
    }
}
