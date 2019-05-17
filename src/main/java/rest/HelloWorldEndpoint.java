package rest;

import org.hibernate.Session;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@ApplicationScoped
@Path("/hello")
public class HelloWorldEndpoint {

	@Inject
	MySessionFactory factory;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response doGet() {
		Session session = factory.getCurrentSession("hibernate.cfg.xml");
		session.getTransaction().begin();
		User user = new User();
		user.setFirstName("Jan");
		user.setLastName("Janssen");
		session.save(user);
		session.getTransaction().commit();
		return Response.ok("Hello from Thorntail!" + user.getId()).build();
	}
}
