/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kovacskornel.tlog16rs.core;

/**
 *
 * @author precognox
 */
public class FutureWorkException extends RuntimeException{

    public FutureWorkException(String string) {
        super(string);
    }

    public FutureWorkException() {
        //returns the exception
    }
    
    
}
