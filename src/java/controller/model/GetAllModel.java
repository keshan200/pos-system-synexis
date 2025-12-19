package controller.model;

import com.google.gson.Gson;
import hibernate.HibernateUtil;
import hibernate.Model;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@WebServlet(name = "GetAllModel", urlPatterns = {"/GetAllModel"})
public class GetAllModel extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        Criteria c = s.createCriteria(Model.class);
        List<Model> modelList = c.list();

        s.close();

        response.setContentType("application/json");
        response.getWriter().write(new Gson().toJson(modelList));
    }
}
