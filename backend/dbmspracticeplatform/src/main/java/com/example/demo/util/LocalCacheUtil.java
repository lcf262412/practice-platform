package com.example.demo.util;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LocalCacheUtil {
	
	private static ConcurrentMap<String, LocalCacheData> cacheRepository = new ConcurrentHashMap();
	
	private static class LocalCacheData {
		private Object val;
		private long timeoutTime;
		
		public LocalCacheData(Object val, long timeoutTime) {
			this.val = val;
			this.timeoutTime = timeoutTime;
		}
		
		public void setVal(Object val) {
			this.val = val;
		}
		
		public void setTimeoutTime(long timeoutTime) {
			this.timeoutTime = timeoutTime;
		}
		
		public Object getVal() {
			return this.val;
		}
		
		public long getTimeoutTime() {
			return this.timeoutTime;
		}
	}
	
	public static boolean set(String key, Object val, long cacheTime) {
		cleanTimeoutCache();
		
		if (key == null || key.trim().length() == 0)
			return false;
		if (val == null)
			remove(key);
		if (cacheTime < 0)
			remove(key);
		
		long timeoutTime = System.currentTimeMillis() + cacheTime;
		LocalCacheData localCacheData = new LocalCacheData(val, timeoutTime);
		cacheRepository.put(key, localCacheData);
		return true;
	}
	
	public static boolean remove(String key) {
		if (key == null || key.trim().length() == 0) {
			return false;
		}
		cacheRepository.remove(key);
		return true;
	}
	
	public static Optional<Object> get(String key) {
		if (key == null || key.trim().length() == 0) {
			return Optional.empty();
		}
		LocalCacheData localCacheData = cacheRepository.get(key);
		if (localCacheData != null && System.currentTimeMillis()<localCacheData.getTimeoutTime()) {//没超时
			if (System.currentTimeMillis() >= (localCacheData.getTimeoutTime()-60*1000))    
			    localCacheData.setTimeoutTime(System.currentTimeMillis()+4*60*60*1000);//延长时间
			
			return Optional.ofNullable(localCacheData.getVal());
		} else {
			remove(key);
			return Optional.empty();
		}
	}
	
	public static boolean cleanTimeoutCache() {
		if (!cacheRepository.keySet().isEmpty()) {
			for (String key : cacheRepository.keySet()) {
				LocalCacheData localCacheData = cacheRepository.get(key);
				if (localCacheData != null && System.currentTimeMillis()>=localCacheData.getTimeoutTime()) {
					cacheRepository.remove(key);
				}
			}
		}
		return true;
	}

}
