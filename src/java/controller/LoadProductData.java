package controller;

import hibernate.Product;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.*;
import hibernate.HibernateUtil;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@WebServlet(name = "LoadProductData", urlPatterns = {"/LoadProductData"})
public class LoadProductData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        try {
            // Fetch brands, models, colors, quality, storage
            List<Brand> brandList = s.createCriteria(Brand.class).list();
            List<Model> modelList = s.createCriteria(Model.class).list();
            List<Quality> qualityList = s.createCriteria(Quality.class).list();
            List<Color> colorList = s.createCriteria(Color.class).list();
            List<Storage> storageList = s.createCriteria(Storage.class).list();




            // Fetch products dynamically
            String category = request.getParameter("category");
            String hql = "FROM Product";
            if (category != null && !category.isEmpty()) {
                hql += " WHERE category = :category";
            }





            List<Product> productList;
            if (category != null && !category.isEmpty()) {
                productList = (List<Product>) s.createQuery(hql)
                        .setParameter("category", category)
                        .list();
            } else {
                productList = (List<Product>) s.createQuery(hql).list();
            }

            Gson gson = new Gson();

            // Add all data to JSON
            responseObject.add("brandList", gson.toJsonTree(brandList));
            responseObject.add("modelList", gson.toJsonTree(modelList));
            responseObject.add("qualityList", gson.toJsonTree(qualityList));
            responseObject.add("colorList", gson.toJsonTree(colorList));
            responseObject.add("storageList", gson.toJsonTree(storageList));
            responseObject.add("productList", gson.toJsonTree(productList));
            responseObject.addProperty("status", true);


        } catch (Exception e) {
            responseObject.addProperty("status", false);
            responseObject.addProperty("error", e.getMessage());
        } finally {
            s.close();
        }


        response.setContentType("application/json");
        response.getWriter().write(new Gson().toJson(responseObject));
    }
}
