package com.sohu.bp.elite.util;

import java.util.Timer;
import java.util.TimerTask;

import com.google.common.base.Function;

public class TimerManager
{
	static Timer timer1=new Timer();
	TimerTask task1=null;
	private Function timeOutHandler;
	private int timetoutPeriod;
	private Object parameters;
	
	public int getTimetoutPeriod() {
		return timetoutPeriod;
	}
	public void setTimetoutPeriod(int timetoutPeriod) {
		this.timetoutPeriod = timetoutPeriod;
	}
	public Object getParameters() {
		return parameters;
	}
	public void setParameters(Object parameters) {
		this.parameters = parameters;
	}
	public Function getTimeOutHandler() {
		return timeOutHandler;
	}
	public void setTimeOutHandler(Function timeOutHandler) {
		this.timeOutHandler = timeOutHandler;
	}
	
	public TimerManager(int timeOutPeriod, Function timeOutHandler)
	{
		this.timetoutPeriod =timeOutPeriod;
		this.timeOutHandler = timeOutHandler;
	}
	public void reset(final Object parameters){
		stop();
		this.task1=new TimerTask(){
		    public void run() {
		     // TODO Auto-generated method stub
		    System.out.println("timout");
		    timeOutHandler.apply(parameters);
		    }
        };
        timer1.schedule(this.task1, this.timetoutPeriod);	
	}
	
	public void stop()
	{
		if(this.task1 != null)
		{
			this.task1.cancel();
			timer1.purge();
		}
	}
}