package rest;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;

@RequestScoped
@Default
public class HibernateUtility {

    private SessionFactory sessionFactory;

    public SessionFactory buildSessionFactory(String path) {
        sessionFactory = new Configuration().configure(path).buildSessionFactory();
        return sessionFactory;
    }

    @Produces
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @PreDestroy
    public void closeSessionFactory() {
        sessionFactory.notify();
        sessionFactory.close();
    }

}
