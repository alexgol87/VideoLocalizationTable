package web;

import util.GoogleDriveApiUtil;
import util.GoogleDriveSpider;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class MainServlet extends HttpServlet {

    private static final Runnable task = GoogleDriveSpider::new;
    private static Thread thread = null;
    private static String lastUpdateTime;
    private static final String TRUE = "true";
    private static final String FALSE = "false";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ((thread.getState() == Thread.State.NEW || thread.getState() == Thread.State.TERMINATED) && req.getParameter("runUpdate").equals("yes")) {
            if (thread.getState() == Thread.State.TERMINATED) thread = new Thread(task);
            thread.start();
            req.setAttribute("lockUpdate", TRUE);
            req.setAttribute("tableReady", FALSE);
        } else if ((thread.getState() == Thread.State.NEW || thread.getState() == Thread.State.TERMINATED)) {
            req.setAttribute("lockUpdate", FALSE);
            req.setAttribute("tableReady", TRUE);
            req.setAttribute("execTime", GoogleDriveSpider.execTime);
            lastUpdateTime = GoogleDriveApiUtil.getModifiedTime(GoogleDriveApiUtil.buildSheetsApiClientService(), "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI");
        } else if ((thread.getState() == Thread.State.RUNNABLE)) {
            req.setAttribute("lockUpdate", TRUE);
            req.setAttribute("tableReady", FALSE);
        }
        req.setAttribute("lastUpdateTime", lastUpdateTime);
        req.getRequestDispatcher("main.jsp").forward(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (thread != null) thread.interrupt();
        thread = new Thread(task);
        req.setAttribute("lockUpdate", FALSE);
        lastUpdateTime = GoogleDriveApiUtil.getModifiedTime(GoogleDriveApiUtil.buildSheetsApiClientService(), "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI");
        req.setAttribute("lastUpdateTime", lastUpdateTime);
        req.getRequestDispatcher("main.jsp").forward(req, resp);
    }
}
