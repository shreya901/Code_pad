/**
 * SJSU CS 218 Fall 2019 TEAM-5
 */

package edu.sjsu.codepad.models;

public class ExecutionResult {

    private boolean status = false;
    private String errorLogs;
    private String infoLogs;
    private String executor; // Worker who executed the code
    
    public ExecutionResult() {
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setErrorLogs(String logString) {
        errorLogs = logString;
    }

    public void setInfoLogs(String logString) {
        infoLogs = logString;
    }

    public boolean getStatus() {
        return status;
    }

    public String getErrorLogs() {
        return errorLogs;
    }

    public String getInfoLogs() {
        return infoLogs;
    }

	public String getExecutor() {
		return executor;
	}

	public void setExecutor(String executor) {
		this.executor = executor;
	}
}
