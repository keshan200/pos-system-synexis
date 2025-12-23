package controller.quality;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Quality;
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

@WebServlet(name = "SaveQuality", urlPatterns = {"/SaveQuality"})
public class AddQuality extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String value = request.getParameter("value");

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (value == null || value.trim().isEmpty()) {
            responseObject.addProperty("message", "Quality value cannot be empty");
        } else {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            try {
                // Check for duplicate quality
                Criteria c = s.createCriteria(Quality.class);
                c.add(Restrictions.eq("value", value.trim()));
                Quality existingQuality = (Quality) c.uniqueResult();

                if (existingQuality != null) {
                    responseObject.addProperty("message", "Quality already exists");
                } else {
                    Quality quality = new Quality();
                    quality.setValue(value.trim());

                    s.beginTransaction();
                    s.save(quality);
                    s.getTransaction().commit();

                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Quality added successfully");
                }
            } catch (Exception e) {
                responseObject.addProperty("message", "Error: " + e.getMessage());
            } finally {
                s.close();
            }
        }

        response.setContentType("application/json");
        response.getWriter().write(new Gson().toJson(responseObject));
    }
}
