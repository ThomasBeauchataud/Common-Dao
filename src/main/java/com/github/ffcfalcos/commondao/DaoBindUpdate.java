package com.github.ffcfalcos.commondao;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Thomas Beauchataud
 * @since 05.11.2019
 * @version 2.1.0
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DaoBindUpdate {

    /**
     * The service used to update the sub entity
     * @return String
     */
    String service();

    /**
     * The method of the service used to update the sub entity
     * @return String
     */
    String method();

    /**
     * The parameter of the method to execute
     * This parameter is issue from a method on the object
     * You can execute multiple pipelined invocation (getSubEntity.getDate.getDay)
     * @return String
     */
    String methodParameter() default "null";

    /**
     * The constant parameter of the method to execute
     * @return String
     */
    String valueParameter() default "null";

}
