package com.blend.architecture.glide.glide.cache;




import com.blend.architecture.glide.glide.recycle.Resource;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ActiveResources {

    private ReferenceQueue<Resource> resourceReferenceQueue;
    private volatile boolean isShutdown;
    private Resource.ResourceListener resourceListener;
    /**
     * 使用弱引用记录 正在使用的资源
     * 通过 弱引用队列获得消息 这时候可以将其从正在使用集合移除
     */
    private Map<Key, ResourceWeakReference> activeResources = new HashMap<>();
    private Thread cleanReferenceQueueThread;

    public ActiveResources(Resource.ResourceListener resourceListener) {
        this.resourceListener = resourceListener;
    }

    public void activate(Key key, Resource resource) {
        resource.setResourceListener(key, resourceListener);
        activeResources.put(key, new ResourceWeakReference(key, resource, getReferenceQueue()));
    }

    public void deactivate(Key key) {
        //相同的key 替换了新的value 返回旧的value
        ResourceWeakReference remove = activeResources.remove(key);
        if (null != remove) {
            remove.clear();
        }
    }

    public Resource get(Key key) {
        ResourceWeakReference activeRef = activeResources.get(key);
        if (activeRef == null) {
            return null;
        }
        Resource resource = activeRef.get();
        return resource;
    }


    private ReferenceQueue<Resource> getReferenceQueue() {
        if (resourceReferenceQueue == null) {
            resourceReferenceQueue = new ReferenceQueue<>();
            cleanReferenceQueueThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!isShutdown) {
                        try {
                            //弱引用被回收 删除对应的key
                            ResourceWeakReference ref = (ResourceWeakReference)
                                    resourceReferenceQueue.remove();
                            activeResources.remove(ref.key);
                        } catch (InterruptedException e) {

                        }
                    }
                }
            }, "glide-active-resources");
            cleanReferenceQueueThread.start();
        }
        return resourceReferenceQueue;
    }

    void shutdown() {
        isShutdown = true;
        if (cleanReferenceQueueThread == null) {
            return;
        }
        cleanReferenceQueueThread.interrupt();
        try {
            cleanReferenceQueueThread.join(TimeUnit.SECONDS.toMillis(5));
            if (cleanReferenceQueueThread.isAlive()) {
                throw new RuntimeException("Failed to join in time");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    static final class ResourceWeakReference extends WeakReference<Resource> {

        private Key key;

        public ResourceWeakReference(Key key, Resource resource, ReferenceQueue<? super
                Resource> q) {
            super(resource, q);
            this.key = key;
        }

    }

}
