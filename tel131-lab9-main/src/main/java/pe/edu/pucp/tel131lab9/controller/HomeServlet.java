package pe.edu.pucp.tel131lab9.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import pe.edu.pucp.tel131lab9.bean.Employee;
import pe.edu.pucp.tel131lab9.dao.PostDao;

import java.io.IOException;

@WebServlet(name = "HomeServlet", urlPatterns = {"/HomeServlet",""})
public class HomeServlet extends HttpServlet {
    private static final String SESSION_NAME = "userSession";
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        PostDao postDao = new PostDao();
        RequestDispatcher view;
        //HttpSession session = request.getSession();
        //Employee employee = (Employee) session.getAttribute(SESSION_NAME);
        //request.setAttribute("idsession",employee.getEmployeeId());
        request.setAttribute("posts", postDao.listPosts());
        view = request.getRequestDispatcher("home.jsp");
        view.forward(request, response);


    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String action = request.getParameter("p") == null ? "home" : request.getParameter("p");

        PostDao postDao = new PostDao();

        switch (action) {
                case "crear":
                response.sendRedirect(request.getContextPath() + "/HomeServlet");
                break;
                case "buscar":
                String textoBuscar = request.getParameter("textoBuscar");
                request.setAttribute("posts", postDao.buscarPorPost(textoBuscar));
                request.getRequestDispatcher("home.jsp").forward(request, response);
                break;
        }
    }

}
