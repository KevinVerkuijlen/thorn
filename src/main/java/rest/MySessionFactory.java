package rest;

import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;

@RequestScoped
public class MySessionFactory implements ISessionFactoryTemplate {

    @Inject
    private HibernateUtility hibernateUtility;

    private SessionFactory sessionFactory;

    private Session currentSession;

    public Session openSession() {
        return sessionFactory.openSession();
    }

    public Session getCurrentSession(String path) {
        if(sessionFactory == null){
            sessionFactory = hibernateUtility.buildSessionFactory(path);
        }
        return (currentSession == null) ? sessionFactory.openSession() : currentSession;
    }

    public StatelessSession openStatelessSession() {
        return sessionFactory.openStatelessSession();
    }

    @PreDestroy
    private void closeSession() {
        if (currentSession != null && currentSession.isOpen()) {
            currentSession.close();
        }
    }
}
