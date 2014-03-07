package com.bmd.bfix.observable;

import java.util.Observable;

public class ObservableBFixLogon extends Observable {
    
    public void logon() {
       
        setChanged();
        notifyObservers();
        clearChanged();
    }

    public void logoff() {
        
        setChanged();
        notifyObservers();
        clearChanged();
    }
}