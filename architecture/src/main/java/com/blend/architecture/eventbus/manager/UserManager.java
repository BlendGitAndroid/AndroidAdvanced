package com.blend.architecture.eventbus.manager;

import com.blend.architecture.eventbus.annotion.ClassId;
import com.blend.architecture.eventbus.model.Friend;

@ClassId("com.blend.architecture.eventbus.manager.UserManager")
public class UserManager implements IUserManager {
    Friend friend;
    private static UserManager sInstance = null;

    private UserManager() {

    }

    //约定这个进程A  单例对象的     规则    getInstance()
    public static synchronized UserManager getInstance() {
        if (sInstance == null) {
            sInstance = new UserManager();
        }
        return sInstance;
    }

    public Friend getFriend() {
        return friend;
    }

    public void setFriend(Friend friend) {
        this.friend = friend;
    }
}
