package com.util.parser;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.converters.FileConverter;
import com.util.BambooClient;
import com.util.BambooProperties;
import com.util.Exceptions.ConcordionNotFoundException;
import com.util.Exceptions.InvalidCredentialsException;
import com.util.Exceptions.InvalidJiraIdException;
import com.util.validators.JiraTaskIdValidator;
import com.util.webclients.JiraClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.LogManager;

/**
 * Created by rajendv3 on 27/06/2017.
 */
public class CmdLineParser {

    @Parameter(names = {"-u", "-Buser"}, description = "Bamboo username", required = true)
    private String username;

    @Parameter(names = {"-pwd", "-Bpassword"}, description = "Bamboo password", password = true)
    private String password;

    @Parameter(names = {"-j", "-BjobId"}, description = "Bamboo build job id", required = true)
    private String jobId;

    @Parameter(names = {"-p", "--package"}, description = "Name of the package to be collected. will collect all links if not specified")
    private String packageName = "";

    @Parameter(names = {"-s", "--save"}, description = "Save results to a file")
    private String saveAsFile = "false";

    @Parameter(names = {"-f", "--file"}, description = "File path to be saved", converter = FileConverter.class)
    private File file = new File("C:/");

    @Parameter(names = {"-a", "--append"}, description = "Append links to file")
    private String appendFile = "false";

    @Parameter(names = {"-npp"}, description = "Include links from NPP test summary")
    private String includeNpp = "false";

    @Parameter(names = {"-jra", "--jira"}, description = "Jira Task id", validateWith = JiraTaskIdValidator.class)
    private String jiraId;

    // Multiple jiraID
    /*@Parameter(names = {"-jra", "--jira"}, description = "Jira Task id", validateWith = JiraTaskIdValidator.class)
    private List<String> jiraIdList;*/

    @Parameter(names = {"-m", "--message", "--preface"}, description = "Jira Comment message")
    private String preface = "Concordion Links";

    @Parameter(names = {"-v", "--verbose"}, description = "Prints every log statements")
    private String verbose;

    @Parameter(names = {"-l", "--limit"}, description = "Limit the links")
    private int limit = 0;

    @Parameter(names = {"--range"}, description = "displays the links between the range", variableArity = true)
    private List<Integer> range = new ArrayList<Integer>() {{
        add(0);
    }};

    @Parameter(names = {"-i", "--include"}, description = "Filter links by success/failed/all")
    private String filterLinks = "all";

    public static void main(String... args) throws IOException, InvalidCredentialsException, ConcordionNotFoundException {

        CmdLineParser parser = new CmdLineParser();
        JCommander jCommander = new JCommander(parser);

        List<String> Args = new ArrayList<>();

        for (String string : args) {
            String[] params = string.split("=");

            if (params.length == 1 && Arrays.asList(new String[]{"-v", "--verbose"}).contains(params[0])) {
                Args.addAll(Arrays.asList(params));
                Args.add(" ");
            } else if (params.length == 1 && Arrays.asList(new String[]{"-Dpwd"}).contains(params[0])) {
                parser.password = params[1];
            } else
                Args.addAll(Arrays.asList(params));
        }

        List<String> helperArgs = new ArrayList<String>() {{
            add("-h");
            add("?");
            add("--usage");
            add("--help");
        }};

        if (args.length == 1 && helperArgs.contains(args[0])) {
            jCommander.usage();
            System.out.println("Options marked with * are mandatory");
            System.exit(0);
        }
        String[] arg = Args.toArray(new String[Args.size()]);

        try {
            jCommander.parse(arg);
        } catch (InvalidJiraIdException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        } catch (ParameterException e) {
            System.out.println("Missing parameters. Parameters marked with * are mandatory");
            jCommander.usage();
            System.exit(0);
        }

        if (parser.verbose == null) {
            LogManager.getLogManager().reset();
        }

        BambooClient client = new BambooClient(parser.setProperties());

        client.loginToBamboo();

        List<String> scrappedLinks = null;

        scrappedLinks = client.scrapLinks();

        if (parser.jiraId != null) {
            JiraClient jiraClient = new JiraClient(parser.setProperties());
            jiraClient.addComment(parser.preface, scrappedLinks);
        }

        // jira List
     /*   List<String> jira = parser.jiraIdList;
        if (!(jira.isEmpty() && jira == null)) {
            for (String jiraTaskId : jira) {
                properties.setJiraTaskId(jiraTaskId);
                JiraClient jiraClient = new JiraClient(parser.setProperties());
                jiraClient.addComment(parser.preface, scrappedLinks);
            }
        }*/
    }


    //private BambooProperties setProperties(String... properties) {
    private BambooProperties setProperties() {
        BambooProperties properties = new BambooProperties();

        properties.setUserName(username);
        properties.setPassword(password);
        properties.setPackageName(packageName);
        properties.setJob_id(jobId);
        properties.setFile(file);
        properties.setSaveAsFile(Boolean.valueOf(saveAsFile));
        properties.setNppSummary(Boolean.valueOf(includeNpp));
        properties.setAppendFile(Boolean.valueOf(appendFile));


        properties.setJiraTaskId(jiraId);
        properties.setRange(range);
        properties.setLimit(limit);
        properties.setFilterLinks(filterLinks);

        return properties;
    }

}
