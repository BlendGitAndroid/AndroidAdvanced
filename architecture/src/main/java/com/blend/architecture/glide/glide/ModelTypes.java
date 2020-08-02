package com.blend.architecture.glide.glide;

import java.io.File;

interface ModelTypes<T> {
    T load(String string);

    T load(File file);

}
