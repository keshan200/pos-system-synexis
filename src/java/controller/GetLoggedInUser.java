package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "GetLoggedInUser", urlPatterns = {"/GetLoggedInUser"})
public class GetLoggedInUser extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject res = new JsonObject();

        HttpSession session = request.getSession(false); // don't create new session
        if (session == null || session.getAttribute("user") == null) {
            res.addProperty("status", false);
            res.addProperty("message", "No user logged in");
        } else {
            User user = (User) session.getAttribute("user");

            res.addProperty("status", true);
            res.addProperty("userId", user.getId());
            res.addProperty("firstName", user.getFirst_name());
            res.addProperty("lastName", user.getLast_name());
            res.addProperty("email", user.getEmail());
            res.addProperty("role", user.getStatus().toString()); // ADMIN / USER etc.
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(res));
    }
}
