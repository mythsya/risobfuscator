package com.agfa.he.sh.cris.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.agfa.he.sh.common.util.DateUtil;
import com.agfa.he.sh.common.util.JacksonUtil;
import com.agfa.he.sh.cris.AppConstants;
import com.agfa.he.sh.cris.domain.MasterPatient;


public class JobContainer {
	
    /**
     * The default thread factory
     */
    static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = "ris-" + poolNumber.getAndIncrement() + "-job-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                                  namePrefix + threadNumber.getAndIncrement(),
                                  0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }	
	
	public static enum JobStatus {
		NEW("new"),
		RUNNING("running"),
		WAITING("waiting"),
		STOP("stop"),
		CANCELLED("cancelled"),
		UNKNOWN("unknown");
		
		private String value;
		JobStatus(String v) {
			this.value = v;
		}
		
		public String value() {
			return this.value;
		}
		
		public static JobStatus from(String v) {
			for(JobStatus st : JobStatus.values()) {
				if (st.value.equals(v)) {
					return st;
				}
			}
			return JobStatus.UNKNOWN;
		}
	}
	
	public static class JobSummary {
		private JobStatus status;
		private Timestamp startAt;
		private Timestamp endAt;
		private int total;
		private int processed;
		private int remains;
		
		public JobStatus getStatus() {
			return status;
		}
		public void setStatus(JobStatus status) {
			this.status = status;
		}
		public Timestamp getStartAt() {
			return startAt;
		}
		public void setStartAt(Timestamp startAt) {
			this.startAt = startAt;
		}
		public Timestamp getEndAt() {
			return endAt;
		}
		public void setEndAt(Timestamp endAt) {
			this.endAt = endAt;
		}
		public int getTotal() {
			return total;
		}
		public void setTotal(int total) {
			this.total = total;
		}
		public int getProcessed() {
			return processed;
		}
		public void setProcessed(int processed) {
			this.processed = processed;
		}
		public int getRemains() {
			return remains;
		}
		public void setRemains(int remains) {
			this.remains = remains;
		}
	}
	
	public static class JobDetail {
		private Timestamp startAt;
		private Timestamp endAt;
		private int total;
		private int processed;
		private int remains;
		private int pos;
		private int step;
		private int start;
		private int to;
		
		public Timestamp getStartAt() {
			return startAt;
		}
		public void setStartAt(Timestamp startAt) {
			this.startAt = startAt;
		}
		public Timestamp getEndAt() {
			return endAt;
		}
		public void setEndAt(Timestamp endAt) {
			this.endAt = endAt;
		}
		public int getTotal() {
			return total;
		}
		public void setTotal(int total) {
			this.total = total;
		}
		public int getProcessed() {
			return processed;
		}
		public void setProcessed(int processed) {
			this.processed = processed;
		}
		public int getRemains() {
			return remains;
		}
		public void setRemains(int remains) {
			this.remains = remains;
		}
		public int getCurrentPosition() {
			return pos;
		}
		public void setCurrentPosition(int pos) {
			this.pos = pos;
		}
		public int getCurrentStep() {
			return step;
		}
		public void setCurrentStep(int step) {
			this.step = step;
		}
		public int getStart() {
			return start;
		}
		public void setStart(int start) {
			this.start = start;
		}
		public int getTo() {
			return to;
		}
		public void setTo(int to) {
			this.to = to;
		}
	}
	
	class WorkerThread extends Thread{
		
		private final ThreadPoolExecutor executor;
		private final JobDetail jobDetail;
		private final int index;		
		
		public WorkerThread(String name, ThreadPoolExecutor executor, JobDetail jd, int idx) {			
			this.executor = executor;
			this.jobDetail = jd;
			this.index = idx;
			this.setName(name);
		}
		
		public JobDetail getJobDetail() {
			return this.jobDetail;
		}
		
		@Override
		public void run() {
			int start = jobDetail.getCurrentPosition(), step = jobDetail.getCurrentStep();
			int end = start+step-1;
			end = end > jobDetail.getTo() ? jobDetail.getTo() : end;
			if (logger.isInfoEnabled()) {
				logger.info(getName()+" is processing patients ["+start+" - "+end+"], progress="+jobDetail.getProcessed()+"/"+jobDetail.getTotal());
			}
					
			List<MasterPatient> patients = patientInfoService.queryPatientByRange(start, end);
			int processed = 0;
			
			try {
				for(MasterPatient p : patients) {
					patientInfoService.obfuscatePatientInfo(p);
					processed ++;
				}
			} catch(Exception e) {
				logger.error("failed to process patients, caused by ", e);
			} finally {
				updateProcessed(processed);				
			}
		}
		
		private void updateProcessed(int processed) {
			jobDetail.setCurrentPosition(jobDetail.getCurrentPosition()+processed);
			jobDetail.setProcessed(jobDetail.getProcessed()+processed);
			jobDetail.setRemains(jobDetail.getTotal()-jobDetail.getProcessed());
			
			if (jobDetail.getRemains() <= 0) {
				jobDetail.setEndAt(DateUtil.getTimestampCurrent());
				
				ScheduledFuture<?> f = futures.get(String.valueOf(index));
				if (f != null) f.cancel(false);
				
				if (logger.isInfoEnabled()) {
					logger.info(getName()+" completed, progress="+jobDetail.getProcessed()+"/"+jobDetail.getTotal());
				}
			}
			
			saveJobDetail(index, jobDetail);
			
			synchronized(lock) {
				saveJobStatus(JobStatus.RUNNING);
				
				JobSummary summary = getJobSummary();
				if (summary != null) {
					summary.setStatus(JobStatus.RUNNING);
					summary.setProcessed(summary.getProcessed()+processed);
					summary.setRemains(summary.getTotal()-summary.getProcessed());
					
					if (summary.getRemains() <= 0) {
						summary.setEndAt(DateUtil.getTimestampCurrent());
						summary.setStatus(JobStatus.STOP);
						
						saveJobStatus(JobStatus.STOP);
					}
					
					saveJobSummary(summary);
				}
			}			
		}
	}

	private final static Logger logger = LoggerFactory.getLogger(JobContainer.class);
	
	private final int batchFetchSize = 500; 
	
	private final int availableProcessors = Runtime.getRuntime().availableProcessors();
	
	private final ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(availableProcessors*2, new DefaultThreadFactory());
	
	//@Autowired
	//private StringRedisTemplate stringRedisTemplate;
	
	private final ConcurrentHashMap<String, String> stringValueCache = new ConcurrentHashMap<String,String>();
	
	@Autowired
	private PatientInfoService patientInfoService;
	
	private final AtomicBoolean scheduled = new AtomicBoolean(false);
	
	private final Object lock = new Object();
	
	private final ConcurrentHashMap<String,ScheduledFuture<?>> futures = new ConcurrentHashMap<String,ScheduledFuture<?>>();

	public void init() {
		if (logger.isInfoEnabled()) {
			logger.info("JobContainer inited.");
		}
	}
	
	public void dispose() {
		scheduler.shutdown();
		
		if (logger.isInfoEnabled()) {
			logger.info("JobContainer disposed.");
		}
	}
	
	public String start(boolean forceRestart, int parallelism) {
		boolean shouldRestart = false;
		if (forceRestart) {
			shouldRestart = true;
		} else {
			JobStatus status = getJobStatus();
			if (status==null || status.equals(JobStatus.UNKNOWN) || status.equals(JobStatus.CANCELLED)) {
				shouldRestart = true;
			}
		}
		
		if (shouldRestart) {
			//clear runtime context
			clearRuntimeContext();
			
			//initialized new jobs
			scheduled.set(false);
			
			//attemp to stop all scheduled jobs
			for(Entry<String, ScheduledFuture<?>> entry : futures.entrySet()) {
				entry.getValue().cancel(true);
			}
		}
		
		// check whether all tasks have been scheduled to running
		if (!scheduled.get()) {
			int jobCount = getJobParallelism();
			JobDetail[] jobDetails;
			if (jobCount <= 0) {
				jobCount = parallelism;
				jobDetails = new JobDetail[jobCount];
				int patientCount = patientInfoService.countMasterPatient();
				int avgCountPerJob = (patientCount+jobCount-1)/jobCount;
				Timestamp now = DateUtil.getTimestampCurrent();
				for(int i=0; i<jobCount; i++) {
					int total = avgCountPerJob;
					if (i == jobCount -1) {
						total = patientCount - avgCountPerJob*i;
					}
					JobDetail jd = new JobDetail();
					jd.setStartAt(now);
					jd.setEndAt(null);
					jd.setTotal(total);
					jd.setProcessed(0);
					jd.setRemains(total);					
					jd.setStart(1+(avgCountPerJob*i));
					jd.setTo(jd.getStart()+total-1);
					jd.setCurrentPosition(jd.getStart());
					jd.setCurrentStep(batchFetchSize);
					jobDetails[i] = jd;
					
					saveJobDetail(i+1, jd);
				}
				
				saveJobParallelism(jobCount);
				saveJobStatus(JobStatus.NEW);
				
				JobSummary summary = new JobSummary();
				summary.setStartAt(now);
				summary.setEndAt(null);
				summary.setStatus(JobStatus.NEW);
				summary.setTotal(patientCount);
				summary.setProcessed(0);
				summary.setRemains(patientCount);
				saveJobSummary(summary);
				
			} else {
				jobDetails = new JobDetail[jobCount];
				for(int i=0; i< jobCount; i++) {
					jobDetails[i] = getJobDetail(i+1);
				}
			}
			
			futures.clear();
			
			for(int i=0; i<jobCount; i++) {
				ScheduledFuture<?> f =scheduler.scheduleAtFixedRate(new WorkerThread("job-thread-"+(i+1), scheduler, jobDetails[i], i+1), 1000, 2000, TimeUnit.MILLISECONDS);
				futures.put(String.valueOf(i+1), f);
			}
			
			scheduled.set(true);
		}
		
		return getValue(AppConstants.KEY_JOB_RUNTIME_SUMMARY);
	}
	
	public String stop() {
		//attemp to stop all scheduled jobs
		for(Entry<String, ScheduledFuture<?>> entry : futures.entrySet()) {
			entry.getValue().cancel(true);
		}
		
		synchronized(lock) {
			saveJobStatus(JobStatus.STOP);
			
			JobSummary summary = getJobSummary();
			if (summary != null) {
				summary.setStatus(JobStatus.STOP);				
				
				saveJobSummary(summary);
			}
		}
		
		scheduled.set(false);
		
		return getValue(AppConstants.KEY_JOB_RUNTIME_SUMMARY);
	}
	
	public String cancel() {
		//attemp to stop all scheduled jobs
		for(Entry<String, ScheduledFuture<?>> entry : futures.entrySet()) {
			entry.getValue().cancel(true);
		}
		
		//clear runtime context
		clearRuntimeContext();
		
		synchronized(lock) {
			saveJobStatus(JobStatus.CANCELLED);
			
			JobSummary summary = getJobSummary();
			if (summary != null) {
				summary.setStatus(JobStatus.CANCELLED);
				summary.setEndAt(DateUtil.getTimestampCurrent());
				
				saveJobSummary(summary);
			}
		}
		
		scheduled.set(false);
		
		return getValue(AppConstants.KEY_JOB_RUNTIME_SUMMARY);
	}
	
	public String status() {
		return getValue(AppConstants.KEY_JOB_RUNTIME_SUMMARY);
	}
	
	private void clearRuntimeContext() {
		int parallel = getJobParallelism();
		if (parallel > 0) {
			for(int i=1; i<=parallel; i++){
				removeKey(AppConstants.KEY_JOB_RUNTIME_PREFIX+i);
			}
		}
		removeKey(AppConstants.KEY_JOB_PARALLELISM);
		removeKey(AppConstants.KEY_JOB_STATUS);
	}
	
	
	
	private JobStatus getJobStatus() {
		String st = getValue(AppConstants.KEY_JOB_STATUS);
		if (st != null) {
			return JobStatus.from(st);
		}
		return JobStatus.UNKNOWN;
	}
	
	private void saveJobStatus(JobStatus st) {
		if (st == null) st=JobStatus.UNKNOWN;
		setKeyValue(AppConstants.KEY_JOB_STATUS, st.value());
	}
	
	private JobSummary getJobSummary() {
		String st = getValue(AppConstants.KEY_JOB_RUNTIME_SUMMARY);
		if (st != null) {
			return JacksonUtil.readValue(st, JobSummary.class);
		}
		return null;
	}
	
	private void saveJobSummary(JobSummary summary) {
		if (summary != null) {
			String jsonStr = JacksonUtil.toJson(summary);
			setKeyValue(AppConstants.KEY_JOB_RUNTIME_SUMMARY, jsonStr);
		}
	}
	
	private JobDetail getJobDetail(int idx) {
		String st = getValue(AppConstants.KEY_JOB_RUNTIME_PREFIX+idx);
		if (st != null) {
			return JacksonUtil.readValue(st, JobDetail.class);
		}
		return null;
	}
	
	private void saveJobDetail(int idx, JobDetail jd) {
		if (idx > 0 && jd!=null) {
			String jsonStr = JacksonUtil.toJson(jd);
			setKeyValue(AppConstants.KEY_JOB_RUNTIME_PREFIX+idx, jsonStr);
		}
	}
	
	private int getJobParallelism() {
		String v  = getValue(AppConstants.KEY_JOB_PARALLELISM);
		if (v == null || v.isEmpty()) {
			return 0;
		}
		return Integer.parseInt(v);
	}
	
	private void saveJobParallelism(int v) {
		setKeyValue(AppConstants.KEY_JOB_PARALLELISM, String.valueOf(v));
	}
	
	private boolean isKeyExists(String key) {
		//return stringRedisTemplate.hasKey(key);
		return stringValueCache.containsKey(key);
	}
	
	private void setKeyValue(String key, String val) {
		if (key!=null && !key.isEmpty() && val != null && !val.isEmpty()) {
			//stringRedisTemplate.opsForValue().set(key, val);
			stringValueCache.put(key,val);
		}
	}
	
	private String getValue(String key) {
		//return stringRedisTemplate.opsForValue().get(key);
		return stringValueCache.get(key);
	}
	
	private void removeKey(String key) {
		//stringRedisTemplate.delete(key);
		stringValueCache.remove(key);
	}
	
}
