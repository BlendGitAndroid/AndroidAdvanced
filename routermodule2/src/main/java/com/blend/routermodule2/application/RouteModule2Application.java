package com.blend.routermodule2.application;

import android.app.Application;

import com.blend.routercore.BlendRouter;

public class RouteModule2Application extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        BlendRouter.init(this);
    }
}
