package org.project.pack.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

@Service
public class MetricsService {
	MeterRegistry mr;
	Map<String, Counter> counters;
	Map<String, Gauge> gauges;
	Map<String, Number> gauges_supplier;
	
	@Autowired
	public MetricsService(MeterRegistry mr) { 
		this.mr = mr; 		
		counters = new HashMap<String, Counter>();
		gauges = new HashMap<String, Gauge>();
		gauges_supplier = new HashMap<String, Number>();
	}
	
	public void IncreamentCounter(String key) {
		Counter counter = counters.get(key);
		if(counter == null) {
			counter = mr.counter(key, "original", "data");
			counters.put(key, counter);
		}
		counter.increment();
	}
	
	public void SetGauge(String key, Number data) {
		Gauge gauge = gauges.get(key);
		if(gauge == null) {
			gauges_supplier.put(key, 0.0);
			gauge = 
				Gauge.builder(key, ()->gauges_supplier.get(key))
				.register(mr);
			gauges.put(key, gauge);
		}
		gauges_supplier.put(key, data);
	}

}
