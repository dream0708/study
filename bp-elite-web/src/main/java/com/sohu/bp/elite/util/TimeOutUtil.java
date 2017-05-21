package com.sohu.bp.elite.util;

import java.util.List;

import com.google.common.base.Function;

public class TimeOutUtil extends Thread
{
	private int timetoutPeriod;
	private Function timeOutHandler;
	
	private Object parameters;
	private boolean isStop;
	
	private TimeOutUtil timo = null;
	
	
	public int getTimetoutPeriod() {
		return timetoutPeriod;
	}

	public void setTimetoutPeriod(int timetoutPeriod) {
		this.timetoutPeriod = timetoutPeriod;
	}

	public Function getTimeOutHandler() {
		return timeOutHandler;
	}

	public void setTimeOutHandler(Function timeOutHandler) {
		this.timeOutHandler = timeOutHandler;
	}

	public Object getParameters() {
		return parameters;
	}

	public void setParameters(Object parameters) {
		this.parameters = parameters;
	}

	public boolean isStop() {
		return isStop;
	}

	public void setStop(boolean isStop) {
		this.isStop = isStop;
	}

	public TimeOutUtil getTimo() {
		return timo;
	}

	public void setTimo(TimeOutUtil timo) {
		this.timo = timo;
	}

	public TimeOutUtil(int timeOutPeriod, Function timeOutHandler)
	{
		this.timetoutPeriod = timeOutPeriod;
		this.timeOutHandler = timeOutHandler;
		this.isStop = false;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try
		{
			Thread.sleep(this.timetoutPeriod);
			System.out.println("[timout]");
			timeOutHandler.apply(this.parameters);
		}catch(Exception e)
		{
			
		}
	}
	
	public void reset(Object parameters){
		destroy();
		this.timo = new TimeOutUtil(this.timetoutPeriod, this.timeOutHandler);
		this.timo.setParameters(parameters);
		this.timo.start();
	}
	
	public void destroy()
	{
		if(this.timo != null)
		{
			this.timo.yield();
			this.timo.interrupt();
			try {
				this.timo.finalize();
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static void main(String[] args)
	{
		TimeOutUtil timo = new TimeOutUtil(2000, new Function<String, Boolean>(){
			@Override
			public Boolean apply(String msgList) {
				// TODO Auto-generated method stub
				System.out.println(msgList);
				return true;
			}});
		
		timo.reset("aaaaaaaa");
	}
}