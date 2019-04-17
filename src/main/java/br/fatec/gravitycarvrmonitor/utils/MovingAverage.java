/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.fatec.gravitycarvrmonitor.utils;

import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Tiago Guerino
 */
public class MovingAverage {

    private final Queue<Double> window = new LinkedList<>();
    private final int period;
    private double sum;

    public MovingAverage(int period) {
        assert period > 0 : "Period must be a positive integer";
        this.period = period;
    }

    public void add(double number) {
        sum += number;
        window.add(number);
        if (window.size() > period) {
            sum -= window.remove();
        }
    }

    public double getAverage() {
        if (window.isEmpty()) {
            return 0.0;
        }
        return sum / window.size();
    }
    
    
}