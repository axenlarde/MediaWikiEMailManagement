
package mngt;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utils.MethodesUtiles;
import utils.attachement;
import utils.variables;

/*********************************************
 * Class used to create an article
 *********************************************/
public class createArticle
	{
	/*****
	 * Variables
	 */
	private int userListID;
	private int eMailID;
	private String cleanedContent, from, subject;
	private Wiki wiki;
	
	
	/****
	 * Constructeur
	 */
	public createArticle(int eMailID, String from, String subject, String content) throws Exception
		{
		this.eMailID = eMailID;
		this.from = from;
		this.subject = subject;
		userListID = 0;
		this.cleanedContent = content;
		
		/******
		 * Getting userListID
		 */
		gettingUserID();
		/******/
		
		/*******
		 * Mise au propre du contenu de l'article
		 * et diverses mises en formes 
		 */
		contentCleaning();
		/********/
		
		/****
		 * Connexion au wiki
		 */
		//wiki = MethodesUtiles.wikiConnection(variables.getUserList().get(userListID).getUserId(), variables.getUserList().get(userListID).getPassword());
		//On se connecte en temps qu'administrateur
		wiki = MethodesUtiles.wikiConnection(MethodesUtiles.getConfig("wikiuser"), MethodesUtiles.getConfig("wikiuserpassword"));
		/*******/
		
		/*******
		 * Cr�ation de l'article
		 */
		create();
		/******/
		}
	
	/*******
	 * Method used to get the user's ID 
	 * in the userList.
	 */
	private void gettingUserID()
		{
		for(int i=0; i<variables.getUserList().size(); i++)
			{
			if(from.compareTo(variables.getUserList().get(i).geteMail())==0)
				{
				userListID = i;
				}
			}
		}
	
	/*********
	 * Method used to clean the article content
	 * in addition we add some format conversion
	 */
	private void contentCleaning()
		{
		String content = cleanedContent;
		
		variables.getLogger().debug("Contenu du message avant nettoiement : "+content);
		
		if(variables.geteMailList().get(eMailID).getContentType().contains("html"))
			{
			//Si l'email contient du html on ne garde que le body
			if(content.contains("<body"))
	    		{
	    		content = content.substring(content.indexOf("<body"), content.indexOf("</body>")+7);
	    		}
			}
		
		variables.getLogger().debug("Contenu du message apr�s la purge HTML : "+content);
		
		//On retire ce qui est apr�s un grand trait. Ce qui normalement indique du texte inutile g�n�r� � la fin d'un email
		if(MethodesUtiles.getConfig("removewarn").compareTo("true") == 0)
			{
			if(content.contains("___________________"))
	    		{
	    		content = content.replace(content.substring(content.indexOf("___________________"), content.length()),"");
	    		}
			}
		
		variables.getLogger().debug("Contenu du message apr�s retrait des avertissements : "+content);
		
		//On retire la signature email de l'auteur
		if(MethodesUtiles.getConfig("removesign").compareTo("true") == 0)
			{
			try
				{
				String patt = variables.getUserList().get(userListID).getRealName();
				variables.getLogger().debug("Le RealName suivant va �tre cherch� :d�but:"+patt+":fin");
				
				//On v�rifie avant de traiter, que le real name est au moins un mot de 3 caract�res
				String minimumrealname = MethodesUtiles.getConfig("minimumrealname");
				
				if(Pattern.matches(".+", patt))
					{
					if(Pattern.matches("\\w+", patt.substring(0, Integer.parseInt(minimumrealname))))
						{
						variables.getLogger().debug("Le RealName suivant est valid� : "+patt);
						Pattern regPat = Pattern.compile(patt, Pattern.CASE_INSENSITIVE);
						Matcher match = regPat.matcher(content);
						if(match.find())
							{
							content = content.replace(content.substring(content.indexOf(match.group()),content.length()),"");
							}
						}
					}
				}
			catch(Exception exc)
				{
				exc.printStackTrace();
				variables.getLogger().error(exc);
				variables.getLogger().error("Une erreur est survenue durant la suppression de la signature");
				}
			}
		
		variables.getLogger().debug("Contenu du message apr�s retrait de la signature : "+content);
		
		//On adapte les points
		content = content.replace("*\r\n", "* ");
		
		//On adapte les * et leur diff�rents niveaux (Max 10 niveaux)
		int lev = 0;
		StringBuffer star = new StringBuffer("*");
		while((Pattern.matches("(?s).* [*].*", content))&&(lev<10))
			{
			content = content.replace("   "+star, star.append("*"));
			lev++;
			}
		
		//On retire les espaces en d�but et en fin de chaque phrase.
		//On rajoute ensuite un <br> � la fin de chaque phrase
		String[] trimer = content.split("\r\n");
		for(int k=0; k<trimer.length; k++)
			{
			trimer[k] = trimer[k].trim();
			trimer[k] = trimer[k]+"<br>\r\n";
			}
		//On r�assemble l'article
		StringBuffer concatenateContent = new StringBuffer("");
		for(int k=0; k<trimer.length; k++)
			{
			concatenateContent.append(trimer[k]);
			}
		
		content = concatenateContent.toString();
		
		//On effectue les remplacements indiqu�s dans le fichier de configuration
		for(int i=0; i<variables.getReplacement().size(); i++)
			{
			variables.getLogger().debug("Remplacement de "+variables.getReplacement().get(i)[0][1]+" par "+variables.getReplacement().get(i)[1][1]);
			//La balise (?i) permet de ne pas tenir compte de la casse
			content = content.replaceAll("(?i)"+variables.getReplacement().get(i)[0][1],variables.getReplacement().get(i)[1][1]);
			}
		
		variables.getLogger().debug("Contenu du message apr�s les remplacements du fichier de config : "+content);
		
		//On ajoute une signature
		content = setSign(content);
		
		//On met l'article dans la bonne cat�gorie
		content = setCategorie(content);
		
		cleanedContent = content;
		variables.getLogger().info("Contenu d'article finale avant cr�ation : "+cleanedContent);
		}
	
	/**************
	 * Method used to add signature at the end of the article
	 */
	private String setSign(String content)
		{
		content += "\r\n\r\n\r\n''Created by "+variables.getUserList().get(userListID).getUserId()+" with "+variables.getNomProg()+" ["+variables.getVersion()+"]''<br>";
		return content;
		}
	
	/*************
	 * Method used to define article's categories
	 */
	private String setCategorie(String content)
		{
		variables.getLogger().info("Recherche de cat�gorie");
		//Va contenir toutes les cat�gories trouv�s dans l'article
		ArrayList<String> catList = new ArrayList<String>();
		
		//Get the appropriate categorie from the article content
		for(int i=0; i<variables.getCategorieList().size(); i++)
			{
			String cat = variables.getCategorieList().get(i);
			variables.getLogger().debug("Recherche de la cat�gorie : "+cat);
			
			String patt = ".*"+cat+".*";	
			Pattern regPat = Pattern.compile(patt, Pattern.CASE_INSENSITIVE);
			Matcher match = regPat.matcher(content);
			if(match.find())
				{
				variables.getLogger().info("Cat�gorie : "+cat+" trouv�e dans l'article");
				catList.add(cat);
				}
			}
		
		if(catList.size() == 0)
			{
			//Si aucune cat�gorie n'a �t� trouv� on place l'article dans la cat�gorie Divers
			content += "[[Category:Divers]]";
			}
		else
			{
			for(int i=0; i<catList.size(); i++)
				{
				content += "[[Category:"+catList.get(i)+"]]";
				}
			}
		return content;
		}
	
	/****
	 * Method used to create a new article in the wiki
	 */
	private void create() throws Exception
		{
		StringBuffer attachLink = new StringBuffer("");
		try
			{
			try
				{
				for(int i=0; i<variables.geteMailList().get(eMailID).getAttachementList().size(); i++)
					{
					attachement att = variables.geteMailList().get(eMailID).getAttachementList().get(i);
					variables.getLogger().info("Upload de la pi�ce jointe "+i+" sur le wiki");
					wiki.upload(att.getFile(), att.getFileName(), "Uploaded with article : "+variables.geteMailList().get(eMailID).getSubject(), "");
					variables.getLogger().info("Upload r�ussi");
					
					variables.getLogger().info("Ajout du lien vers la pi�ce jointe");
					//Si le fichier est une image, on l'affiche � la fin de l'article plut�t que de cr�er un simple lien 
					if(MethodesUtiles.isPicture(att.getFileName()))
						{
						attachLink.append("<br>[[Fichier:"+att.getFileName()+"]]");
						}
					else
						{
						attachLink.append("<br>Fichier attach� : [[M�dia:"+att.getFileName()+"]]");
						}
					}
				}
			catch(Exception e)
				{
				e.printStackTrace();
				variables.getLogger().error(e);
				}
			//Cr�ation de l'article
			wiki.edit(subject, cleanedContent+attachLink.toString(), "");
			}
		catch(Exception exc)
			{
			variables.getLogger().error(exc);
			throw new Exception("Echec de cr�ation de l'article");
			}
		finally
			{
			try
				{
				wiki.logout();
				}
			catch(Exception exc)
				{
				variables.getLogger().error(exc);
				throw new Exception("Echec de d�connexion du wiki");
				}
			}
		}
	
	/*2013*//*RATEL Alexandre 8)*/
	}
