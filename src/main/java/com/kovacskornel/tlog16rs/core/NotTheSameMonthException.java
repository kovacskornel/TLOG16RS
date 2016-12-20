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
public class NotTheSameMonthException extends RuntimeException{

    public NotTheSameMonthException() {
    }

    public NotTheSameMonthException(String message) {
        super(message);
    }
    
}
