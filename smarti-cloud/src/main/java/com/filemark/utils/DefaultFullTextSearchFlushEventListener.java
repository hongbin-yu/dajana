package com.filemark.utils;

import org.hibernate.event.FlushEvent;
import org.hibernate.event.def.DefaultFlushEventListener;
import org.hibernate.search.event.FullTextIndexEventListener;

public class DefaultFullTextSearchFlushEventListener extends
	FullTextIndexEventListener {
	DefaultFlushEventListener defaultFlushEventListener = new DefaultFlushEventListener();
	@Override
	public void onFlush(FlushEvent event) {
		defaultFlushEventListener.onFlush(event);
		super.onFlush(event);
	}

	


}
