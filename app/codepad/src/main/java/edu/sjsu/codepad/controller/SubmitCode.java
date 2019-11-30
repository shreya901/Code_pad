/**
 * SJSU CS 218 Fall 2019 TEAM-5
 */

package edu.sjsu.codepad.controller;
import edu.sjsu.codepad.dbconfig.CodeDb;
import edu.sjsu.codepad.models.CodeMetadata;
import edu.sjsu.codepad.models.ExecutionResult;
import edu.sjsu.codepad.utils.AwsUtils;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import java.io.*;
import java.net.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Controller
public class SubmitCode {

    private static final String EXECUTOR_SERVICE_ENV_VARNAME = "EXECUTOR_SERVICE_URL";
    
    public String renderError(Model model, String err) {
		model.addAttribute("status", err);
		return "submit_error";
    }

    @RequestMapping(value = "/submitcode", 
    		        method = RequestMethod.POST)
    public String submitCode(@RequestParam("code") MultipartFile code,
    						 @RequestParam("codeText") String codeText,
    					     @RequestParam("language") String language, 
    					   Model model) {

    	SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
    	f.setTimeZone(TimeZone.getTimeZone("UTC"));
    	String startTime = f.format(new Date());
    	
		String fileName = "";
		if (code != null && !code.isEmpty()) {
			System.out.println(code);
			String[] fileNameTokens = code.getOriginalFilename().split("\\.");
			language = fileNameTokens[1];
			fileName = code.getOriginalFilename();
		} else {
			fileName = "Solution";
			if (language.equals("java")) {
				fileName = fileName + ".java";
			} else {
				fileName = fileName + ".py";
			}
		}
    		 

//		System.out.println(fileNameTokens.length);
//		if (!fileNameTokens[1].equals(language)) {
//			System.out.println("The selected language does not match with"
//					+ " the uploaded code");
//			String err = "Your file type does not match your selected language type! "
//					+ "Unable to process your request";
//			return renderError(model, err);
//		}
    
        CodeDb db = new CodeDb();    
        db.initialize();
        Integer codeId = null;
		try {
			//codeId = db.addCode(getContent(code.getInputStream()));
			codeId = db.addCode(codeText);

		} catch (Exception e) {
			String err = "Unable to connect to database";
			return renderError(model, err);
			
		}
  
		CodeMetadata metadata = new CodeMetadata(codeId, language, fileName);
		String executorServiceUrl = System.getenv(EXECUTOR_SERVICE_ENV_VARNAME);
		if (executorServiceUrl == null || executorServiceUrl.isEmpty()) {
			String err = "Environment variable " + EXECUTOR_SERVICE_ENV_VARNAME
					+ " is not configured";
			return renderError(model, err);
		}

		final String uri = "http://" + executorServiceUrl
				+ ExecuteCode.METHOD_NAME;
		RestTemplate template = new RestTemplate();
		ExecutionResult result = template.postForObject(uri, metadata,
				ExecutionResult.class);
		String endTime = f.format(new Date());
		model.addAttribute("showResult", true);
		model.addAttribute("gatewayServiceIP", AwsUtils.getMyPublicIP());
		model.addAttribute("executorServiceIP", result.getExecutor());
		model.addAttribute("StartTime", startTime);
		model.addAttribute("endTime", endTime);
		model.addAttribute("codeText", codeText);
		if (result.getStatus()) {
			model.addAttribute("log", result.getInfoLogs());
		}
		else {
			model.addAttribute("log", result.getErrorLogs());			
		}
		return "index";
    }
    
/*
	@RequestMapping(value = "/submitcode", method = RequestMethod.POST)
	public ExecutionResult submitCode(@RequestParam("code") MultipartFile code,
			@RequestParam("language") String language) {
		System.out.println("Code suhmitted: " + 
			code.getOriginalFilename() + " in " + language);

		CodeDb db = new CodeDb();
		db.initialize();
		Integer codeId = null;
		try {
			codeId = db.addCode(getContent(code.getInputStream()));
		} catch (IOException e) {
			return null;
		}
		System.out.println("Inserted in DB and generated code id:" + codeId);

		CodeMetadata metadata = new CodeMetadata(codeId, language, code.getOriginalFilename());

		String executorServiceUrl = System.getenv(EXECUTOR_SERVICE_ENV_VARNAME);
		if (executorServiceUrl == null || executorServiceUrl.isEmpty()) {
			ExecutionResult result = new ExecutionResult();
			result.setStatus(false);
			result.setErrorLogs("Environment variable " + 
			EXECUTOR_SERVICE_ENV_VARNAME + " is not configured.");
			return result;
		}

		final String uri = "http://" + executorServiceUrl + ExecuteCode.METHOD_NAME;
		RestTemplate template = new RestTemplate();
		ExecutionResult result = template.postForObject(uri, metadata, ExecutionResult.class);
		result.setExecutorGateway(AwsUtils.getMyPublicIP());
		return result;
	}
*/

	private String getContent(InputStream inputStream) {
		String result = new BufferedReader(new InputStreamReader(inputStream)).lines()
				.collect(Collectors.joining("\n"));
		return result;
	}
}
