package com.filemark.utils;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.TermQuery;
import org.hibernate.search.annotations.Factory;
import org.hibernate.search.annotations.Key;
import org.hibernate.search.filter.CachingWrapperFilter;
import org.hibernate.search.filter.FilterKey;
import org.hibernate.search.filter.StandardFilterKey;

public class AppFilterFactory {
	private String app;

	
	
	
    @Key
    public FilterKey getKey() {
        StandardFilterKey key = new StandardFilterKey();
        key.addParameter( app );
        return key;
    }

    @Factory
    public Filter getFilter() {
    	//BooleanQuery query = new BooleanQuery();
        	
        TermQuery query = new TermQuery( new Term("doc.fold.draw.app.name", app ) );
        //query.add(new BooleanClause(appFilter, BooleanClause.Occur.MUST));

        
  
        return new CachingWrapperFilter( new QueryWrapperFilter(query) );
    }

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}



	
}
