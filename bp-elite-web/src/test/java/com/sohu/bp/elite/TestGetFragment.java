package com.sohu.bp.elite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapterFactory;
import com.sohu.bp.decoration.adapter.BpMediaArticleServiceAdapter;

public class TestGetFragment
{
	private static Logger logger = (Logger)LoggerFactory.getLogger(TestGetFragment.class);
	private static BpMediaArticleServiceAdapter articleServiceAdapter = BpDecorationServiceAdapterFactory.getBpMediaArticleServiceAdapter();
	public static void getAdFragment()
	{
		//15 右侧 16 底部
		try
		{
			String sideContent = articleServiceAdapter.getPageFragDataWithArea(15, 0);
			logger.info(sideContent);
			
			String bottomContent = articleServiceAdapter.getPageFragDataWithArea(16, 0);
			logger.info(bottomContent);
		}catch(Exception e)
		{
			logger.error("", e);
		}
		
	}
	
	public static void main(String[] args)
	{
		getAdFragment();
	}
}