package edu.cornell.mannlib.vitro.webapp.sparql;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vedit.beans.LoginFormBean;
import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.beans.VClassGroup;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.edit.SiteAdminController;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;

/**
 * This servlet gets all the range classes for a given predicate.
 * 
 * @param predicate
 * @author yuysun
 */

public class GetObjectClasses extends BaseEditController {

	private static final Log log = LogFactory.getLog(SiteAdminController.class
			.getName());

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			super.doGet(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		VitroRequest vreq = new VitroRequest(request);

		Object obj = vreq.getSession().getAttribute("loginHandler");
		LoginFormBean loginHandler = null;
		if (obj != null && obj instanceof LoginFormBean)
			loginHandler = ((LoginFormBean) obj);
		if (loginHandler == null
				|| !"authenticated".equalsIgnoreCase(loginHandler
						.getLoginStatus()) ||
				// rjy7 Allows any editor (including self-editors) access to
				// this servlet.
				// This servlet is now requested via Ajax from some custom
				// forms, so anyone
				// using the custom form needs access rights.
				Integer.parseInt(loginHandler.getLoginRole()) < LoginFormBean.NON_EDITOR) {
			HttpSession session = request.getSession(true);

			session.setAttribute("postLoginRequest", vreq.getRequestURI()
					+ (vreq.getQueryString() != null ? ('?' + vreq
							.getQueryString()) : ""));
			String redirectURL = request.getContextPath()
					+ Controllers.SITE_ADMIN + "?login=block";
			response.sendRedirect(redirectURL);
			return;
		}

		String predicate = vreq.getParameter("predicate");
		if (predicate == null || predicate.trim().equals("")) {
			return;
		}

		ObjectPropertyDao odao = vreq.getFullWebappDaoFactory()
				.getObjectPropertyDao();
		ObjectProperty oprop = (ObjectProperty) odao
				.getObjectPropertyByURI(predicate);

		VClassDao vcDao = vreq.getFullWebappDaoFactory().getVClassDao();
		VClass vClass = (oprop.getRangeVClassURI() != null) ? vcDao
				.getVClassByURI(oprop.getRangeVClassURI()) : null;

		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String respo = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		respo += "<options>";
		if (vClass != null) {
			respo += "<option>" + "<key>" + vClass.getPickListName() + "</key>"
					+ "<value>" + vClass.getURI() + "</value>" + "</option>";
		} else {
			List classGroups = vreq.getFullWebappDaoFactory()
					.getVClassGroupDao().getPublicGroupsWithVClasses(true,
							true, false); // order by displayRank, include
											// uninstantiated classes, don't get
											// the counts of individuals

			Iterator classGroupIt = classGroups.iterator();
			while (classGroupIt.hasNext()) {
				VClassGroup group = (VClassGroup) classGroupIt.next();
				List classes = group.getVitroClassList();
				Iterator classIt = classes.iterator();
				while (classIt.hasNext()) {
					VClass clazz = (VClass) classIt.next();
					respo += "<option>" + "<key>" + clazz.getPickListName()
							+ "</key>" + "<value>" + clazz.getURI()
							+ "</value>" + "</option>";
				}
			}
		}

		/*
		 * VClass clazz = oprop.getRangeVClass();
		 * response.setContentType("text/xml");
		 * response.setCharacterEncoding("UTF-8"); PrintWriter out =
		 * response.getWriter(); String respo =
		 * "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"; respo += "<options>";
		 * 
		 * if (clazz != null) { respo += "<option>" + "<key>" +
		 * clazz.getPickListName() + "</key>" + "<value>" + clazz.getURI() +
		 * "</value>" + "</option>"; } else{ List classGroups =
		 * vreq.getFullWebappDaoFactory
		 * ().getVClassGroupDao().getPublicGroupsWithVClasses(true,true,false);
		 * // order by displayRank, include uninstantiated classes, don't get
		 * the counts of individuals Iterator classGroupIt =
		 * classGroups.iterator(); while (classGroupIt.hasNext()) { VClassGroup
		 * group = (VClassGroup)classGroupIt.next(); List classes =
		 * group.getVitroClassList(); Iterator classIt = classes.iterator();
		 * while (classIt.hasNext()) { clazz = (VClass) classIt.next();
		 * System.out.println(clazz.getPickListName()); respo += "<option>" +
		 * "<key>" + clazz.getPickListName() + "</key>" + "<value>" +
		 * clazz.getURI() + "</value>" + "</option>"; } }
		 * 
		 * }
		 */
		respo += "</options>";
		out.println(respo);
		out.flush();
		out.close();
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}

}
