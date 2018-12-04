package com.eklanku.otuChat.utils.helpers.vcard;

import android.content.ContentValues;
import android.content.Intent;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

import ezvcard.VCard;
import ezvcard.property.Birthday;
import ezvcard.property.Email;
import ezvcard.property.Organization;
import ezvcard.property.Telephone;
import ezvcard.property.Url;

public class ContactVCardIntentHelper {

    public static void fillContactIntentInsertData(VCard card, Intent intent) {
        fillName(card, intent);
        fillNumbers(card, intent);
        fillEmails(card, intent);
        fillCompanies(card, intent);

//      fill other custom fields
        ArrayList<ContentValues> data = new ArrayList<>();
        fillUrls(card, data);
        fillBirthday(card, data);

        intent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, data);
    }

    static void fillName(VCard card, Intent intent) {
        String numberFirst = card.getTelephoneNumbers().get(0) != null ? card.getTelephoneNumbers().get(0).getText() : "";
        String name = card.getFormattedName() != null ? card.getFormattedName().getValue() : numberFirst;
        intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
    }

    static void fillNumbers(VCard card, Intent intent) {
        List<Telephone> numbers = card.getTelephoneNumbers();
        for (int i = 0; i < numbers.size(); i++) {
            Telephone telephone = numbers.get(i);

            if (i == 0) {
                intent.putExtra(ContactsContract.Intents.Insert.PHONE, telephone.getText());
            } else if (i == 1) {
                intent.putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE, telephone.getText());
            } else if (i == 2) {
                intent.putExtra(ContactsContract.Intents.Insert.TERTIARY_PHONE, telephone.getText());
            }
            fillNumberTypes(telephone, intent);
        }
    }

    static void fillNumberTypes(Telephone telephone, Intent intent) {
        for (int i = 0; i < telephone.getTypes().size(); i++) {
            String telephoneType = telephone.getTypes().get(i).getValue();
            if (i == 0) {
                intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, telephoneType);
            } else if (i == 1) {
                intent.putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE_TYPE, telephoneType);
            } else if (i == 2) {
                intent.putExtra(ContactsContract.Intents.Insert.TERTIARY_PHONE_TYPE, telephoneType);
            }
        }
    }

    static void fillEmails(VCard card, Intent intent) {
        List<Email> emails = card.getEmails();
        for (int i = 0; i < emails.size(); i++) {
            Email email = emails.get(i);
            if (i == 0) {
                intent.putExtra(ContactsContract.Intents.Insert.EMAIL, email.getValue());
            } else if (i == 1) {
                intent.putExtra(ContactsContract.Intents.Insert.SECONDARY_EMAIL, email.getValue());
            } else if (i == 2) {
                intent.putExtra(ContactsContract.Intents.Insert.TERTIARY_EMAIL, email.getValue());
            }
            fillEmailsTypes(email, intent);
        }
    }

    static void fillEmailsTypes(Email email, Intent intent) {
        for (int i = 0; i < email.getTypes().size(); i++) {
            String emailType = email.getTypes().get(i).getValue();
            if (i == 0) {
                intent.putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, emailType);
            } else if (i == 1) {
                intent.putExtra(ContactsContract.Intents.Insert.SECONDARY_EMAIL_TYPE, emailType);
            } else if (i == 2) {
                intent.putExtra(ContactsContract.Intents.Insert.TERTIARY_EMAIL_TYPE, emailType);
            }
        }
    }

    static void fillCompanies(VCard card, Intent intent) {
        Organization organization = card.getOrganization();
        if(organization != null) {
            intent.putExtra(ContactsContract.Intents.Insert.COMPANY, organization.getValues().get(0));
        }
    }

    static void fillUrls(VCard card, ArrayList<ContentValues> data) {
        List<Url> urls = card.getUrls();

        for (int i = 0; i < urls.size(); i++) {
            Url url = urls.get(i);
            ContentValues row = new ContentValues();
            row.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE);
            row.put(ContactsContract.CommonDataKinds.Website.TYPE, url.getType());
            row.put(ContactsContract.CommonDataKinds.Website.URL, url.getValue());
            data.add(row);
        }
    }

    static void fillBirthday(VCard card, ArrayList<ContentValues> data) {
        Birthday birthday = card.getBirthday();
        if(birthday != null) {
            ContentValues row = new ContentValues();
            row.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE);
            row.put(ContactsContract.CommonDataKinds.Event.TYPE, ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY);
            row.put(ContactsContract.CommonDataKinds.Event.START_DATE, birthday.getDate().toString());
            data.add(row);
        }
    }
}
