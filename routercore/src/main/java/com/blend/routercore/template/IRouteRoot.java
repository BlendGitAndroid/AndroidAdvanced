package com.blend.routercore.template;

import java.util.Map;

// 路由分组表（根据包名）
public interface IRouteRoot {

    /**
     * @param routes input
     */
    void loadInto(Map<String, Class<? extends IRouteGroup>> routes);
}
