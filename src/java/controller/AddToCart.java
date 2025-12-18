package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Cart;
import hibernate.HibernateUtil;
import hibernate.Product;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
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
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;


@WebServlet(name = "AddToCart", urlPatterns = {"/AddToCart"})
public class AddToCart extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession ses = request.getSession();
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", true);

        List<Cart> cartList = (List<Cart>) ses.getAttribute("sessionCart");
        if(cartList == null) cartList = new ArrayList<>();
        responseObject.add("cartItems", gson.toJsonTree(cartList));

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action"); // add, update, remove
        int prId = Integer.parseInt(request.getParameter("prId"));
        int qty = Integer.parseInt(request.getParameter("qty"));

        HttpSession ses = request.getSession();
        List<Cart> cartList = (List<Cart>) ses.getAttribute("sessionCart");
        if(cartList == null) cartList = new ArrayList<>();

        Cart founded = null;
        for(Cart c : cartList) {
            if(c.getProduct().getId() == prId) {
                founded = c;
                break;
            }
        }

        if("add".equals(action)){
            if(founded != null){
                founded.setQty(founded.getQty() + qty);
            } else {
                Session s = HibernateUtil.getSessionFactory().openSession();
                Product product = (Product) s.get(Product.class, prId);
                Cart c = new Cart();
                c.setProduct(product);
                c.setQty(qty);
                cartList.add(c);
                s.close();
            }
        } else if("update".equals(action)){
            if(founded != null) founded.setQty(qty);
        } else if("remove".equals(action)){
            if(founded != null) cartList.remove(founded);
        }

        ses.setAttribute("sessionCart", cartList);

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", true);
        responseObject.add("cartItems", gson.toJsonTree(cartList));

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }
}
