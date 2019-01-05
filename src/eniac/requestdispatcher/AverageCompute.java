package eniac.requestdispatcher;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AverageCompute {
	private Map<String, Date> startTime;
	private List<Long> times;
	private int number;
	
	public AverageCompute(int number) {
		startTime = new HashMap<>();
		times = new ArrayList<>();
		this.number = number;
	}
	
	public AverageCompute() {
		this(0);
	}
	
	public void addStartTime(String rq, Date d) {
		startTime.put(rq, d);
	}
	
	public void addEndTime(String rq, Date d) {
		times.add(d.getTime() - startTime.remove(rq).getTime());
		if(number == 0) return;
		if(times.size() > number) {
			times.remove(0);
		}
	}
	
	
	
	public int getAverage() {
		if(times.size() == 0) return 0;
		int avg = 0;
		for(int i = 0; i < times.size(); i++) {
			avg += times.get(i);
		}
		return avg/times.size();
	}
}
