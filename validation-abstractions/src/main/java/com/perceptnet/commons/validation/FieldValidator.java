

package com.perceptnet.commons.validation;

/**
 *
 * created by vkorovkin on 16.05.2018
 */
public interface FieldValidator<V, F> {
    void validateFieldValue(ValidationContext ctx, Object onObject, V value, F ... fieldMetas);
}
