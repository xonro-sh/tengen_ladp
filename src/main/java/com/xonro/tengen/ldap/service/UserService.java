package com.xonro.tengen.ldap.service;

import com.actionsoft.awf.organization.control.UserContext;
import com.xonro.tengen.ldap.bean.ResponseModel;
import com.xonro.tengen.ldap.bean.UserInfo;

import javax.naming.NamingException;

/**
 * 用户服务
 * @author louie
 * @date 2018-1-30
 */
public interface UserService {

    /**
     * 打开新增用户界面
     * @param userContext
     * @return
     */
    public String toAddUserPage(UserContext userContext);

    /**
     * 打开修改用户密码界面
     * @param userContext
     * @return
     */
    public String toChangePwsPage(UserContext userContext);

    /**
     * 修改用户账号密码
     * @param account 用户账号
     * @param oldPwd 旧密码
     * @param newPwd 新密码
     * @return
     */
    public ResponseModel changeUserPwd(String account,String oldPwd,String newPwd) throws Exception;

    /**
     * 新增用户
     * @param userInfo 新增的用户信息
     * @return
     */
    public ResponseModel createUser(UserInfo userInfo) throws NamingException, Exception;
}
