package edu.upenn.cis455.webserver.engine;

import edu.upenn.cis455.webserver.engine.http.ConnectionSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private static Logger log = LogManager.getLogger(SessionManager.class);

    private Map<String, ConnectionSession> sessions = new ConcurrentHashMap<>();

    public ConnectionSession createSession(ServletContext context) {

        String id = UUID.randomUUID().toString();

        ConnectionSession session = new ConnectionSession(id, context, this);
        sessions.put(id, session);

        log.info("Create new session: id:" + id + " duration:" + session.getMaxInactiveInterval());


        return session;
    }

    public synchronized ConnectionSession findSession(String id) {
        if (id == null) {
            return null;
        }

        if (sessions.containsKey(id)) {

            if (isValid(sessions.get(id))) {
                return sessions.get(id);
            } else {

                sessions.remove(id);
                log.info("Invalidated expired session: id:" + id);

            }

        }

        return null;
    }

    public synchronized void invalidateSession(String id) {
        if (id == null) {
            return;
        }

        sessions.remove(id);
        log.info("Invalidated session: id:" + id);

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
