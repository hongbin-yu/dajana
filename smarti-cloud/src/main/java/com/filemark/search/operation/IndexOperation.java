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
/* Created on Jul 16, 2003 */
package com.filemark.search.operation;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;

import com.filemark.jcr.model.Page;
import com.filemark.search.FieldConstants;
import com.filemark.search.IndexManager;
import com.filemark.search.IndexManagerImpl;


/**
 * This is the base class for all index operation. 
 * These operations include:<br>
 *    SearchOperation<br>
 *    AddWeblogOperation<br>
 *    RemoveWeblogOperation<br>
 *    RebuildUserIndexOperation
 *
 * @author Mindaugas Idzelis (min@idzelis.com)
 */
public abstract class IndexOperation implements Runnable {

    private static Log mLogger = LogFactory.getFactory().getInstance(IndexOperation.class);

    //~ Instance fields
    // ========================================================
    protected IndexManager manager;
    private IndexReader reader;
    private IndexWriter writer;

    //~ Constructors
    // ===========================================================
    public IndexOperation(IndexManager manager) {
        this.manager = manager;
    }

    //~ Methods
    // ================================================================


    protected Document getWebDocument(Page data) {


        Document doc = new Document();

  
        // keyword
        doc.add(new Field(FieldConstants.UID, data.getUid(),Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field(FieldConstants.PATH, data.getPath(),Field.Store.YES, Field.Index.NOT_ANALYZED));
        
        String content = data.getContent();
        doc.add(new Field(FieldConstants.CONTENT,
                (content==null?"":content),
                Field.Store.NO, Field.Index.ANALYZED));
        
        return doc;
    }
    
    @SuppressWarnings("deprecation")
	protected IndexReader beginDeleting() {
        try {
            reader = IndexReader.open(manager.getIndexDirectory());
        } catch (IOException e) {
        }

        return reader;
    }

    protected void endDeleting() {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                mLogger.error("ERROR closing reader");
            }
        }
    }

    @SuppressWarnings("deprecation")
	protected IndexWriter beginWriting() {
        try {
            writer = new IndexWriter(manager.getIndexDirectory(), IndexManagerImpl.getAnalyzer(), false, null);
        } catch (IOException e) {
            mLogger.error("ERROR creating writer", e);
        }

        return writer;
    }

    protected void endWriting() {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                mLogger.error("ERROR closing writer", e);
            }
        }
    }

    public void run() {
        doRun();
    }

    protected abstract void doRun();
}
