package capsrock.beta;

import java.util.ArrayList;
import java.util.Calendar;

//Interface of objects for data storage
public interface Structures {
	
	//Contains the data that is sent to database
	public class WebTimeEntry {
		public TimeEntry te;
		public String location;
		public Calendar date;
		
		public WebTimeEntry(TimeEntry te, String location) {
			this.te = te;
			this.location = location;
			date = Calendar.getInstance();
		}
	}
	
	//The simplest entry, 2 timestamps and type
	public class TimeEntry {
		Calendar startTime;
		Calendar endTime;
		boolean workTime;
		
		public TimeEntry() {
			startTime = Calendar.getInstance();
			endTime = null;
			workTime = true;
		}
		
		public TimeEntry(boolean work) {
			startTime = Calendar.getInstance();
			endTime = null;
			workTime = work;
		}
		
		public void AddEndTime() {
			endTime = Calendar.getInstance();
		}
	}
	
	//Holds geo-fence information
	//Not yet implemented
	public class GLocation {
		String address;
		String name;
		
		public GLocation() {
			address = "here";
			name = "there";
		}
		
		public GLocation(String add, String nm) {
			address = new String(add);
			name = new String(nm);
		}
	}
	
	//Combines locations and time entries
	public class LocationTimeSheet {
		public GLocation location;
		ArrayList<TimeEntry> timeEntries;
		public int index;
		
		public LocationTimeSheet() {
			location = new GLocation("hry","grr");
			timeEntries = new ArrayList<TimeEntry>();
			index = -1;
		}
		
		public LocationTimeSheet(GLocation loc) {
			location = loc;
			timeEntries = new ArrayList<TimeEntry>();
			index = -1;
		}
		
		public void AddTimeEntry(TimeEntry entry) {
			index = timeEntries.size();
			timeEntries.add(entry);
		}
		public TimeEntry GetOpenEntry() {
			if (index > -1)
				return timeEntries.get(index);
			return null;
		}
	}
	
	//A daily record of LocationTimeSheets
	//These will be returned from server
	public class TimeSheet {
		Calendar date;
		ArrayList<LocationTimeSheet> locaSheets;
		
		public TimeSheet() {
			date = Calendar.getInstance();
			locaSheets = new ArrayList<LocationTimeSheet>();
		}
		
		public void AddLocaSheet(LocationTimeSheet loca) {
			locaSheets.add(loca);
		}
		
		public LocationTimeSheet findLocaSheet(String locationString) {
			for (int i = 0; i < locaSheets.size(); i++) {
				if (locaSheets.get(i).location.name.equals(locationString)){
					return locaSheets.get(i);
				}
			}
			return null;
		}
	}
}