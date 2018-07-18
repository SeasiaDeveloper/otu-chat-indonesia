package com.quickblox.q_municate_db.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import static com.quickblox.q_municate_db.models.Message.Column.BODY;
import static com.quickblox.q_municate_db.models.Message.Column.CREATED_DATE;
import static com.quickblox.q_municate_db.models.Message.Column.ID;
import static com.quickblox.q_municate_db.models.Message.Column.STATE;
import static com.quickblox.q_municate_db.models.Message.Column.TABLE_NAME;

@DatabaseTable(tableName = TABLE_NAME)
public class Message implements Serializable {

    @DatabaseField(
            id = true,
            unique = true,
            columnName = ID)
    private String messageId;

    @DatabaseField(
            foreign = true,
            foreignAutoRefresh = true,
            canBeNull = false,
            columnDefinition = "INTEGER REFERENCES " + DialogOccupant.Column.TABLE_NAME + "(" + DialogOccupant.Column.ID + ") ON DELETE CASCADE",
            columnName = DialogOccupant.Column.ID)
    private DialogOccupant dialogOccupant;

    @DatabaseField(
            foreign = true,
            foreignAutoRefresh = true,
            canBeNull = true,
            columnName = Attachment.Column.ID)
    private Attachment attachment;

    @DatabaseField(
            columnName = STATE)
    private State state;

    @DatabaseField(
            columnName = BODY)
    private String body;

    @DatabaseField(
            columnName = CREATED_DATE)
    private long createdDate;

    @DatabaseField(
            columnName = Column.PROPERTY_REPLY_MESSAGE)
    private String PROPERTY_REPLY_MESSAGE;

    @DatabaseField(
            columnName = Column.SENDER_ID)
    private int senderId;

    @DatabaseField(
            columnName = Column.RECIPENT_ID)
    private int recipentId;

    @DatabaseField(
            columnName = Column.DIALOG_ID)
    private String dialogId;


    public Message() {
    }

    public Message(String messageId, DialogOccupant dialogOccupant, Attachment attachment, State state,
                   String body, long createdDate) {
        this.messageId = messageId;
        this.dialogOccupant = dialogOccupant;
        this.attachment = attachment;
        this.state = state;
        this.body = body;
        this.createdDate = createdDate;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
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

    public Attachment getAttachment() {
        return attachment;
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public DialogOccupant getDialogOccupant() {
        return dialogOccupant;
    }

    public String getPROPERTY_REPLY_MESSAGE() {
        return PROPERTY_REPLY_MESSAGE;
    }

    public void setPROPERTY_REPLY_MESSAGE(String PROPERTY_REPLY_MESSAGE) {
        this.PROPERTY_REPLY_MESSAGE = PROPERTY_REPLY_MESSAGE;
    }

    public int getSenderId() {
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

    public void setDialogOccupant(DialogOccupant dialogOccupant) {
        this.dialogOccupant = dialogOccupant;
    }

    public boolean isIncoming(int currentUserId) {
        return dialogOccupant != null &&  dialogOccupant.getUser() != null && currentUserId != dialogOccupant.getUser().getId();
    }

    @Override
    public String toString() {
        return "Message [messageId='" + messageId
                + "', dialogOccupant='" + dialogOccupant
                + "', body='" + body
                + "', createdDate='" + createdDate
                + "', property_reply_message='" + PROPERTY_REPLY_MESSAGE
                + "', state='" + state + "']";
    }

    public interface Column {

        String TABLE_NAME = "message";
        String ID = "message_id";
        String BODY = "body";
        String CREATED_DATE = "created_date";
        String STATE = "state";
        String PROPERTY_REPLY_MESSAGE = "property_reply_message";
        String SENDER_ID = "senderId";
        String RECIPENT_ID = "recipentId";
        String DIALOG_ID = "dialogId";

    }
}