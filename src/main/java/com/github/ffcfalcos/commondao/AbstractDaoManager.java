package com.github.ffcfalcos.commondao;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Beauchataud
 * @since 03.11.2019
 * @version 2.1.0
 * The abstract class is used to manage complex entities stored with DAO
 * It has the method {@link AbstractDaoManager#enrich(Object)} which enrich an entities
 *      with appropriates annotation by executing queries on other services which permit to generate complex entities
 * It also has the method {@link AbstractDaoManager#enrich(Object)} which update an
 *      complex entity by updating his sub entities with others services
 */
@SuppressWarnings({"unused","WeakerAccess"})
public abstract class AbstractDaoManager {

    private List<Object> services;

    /**
     * Add services to the AbstractDaoManager to execute invoked method
     * @param services Object[]
     */
    protected void setDaoBindServices(List<Object> services) {
        this.services = services;
    }

    /**
     * Enrich an object which have a {@link DaoBindSelect} annotation on methods
     * @param object Object the object to enrich
     * @throws Exception If there an error while meta invocation
     */
    protected void enrich(Object object) throws Exception {
        Method[] methods = object.getClass().getDeclaredMethods();
        for(Method method : methods) {
            DaoBindSelect daoBindSelect = method.getAnnotation(DaoBindSelect.class);
            if(daoBindSelect == null) {
                continue;
            }
            Object parameter = null;
            if(!daoBindSelect.loopMethod().equals("null")) {
                Method loopMethod = getMethodByName(daoBindSelect.loopMethod(), object);
                parameter = loopMethod.invoke(object);
            } else {
                if (!daoBindSelect.methodParameter().equals("null")) {
                    parameter = getParameter(object, daoBindSelect.methodParameter());
                }
                if (!daoBindSelect.valueParameter().equals("null")) {
                    parameter = daoBindSelect.valueParameter();
                }
            }
            Object finalParameter;
            Object service = getServiceByName(daoBindSelect.service());
            Method finalMethod = getMethodByName(daoBindSelect.method(), service);
            if(parameter == null) {
                finalParameter = finalMethod.invoke(service);
            } else {
                if(parameter instanceof List) {
                    List<Object> objectList = new ArrayList<>();
                    for(Object subParameter : (List)parameter) {
                        subParameter = getMethodByName(daoBindSelect.methodParameter(), subParameter).invoke(subParameter);
                        objectList.add(finalMethod.invoke(service, subParameter));
                    }
                    finalParameter = objectList;
                } else {
                    finalParameter = finalMethod.invoke(service, parameter);
                }
            }
            method.invoke(object, finalParameter);
        }
    }

    /**
     * Update an object which has the {@link DaoBindUpdate} annotation on methods
     * @param object Object the object to update
     * @throws Exception If there an error while meta invocation
     */
    protected void update(Object object) throws Exception {
        Method[] methods = object.getClass().getDeclaredMethods();
        for(Method method : methods) {
            DaoBindUpdate daoBindUpdate = method.getAnnotation(DaoBindUpdate.class);
            if(daoBindUpdate == null) {
                continue;
            }
            Object parameter = null;
            Object service = getServiceByName(daoBindUpdate.service());
            if(!daoBindUpdate.methodParameter().equals("null")) {
                parameter = method.invoke(object);
            }
            if(!daoBindUpdate.valueParameter().equals("null")) {
                parameter = daoBindUpdate.valueParameter();
            }
            Method finalMethod = getMethodByName(daoBindUpdate.method(), service);
            if(parameter instanceof List) {
                for(Object subParameter : (List)parameter) {
                    finalMethod.invoke(service, subParameter);
                }
            } else {
                finalMethod.invoke(service, parameter);
            }
        }
    }

    /**
     * Execute multiple invocation on the same object to have the appropriate parameter
     * @param object Object
     * @param daoBindMethodParameter String {@link DaoBindUpdate#methodParameter()}
     * @return Object
     * @throws Exception if some invoked method doesn't exists
     */
    private Object getParameter(Object object, String daoBindMethodParameter) throws Exception {
        String[] methodsName = daoBindMethodParameter.split("\\.");
        for(String methodName : methodsName) {
            for(Method method : object.getClass().getDeclaredMethods()) {
                if(method.getName().equals(methodName)) {
                    object = method.invoke(object);
                }
            }
        }
        return object;
    }

    /**
     * Return a service identified by his name
     * @param serviceName String
     * @return Object
     * @throws Exception If there is no service with this name
     */
    private Object getServiceByName(String serviceName) throws Exception {
        for(Object service : services) {
            if(service.getClass().getName().equals(serviceName)) {
                return service;
            }
        }
        throw new Exception("Impossible to find the service " + serviceName);
    }

    /**
     * Return a method of the service identified by his name
     * @param methodName String
     * @param service String
     * @return {@link java.lang.reflect.Method}
     * @throws Exception If this service doesn't have any method with this name
     */
    private Method getMethodByName(String methodName, Object service) throws Exception {
        for(Method method : service.getClass().getMethods()) {
            if(method.getName().equals(methodName)) {
                return method;
            }
        }
        throw new Exception("Impossible to find the method " + methodName + " for the service" + service.getClass().getName());
    }

}
