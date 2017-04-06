package DealJsonDate;

import java.util.ArrayList;
import java.util.List;

public class AnalyzeDate {
	private List<WriteStationDate> railDateList;
	private int size=0;
	/**
	 * 构造方法
	 * @param railDateList 集合 List<WriteStationDate> 内含车站所有信息
     */
	public AnalyzeDate(List<WriteStationDate> railDateList) {//构造方法

		this.railDateList=railDateList;
		size=railDateList.size();
	}

	/**
	 * 获取站点间时间间隔（秒）
	 * @return
     */
	public List<Integer> getTimeInterval(){
		List<Integer> timeList=new ArrayList<>();
		int i1=0;
		for(int i=0;i<size-1;i++){
			int time=railDateList.get(i+1).getArriveTime()-railDateList.get(i).getArriveTime();
			timeList.add(time);
			i1+=time;
		}
		return timeList;
	}

	/**
	 *
	 * @return 返回某条线路地铁走完所花时间（秒）
     */
	public int getRailTime(){

		int time=railDateList.get(size-1).getArriveTime()-railDateList.get(0).getArriveTime();

		return time;
	}
	/**
	 * 获取两站点间的直线斜率
	 * @return
     */
	public List<Float> getSlope() {
		List<Float>  slopeList=new ArrayList<>();
		for(int i=0;i<size-1;i++){
			float height=railDateList.get(i+1).getYcoord()-railDateList.get(i).getYcoord();
			float width =railDateList.get(i+1).getXcoord()-railDateList.get(i).getXcoord();
			float slope=height/width;
			slopeList.add(slope);
		}
		return slopeList ;
	}

	/**
	 * 获取站点Y坐标，注意Y坐标仍为自定义坐标

	 * @return
     */
	public List<Float> getListStopYcoord(){
		List<Float>  ListStopYcoord=new ArrayList<>();
		for (int i = 0; i <size; i++) {
			ListStopYcoord.add(railDateList.get(i).getYcoord());
		}
		return ListStopYcoord;
	}

	/**
	 * 获取站点X坐标，注意，X坐标仍为自定义坐标
	 * @return
     */
	public List<Float> getListStopXcoord(){
		List<Float>  ListStopXcoord=new ArrayList<>();
		for (int i = 0; i <size; i++) {
			ListStopXcoord.add(railDateList.get(i).getXcoord());
		}
		return ListStopXcoord;
	}

	/**
	 * 求两站点间距离(大于0)
	 * @param TargetOrderNum 此参数须大于0；
	 * @return
     */
	public float getDistance(int TargetOrderNum){
		if(TargetOrderNum<=0) throw new IndexOutOfBoundsException("TargetOrderNum须大于1");

        float x1=railDateList.get(TargetOrderNum-1).getXcoord();
		float x2=railDateList.get(TargetOrderNum).getXcoord();
		float y1=railDateList.get(TargetOrderNum-1).getYcoord();
		float y2=railDateList.get(TargetOrderNum).getYcoord();
		float distance= (float) Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
		return distance;
	}

	/**
	 * 获取两站点X（自定义）坐标上的距离（有正负）
	 * @param TargetOrderNum
	 * @return
     */
	public float getXDistance(int TargetOrderNum){
		if(TargetOrderNum<=0) throw new IndexOutOfBoundsException("TargetOrderNum须大于1");

		float x1=railDateList.get(TargetOrderNum-1).getXcoord();
		float x2=railDateList.get(TargetOrderNum).getXcoord();

		float distance= x2-x1;
		return distance;
	}

	/**
	 * 获取两站点Y(自定义)坐标上的距离（有正负）
	 * @param TargetOrderNum
	 * @return
     */
	public float getYDistance(int TargetOrderNum){
		if(TargetOrderNum<=0) throw new IndexOutOfBoundsException("TargetOrderNum须大于1");

		float y1=railDateList.get(TargetOrderNum-1).getYcoord();
		float y2=railDateList.get(TargetOrderNum).getYcoord();
		float distance= y2-y1;
		return distance;
	}


}
