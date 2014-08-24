package arcanelux.library;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ArcCalendar {
	public static final SimpleDateFormat FORMAT_yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat FORMAT_yy_MM_DD = new SimpleDateFormat("yy-MM-dd");
	
	public static String getDateStringFromCalendar(Calendar cal) {
		return FORMAT_yyyy_MM_dd.format(cal.getTime());
	}
	public static String getDateStringFromCalendar(Calendar cal, SimpleDateFormat format) {
		return format.format(cal.getTime());
	}
	public static String getDateStringFromCalendar(Calendar cal, String formatString) {
		SimpleDateFormat format = new SimpleDateFormat(formatString);
		return format.format(cal.getTime());
	}
	
	private Calendar mCalendar;
	
	public ArcCalendar() {
		mCalendar = Calendar.getInstance();
	}
	public ArcCalendar(Calendar cal) {
		mCalendar = cal;
	}
	
	public String getDateString() {
		return FORMAT_yyyy_MM_dd.format(mCalendar.getTime());
	}
	public String getDateString(SimpleDateFormat format) {
		return format.format(mCalendar.getTime());
	}
	public String getDateString(String formatString) {
		SimpleDateFormat format = new SimpleDateFormat(formatString);
		return format.format(mCalendar.getTime());
	}
}
