package com.teamb.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.teamb.model.MemberDTO;
import com.teamb.service.LoginMapper;
import com.teamb.service.MemberMapper;


/*	이	   름 : LoginController class
	개  발   자 : 박 준 언 , 황지은
	설	   명 : 로그인 컨트롤러  
*/

@Controller
public class LoginController {

	@Autowired
	private LoginMapper loginMapper;
	private MemberMapper memberMapper;

	@Resource(name = "upLoadPath")
	private String upLoadPath;

	@RequestMapping("/login.log")
	public String login() {
		return "login/login";
	}

	@RequestMapping("/logout.log")
	public String logout(HttpServletRequest req, HttpSession session) {
		session.invalidate();
		String msg = "로그아웃 되었습니다.";
		String url = "home.do";
		req.setAttribute("msg", msg);
		req.setAttribute("url", url);
		return "message";
	}
	
	@RequestMapping("/loginOk.log")
	   public String loginOk(HttpServletRequest req,HttpSession session){
			MemberDTO dto = loginMapper.getMemberid(req.getParameter("id"));
			int res = loginMapper.loginOk(dto);
			
	      String msg = null, url = null;
	      switch(res){
	      case MemberDTO.OK:	    
	    	  session.setAttribute("mbId", dto.getId());
	    	  session.setAttribute("memberDto", dto);
	    	  session.setAttribute("upLoadPath", upLoadPath);
	            if(dto.getId().equals("admin")) {
	            msg = "관리자로 로그인 하였습니다.";
	            url = "main.mem";
	            }else{
	            msg = "로그인 하였습니다.";
	            url = "main.mem";
	            }
	            
	            break;
	      
	      case MemberDTO.NOT_ID :
	         msg = "등록된 회원이 아닙니다.";
	         url = "login.mem";
	         break;
	      case MemberDTO.NOT_PW :
	         msg = "비밀번호를 확인해 주세요.";
	         url = "login.mem";
	         break;
	      case MemberDTO.ERROR :
	         msg = "에러";
	         url = "index.mem";
	      }
	      req.setAttribute("msg", msg);
	      req.setAttribute("url", url);
	      return "message";
	   }
	@RequestMapping(value = "/member_search.log")
	public String searchMemberForm(HttpServletRequest req) {
		String mode = req.getParameter("mode");
		req.setAttribute("mode", mode);
		return "login/search";
	}

	@RequestMapping(value = "/member_search_ok.log")
	public String searchMember_id(HttpServletRequest req) {
		String mode = req.getParameter("mode");
		String name = req.getParameter("name");
		String email = req.getParameter("email");
		System.out.println(mode);
		String msg = null, url = null;
		if (mode.equals("search_id")) {
			if (memberMapper.searchMember_id(name, email) != null) {
				msg = "회원님 아이디는 " + memberMapper.searchMember_id(name, email) + " 입니다.";
			} else {
				msg = "이름과 이메일을 확인해주세요.";
			}
			url = "login.mem";
		} else if (mode.equals("pw")) {
			String id = req.getParameter("id");
			if (memberMapper.searchMember_pw(name, email, id) != null) {
				msg = "회원님 비밀번호는 " + memberMapper.searchMember_pw(name, email, id) + " 입니다.";
			} else {
				msg = "이름과 이메일, 아이디를 확인해 주세요.";
			}
			url = "login.mem";
		} else {
			msg = "��ϵ� ������ �����ϴ�.";
			url = "login.mem";
		}
		req.setAttribute("msg", msg);
		req.setAttribute("url", url);

		return "message";

	}
}
