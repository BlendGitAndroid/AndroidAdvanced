package com.blend.algorithm.gc;

class Allocation {

    private static class BigAllocation {
        private static final int _1MB = 1024 * 1024; //1M的大小

        // * 大对象直接进入老年代
        public static void main(String[] args) {
            byte[] allocation1, allocation2, allocation3;
            allocation1 = new byte[2 * _1MB]; //根据信息可以知道 2M的数组大约占据 3M的空间
            allocation2 = new byte[3 * _1MB];//大对象直接进入老年代（3M的数组大约占据 3M的空间）
        }
    }

    public static class EdenAllocation {
        private static final int _1MB = 1024 * 1024; //1M的大小

        // * 对象优先在Eden分配
        public static void main(String[] args) {
            byte[] allocation1, allocation2, allocation3, allocation4;
            allocation1 = new byte[1 * _1MB]; //根据信息可以知道 1M的数组大约占据 1.5M的空间(对象头，对象数据、填充)
            allocation2 = new byte[1 * _1MB];
            allocation3 = new byte[1 * _1MB];
            allocation4 = new byte[1 * _1MB];
        }

    }

}
