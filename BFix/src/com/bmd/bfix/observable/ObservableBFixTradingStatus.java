package com.bmd.bfix.observable;

import java.util.Observable;

public class ObservableBFixTradingStatus extends Observable {
    
    public void changeStatus() {
       
        setChanged();
        notifyObservers();
        clearChanged();
    }

}