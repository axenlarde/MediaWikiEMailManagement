
package utils;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.sql.*; 

import mngt.Wiki;
import mngt.eMailSender;

/*********************************************
 * Classe contenant les methodes statiques
 *********************************************/
public class MethodesUtiles
	{
	
	public static ArrayList<String[][]> optionSetting(String option) throws Exception
		{
		ArrayList<String> listParams;
		listParams = new ArrayList<String>();
		listParams.add("config");
		listParams.add(option);
		return xmlFileReading(listParams);
		}
	
	public static String getValidEmailContent(String config)
		{
		String validConfig = getConfig(config);
		
		validConfig = validConfig.replace("\\r", "\r");
		validConfig = validConfig.replace("\\n", "\n");
		
		return validConfig;
		}
	
	public static ArrayList<String[][]> xmlFileReading(ArrayList<String> listParams) throws Exception
		{
		ArrayList<String[][]> answer;
		
		answer = xMLGear.getResultListTab(variables.getConfigFileContent(), listParams);
		return answer;
		}
	
	public static String getConfig(String config)
		{
		variables.getLogger().debug("Recherche de l'option : "+config);
		ArrayList<ArrayList<String[][]>> list;
		list = new ArrayList<ArrayList<String[][]>>();
		list.add(variables.getMisc());
		list.add(variables.getPop());
		list.add(variables.getSmtp());
		list.add(variables.getWiki());
		for(int a=0; a<list.size(); a++)
			{
			ArrayList<String[][]> listo;
			listo = list.get(a);
			for(int i=0; i<listo.get(0).length; i++)
				{
				if(listo.get(0)[i][0].compareTo(config)==0)
					{
					variables.getLogger().debug("valeure trouvée : "+listo.get(0)[i][1]);
					return listo.get(0)[i][1];
					}
				}
			}
		variables.getLogger().debug("Aucune option trouvée");
		return null;
		}
	
	public static boolean checkUser(String userEMail)
		{
		variables.getLogger().info("Vérification de l'utilisateur : "+userEMail);
		
		for(int i=0; i<variables.getUserList().size(); i++)
			{
			if(userEMail.compareTo(variables.getUserList().get(i).geteMail())==0)
				{
				variables.getLogger().info("Email approuvé, utilisateur : "+variables.getUserList().get(i).getUserId());
				return true;
				}
			}
		return false;
		}
	
	public static void fillUserList() throws Exception
		{
		if(MethodesUtiles.getConfig("getusermethod").compareTo("http") != 0)
			{
			//On récupère la liste des utilisateurs depuis la base de données
			Connection connexion = null;
			try
				{
				connexion = getDBCon();
				}
			catch(Exception exc)
				{
				exc.printStackTrace();
				variables.getLogger().error(exc);
				throw new Exception("Erreur durant la connexion à la base MYSQL");
				}
			
			try
				{
				Statement instruction = connexion.createStatement();
				ResultSet resultat = instruction.executeQuery("SELECT * from wikiuser");
				while(resultat.next())
					{
					String userid = resultat.getString("user_name");
					String email = resultat.getString("user_email");
					String realName = resultat.getString("user_real_name");
					String pass = resultat.getString("user_password");
					String passOK = "NOK";
					if(Pattern.matches(".+",pass))
						{
						passOK = "OK####";
						}
					variables.getLogger().debug("Utilisateur trouvé : UserID: "+userid+" email: "+email+" realname: "+realName+" password: "+passOK);
					if(resultat.getString("user_name").compareTo("Admin") != 0)
						{
						variables.getUserList().add(new userInfo(email,userid,pass,"","",realName));
						}
					}
				}
			catch(Exception exc)
				{
				exc.printStackTrace();
				variables.getLogger().error(exc);
				throw new Exception("Erreur durant la récupération des utilisateurs");
				}
			finally
				{
				try
					{
					variables.getLogger().debug("Fermeture de la connexion à la base de données");
					connexion.close();
					variables.getLogger().debug("Connexion fermée");
					}
				catch(Exception exc)
					{
					exc.printStackTrace();
					variables.getLogger().error(exc);
					}
				}
			}
		else
			{
			//On récupère la liste des utilisateurs depuis le wiki
			
			Wiki wiki = wikiConnection(getConfig("wikiuser"), getConfig("wikiuserpassword"));
			String[] userList = wiki.allUsers("", 100);
			wiki.logout();
			
			for(int i=0; i<userList.length; i++)
				{
				variables.getLogger().debug("UserID récupérés du wiki : "+userList[i]);
				if(userList[i].compareTo("Admin") != 0)
					{
					wiki = wikiConnection(userList[i],getConfig("wikidefaultpassword"));
					String userInfo = wiki.getUserData();
					variables.getUserList().add(getUserInfo(userInfo));
					wiki.logout();
					}
				}
			}
		}
	
	/**
	 * Method used to get DB connection
	 */
	public static Connection getDBCon() throws Exception
		{
		return dBConnection(getConfig("ipwiki"), getConfig("wikidbname"), getConfig("wikidbuser"), getConfig("wikidbpassword"));
		}
	
	/**
	 * Method used to create DB connection 
	 */
	private static Connection dBConnection(String IP, String dbName, String dbUser, String dbPass) throws Exception
		{
		try
			{
			Class.forName("com.mysql.jdbc.Driver");
			String URL = "jdbc:mysql://"+IP+":3306/"+dbName;
			variables.getLogger().debug("Connexion JDBC : "+URL);
			return DriverManager.getConnection(URL,dbUser,dbPass);
			}
		catch(Exception exc)
			{
			exc.printStackTrace();
			variables.getLogger().error(exc);
			throw new Exception("Erreur durant l'accès à la base de données : "+exc.getMessage());
			}
		}
	
	
	/***
	 * Method used to create a new userinfo object
	 */
	private static userInfo getUserInfo(String userInfo)
		{
		String eMail, userId, password, firstName, lastName, realName;
		
		eMail = parseUserInfo(userInfo, "email");
		userId = parseUserInfo(userInfo, "name");
		password = getConfig("wikidefaultpassword");
		firstName = "";
		lastName = "";
		realName = parseUserInfo(userInfo, "realname");
		
		variables.getLogger().debug("Utilisateur ajouté à la liste : EMail:"+eMail+" UserID:"+userId+" Password:"+password+" RealName: "+realName);
		return new userInfo(eMail, userId, password, firstName, lastName, realName);
		}
	
	/****
	 * Method used to parse and get user Info
	 */
	private static String parseUserInfo(String userInfo, String wantToGet)
		{
		String answer = userInfo;
		//On enlève le début
		answer = answer.substring(answer.indexOf(wantToGet+"=\"")+wantToGet.length()+2, answer.length());
		//On enlève la fin
		answer = answer.substring(0,answer.indexOf("\""));
		variables.getLogger().debug("Wanted to get : "+wantToGet+" getted : "+answer);
		return answer;
		}
	
	
	/*******
	 * Method used to establish a connection
	 * to the wiki
	 */
	public static Wiki wikiConnection(String user, String password) throws Exception
		{
		try
			{
			Wiki wiki = new Wiki(MethodesUtiles.getConfig("ipwiki")); // create a new wiki connection
		    wiki.setThrottle(5000); // set the edit throttle to 0.2 Hz
		    wiki.login(user, password); // login
		    variables.getLogger().info("Connexion au wiki");
		    return wiki;
			}
		catch(Exception exc)
			{
			exc.printStackTrace();
			variables.getLogger().error(exc);
			throw new Exception("Echec de connexion au wiki");
			}
		}
	
	/*********
	 * Method used to fill the replacement list
	 * from the configuration file
	 */
	public static void fillReplacementList() throws Exception
		{
		ArrayList<String> listParams;
		listParams = new ArrayList<String>();
		listParams.add("config");
		listParams.add("replacement");
		listParams.add("rep");
		variables.setReplacement(xmlFileReading(listParams));
		}
	
	/***************************************
	 * Method used to fill the categories list
	 * To do this, we have to get the list from
	 * the wiki.
	 ***************************************/
	public static void fillCategorieList() throws Exception
		{
		//On récupère la liste des catégories depuis le wiki
		Wiki wiki = wikiConnection(getConfig("wikiuser"), getConfig("wikiuserpassword"));
		ArrayList<String> usedCatList = wiki.getCategorieList();
		ArrayList<String> unUsedCatList = wiki.getUnusedCategorieList();
		wiki.logout();
		
		variables.getLogger().debug("Vidange de la liste de catégorie");
		variables.setCategorieList(new ArrayList<String>());
		
		variables.getLogger().debug("Catégorie trouvé dans le wiki :");
		for(int i=0; i<usedCatList.size(); i++)
			{
			variables.getLogger().debug(usedCatList.get(i));
			variables.getCategorieList().add(usedCatList.get(i));
			}
		for(int i=0; i<unUsedCatList.size(); i++)
			{
			variables.getLogger().debug(unUsedCatList.get(i));
			variables.getCategorieList().add(unUsedCatList.get(i));
			}
		variables.getLogger().debug("Nombre de catégories : "+variables.getCategorieList().size());
		}
	
	/********************************************
	 * Method used to init the class eMailsender
	 ********************************************/
	public static void initEMailServer()
		{
		String port = MethodesUtiles.getConfig("smtpemailport");
		String protocol = MethodesUtiles.getConfig("smtpemailprotocol");
		String server = MethodesUtiles.getConfig("smtpemailserver");
		String user = MethodesUtiles.getConfig("smtpemail");
		String password = MethodesUtiles.getConfig("smtpemailpassword");
		variables.seteMSender(new eMailSender(port, protocol, server, user, password));
		}
	
	/*************************************************
	 * Method used to guess the correct article title
	 ************************************************/
	public static String getArticleTitle(String title)
		{
		String newTitle = title;
		
		//On remplace les caractères spéciaux par leur équivalent HTML
		try
			{
			newTitle = stringToHTMLString(newTitle);
			}
		catch(Exception exc)
			{
			exc.printStackTrace();
			variables.getLogger().error(exc);
			newTitle = title;
			}
		
		//First space removing
		if(newTitle.substring(0, 1).compareTo(" ")==0)
			{
			newTitle = newTitle.substring(1,newTitle.length());
			}
		
		//Space replacing
		newTitle = newTitle.replace(" ", "_");
		
		variables.getLogger().debug("Titre de l'article deviné : "+newTitle);
		return newTitle;
		}
	
	
	/************************
	 * Method used to convert special char into HTML Char
	 */
	public static String stringToHTMLString(String string) throws Exception
		{
	    StringBuffer sb = new StringBuffer(string.length());
	    // true if last char was blank
	    boolean lastWasBlankChar = false;
	    int len = string.length();
	    char c;
	
	    for (int i = 0; i < len; i++)
	        {
	        c = string.charAt(i);
	        if (c == ' ') {
	            // blank gets extra work,
	            // this solves the problem you get if you replace all
	            // blanks with &nbsp;, if you do that you loss 
	            // word breaking
	            if (lastWasBlankChar) {
	                lastWasBlankChar = false;
	                sb.append("_");
	                }
	            else {
	                lastWasBlankChar = true;
	                sb.append(' ');
	                }
	            }
	        else {
	            lastWasBlankChar = false;
	            //
	            // HTML Special Chars
	            if (c == '"')
	                sb.append("&quot;");
	            else if (c == '&')
	                sb.append("&amp;");
	            else if (c == '<')
	                sb.append("&lt;");
	            else if (c == '>')
	                sb.append("&gt;");
	            else if (c == '\n')
	                // Handle Newline
	                sb.append("&lt;br/&gt;");
	            else {
	                int ci = 0xffff & c;
	                if (ci < 160 )
	                    // nothing special only 7 Bit
	                    sb.append(c);
	                else {
	                    // Not 7 Bit use the unicode system
	                    sb.append("&#");
	                    sb.append(new Integer(ci).toString());
	                    sb.append(';');
	                    }
	                }
	            }
	        }
	    return sb.toString();
		}
	
	/***
	 * Method used to determine if a file is a picture or something
	 * else
	 */
	public static boolean isPicture(String fileName)
		{
		try
			{
			variables.getLogger().debug("On cherche a savoir si le fichier "+fileName+" est une image");
			String[] fileType = fileName.split("\\.");
			String type = fileType[fileType.length-1];
			variables.getLogger().debug("Extention de fichier trouvé : "+type);
			
			String[] fileTypeallowed = getConfig("ispicture").split(",");
			
			for(int i=0; i<fileTypeallowed.length; i++)
				{
				if(fileTypeallowed[i].compareTo(type) == 0)
					{
					variables.getLogger().debug("Le fichier est bien une image");
					return true;
					}
				}
			}
		catch(Exception exc)
			{
			exc.printStackTrace();
			variables.getLogger().error(exc);
			}
		return false;
		}
	
	/**
	 * Methods used to send an email to the administrator
	 */
	public static void sendToAdminList(String sub, String cont, String desc)
		{
		try
			{
			String sendTo = new String("");
			String subject = sub;
			String content = cont;
			String eMailDesc = desc;
			
			String[] emailTab = MethodesUtiles.getConfig("smtpemailadmin").split(",");
			
			for(int j=0; j<emailTab.length; j++)
				{
				sendTo = emailTab[j];
				variables.geteMSender().send(sendTo, subject, content, eMailDesc);
				}
			}
		catch (Exception exc)
			{
			exc.printStackTrace();
			variables.getLogger().error(exc);
			variables.getLogger().error("Failed to send email to the admin list. check if the smtp server is reachable : "+exc.getMessage());
			}
		}
	
	/**
	 * Methods used to send an email to user list
	 */
	public static void sendToUserList(String sub, String cont, String desc)
		{
		try
			{
			String sendTo = new String("");
			String subject = sub;
			String content = cont;
			String eMailDesc = desc;
			
			String[] emailTab = MethodesUtiles.getConfig("smtpemailuserlist").split(",");
			
			for(int j=0; j<emailTab.length; j++)
				{
				sendTo = emailTab[j];
				variables.geteMSender().send(sendTo, subject, content, eMailDesc);
				}
			}
		catch (Exception exc)
			{
			exc.printStackTrace();
			variables.getLogger().error(exc);
			variables.getLogger().error("Failed to send email to the user list. check if the smtp server is reachable : "+exc.getMessage());
			}
		}
	
	
	
	/*2013*//*RATEL Alexandre 8)*/
	}
