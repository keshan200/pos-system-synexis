package controller.brand;

import com.google.gson.Gson;
import hibernate.Brand;
import hibernate.HibernateUtil;
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

@WebServlet(name = "GetAllBrand", urlPatterns = {"/GetAllBrand"})
public class GetAllBrand extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        Criteria c = s.createCriteria(Brand.class);
        List<Brand> brandList = c.list();

        s.close();

        response.setContentType("application/json");
        response.getWriter().write(new Gson().toJson(brandList));
    }
}
