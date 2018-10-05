package com.quickblox.q_municate_core.models;

import android.util.Log;

import com.google.gson.Gson;
import com.connectycube.chat.model.ConnectycubeAttachment;
import com.connectycube.chat.model.ConnectycubeChatMessage;
import com.quickblox.q_municate_db.models.Attachment;
import com.quickblox.q_municate_db.models.DialogNotification;
import com.quickblox.q_municate_db.models.DialogOccupant;
import com.quickblox.q_municate_db.models.Message;
import com.quickblox.q_municate_db.models.State;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Comparator;

// Combination DialogNotification and Message (for chats)
public class CombinationMessage extends ConnectycubeChatMessage implements Serializable {

    private String messageId;
    private DialogOccupant dialogOccupant;
    private Attachment attachment;
    private State state;
    private String body;
    private long createdDate;
    private ConnectycubeAttachment connectycubeAttachment;
    //private Map<String,String> properties;
    private String replyMessage;
    private DialogNotification.Type notificationType;

    private int senderId;

    private int recipentId;

    private String dialogId;

    public CombinationMessage(DialogNotification dialogNotification) {
        this.messageId = dialogNotification.getDialogNotificationId();
        this.dialogOccupant = dialogNotification.getDialogOccupant();
        this.state = dialogNotification.getState();
        this.createdDate = dialogNotification.getCreatedDate();
        this.notificationType = dialogNotification.getType();
        this.body = dialogNotification.getBody();
    }

    public CombinationMessage(Message message) {
        this.messageId = message.getMessageId();
        this.dialogOccupant = message.getDialogOccupant();
        this.attachment = message.getAttachment();
        this.state = message.getState();
        this.body = message.getBody();
        this.createdDate = message.getCreatedDate();
        this.replyMessage = message.getPROPERTY_REPLY_MESSAGE();
        this.senderId = message.getSenderId();
        this.recipentId = message.getRecipentId();
        this.dialogId = message.getDialogId();
        this.setDateSent(createdDate);
        addConnectycubeAttachment(attachment);
    }

    public ConnectycubeAttachment getConnectycubeAttachment() {
        return connectycubeAttachment;
    }

    public void setConnectycubeAttachment(ConnectycubeAttachment connectycubeAttachment) {
        this.connectycubeAttachment = connectycubeAttachment;
    }

    /*@Override
    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }*/

    public String getReplyMessage() {
        return replyMessage;
    }

    public void setReplyMessage(String replyMessage) {
        this.replyMessage = replyMessage;
    }

    public Message toMessage() {
        Message message = new Message();
        message.setMessageId(messageId);
        message.setDialogOccupant(dialogOccupant);
        message.setAttachment(attachment);
        message.setState(state);
        message.setBody(body);
        message.setCreatedDate(createdDate);
        message.setPROPERTY_REPLY_MESSAGE(replyMessage);
        return message;
    }

    public DialogNotification toDialogNotification() {
        DialogNotification dialogNotification = new DialogNotification();
        dialogNotification.setDialogNotificationId(messageId);
        dialogNotification.setDialogOccupant(dialogOccupant);
        dialogNotification.setState(state);
        dialogNotification.setType(notificationType);
        dialogNotification.setBody(body);
        dialogNotification.setCreatedDate(createdDate);
        return dialogNotification;
    }

    public String getMessageId() {
        return messageId;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getRecipentId() {
        return recipentId;
    }

    public void setRecipentId(int recipentId) {
        this.recipentId = recipentId;
    }

    public String getDialogId() {
        return dialogId;
    }

    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public DialogNotification.Type getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(DialogNotification.Type notificationType) {
        this.notificationType = notificationType;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Attachment getAttachment() {
        return attachment;
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }

    public DialogOccupant getDialogOccupant() {
        return dialogOccupant;
    }

    public void setDialogOccupant(DialogOccupant dialogOccupant) {
        this.dialogOccupant = dialogOccupant;
    }

    public boolean isIncoming(int currentUserId) {
        return dialogOccupant != null && dialogOccupant.getUser() != null && currentUserId != dialogOccupant.getUser().getId().intValue();
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder(CombinationMessage.class.getSimpleName());
        sb.append("[ messageId = " + getMessageId())
                .append(", dialogOccupant = " + getDialogOccupant())
                .append(", attachment = " + getAttachment())
                .append(", state = " + getState())
                .append(", body = " + getBody())
                .append(", createdDate = " + getCreatedDate())
                .append(", notificationType = " + getNotificationType()+ " ]");
        return sb.toString();
    }


    public String getReplyMessageString(){
        JSONObject object = new JSONObject();
        JSONArray attach = new JSONArray();


        if(getAttachment()!=null) {
            String attachStr = new Gson().toJson(getAttachments()); //getAttachment().getAttachString();
            if(attachStr.contains("\"id\"")) {
                attachStr = attachStr.replaceFirst("\"id\"", "\"ID\"");
            }
            Log.v("ReplyMessage", attachStr);
            try {
                attach = new JSONArray(attachStr);
                if(attach.getJSONObject(0).has("data")) {
                    String strData = attach.getJSONObject(0).getString("data");
                    JSONObject objLocation = new JSONObject();
                    objLocation.put("content-type", "location");
                    objLocation.put("data", strData);
                    attach.getJSONObject(0).put("customParameters", objLocation);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            object.put("recipientID", getRecipentId());
            object.put("attachments", attach);
            object.put("text", getBody());
            JSONArray array = new JSONArray();
            array.put(getSenderId());
            array.put(getRecipentId());
            object.put("createdDate", getCreatedDate());
            object.put("deliveredIDs", getDeliveredIds());
            object.put("dialogID", getDialogId());
            object.put("senderID", getSenderId());
            object.put("ID", getMessageId());
            object.put("readIDs", getReadIds());

            JSONObject objectCustomParameter = new JSONObject();
            objectCustomParameter.put("message_id", getMessageId());
            object.put("customParameters", objectCustomParameter);
            object.put("notificationType", getNotificationType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /*StringBuilder sb = new StringBuilder();
        //int[] ids = {getSenderId(),getRecipentId()};
        String attach ="[]";
        if(getAttachment()!=null) {
            attach = getAttachment().getAttachString();
        }
        sb.append("{recipientID:'" + getRecipentId()+"'")
                .append(",attachments:'" + attach+"'")
                .append(",text:'" + getBody()+"'")
                .append(",createdDate:'" + getCreatedDate()+"'")
                .append(",deliveredIDs:'" + "["+getSenderId()+","+getRecipentId()+"]")
                .append(",dialogID:'" + getDialogId()+"'")
                .append(",senderID:'" + getSenderId()+"'")
                .append(",ID:'" + getMessageId()+"'")
                .append(",readIDs:" + "["+getSenderId()+","+getRecipentId()+"]")
                .append(",customParameters:" + "{message_id:'"+getMessageId()+"'}")
                .append(",notificationType:" + getNotificationType()+ " }");*/
        Log.v("ReplyMessage", object.toString());
        return object.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof CombinationMessage) {
            return ((CombinationMessage) object).getMessageId().equals(messageId);
        } else {
            return false;
        }
    }

    private void addConnectycubeAttachment(Attachment attachment) {
        String attachType;
        if(attachment == null ){
            return;
        }
        if(attachment.getType() == null){
            attachType = connectycubeAttachment.IMAGE_TYPE;
        } else {
            attachType = attachment.getType().name().toLowerCase();
        }
        connectycubeAttachment = new ConnectycubeAttachment(attachType);
        connectycubeAttachment.setId(attachment.getAttachmentId());
        connectycubeAttachment.setUrl(attachment.getRemoteUrl());
        connectycubeAttachment.setName(attachment.getName());
        connectycubeAttachment.setSize(attachment.getSize());
        connectycubeAttachment.setHeight(attachment.getHeight());
        connectycubeAttachment.setWidth(attachment.getWidth());
        connectycubeAttachment.setDuration(attachment.getDuration());
        connectycubeAttachment.setData(attachment.getAdditionalInfo());
        this.addAttachment(connectycubeAttachment);
    }

    public static class DateComparator implements Comparator<CombinationMessage> {

        @Override
        public int compare(CombinationMessage combinationMessage1, CombinationMessage combinationMessage2) {
            return ((Long) combinationMessage1.getCreatedDate()).compareTo(
                    ((Long) combinationMessage2.getCreatedDate()));
        }
    }
}