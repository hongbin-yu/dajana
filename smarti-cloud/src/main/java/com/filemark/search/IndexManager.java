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

import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;

import com.filemark.search.operation.IndexOperation;


/**
 * Interface to Roller's Lucene-based search facility.
 * @author Dave Johnson
 */
public interface IndexManager
{
    /** Does index need to be rebuild */
    public abstract boolean isInconsistentAtStartup();

    /** Does index need to be rebuild */
    public Document getDocument(int n) throws CorruptIndexException, IOException ;
    public Directory getIndexDirectory();
    public ReadWriteLock getReadWriteLock();
    public IndexReader getSharedIndexReader();
    public void resetSharedReader();
    public void executeIndexOperationNow(final IndexOperation op);
    
    /** Remove user from index, returns immediately and operates in background */
    public void removeWebsiteIndex(Term term) throws Exception;
    
    
    /** Execute operation immediately */
    //public abstract void executeIndexOperationNow(final IndexOperation op);

    /**
     * Release all resources associated with Roller session.
     */
    public abstract void release();
    
    
    /**
     * Initialize the search system.
     *
     * @throws InitializationException If there is a problem during initialization.
     */
    public void initialize() throws Exception;
    
    
    /** Shutdown to be called on application shutdown */
    public abstract void shutdown();


    public abstract void rebuildWebsiteIndex(String path) throws Exception;

    public abstract void rebuildPageIndex(String path) throws Exception;
}
