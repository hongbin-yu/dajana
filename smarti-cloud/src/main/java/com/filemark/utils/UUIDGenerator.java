package com.filemark.utils;

import java.util.UUID;


/**
* Generates UUID values.
*/
public class UUIDGenerator {
   
    // non-instantiable
    private UUIDGenerator() {}
   
    
    /**
     * Generate a new UUID.
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }
   
}
 
