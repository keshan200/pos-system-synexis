package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@MultipartConfig
@WebServlet(name = "UpdateProduct", urlPatterns = {"/UpdateProduct"})
public class UpdateProduct extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String productId = request.getParameter("productId");
        String brandId = request.getParameter("brandId");
        String modelId = request.getParameter("modelId");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String storageId = request.getParameter("storageId");
        String colorId = request.getParameter("colorId");
        String conditionId = request.getParameter("conditionId");
        String price = request.getParameter("price");
        String qty = request.getParameter("qty");

        Part part1 = request.getPart("image1");
        Part part2 = request.getPart("image2");
        Part part3 = request.getPart("image3");

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        // Validation
        if (!Util.isInteger(productId)) {
            responseObject.addProperty("message", "Invalid product ID!");
        } else {
            Product p = (Product) s.get(Product.class, Integer.parseInt(productId));
            if (p == null) {
                responseObject.addProperty("message", "Product not found!");
            } else if (!Util.isInteger(brandId) || Integer.parseInt(brandId) == 0) {
                responseObject.addProperty("message", "Invalid brand!");
            } else if (!Util.isInteger(modelId) || Integer.parseInt(modelId) == 0) {
                responseObject.addProperty("message", "Invalid model!");
            } else if (title.isEmpty()) {
                responseObject.addProperty("message", "Product title cannot be empty!");
            } else if (description.isEmpty()) {
                responseObject.addProperty("message", "Product description cannot be empty!");
            } else if (!Util.isInteger(storageId) || Integer.parseInt(storageId) == 0) {
                responseObject.addProperty("message", "Invalid storage!");
            } else if (!Util.isInteger(colorId) || Integer.parseInt(colorId) == 0) {
                responseObject.addProperty("message", "Invalid color!");
            } else if (!Util.isInteger(conditionId) || Integer.parseInt(conditionId) == 0) {
                responseObject.addProperty("message", "Invalid condition!");
            } else if (!Util.isDouble(price) || Double.parseDouble(price) <= 0) {
                responseObject.addProperty("message", "Invalid price!");
            } else if (!Util.isInteger(qty) || Integer.parseInt(qty) <= 0) {
                responseObject.addProperty("message", "Invalid quantity!");
            } else {

                Brand brand = (Brand) s.get(Brand.class, Integer.parseInt(brandId));
                Model model = (Model) s.get(Model.class, Integer.parseInt(modelId));
                Storage storage = (Storage) s.get(Storage.class, Integer.parseInt(storageId));
                Color color = (Color) s.get(Color.class, Integer.parseInt(colorId));
                Quality quality = (Quality) s.get(Quality.class, Integer.parseInt(conditionId));

                if (brand == null || model == null || storage == null || color == null || quality == null) {
                    responseObject.addProperty("message", "Invalid related data!");
                } else if (model.getBrand().getId() != brand.getId()) {
                    responseObject.addProperty("message", "Model does not match the selected brand!");
                } else {
                    // Update product

                    p.setModel(model);
                    p.setTitle(title);
                    p.setDescription(description);
                    p.setStorage(storage);
                    p.setColor(color);
                    p.setQuality(quality);
                    p.setPrice(Double.parseDouble(price));
                    p.setQty(Integer.parseInt(qty));


                    s.beginTransaction().commit();

                    // Update images if new ones are uploaded
                    String appPath = getServletContext().getRealPath("");
                    String productPath = appPath.replace("build" + File.separator + "web", "web" + File.separator + "product-images" + File.separator + p.getId());

                    File productFolder = new File(productPath);
                    if (!productFolder.exists()) productFolder.mkdir();

                    if (part1 != null && part1.getSize() > 0) {
                        Files.copy(part1.getInputStream(), new File(productFolder, "image1.png").toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                    if (part2 != null && part2.getSize() > 0) {
                        Files.copy(part2.getInputStream(), new File(productFolder, "image2.png").toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                    if (part3 != null && part3.getSize() > 0) {
                        Files.copy(part3.getInputStream(), new File(productFolder, "image3.png").toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }

                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Product updated successfully!");
                }
            }
        }

        s.close();
        response.setContentType("application/json");
        response.getWriter().write(new Gson().toJson(responseObject));
    }
}
