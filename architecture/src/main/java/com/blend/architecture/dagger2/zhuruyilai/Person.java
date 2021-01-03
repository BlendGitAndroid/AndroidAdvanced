package com.blend.architecture.dagger2.zhuruyilai;

class Person {

    private Bike mBike;
    // private Car mCar;

    public Person() {
        mBike = new Bike();
        // mCar = new Car();
    }

    public void goOut() {
        mBike.drive();
        // mCar.drive();
    }

}
