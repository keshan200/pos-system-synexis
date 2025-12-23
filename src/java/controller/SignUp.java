package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Mail;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "SignUp", urlPatterns = {"/SignUp"})


public class SignUp extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject resJsonObject = new JsonObject();
        resJsonObject.addProperty("status", false);

        try {
            JsonObject userJson = gson.fromJson(request.getReader(), JsonObject.class);

            String firstName = userJson.get("firstName").getAsString().trim();
            String lastName = userJson.get("lastName").getAsString().trim();
            final String email = userJson.get("email").getAsString().trim();
            String password = userJson.get("password").getAsString().trim();


            if (firstName.isEmpty()) {
                resJsonObject.addProperty("message", "First Name cannot be empty");
            } else if (lastName.isEmpty()) {
                resJsonObject.addProperty("message", "Last Name cannot be empty");
            } else if (email.isEmpty()) {
                resJsonObject.addProperty("message", "Email cannot be empty");
            } else if (!Util.isEmailValid(email)) {
                resJsonObject.addProperty("message", "Please enter a valid email!");
            } else if (password.isEmpty()) {
                resJsonObject.addProperty("message", "Password cannot be empty");
            } else if (!Util.isPasswordValid(password)) {
                resJsonObject.addProperty("message", "The password must contain at least uppercase, lowercase, number, special characters, and be minimum eight characters long!");
            } else {

                SessionFactory sf = HibernateUtil.getSessionFactory();
                Session session = sf.openSession();
                session.beginTransaction();

                Criteria criteria = session.createCriteria(User.class);
                criteria.add(Restrictions.eq("email", email));

                if (!criteria.list().isEmpty()) {
                    resJsonObject.addProperty("message", "User already exists");
                } else {

                    User u = new User();
                    u.setFirst_name(firstName);
                    u.setLast_name(lastName);
                    u.setEmail(email);
                    u.setPassword(password);
                    u.setVerification(Util.generateCode());

                    session.save(u);
                    session.getTransaction().commit();

                    HttpSession ses = request.getSession();
                    ses.setAttribute("email", email);
                    ses.setAttribute("userId", u.getId());

                    new Thread(() -> {
                        Mail.sendMail(
                                email,
                                "Mayans - Verification",
                                "<h1>" + u.getVerification() + "</h1>"
                        );
                    }).start();

                    resJsonObject.addProperty("status", true);
                }
                session.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
            resJsonObject.addProperty("status", false);
            resJsonObject.addProperty("message", "Server error: " + e.getMessage());
        }


        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(resJsonObject));
    }
}