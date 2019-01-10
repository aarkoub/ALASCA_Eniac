package eniac.requestdispatcher;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AverageCompute {
	private Map<String, Date> startTime;
	private int average;
	public static final double ALPHA = 0.1;
	
	public AverageCompute() {
		startTime = new HashMap<>();
		average = 0;
	}
	
	
	public void addStartTime(String rq, Date d) {
		startTime.put(rq, d);
	}
	
	public void addEndTime(String rq, Date d) {
		int avgraw = (int)(d.getTime() - startTime.remove(rq).getTime());
		average = (int)(ALPHA*avgraw + (1.0-ALPHA)*average);
	}
	
	
	
	public int getAverage() {
		return average;
	}
}
