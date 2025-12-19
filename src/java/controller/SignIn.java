package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.User;
import hibernate.UserStatus;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "SignIn", urlPatterns = {"/SignIn"})
public class SignIn extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject userJson = gson.fromJson(request.getReader(), JsonObject.class);

        String email = userJson.get("email").getAsString();
        String password = userJson.get("password").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        // Validation
        if (email.isEmpty()) {
            responseObject.addProperty("message", "Email can not be empty");
        } else if (!Util.isEmailValid(email)) {
            responseObject.addProperty("message", "Please enter a valid email!");
        } else if (password.isEmpty()) {
            responseObject.addProperty("message", "Password can not be empty");
        } else {
            // Hibernate session
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();

            Criteria criteria = session.createCriteria(User.class);
            Criterion emailCriterion = Restrictions.eq("email", email);
            Criterion passwordCriterion = Restrictions.eq("password", password);
            criteria.add(emailCriterion);
            criteria.add(passwordCriterion);

            if (criteria.list().isEmpty()) {
                responseObject.addProperty("message", "Invalid credentials!");
            } else {
                User u = (User) criteria.list().get(0);
                responseObject.addProperty("status", true);

                HttpSession httpSession = request.getSession();


                // Verification check
                if (!u.getVerification().equalsIgnoreCase("Verified")) {
                    httpSession.setAttribute("email", email);
                    responseObject.addProperty("message", "1"); // Not verified yet
                } else {
                    httpSession.setAttribute("user", u);

                    // Role-based response
                    if (u.getStatus() == UserStatus.ADMIN) {
                        responseObject.addProperty("role", "ADMIN");
                        responseObject.addProperty("message", "ADMIN_LOGIN_SUCCESS");
                    } else {
                        responseObject.addProperty("role", "USER");
                        responseObject.addProperty("message", "USER_LOGIN_SUCCESS");
                    }
                }
            }

            session.close();
        }

        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);
    }

}
