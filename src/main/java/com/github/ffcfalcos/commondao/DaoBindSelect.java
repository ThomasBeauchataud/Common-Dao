package com.github.ffcfalcos.commondao;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Thomas Beauchataud
 * @since 05.11.2019
 * @version 2.1.0
 * This annotation is used to be add on setter methods of a complex entity
 * It allow the entity to use the {@link AbstractDaoManager#enrich(Object)} to generate a complex entities with sub entities
 *      stored with other services
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DaoBindSelect {

    /**
     * The service used to get the sub entity
     * @return String
     */
    String service();

    /**
     * The method of the service used to get the sub entity
     * @return String
     */
    String method();

    /**
     * The parameter of the method to execute
     * This parameter is issue from a method on the object
     * You can execute multiple pipelined invocation (getSubEntity.getDate.getDay)
     * This parameter only work to treat a singleton parameter, to use lists, watch {@link DaoBindSelect#loopMethod()}
     * @return String
     */
    String methodParameter() default "null";

    /**
     * The constant parameter of the method to execute
     * @return String
     */
    String valueParameter() default "null";

    /**
     * This parameter is used to get parameter from a method on the object but which returns a list of object
     * In this case, you still can use methodParameter to extract an attribute on the sub object,
     *      but dont write the getter method
     * @return String
     */
    String loopMethod() default "null";

}
