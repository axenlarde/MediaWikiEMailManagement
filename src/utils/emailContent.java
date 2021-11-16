
package utils;

import java.io.File;
import java.util.ArrayList;

/*********************************************
 * This class is used to store email 
 * content.
 *********************************************/
public class emailContent
	{
	/*****
	 * Variables
	 */
	String from, to, subject, contentType, content;
	ArrayList<attachement> attachementList;
	
	
	 /*************
	 * Constructeur
	 **************/
	public emailContent(String from, String to, String subject, String contentType, String content, ArrayList<attachement> attachementList)
		{
		this.from = from;
		this.to = to;
		this.subject = subject;
		this.contentType = contentType;
		this.content = content;
		this.attachementList = attachementList;
		}


	public String getFrom()
		{
		return from;
		}

	public String getTo()
		{
		return to;
		}

	public String getContent()
		{
		return content;
		}

	public String getSubject()
		{
		return subject;
		}

	public void setSubject(String subject)
		{
		this.subject = subject;
		}

	public String getContentType()
		{
		return contentType;
		}

	public void setContentType(String contentType)
		{
		this.contentType = contentType;
		}

	public ArrayList<attachement> getAttachementList()
		{
		return attachementList;
		}
	
	
	
	/*2013*//*RATEL Alexandre 8)*/
	}
