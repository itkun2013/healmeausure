package com.konsung.bean;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * 病人信息类
 * 使用Ormlite 映射到数据库
 */
@DatabaseTable(tableName = "t_patient")
public class PatientBean {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(canBeNull = false)
    private String name;
    @DatabaseField
    private String num = ""; //编号

    @DatabaseField
    private String idcard = "";
    @DatabaseField
    private String idByServer;
    @DatabaseField
    private String orgId;
    @DatabaseField
    private String address;
    @DatabaseField
    private String waist; // 腰围
    @DatabaseField
    private String hipline; // 臀围
    @DatabaseField
    private int sex = 0; //性别 1男 2女
    @DatabaseField
    private int age = 0;
    @DatabaseField
    private int blood = 0;
    @DatabaseField
    private int patientType = 0; // 居民类型 成人 小儿 婴幼儿
    @DatabaseField
    private String contact; // 居民联系方式
    @DatabaseField
    private float height = 0f;
    @DatabaseField
    private float weight = 0f;
    @DatabaseField(dataType = DataType.DATE_LONG)
    private Date birthday;

    @DatabaseField(dataType = DataType.DATE_LONG)
    private Date addDate = new Date(); //居民添加日期
    @DatabaseField(dataType = DataType.DATE_LONG)
    private Date crimeMeasureDate; //当前居民最近的侧量事间
    @DatabaseField
    private String iconPath; //居民身份证头像路径

    /**
     * 获取id的值
     *
     * @return id id值
     */
    public int getId() {
        return id;
    }

    /**
     * 设置id的值
     *
     * @param id id值
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 获取name的值
     *
     * @return name name值
     */
    public String getName() {
        return name;
    }

    /**
     * 设置name的值
     *
     * @param name name值
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取num的值
     *
     * @return num num值
     */
    public String getNum() {
        return num;
    }

    /**
     * 设置num的值
     *
     * @param num num值
     */
    public void setNum(String num) {
        this.num = num;
    }

    /**
     * 获取idCard的值
     *
     * @return idcard idCard值
     */
    public String getIdCard() {
        return idcard;
    }

    /**
     * 设置idCard的值
     *
     * @param idcard idCard值
     */
    public void setIdCard(String idcard) {
        this.idcard = idcard;
    }

    /**
     * 获取idByServer的值
     *
     * @return idByServer idByServer值
     */
    public String getIdByServer() {
        return idByServer;
    }

    /**
     * 设置idByServer的值
     *
     * @param idByServer idByServer值
     */
    public void setIdByServer(String idByServer) {
        this.idByServer = idByServer;
    }

    /**
     * 获取orgId的值
     *
     * @return orgId orgId值
     */
    public String getOrgId() {
        return orgId;
    }

    /**
     * 设置orgId的值
     *
     * @param orgId orgId值
     */
    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取address的值
     *
     * @return address address值
     */
    public String getAddress() {
        return address;
    }

    /**
     * 设置address的值
     *
     * @param address address值
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 获取waist的值
     *
     * @return waist waist值
     */
    public String getWaist() {
        return waist;
    }

    /**
     * 设置waist的值
     *
     * @param waist waist值
     */
    public void setWaist(String waist) {
        this.waist = waist;
    }

    /**
     * 获取hipline的值
     *
     * @return hipline hipline值
     */
    public String getHipline() {
        return hipline;
    }

    /**
     * 设置hipline的值
     *
     * @param hipline hipline值
     */
    public void setHipline(String hipline) {
        this.hipline = hipline;
    }

    /**
     * 获取sex的值
     *
     * @return sex sex值
     */
    public int getSex() {
        return sex;
    }

    /**
     * 设置sex的值
     *
     * @param sex sex值
     */
    public void setSex(int sex) {
        this.sex = sex;
    }

    /**
     * 获取age的值
     *
     * @return age age值
     */
    public int getAge() {
        return age;
    }

    /**
     * 设置age的值
     *
     * @param age age值
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * 获取blood的值
     *
     * @return blood blood值
     */
    public int getBlood() {
        return blood;
    }

    /**
     * 设置blood的值
     *
     * @param blood blood值
     */
    public void setBlood(int blood) {
        this.blood = blood;
    }

    /**
     * 获取patientType的值
     *
     * @return patientType patientType值
     */
    public int getPatientType() {
        return patientType;
    }

    /**
     * 设置patientType的值
     *
     * @param patientType patientType值
     */
    public void setPatientType(int patientType) {
        this.patientType = patientType;
    }

    /**
     * 获取contact的值
     *
     * @return contact contact值
     */
    public String getContact() {
        return contact;
    }

    /**
     * 设置contact的值
     *
     * @param contact contact值
     */
    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * 获取height的值
     *
     * @return height height值
     */
    public float getHeight() {
        return height;
    }

    /**
     * 设置height的值
     *
     * @param height height值
     */
    public void setHeight(float height) {
        this.height = height;
    }

    /**
     * 获取weight的值
     *
     * @return weight weight值
     */
    public float getWeight() {
        return weight;
    }

    /**
     * 设置weight的值
     *
     * @param weight weight值
     */
    public void setWeight(float weight) {
        this.weight = weight;
    }

    /**
     * 获取birthday的值
     *
     * @return birthday birthday值
     */
    public Date getBirthday() {
        return birthday;
    }

    /**
     * 设置birthday的值
     *
     * @param birthday birthday值
     */
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    /**
     * 获取addDate的值
     *
     * @return addDate addDate值
     */
    public Date getAddDate() {
        return addDate;
    }

    /**
     * 设置addDate的值
     *
     * @param addDate addDate值
     */
    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    /**
     * 获取crimeMeasureDate的值
     *
     * @return crimeMeasureDate crimeMeasureDate值
     */
    public Date getCrimeMeasureDate() {
        return crimeMeasureDate;
    }

    /**
     * 设置crimeMeasureDate的值
     *
     * @param crimeMeasureDate crimeMeasureDate值
     */
    public void setCrimeMeasureDate(Date crimeMeasureDate) {
        this.crimeMeasureDate = crimeMeasureDate;
    }

    /**
     * 获取iconPath的值
     *
     * @return iconPath iconPath值
     */
    public String getIconPath() {
        return iconPath;
    }

    /**
     * 设置iconPath的值
     *
     * @param iconPath iconPath值
     */
    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

}