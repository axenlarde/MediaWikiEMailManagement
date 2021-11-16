
package mngt;


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;

import utils.MethodesUtiles;
import utils.attachement;
import utils.emailContent;
import utils.variables;

/*********************************************
 * This class contains method to getMail 
 * and fill the Email List. 
 *********************************************/
public class fillEMailList
	{
	/***
	 * Variables
	 */
	private static String content;
	private static String contentType;
	private static ArrayList<attachement> attachementList;
	
	/*******
	 * Method used to fill the eMail list
	 */
	public static void fill() throws Exception
		{
		ArrayList<emailContent> eMailList;
		eMailList = new ArrayList<emailContent>();
		eMailList = getMail(eMailList);
		variables.seteMailList(eMailList);
		}
	
	/********
	 * Method used to get EMail from the pop3 server
	 */
	private static ArrayList<emailContent> getMail(ArrayList<emailContent> eMailList) throws Exception
		{
		/**
		 * Variables
		 */
		String server, proto, username, password, port, protocol;
		server = MethodesUtiles.getConfig("popemailserver");
		proto = MethodesUtiles.getConfig("popemailprotocol");
		username = MethodesUtiles.getConfig("popemail");
		password = MethodesUtiles.getConfig("popemailpassword");
		port = MethodesUtiles.getConfig("popemailport");
		protocol = "pop3";
		
		Properties props = System.getProperties();
	    
		if (proto.compareTo("TLS")==0)
        	{
            variables.getLogger().info("Using secure protocol: "+proto);
            props.put("mail.pop3s.starttls.enable","true");
            props.setProperty("mail.pop3.socketFactory.fallback", "false");
            props.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        	}
		else
			{
			variables.getLogger().info("Using unsecure protocol: "+proto);
			}
		
		props.put("mail.transport.protocol", "pop");
	    props.put("mail."+protocol+".host", server);
	    props.put("mail."+protocol+".port", port);
	    props.put("mail."+protocol+".timeout", MethodesUtiles.getConfig("poptimeout"));
	    props.put("mail."+protocol+".auth", "true");
	    
	    Session session = Session.getInstance(props, null);
	    Store store=session.getStore("pop3");
        
        try
        	{
        	variables.getLogger().info("Connecting to server "+server+" on "+port);
    	    store.connect(username, password);
        	}
        catch (Exception exc)
        	{
        	variables.getLogger().error(exc);
            throw new Exception("Could not connect to server.");
        	}
        
        try
	        {
	        variables.getLogger().info("Récupération des messages");
	        
	        Folder folder = store.getFolder("INBOX");
	        folder.open(Folder.READ_ONLY);
	        Message[] messages = folder.getMessages();
	        
            if (messages == null)
            	{
            	folder.close(false);
            	store.close();
            	throw new Exception("Could not retrieve message list.");
            	}
            else if (messages.length == 0)
            	{
            	folder.close(false);
            	store.close();
            	throw new Exception("No new messages");
            	}
            
            variables.getLogger().info("Nombre de message récupérés : "+messages.length);
            
        	FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE);
            folder.fetch(messages, fp);
            
            for(int i=0; i<messages.length; i++)
            	{
            	content = new String("");
            	String from = new String("");
            	contentType = new String("");
            	attachementList = new ArrayList<attachement>();
            	
            	//Getting From
            	if(messages[i].getFrom()[0].toString().contains("<"))
            		{
            		from = messages[i].getFrom()[0].toString().substring(messages[i].getFrom()[0].toString().indexOf("<")+1, messages[i].getFrom()[0].toString().indexOf(">"));
            		}
            	else
            		{
            		from = messages[i].getFrom()[0].toString();
            		}
            	
            	//Getting Content
            	if(messages[i].isMimeType("text/plain"))
            		{
            		//On renseigne le content type
            		contentType = messages[i].getContentType();
            		content = (String) messages[i].getContent();
            		}
            	else
            		{
            		Multipart mp = (Multipart)messages[i].getContent();
            		variables.getLogger().debug("EMail numéro : "+(i+1)+" nombre de partie MIME : "+mp.getCount());
            		
        	        manageMultipart(mp);
            		}
            	
            	if((messages[i].getSubject().contains("INFO :"))||(messages[i].getSubject().contains("ERREUR :")))
            		{
            		variables.getLogger().info("Le message contenait INFO ou ERREUR. Il n'a pas été pris en compte");
            		}
            	else
            		{
            		//On nettoie le sujet en ne gardant que ce qui se trouve après les ":"
            		String subject = messages[i].getSubject();
            		String[] tab = subject.split(":");
            		subject = tab[tab.length-1];
            		
            		//On nettoie le sujet en enlevant les crochets (non supporté dans le titre)
            		subject = subject.replace("[", "");
            		subject = subject.replace("]", "");
            		
            		variables.getLogger().info("From : "+from+" Subject : "+subject+" Content type : "+contentType+" Content : "+content);
	            	eMailList.add(new emailContent(from, username, subject, contentType, content, attachementList));
            		}
            	}
            
            folder.close(false);
            variables.getLogger().info("POP3 Folder closing : done");
            store.close();
            variables.getLogger().info("POP3 Store closing : done");
	        }
        catch (Exception exc)
	        {
	        exc.printStackTrace();
	        throw new Exception(exc);
	        }
        return eMailList;
		}
	
	/**
	 * Method used to save attached file
	 */
	private static attachement saveFile(String fileName, InputStream in) throws Exception
		{
		File file = new File(MethodesUtiles.getConfig("temppath")+fileName);
		boolean success = true;
		
		if(file.exists())
    		{
    		variables.getLogger().debug("Le fichier : "+fileName+" existe déja, suppression");
    		success = file.delete();
    		}
		
		variables.getLogger().debug("Taille du fichier : "+file.length());
		if(file.length()>Integer.parseInt(MethodesUtiles.getConfig("maxfilesize")))
			{
			throw new Exception("La pièce jointe est trop grosse, elle ne sera pas prise en compte");
			}
		
		if(success)
			{
			variables.getLogger().debug("Enregistrement du fichier : "+fileName+" dans le répertoire temporaire");
    	    FileOutputStream fos = new FileOutputStream(file);
    	    byte[] buf = new byte[4096];
    	    int bytesRead;
    	    while((bytesRead = in.read(buf))!=-1)
    	    	{
    	        fos.write(buf, 0, bytesRead);
    	    	}
		    fos.flush();
		    fos.close();
    		}
		else
			{
			throw new Exception("La suppression du fichier "+file.getName()+" a échoué pour une raison inconnue");
			}
		variables.getLogger().debug("Enregistrement résussi");
		return (new attachement(fileName, file));
		}
	
	/**
	 * Method used to extract eMail Multi part
	 */
	private static void manageMultipart(Multipart mp) throws Exception
		{
		try
			{
			 for (int j=0; j<mp.getCount(); j++)
	        	{
	        	Part part = mp.getBodyPart(j);
	        	String disposition = part.getDisposition();
		        	
		        if ((disposition != null) && ((disposition.equals(Part.ATTACHMENT) || (disposition.equals(Part.INLINE)))))
		        	{
		        	variables.getLogger().info("L'eMail contient une pièce jointe");
		        	variables.getLogger().info("Sauvegarde de la pièce jointe");
		        	attachementList.add(saveFile(part.getFileName(), part.getInputStream()));
		        	}
		        
		        /*****
		         * On ne récupère que la version plain/text dans le cas d'un email MultiPart
		         * La version html n'est récupéré que si la version plain text n'est pas fournie
		         */
		        variables.getLogger().debug("Contenu du multi part : "+part.getContentType());
		        if(part.getContentType().contains("text/plain"))
		        	{
		 	        //Version plain text
		        	contentType = "text/plain";
		        	content = (String)mp.getBodyPart(j).getContent();
		        	}/*
		        else if(part.getContentType().contains("text/html"))
		        	{
		 	        //Version Html
		        	contentType = "text/html";
		        	content = (String)mp.getBodyPart(j).getContent();
		        	}*/
		        else if((disposition == null) && (!part.getContentType().contains("text/html")) && (!part.getContentType().contains("image")))
		        	{
		        	MimeBodyPart mbp = (MimeBodyPart)part;
		        	if(mbp.isMimeType("text/plain"))
		        		{
		        		contentType = "text/plain";
	            		content = (String) mbp.getContent();
		        		}
		        	else
		        		{
		        		Multipart newMP = (Multipart)mbp.getContent();
		        		manageMultipart(newMP);
		        		}
		        	}
	        	}
			}
		catch(Exception exc)
			{
			exc.printStackTrace();
			variables.getLogger().error(exc);
			throw new Exception(exc.getMessage());
			//System.exit(0);
			}
		}
		
	/*2013*//*RATEL Alexandre 8)*/
	}
