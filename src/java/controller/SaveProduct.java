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
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import model.Util;

@MultipartConfig
@WebServlet(name = "SaveProduct", urlPatterns = {"/SaveProduct"})
public class SaveProduct extends HttpServlet {

    private static final int PENDING_STATUS_ID = 1;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        User sessionUser = (User) request.getSession().getAttribute("user");
        if (sessionUser == null) {
            responseObject.addProperty("message", "Please sign in!");
        } else if (sessionUser.getStatus() != UserStatus.ADMIN) {
            responseObject.addProperty("message", "Access denied! Only admin can add products.");
        } else {
            String categoryId = request.getParameter("categoryId");
            String modelId = request.getParameter("modelId");
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String storageId = request.getParameter("storageId");
            String colorId = request.getParameter("colorId");
            String qualityId = request.getParameter("conditionId");
            String price = request.getParameter("price");
            String qty = request.getParameter("qty");

            Part part1 = request.getPart("image1");
            Part part2 = request.getPart("image2");
            Part part3 = request.getPart("image3");

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            s.beginTransaction();

            try {
                // Validation
                if (!Util.isInteger(modelId) || Integer.parseInt(modelId) == 0) {
                    responseObject.addProperty("message", "Please select a valid model!");
                } else if (title == null || title.isEmpty()) {
                    responseObject.addProperty("message", "Product title cannot be empty");
                } else if (description == null || description.isEmpty()) {
                    responseObject.addProperty("message", "Product description cannot be empty");
                } else if (!Util.isInteger(storageId) || Integer.parseInt(storageId) == 0) {
                    responseObject.addProperty("message", "Please select a valid storage");
                } else if (!Util.isInteger(colorId) || Integer.parseInt(colorId) == 0) {
                    responseObject.addProperty("message", "Please select a valid color");
                } else if (!Util.isInteger(qualityId) || Integer.parseInt(qualityId) == 0) {
                    responseObject.addProperty("message", "Please select a valid quality");
                } else if (!Util.isDouble(price) || Double.parseDouble(price) <= 0) {
                    responseObject.addProperty("message", "Please enter a valid price");
                } else if (!Util.isInteger(qty) || Integer.parseInt(qty) <= 0) {
                    responseObject.addProperty("message", "Please enter a valid quantity");
                } else if (part1.getSubmittedFileName() == null) {
                    responseObject.addProperty("message", "Product image one is required");
                } else if (part2.getSubmittedFileName() == null) {
                    responseObject.addProperty("message", "Product image two is required");
                } else if (part3.getSubmittedFileName() == null) {
                    responseObject.addProperty("message", "Product image three is required");
                } else {
                    // Fetch entities
                    Model model = (Model) s.get(Model.class, Integer.parseInt(modelId));
                    Storage storage = (Storage) s.get(Storage.class, Integer.parseInt(storageId));
                    Color color = (Color) s.get(Color.class, Integer.parseInt(colorId));
                    Quality quality = (Quality) s.get(Quality.class, Integer.parseInt(qualityId));
                    Status status = (Status) s.get(Status.class, PENDING_STATUS_ID);
                    Category category = (Category) s.get(Category.class, Integer.parseInt(categoryId));

                    if (model == null || storage == null || color == null || quality == null) {
                        responseObject.addProperty("message", "Invalid product data");
                    } else {
                        // Create product
                        Product p = new Product();
                        p.setModel(model);
                        p.setTitle(title);
                        p.setDescription(description);
                        p.setStorage(storage);
                        p.setColor(color);
                        p.setQuality(quality);
                        p.setPrice(Double.parseDouble(price));
                        p.setQty(Integer.parseInt(qty));
                        p.setStatus(status);
                        p.setUser(sessionUser);
                        p.setCategory(category);
                        p.setCreated_at(new Date());

                        int productId = (int) s.save(p);
                        s.getTransaction().commit();

                        // Upload images
                        String appPath = getServletContext().getRealPath("");
                        String newPath = appPath.replace("build" + File.separator + "web",
                                "web" + File.separator + "product-images");
                        File productFolder = new File(newPath, String.valueOf(productId));
                        productFolder.mkdir();

                        Files.copy(part1.getInputStream(), new File(productFolder, "image1.png").toPath(), StandardCopyOption.REPLACE_EXISTING);
                        Files.copy(part2.getInputStream(), new File(productFolder, "image2.png").toPath(), StandardCopyOption.REPLACE_EXISTING);
                        Files.copy(part3.getInputStream(), new File(productFolder, "image3.png").toPath(), StandardCopyOption.REPLACE_EXISTING);

                        responseObject.addProperty("status", true);
                        responseObject.addProperty("message", "Product added successfully!");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                responseObject.addProperty("message", "Server error: " + e.getMessage());
            } finally {
                s.close();
            }
        }

        // Send response
        Gson gson = new Gson();
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }
}
