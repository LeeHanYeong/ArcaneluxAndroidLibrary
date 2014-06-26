package arcanelux.library.common;

public class ArcConstant {
	private static String URL_GENYMOTION = "http://192.168.56.1/";
	private static String URL_GENYMOTION_WITHPORT = "http://192.168.56.1:%d/";
	public static String getGenymotionURL(int portNumber){
		return String.format(URL_GENYMOTION_WITHPORT, portNumber);
	}
	public static String getGenymotionURL(){
		return URL_GENYMOTION;
	}
	
}
