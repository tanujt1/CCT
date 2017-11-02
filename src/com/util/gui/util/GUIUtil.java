package com.util.gui.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by rajendv3 on 8/07/2017.
 */
public class GUIUtil {

    public static Object[][] parseJiraLinks(List<String> links,String[] columns){
        int val = 0;
        Object[][] data = new Object[links.size()][columns.length];

        for(String link : links){
            data[val][0]= new Boolean(false);
            for (int i=1;val<links.size() && i<columns.length;i++){
                data[val++][i]=link;
            }
        }

        return data;
    }

    public static Object[][] parseLinksAndPackages(Map<Integer, String> dataMap, String packageName,String[] columns){

        int val = 1;
        Object[][] data = new Object[dataMap.size()][columns.length];

        for(Map.Entry<Integer,String> entry : dataMap.entrySet()){

            String link = entry.getValue();
            String pack = link.substring(link.indexOf(packageName),link.lastIndexOf("/"));
            String fileName = link.substring(link.lastIndexOf("/")+1);

            data[entry.getKey()][0]=val++;
            data[entry.getKey()][1]=pack.replaceAll("/",".");
            data[entry.getKey()][2]=fileName;

        }

        return data;
    }

    public static  List<String> parseLinksFromMap(List<Map<String,List<String>>> parsedLinks) {
        List<String> list = new ArrayList<>();

        for(Map<String,List<String>> item : parsedLinks){
            for(Map.Entry<String,List<String>> mapItem : item.entrySet()){
                list.addAll(mapItem.getValue());
            }
        }

        return list;
    }
}
