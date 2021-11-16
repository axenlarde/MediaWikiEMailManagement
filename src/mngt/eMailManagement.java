
package mngt;

import utils.MethodesUtiles;
import utils.emailContent;
import utils.variables;

/*********************************************
 * Class used to manage EMail reception
 * The class used POP3 protocol to get new email
 * from the email server
 *********************************************/
public class eMailManagement extends Thread
	{
	/**
	 * Variables
	 */
	
	
	
	
	
	/***
	 * Constructeur
	 */
	public eMailManagement()
		{
		start();
		}
	
	
	public void run()
		{
		while(true)
			{
			try
				{
				/****************************************
				 * Récupération des emails en pop3
				 * Les emails sont placés dans une arrayList
				 ***************************************/
				fillEMailList.fill();
				/********/
				
				/*************************************
				 * On récupère la liste des utilisateurs à chaque passage.
				 * Ainsi si un nouvel utilisateur est créé, il n'est pas
				 * nécessaire de redémarrer le programme pour qu'il soit
				 * pris en compte
				 *******************/
				MethodesUtiles.fillUserList();
				/******************************/
				
				/*******************************************************
				 * De la même façon, on récupère la liste des catégories
				 ****************/
				MethodesUtiles.fillCategorieList();
				/**************************************/
				
				/****
				 * Pour chaque email on va initier le processus de création
				 * d'un article sur le wiki
				 */
				for(int i=0; i<variables.geteMailList().size(); i++)
					{
					/**********
					 * On vérifie si l'email a été envoyé par
					 * un utilisateur connu
					 */
					if(MethodesUtiles.checkUser(variables.geteMailList().get(i).getFrom()))
						{
						/***********
						 * Création de l'article
						 */
						try
							{
							variables.getLogger().info("Création de l'article");
							new createArticle(i, variables.geteMailList().get(i).getFrom(), variables.geteMailList().get(i).getSubject(), variables.geteMailList().get(i).getContent());
							/*********/
							/*********
							 * Article créé avec succès
							 */
							variables.getLogger().info("Article correctement crée. Envoi d'un email");
							/********/
							
							//Envoi d'un email à l'utilisateur
							StringBuffer cont = new StringBuffer(MethodesUtiles.getValidEmailContent("emailusercreationsuccess"));
							String sendTo = variables.geteMailList().get(i).getFrom();
							String subject = "INFO : Article créé";
							String eMailDesc = "emailusercreationsuccess";
							
							String articleUrl = MethodesUtiles.getConfig("urlwiki")+MethodesUtiles.getArticleTitle(variables.geteMailList().get(i).getSubject());
							
							cont.append("\r\n\r\n");
							cont.append("Il a pour sujet : "+variables.geteMailList().get(i).getSubject()+"\r\n\r\n");
							cont.append("Vous pourrez le retrouver à l'adresse suivante : "+articleUrl+"\r\n\r\n");
							cont.append("Cordialement");
							variables.geteMSender().send(sendTo, subject, cont.toString(), eMailDesc);
							
							//Envoi d'un eMail à la liste d'utilisateur
							cont = new StringBuffer(MethodesUtiles.getValidEmailContent("emailcreationsuccess"));
							subject = "INFO : Article créé";
							eMailDesc = "emailcreationsuccess";
							
							cont.append("\r\n\r\n");
							cont.append("Article créé par : "+variables.geteMailList().get(i).getFrom()+"\r\n");
							cont.append("Sujet de l'article : "+variables.geteMailList().get(i).getSubject()+"\r\n");
							cont.append("Il est possible de le visualiser à l'adresse suivante : "+articleUrl+"\r\n\r\n");
							MethodesUtiles.sendToUserList(subject, cont.toString(), eMailDesc);
							}
						catch(Exception exc)
							{
							exc.printStackTrace();
							variables.getLogger().error(exc);
							
							//Une erreur s'est produite durant la création de l'article, on envoi donc un eMail d'avertissement
							//Envoi d'un eMail à l'utilisateur
							String content = new String(MethodesUtiles.getValidEmailContent("emailusercreationerror"));
							String sendTo = variables.geteMailList().get(i).getFrom();
							String subject = "ERREUR : Erreur système durant la création";
							String eMailDesc = "emailusercreationerror";
							variables.geteMSender().send(sendTo, subject, content, eMailDesc);
							
							//Envoi d'un eMail à la liste d'admin
							StringBuffer cont = new StringBuffer(MethodesUtiles.getValidEmailContent("emailcreationerror"));
							subject = "Avertissement : ERREUR : Erreur système durant la création";
							eMailDesc = "emailcreationerror";
							
							cont.append("\r\n\r\n");
							cont.append("Auteur : "+sendTo+"\r\n");
							cont.append("Sujet de l'article : "+variables.geteMailList().get(i).getSubject()+"\r\n");
							MethodesUtiles.sendToAdminList(subject, cont.toString(), eMailDesc);
							}
						}
					else
						{
						variables.getLogger().info("Un mail a été traité mais l'expéditeur est inconnu");
						variables.getLogger().info("Envoi d'un email d'échec à l'adresse suivante : "+variables.geteMailList().get(i).getFrom());
						
						//Envoi d'un eMail d'erreur à l'utilisateur
						String content = new String(MethodesUtiles.getValidEmailContent("emailusernotallowed"));
						String sendTo = variables.geteMailList().get(i).getFrom();
						String subject = "ERREUR : Utilisateur non reconnu";
						String eMailDesc = "emailusernotallowed";
						variables.geteMSender().send(sendTo, subject, content, eMailDesc);
						
						//Envoi d'un eMail à la liste d'admin
						StringBuffer cont = new StringBuffer(MethodesUtiles.getValidEmailContent("emailnotallowed"));
						subject = "Avertissement : ERREUR : Utilisateur non reconnu";
						eMailDesc = "emailnotallowed";
						
						cont.append("\r\n\r\n");
						cont.append("Tentative de création par : "+sendTo+"\r\n");
						cont.append("Sujet de l'article : "+variables.geteMailList().get(i).getSubject()+"\r\n");
						MethodesUtiles.sendToAdminList(subject, cont.toString(), eMailDesc);
						}
					}
				variables.getLogger().debug("Suppression des fichiers stockés temporairement");
				for(int i=0; i<variables.geteMailList().size(); i++)
					{
					for(int j=0; j<variables.geteMailList().get(i).getAttachementList().size(); j++)
						{
						try
							{
							variables.geteMailList().get(i).getAttachementList().get(j).getFile().delete();
							}
						catch(Exception e)
							{
							variables.getLogger().error(e);
							variables.getLogger().error("Il n'a pas été possible de supprimer le fichier temporaire :"+variables.geteMailList().get(i).getAttachementList().get(j).getFileName());
							}
						}
					}
				}
			catch(javax.mail.MessagingException mmexc)
				{
				mmexc.printStackTrace();
				variables.getLogger().error(mmexc);
				}
			catch(java.lang.IllegalStateException illexc)
				{
				illexc.printStackTrace();
				variables.getLogger().error(illexc);
				}
			catch(Exception exc)
				{
				exc.printStackTrace();
				variables.getLogger().error(exc);
				
				if(exc.getMessage().compareTo("java.lang.Exception: No new messages") == 0)
					{
					}
				else if(exc.getMessage().compareTo("java.lang.Exception: Could not connect to server") == 0)
					{
					}
				else if(exc.getMessage().compareTo("javax.mail.AuthenticationFailedException: EOF on socket") == 0)
					{
					}
				else
					{
					//Une erreur s'est produite, on envoi donc un eMail d'avertissement
					//Envoi d'un eMail à la liste d'admin
					StringBuffer cont = new StringBuffer(MethodesUtiles.getValidEmailContent("emailcreationerror"));
					String subject = "Avertissement : ERREUR : Erreur système";
					String eMailDesc = "emailcreationerror";
					
					cont.append("\r\n\r\n");
					cont.append("EMailWikiManager : Une erreur critique est survenu, le programme a été arrêté.\r\n");
					cont.append("Veuillez vérifier dans l'ordre les éléments suivants : \r\n");
					cont.append("- Un email pose peut-être problème \r\n");
					cont.append("- Le serveur est peut être un disfonctionnement \r\n");
					cont.append("- Autres... \r\n");
					cont.append("Description de l'erreur : "+exc.getMessage()+"\r\n");
					
					MethodesUtiles.sendToAdminList(subject, cont.toString(), eMailDesc);
					}
				}
			finally
				{
				try
					{
					//On attends avant de vérifier s'il y a des nouveaux emails
					variables.getLogger().info("Attente de "+MethodesUtiles.getConfig("popfreq")+" ms avant nouveau cycle");
					sleep(Integer.parseInt(MethodesUtiles.getConfig("popfreq")));
					variables.geteMailList().clear();
					System.gc();
					}
				catch(Exception exc)
					{
					variables.getLogger().error(exc);
					}
				}
			}
		}
	
	/*2013*//*RATEL Alexandre 8)*/
	}
