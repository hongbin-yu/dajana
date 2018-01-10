package com.filemark.utils;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.TermQuery;
import org.hibernate.search.annotations.Factory;
import org.hibernate.search.annotations.Key;
import org.hibernate.search.filter.CachingWrapperFilter;
import org.hibernate.search.filter.FilterKey;
import org.hibernate.search.filter.StandardFilterKey;

public class DrawFilterFactory {
	private String draw;

	
	
	
    @Key
    public FilterKey getKey() {
        StandardFilterKey key = new StandardFilterKey();
        key.addParameter( draw );
        return key;
    }

    @Factory
    public Filter getFilter() throws ParseException {
    	//BooleanQuery query = new BooleanQuery();
		QueryParser qParser = new QueryParser(null, "doc.fold.draw.name",new StandardAnalyzer(null));
        Query query = null;
//		try {
			query = qParser.parse(draw.replaceAll(":", " ").replaceAll(" ", "+"));
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
      	
//        TermQuery query = new TermQuery( new Term("doc.fold.draw.name", draw ) );
        //query.add(new BooleanClause(appFilter, BooleanClause.Occur.MUST));

        
  
        return new CachingWrapperFilter( new QueryWrapperFilter(query) );
    }

	public String getDraw() {
		return draw;
	}

	public void setDraw(String draw) {
		this.draw = draw;
	}



	
}
