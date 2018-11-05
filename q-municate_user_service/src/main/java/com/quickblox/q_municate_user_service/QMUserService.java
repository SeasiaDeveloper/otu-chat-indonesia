package com.quickblox.q_municate_user_service;


import com.connectycube.core.exception.ResponseException;
import com.connectycube.core.request.PagedRequestBuilder;
import com.connectycube.core.server.Performer;
import com.connectycube.extensions.RxJavaPerformProcessor;
import com.connectycube.users.model.ConnectycubeAddressBookContact;
import com.connectycube.users.model.ConnectycubeAddressBookResponse;
import com.quickblox.q_municate_base_service.QMBaseService;
import com.quickblox.q_municate_user_service.cache.QMUserCache;
import com.quickblox.q_municate_user_service.cache.QMUserMemoryCache;
import com.quickblox.q_municate_user_service.model.QMUser;
import com.quickblox.q_municate_user_service.model.QMUserColumns;
import com.connectycube.users.ConnectycubeUsers;
import com.connectycube.users.model.ConnectycubeUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class QMUserService extends QMBaseService {

    private static QMUserService instance;

    public static QMUserService getInstance(){
        return instance;
    }

    @Inject
    protected QMUserCache userCache;
    private ArrayList<ConnectycubeAddressBookContact> addressBookCache;

    public static void init(QMUserCache userCache){
        instance = new QMUserService(userCache);
    }

    public QMUserService(){
        super.init(userCache) ;
    }

    public QMUserService(QMUserCache userCache){
        this.userCache = userCache != null ? userCache : new QMUserMemoryCache();
        this.addressBookCache = addressBookCache != null ? addressBookCache : new ArrayList<ConnectycubeAddressBookContact>();
        super.init(userCache) ;
    }

    @Override
    protected void serviceWillStart() {
    }

    public QMUserCache getUserCache() {
        return userCache;
    }

    public Observable<QMUser> getUser(final int userId){
        return getUser(userId, true);
    }

    public Observable<QMUser> getUser(final int userId, boolean forceLoad){
        return  getUserByColumn(QMUserColumns.ID, String.valueOf(userId), forceLoad);
    }

    public QMUser getUserSync(final int userId, boolean forceLoad) throws ResponseException {
        return  getUserByColumnSync(QMUserColumns.ID, String.valueOf(userId), forceLoad);
    }

    public Observable<QMUser> getUserByLogin(final String login){
        return getUserByLogin(login, true);
    }

    public Observable<QMUser> getUserByLogin(final String login, boolean forceLoad){
        return  getUserByColumn(QMUserColumns.LOGIN, login, forceLoad);
    }

    public Observable<QMUser> getUserByFacebookId(final String facebookId){
        return getUserByFacebookId(facebookId, true);
    }

    public Observable<QMUser> getUserByFacebookId(final String facebookId, boolean forceLoad){
        return  getUserByColumn(QMUserColumns.FACEBOOK_ID, facebookId, forceLoad);

    }

    public Observable<QMUser> getUserByTwitterId(final String twitterId){
        return getUserByTwitterId(twitterId, true);
    }

    public Observable<QMUser> getUserByTwitterId(final String twitterId, boolean forceLoad) {
        return getUserByColumn(QMUserColumns.TWITTER_ID, twitterId, forceLoad);
    }

    public Observable<QMUser> getUserByTwitterDigitsId(final String twitterDigitsId){
        return getUserByTwitterDigitsId(twitterDigitsId, true);
    }

    public Observable<QMUser> getUserByTwitterDigitsId(final String twitterDigitsId, boolean forceLoad){
        return getUserByColumn(QMUserColumns.TWITTER_DIGITS_ID, twitterDigitsId, forceLoad);
    }

    public Observable<QMUser> getUserByEmail(final String email){
        return getUserByEmail(email, true);
    }

    public Observable<QMUser> getUserByEmail(final String email, boolean forceLoad){
        return getUserByColumn(QMUserColumns.EMAIL, email, forceLoad);
    }

    public Observable<QMUser> getUserByExternalId(final String externalId){
        return getUserByExternalId(externalId, true);
    }

    public Observable<QMUser> getUserByExternalId(final String externalId, boolean forceLoad){
        return getUserByColumn(QMUserColumns.EXTERNAL_ID, externalId, forceLoad);
    }

    public Observable<QMUser> updateUser(final QMUser user){
        Observable<QMUser> result = null;

        Performer<ConnectycubeUser> performer = ConnectycubeUsers.updateUser(user);
        final Observable<ConnectycubeUser> observable = performer.convertTo(RxJavaPerformProcessor.INSTANCE);

        result = observable.map(new Func1<ConnectycubeUser,QMUser>() {
            @Override
            public QMUser call(ConnectycubeUser connectycubeUser) {
                QMUser result = QMUser.convert(connectycubeUser);
                userCache.update(result);
                return result;
            }
        });

        return result;
    }

    public QMUser updateUserSync(final QMUser user) throws ResponseException {
        QMUser result = null;

        result = QMUser.convert(ConnectycubeUsers.updateUser(user).perform());

        userCache.update(result);

        return result;
    }

    public Observable<Void> deleteUser(final int userId){
        Observable<Void> result = null;

        Performer<Void> performer = ConnectycubeUsers.deleteUser();
        final Observable<Void> observable = performer.convertTo(RxJavaPerformProcessor.INSTANCE);

        result = observable.flatMap(new Func1<Void, Observable<Void>>() {
            @Override
            public Observable<Void> call(Void aVoid) {
                userCache.deleteById(Long.valueOf(userId));
                return observable;
            }
        });

        return result;
    }

    public Observable<Void> deleteByExternalId(final String externalId){
        Observable<Void> result = null;

        Performer<Void> performer = ConnectycubeUsers.deleteByExternalId(externalId);
        final Observable<Void> observable = performer.convertTo(RxJavaPerformProcessor.INSTANCE);

        result = observable.flatMap(new Func1<Void, Observable<Void>>() {
            @Override
            public Observable<Void> call(Void aVoid) {
                userCache.deleteUserByExternalId(externalId);
                return observable;
            }
        });

        return result;
    }


    public Observable<List<QMUser>> getUsers(PagedRequestBuilder requestBuilder) {
        Observable<List<QMUser>> result = null;
        Performer<ArrayList<ConnectycubeUser>> performer = ConnectycubeUsers.getUsers(requestBuilder);
        final Observable<List<ConnectycubeUser>> observable = performer.convertTo(RxJavaPerformProcessor.INSTANCE);

        result = observable.map(new Func1<List<ConnectycubeUser>,List<QMUser>>() {
            @Override
            public List<QMUser> call(List<ConnectycubeUser> connectycubeUsers) {
                List<QMUser> result = QMUser.convertList(connectycubeUsers);
                userCache.createOrUpdateAll(result);
                return result;
            }
        });
        return result;
    }


    public Observable<List<QMUser>> getUsersByIDs(final Collection<Integer> usersIds, final PagedRequestBuilder requestBuilder) {
        return  getUsersByIDs(usersIds, requestBuilder, true);
    }

    public  List<QMUser> getUsersByIDsSync(final Collection<Integer> usersIds, final PagedRequestBuilder requestBuilder) throws ResponseException {
        return getUsersByIDsSync(usersIds,requestBuilder, true);
    }

    public Observable<List<QMUser>> getUsersByIDs(final Collection<Integer> usersIds, final PagedRequestBuilder requestBuilder, boolean forceLoad){
        Observable<List<QMUser>> result = null;

        if (!forceLoad) {
            result = Observable.defer(new Func0<Observable<List<QMUser>>>() {
                @Override
                public Observable<List<QMUser>> call() {
                    List<QMUser> qmUsers = userCache.getUsersByIDs(usersIds);
                    return  qmUsers.size() == 0 ? getUsersByIDs(usersIds, requestBuilder, true) : Observable.just(qmUsers);
                }
            });
            return result;
        }

        Performer<ArrayList<ConnectycubeUser>> performer = ConnectycubeUsers.getUsersByIDs(usersIds, requestBuilder);
        final Observable<List<ConnectycubeUser>> observable = performer.convertTo(RxJavaPerformProcessor.INSTANCE);

        result = observable.map(new Func1<List<ConnectycubeUser>,List<QMUser>>() {
            @Override
            public List<QMUser> call(List<ConnectycubeUser> connectycubeUsers) {
                List<QMUser> result = QMUser.convertList(connectycubeUsers);
                userCache.createOrUpdateAll(result);
                return result;
            }
        });

        return result;
    }

    public  List<QMUser> getUsersByIDsSync(final Collection<Integer> usersIds, final PagedRequestBuilder requestBuilder, boolean forceLoad) throws ResponseException {
        List<QMUser> result = null;

        if (!forceLoad) {
            List<QMUser> qmUsers = userCache.getUsersByIDs(usersIds);
            return qmUsers.size() == 0 ? getUsersByIDsSync(usersIds, requestBuilder, true) : qmUsers;
        }
        result = QMUser.convertList(syncAddressBookContactsAndUsersByIds(usersIds, requestBuilder).toBlocking().first());
        userCache.createOrUpdateAll(result);
        return result;
    }

    private Observable<List<ConnectycubeUser>> syncAddressBookContactsAndUsersByIds(final Collection<Integer> usersIds, final PagedRequestBuilder requestBuilder) {
        final Observable<ArrayList<ConnectycubeAddressBookContact>> observableBook = getCurrentAddressBook();
        Performer<ArrayList<ConnectycubeUser>> performer = ConnectycubeUsers.getUsersByIDs(usersIds, requestBuilder);
        final Observable<ArrayList<ConnectycubeUser>> observableUsers = performer.convertTo(RxJavaPerformProcessor.INSTANCE);

        return Observable.zip(
                observableBook.subscribeOn(Schedulers.io()),
                observableUsers.subscribeOn(Schedulers.io()),
                new Func2<ArrayList<ConnectycubeAddressBookContact>, List<ConnectycubeUser>, List<ConnectycubeUser>>() {
                    @Override
                    public List<ConnectycubeUser> call(ArrayList<ConnectycubeAddressBookContact> contacts, List<ConnectycubeUser> users) {
                        //add names
                        for (ConnectycubeUser user : users) {
                            for (ConnectycubeAddressBookContact contact : contacts) {
                                if(user.getPhone().equals(contact.getPhone())) {
                                    user.setFullName(contact.getName());
                                }
                            }
                        }
                        return users;
                    }
                }
        );
    }

    private Observable<ConnectycubeUser> syncAddressBookContactsAndUserById(String column, String value) {
        final Observable<ArrayList<ConnectycubeAddressBookContact>> observableBook = getCurrentAddressBook();
        Performer<ConnectycubeUser> performer = getUserByColumnFromServer(column, value);
        final Observable<ConnectycubeUser> observableUser = performer.convertTo(RxJavaPerformProcessor.INSTANCE);
        return Observable.zip(
                observableBook.subscribeOn(Schedulers.io()),
                observableUser.subscribeOn(Schedulers.io()),
                new Func2<ArrayList<ConnectycubeAddressBookContact>, ConnectycubeUser, ConnectycubeUser>() {
                    @Override
                    public ConnectycubeUser call(ArrayList<ConnectycubeAddressBookContact> contacts, ConnectycubeUser user) {
                        //add names
                        for (ConnectycubeAddressBookContact contact : contacts) {
                            if (user.getPhone().equals(contact.getPhone())) {
                                user.setFullName(contact.getName());
                            }
                        }
                        return user;
                    }
                }
        );
    }

    private Observable<ArrayList<ConnectycubeAddressBookContact>> getCurrentAddressBook() {
        return addressBookCache.isEmpty() ? getAddressBookFromRest() : getAddressBookCache();
    }

    private Observable<ArrayList<ConnectycubeAddressBookContact>> getAddressBookCache() {
        return Observable.just(addressBookCache);
    }

    private Observable<ArrayList<ConnectycubeAddressBookContact>> getAddressBookFromRest() {
        Observable<ArrayList<ConnectycubeAddressBookContact>> observableBook = getAddressBook();
        return observableBook.map(new Func1<ArrayList<ConnectycubeAddressBookContact>, ArrayList<ConnectycubeAddressBookContact>>() {
            @Override
            public ArrayList<ConnectycubeAddressBookContact> call(ArrayList<ConnectycubeAddressBookContact> contacts) {
                addressBookCache.clear();
                addressBookCache.addAll(contacts);
                return addressBookCache;
            }
        });
    }

//    public Performer<ArrayList<ConnectycubeUser>> getUsersByIDsPerformer(final Collection<Integer> usersIds, final QBPagedRequestBuilder requestBuilder, final boolean forceLoad){
//        Performer<ArrayList<ConnectycubeUser>>  result = null;
//
//            result = new Performer<ArrayList<ConnectycubeUser>>() {
//
//                private boolean canceled;
//
//                @Override
//                public void performAsync(EntityCallback<ArrayList<ConnectycubeUser>> callback) {
//                    if(canceled){
//                        return;
//                    }
//                }
//
//                @Override
//                public ArrayList<ConnectycubeUser> perform() throws ResponseException {
//                    ArrayList<ConnectycubeUser> result = null;
//                    if (!forceLoad) {
//                        result = (ArrayList<ConnectycubeUser>)userCache.getUsersByIDs(usersIds);
//                        if(result.size()>0){
//                            return  result;
//                        }
//                    }
//                    result = ConnectycubeUsers.getUsersByIDs(usersIds, requestBuilder).perform();
//                    userCache.createOrUpdateAll(result);
//                    return result;
//                }
//
//                @Override
//                public <R> R convertTo(PerformProcessor<?> performProcessor) {
//                    return (R)performProcessor.process(this);
//                }
//
//                @Override
//                public boolean isCanceled() {
//                    return canceled;
//                }
//
//                @Override
//                public void cancel() {
//                    canceled = true;
//                }
//            };
//            return result;
//
//      }

    public List<QMUser> getUsersByFacebookIdSync(final Collection<String> usersFacebookIds, final PagedRequestBuilder requestBuilder, boolean forceLoad) throws ResponseException {
        return getUsersByColumnSync(QMUserColumns.FACEBOOK_ID, usersFacebookIds, requestBuilder, forceLoad);
    }

    public List<QMUser> getUsersByEmailsSync(final Collection<String> usersEmails, final PagedRequestBuilder requestBuilder, boolean forceLoad) throws ResponseException {
        return getUsersByColumnSync(QMUserColumns.EMAIL, usersEmails, requestBuilder, forceLoad);
    }


    public Observable<List<QMUser>>  getUsersByEmails(final Collection<String> usersEmails, final PagedRequestBuilder requestBuilder, boolean forceLoad){
        return getUsersByColumn(QMUserColumns.EMAIL, usersEmails, requestBuilder, forceLoad);
    }

    public Observable<List<QMUser>> getUsersByLogins(final Collection<String> usersLogins, final PagedRequestBuilder requestBuilder,  boolean forceLoad){
        return getUsersByColumn(QMUserColumns.LOGIN, usersLogins, requestBuilder, forceLoad);
    }

    public Observable<List<QMUser>> getUsersByFacebookId(final Collection<String> usersFacebookIds, final PagedRequestBuilder requestBuilder,  boolean forceLoad){
        return getUsersByColumn(QMUserColumns.FACEBOOK_ID, usersFacebookIds, requestBuilder, forceLoad);
    }

    public Observable<List<QMUser>> getUsersByTwitterIds(final Collection<String> usersTwitterIds, final PagedRequestBuilder requestBuilder,  boolean forceLoad){
        return getUsersByColumn(QMUserColumns.TWITTER_ID, usersTwitterIds, requestBuilder, forceLoad);
    }

    public Observable<List<QMUser>> getUsersByTwitterDigitsIds(final Collection<String> usersTwitterDigitsIds, final PagedRequestBuilder requestBuilder,  boolean forceLoad){
        return getUsersByColumn(QMUserColumns.TWITTER_DIGITS_ID, usersTwitterDigitsIds, requestBuilder, forceLoad);
    }

    public Observable<List<QMUser>> getUsersByFullName(final String fullName, final PagedRequestBuilder requestBuilder,  boolean forceLoad){
        return getUsersByColumn(QMUserColumns.FULL_NAME, fullName, requestBuilder, forceLoad);
    }

    public Observable<List<QMUser>>  getUsersByTags(final Collection<String> tags, final PagedRequestBuilder requestBuilder,  boolean forceLoad){
        return getUsersByColumn(QMUserColumns.TAGS, tags, requestBuilder, forceLoad);
    }

    public Observable<List<QMUser>>  getUsersByPhoneNumbers(final Collection<String> usersPhoneNumbers, final PagedRequestBuilder requestBuilder,  boolean forceLoad){
        return getUsersByColumn(QMUserColumns.PHONE, usersPhoneNumbers, requestBuilder, forceLoad);
    }

    public Observable<List<QMUser>> getUsersByFilter(final Collection<?> filterValue, final String filter, final PagedRequestBuilder requestBuilder,  boolean forceLoad) {
        Observable<List<QMUser>> result = null;

        if (!forceLoad) {
            result = Observable.defer(new Func0<Observable<List<QMUser>>>() {
                @Override
                public Observable<List<QMUser>> call() {
                    List<QMUser> qmUsers = userCache.getUsersByFilter(filterValue, filter);
                    return qmUsers.size() == 0 ? getUsersByFilter(filterValue, filter, requestBuilder, true) : Observable.just(qmUsers);
                }
            });
            return result;
        }

        Performer<ArrayList<ConnectycubeUser>> performer = ConnectycubeUsers.getUsersByFilter(filterValue, filter, requestBuilder);
        final Observable<List<ConnectycubeUser>> observable = performer.convertTo(RxJavaPerformProcessor.INSTANCE);

        result = observable.map(new Func1<List<ConnectycubeUser>,List<QMUser>>() {
            @Override
            public List<QMUser> call(List<ConnectycubeUser> connectycubeUsers) {
                List<QMUser> result = QMUser.convertList(connectycubeUsers);
                userCache.createOrUpdateAll(result);
                return result;
            }
        });

        return result;
    }

    public Observable<ConnectycubeAddressBookResponse> uploadAddressBook(ArrayList<ConnectycubeAddressBookContact> localBook) {
        return uploadAddressBook(localBook, null);
    }

    public Observable<ConnectycubeAddressBookResponse> uploadAddressBook(ArrayList<ConnectycubeAddressBookContact> localBook, String UDID) {
        Performer<ConnectycubeAddressBookResponse> performer = ConnectycubeUsers.uploadAddressBook(localBook, UDID, false);
        return performer.convertTo(RxJavaPerformProcessor.INSTANCE);
    }

    public Observable<ConnectycubeAddressBookResponse> uploadAddressBookForce(ArrayList<ConnectycubeAddressBookContact> localBook) {
        return uploadAddressBookForce(localBook, null);
    }

    public Observable<ConnectycubeAddressBookResponse> uploadAddressBookForce(ArrayList<ConnectycubeAddressBookContact> localBook, String UDID) {
        Performer<ConnectycubeAddressBookResponse> performer = ConnectycubeUsers.uploadAddressBook(localBook, UDID, true);
        return performer.convertTo(RxJavaPerformProcessor.INSTANCE);
    }

    public Observable<ArrayList<ConnectycubeAddressBookContact>> getAddressBook() {
        return getAddressBook(null);
    }

    public Observable<ArrayList<ConnectycubeAddressBookContact>> getAddressBook(String UDID) {
        Performer<ArrayList<ConnectycubeAddressBookContact>> performer = ConnectycubeUsers.getAddressBook(UDID);
        return performer.convertTo(RxJavaPerformProcessor.INSTANCE);
    }


    public Observable<ArrayList<ConnectycubeUser>> getRegisteredContacts() {
        Performer<ArrayList<ConnectycubeUser>> performer = ConnectycubeUsers.getRegisteredUsersFromAddressBook(null, false);
        return performer.convertTo(RxJavaPerformProcessor.INSTANCE);
    }

    public Observable<ArrayList<ConnectycubeUser>> getRegisteredContacts(String UDID, boolean compact) {
        Performer<ArrayList<ConnectycubeUser>> performer = ConnectycubeUsers.getRegisteredUsersFromAddressBook(UDID, compact);
        return performer.convertTo(RxJavaPerformProcessor.INSTANCE);
    }


    private Observable<QMUser> getUserByColumn(final String column, final String value,  boolean forceLoad){
        Observable<QMUser> result = null;

        if (!forceLoad) {
            result = Observable.defer(new Func0<Observable<QMUser>>() {
                @Override
                public Observable<QMUser> call() {
                    QMUser qmUser = userCache.getUserByColumn(column, value);
                    return  qmUser == null ? getUserByColumn(column, value, true) :  Observable.just(qmUser);
                }
            });
            return result;
        }

        Performer<ConnectycubeUser> performer = getUserByColumnFromServer(column,value);
        final Observable<ConnectycubeUser> observable = performer.convertTo(RxJavaPerformProcessor.INSTANCE);

        result = observable.map(new Func1<ConnectycubeUser,QMUser>() {
            @Override
            public QMUser call(ConnectycubeUser connectycubeUser) {
                //TODO VT temp code before implement feature "last user activity" in SDK
                ConnectycubeUser userWithActualLastActivity = getUserWithLatestLastActivity(connectycubeUser);
                QMUser result = QMUser.convert(userWithActualLastActivity);
                userCache.createOrUpdate(result);
                return result;
            }
        });

        return result;
    }

    private QMUser getUserByColumnSync(final String column, final String value, boolean forceLoad) throws ResponseException {
        QMUser result = null;

        if (!forceLoad) {
           QMUser user = userCache.getUserByColumn(column, value);
           return  user == null ? getUserByColumnSync(column, value, true) : user;
        }

        ConnectycubeUser loadedUser = QMUser.convert(syncAddressBookContactsAndUserById(column, value).toBlocking().first());

        //TODO VT temp code before implement feature "last user activity" in SDK
        ConnectycubeUser userWithActualLastActivity = getUserWithLatestLastActivity(loadedUser);

        result = QMUser.convert(userWithActualLastActivity);
        userCache.createOrUpdate(result);
        return result;
    }

    private ConnectycubeUser getUserWithLatestLastActivity(ConnectycubeUser loadedUser) {
        if (loadedUser != null && loadedUser.getLastRequestAt() != null){
            QMUser tempQmUser = userCache.getUserByColumn(QMUserColumns.ID, String.valueOf(loadedUser.getId()));
            if (tempQmUser != null) {
                ConnectycubeUser existUser = QMUser.convert(tempQmUser);
                if (existUser.getLastRequestAt() != null) {
                    if (existUser.getLastRequestAt().after(loadedUser.getLastRequestAt())) {
                        //sets for loaded user last activity saved before from roster listener
                        loadedUser.setLastRequestAt(existUser.getLastRequestAt());
                    }
                }
            }
        }

        return loadedUser;
    }

    private Observable<List<QMUser>> getUsersByColumn(final String column, final String value, final PagedRequestBuilder requestBuilder,  boolean forceLoad){
        Observable<List<QMUser>> result = null;

        if (!forceLoad) {
            result = Observable.defer(new Func0<Observable<List<QMUser>>>() {
                @Override
                public Observable<List<QMUser>> call() {
                    List<QMUser> qmUsers =  userCache.getByColumn(column, value);
                    return  qmUsers.size() == 0 ? getUsersByColumn(column, value, requestBuilder, true) : Observable.just(qmUsers);
                }
            });
            return result;
        }

        Performer<ArrayList<ConnectycubeUser>> performer = getUsersByColumnFromServer(column, value,  requestBuilder);
        final Observable<List<ConnectycubeUser>> observable = performer.convertTo(RxJavaPerformProcessor.INSTANCE);

        result = observable.map(new Func1<List<ConnectycubeUser>,List<QMUser>>() {
            @Override
            public List<QMUser> call(List<ConnectycubeUser> connectycubeUsers) {
                List<QMUser> result = QMUser.convertList(connectycubeUsers);
                userCache.createOrUpdateAll(result);
                return result;
            }
        });
        return result;
    }

    public Observable<List<QMUser>>  getUsersByColumn(final String column, final Collection<String> values, final PagedRequestBuilder requestBuilder,  boolean forceLoad){
        Observable<List<QMUser>> result = null;

        if (!forceLoad) {
            result = Observable.defer(new Func0<Observable<List<QMUser>>>() {
                @Override
                public Observable<List<QMUser>> call() {
                    List<QMUser> qmUsers = userCache.getByColumn(column, values);
                    return  qmUsers.size() == 0 ? getUsersByColumn(column, values, requestBuilder, true): Observable.just(qmUsers);
                }
            });
            return result;
        }

        Performer<ArrayList<ConnectycubeUser>> performer = getUsersByColumnFromServer(column, values, requestBuilder);
        final Observable<List<ConnectycubeUser>> observable = performer.convertTo(RxJavaPerformProcessor.INSTANCE);

        result = observable.map(new Func1<List<ConnectycubeUser>,List<QMUser>>() {
            @Override
            public List<QMUser> call(List<ConnectycubeUser> connectycubeUsers) {
                List<QMUser> result = QMUser.convertList(connectycubeUsers);
                userCache.createOrUpdateAll(result);
                return result;
            }
        });

        return result;
    }


    public List<QMUser> getUsersByColumnSync(final String column, final Collection<String> values, final PagedRequestBuilder requestBuilder, boolean forceLoad) throws ResponseException {
        List<QMUser> result = null;

        if (!forceLoad) {
                    List<QMUser> qmUsers = userCache.getByColumn(column, values);
                    return  qmUsers.size() == 0 ? getUsersByColumnSync(column, values, requestBuilder, true):qmUsers;
        }

        result = QMUser.convertList(getUsersByColumnFromServer(column, values, requestBuilder).perform());
        userCache.createOrUpdateAll(result);

        return result;
    }


    private Performer<ConnectycubeUser> getUserByColumnFromServer(String column, String value){
        Performer<ConnectycubeUser> result = null;
        switch (column){
            case QMUserColumns.ID:
                result = ConnectycubeUsers.getUser(Integer.parseInt(value));
                break;
            case QMUserColumns.FULL_NAME:
                result = null;
                break;
            case QMUserColumns.EMAIL:
                result = ConnectycubeUsers.getUserByEmail(value);
                break;
            case QMUserColumns.LOGIN:
                result = ConnectycubeUsers.getUserByLogin(value);
                break;
            case QMUserColumns.PHONE:
                result = null;
                break;
            case QMUserColumns.WEBSITE:
                result = null;
                break;
            case QMUserColumns.LAST_REQUEST_AT:
                result = null;
                break;
            case QMUserColumns.EXTERNAL_ID:
                result = ConnectycubeUsers.getUserByExternalId(value);
                break;
            case QMUserColumns.FACEBOOK_ID:
                result = ConnectycubeUsers.getUserByFacebookId(value);
                break;
            case QMUserColumns.TWITTER_ID:
                result = ConnectycubeUsers.getUserByTwitterId(value);
                break;
            case QMUserColumns.TWITTER_DIGITS_ID:
                result = ConnectycubeUsers.getUserByTwitterDigitsId(value);
                break;
            case QMUserColumns.BLOB_ID:
                result = null;
                break;
            case QMUserColumns.TAGS:
                result = null;
                break;
            case QMUserColumns.PASSWORD:
                result = null;
                break;
            case QMUserColumns.OLD_PASSWORD:
                result = null;
                break;
            case QMUserColumns.CUSTOM_DATE:
                result = null;
                break;
        }
        return result;
    }


    private Performer<ArrayList<ConnectycubeUser>> getUsersByColumnFromServer(String column, String value, PagedRequestBuilder  requestBuilder){
        Performer<ArrayList<ConnectycubeUser>> result = null;
        switch (column){
            case QMUserColumns.ID:
                result = null;
                break;
            case QMUserColumns.FULL_NAME:
                result = ConnectycubeUsers.getUsersByFullName(value, requestBuilder);
                break;
            case QMUserColumns.EMAIL:
                result = null;
                break;
            case QMUserColumns.LOGIN:
                result = null;
                break;
            case QMUserColumns.PHONE:
                result = null;
                break;
            case QMUserColumns.WEBSITE:
                result = null;
                break;
            case QMUserColumns.LAST_REQUEST_AT:
                result = null;
                break;
            case QMUserColumns.EXTERNAL_ID:
                result =null;
                break;
            case QMUserColumns.FACEBOOK_ID:
                result = null;
                break;
            case QMUserColumns.TWITTER_ID:
                result = null;
                break;
            case QMUserColumns.TWITTER_DIGITS_ID:
                result = null;
                break;
            case QMUserColumns.BLOB_ID:
                result = null;
                break;
            case QMUserColumns.TAGS:
                result = null;
                break;
            case QMUserColumns.PASSWORD:
                result = null;
                break;
            case QMUserColumns.OLD_PASSWORD:
                result = null;
                break;
            case QMUserColumns.CUSTOM_DATE:
                result = null;
                break;
        }
        return result;
    }

    private Performer<ArrayList<ConnectycubeUser>> getUsersByColumnFromServer(String column, Collection<String> values, PagedRequestBuilder  requestBuilder){
        Performer<ArrayList<ConnectycubeUser>> result = null;
        switch (column){
            case QMUserColumns.ID:
                result = null;
                break;
            case QMUserColumns.FULL_NAME:
                result = null;
                break;
            case QMUserColumns.EMAIL:
                result = ConnectycubeUsers.getUsersByEmails(values, requestBuilder);
                break;
            case QMUserColumns.LOGIN:
                result = ConnectycubeUsers.getUsersByLogins(values, requestBuilder);
                break;
            case QMUserColumns.PHONE:
                result = ConnectycubeUsers.getUsersByPhoneNumbers(values, requestBuilder);
                break;
            case QMUserColumns.WEBSITE:
                result = null;
                break;
            case QMUserColumns.LAST_REQUEST_AT:
                result = null;
                break;
            case QMUserColumns.EXTERNAL_ID:
                result = null;
                break;
            case QMUserColumns.FACEBOOK_ID:
                result = ConnectycubeUsers.getUsersByFacebookId(values, requestBuilder);
                break;
            case QMUserColumns.TWITTER_ID:
                result = ConnectycubeUsers.getUsersByTwitterId(values, requestBuilder);
                break;
            case QMUserColumns.TWITTER_DIGITS_ID:
                result = ConnectycubeUsers.getUsersByTwitterDigitsId(values, requestBuilder);
                break;
            case QMUserColumns.BLOB_ID:
                result = null;
                break;
            case QMUserColumns.TAGS:
                result = null;
                break;
            case QMUserColumns.PASSWORD:
                result = null;
                break;
            case QMUserColumns.OLD_PASSWORD:
                result = null;
                break;
            case QMUserColumns.CUSTOM_DATE:
                result = null;
                break;
        }
        return result;
    }


    private  ConnectycubeUser getUserByEmailFromCache(String email) {
        return userCache.getUserByColumn(QMUserColumns.EMAIL, email);
    }

    private ConnectycubeUser getUserByLoginFromCache(String login) {
        return userCache.getUserByColumn(QMUserColumns.LOGIN, login);
    }

    private ConnectycubeUser getUserByFacebookIdFromCache(String facebookId) {
        return userCache.getUserByColumn(QMUserColumns.FACEBOOK_ID, facebookId);
    }

    private ConnectycubeUser getUserByTwitterIdFromCache(String twitterId) {
        return userCache.getUserByColumn(QMUserColumns.TWITTER_ID, twitterId);
    }

    private ConnectycubeUser getUserByTwitterDigitsIdFromCache(String twitterDigitsId) {
        return userCache.getUserByColumn(QMUserColumns.TWITTER_DIGITS_ID, twitterDigitsId);
    }

    private ConnectycubeUser getUserByExternalIDFromCache(String externalId) {
        return userCache.getUserByColumn(QMUserColumns.EXTERNAL_ID, externalId);
    }

    private ConnectycubeUser getUserByFullNameFromCache(String fullName) {
        return userCache.getUserByColumn(QMUserColumns.FULL_NAME, fullName);
    }

    private ConnectycubeUser getUserByTagFromCache(String tag) {
        return userCache.getUserByColumn(QMUserColumns.TAGS, tag);
    }

    private ConnectycubeUser getUserByPhoneNumberFromCache(String phoneNumber) {
        return null;
    }

    private List<QMUser> getUsersByEmailsFromCache(Collection<String> usersEmails) {
        return userCache.getByColumn(QMUserColumns.EMAIL, usersEmails);
    }

    private List<QMUser> getUsersByLoginsFromCache(Collection<String> usersLogins) {
        return userCache.getByColumn(QMUserColumns.LOGIN, usersLogins);
    }

    private List<QMUser> getUsersByFacebookIdsFromCache(Collection<String> usersFacebookIds) {
        return userCache.getByColumn(QMUserColumns.FACEBOOK_ID, usersFacebookIds);
    }

    private List<QMUser> getUsersByTwitterIdsFromCache(Collection<String> usersTwitterIds) {
        return userCache.getByColumn(QMUserColumns.TWITTER_ID, usersTwitterIds);
    }

    private List<QMUser> getUsersByTwitterDigitsIdsFromCache(Collection<String> usersTwitterDigitsIds) {
        return userCache.getByColumn(QMUserColumns.TWITTER_DIGITS_ID, usersTwitterDigitsIds);
    }

    private List<QMUser> getUsersByExternalIdsFromCache(Collection<String> usersExternalIds) {
        return userCache.getByColumn(QMUserColumns.EXTERNAL_ID, usersExternalIds);
    }

    private List<QMUser> getUsersByFullNameFromCache(String fullName) {
        return userCache.getByColumn(QMUserColumns.FULL_NAME, fullName);
    }

    private List<QMUser> getUsersByTagsFromCache(Collection<String> tags) {
        return userCache.getByColumn(QMUserColumns.TAGS, tags);
    }

    private List<QMUser> getUsersByPhoneNumbersFromCache(Collection<String> usersPhoneNumbers) {
        return userCache.getByColumn(QMUserColumns.PHONE, usersPhoneNumbers);
    }

}
