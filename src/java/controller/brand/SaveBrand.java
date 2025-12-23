package controller.brand;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Brand;
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
@WebServlet(name = "SaveBrand", urlPatterns = {"/SaveBrand"})
public class SaveBrand extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");

        JsonObject json = new JsonObject();

        String name = request.getParameter("name");


        System.out.println("Received brand name: " + name);

        if (name == null || name.trim().isEmpty()) {
            json.addProperty("status", false);
            json.addProperty("message", "Brand name cannot be empty");
            response.getWriter().write(json.toString());
            return;
        }

        name = name.trim();

        Session session = HibernateUtil.getSessionFactory().openSession();

        try {
            // check duplicate
            Criteria c = session.createCriteria(Brand.class);
            c.add(Restrictions.eq("name", name));
            Brand existing = (Brand) c.uniqueResult();

            if (existing != null) {
                json.addProperty("status", false);
                json.addProperty("message", "Brand already exists");
            } else {
                Brand brand = new Brand();
                brand.setName(name);

                session.beginTransaction();
                session.save(brand);
                session.getTransaction().commit();

                json.addProperty("status", true);
                json.addProperty("message", "Brand added successfully");
            }

        } catch (Exception e) {
            e.printStackTrace();
            json.addProperty("status", false);
            json.addProperty("message", "Server error");
        } finally {
            session.close();
        }

        response.getWriter().write(json.toString());
    }
}
