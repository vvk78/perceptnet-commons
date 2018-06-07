package com.perceptnet.commons.beanprocessing.validation;

import com.perceptnet.commons.beanprocessing.validation.dto.OrderCreationDto;
import com.perceptnet.commons.beanprocessing.validation.datao.OrderDo;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.perceptnet.commons.tests.TestGroups.UNIT;

/**
 * created by vkorovkin on 18.05.2018
 */
public class DtoByDoValidatorTest {
    private ValidationContext ctx;
    private DtoByDoValidator v;

    @BeforeMethod(groups = {UNIT})
    public void beforeMethod() {
        ctx = validationCtx();
        v = new DtoByDoValidator(ctx);
    }

    @Test(groups = {UNIT})
    public void testOrderValidation() {
        OrderCreationDto dto = new OrderCreationDto();
        v.process(dto, OrderDo.class);
    }

    private ValidationContext validationCtx() {
        return new ValidationContext(ReflectionCache.DTO_REF_PROVIDER, ReflectionCache.DO_REF_PROVIDER);
    }

}