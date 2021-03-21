/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trongns.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import trongns.answer.AnswerDAO;
import trongns.answer.AnswerDTO;
import trongns.question.QuestionDAO;
import trongns.question.QuestionDTO;
import trongns.subject.SubjectDAO;
import trongns.subject.SubjectDTO;

/**
 *
 * @author TrongNS
 */
public class ViewQuestionServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String url = "home";
        boolean haveId = false;
        try {
            int pageNo = 1;
            int pageSize = 20;
            
            SubjectDAO subjectDAO = new SubjectDAO();

            String subjectId = request.getParameter("id");
            if (subjectId != null && !subjectId.trim().isEmpty()) {
                haveId = true;
                url = "viewQuestion.jsp";
                
                subjectDAO.getSubjectById(subjectId);
                SubjectDTO dto = subjectDAO.getSubjectChosen();

                QuestionDAO questDAO = new QuestionDAO();
                questDAO.getQuestionListWithCondition(subjectId, null, null, pageSize, pageNo);
                ArrayList<QuestionDTO> questionList = questDAO.getListQuestion();

                if (questionList != null) {
                    AnswerDAO ansDAO = new AnswerDAO();
                    for (QuestionDTO quest : questionList) {
                        ansDAO.getAnswerListByQuestionId(quest.getQuestionId());
                        ArrayList<AnswerDTO> answerList = ansDAO.getListAnswer();
                        quest.setAnswerList(answerList);
                    }

                    questDAO.countAllQuestionsBySubjectId(subjectId);
                    int totalQuests = questDAO.getTotalQuestion();

                    if (totalQuests > 0) {
                        request.setAttribute("SUBJECT_INFO", dto);
                    } else {
                        request.setAttribute("SUBJECT_INFO", "");
                    }
                    
                    request.setAttribute("QUESTION_LIST", questionList);
                    request.setAttribute("PAGE_NO", pageNo);
                    request.setAttribute("PAGE_SIZE", pageSize);

                    if (((pageSize * pageNo) >= totalQuests) || questionList.size() < pageSize) {
                        request.setAttribute("LAST_LIST", "last");
                    }
                }
            }

            SubjectDAO dao = new SubjectDAO();
            dao.getAllSubjects();
            ArrayList<SubjectDTO> subjectList = dao.getListSubject();
            request.setAttribute("SUBJECT_LIST", subjectList);
        } catch (NamingException ex) {
            String msg = ex.getMessage();
            log("Error at ViewQuestionServlet _ NamingException : " + msg);
        } catch (SQLException ex) {
            String msg = ex.getMessage();
            log("Error at ViewQuestionServlet _ SQLException : " + msg);
        } finally {
            if (haveId) {
                request.getRequestDispatcher(url).forward(request, response);
            } else {
                response.sendRedirect(url);
            }
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
