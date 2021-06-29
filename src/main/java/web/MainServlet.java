package web;

import util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class MainServlet extends HttpServlet {

    private static Runnable taskVideo = GoogleDriveSpider::new;
    private static Runnable taskBanner = GoogleDriveBannerSpider::new;
    private static Runnable taskCommunity = GoogleDriveCommunitySpider::new;
    private static Thread thread = new Thread();
    private static String lastUpdateTimeVideo;
    private static String lastUpdateTimeBanner;
    private static String lastUpdateTimeCommunity;
    private static final String TRUE = "true";
    private static final String FALSE = "false";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ((thread.getState() == Thread.State.NEW || thread.getState() == Thread.State.TERMINATED) && req.getParameter("runUpdate").equals("yes")) {
            //if (thread.getState() == Thread.State.TERMINATED)
            if (req.getParameter("creativeType").equals("video")) thread = new Thread(taskVideo);
            else if (req.getParameter("creativeType").equals("banner")) thread = new Thread(taskBanner);
            else thread = new Thread(taskCommunity);
            if (req.getParameter("updatePreview") != null) DropboxApiUtil.startUpdatePreview();
            else DropboxApiUtil.stopUpdatePreview();
            thread.start();
            req.setAttribute("lockUpdate", TRUE);
            req.setAttribute("tableReady", FALSE);
        } else if ((thread.getState() == Thread.State.NEW || thread.getState() == Thread.State.TERMINATED)) {
            req.setAttribute("lockUpdate", FALSE);
            req.setAttribute("tableReady", TRUE);
            req.setAttribute("execTime", GeneralUtil.execTime);
            req.setAttribute("videoErrorsCE", GoogleDriveSpider.videoErrorsCE.size());
            req.setAttribute("videoErrorsCM", GoogleDriveSpider.videoErrorsCM.size());
            //GoogleDriveSpider.videoErrorsCE.clear();
            //GoogleDriveSpider.videoErrorsCM.clear();
            lastUpdateTimeVideo = GoogleDriveApiUtil.getModifiedTime(GoogleDriveApiUtil.buildSheetsApiClientService(), "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "video COEm!R1:R1");
            lastUpdateTimeBanner = GoogleDriveApiUtil.getModifiedTime(GoogleDriveApiUtil.buildSheetsApiClientService(), "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "banners COEm!T1:T1");
            lastUpdateTimeCommunity = GoogleDriveApiUtil.getModifiedTime(GoogleDriveApiUtil.buildSheetsApiClientService(), "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "BC_COEm!K1:K1");
        } else if ((thread.getState() == Thread.State.RUNNABLE)) {
            req.setAttribute("lockUpdate", TRUE);
            req.setAttribute("tableReady", FALSE);
        }
        req.setAttribute("lastUpdateTimeVideo", lastUpdateTimeVideo);
        req.setAttribute("lastUpdateTimeBanner", lastUpdateTimeBanner);
        req.setAttribute("lastUpdateTimeCommunity", lastUpdateTimeCommunity);
        req.getRequestDispatcher("main.jsp").

                forward(req, resp);

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (thread == null) thread = new Thread();
        else if ((thread.getState() == Thread.State.RUNNABLE)) {
            req.setAttribute("lockUpdate", TRUE);
            req.setAttribute("tableReady", FALSE);
        } else {
            req.setAttribute("lockUpdate", FALSE);
        }
        lastUpdateTimeVideo = GoogleDriveApiUtil.getModifiedTime(GoogleDriveApiUtil.buildSheetsApiClientService(), "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "video COEm!R1:R1");
        lastUpdateTimeBanner = GoogleDriveApiUtil.getModifiedTime(GoogleDriveApiUtil.buildSheetsApiClientService(), "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "banners COEm!T1:T1");
        lastUpdateTimeCommunity = GoogleDriveApiUtil.getModifiedTime(GoogleDriveApiUtil.buildSheetsApiClientService(), "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "BC_COEm!K1:K1");
        req.setAttribute("lastUpdateTimeVideo", lastUpdateTimeVideo);
        req.setAttribute("lastUpdateTimeBanner", lastUpdateTimeBanner);
        req.setAttribute("lastUpdateTimeCommunity", lastUpdateTimeCommunity);
        //TODO Google authorization https://coderoad.ru/15938514/Java-%D0%B8-Google-Spreadsheets-API-%D0%B0%D0%B2%D1%82%D0%BE%D1%80%D0%B8%D0%B7%D0%B0%D1%86%D0%B8%D1%8F-%D1%81-OAuth-2-0
        req.getRequestDispatcher("main.jsp").forward(req, resp);
    }
}
