package com.perceptnet.commons.validation;


public interface ValidationContext {
    void registerProblem(String path, String problem);

//    String getResourceQualifier();
}
