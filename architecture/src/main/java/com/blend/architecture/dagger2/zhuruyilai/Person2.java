package com.blend.architecture.dagger2.zhuruyilai;

class Person2  implements DepedencySetter{

    private Drivable mDrivable;

    public Person2(){
        mDrivable = new Bike();
    }

    public Person2(Drivable drivable){
        mDrivable = drivable;
    }

    public void setDrivable(Drivable drivable){
        mDrivable = drivable;
    }

    public void goOut() {
        mDrivable.drive();
        // mCar.drive();
    }

    @Override
    public void set(Drivable drivable) {

    }
}
