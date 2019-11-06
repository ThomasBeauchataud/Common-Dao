package com.github.ffcfalcos.commondao;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Thomas Beauchataud
 * @since 05.11.2019
 * @version 2.1.0
 * This annotation is used on dao class extending {@link AbstractDao}
 * It allow a class to used pre-constructed SQL request such as:
 *      {@link AbstractDao#deleteById(int)}
 *      {@link AbstractDao#getById(int)}
 *      {@link AbstractDao#getAll()}
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DaoBind {

    /**
     * The name of the Table associated to the dao class
     * @return String
     */
    String tableName();

}
