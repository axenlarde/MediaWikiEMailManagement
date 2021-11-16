
package utils;

/*********************************************
 * This class contains all the user's data
 *********************************************/
public class userInfo
	{
	/****
	 * Variables
	 */
	private String eMail, userId, password, firstName, lastName, realName;
	

	/****
	 * Constructeur
	 */
	public userInfo(String eMail, String userId, String password, String firstName, String lastName, String realName)
		{
		this.eMail = eMail;
		this.userId = userId;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.realName = realName;
		}


	public String geteMail()
		{
		return eMail;
		}

	public String getUserId()
		{
		return userId;
		}

	public String getPassword()
		{
		return password;
		}

	public String getFirstName()
		{
		return firstName;
		}

	public String getLastName()
		{
		return lastName;
		}
	
	public String getRealName()
		{
		return realName;
		}

	/*2013*//*RATEL Alexandre 8)*/
	}
