package com.xonro.tengen.ldap.service.impl;

import com.actionsoft.awf.organization.control.UserContext;
import com.actionsoft.htmlframework.web.ActionsoftWeb;
import com.xonro.tengen.ldap.bean.ResponseModel;
import com.xonro.tengen.ldap.bean.UserInfo;
import com.xonro.tengen.ldap.dao.LdapDao;
import com.xonro.tengen.ldap.service.UserService;
import org.apache.commons.beanutils.BeanUtils;

import java.util.Hashtable;
import java.util.Map;

/**
 * @author louie
 * @date 2018-2-1
 */
public class UserServiceImpl extends ActionsoftWeb implements UserService{

    private LdapDao ldapDao = LdapDao.INSTANCE;

    public String toAddUserPage(UserContext userContext) {
        String pageName = "com.xonro.tengen_createLdapUser.html";
        Hashtable<String,String> param = new Hashtable<String, String>();
        param.put("sid",userContext.getSessionId());
        return getHtmlPage(pageName,param);
    }

    public String toChangePwsPage(UserContext userContext) {
        String pageName = "com.xonro.tengen_changeLdapPwd.html";
        Hashtable<String,String> param = new Hashtable<String, String>();
        param.put("sid",userContext.getSessionId());
        param.put("uid",userContext.getUID());
        return getHtmlPage(pageName,param);
    }

    /**
     * 修改账户密码
     * @param account 用户账号
     * @param oldPwd 旧密码
     * @param newPwd 新密码
     * @return 修改结果
     */
    public ResponseModel changeUserPwd(String account, String oldPwd, String newPwd) throws Exception {
        try {
            //校验账号
            if (!ldapDao.findUserByAccount(account)){
                return new ResponseModel(false,"账号"+account+"不存在");
            }

            //校验旧密码
            if (!ldapDao.checkUserPassword(account,oldPwd)){
                return new ResponseModel(false,"密码校验失败");
            }

            //更改密码
            ldapDao.changePwdByAccount(account,newPwd);
            return new ResponseModel(true,"success");
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw e;
        }
    }

    /**
     * 创建用户
     * @param userInfo 新增的用户信息
     * @return
     */
    public ResponseModel createUser(UserInfo userInfo) throws Exception {
        String userAccount = userInfo.getUid();
        try {
            //校验账号
            if (ldapDao.findUserByAccount(userAccount)){
                return new ResponseModel(false,"账号"+userAccount+"已存在");
            }

            //创建用户
            Map<String,String> infoMap = BeanUtils.describe(userInfo);
            infoMap.remove("class");
            infoMap.remove("defaultInfo");
            ldapDao.createUserAccount(infoMap);

            return new ResponseModel(true,"success");
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw e;
        }
    }


    private UserServiceImpl(){}

    public static UserServiceImpl getInstance(){
        return new UserServiceImpl();
    }

}
