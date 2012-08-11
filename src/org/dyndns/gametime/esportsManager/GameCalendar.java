package org.dyndns.gametime.esportsManager;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class GameCalendar {
	private Calendar cal = new GregorianCalendar();
	String months[] = { "January", " February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };
	String days[] = { "Sunday", "Monday", "Tuesday", "Wednsday", "Thursday", "Friday", "Saturday" };
	
	public GameCalendar(){
		cal.set(2012, 1, 1);
	}
	
	public void addDay(){
		cal.add(Calendar.DATE, 1);
	}
	
	public String toString(){
		return months[ cal.get(Calendar.MONTH) - 1 ] + " " + cal.get(Calendar.DATE) + ", " + cal.get(Calendar.YEAR);
	}	
	
	public String day(){
		return days[ cal.get(Calendar.DATE) ];
	}
	
}
