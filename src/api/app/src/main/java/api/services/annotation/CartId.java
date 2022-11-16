package api.services.annotation;

import io.micronaut.core.bind.annotation.Bindable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to be used to bind the CartId from the session to the UUID.
 *
 * @see {@link api.services.support.CartIdBinder}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Bindable
public @interface CartId {
}
