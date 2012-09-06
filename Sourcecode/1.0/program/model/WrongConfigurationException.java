package zisko.multicastor.program.model;

/**
 * 
 * @author gerz
 *
 */
public class WrongConfigurationException extends Exception
{
	private String errorMessage;

	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage()
	{
		return errorMessage;
	}

	
}
