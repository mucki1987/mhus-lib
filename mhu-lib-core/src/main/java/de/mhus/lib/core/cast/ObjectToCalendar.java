/**
 * Copyright 2018 Mike Hummel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.lib.core.cast;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import de.mhus.lib.core.MCast;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.logging.Log;

/**
 * Accepted formats:
 * 
 * now, jetzt
 * integer as timestamp
 * <<date>>[[ |_|T]<<time>>]
 * date:
 * yyyy-mm-dd
 * dd.mm.yyyy
 * dd/mm/yyyy
 * mm/dd/yyyy + locale == US
 * time:
 * MM:HH:ss[.SSS][Z][zone]
 * MM-HH-ss[.SSS][Z][zone]
 * MM:HH am/pm[Z][zone]
 * Jan 1, 2000 1:00 am[Z][zone]
 * 
 * Currently Not Accepted:
 * 
 * 1. Januar 2000 13:00:00
 * 
 * @author mikehummel
 *
 */
public class ObjectToCalendar implements Caster<Object,Calendar>{

	private final static Log log = Log.getLog(ObjectToCalendar.class);
	
	@Override
	public Class<? extends Calendar> getToClass() {
		return Calendar.class;
	}

	@Override
	public Class<? extends Object> getFromClass() {
		return Object.class;
	}

	@Override
	public Calendar cast(Object in, Calendar def) {
		return cast(in, def, Locale.getDefault());
	}
	
	public Calendar cast(Object in, Calendar def, Locale locale) {
		if (in == null) return def;
		if (in instanceof Calendar) return (Calendar)in;
		if (in instanceof Date) {
			Calendar c = Calendar.getInstance();
			c.setTime((Date)in);
			return c;
		}
		try {
			String ins = MCast.toString(in);
			Calendar ret = toCalendar(ins, locale);
			if (ret == null) return def;
			return ret;
		} catch (Throwable t) {
			return def;
		}
	}

	public static Calendar toCalendar(String in, Locale locale) {
		if (in == null) return null;
		
		
		try {
			
			Calendar c = Calendar.getInstance();
			if (in.equals("now") || in.equals("jetzt")) {
				return c;
			}
			
			boolean retOk = false;
			c.clear();

			String date = in.trim();

			int hour = 0;
			int min = 0;
			int sec = 0;
			int millies = 0;
			String zone = null;
			
			// check if date and time
			char sep = '?';
			if ( MString.isIndex(date, '_' ) )
				sep = '_';
			else
			if ( MString.isIndex(date, ' ' ) )
				sep = ' ';
			else
			if (MString.isIndex(date, 'T'))
				sep = 'T';
			
			// TODO can't read DE: '1. Januar 2000 13:00:00'
			
			{
				// US Format: 'Jan 1, 2000 1:00 am'
				if (sep == ' ' && MString.isIndex(date, ',')) {
					int p1 = date.indexOf(' ');
					int p2 = date.indexOf(',');
					try {
						int month = toMonth(date.substring(0, p1));
						int day = Integer.parseInt(date.substring(p1+1, p2).trim());
						date = date.substring(p2+1).trim();
						p1 = date.indexOf(' ');
						int year = Integer.parseInt(date.substring(0,p1).trim());
						c.set(year, month, day);
						
						date = " " + date.substring(p1+1).trim(); // rest is time
						sep = ' ';
						retOk = true;
					} catch (NumberFormatException e) {}
				}
			}

			if (sep != '?') {
				// found also time ... parse it !
				String apm = null;
				String time = MString.afterIndex(date, sep).trim();
				date = MString.beforeIndex(in, sep).trim();

				// zone
				char sep2 = '?';
				if ( MString.isIndex(time, ' ' ) )
					sep2 = ' ';
				else
				if ( MString.isIndex(time, '_' ) )
					sep2 = '_';
				else
				if (MString.isIndex(time, 'Z'))
					sep2 = 'Z';
				
				if (sep2 == ' ') {
					zone = MString.afterIndex(time, sep2).trim();
					time = MString.beforeIndex(time, sep2).trim();
					if (zone.startsWith("am") || zone.startsWith("pm")) {
						apm = zone.substring(0, 2);
						zone = zone.substring(2).trim();
					}
				} else
				if (sep2 != '?') {
					zone = MString.afterIndex(time, sep2).trim();
					time = MString.beforeIndex(time, sep2).trim();
				} else
				if (time.startsWith("+") || time.startsWith("-")) {
					zone = time.trim();
					time = "";
				}

				// milliseconds
				if (MString.isIndex(time, '.')) {
					millies = toint(MString.afterLastIndex(time, '.'), 0);
					time = MString.beforeLastIndex(time, '.');
				}
				
				time = time.trim();
				if (time.length() > 0) {
					String sep3 = null;
					if (MString.isIndex(time, ':'))
						sep3 = "\\:";
					else
					if (MString.isIndex(time, '-'))
						sep3 = "-";
					else
					if (MString.isIndex(time, '.'))
						sep3 = "\\.";
					
					// parse time
					String[] parts = time.split(sep3);
					if (parts.length > 1) {
						hour = toint(parts[0], 0);
						min = toint(parts[1], 0);
						if (parts.length > 2)
							sec = toint(parts[2], 0);
					}
	
					if (apm != null) {
						// https://www.timeanddate.com/time/am-and-pm.html
						if (hour == 0) { // 0 is not valid - reset time to 0
							min = 0;
							sec = 0;
							millies = 0;
							zone = "";
						} else
						if (apm.equals("am")) {
							if (hour == 12)
								hour = 0;
						} else
						if (apm.equals("pm")) {
							if (hour != 12)
								hour = hour + 12;
						}
					}
					c.set(Calendar.HOUR_OF_DAY, hour);
					c.set(Calendar.MINUTE, min);
					c.set(Calendar.MILLISECOND, sec * 1000 + millies);
					
					if (zone != null && zone.length() > 0) {
						// https://www.timeanddate.com/time/gmt-utc-time.html
						if (zone.startsWith("-") || zone.startsWith("+"))
							zone = "GMT"+zone;
						TimeZone tz = TimeZone.getTimeZone(zone);
						c.setTimeZone(tz);
					}
				}
			}
			
			if (retOk) return c;

			// parse the date
			if ( date.indexOf('-') > 0) {
				// technical time 2000.12.31
				String[] parts = date.split("-");
				
	
				if (parts.length == 3) {
						int year = Integer.parseInt(parts[0]);
						if (parts[0].length()==2) year = year + 2000; // will this lib life for 100 years ???
						int month = toMonth(parts[1]);
						int day   = Integer.parseInt(parts[2]);
						c.set(year,month, day);
				} else if (parts.length == 2) {
					c.set(Calendar.MONTH, toMonth(parts[0]));
					c.set(Calendar.DATE, Integer.parseInt(parts[1]));
				} else {
					parts = date.split("\\.");
					if (parts.length == 3) {
						int year = Integer.parseInt(parts[2]);
						if (parts[2].length()==2) year = year + 2000; // will this lib life for 100 years ???
						int month = toMonth(parts[1]);
						int day   = Integer.parseInt(parts[0]);
						c.set(year,month,day);
					} else if (parts.length == 2) {
						int year = Integer.parseInt(parts[1]);
						if (parts[1].length()==2) year = year + 2000; // will this lib life for 100 years ???
						int month = toMonth(parts[0]);					
						c.set(Calendar.MONTH, month);
						c.set(Calendar.YEAR, year);
					} else {
						parts = date.split("/");
						if (parts.length == 3) {
							if (Locale.US.equals(locale)) {
								c.set(
										Integer.parseInt(parts[2]),
									  toMonth(parts[0]),
									  Integer.parseInt(parts[1])
									  );
							} else {
								c.set(
										Integer.parseInt(parts[2]),
										toMonth(parts[1]),
										Integer.parseInt(parts[0])
										);
							}
						}
						if (parts.length == 2) {
							c.set(Calendar.MONTH, toMonth(parts[0]) );
							c.set(Calendar.YEAR, Integer.parseInt(parts[1]));
						}
					}
				}
	
	//			if (zone != null) {
	//				TimeZone tz = TimeZone.getTimeZone(zone);
	//				c.setTimeZone(tz);
	//			}
				
				return c;
			} else
			if ( date.indexOf('.') > 0) {
				// german time 31.12.2000
				String[] parts = date.split("\\.");
				if (parts.length == 3) {
					int year = Integer.parseInt(parts[2]);
					if (parts[2].length()==2) year = year + 2000; // will this lib life for 100 years ???
					int month = Integer.parseInt(parts[1])-1;
					int day   = Integer.parseInt(parts[0]);
					c.set(year,month, day);
					return c;
				} else
				if (parts.length == 2) {
					int year = Integer.parseInt(parts[1]);
					if (parts[1].length()==2) year = year + 2000; // will this lib life for 100 years ???
					int month = Integer.parseInt(parts[0])-1;
					c.set(year,month, 1);
					return c;
				} else
					return null;
			} else
			if ( date.indexOf('/') > 0) {
				String[] parts = date.split("/");
				if (Locale.US.equals(locale)) {
					// US 12/31/2000
					if (parts.length == 3) {
						int year = Integer.parseInt(parts[2]);
						if (parts[2].length()==2) year = year + 2000; // will this lib life for 100 years ???
						int month = Integer.parseInt(parts[0])-1;
						int day   = Integer.parseInt(parts[1]);
						c.set(year,month, day);
						return c;
					} else
					if (parts.length == 2) {
						int year = Integer.parseInt(parts[1]);
						if (parts[1].length()==2) year = year + 2000; // will this lib life for 100 years ???
						int month = Integer.parseInt(parts[0])-1;
						c.set(year,month, 1);
						return c;
					} else
						return null;
				} else {
					// france or UK 31/12/2000
					if (parts.length == 3) {
						int year = Integer.parseInt(parts[2]);
						if (parts[2].length()==2) year = year + 2000; // will this lib life for 100 years ???
						int month = Integer.parseInt(parts[1])-1;
						int day   = Integer.parseInt(parts[0]);
						c.set(year,month, day);
						return c;
					} else
					if (parts.length == 2) {
						int year = Integer.parseInt(parts[1]);
						if (parts[1].length()==2) year = year + 2000; // will this lib life for 100 years ???
						int month = Integer.parseInt(parts[0])-1;
						c.set(year,month, 1);
						return c;
					} else
						return null;
				}
			}
			
			try {
				// timestamp
				long ts = Long.valueOf(date);
				c.setTimeInMillis(ts);
				return c;
			} catch (NumberFormatException e) {}
			
			return null;

		} catch (Throwable e) {
			log.t(in,e);
		}

		// return unknown - timestamp is 0
		return null;
	}

	/**
	 * Return the value of the month 0 = Januar
	 * @param in name or number of the month 1 or 'jan' or 'jnuary' is 0
	 * @return string as integer 0=jan, 11=dec
	 */
	public static int toMonth(String in) {
		try {
			int out = Integer.parseInt(in);
			if (out > 0 && out < 13) return out-1;
		} catch (Throwable t) {
		}
		in = in.toLowerCase();
		switch (in) {
		case "jan":
		case "januar":
		case "january":
			return 0;
		case "feb":
		case "februar":
		case "february":
			return 1;
		case "mrz":
		case "march":
		case "märz":
			return 2;
		case "apr":
		case "april":
			return 3;
		case "mai":
		case "may":
			return 4;
		case "jun":
		case "juni":
		case "june":
			return 5;
		case "jul":
		case "juli":
		case "july":
			return 6;
		case "aug":
		case "august":
			return 7;
		case "sep":
		case "september":
		case "septembre":
			return 8;
		case "okt":
		case "oct":
		case "oktober":
		case "october":
			return 9;
		case "nov":
		case "november":
			return 10;
		case "dez":
		case "dec":
		case "dezember":
		case "december":
			return 11;
		}
		throw new NumberFormatException(in);
	}

	private static int toint(String in, int def) {
		if (in == null) return def;
		try {
			return Integer.parseInt(in);
		} catch (NumberFormatException nfe) {
			return def;
		}
	}

}
