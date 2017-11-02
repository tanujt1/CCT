package com.util;

import com.util.Exceptions.ConcordionNotFoundException;
import com.util.Exceptions.InsufficientParamertersException;
import com.util.Exceptions.InvalidCredentialsException;
import com.util.helper.ParserHelper;

import java.io.IOException;

/**
 * Created by rajendv3 on 25/06/2017.
 */
public class ConcordionLinksParser {

    public static void main(String... args) throws InsufficientParamertersException, InvalidCredentialsException, IOException, ConcordionNotFoundException {

        BambooProperties bambooProperties = new BambooProperties();

        String username=null,jobid=null,packageName = null,filePath=null;
        boolean isSaveAsFile=false,appendFile=false,isNppSummary=false;
        String password=null;

        if(args.length==1 && (args[0].equals("-h") || args[0].equals("?"))){
            System.out.println(ParserHelper.getParserDoc());
            System.exit(0);
        }

        if(args.length<3)
            throw new InsufficientParamertersException(ParserHelper.getParserDoc());

        for (String str : args){

            String[] params = str.split("=");

            if(params.length!=2 && !"-Bpassword".equalsIgnoreCase(params[0]))
                throw new IllegalArgumentException(String.format("Invalid no of argument \"%s\" passed, argument must be of the format key=value",str));

            switch (params[0]){
                case "-Buser":
                    username = params[1];
                    break;
                case "-Bpassword":
                    password = params[1];
                    break;
                case "-BjobId":
                    jobid = params[1];
                    break;
                case "-BpackageName":
                    packageName = params[1];
                    break;
                case "-BsaveAsFile":
                    isSaveAsFile = Boolean.valueOf(params[1]);
                    break;
                case "-BfilePath":
                    filePath = params[1];
                    break;
                case "-BappendFile":
                    appendFile=Boolean.valueOf(params[1]);
                    break;
                case "-BisNppSummary":
                    isNppSummary = Boolean.valueOf(params[1]);
                    break;
                default:
                    throw new IllegalArgumentException(String.format("Invalid argument \"%s\" is passed",params[0]));
            }
        }

        bambooProperties.setPackageName(packageName);
        bambooProperties.setFilePath(filePath);
        bambooProperties.setJob_id(jobid);
        bambooProperties.setUserName(username);
        bambooProperties.setPassword(password);
        bambooProperties.setSaveAsFile(isSaveAsFile);
        bambooProperties.setAppendFile(appendFile);
        bambooProperties.setNppSummary(isNppSummary);

        System.out.println("Initializing bamboo client....");
        System.out.println("Launching bamboo client....");
        BambooClient bambooClient = new BambooClient(bambooProperties);

        bambooClient.scrapNonNPPLinks();
    }


}
