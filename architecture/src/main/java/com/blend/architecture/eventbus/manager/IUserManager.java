package com.blend.architecture.eventbus.manager;

import com.blend.architecture.eventbus.annotion.ClassId;
import com.blend.architecture.eventbus.model.Friend;

//接口的方式  描述 一个类
@ClassId("com.blend.architecture.eventbus.manager.UserManager")
public interface IUserManager {

    public Friend getFriend();

    public void setFriend(Friend friend);
}
