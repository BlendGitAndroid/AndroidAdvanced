package com.blend.algorithm.proxy;

class Factory implements Tools {

    @Override
    public String saleTools(int number) {
        System.out.println("Customer needs " + number + " tools");
        return "sell out";
    }
}
