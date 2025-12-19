package controller.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Brand;
import hibernate.HibernateUtil;
import hibernate.Model;
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

@WebServlet(name = "SaveModel", urlPatterns = {"/SaveModel"})
public class SaveModel extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String brandId = request.getParameter("brandId");

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (name == null || name.trim().isEmpty()) {
            responseObject.addProperty("message", "Model name cannot be empty");
        } else if (brandId == null || brandId.equals("0")) {
            responseObject.addProperty("message", "Please select a brand");
        } else {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Brand brand = (Brand) s.get(Brand.class, Integer.parseInt(brandId));
            if (brand == null) {
                responseObject.addProperty("message", "Invalid brand");
            } else {

                // check duplicate model under same brand
                Criteria c = s.createCriteria(Model.class);
                c.add(Restrictions.eq("name", name.trim()));
                c.add(Restrictions.eq("brand", brand));
                Model existingModel = (Model) c.uniqueResult();

                if (existingModel != null) {
                    responseObject.addProperty("message", "Model already exists for this brand");
                } else {

                    Model model = new Model();
                    model.setName(name.trim());
                    model.setBrand(brand);

                    s.beginTransaction();
                    s.save(model);
                    s.getTransaction().commit();

                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Model added successfully");
                }
            }

            s.close();
        }

        response.setContentType("application/json");
        response.getWriter().write(new Gson().toJson(responseObject));
    }
}
