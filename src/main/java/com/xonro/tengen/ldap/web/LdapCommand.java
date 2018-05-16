package com.xonro.tengen.ldap.web;

import com.actionsoft.application.server.BaseSocketCommand;
import com.actionsoft.awf.organization.control.UserContext;
import com.actionsoft.awf.util.UtilString;
import com.alibaba.fastjson.JSON;
import com.xonro.tengen.ldap.bean.UserInfo;
import com.xonro.tengen.ldap.factory.ServiceFactory;
import com.xonro.tengen.ldap.service.UserService;

import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Vector;

/**
 * LDAP相关请求命令监听
 * @author louie
 * @date 2018-1-30
 */
public class LdapCommand implements BaseSocketCommand {

    private final String TO_ADD_PAGE = "com.xonro.tengen_toAddAccount";
    private final String ADD_ACCOUNT = "com.xonro.tengen_addAccount";
    private final String TO_CHANGEPWD_PAGE = "com.xonro.tengen_toChangePwd";
    private final String CHANGE_PWD = "com.xonro.tengen_changePwd";

    private UserService userService = ServiceFactory.getUserService();

    @Override
    public boolean executeCommand(UserContext userContext, Socket socket, OutputStreamWriter writer,
                                  Vector vector, UtilString utilString, String socketCommand) throws Exception {
        /**
         * 打开新增用户界面
         */
        if (TO_ADD_PAGE.equals(socketCommand)){
            writer.write(userService.toAddUserPage(userContext));
        }

        /**
         * 打开修改用户密码界面
         */
        else if (TO_CHANGEPWD_PAGE.equals(socketCommand)){
            writer.write(userService.toChangePwsPage(userContext));
        }

        /**
         * 新增用户账号
         */
        else if (ADD_ACCOUNT.equals(socketCommand)){
            String userInfo = utilString.matchValue("_user[","]user_");
            UserInfo user = JSON.parseObject(userInfo,UserInfo.class);
            writer.write(JSON.toJSONString(userService.createUser(user.getDefaultInfo())));
        }

        /**
         * 修改用户密码
         */
        else if (CHANGE_PWD.equals(socketCommand)){
            String userAccount = utilString.matchValue("_uid[","]uid_");
            String oldPassword = utilString.matchValue("_oldpwd[","]oldpwd_");
            String newPassword = utilString.matchValue("_newpwd[","]newpwd_");

            writer.write(JSON.toJSONString(userService.changeUserPwd(userAccount,oldPassword,newPassword)));
        }

        else {
            return false;
        }

        return true;
    }
}
