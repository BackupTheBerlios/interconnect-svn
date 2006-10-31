package de.hupc.sources;

import java.util.ArrayList;

public interface BatchFileSourceListener {
	/**
	 * Gets called whenever new files are available.
	 * @param fileNames
	 */
	public void filesAvailable(ArrayList<String> fileNames);
}
