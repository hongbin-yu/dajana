/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */
/* Created on Jul 18, 2003 */
package com.filemark.search.operation;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;

import com.filemark.search.FieldConstants;
import com.filemark.search.IndexManager;
//import org.apache.lucene.util.Version;
import com.filemark.search.IndexUtil;



/**
 * An operation that searches the index.
 * @author Mindaugas Idzelis (min@idzelis.com)
 */
public class SearchOperation extends ReadFromIndexOperation {

    //~ Static fields/initializers =============================================
    
    private static Log mLogger =
            LogFactory.getFactory().getInstance(SearchOperation.class);
    
    private static String[] SEARCH_FIELDS = new String[] {
        FieldConstants.ID,
    	FieldConstants.FOLD,
        FieldConstants.DOC,
    	FieldConstants.INDEX1,
        FieldConstants.CONTENT,
                       
    };

    private static BooleanClause.Occur[] SEARCH_FLAGS = new BooleanClause.Occur[] {
        BooleanClause.Occur.SHOULD, 
    	BooleanClause.Occur.SHOULD, 
        BooleanClause.Occur.SHOULD, 
        BooleanClause.Occur.SHOULD, 
        BooleanClause.Occur.SHOULD
    };

    private static Sort SORTER = new Sort( new SortField(
            FieldConstants.ID, SortField.STRING, false) );
    
    //~ Instance fields ========================================================
    
    private String term;
    private String filter = null;
    private String security;
    private String sort = FieldConstants.ID;
    private Query query = null;
    
    private TopDocs searchresults;
    private String parseError;
    private int maxCount = 10;
    
    //~ Constructors ===========================================================
    
    /**
     * Create a new operation that searches the index.
     */
    public SearchOperation(IndexManager mgr) {
        // TODO: finish moving  IndexManager to backend, so this cast is not needed
        super(mgr);
    }
    
    //~ Methods ================================================================
    
    public void setTerm(String term) {
        this.term = term;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void doRun() {
        searchresults = null;
        Filter sidebarfilter = null;
        IndexSearcher searcher = null;
        
        try {
            IndexReader reader = manager.getSharedIndexReader();
            searcher = new IndexSearcher(reader);
            if(query == null) {
                query = MultiFieldQueryParser.parse(Version.LUCENE_30, term,
                        SEARCH_FIELDS, SEARCH_FLAGS,
                        new StandardAnalyzer(Version.LUCENE_30));            	
            }

            
            Term termsecurity = IndexUtil.getTerm(FieldConstants.SECURITY, security);
            Term deletedTerm = IndexUtil.getTerm(FieldConstants.DELETED, "1");
            if(BooleanQuery.getMaxClauseCount() < 4096)
            	BooleanQuery.setMaxClauseCount(4096);

/*            BooleanQuery bQuery = new BooleanQuery();
            bQuery.add(query, BooleanClause.Occur.MUST);
            bQuery.add(new TermQuery(deletedTerm), BooleanClause.Occur.MUST_NOT);
            query = bQuery;  */          
            if (termsecurity != null) {
                BooleanQuery securityQuery = new BooleanQuery();
                securityQuery.add(new PrefixQuery(termsecurity), BooleanClause.Occur.MUST);
                securityQuery.add(new TermQuery(deletedTerm), BooleanClause.Occur.MUST_NOT);
                
                if(filter!=null) {
                    QueryParser pParser = new QueryParser(Version.LUCENE_30, "security",new StandardAnalyzer(null));
                    securityQuery.add(new BooleanClause(pParser.parse(filter),BooleanClause.Occur.MUST));
                }                               
                sidebarfilter = new QueryWrapperFilter(securityQuery);
                mLogger.debug("filter:"+securityQuery.toString());  
                //query = bQuery;
            }
            
/*            if(filter!=null) {
            	BooleanQuery bQuery = new BooleanQuery();
                bQuery.add(query, BooleanClause.Occur.MUST);
                QueryParser pParser = new QueryParser("security",new StandardAnalyzer());
                bQuery.add(new BooleanClause(pParser.parse(filter),BooleanClause.Occur.MUST));
                query = bQuery;
            }*/
            
            mLogger.debug("query:"+query.toString());        
            searchresults = searcher.search(query, sidebarfilter,maxCount,SORTER);//.search(query, null/*Filter*/, new SortField(sort,SortField.STRING, true));

        } catch (IOException e) {
            mLogger.error("Error searching index", e);
            parseError = e.getMessage();

        } catch (ParseException e) {
            // who cares?
        	mLogger.error("Error searching index", e);
            parseError = e.getMessage();
        } catch (Exception e) {
        	mLogger.error("Error searching index", e);
            parseError = e.getMessage();        	
        }
        // don't need to close the reader, since we didn't do any writing!
    }
    
    public TopDocs getResults() {
        return searchresults;
    }
    
    public int getResultsCount() {
        if (searchresults == null) return -1;
        
        return searchresults.totalHits;
    }
    
    public String getParseError() {
        return parseError;
    }

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public int getMaxCount() {
		return maxCount;
	}

	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}

	public String getTerm() {
		return term;
	}

	public Query getQuery() {
		return query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	public String getSecurity() {
		return security;
	}

	public void setSecurity(String security) {
		this.security = security;
	}



    

}
