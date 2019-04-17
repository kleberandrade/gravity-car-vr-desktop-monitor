package br.fatec.gravitycarvrmonitor.utils;

public class StatisticsTracker {
    
    private int mCount;
    private double mTotal;
    private double mMinimum;
    private double mMaximum;
    
    public void clear(){
        mCount = 0;
        mTotal = 0;
    }

    public void addNumber(double number) {
        mCount++;
        mTotal += number;
        adjustMinimumAndMaximum(number);
    }
   
    private void adjustMinimumAndMaximum(double number) {
        if (containsSingleNumber()) {
            mMinimum = number;
            mMaximum = number;
        } else if (number < mMinimum) {
            mMinimum = number;
        } else if (number > mMaximum) {
            mMaximum = number;
        }
    }
    
    private boolean containsSingleNumber() {
        return mCount == 1;
    }

    public int getCount() {
        return mCount;
    }

    public double getTotal() {
        return mTotal;
    }

    public double getMinimum() {
        return mMinimum;
    }

    public double getMaximum() {
        return mMaximum;
    }

    public double getAverage() {
        return mTotal / (double)mCount;
    }
    
    
}
