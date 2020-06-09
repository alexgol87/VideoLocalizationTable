package web;

import util.GoogleDriveApiUtil;
import util.GoogleDriveSpider;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;

import static util.GeneralUtil.endTimeFixing;
import static util.GeneralUtil.startTimeFixing;

public class MainServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getParameter("runUpdate").equals("yes")) {
            //Instant start = startTimeFixing();
            Runnable task = GoogleDriveSpider::getInstance;
            Thread thread = new Thread(task);
            thread.start();
            //req.setAttribute("executionTime", "Execution Time: "+endTimeFixing(start));
            String lastUpdateTime = GoogleDriveApiUtil.getModifiedTime(GoogleDriveApiUtil.buildSheetsApiClientService(), "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI");
            req.setAttribute("lastUpdateTime", lastUpdateTime);
            req.setAttribute("lockUpdateButton","true");
            req.getRequestDispatcher("main.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //if (GoogleDriveSpider.checkInstance()) req.setAttribute("lockUpdateButton","true");
        String lastUpdateTime = GoogleDriveApiUtil.getModifiedTime(GoogleDriveApiUtil.buildSheetsApiClientService(), "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI");
        req.setAttribute("lastUpdateTime", lastUpdateTime);
        req.getRequestDispatcher("main.jsp").forward(req, resp);
    }
}
