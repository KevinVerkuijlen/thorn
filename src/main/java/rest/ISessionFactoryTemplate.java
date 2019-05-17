package rest;

import org.hibernate.Session;
import org.hibernate.StatelessSession;

/**
 * use the sessionfactory from hibernateUtilty to create sessions and return them.
 */
public interface ISessionFactoryTemplate {

    /**
     * @return current session
     */
    public Session getCurrentSession(String path);

    /**
     * @return open session
     */
    public Session openSession();

    /**
     * @return return stateless session instead of a regular session. useful for one time operations.
     */
    public StatelessSession openStatelessSession();

}
