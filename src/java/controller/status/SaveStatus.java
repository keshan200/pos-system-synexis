package controller.status;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Status;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "SaveStatus", urlPatterns = {"/SaveStatus"})
public class SaveStatus extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String value = request.getParameter("value");

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (value == null || value.trim().isEmpty()) {
            responseObject.addProperty("message", "Status value cannot be empty");
        } else {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            // check duplicate status
            Criteria c = s.createCriteria(Status.class);
            c.add(Restrictions.eq("value", value.trim()));
            Status existingStatus = (Status) c.uniqueResult();

            if (existingStatus != null) {
                responseObject.addProperty("message", "Status already exists");
            } else {

                Status status = new Status();
                status.setValue(value.trim());

                s.beginTransaction();
                s.save(status);
                s.getTransaction().commit();

                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Status added successfully");
            }

            s.close();
        }

        response.setContentType("application/json");
        response.getWriter().write(new Gson().toJson(responseObject));
    }
}
