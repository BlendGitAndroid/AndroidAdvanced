package com.blend.routercore.template;


import com.blend.routerannotation.model.RouteMeta;

import java.util.Map;

public interface IRouteGroup {

    void loadInto(Map<String, RouteMeta> atlas);
}
