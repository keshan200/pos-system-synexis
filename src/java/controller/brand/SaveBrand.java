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

        String name = request.getParameter("name");

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (name == null || name.trim().isEmpty()) {
            responseObject.addProperty("message", "Brand name cannot be empty");
        } else {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            // check duplicate brand
            Criteria c = s.createCriteria(Brand.class);
            c.add(Restrictions.eq("name", name.trim()));
            Brand existingBrand = (Brand) c.uniqueResult();

            if (existingBrand != null) {
                responseObject.addProperty("message", "Brand already exists");
            } else {

                Brand brand = new Brand();
                brand.setName(name.trim());

                s.beginTransaction();
                s.save(brand);
                s.getTransaction().commit();

                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Brand added successfully");
            }

            s.close();
        }

        response.setContentType("application/json");
        response.getWriter().write(new Gson().toJson(responseObject));
    }
}
