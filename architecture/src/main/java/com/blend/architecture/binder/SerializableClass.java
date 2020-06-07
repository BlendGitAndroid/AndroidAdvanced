package com.blend.architecture.binder;

import java.io.Serializable;


/**
 * Serializable接口：Java提供的一个序列化接口，是一个空接口，为对象提供标准的序列化和发序列化。
 *
 * 静态成员变量不属于类，不会参与序列化；transient关键字标记的成员变量不参与序列化的过程。
 */
public class SerializableClass implements Serializable {

    /*
    serialVersionUID是用来辅助序列化和反序列的过程的，原则上序列化后的数据中的serialVersionUID只有和当前类的serialVersionUID
    相同才能够正常地被反序列化。
    工作机制：序列化的时候会把当前类的serialVersionUID写入序列化的文件中，反序列化的时候会去检查文件中的serialVersionUID是否和当前类的版本
    相同，若是不同，表示成员变量的数量、类型等发生了变化，无法进行反序列化，报错。
    一般来说，应该手动指定serialVersionUID的值。
     */
    private static final long serialVersionUID = 55L;

}
