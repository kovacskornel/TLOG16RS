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
public class InvalidTaskIDException extends RuntimeException {

    public InvalidTaskIDException() {
        
    }

    public InvalidTaskIDException(String string) {
        super(string);
    }
    
    
}
