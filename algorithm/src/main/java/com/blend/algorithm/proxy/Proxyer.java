package com.blend.algorithm.proxy;

class Proxyer implements Tools, ToolsA {

    private Tools mTools;

    public Proxyer(Tools tools) {
        mTools = tools;
    }

    @Override
    public String saleTools(int number) {
        System.out.println("START");
        mTools.saleTools(number);
        System.out.println("END");
        return "static sell out";
    }

    @Override
    public void saleToolsA(int number) {
        System.out.println("This is good tools:");
        mTools.saleTools(number);
    }
}
