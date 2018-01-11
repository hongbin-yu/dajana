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

package com.filemark.search;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.springframework.transaction.annotation.Transactional;

import com.filemark.jcr.model.Folder;
import com.filemark.jcr.service.JcrServices;
import com.filemark.runnable.ThreadManager;
//import com.filemark.search.operation.InitRepository;
import com.filemark.search.operation.IndexOperation;
import com.filemark.search.operation.WriteToIndexOperation;



/**
 * Lucene implementation of IndexManager. This is the central entry point 
 * into the Lucene searching API.
 * @author Mindaugas Idzelis (min@idzelis.com)
 * @author mraible (formatting and making indexDir configurable)
 */

@Transactional(readOnly = true)
public class IndexManagerImpl implements IndexManager {
    //~ Static fields/initializers
    // =============================================
	@Inject
    private ThreadManager threadManager;
    @Inject
	private JcrServices jcrServiceImpl; 
    //@Inject
	//private SessionFactory sessionFactory;
    private IndexReader reader;
    

    
    static Log mLogger = LogFactory.getFactory().getInstance(
            IndexManagerImpl.class);
    
    //~ Instance fields
    // ========================================================
    
    private boolean searchEnabled = true;
    
    File indexConsistencyMarker;
    
    private boolean useRAMIndex = false;
    
    private RAMDirectory fRAMindex;
    
    private String indexDir = null;
    
    private boolean inconsistentAtStartup = false;
    
    private ReadWriteLock rwl = new ReentrantReadWriteLock();
    
    //~ Constructors
    // ===========================================================
    
    /**
     * Creates a new lucene index manager. This should only be created once.
     * Creating the index manager more than once will definately result in
     * errors. The preferred way of getting an index is through the
     * RollerContext.
     *
     * @param indexDir -
     *            the path to the index directory
     */

    protected IndexManagerImpl() {

        
        // we also need to know what our index directory is
        // Note: system property expansion is now handled by WebloggerConfig

        
        // a little debugging
        mLogger.info("search enabled: " + this.searchEnabled);
        mLogger.info("index dir: " + this.indexDir);
        
        String test = indexDir + File.separator + ".index-inconsistent";
        indexConsistencyMarker = new File(test);

    }
    
    
    /**
     * @inheritDoc
     */
    public void initialize() {
        // only initialize the index if search is enabled
    	try {
        if (this.searchEnabled && indexDir != null) {

            mLogger.info("search enabled: " + this.searchEnabled);
            mLogger.info("index dir: " + this.indexDir);
            
            String test = indexDir +"/reindex/"+ File.separator + ".index-inconsistent";
            indexConsistencyMarker = new File(test);
            
            // 1. If inconsistency marker exists.
            //     Delete index
            // 2. if we're using RAM index
            //     load ram index wrapper around index
            //
            if (indexConsistencyMarker.exists()) {
                getFSDirectory(true);
                inconsistentAtStartup = true;
                mLogger.debug("Index inconsistent: marker exists");
            } else {
                try {
                    File makeIndexDir = new File(indexDir+"/reindex/");
                    if (!makeIndexDir.exists()) {
                        makeIndexDir.mkdirs();
                        inconsistentAtStartup = true;
                        indexConsistencyMarker.createNewFile();
                        mLogger.debug("Index inconsistent: new");
                    }

                } catch (IOException e) {
                    mLogger.error(e);
                }
            }
            
            if (indexExists()) {
                if (useRAMIndex) {
                    Directory filesystem = getFSDirectory(false);                    
                    try {
                        fRAMindex = new RAMDirectory(filesystem);
                    } catch (IOException e) {
                        mLogger.error("Error creating in-memory index", e);
                    }
                }
            } else {
                mLogger.debug("Creating index");
                inconsistentAtStartup = true;
                if (useRAMIndex) {
                    fRAMindex = new RAMDirectory();
                    createIndex(fRAMindex);
                } else {
                    createIndex(getFSDirectory(true));// create index by lucene hibernate
                }
            }
            
            if (isInconsistentAtStartup()) {
                mLogger.info(
                    "Index was inconsistent. Rebuilding index in the background...");
                //addUsagelog("fmdba","Index was inconsistent. Rebuilding index in the background...",SmartiConstant.AUTOINDEX);
               
                try {
                	String where = null;
                    //rebuildWebsiteIndex(where);
                    indexConsistencyMarker.delete();
                } catch (Exception e) {
                    mLogger.error("ERROR: scheduling re-index operation");
                }
            } else {
                //addUsagelog("fmdba","Index initialized and ready for use.",SmartiConstant.AUTOINDEX);
            	
                mLogger.info("Index initialized and ready for use.");
            }

        }
    	}catch(Exception ee) {
    		mLogger.error(ee.getMessage());
    	}
        //if(!jcrServiceImpl.nodeExsits(Folder.root)) {
        //initializeRepository();
            //importDocuments("fmdba","",3);
        //}
    }
    


    
    //~ Methods
    // ================================================================
    
    public void initializeRepository() {
/*        try {
        	    
                threadManager.executeInForeground(new InitRepository(sessionFactory,jcrServiceImpl));
        } catch (InterruptedException e) {
            //addUsagelog("fmdba","Error executing operation: Import System tables to JCR",SmartiConstant.ERROR);

            mLogger.error("Error executing operation", e);
        }
  	*/
  	
    }
    
    
    public ReadWriteLock getReadWriteLock() {
        return rwl;
    }
    
    public boolean isInconsistentAtStartup() {
        return inconsistentAtStartup;
    }
    
    
    
    public Document getDocument(int n) throws CorruptIndexException, IOException {
		return getSharedIndexReader().document(n);
	}


	/**
     * This is the analyzer that will be used to tokenize comment text.
     *
     * @return Analyzer to be used in manipulating the database.
     */
    public static final Analyzer getAnalyzer() {
        return new StandardAnalyzer(null);
    }

    private void scheduleIndexOperation(final Runnable op) {
        try {
            // only if search is enabled
            if(this.searchEnabled) {
                mLogger.debug("Starting scheduled index operation: "+op.getClass().getName());
                threadManager.executeInBackground(op);
            }
        } catch (InterruptedException e) {
        	mLogger.error("Error executing operation", e);
        }
    }
    
    private void scheduleIndexOperation(final IndexOperation op) {
        try {
            // only if search is enabled
            if(this.searchEnabled) {
                mLogger.debug("Starting scheduled index operation: "+op.getClass().getName());

                threadManager.executeInBackground(op);
            }
        } catch (InterruptedException e) {
             mLogger.error("Error executing operation", e);
        }
    }
    
    /**
     * @param search
     */
    public void executeIndexOperationNow(final IndexOperation op) {
        try {
            // only if search is enabled
            if(this.searchEnabled) {
                mLogger.debug("Executing index operation now: "+op.getClass().getName());
                //addUsagelog("fmdba","Executing index operation now: "+op.getClass().getName(),SmartiConstant.AUTOINDEX);

                threadManager.executeInForeground(op);
            }
        } catch (InterruptedException e) {
            mLogger.error("Error executing operation", e);
        }
    }
    
    public synchronized void resetSharedReader() {
        reader = null;
    }
    public synchronized IndexReader getSharedIndexReader() {
        if (reader == null) {
            try {
                reader = IndexReader.open(getIndexDirectory());
            } catch (IOException e) {
            }
        }
        return reader;
    }
    



	/**
     * Get the directory that is used by the lucene index. This method will
     * return null if there is no index at the directory location. If we are
     * using a RAM index, the directory will be a ram directory.
     *
     * @return Directory The directory containing the index, or null if error.
     */
    public Directory getIndexDirectory() {
        if (useRAMIndex) {
            return fRAMindex;
        } else {
            return getFSDirectory(false);
        }
    }
    
    private boolean indexExists() throws IOException {
        return IndexReader.indexExists(getFSDirectory(true) );
    }
    
    Directory getFSDirectory(boolean delete) {
        Directory directory = null;
        
        try {
            //directory = FSDirectory.getDirectory(getIndexDir(), delete);
            directory = FSDirectory.open(new File(getIndexDir()));

        } catch (IOException e) {
            mLogger.error("Problem accessing index directory", e);
        }
        
        return directory;
    }
    
    private void createIndex(Directory dir) {
        IndexWriter writer = null;
        
        try {
            writer = new IndexWriter(dir, IndexManagerImpl.getAnalyzer(), true, null);
        } catch (IOException e) {
            mLogger.error("Error creating index", e);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
            }
        }
    }
    
    private IndexOperation getSaveIndexOperation() {
        return new WriteToIndexOperation(this) {
            public void doRun() {
                Directory dir = getIndexDirectory();
                Directory fsdir = getFSDirectory(true);
                
                IndexWriter writer = null;
                
                try {
                    writer = new IndexWriter(fsdir, IndexManagerImpl
                            .getAnalyzer(), true, null);
                    
                    writer.addIndexes(new Directory[] { dir });
                    indexConsistencyMarker.delete();
                } catch (IOException e) {
                    mLogger.error("Problem saving index to disk", e);
                    
                    // Delete the directory, since there was a problem saving
                    // the RAM contents
                    getFSDirectory(true);
                } finally {
                    try {
                        if (writer != null)
                            writer.close();
                    } catch (IOException e1) {
                        mLogger.warn("Unable to close IndexWriter.");
                    }
                }
                
            }
        };
    }
    
    public void release() {
        // no-op
    }
    
    public void shutdown() {
    	mLogger.info("Shutdown IndexWriter.");
        if (useRAMIndex) {
            scheduleIndexOperation(getSaveIndexOperation());
        } else {
            indexConsistencyMarker.delete();
        }
        
        try {
            if (reader != null)
                reader.close();
        } catch (IOException e) {
            // won't happen, since it was
        }
    }

	
	public String getIndexDir() {
		return indexDir+"/indexes/pages";
	}


	public void setIndexDir(String indexDir) {
		this.indexDir = indexDir;
	}


	public boolean isSearchEnabled() {
		return searchEnabled;
	}


	public void setSearchEnabled(boolean searchEnabled) {
		this.searchEnabled = searchEnabled;
	}

	public void setSearchEnabled(String searchEnabled) {
		if("true".equals(searchEnabled))
			this.searchEnabled = true;
	}


	public void rebuildWebsiteIndex(String path) throws Exception {
		// TODO Auto-generated method stub
		
	}


	public void rebuildPageIndex(String path) throws Exception {
		// TODO Auto-generated method stub
		
	}


	public void removeWebsiteIndex(Term term) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
