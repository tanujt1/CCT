package com.util.helper;

/**
 * Created by rajendv3 on 25/06/2017.
 */
public class ParserHelper {

    final static String parserDoc = "Bamboo parser options\n" +
            "\n" +
            "\t-Buser \t\t\t\t Bamboo username * \n" +
            "\t-Bpassword\t\t\t Bamboo password  *\n" +
            "\t-BjobId\t\t\t\t Bamboo build job id *\n" +
            "\t-BpackageName\t\t\t Package name to filter links (if null returns everthing) e.g mpe/npp (case Sensitive)\n" +
            "\t-BsaveAsFile\t\t\t Saves the output to a file (true/false)\n" +
            "\t-BfilePath\t\t\t Path to the file to be saved (defaultPath= \"C:\\\")\n" +
            "\t-BappendFile\t\t\t Append contents to file instead of writing to them (true/false)\n" +
            "\t-BisNppSummary\t\t\t Find npp concordions from NPPTestSummary report (true/false)\n" +
            "\t\n" +
            "Options marked with * are mandatory";

    public static String getParserDoc(){
        return parserDoc;
    }
}
