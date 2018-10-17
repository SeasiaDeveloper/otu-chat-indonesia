package com.quickblox.q_municate_base_service;

import com.connectycube.core.exception.ResponseException;
import com.connectycube.users.model.ConnectycubeUser;

public interface QMServiceManagerListener {

    /**
     *  Get user from current session
     *
     *  @return QBUUser instance
     */
    ConnectycubeUser getCurrentUser();

    /**
     *  Check is current session is authorized
     *
     *  @return true if authorized
     */
    boolean isAuthorized();

}
