package de.thwildau.tools;

public class Constants {

	private static String localhost = "http://192.168.0.101";

	public static final boolean DEBUG = false;
	/*
	 * URLs
	 */
	//	public static final String URL_LOGIN = "http://justsomespace.de/data/login.php";
	//	public static final String URL_LOGOUT = "http://justsomespace.de/data/logout.php";
	//	public static final String URL_LOGIN_REQUEST = "http://justsomespace.de/data/isLoggedIn.php";
	//	public static final String URL_LAYOUT_IMAGE = "http://justsomespace.de/data/img_get.php";
	//	public static final String URL_LAMP = "http://justsomespace.de/data/getLamps.php";

	/*
	 * URLs localhost
	 */
	public static final String URL_LOGIN = localhost+"/delightful/login.php";
	public static final String URL_LOGOUT = localhost+"/delightful/logout.php";
	public static final String URL_LOGIN_REQUEST = localhost+"/delightful/isLoggedIn.php";
	public static final String URL_LAYOUT_IMAGE = localhost+"/delightful/img_get.php";
	public static final String URL_LAMP = localhost+"/delightful/getLamps.php";
	public static final String URL_SAVE_GRADIENT = localhost+"/delightful/saveGradient.php";
	public static final String URL_GET_GRADIENT = localhost+"/delightful/getGradient.php";

}
