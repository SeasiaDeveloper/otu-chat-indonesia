package com.eklanku.otuChat.utils.helpers;

import android.util.Log;

import com.eklanku.otuChat.ui.activities.contacts.ContactsModel;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ContactJsonHelper {

    public static String createContactsJsonFromList(ArrayList<ContactsModel> contacts) {
        List<JsonObject> jsonObjects = new ArrayList<>();
        for (ContactsModel model : contacts) {
            JsonObject contactObject = createContactJsonFromContactModel(model);
            jsonObjects.add(contactObject);
        }
        return new Gson().toJson(jsonObjects);
    }

    public static ArrayList<ContactsModel> convertJsonContactToList(String jsonContacts) {
        ArrayList<ContactsModel> contacts = new ArrayList<>();
        Type listType = new TypeToken<ArrayList<JsonObject>>() {
        }.getType();
        List<JsonObject> jsonList = new Gson().fromJson(jsonContacts, listType);
        Log.d("AMBRA", "convertJsonContactToList jsonContacts= " + jsonContacts);
        for (JsonObject jsonObject : jsonList) {
            String name = jsonObject.get("name").toString();
            String phone = jsonObject.get("phone").toString();
            contacts.add(new ContactsModel(phone, name, "0"));
        }
        return contacts;
    }

    private static JsonObject createContactJsonFromContactModel(ContactsModel model) {
        JsonObject contactObject = new JsonObject();
        contactObject.addProperty("name", model.getFullName());
        contactObject.addProperty("phone", model.getPhone());
        return contactObject;
    }
}
