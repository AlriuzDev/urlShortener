import java.util.HashMap;
import java.util.Random;

public class URLShortener {
	// storage for generated keys
	private HashMap<String, String> shortMap; // key-url map for short url
	private HashMap<String, String> ogMap;// url-key map to quickly check whether an original url is already entered
	private String domain; // Use this attribute to generate urls for a custom domain name
	private int keyLength; // the key length in URL defaults to 8
	private Random random; // Random object used to generate random integers
	private static char[] charToKeys = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray(); // This array is used for mapping valid character to keys

	// Default Constructor
	URLShortener() {
		shortMap = new HashMap<String, String>();
		ogMap = new HashMap<String, String>();
		random = new Random();
		keyLength = 5;
		domain = "http://nonevalidurl.io";
	}

	// Constructor which enables you to define tiny URL key length and base URL name
	URLShortener(int keyLength, String domain) {
		this(); //Calling default constructor
		this.keyLength = keyLength;
        this.domain = !domain.isEmpty() ? sanitizeURL(domain) : this.domain;
	}

	// getShortURL
	// the public method which can be called to shorten a given URL
	public String getShortURL(String originalURL) {
		String shortURL = "";
		originalURL = sanitizeURL(originalURL);
		if (validateURL(originalURL)) {
			shortURL = domain + "/";
            shortURL += ogMap.containsKey(originalURL) ? ogMap.get(originalURL) : getKey(originalURL);
		}
		// add http part
		return shortURL;
	}

	// public method which returns back the original URL given the shortened url
	public String getOriginalURL(String shortURL) {
		String originalURL = "";
		String key = shortURL.substring(this.domain.length() + 1);
		originalURL = shortMap.get(key);
		return originalURL;
	}

	private boolean validateURL(String url) {
		return url.matches("([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}+(\\/[^\\s]*)*");
	}

	// sanitizeURL
	// This method should take care various issues with a valid url
	//no spacing before and after url
	// e.g. www.google.com,www.google.com/, http://www.google.com, http://www.google.com/
	// all the above URL should point to same shortened URL
	private String sanitizeURL(String url) {
    	url = url.trim();
		if (url.substring(0, 7).equals("http://"))
			url = url.substring(7);

		if (url.substring(0, 8).equals("https://"))
			url = url.substring(8);

		if (url.charAt(url.length() - 1) == '/')
			url = url.substring(0, url.length() - 1);
		return url;
	}

	private String getKey(String originalURL) {
		String key;
		key = generateKey();
		shortMap.put(key, originalURL);
		ogMap.put(originalURL, key);
		return key;
	}

	private String generateKey() {
		String key ="";
		boolean isValidKey = false;
		while(!isValidKey){
			for(int i=0; i <= this.keyLength; i++){
				key += charToKeys[random.nextInt(charToKeys.length)];
			}
			isValidKey = !shortMap.containsKey(key) ? true : false;
		}
		return key;
	}

	// testing code
	public static void main(String args[]) {
		URLShortener u = new URLShortener(5, "www.tinyurl.com/");

		String urls[] = { "www.google.com/", "www.google.com",
				"http://www.yahoo.com", "    www.yahoo.com/", "www.amazon.com",
				"www.amazon.com/page1.php", "www.amazon.com/page2.php",
				"www.flipkart.in", "www.rediff.com", "www.techmeme.com",
				"www.techcrunch.com", "www.lifehacker.com", "www.icicibank.com" };

		for (int i = 0; i < urls.length; i++) {
			System.out.println("URL: " + String.format("%-27s", urls[i])  + "Tiny: " + String.format("%-25s", u.getShortURL(urls[i])));
		}
	}
}