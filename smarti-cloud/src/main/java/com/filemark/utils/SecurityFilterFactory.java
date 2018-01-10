package com.filemark.utils;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.TermQuery;
import org.hibernate.search.annotations.Factory;
import org.hibernate.search.annotations.Key;
import org.hibernate.search.filter.CachingWrapperFilter;
import org.hibernate.search.filter.FilterKey;
import org.hibernate.search.filter.StandardFilterKey;
import org.jfree.util.Log;

public class SecurityFilterFactory {
	private String security;
	
	
	
    @Key
    public FilterKey getKey() {
        StandardFilterKey key = new StandardFilterKey();
       	key.addParameter( security );
        return key;
    }

    @Factory
    public Filter getFilter() throws ParseException {
    	BooleanQuery query = new BooleanQuery();

      QueryParser pParser = new QueryParser(null, "security",new StandardAnalyzer(null));
        //try {
			query.add(new BooleanClause(pParser.parse(security),BooleanClause.Occur.MUST));
		//} catch (ParseException e) {
		//	Log.error(e.toString());
		//}
       	
        //Term appFilter = new Term("security", security );
        //query.add(new PrefixQuery(appFilter), BooleanClause.Occur.MUST);
        return new CachingWrapperFilter( new QueryWrapperFilter(query) );
    }

	public String getSecurity() {
		return security;
	}

	public void setSecurity(String security) {
		this.security = security;
	}



	
}
