package zisko.multicastor.program.lang;

/**
 * This exception is thrown if a language file is invalid
 * In the most cases it does not contain all the required key value pairs that are needed. 
 */
@SuppressWarnings("serial")
public class InvalidLanguageFileException extends Exception {
	/**
	 * The first missing key value
	 * @see LanguageManager.keys
	 */
	private String errorKey;
	/**
	 * The index the first missing key
	 * @see LanguageManager.keys
	 */
	private int errorIndex;
	/**
	 * the reference to the used key array for the validation
	 */
	private String[] keys;
	
	/**
	 * Creates a new Invalid LanguageFileException
	 * @param errorIndex The first missing key value
	 * @param errorKey The index the first missing key
	 * @param keys the reference to the used key array for the validation
	 * @see LanguageManager.keys
	 */
	public InvalidLanguageFileException(int errorIndex,String errorKey,String[] keys){
		this.errorIndex=errorIndex;
		this.keys=keys;
		this.errorKey=errorKey;
	}

	/**
	 * Returns the name of the first missing key
	 * @return name of the first missing key
	 */
	public String getErrorKey() {
		return errorKey;
	}
	
	/**
	 * Returns the index of the first missing key
	 * @return index of the first missing key
	 */
	public int getErrorIndex() {
		return errorIndex;
	}

	/**
	 * Returns the reference to the used key array for the validation
	 * @return reference to the used key array for the validation
	 */
	public String[] getKeys() {
		return keys;
	}

	
}
