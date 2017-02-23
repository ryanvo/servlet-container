package edu.upenn.cis455.webserver.engine;

import edu.upenn.cis455.webserver.engine.http.ConnectionSession;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author rtv
 */
public class SessionManager {


    private Map<String, ConnectionSession> sessions = new ConcurrentHashMap<>();
    private ServletContext context;

    public SessionManager(ServletContext context) {
        this.context = context;
    }

    public ConnectionSession createSession() {

        String id = UUID.randomUUID().toString();

        ConnectionSession session = new ConnectionSession(id, context);
        sessions.put(id, session);

        return session;
    }

    public synchronized ConnectionSession getSession(String id) {
        if (sessions.containsKey(id)) {

            if (isValid(sessions.get(id))) {
                return sessions.get(id);
            } else {
                sessions.remove(id);
            }

        }

        return null;
    }

    public synchronized void invalidateSession(String id) {
        sessions.remove(id);
    }

    public boolean isValid(String id) {
        if (!sessions.containsKey(id)) {
            return false;
        } else {
            return isValid(sessions.get(id));
        }
    }

    public boolean isValid(HttpSession session) {

        if (session.getMaxInactiveInterval() < 0) {
            return true;
        }

        long now = Instant.now().toEpochMilli();
        return  (now - session.getCreationTime()) < session.getMaxInactiveInterval();
    }

}
