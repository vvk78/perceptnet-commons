package com.perceptnet.commons.validation;


/**
 * created by vkorovkin on 18.05.2018
 */
public interface Validatable {

    //todo consider returning boolean or a List (with found problems)
    /**
     * Validates item and registers all found problems with {@link ValidationContext#registerProblem(String, String)}
     *
     * @param ctx validation context
     * @param path the path the item being validated is located at
     */
    void validate(ValidationContext ctx, String path);
}
