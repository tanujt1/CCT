package com.util;

import com.sun.istack.internal.NotNull;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BambooProperties{

    private String BASE_PACKAGE_NAME="channelsUpstream/requests/";

    private String packageName;

    @NotNull
    private String userName;
    @NotNull
    private String password;
    @NotNull
    private String job_id;

    private String filterLinks="all";

    private boolean saveAsFile = false;
    private boolean appendFile = false;
    private boolean isNppSummary=false;

    private String filePath="C:/";

    private String jiraTaskId;

    // Multiple jiraId
    /*private List<String> jiraTaskIdList;*/

    public String getJiraTaskId() {
        return jiraTaskId;
    }

    public void setJiraTaskId(String jiraTaskId) {
        this.jiraTaskId = jiraTaskId;
    }

    private File file;

    private int limit;

    private List<Integer> range;

    public BambooProperties() {
        this.range = new ArrayList<Integer>(){{
            add(0);
        }};
    }

    public String getFilterLinks() {
        return filterLinks;
    }

    public void setFilterLinks(String filterLinks) {
        if(StringUtils.isNotEmpty(filterLinks))
            this.filterLinks = filterLinks;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
        if(this.range!=null && this.range.size()==1 && this.limit>0){
            this.range.add(limit);
        }
    }

    public List<Integer> getRange() {
        return range;
    }

    public void setRange(List<Integer> range) {

        if(CollectionUtils.isNotEmpty(range)){
            this.range = range;
        }
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
        filePath = file.getAbsolutePath();
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
      this.packageName = BASE_PACKAGE_NAME+packageName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        // System.out.println(this.userName);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
       //  System.out.println(this.password);
    }

    public String getJob_id() {
        return job_id;
    }

    public void setJob_id(String job_id) {
        this.job_id = job_id;
       //  System.out.println(this.job_id);
    }

    public boolean isSaveAsFile() {
        return saveAsFile;
    }

    public void setSaveAsFile(boolean saveAsFile) {
        this.saveAsFile = saveAsFile;
        // System.out.println(saveAsFile);
    }

    public String getFilePath() {
        return filePath;
    }

     public  void setFilePath(String filePath) {
        this.filePath = filePath;

        file = new File(filePath);

        // System.out.println(filePath);
     }

    public boolean isAppendFile() {
        return appendFile;
    }

    public void setAppendFile(boolean appendFile) {
        this.appendFile = appendFile;
    }

    public boolean isNppSummary() {
        return isNppSummary;
    }

    public void setNppSummary(boolean nppSummary) {
        isNppSummary = nppSummary;
    }

    /**
     * https://www.reddit.com/r/learnprogramming/comments/2q6gma/javahow_to_web_scrape_a_site_behind_a_log_in/
     */

    @Override
    public String toString() {
        return "BambooProperties{" +
                "packageName='" + packageName + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", job_id='" + job_id + '\'' +
                ", filterLinks='" + filterLinks + '\'' +
                ", saveAsFile=" + saveAsFile +
                ", appendFile=" + appendFile +
                ", isNppSummary=" + isNppSummary +
                ", filePath='" + filePath + '\'' +
                ", jiraTaskId='" + jiraTaskId + '\'' +
                ", file=" + file +
                ", limit=" + limit +
                ", range=" + range +
                '}';
    }
}
