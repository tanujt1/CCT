package com.util;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.util.Exceptions.ConcordionNotFoundException;
import com.util.Exceptions.InvalidCredentialsException;
import com.util.helper.FileSaver;
import com.util.helper.FileSaverImpl;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rajendv3 on 25/06/2017.
 */
public class BambooClient {

    private final WebClient WEB_CLIENT = new WebClient(BrowserVersion.INTERNET_EXPLORER_11);

    private BambooProperties bambooProperties;

    private FileSaver fileSaver;

    private static boolean loginSucces;

    private List<String> filterLinks = new ArrayList<String>() {{
        add("success");
        add("failure");
    }};


    public BambooClient() {
        bambooProperties = new BambooProperties();
        WEB_CLIENT.getCookieManager().setCookiesEnabled(true);
    }

    public BambooClient(BambooProperties bambooProperties) {
        this.bambooProperties = bambooProperties;
        WEB_CLIENT.getCookieManager().setCookiesEnabled(true);
        WEB_CLIENT.getOptions().setUseInsecureSSL(true);
        WEB_CLIENT.getOptions().setThrowExceptionOnScriptError(false);
        fileSaver = new FileSaverImpl();
        System.out.println("Bamboo Client started....");
    }

    public BambooProperties getBambooProperties() {
        return bambooProperties;
    }

    public void setBambooProperties(BambooProperties bambooProperties) {
        this.bambooProperties = bambooProperties;
    }

    public boolean loginToBamboo() throws InvalidCredentialsException, IOException {
        System.out.println("Logging in to bamboo...");
        try {
            String LOGIN_URL = "https://bamboo.service.anz/userlogin!default.action?os_destination=%2Fstart.action";

            HtmlPage loginPage = WEB_CLIENT.getPage(LOGIN_URL);

            HtmlForm loginForm = loginPage.getFirstByXPath("//form[@id='loginForm']");

            loginForm.getInputByName("os_username").setValueAttribute(bambooProperties.getUserName());
            loginForm.getInputByName("os_password").setValueAttribute(bambooProperties.getPassword());

            // https://bamboo.service.anz/allPlans.action
            HtmlPage afterLogin = loginForm.getInputByName("save").click();

            HtmlDivision temp = afterLogin.getFirstByXPath("//div[@class='aui-message error']");

            String error = temp != null ? temp.getFirstChild().getNextSibling().getFirstChild().asText() : null;

            if (error != null) {
                throw new InvalidCredentialsException(error);
            }

            System.out.println("login successfull....");
            loginSucces = true;
        } catch (FailingHttpStatusCodeException e) {
            loginSucces = false;
        }

        return loginSucces;
    }

    // URL = ../concordion/testSummary.html
    private String get(String URL) {
        System.out.println("fetching webpage....");
        try {
            return WEB_CLIENT.getPage(URL).getWebResponse().getContentAsString();
        } catch (FailingHttpStatusCodeException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, List<String>> scrapNonNPPLinks() throws InvalidCredentialsException, IOException, ConcordionNotFoundException {
        return parseTestSummaryReport();
    }

    public Map<String, List<String>> scrapNppLinks() throws ConcordionNotFoundException {
        return parseNppTestSummaryReport();
    }

    private List<String> parseReport(String reportText) throws ConcordionNotFoundException {

        String baseUrl = prepareBaseUrl(bambooProperties.getJob_id());

        //    String url = baseUrl + reportText; // ..concordion/ + testSummary.html
        String baseUrlUptoConcordion = "";

        baseUrlUptoConcordion = getupToConcordionPage();

        String finalConcordionPageURL = get(baseUrlUptoConcordion + reportText);

        // parsing with the use of Jsoup
        List<String> htmlLinks = parseLinks(finalConcordionPageURL, baseUrl);

        if (htmlLinks == null || htmlLinks.isEmpty()) {
            System.out.println("No links found. Exiting the program....");
            System.exit(0);
        }

        int start = 0;
        int end = htmlLinks.size();

        if (bambooProperties.getRange() != null && bambooProperties.getRange().size() >= 2) {
            start = bambooProperties.getRange().get(0);
            end = bambooProperties.getRange().get(1);
        }

        htmlLinks = htmlLinks.subList(start, end);

        if (bambooProperties.isSaveAsFile()) {
            System.out.println("Writing to file " + bambooProperties.getFilePath() + "....");
            saveAsFile(htmlLinks, reportText);
        } else {
            for (String link : htmlLinks) {
                System.out.println(link);
            }
        }

        return htmlLinks;
    }

    // This method parse .../artifact page and gets the concordion link under a[href]
    private String getupToConcordionPage() throws ConcordionNotFoundException {

        boolean concordion = false;
        // baseUrl = ../artifact
        String baseUrl = prepareBaseUrl(bambooProperties.getJob_id());
        String testReport = get(baseUrl);
        Elements elements = Jsoup.parse(testReport).select("a[href]");
        String concordionPageToBeParsed = "";
        for (Element element : elements) {

            if (element.text().equalsIgnoreCase("concordion")) {
                concordionPageToBeParsed = element.attr("href");
                concordion = true;
            }
        }
        if (concordion == false) {
            throw new ConcordionNotFoundException();

        }
        String[] splitUrl = concordionPageToBeParsed.split("/");
        String baseUrlUptoConcordion = "https://bamboo.service.anz/";
        for (String splittedElement : splitUrl) {
            if (!splittedElement.equalsIgnoreCase("index.html")) {
                baseUrlUptoConcordion += splittedElement + "/";
            }
        }
        return baseUrlUptoConcordion;
    }

    // NonNpp
    private Map<String, List<String>> parseTestSummaryReport() throws ConcordionNotFoundException {
        Map<String, List<String>> map = new HashMap<>();

        System.out.println("Parsing TestSummary....");
        map.put("TestSumamry.html", parseReport("testSummary.html"));

        return map;
    }

    // Npp
    private Map<String, List<String>> parseNppTestSummaryReport() throws ConcordionNotFoundException {
        Map<String, List<String>> map = new HashMap<>();

        System.out.println("Parsing NppTestSummary....");
        map.put("NPPTestSummary.html", parseReport("NPPTestSummary.html"));

        return map;
    }

    private List<String> parseLinks(String testSummaryPage, String baseUrl) {

        Elements elements = Jsoup.parse(testSummaryPage).select("a[href]");
        List<String> htmlLinks = new ArrayList<>();

        for (Element element : elements) {
            String elementLink = element.attr("href");
            String elementClass = element.className();

            if (elementLink != null && elementLink.contains(bambooProperties.getPackageName())) {

                if (!bambooProperties.getFilterLinks().equals("all")) {
                    if (filterLinks.contains(elementClass) && StringUtils.equalsIgnoreCase(elementClass, bambooProperties.getFilterLinks()))
                        htmlLinks.add(baseUrl + elementLink);
                } else {
                    htmlLinks.add(baseUrl + elementLink);
                }
            }
        }

        return htmlLinks;
    }

    private int saveAsFile(List<String> htmlLinks, String reportLink) {
        return fileSaver.saveAsFile(htmlLinks, reportLink, bambooProperties);
    }

    private String prepareBaseUrl(String ptmJobName) {
        // String baseURL = "https://bamboo.service.anz/browse/%s/artifact/";
        String baseURL = "https://bamboo.service.anz/browse/%s/artifact/";

        return String.format(baseURL, ptmJobName);
    }

    public boolean isLoginSucces() {
        return loginSucces;
    }

    public void setLoginSucces(boolean loginSucces) {
        this.loginSucces = loginSucces;
    }

    public List<String> scrapLinks() throws ConcordionNotFoundException {

        List<String> list = new ArrayList<>();

        try {

            // key is the html page eg. TestSummary.html and value is the list containing all the html links
            Map<String, List<String>> links = scrapNonNPPLinks();

            for (Map.Entry<String, List<String>> entry : links.entrySet()) {
                list.addAll(entry.getValue());
            }
            if (bambooProperties.isNppSummary())
                list.addAll(scrapNppLinks().get(0));

        } catch (InvalidCredentialsException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}

