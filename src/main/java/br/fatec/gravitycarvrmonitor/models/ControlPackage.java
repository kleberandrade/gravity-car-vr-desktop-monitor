package br.fatec.gravitycarvrmonitor.models;

public class ControlPackage {

    private int mLeftVibrationMotor;
    private int mRightVibrationMotor;
    private int mFanSpeed;

    public ControlPackage() {
    }
    
    public ControlPackage(int mLeftVibrationMotor, int mRightVibrationMotor, int mFanSpeed) {
        this.mLeftVibrationMotor = mLeftVibrationMotor;
        this.mRightVibrationMotor = mRightVibrationMotor;
        this.mFanSpeed=mFanSpeed;
    }

    public int getLeftVibrationMotor() {
        return mLeftVibrationMotor;
    }

    public void setLeftVibrationMotor(int mLeftVibrationMotor) {
        this.mLeftVibrationMotor = mLeftVibrationMotor;
    }

    public int getRightVibrationMotor() {
        return mRightVibrationMotor;
    }

    public void setRightVibrationMotor(int mRightVibrationMotor) {
        this.mRightVibrationMotor = mRightVibrationMotor;
    }

    @Override
    public String toString() {
        return "ControlPackage{" + "mLeftVibrationMotor=" + mLeftVibrationMotor + ", mRightVibrationMotor=" + mRightVibrationMotor + ", mFanSpeed=" + getFanSpeed()+'}';
    }

    public int getFanSpeed() {
        return mFanSpeed;
    }

    public void setmFanSpeed(int mFanSpeed) {
        this.mFanSpeed = mFanSpeed;
    }
}
