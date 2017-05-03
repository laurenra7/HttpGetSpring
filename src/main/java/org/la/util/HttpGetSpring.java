package org.la.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class HttpGetSpring {

    private static boolean modeVerbose;

    public static void main(String[] args) {

        int exitStatus = 0;
        modeVerbose = false;

        // Build command line options
        Options clOptions = new Options();
        clOptions.addOption(Option.builder("h")
                .longOpt("help")
                .desc("Show this help")
                .build());
        clOptions.addOption(Option.builder("o")
                .longOpt("output")
                .desc("output file")
                .hasArg()
                .argName("filename")
                .build());
        clOptions.addOption(Option.builder("v")
                .longOpt("verbose")
                .desc("show processing messages")
                .build());

        if(args.length == 0) {
            showCommandHelp(clOptions);
        }
        else {
            exitStatus = processCommandLine(args, clOptions);
        }

        System.exit(exitStatus);

    }


    private static int processCommandLine(String[] args, Options clOptions) {
        int executeStatus = 0;
        String url = "";
        String outputJson = "";

        CommandLineParser clParser = new DefaultParser();


        try {
            CommandLine line = clParser.parse(clOptions, args);

            if (line.hasOption("help")) {
                showCommandHelp(clOptions);
            }
            else {
                if (line.hasOption("verbose")) {
                    modeVerbose = true;
                }

                // Remaining command line parameter(s), if any, is URL
                List<String> cmdLineUrl = line.getArgList();
                if(cmdLineUrl.size() > 0) {
                    url = cmdLineUrl.get(0); // Get only the first parameter as URL, ignore others

                    ResponseEntity<String> response = httpGet(url);
                    if (response != null) {

                        if (line.hasOption("output")) {
                            // Write response to output file
                            executeStatus = writeStringToFile(line.getOptionValue("output"), httpResultRaw(response));
                        }
                        else {
                            // Write response to console
                            System.out.println(httpResultRaw(response));
                        }

                    }

                }
                else {
                    System.out.println("Error: no URL");
                    showCommandHelp(clOptions);
                }
            }
        }
        catch (ParseException e) {
            System.err.println("Command line parsing failed. Error: " + e.getMessage() + "\n");
            showCommandHelp(clOptions);
            executeStatus = 1;
        }

        return executeStatus;
    }


    private static ResponseEntity<String> httpGet(String url) {

        ResponseEntity<String> response = null;

        RestTemplate httpService = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.setContentType(MediaType.TEXT_PLAIN);
        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

        try {
            response = httpService.exchange(url, HttpMethod.GET, httpEntity, String.class);
        }
        catch (RestClientException e) {
            e.printStackTrace();
//            System.out.println("Error processing HTTP request. HTTP status code is " + e.getMessage());
//            System.out.println("Stack trace:");
//            System.out.println(e);
        }

        return response;
    }


    private static String httpResultRaw(ResponseEntity<String> response) {

        String resultString = "HTTP Response Code: " + String.valueOf(response.getStatusCode()) + "\n";

        HttpHeaders resultHeaders = response.getHeaders();

        resultString = resultString + "Headers:\n";
        for (Map.Entry<String, List<String>> header : resultHeaders.entrySet()) {
            resultString = resultString + header.getKey() + ": ";
            String values = "";
            for (String value : header.getValue()) {
                resultString = resultString + value + ",";
            }
            resultString.replaceAll(",$", "");
            resultString = resultString + "\n";
        }

        resultString = resultString + "Body:\n" + response.getBody();
        return resultString;
    }


    private static int writeStringToFile(String outputFilename, String outputString) {
        int status = 0;
        BufferedWriter bufferedWriter = null;
        FileWriter fileWriter = null;

        if (modeVerbose) {
            System.out.println("Output file: " + outputFilename);
        }

        try {
            fileWriter = new FileWriter(outputFilename);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(outputString);

        }
        catch (IOException e) {
            System.out.println("Problem writing to file. Error: " + e.getMessage());
            status = 1;
        }
        finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
                if (fileWriter != null) {
                    fileWriter.close();
                }
            }
            catch (IOException ioErr) {
                System.out.println("Problem closing file. Error: " + ioErr.getMessage());
                status = 1;
            }
        }

        return status;
    }


    private static void showCommandHelp(Options options) {
        String commandHelpHeader = "\nDo an HTTP GET from a URL\n\n";
        String commandHelpFooter = "\nExamples:\n\n" +
                "  java -jar httpGetTest.jar https://someurl.com/get/stuff\n\n" +
                "  java -jar httpGetTest.jar -o myfile.txt https://someurl.com/get/stuff\n\n";

        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(88,"java -jar httpGetTest.jar url", commandHelpHeader, options, commandHelpFooter, true);
    }


}
