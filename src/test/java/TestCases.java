import com.alibaba.fastjson.JSON;
import com.xonro.tengen.ldap.bean.ResponseModel;
import com.xonro.tengen.ldap.bean.UserInfo;
import com.xonro.tengen.ldap.dao.LdapDao;
import com.xonro.tengen.ldap.factory.ServiceFactory;
import com.xonro.tengen.ldap.service.UserService;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.Test;

import javax.naming.NamingException;
import java.lang.reflect.InvocationTargetException;

/**
 * @author louie
 * @date 2018-1-29
 */
public class TestCases {

    @Test
    public void tesgChangeUserPwd() throws Exception {
        String userAccount = "baihang";
        LdapDao.INSTANCE.changePwdByAccount(userAccount,"123456");
    }

    @Test
    public void testCheckUserPwd() throws Exception {
        String userAccount = "testAccount2";
        boolean isRight = LdapDao.INSTANCE.checkUserPassword(userAccount,"11");
        System.out.println(isRight);
    }

    @Test
    public void testFindUser() throws NamingException {
        String account = "hongxianheng";
        System.out.println(LdapDao.INSTANCE.findUserByAccount(account));;
    }

    @Test
    public void testUserInfo() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        UserInfo userInfo = new UserInfo("account","name","1","1@123.com");
        userInfo = userInfo.getDefaultInfo();
        System.out.println(userInfo.toString());

        System.out.println(BeanUtils.describe(userInfo));

    }

    UserService userService = ServiceFactory.getUserService();

    @Test
    public void testChangePwdServcie() throws Exception {
        ResponseModel responseModel = userService.changeUserPwd("testAccount","123456","1");
        System.out.println(JSON.toJSONString(responseModel));
    }

    @Test
    public void testCreateService() throws Exception {
        UserInfo userInfo = new UserInfo("testAccount5","测试账号5","1","").getDefaultInfo();
        ResponseModel responseModel = userService.createUser(userInfo);
        System.out.println(JSON.toJSONString(responseModel));
    }

}
