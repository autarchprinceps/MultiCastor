package zisko.multicastor.program.model;

/**
 * Custom Exception fuer eine falsche Konfiguration
 *
 */
public class WrongConfigurationException extends Exception
{
	private static final long serialVersionUID = 4106264200218128114L;
	
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
