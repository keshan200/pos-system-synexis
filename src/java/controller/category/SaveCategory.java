package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Category;
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

@WebServlet(name = "SaveCategory", urlPatterns = {"/SaveCategory"})
public class SaveCategory extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (name == null || name.trim().isEmpty()) {
            responseObject.addProperty("message", "Category name cannot be empty");
        } else {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            // check duplicate category
            Criteria c = s.createCriteria(Category.class);
            c.add(Restrictions.eq("name", name.trim()));
            Category existingCategory = (Category) c.uniqueResult();

            if (existingCategory != null) {
                responseObject.addProperty("message", "Category already exists");
            } else {

                Category category = new Category();
                category.setName(name.trim());

                s.beginTransaction();
                s.save(category);
                s.getTransaction().commit();

                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Category added successfully");
            }

            s.close();
        }

        response.setContentType("application/json");
        response.getWriter().write(new Gson().toJson(responseObject));
    }
}
