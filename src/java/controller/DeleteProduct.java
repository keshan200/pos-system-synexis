package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Product;
import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@WebServlet(name = "DeleteProduct", urlPatterns = {"/DeleteProduct"})
public class DeleteProduct extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String productId = request.getParameter("productId");
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (productId == null || productId.isEmpty()) {
            responseObject.addProperty("message", "Product ID is required!");
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Product p = (Product) s.get(Product.class, Integer.parseInt(productId));
            if (p == null) {
                responseObject.addProperty("message", "Product not found!");
            } else {
                s.beginTransaction();
                s.delete(p);
                s.getTransaction().commit();
                s.close();

                // Delete product images folder
                String appPath = getServletContext().getRealPath("");
                File productFolder = new File(appPath.replace("build" + File.separator + "web", "web" + File.separator + "product-images" + File.separator + p.getId()));
                if (productFolder.exists()) {
                    for (File file : productFolder.listFiles()) {
                        file.delete();
                    }
                    productFolder.delete();
                }

                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Product deleted successfully!");
            }
        }

        response.setContentType("application/json");
        response.getWriter().write(new Gson().toJson(responseObject));
    }
}
