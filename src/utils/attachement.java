
package utils;

import java.io.File;

/*********************************************
 * Class used to store attachement info
 *********************************************/
public class attachement
	{
	/**
	 * Variables
	 */
	String fileName;
	File file;
	
	/**
	 * Constructeur
	 */
	public attachement(String fileName, File file)
		{
		this.fileName = fileName;
		this.file = file;
		}

	public String getFileName()
		{
		return fileName;
		}

	public File getFile()
		{
		return file;
		}
	
	
	/*2013*//*RATEL Alexandre 8)*/
	}
