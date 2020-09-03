package com.teamb.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.teamb.model.Comm_MemberDTO;
import com.teamb.model.CommboardDTO;

@Controller
public class CommTogetherController {
	
	@RequestMapping("/comm_togetherList.do")
	public String togetherList(HttpServletRequest req) {
		return "comm/board/comm_togetherList";
	}
	
	@RequestMapping("/comm_togetherWF.do")
	public String togetherWF(HttpServletRequest req) {
		return "comm/board/comm_togetherWF";
	}
	
	/*@RequestMapping(value = "/comm_togetherWP.do", method = RequestMethod.POST)
	public String togetherWP(HttpServletRequest req, TogetherDTO dto) {
		int res = memberMapper.insertMember(dto);
		String msg = null, url = null;
		if (res > 0) {
			msg = "회원가입성공!! 회원목록페이지로 이동합니다.";
			url = "member_memberAll.do";
		} else {
			msg = "회원가입실패!! 회원관리페이지로 이동합니다.";
			url = "member_main.do";
		}
		req.setAttribute("msg", msg);
		req.setAttribute("url", url);
		return "message";
	}*/
}
