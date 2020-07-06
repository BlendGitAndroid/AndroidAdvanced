// MyEventBusService.aidl
package com.blend.architecture;

// Declare any non-default types here with import statements

import com.blend.architecture.Request;
import com.blend.architecture.Responce;

interface MyEventBusService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    Responce send(in Request request);
}
