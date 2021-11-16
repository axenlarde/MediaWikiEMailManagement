package utils;

import javax.swing.JOptionPane;

public class ClearFrenchString
	{
	/************
	 * Variables
	 ***********/
	
	
	
	/******************************************************
	 * Method used to replace french special characters by
	 * common ASCII equivalent characters 
	 * @throws Exception 
	 ******************************************************/
	public static String translate(String src) throws Exception
		{
		StringBuffer result = new StringBuffer();
		if(src!=null && src.length()!=0)
			{
			int a = -1;
			char c = (char)0;
			String chars= "() /#&-_.,'1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTU€‹VWXYZ‡‚‰ÈËÍÎÓÔÙˆ˘˚¸‹Á…»Àœ‘”“’ÚÒ—√„";
			String replace= "      -_., 1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUUUVWXYZaaaeeeeiioouuuUcEEEIOOOOonNAa";
			for(int i=0; i<src.length(); i++)
				{
				c = src.charAt(i);
				if((a=chars.indexOf(c))!=-1)
					{
					result.append(replace.charAt(a));
					}
				else
					{
					throw new Exception("Un caractËre n'a pas pu Ítre remplacÈ");
					}
				}
			/******************************************
			 * Remove spaces present at the beginning or
			 * at the end of a sentence
			 *************************/
			for(int i=0; i<result.length(); i++)
				{
				c = result.charAt(i);
				if((" ".indexOf(c) == 0) && (i == 0))
					{
					result.deleteCharAt(0);
					}
				else if((" ".indexOf(c) == 0) && (i == result.length()-1))
					{
					result.deleteCharAt(result.length()-1);
					}
				}
			
			/*************************
			 * Remove double spaces
			 *************************/
			for(int i=1; i<result.length(); i++)
				{
				if((" ".indexOf(result.charAt(i)) == 0) && (" ".indexOf(result.charAt(i-1)) == 0))
					{
					result.deleteCharAt(i);
					i--;
					}
				}
			}
		return result.toString();
		}
	
	
	
	/*2013*//*RATEL Alexandre 8)*/
	}
