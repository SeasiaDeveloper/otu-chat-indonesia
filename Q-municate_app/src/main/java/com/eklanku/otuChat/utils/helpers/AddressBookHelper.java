package com.eklanku.otuChat.utils.helpers;

import android.database.Cursor;
import android.provider.ContactsContract;

import com.connectycube.users.model.ConnectycubeAddressBookContact;
import com.connectycube.users.model.ConnectycubeAddressBookResponse;
import com.eklanku.otuChat.App;

import java.util.ArrayList;
import java.util.regex.Pattern;

import rx.Observable;
import rx.functions.Func1;

public class AddressBookHelper {

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

    public void uploadAddressBook() {
        getContactListObservable().flatMap((Func1<ArrayList<ConnectycubeAddressBookContact>, Observable<ConnectycubeAddressBookResponse>>)
                localContacts -> serviceManager.uploadAddressBook(localContacts)).onErrorResumeNext(e -> Observable.empty()).subscribe();
    }

    public void deleteAddressBook() {
        serviceManager.getAddressBook().flatMap((Func1<ArrayList<ConnectycubeAddressBookContact>, Observable<ConnectycubeAddressBookResponse>>)
                contacts -> serviceManager.deleteAddressBook(contacts)).onErrorResumeNext(e -> Observable.empty()).subscribe();
    }

    private Observable<ArrayList<ConnectycubeAddressBookContact>> getContactListObservable() {
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
}
