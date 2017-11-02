package com.util.helper;

import com.util.BambooProperties;

import java.util.List;

/**
 * Created by rajendv3 on 4/07/2017.
 */
public interface FileSaver {
    int saveAsFile(List<String> fileContents, String fileTitle, BambooProperties properties);
}
