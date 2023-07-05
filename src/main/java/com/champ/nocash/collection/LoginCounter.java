package com.champ.nocash.collection;

public class LoginCounter {
    private Integer counter;
    public LoginCounter() {
        reset();
    }
    public boolean isValid() {
        return  counter <= 3;
    }
    public void increment() {
        counter++;
    }
    public void reset() {
        counter = 0;
    }
    public void setCounter(int count) {

    }
    public int getCounter() {
        return counter;
    }
}
