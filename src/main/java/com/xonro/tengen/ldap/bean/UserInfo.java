package com.xonro.tengen.ldap.bean;

/**
 * 用户信息
 * @author louie
 * @date 2018-2-1
 */
public class UserInfo {

    public UserInfo(){}

    /**
     * 含参构造
     * @param uid 用户账号
     * @param displayname 用户姓名
     * @param userpassword 用户密码
     * @param mail 邮箱
     */
    public UserInfo(String uid,String displayname,String userpassword,String mail){
        this.uid = uid;
        this.displayname = displayname;
        this.userpassword = userpassword;
        this.mail = mail;
    }

    public UserInfo getDefaultInfo(){
        this.cn = this.uid;
        this.sn = this.uid;
        this.givenname = this.uid;
        this.fullname = "uid="+uid+",cn=users,DC=TENGEN,DC=COM,DC=CN";
        return this;
    }

    private String uid;
    private String displayname;
    private String givenname;
    private String mail;
    private String userpassword;
    private String fullname;
    private String cn;
    private String sn;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public String getGivenname() {
        return givenname;
    }

    public void setGivenname(String givenname) {
        this.givenname = givenname;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getUserpassword() {
        return userpassword;
    }

    public void setUserpassword(String userpassword) {
        this.userpassword = userpassword;
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

}
