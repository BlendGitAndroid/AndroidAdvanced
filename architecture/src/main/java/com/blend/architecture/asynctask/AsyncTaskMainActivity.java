package com.blend.architecture.asynctask;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.blend.architecture.R;

/**
 * AsyncTask原理分析：
 * 1.构造函数。mWorker代表了AsyncTask要执行的任务，是对Callable接口的封装，意味着这个任务是有返回值的。mFuture代表了AsyncTask
 * 要执行的任务的返回结果，其实就是个FutureTask，按照FutureTask标准用法，mWorker作为Callable被传给了mFuture，那么mFuture的结果
 * 就从mWorker执行的任务中取得。仔细看mWorker，return语句返回的结果就是我们前面所说的doInBackground()的执行结果。
 * 2.执行。每次调用execute，就创建一个Runnable匿名内部类对象，这个对象存入mTasks，在匿名内部类的run函数里面调用传入参数r.run()。
 * 然后通过一个scheduleNext函数把mTasks里面的所有对象通过THREAD_POOL_EXECUTOR.execute(mActive)执行一遍。说穿了，也就是说
 * SerialExecutor类会把所有的任务丢入一个容器，之后把容器里面的所有对象一个一个的排队（串行化）执行THREAD_POOL_EXECUTOR.execute(mActive);
 * 3.结果。当子线程需要和UI线程进行通信时，其实就是通过handler，往UI线程发送消息。
 * <p>
 * 总结：
 * 1）线程池的创建：
 * 在创建了AsyncTask的时候，会默认创建两个线程池SerialExecutor和ThreadPoolExecutor，SerialExecutor负责将任务串行化，ThreadPoolExecutor
 * 是真正执行任务的地方，且无论有多少个AsyncTask实例，两个线程池都会只有一份。
 * 2）任务的执行：
 * 在execute中，会执行run方法，当执行完run方法后，会调用scheduleNext()不断的从双端队列中轮询，获取下一个任务并继续放到一个子线程中执行，直到异步任
 * 务执行完毕。
 * 3）消息的处理：
 * 在执行完onPreExecute()方法之后，执行了doInBackground()方法，然后就不断的发送请求获取数据；在这个AsyncTask中维护了一个InternalHandler的类，
 * 这个类是继承Handler的，获取的数据是通过handler进行处理和发送的。在其handleMessage方法中，将消息传递给onProgressUpdate()进行进度的更新，
 * 也就可以将结果发送到主线程中，进行界面的更新了。
 * 4）使用AsyncTask的注意点
 * 通过观察代码我们可以发现，每一个new出的AsyncTask只能执行一次execute()方法，多次运行将会报错，如需多次，需要新new一个AsyncTask。
 * <p>
 * AsyncTask优缺点
 * 优点：AsyncTask是一个轻量级的异步任务处理类，轻量级体现在，使用方便、代码简洁上，而且整个异步任务的过程可以通过cancel()进行控制；
 * 缺点：不适用于处理长时间的异步任务，一般这个异步任务的过程最好控制在几秒以内，如果是长时间的异步任务就需要考虑多线程的控制问题；当处理多个异步任务时，UI更新变得困难。
 * Handler:
 * 优点：代码结构清晰，容易处理多个异步任务；
 * 缺点：当有多个异步任务时，由于要配合Thread或Runnable，代码可能会稍显冗余。
 * 总之，AsyncTask不失为一个非常好用的异步任务处理类，只要不是频繁对大量UI进行更新，可以考虑使用；而Handler在处理大量UI更新时可以考虑使用。
 */
public class AsyncTaskMainActivity extends AppCompatActivity {

    @Override
    @SuppressLint("StaticFieldLeak")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_task_main);
        AsyncTask<String, Integer, Boolean> asyncTask = new AsyncTask<String, Integer, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(String... strings) {
                if (strings[0].startsWith("Blend")) {
                    return true;
                }
                publishProgress(1);
                return false;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
            }
        };
        asyncTask.execute("Blend");
    }
}