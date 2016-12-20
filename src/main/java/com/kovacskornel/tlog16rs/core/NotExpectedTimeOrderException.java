/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kovacskornel.tlog16rs.core;

/**
 *
 * @author Flamy
 */
public class NotExpectedTimeOrderException extends RuntimeException {

    public NotExpectedTimeOrderException() {
    }           

    public NotExpectedTimeOrderException(String string) {
        super(string);
    }
}
