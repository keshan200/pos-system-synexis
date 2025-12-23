package controller.quality;

import com.google.gson.Gson;
import hibernate.HibernateUtil;
import hibernate.Quality;
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

@WebServlet(name = "GetAllQuality", urlPatterns = {"/GetAllQuality"})
public class GetAllQuality extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        List<Quality> qualityList = null;
        try {
            Criteria c = s.createCriteria(Quality.class);
            qualityList = c.list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            s.close();
        }

        response.setContentType("application/json");
        response.getWriter().write(new Gson().toJson(qualityList));
    }
}
