package it.dfa.unict;

import it.dfa.unict.util.Constants;

public class AppPreferences {
	private String fgHost = "";
	private int fgPort;
	private String pilotScript = "";
	private int applicationId;
	private int applicationDescription;

	public int getApplicationDescription() {
		return applicationDescription;
	}

	public void setApplicationDescription(int applicationDescription) {
		this.applicationDescription = applicationDescription;
	}

	public String getFgHost() {
		return fgHost;
	}

	public void setFgHost(String fgHost) {
		this.fgHost = fgHost;
	}

	public int getFgPort() {
		return fgPort;
	}

	public void setFgPort(int fgPort) {
		this.fgPort = fgPort;
	}

	public String getPilotScript() {
		return pilotScript;
	}

	public void setPilotScript(String pilotScript) {
		this.pilotScript = pilotScript;
	}

	/**
	 * Returns a text string containing the AppPreferences values dump
	 * 
	 * @return String with HTML statements inside a 'table' block
	 * 
	 * @see AppPreferences
	 */
	public String dump() {
		String dump = Constants.NEW_LINE + "Preference values:"
				+ Constants.NEW_LINE + "-----------------" + Constants.NEW_LINE
				+ "Futuregateway host              : '" + fgHost + "'"
				+ Constants.NEW_LINE + "Futuregateway port            : '"
				+ fgPort + "'" + Constants.NEW_LINE
				+ "pilotScript                  : '" + pilotScript + "'";
		return dump;
	} // dumpPreferences

	public int getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(int applicationId) {
		this.applicationId = applicationId;
	}

}
