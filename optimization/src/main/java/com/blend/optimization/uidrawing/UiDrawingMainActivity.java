package com.blend.optimization.uidrawing;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.blend.optimization.R;

/**
 * CPU与GPU的工作流程：
 * CPU的任务繁多，做逻辑计算外，还要做内存管理、显示操作，因此在实际运算的时候性能会大打折扣，在没有GPU的时代，
 * 不能显示复杂的图形，其运算速度远跟不上今天复杂三维游戏的要求。即使CPU的工作频率超过2GHz或更高，对它绘制图形提高也不大。这时GPU的
 * 设计就出来了。CPU将UI对象处理为多维图像纹理，GPU再对图像进行光栅化，最后显示出来。
 * 光栅化：将CPU计算完的向量图形格式表示的图像转换成GPU能显示的位图以用于显示器。
 * <p>
 * 卡顿分析：
 * Android系统每隔16ms发出VSYNC信号(1000ms/60=16.66ms)，触发对UI 进行渲染，如果每次渲染都成功这样就能够达到流畅的画面所需要的
 * 60fps，为了能够实现60fps，这意味着计算渲染的大多数操作都必须在16ms内完成。但是当画面渲染超过16ms时，垂直同步机制会让显示器等待
 * GPU完成栅格化渲染操作，这样会让这一帧动画多停留16ms，甚至更多，这样就造成了用户看起来画面停顿。
 * 16毫秒的时间主要被两件事情所占用：
 * 第一件：将UI对象转换为一系列多边形和纹理。
 * 第二件：CPU传递处理数据到GPU。
 * 所以很明显，要缩短这两部分的时间，也就是说需要尽量减少对象转换的次数，以及上传数据的次数。
 * 在Android手机上表现出来就是：CPU减少xml转换成对象的时间和GPU减少重复绘制的时间。
 * <p>
 * 过度绘制：
 * GPU在绘制的过程中，一层一层的渲染，16ms渲染一次，这样就会造成图像覆盖，即无用的图像还被绘制在底层，造成不必要的浪费。
 * 1.自定义控件，onDraw方法做了过多的重复绘制。
 * 2.布局层次太深，重叠性太强。用户看不到的区域GPU也会渲染，导致耗时增加。
 * 过度绘制工具：在手机端的开发者选项中，打开GPU过程绘制工具，根据颜色的不同，来区分不同的绘制情况。
 * <p>
 * UI优化方案：
 * 1.减少背景重复。去掉所有activity主题设置中的属性直接在styles.xml中设置<item name="android:windowBackground">@null</item>，
 * 然后在需要的地方增加背景；还有再是非业务需要，不要去设置背景。
 * 2.使用裁减减少控件之间的重合部分。
 * 3.Android7.0之后系统做出的优化，invalidate()不再执行测量和布局动作。只要位置和大小没有改变，就不需要执行测量和布局。
 * 注意的地方：
 * 1.能在一个平面显示的内容，尽量只用一个容器。
 * 2.尽可能把相同的容器合并merge。
 * 3.能复用的代码，用include处理，可以减少GPU重复工作。
 * <p>
 * 常用的工具：
 * 1.Android/sdk/tools/bin/ui automator viewer.bat
 * 2.Android\sdk\tools\monitor.bat
 * 3.Device Monitor窗口中Hierarchy view。三个点也是代表着View的Measure, Layout和Draw。
 * 绿: 表示该View的此项性能比该View Tree中超过50%的View都要快；
 * 例如,代表Measure的是绿点,意味着这个视图的测量时间快于树中的视图对象的50%。
 * 黄: 表示该View的此项性能比该View Tree中超过50%的View都要慢；
 * 红: 表示该View的此项性能是View Tree中最慢的。
 */
public class UiDrawingMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_drawing_main);
    }
}