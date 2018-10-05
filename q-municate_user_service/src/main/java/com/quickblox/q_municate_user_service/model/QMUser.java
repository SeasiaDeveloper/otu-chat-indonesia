package com.quickblox.q_municate_user_service.model;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.table.DatabaseTable;
import com.quickblox.q_municate_user_service.utils.Utils;
import com.connectycube.users.model.ConnectycubeUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by pelipets on 1/10/17.
 */

@DatabaseTable(tableName = QMUserColumns.TABLE_NAME)
public class QMUser extends ConnectycubeUser {

    private String avatar;

    private String status;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String username;

    public static QMUser convert(ConnectycubeUser connectycubeUser){
        QMUser result = new QMUser();
        result.setId(connectycubeUser.getId());
        result.setFullName(connectycubeUser.getFullName());
        result.setEmail(connectycubeUser.getEmail());
        result.setLogin(connectycubeUser.getLogin());
        result.setPhone(connectycubeUser.getPhone());
        result.setWebsite(connectycubeUser.getWebsite());
        result.setLastRequestAt(connectycubeUser.getLastRequestAt());
        result.setExternalId(connectycubeUser.getExternalId());
        result.setFacebookId(connectycubeUser.getFacebookId());
        result.setTwitterId(connectycubeUser.getTwitterId());
        result.setTwitterDigitsId(connectycubeUser.getTwitterDigitsId());
        result.setFileId(connectycubeUser.getFileId());
        result.setTags(connectycubeUser.getTags());
        result.setPassword(connectycubeUser.getPassword());
        result.setOldPassword(connectycubeUser.getOldPassword());
        result.setCustomData(connectycubeUser.getCustomData());
        result.setCreatedAt(connectycubeUser.getCreatedAt());
        result.setUpdatedAt(connectycubeUser.getUpdatedAt());

        final QMUserCustomData userCustomData = Utils.customDataToObject(connectycubeUser.getCustomData());
        result.setAvatar(userCustomData.getAvatarUrl());
        result.setStatus(userCustomData.getStatus());
        return result;
    }

    public static List<QMUser> convertList(List<ConnectycubeUser> connectycubeUsers){
        List<QMUser> result = new ArrayList<QMUser>(connectycubeUsers.size());
        for(ConnectycubeUser connectycubeUser: connectycubeUsers){
            result.add(QMUser.convert(connectycubeUser));
        }
        return result;
    }
}
