package controller.colour;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Color;
import hibernate.HibernateUtil;
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

@WebServlet(name = "SaveColor", urlPatterns = {"/SaveColor"})
public class SaveColor extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String value = request.getParameter("value");

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (value == null || value.trim().isEmpty()) {
            responseObject.addProperty("message", "Color value cannot be empty");
        } else {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            // check duplicate color
            Criteria c = s.createCriteria(Color.class);
            c.add(Restrictions.eq("value", value.trim()));
            Color existingColor = (Color) c.uniqueResult();

            if (existingColor != null) {
                responseObject.addProperty("message", "Color already exists");
            } else {
                Color color = new Color();
                color.setValue(value.trim());

                s.beginTransaction();
                s.save(color);
                s.getTransaction().commit();

                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Color added successfully");
            }

            s.close();
        }

        response.setContentType("application/json");
        response.getWriter().write(new Gson().toJson(responseObject));
    }
}
