package com.filemark.search.operation;

import java.util.Date;

import javax.jcr.RepositoryException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.filemark.jcr.model.Folder;
import com.filemark.jcr.service.JcrServices;

public class InitRepository implements Runnable {

	private SessionFactory sessionFactory;
	private JcrServices jcrService;
	static Log mLogger = LogFactory.getFactory().getInstance(
    		InitRepository.class);

    
    
	public InitRepository(SessionFactory sessionFactory, JcrServices jrcService) {
		super();
		this.sessionFactory = sessionFactory;
		this.jcrService = jrcService;
	}




	public synchronized void run() {
        Date start = new Date();

		Transaction txn = null;
		Session session = null;
		javax.jcr.Session jcrSession = null;
        mLogger.info("Start import DB...");
		try {
	        jcrService.deleteNode(Folder.root+"/system");
	        String root=Folder.root+"/system/apps";
			String sql = "from App where deleted is null order by name";

			if(!jcrService.nodeExsits(Folder.root))
				jcrService.addNodes(Folder.root,"nt:unstructured","fmdba");
			if(!jcrService.nodeExsits(Folder.root+"/system"))
				jcrService.addNodes(Folder.root+"/system","nt:unstructured","fmdba");

			//jcrService.addNodes("/smarti/Tools","nt:unstructured");
			//jcrService.addNodes("/smarti/Tools/import","nt:unstructured");
			//jcrService.addNodes("/smarti/Tools/export","nt:unstructured");
			//jcrService.addNodes("/smarti/Tools/archive","nt:unstructured");
		
		
			//jcrService.deleteNode("/smarti");
        } catch (RepositoryException e) {
	        mLogger.error(e);
        } catch (RuntimeException e) {	        
	        mLogger.error(e);
        } catch (Exception e) {
	    	if(txn != null)
	    		txn.rollback();
	    	mLogger.error(e);
	    }finally {

	    	if(session != null)
	    		session.close();
	    	if(jcrSession != null)
	    		jcrSession.logout();
        }

        Date end = new Date();
        double length = (end.getTime() - start.getTime()) / (double) 1000;
        
        mLogger.info(
                "Completed DB to Repository for all applications in " + length + " secs");
        //addUsagelog("fmdba", "Completed DB to Repository for all applications in " + length + " secs",SmartiConstant.AUTOINDEX);

	}



}
