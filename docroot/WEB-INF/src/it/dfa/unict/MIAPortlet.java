package it.dfa.unict;

import it.dfa.unict.util.Constants;
import it.dfa.unict.util.Utils;
import it.infn.ct.GridEngine.Job.InfrastructureInfo;
import it.infn.ct.GridEngine.Job.MultiInfrastructureJobSubmission;
import it.infn.ct.GridEngine.JobResubmission.GEJobDescription;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.ProcessAction;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

public class MIAPortlet extends MVCPortlet {



private final Log _log = LogFactoryUtil.getLog(MIAPortlet.class);



	public static String pilotScript;

	/**
	 * Initializes portlet's configuration with pilot-script file path.
	 * 
	 * @see com.liferay.util.bridges.mvc.MVCPortlet#init()
	 */
	@Override
	public void init() throws PortletException {
		super.init();
		pilotScript = getPortletContext().getRealPath(Constants.FILE_SEPARATOR)
				+ "WEB-INF/job/" + getInitParameter("pilot-script");
	}

	/**
	 * Processes the 'multipart/form-data' form uploading file, gets the jobs
	 * label finally submits job towards a list of enabled DCI specified in the
	 * configuration.
	 * 
	 * @param actionRequest
	 * @param actionResponse
	 * @throws IOException
	 * @throws PortletException
	 */
	@ProcessAction(name = "submit")
	public void submit(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {

		AppInput appInput = new AppInput();
		PortletPreferences preferences = actionRequest.getPreferences();

		String JSONAppPrefs = GetterUtil.getString(preferences.getValue(
				Constants.APP_PREFERENCES, null));
		AppPreferences appPrefs = Utils.getAppPreferences(JSONAppPrefs);

		String JSONAppInfras = GetterUtil.getString(preferences.getValue(
				Constants.APP_INFRASTRUCTURE_INFO_PREFERENCES, null));

		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.TS_FORMAT);
		String timestamp = dateFormat.format(Calendar.getInstance().getTime());
		appInput.setTimestamp(timestamp);

		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest
				.getAttribute(WebKeys.THEME_DISPLAY);
		User user = themeDisplay.getUser();
		String username = user.getScreenName();
		appInput.setUsername(username);
		UploadPortletRequest uploadRequest = PortalUtil
				.getUploadPortletRequest(actionRequest);

		File uploadedFile = processInputFile(uploadRequest, username,
				timestamp, appInput);

		if (uploadedFile != null && uploadedFile.length() == 0) {
			SessionErrors.add(actionRequest, "empty-file");
		} else {

			if (uploadedFile != null)
				appInput.setInputSandbox(uploadedFile.getAbsolutePath());

			String joblabel = ParamUtil.getString(uploadRequest, "jobLabel");

			appInput.setJobLabel(joblabel);

			_log.info(appInput);

			List<AppInfrastructureInfo> enabledInfras = Utils
					.getEnabledInfrastructureInfo(JSONAppInfras);

			if (enabledInfras.size() > 0) {
				InfrastructureInfo infrastructureInfo[] = Utils
						.convertAppInfrastructureInfo(enabledInfras);

				submitJob(appPrefs, appInput, infrastructureInfo);

				PortalUtil.copyRequestParameters(actionRequest, actionResponse);
				actionResponse.setRenderParameter("jobLabel", joblabel);
				actionResponse
						.setRenderParameter("jspPage", "/jsps/submit.jsp");
			}
		}

		// Hide default Liferay success/error messages
		PortletConfig portletConfig = (PortletConfig) actionRequest
				.getAttribute(JavaConstants.JAVAX_PORTLET_CONFIG);
		LiferayPortletConfig liferayPortletConfig = (LiferayPortletConfig) portletConfig;
		SessionMessages.add(actionRequest, liferayPortletConfig.getPortletId()
				+ SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);
	}

	/**
	 * Method responsible of job submission.
	 * 
	 * @param preferences
	 *            Application preferences object.
	 * @param appInput
	 *            An object consists of view input fields values and other job
	 *            input parameters.
	 * @param enabledInfrastructures
	 *            List of enabled infrastructures.
	 */
	private void submitJob(AppPreferences preferences, AppInput appInput,
			InfrastructureInfo[] enabledInfrastructures) {

		// Job details
		String executable = "/bin/sh";
		String arguments = FileUtil.getShortFileName(pilotScript) + " "
				+ appInput.getInputFileName();
		String outputPath = "/tmp/";
		String outputFile = "hostname-Output.txt";
		String errorFile = "hostname-Error.txt";
		String appFile = "hostname-Files.tar.gz";

		// InputSandbox (string with comma separated list of file names)
		String inputSandbox = pilotScript + "," + appInput.getInputSandbox();
		// OutputSandbox (string with comma separated list of file names)
		String outputSandbox = appFile;

		// Take care of job requirements
		// More requirements can be specified in the preference value
		// 'jobRequirements'
		// separating each requirement by the ';' character
		// The loop prepares a string array with GridEngine/JSAGA compliant
		// requirements
		String jdlRequirements[] = preferences.getJobRequirements().split(";");
		int numRequirements = 0;
		for (int i = 0; i < jdlRequirements.length; i++) {
			if (!jdlRequirements[i].equals("")) {
				jdlRequirements[numRequirements] = "JDLRequirements=("
						+ jdlRequirements[i] + ")";
				numRequirements++;
				_log.debug("Requirement[" + i + "]='" + jdlRequirements[i]
						+ "'");
			}
		}

		// Prepare the GridEngine job description
		GEJobDescription jobDesc = new GEJobDescription();
		jobDesc.setExecutable(executable);
		jobDesc.setArguments(arguments);
		jobDesc.setOutputPath(outputPath);
		jobDesc.setOutput(outputFile);
		jobDesc.setError(errorFile);
		jobDesc.setOutputFiles(outputSandbox);
		jobDesc.setInputFiles(inputSandbox);

		// GridEngine' MultiInfrastructure job submission object
		MultiInfrastructureJobSubmission miJobSubmission = null;

		// Initialize the GridEngine MultiInfrastructure Job Submission object
		// GridEngine uses two different kind of constructors. The constructor
		// taking no database arguments is used for production environments,
		// while the constructor taking SciGwyUserTrackingDB parameters is
		// normally used for development purposes. In order to switch-on the
		// production just check 'Production Environments' in portlet
		// configuration
		if (!preferences.isProductionEnviroment()) {
			String DBNM = "jdbc:mysql://"
					+ preferences.getSciGwyUserTrackingDB_Hostname() + "/"
					+ preferences.getSciGwyUserTrackingDB_Database();
			String DBUS = preferences.getSciGwyUserTrackingDB_Username();
			String DBPW = preferences.getSciGwyUserTrackingDB_Password();
			miJobSubmission = new MultiInfrastructureJobSubmission(DBNM, DBUS,
					DBPW, jobDesc);
			_log.debug("MultiInfrastructureJobSubmission [DEVEL]\n"
					+ Constants.NEW_LINE + "    DBNM: '" + DBNM + "'"
					+ Constants.NEW_LINE + "    DBUS: '" + DBUS + "'"
					+ Constants.NEW_LINE + "    DBPW: '" + DBPW + "'");
		} else {
			miJobSubmission = new MultiInfrastructureJobSubmission(jobDesc);
			_log.debug("MultiInfrastructureJobSubmission [PROD]");
		}

		for (int i = 0; i < enabledInfrastructures.length; i++) {
			_log.debug("Adding infrastructure #" + (i + 1) + " - Name: '"
					+ enabledInfrastructures[i].getName() + "'"
					+ Constants.NEW_LINE);
			miJobSubmission.addInfrastructure(enabledInfrastructures[i]);
		}

		// GridOperations' Application Id
		int applicationId = Integer.parseInt(preferences.getGridOperationId());

		// Grid Engine' UserTraking needs the portal IP address
		String portalIPAddress = "";
		try {
			InetAddress addr = InetAddress.getLocalHost();
			byte[] ipAddr = addr.getAddress();
			portalIPAddress = "" + (short) (ipAddr[0] & 0xff) + ":"
					+ (short) (ipAddr[1] & 0xff) + ":"
					+ (short) (ipAddr[2] & 0xff) + ":"
					+ (short) (ipAddr[3] & 0xff);
		} catch (Exception e) {
			_log.error("Unable to get the portal IP address");
		}

		// Setup job requirements
		if (numRequirements > 0)
			miJobSubmission.setJDLRequirements(jdlRequirements);

		// Ready now to submit the Job
		miJobSubmission.submitJobAsync(appInput.getUsername(), portalIPAddress,
				applicationId, appInput.getJobLabel());

		// Show log
		// View jobSubmission details in the log
		_log.info(Constants.NEW_LINE + "JobSent" + Constants.NEW_LINE
				+ "-------" + Constants.NEW_LINE + "Portal address: '"
				+ portalIPAddress + "'" + Constants.NEW_LINE
				+ "Executable    : '" + executable + "'" + Constants.NEW_LINE
				+ "Arguments     : '" + arguments + "'" + Constants.NEW_LINE
				+ "Output path   : '" + outputPath + "'" + Constants.NEW_LINE
				+ "Output sandbox: '" + outputSandbox + "'"
				+ Constants.NEW_LINE + "Ouput file    : '" + outputFile + "'"
				+ Constants.NEW_LINE + "Error file    : '" + errorFile + "'"
				+ Constants.NEW_LINE + "Input sandbox : '" + inputSandbox + "'"
				+ Constants.NEW_LINE);
	}

	/**
	 * @param uploadRequest
	 * @param username
	 * @param timestamp
	 * @param appInput
	 * @return
	 * @throws IOException
	 */
	private File processInputFile(UploadPortletRequest uploadRequest,
			String username, String timestamp, AppInput appInput)
			throws IOException {

		File file = null;
		String fileInputName = "fileupload";

		String sourceFileName = uploadRequest.getFileName(fileInputName);

		if (Validator.isNotNull(sourceFileName)) {
			_log.debug("Uploading file: " + sourceFileName + " ...");

			String fileName = FileUtil.stripExtension(sourceFileName);
			_log.debug(fileName);

			appInput.setInputFileName(fileName);

			String extension = FileUtil.getExtension(sourceFileName);
			_log.debug(extension);

			// Get the uploaded file as a file.
			File uploadedFile = uploadRequest.getFile(fileInputName, true);
			File folder = new File(Constants.ROOT_FOLDER_NAME);
			// This is our final file path.
			file = new File(folder.getAbsolutePath() + Constants.FILE_SEPARATOR
					+ username + "_" + timestamp + "_" + fileName
					+ ((!extension.isEmpty()) ? "." + extension : ""));
			FileUtil.move(uploadedFile, file);

		}
		return file;
	}
}
