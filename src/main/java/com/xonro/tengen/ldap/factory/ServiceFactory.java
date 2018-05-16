package com.xonro.tengen.ldap.factory;

import com.xonro.tengen.ldap.service.UserService;
import com.xonro.tengen.ldap.service.impl.UserServiceImpl;

/**
 * @author louie
 * @date 2018-2-1
 */
public class ServiceFactory {

    public static UserService getUserService(){
        return UserServiceImpl.getInstance();
    }

}
