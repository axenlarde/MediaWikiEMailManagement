
package utils;

import java.util.ArrayList;

import mngt.eMailSender;

import org.apache.log4j.Logger;

/*********************************************
 * Classe contenant les variables statiques
 *********************************************/
public class variables
	{
	/**
	 * Listes des variables statiques
	 */
	private static String nomProg;
	private static String version;
	private static Logger logger; 
	private static String configFileContent;
	private static ArrayList<emailContent> eMailList;
	private static ArrayList<String[][]> wiki, misc, pop, smtp, replacement;
	private static ArrayList<userInfo> userList;
	private static ArrayList<String> categorieList;
	private static eMailSender eMSender;
	
	
	
	/**
	 * Contructeur
	 */
	public variables()
		{
		wiki = new ArrayList<String[][]>();
		misc = new ArrayList<String[][]>();
		pop = new ArrayList<String[][]>();
		smtp = new ArrayList<String[][]>();
		userList = new ArrayList<userInfo>();
		eMailList = new ArrayList<emailContent>();
		replacement = new ArrayList<String[][]>();
		categorieList = new ArrayList<String>();
		}
	
	/****
	 * Getters and Setters
	 */

	public static String getNomProg()
		{
		return nomProg;
		}

	public static void setNomProg(String nomProg)
		{
		variables.nomProg = nomProg;
		}

	public static String getVersion()
		{
		return version;
		}

	public static void setVersion(String version)
		{
		variables.version = version;
		}

	public static Logger getLogger()
		{
		return logger;
		}

	public static void setLogger(Logger logger)
		{
		variables.logger = logger;
		}

	public static ArrayList<String[][]> getWiki()
		{
		return wiki;
		}

	public static void setWiki(ArrayList<String[][]> wiki)
		{
		variables.wiki = wiki;
		}

	public static ArrayList<String[][]> getMisc()
		{
		return misc;
		}

	public static void setMisc(ArrayList<String[][]> misc)
		{
		variables.misc = misc;
		}

	public static ArrayList<String[][]> getPop()
		{
		return pop;
		}

	public static void setPop(ArrayList<String[][]> pop)
		{
		variables.pop = pop;
		}

	public static ArrayList<String[][]> getSmtp()
		{
		return smtp;
		}

	public static void setSmtp(ArrayList<String[][]> smtp)
		{
		variables.smtp = smtp;
		}

	public static String getConfigFileContent()
		{
		return configFileContent;
		}

	public static void setConfigFileContent(String configFileContent)
		{
		variables.configFileContent = configFileContent;
		}

	public static ArrayList<emailContent> geteMailList()
		{
		return eMailList;
		}

	public static void seteMailList(ArrayList<emailContent> eMailList)
		{
		variables.eMailList = eMailList;
		}

	public static ArrayList<userInfo> getUserList()
		{
		return userList;
		}

	public static void setUserList(ArrayList<userInfo> userList)
		{
		variables.userList = userList;
		}

	public static ArrayList<String[][]> getReplacement()
		{
		return replacement;
		}

	public static void setReplacement(ArrayList<String[][]> replacement)
		{
		variables.replacement = replacement;
		}

	public static ArrayList<String> getCategorieList()
		{
		return categorieList;
		}

	public static void setCategorieList(ArrayList<String> categorieList)
		{
		variables.categorieList = categorieList;
		}

	public static eMailSender geteMSender()
		{
		return eMSender;
		}

	public static void seteMSender(eMailSender eMSender)
		{
		variables.eMSender = eMSender;
		}
	
	
	/*****
	 * End of getters and Setters 
	 */
	
	
	
	
	
	
	
	
	/*2013*//*RATEL Alexandre 8)*/
	}
