package de.hupc.sources;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Implements a polling BatchFileSource.
 * Parameter:
 *   directory 			- a String naming the directory for polling
 *   filenamePattern	- a regular expression for filtering the files
 *   listener 			- an implementation of BatchFileSourceListener
 *    
 * @author oliverzein
 *
 */
public class BatchFileSource implements Job {
	/**
	 * The Logger, you know :-)
	 */
	static Logger logger = Logger.getLogger("de.hupc");
	/**
	 * The directory to look for files
	 */
	private static String directory = null;
	/**
	 * The filename(s) to look for. Can also contain wildcards.
	 */
	private static String filenamePattern = null;
	/**
	 * The listener wich gets notified when new files where found. 
	 */
	private static BatchFileSourceListener listener = null;
	/**
	 * The Hashmap containing the files found in the last run.
	 */
	private static HashMap<String, Long> fileRepository = null;
	/**
	 * Is another instance of BatchFileSource processing files?
	 */
	private static boolean isProcessing = false;
	
	public BatchFileSource() {		
	}
	
	void notifyListener(ArrayList<String> changedFiles) throws Exception {
		if(listener != null) {			
			listener.filesAvailable(changedFiles);
		}
	}

	/**
	 * Gets called by job scheduling system. 
	 */
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// Checck if another instance is already running. If so, stop processing.
		if(isProcessing) {
			logger.debug("Another instance is processing - skipping this run.");
			return;
		}		
		try {
			isProcessing = true;
			if(directory == null) {
				// run first time initialization
				fileRepository = new HashMap<String, Long>();
				JobDataMap dataMap = context.getJobDetail().getJobDataMap();
				directory = dataMap.getString("directory");
				filenamePattern = dataMap.getString("filenamePattern");
				listener = (BatchFileSourceListener)dataMap.get("listener");
			}
			
			// do the re-occuring tasks		
			this.compareFileList();
			
			// test blocking behaviour
			//Thread.sleep(20000);
		} catch (Exception e) {
			logger.error(e, e);
			throw(new JobExecutionException(e));
		} finally {
			isProcessing = false;
		}
		
		logger.debug("BatchFileSource thread finished executing ...");
	}
	
	/**
	 * Check for new or changed files.
	 * @throws Exception
	 */
	private void compareFileList() throws Exception {
		logger.debug("Entering compareFileList()");
		ArrayList<String> changedFiles = new ArrayList<String>();
		File dir = new File(directory);
		if(!dir.isDirectory()) {
			throw(new Exception(directory + " is not a directory."));
		}
				
		File[] list = dir.listFiles(new FileFilter());
		for (int i = 0; i < list.length; i++) {
			// check if file was there before.
			if(fileRepository.containsKey(list[i].getPath())) {
				// file is already in repository
				logger.debug("Found file " + list[i].getPath());
				// check if file has chnged since last run
				long timestamp = fileRepository.get(list[i].getPath());
				if(list[i].lastModified() > timestamp) {					
					logger.debug("File " + list[i].getPath() + " has new modification time.");
					// check if file is locked
					if(list[i].canWrite()) {
						// update new file modification time into repository 
						fileRepository.put(list[i].getPath(), new Long(list[i].lastModified()));
						changedFiles.add(list[i].getPath());
					}
				}
			} else {
				// File is not yet in repository. Put it in repository. 
				logger.debug("New file found: " + list[i].getPath());
				fileRepository.put(list[i].getPath(), new Long(list[i].lastModified()));
				changedFiles.add(list[i].getPath());				
			}			
		}
		
		// notify listener about new or changed files.
		if(changedFiles.size() > 0) {
			this.notifyListener(changedFiles);
		}
	}
	
	/**
	 * Filter construct for this BatchFileSource 
	 * @author oliverzein
	 *
	 */
	class FileFilter implements FilenameFilter {
		public boolean accept(File dir, String fileName) {
			return fileName.matches(filenamePattern);
		}
	}
}