package com.util.helper;

import com.util.BambooProperties;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.List;

/**
 * Created by rajendv3 on 4/07/2017.
 */
public class FileSaverImpl implements FileSaver {

    @Override
    public int saveAsFile(List<String> fileContents, String fileTitle, BambooProperties bambooProperties) {
        File file = bambooProperties.getFile();

        try{
            if(!file.exists()){
                System.out.println("File does not exists! creating new file");

                File parentDir = file.getParentFile();
                if(!parentDir.exists()){
                    parentDir.mkdirs();
                }
                if(!file.createNewFile()){
                    throw new Exception("File creation failed");
                }
            }else{
                if(!file.canWrite()){
                    throw new Exception("Cannot write to a Read only file");
                }
            }

            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file,bambooProperties.isAppendFile()));

            fileWriter.write(new Date().toString()+"\n");
            fileWriter.write("JOB ID : "+bambooProperties.getJob_id()+"\n");
            fileWriter.write("PACKAGE NAME : "+bambooProperties.getPackageName()+"\n");
            fileWriter.write("REPORT NAME : "+fileTitle+"\n");
            fileWriter.write("LINKS COUNT : "+fileContents.size()+" links \n");

            fileWriter.newLine();

            for (String link : fileContents){
                fileWriter.write(link);
                fileWriter.write("\n\n");
            }

            int i=2;
            while (i-->0){
                fileWriter.write("============================================================");
            }

            fileWriter.newLine();
            fileWriter.close();
            System.out.println(fileContents.size()+" items coppied to file "+bambooProperties.getFilePath());

            return fileContents.size();

        }catch (Exception e){

            return 0;
        }
    }
}
