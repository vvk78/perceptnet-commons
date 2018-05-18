package com.perceptnet.commons.validation;


/**
 *
 * created by vkorovkin on 16.05.2018
 */
public interface ValidationContext {
    void registerProblem(String path, String problem);

//    String getResourceQualifier();
}
