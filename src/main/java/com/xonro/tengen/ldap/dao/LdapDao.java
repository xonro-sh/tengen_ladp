package com.xonro.tengen.ldap.dao;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPSearchResults;
import com.novell.ldap.util.DN;
import com.tengen.function.SimpleFunciton;
import com.tengen.ldap.Constants;
import org.apache.commons.lang3.StringUtils;
import org.quartz.utils.StringKeyDirtyFlagMap;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.nio.charset.Charset;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * LDAP数据服务
 * @author louie
 * @date 2018-1-30
 */
public enum LdapDao {
    INSTANCE;

    private SimpleFunciton simpleFunciton = new SimpleFunciton();

    /**
     * LDAP服务ip
     */
    private final String LDAP_SERVER_IP = simpleFunciton.getProperties(Constants.KEY_LDAPIP);

    /**
     * LDAP服务端口号
     */
    private final int LDAP_SERVER_PORT = Integer.parseInt(simpleFunciton.getProperties(Constants.KEY_LDAPPORT));

    /**
     * LDAP版本
     */
    private final int LDAP_SERVER_VERSION = LDAPConnection.LDAP_V3;

    private final String LDAP_SERVER_BASEDN = new BaseServer().getBaseDn();

    private final String LDAP_PERSOPN_FILTER = simpleFunciton.getProperties(Constants.KEY_PERSON_FILTER);

    private final Hashtable<String,String> LDAP_DEFAULT_CONTEXT = new BaseServer().getDefaultDirContext();

    /**
     * 根据用户账号查找用户，找到返回true，否则返回false
     * @param account 用户账号
     * @return true/false
     * @throws NamingException
     */
    public boolean findUserByAccount(String account) throws NamingException {
        String filter = "(&(uid="+account+"))";
        try {
            DirContext dirContext = new InitialDirContext(LDAP_DEFAULT_CONTEXT);
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            NamingEnumeration searchResult = dirContext.search("",filter,searchControls);
            while (searchResult.hasMore()){
//                SearchResult sr=(SearchResult)searchResult.next();
//                Attributes attrs=sr.getAttributes();
//                NamingEnumeration attrList=attrs.getAll();
                return true;
            }
        } catch (NamingException e) {
            e.printStackTrace(System.err);
            throw e;
        }
        return false;
    }


    /**
     * 校验用户密码是否正确
     * @param userAccount
     * @param password
     * @return
     */
    public boolean checkUserPassword(String userAccount,String password){
        LDAPConnection connection = null;

        try {
            connection = new LDAPConnection();
            connection.connect(LDAP_SERVER_IP,LDAP_SERVER_PORT);
            connection.bind(LDAP_SERVER_VERSION,getUserDn(new DN(LDAP_SERVER_BASEDN),userAccount),password.getBytes());

            return true;
        } catch (LDAPException e) {
            e.printStackTrace(System.err);
        }
        return false;
    }

    /**
     * 更改用户密码
     * @param userAccount
     * @param newPassword
     * @throws Exception
     */
    public void changePwdByAccount(String userAccount,String newPassword) throws Exception {
        LDAPConnection connection = null;

        try {
            connection = getLdapBaseConnection();
            String userDn = getUserDn(connection,new DN(LDAP_SERVER_BASEDN),userAccount);

            ModificationItem[] modificationItems = new ModificationItem[1];
            modificationItems[0] = new ModificationItem(
                    DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userpassword", newPassword)
            );

            DirContext dirContext = new InitialDirContext(LDAP_DEFAULT_CONTEXT);
            dirContext.modifyAttributes(userDn,modificationItems);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw e;
        } finally {
            if (connection != null){
                connection.disconnect();
            }
        }
    }

    /**
     * 创建用户
     * @param attributeMap 用户属性集合
     * @throws Exception
     */
    public void createUserAccount(Map<String,String> attributeMap) throws Exception {
        LDAPConnection connection = null;

        try {
            connection = getLdapBaseConnection();
            DirContext dirContext = new InitialDirContext(LDAP_DEFAULT_CONTEXT);

            BasicAttributes attributes = new BasicAttributes();

            BasicAttribute objclassSet = new BasicAttribute("objectclass");
            objclassSet.add("person");
            objclassSet.add("top");
            objclassSet.add("organizationalPerson");
            objclassSet.add("inetOrgPerson");

            attributes.put(objclassSet);

            String fullName = attributeMap.get("fullname");
            attributeMap.remove("fullname");

            Set<String> attributeKeys = attributeMap.keySet();
            for (String attributeKey : attributeKeys) {
                if (StringUtils.isNotEmpty(attributeMap.get(attributeKey))){
                    attributes.put(attributeKey,attributeMap.get(attributeKey));
                }
            }

            dirContext.createSubcontext(fullName, attributes);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw e;
        }finally {
            if (connection != null){
                connection.disconnect();
            }
        }
    }

    /**
     * 获取ldap基础连接
     * @return
     * @throws LDAPException
     */
    private LDAPConnection getLdapBaseConnection() throws LDAPException {
        LDAPConnection connection = null;

        try {
            connection = new LDAPConnection();
            connection.connect(LDAP_SERVER_IP,LDAP_SERVER_PORT);
            connection.bind(LDAP_SERVER_VERSION,null,"".getBytes());
        } catch (LDAPException e) {
            e.printStackTrace(System.err);
            throw e;
        }
        return connection;
    }

    /**
     * 获取用户DN值
     * @param connection
     * @param baseDn
     * @param userAccount
     * @return
     * @throws LDAPException
     */
    private String getUserDn(LDAPConnection connection,DN baseDn,String userAccount) throws LDAPException {
        try {
            LDAPSearchResults empRs = connection.search(
                    baseDn.toString(), LDAPConnection.SCOPE_SUB,
                    "(&("+LDAP_PERSOPN_FILTER+")(uid=" + userAccount+ "))",null,false);

            if (empRs.hasMore()) {
                LDAPEntry entry = empRs.next();
                return entry.getDN();
            }
        } catch (LDAPException e) {
            e.printStackTrace(System.err);
            throw e;
        }
        return null;
    }

    private String getUserDn(DN baseDn,String userAccount) throws LDAPException {
        LDAPConnection connection = null;

        try {
            connection = getLdapBaseConnection();
            return getUserDn(connection,baseDn,userAccount);
        } catch (LDAPException e) {
            e.printStackTrace(System.err);
            throw e;
        }finally {
            if (connection != null){
                connection.disconnect();
            }
        }
    }

    private class BaseServer{
        private String getBaseDn(){
            String[] baseDns = simpleFunciton.getProperties(Constants.KEY_BASEDN).split("\\.");
            StringBuilder baseDnStr = new StringBuilder();
            for (String baseDn : baseDns) {
                baseDnStr.append("DC="+baseDn+",");
            }

            String base = baseDnStr.toString();
            return base.substring(0,base.lastIndexOf(","));
        }

        private Hashtable<String,String> getDefaultDirContext(){
            Hashtable<String, String> defaultDirContext = new Hashtable<String, String>();
            defaultDirContext.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            defaultDirContext.put(Context.PROVIDER_URL, "ldap://" + LDAP_SERVER_IP + ":" + LDAP_SERVER_PORT);
            defaultDirContext.put(Context.SECURITY_AUTHENTICATION, "Simple");
            //绑定用户名
            defaultDirContext.put(Context.SECURITY_PRINCIPAL, "cn=root");
            //绑定用户名密码
            defaultDirContext.put(Context.SECURITY_CREDENTIALS, "tengen");

            return defaultDirContext;
        }
    }

}
