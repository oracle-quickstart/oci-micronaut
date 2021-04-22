package mushop.carts.soda;

import io.micronaut.aop.Introduction;
import io.micronaut.core.annotation.Internal;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Introduction
@Internal
@interface TransactionalOracleSodaDatabaseAnn {
}
