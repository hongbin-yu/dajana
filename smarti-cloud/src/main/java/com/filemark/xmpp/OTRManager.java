package com.filemark.xmpp;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import net.java.otr4j.OtrEngineHost;
import net.java.otr4j.OtrEngineListener;
import net.java.otr4j.OtrException;
import net.java.otr4j.OtrPolicy;
import net.java.otr4j.OtrPolicyImpl;
import net.java.otr4j.session.FragmenterInstructions;
import net.java.otr4j.session.InstanceTag;
import net.java.otr4j.session.SessionID;

public class OTRManager implements OtrEngineHost, OtrEngineListener{
    private static OTRManager instance;
    private static Map<SecurityOtrMode, OtrPolicy> POLICIES;

    static {
        POLICIES = new HashMap<>();
        POLICIES.put(SecurityOtrMode.disabled, new OtrPolicyImpl(OtrPolicy.NEVER));
        POLICIES.put(SecurityOtrMode.manual, new OtrPolicyImpl(OtrPolicy.OTRL_POLICY_MANUAL));
        POLICIES.put(SecurityOtrMode.auto, new OtrPolicyImpl(OtrPolicy.OPPORTUNISTIC));
        POLICIES.put(SecurityOtrMode.required, new OtrPolicyImpl(OtrPolicy.OTRL_POLICY_ALWAYS));
    }

   // private final EntityNotificationProvider<SMRequest> smRequestProvider;
   // private final EntityNotificationProvider<SMProgress> smProgressProvider;
    /**
     * Accepted fingerprints for user in account.
     */
    //private final NestedNestedMaps<String, Boolean> fingerprints;
    /**
     * Fingerprint of encrypted or encrypted and verified session for user in account.
     */
   // private final NestedMap<String> actives;
    /**
     * Finished entity's sessions for users in accounts.
     */
    //private final NestedMap<Boolean> finished;
    /**
     * Used OTR sessions for users in accounts.
     */
    //private final NestedMap<Session> sessions;
    /**
     * Service for keypair generation.
     */
    private final ExecutorService keyPairGenerator;
    
    public static OTRManager getInstance() {
        if (instance == null) {
            instance = new OTRManager();
        }

        return instance;
    }
    private OTRManager() {
        //smRequestProvider = new EntityNotificationProvider<>(R.drawable.ic_stat_help);
        //smProgressProvider = new EntityNotificationProvider<>(R.drawable.ic_stat_help);
        //smProgressProvider.setCanClearNotifications(false);
        //fingerprints = new NestedNestedMaps<>();
        //actives = new NestedMap<>();
        //finished = new NestedMap<>();
        //sessions = new NestedMap<>();
        keyPairGenerator = Executors.newSingleThreadExecutor(new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable runnable) {
                        Thread thread = new Thread(runnable, "Key pair generator service");
                        thread.setPriority(Thread.MIN_PRIORITY);
                        thread.setDaemon(true);
                        return thread;
                    }
                });
    }
    
	@Override
	public void outgoingSessionChanged(SessionID arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sessionStatusChanged(SessionID arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void askForSecret(SessionID arg0, InstanceTag arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finishedSessionMessage(SessionID arg0, String arg1)
			throws OtrException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getFallbackMessage(SessionID arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FragmenterInstructions getFragmenterInstructions(SessionID arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getLocalFingerprintRaw(SessionID arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public KeyPair getLocalKeyPair(SessionID arg0) throws OtrException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getReplyForUnreadableMessage(SessionID arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OtrPolicy getSessionPolicy(SessionID arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void injectMessage(SessionID arg0, String arg1) throws OtrException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void messageFromAnotherInstanceReceived(SessionID arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void multipleInstancesDetected(SessionID arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void requireEncryptedMessage(SessionID arg0, String arg1)
			throws OtrException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showError(SessionID arg0, String arg1) throws OtrException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void smpAborted(SessionID arg0) throws OtrException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void smpError(SessionID arg0, int arg1, boolean arg2)
			throws OtrException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unencryptedMessageReceived(SessionID arg0, String arg1)
			throws OtrException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unreadableMessageReceived(SessionID arg0) throws OtrException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unverify(SessionID arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void verify(SessionID arg0, String arg1, boolean arg2) {
		// TODO Auto-generated method stub
		
	}
	
    public enum SecurityOtrMode {

        /**
         * OTR is disabled.
         */
        disabled,

        /**
         * Manually send request and confirm requests.
         */
        manual,

        /**
         * Automatically try to use OTR.
         */
        auto,

        /**
         * Require to use OTR.
         */
        required

    }
}
