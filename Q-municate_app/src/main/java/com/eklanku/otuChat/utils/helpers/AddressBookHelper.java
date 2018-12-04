package com.eklanku.otuChat.utils.helpers;

import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.connectycube.users.model.ConnectycubeAddressBookContact;
import com.connectycube.users.model.ConnectycubeAddressBookResponse;
import com.connectycube.users.model.ConnectycubeUser;
import com.eklanku.otuChat.App;
import com.eklanku.otuChat.ui.activities.contacts.ContactsModel;
import com.quickblox.q_municate_core.qb.helpers.QBFriendListHelper;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import ezvcard.Ezvcard;
import ezvcard.VCard;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class AddressBookHelper {
    public static final String REG_TYPE_REGISTERED = "1";
    public static final String REG_TYPE_NOT_REGISTERED = "0";

    private static final String TAG = AddressBookHelper.class.getSimpleName();
    private static final int COUNTRY_CODE = 62;

    private static volatile AddressBookHelper instance;
    private ServiceManager serviceManager;
    private List<VCard> vCardsCache;

    private AddressBookHelper() {
        serviceManager = ServiceManager.getInstance();
        this.vCardsCache = vCardsCache != null ? vCardsCache : new ArrayList<>();
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

    private Observable<List<VCard>> getContactVCardListQuery() {
        Observable<ArrayList<VCard>> observableVCard = getContactVCardListObservable();

        return observableVCard.map((Func1<List<VCard>, List<VCard>>) vCards -> {
            vCardsCache.clear();
            vCardsCache.addAll(vCards);
            return vCardsCache;
        }).onErrorResumeNext((Func1<Throwable, Observable<List<VCard>>>) throwable -> {
            ErrorUtils.logError(TAG, throwable);
            return Observable.just(Collections.<VCard>emptyList());
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<VCard>> getContactVCardListSilentUpdate() {
        updateVCardsCacheIfNeed();
        return vCardsCache.isEmpty() ? getContactVCardListQuery() : getContactVCardListCache();
    }

    private Observable<List<VCard>> getContactVCardListCache() {
        return Observable.just(vCardsCache);
    }

    private void updateVCardsCacheIfNeed() {
        if (!vCardsCache.isEmpty()) {
            getContactVCardListObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(vCards -> {
                        vCardsCache.clear();
                        vCardsCache.addAll(vCards);
                    });
        }
    }

    public Observable<List<VCard>> getContactVCardListUpdate() {
        return vCardsCache.isEmpty() ? getContactVCardListQuery() : getContactVCardListCacheWithUpdate();
    }

    private Observable<List<VCard>> getContactVCardListCacheWithUpdate() {
        return Observable.create((Observable.OnSubscribe<List<VCard>>) emitter -> {
            try {
                emitter.onNext(vCardsCache);
                updateVCardsCache();
                emitter.onNext(vCardsCache);
                emitter.onCompleted();
            } catch (Exception e) {
                emitter.onError(e);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void updateVCardsCache() throws IOException {
        ArrayList<VCard> contacts = contactVCardList();
        vCardsCache.clear();
        vCardsCache.addAll(contacts);
    }

    private Observable<ArrayList<VCard>> getContactVCardListObservable() {
        return Observable.defer(() -> {
            ArrayList<VCard> contacts = null;
            try {
                contacts = contactVCardList();
            } catch (IOException e) {
                Observable.error(e);
            }
            return Observable.just(contacts);
        });
    }

    private ArrayList<VCard> contactVCardList() throws IOException {
        Set<VCard> vCards = new HashSet<>();

        Cursor phones = App.getInstance().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        if (phones != null) {
            while (phones.moveToNext()) {
                String vCardStr = getVCardFromUri(phones);
                VCard vcard = Ezvcard.parse(vCardStr).first();
                vCards.add(vcard);
            }
            phones.close();
        }
        ArrayList<VCard> contacts = new ArrayList<>(vCards);
        return contacts;
    }

    private String getVCardFromUri(Cursor phones) throws IOException {
        String lookupKey = phones.getString(phones.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
        AssetFileDescriptor fd = App.getInstance().getContentResolver().openAssetFileDescriptor(uri, "r");
        FileInputStream fis = fd.createInputStream();

        byte[] buf = readBytes(fis);
        return new String(buf);
    }

    private byte[] readBytes(InputStream inputStream) throws IOException {
        // this dynamically extends to take the bytes you read
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }
}
