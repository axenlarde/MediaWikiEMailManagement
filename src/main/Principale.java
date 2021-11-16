
package main;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mngt.eMailManagement;

import org.apache.log4j.Level;

import utils.MethodesUtiles;
import utils.initLogging;
import utils.variables;
import utils.xMLGear;
import utils.xMLReader;

/*********************************************
 * Method used to launch the program
 * 
 * @author RATEL Alexandre
 *********************************************/
public class Principale
	{
	
	
	/******
	 * Constructeurs
	 */
	public Principale()
		{
		variables.setNomProg("EmailWikiManager");
		variables.setVersion("1.4.4");
		
		
		/**********************
		 * Initialisation de la journalisation
		 ************/
		variables.setLogger(initLogging.init());
		variables.getLogger().info("Entering application");
		variables.getLogger().info("## Welcome to : "+variables.getNomProg()+" : "+variables.getVersion()+" ##");
		variables.getLogger().info("## Author : RATEL Alexandre ##");
		/**************/
		

		/***********
		 * Initialisation des variables
		 */
		new utils.variables();
		/************/
		
		/*************
		 * Lectures du fichier xml de paramétrages
		 */
		try
			{
			variables.getLogger().info("Lecture du fichier de préférence : configFile.xml");
			variables.setConfigFileContent(xMLReader.fileRead("./configFile.xml"));
			
			//On récupère les variables et on les assignes
			variables.setWiki(utils.MethodesUtiles.optionSetting("wiki"));
			variables.setMisc(utils.MethodesUtiles.optionSetting("misc"));
			variables.setPop(utils.MethodesUtiles.optionSetting("pop"));
			variables.setSmtp(utils.MethodesUtiles.optionSetting("smtp"));
			}
		catch(Exception exc)
			{
			exc.printStackTrace();
			variables.getLogger().error(exc);
			variables.getLogger().error("Il n'est pas acceptable que l'initialisation du service ai échoué. Fin du programme : System.exit(0)");
			System.exit(0);
			}
		/*********/
		
		/*************************
		 * Set the logging level
		 *************************/
		String level = utils.MethodesUtiles.getConfig("loglevel");
		if(level.compareTo("DEBUG")==0)
			{
			variables.getLogger().setLevel(Level.DEBUG);
			}
		else if (level.compareTo("INFO")==0)
			{
			variables.getLogger().setLevel(Level.INFO);
			}
		
		variables.getLogger().info("Niveau de log d'après le fichier de configuration : "+variables.getLogger().getLevel().toString());
		/******************/
		
		/**
		 * Initialisation divers
		 */
		try
			{
			//MethodesUtiles.fillUserList();
			MethodesUtiles.fillReplacementList();
			//MethodesUtiles.fillCategorieList();
			MethodesUtiles.initEMailServer();
			}
		catch(Exception exc)
			{
			exc.printStackTrace();
			variables.getLogger().error(exc);
			variables.getLogger().error("Il n'est pas acceptable que l'initialisation du service ai échoué. Fin du programme : System.exit(0)");
			System.exit(0);
			}
		/*********/
		
		
		/***********
		 * Lancement du gestionnaire d'email
		 */
		new eMailManagement();
		/*******/
		}
	
	
	/****************************************
	 * Main
	 ****************************************/
	public static void main(String[] args)
		{
		new Principale();
		}
	
	/*2013*//*RATEL Alexandre 8)*/
	}
