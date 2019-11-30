/**
 * SJSU CS 218 Fall 2019 TEAM-5
 */

package edu.sjsu.codepad.controller;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import java.nio.file.Files;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import edu.sjsu.codepad.dbconfig.CodeDb;
import edu.sjsu.codepad.models.CodeMetadata;
import edu.sjsu.codepad.models.ExecutionResult;
import edu.sjsu.codepad.utils.AwsUtils;


@RestController
public class ExecuteCode {

    public static final String METHOD_NAME = "/executecode";

    @ResponseBody
    @RequestMapping(value = METHOD_NAME, method = RequestMethod.POST)
    public ExecutionResult executeCode(@RequestBody CodeMetadata codeMetadata) throws IOException {
        System.out.println(codeMetadata.toString());
        int id = codeMetadata.getId();
        String type = codeMetadata.getType();
        CodeDb db = new CodeDb();
        db.initialize();
        String codetext = null;

        codetext = db.searchCode(id);

        String filename = codeMetadata.getFilename();

        Path pathToFile = Paths.get(filename);
        ExecutionResult er = new ExecutionResult();
        Files.deleteIfExists(pathToFile);

        try (
                final BufferedWriter writer = Files.newBufferedWriter(pathToFile,
                        StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        ) {

            writer.write(codetext);
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        try {
            if (type.equals(new String("java"))) {
                String className = pathToFile.toString().split("\\.")[0];

                er = runProcess("javac " + pathToFile.toString());
                System.out.println("**********");
                if (er.getStatus()) {
                    ExecutionResult er1 = new ExecutionResult();
                    er1 = runProcess("java " + className);
                    if (!er1.getStatus())
                        db.updateResult(id, er1.getErrorLogs());
                    else
                        db.updateResult(id, er1.getInfoLogs());
                    return er1;

                } else {
                    db.updateResult(id, er.getErrorLogs());
                }
            } else {
                er = runProcess("python " + pathToFile.toString());
                if (!er.getStatus())
                    db.updateResult(id, er.getErrorLogs());
                else
                    db.updateResult(id, er.getInfoLogs());
                System.out.println("**********");

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return er;

    }

    private static ExecutionResult runProcess(String command) throws Exception {
        Process pro = Runtime.getRuntime().exec(command);
        String info = printLines(command + " stdout:", pro.getInputStream());
        String error = printLines(command + " stderr:", pro.getErrorStream());
        pro.waitFor();
        System.out.println(command + " exitValue() " + pro.exitValue());
        ExecutionResult er = new ExecutionResult();

        if (pro.exitValue() == 0) {
            er.setStatus(true);
            er.setInfoLogs(info);
        } else {
            er.setStatus(false);
            er.setErrorLogs(error);
        }
        er.setExecutor(AwsUtils.getMyPublicIP());
        return er;
    }

    private static String printLines(String cmd, InputStream ins) throws Exception {
        String line = null;
        String resultString = "";
        BufferedReader in = new BufferedReader(
                new InputStreamReader(ins));
        while ((line = in.readLine()) != null) {
            System.out.println(cmd + " " + line);
            resultString += line;
        }
        return resultString;
    }
}
