package com.eklanku.otuChat.utils.helpers.vcard;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

import ezvcard.Ezvcard;
import ezvcard.VCard;

public class ContactVCardConvertHelper {

    public static String createContactsJsonFromList(ArrayList<String> contacts) {
        return new Gson().toJson(contacts);
    }

    public static ArrayList<String> convertJsonContactToList(String jsonContacts) {
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return new Gson().fromJson(jsonContacts, type);
    }

    public static ArrayList<String> convertVCardListToStringList(ArrayList<VCard> vCards) {
        ArrayList<String> contactsRaw = new ArrayList<>(vCards.size());
        for (VCard vCard : vCards) {
            contactsRaw.add(vCard.write());
        }
        return contactsRaw;
    }

    public static ArrayList<VCard> convertStringListToVCardList(ArrayList<String> contactsRow) {
        ArrayList<VCard> vCards = new ArrayList<>(contactsRow.size());
        for (String vCardStr : contactsRow) {
            vCards.add(Ezvcard.parse(vCardStr).first());
        }
        return vCards;
    }

    public static void sortVCards(ArrayList<VCard> vCards) {
        Collections.sort(vCards, (o1, o2) -> {
            String card1 = o1.getFormattedName() != null ? o1.getFormattedName().getValue() : o1.getTelephoneNumbers().get(0).getText();
            String card2 = o2.getFormattedName() != null ? o2.getFormattedName().getValue() : o2.getTelephoneNumbers().get(0).getText();
            return card1.compareTo(card2);
        });
    }
}
