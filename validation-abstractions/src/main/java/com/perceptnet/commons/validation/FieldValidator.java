

package com.perceptnet.commons.validation;

/**
 * Created by vkorovkin on 20.04.2015.
 */
public interface FieldValidator<V> {
    void validateFieldValue(ValidationContext ctx, Object onObject, V value);
}
