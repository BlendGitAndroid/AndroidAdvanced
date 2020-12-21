package com.blend.algorithm.proxy;

class FactoryA implements ToolsA {
    @Override
    public void saleToolsA(int number) {
        System.out.println("we have " + number + "good tools");
    }
}
