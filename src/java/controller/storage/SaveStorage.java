package controller.storage;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Storage;
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

@WebServlet(name = "SaveStorage", urlPatterns = {"/SaveStorage"})
public class SaveStorage extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String value = request.getParameter("value");

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (value == null || value.trim().isEmpty()) {
            responseObject.addProperty("message", "Storage value cannot be empty");
        } else {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            // check duplicate storage
            Criteria c = s.createCriteria(Storage.class);
            c.add(Restrictions.eq("value", value.trim()));
            Storage existingStorage = (Storage) c.uniqueResult();

            if (existingStorage != null) {
                responseObject.addProperty("message", "Storage already exists");
            } else {

                Storage storage = new Storage();
                storage.setValue(value.trim());

                s.beginTransaction();
                s.save(storage);
                s.getTransaction().commit();

                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Storage added successfully");
            }

            s.close();
        }

        response.setContentType("application/json");
        response.getWriter().write(new Gson().toJson(responseObject));
    }
}
