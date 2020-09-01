package com.teamb.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.teamb.model.Comm_MemberDTO;
import com.teamb.model.CommboardDTO;
import com.teamb.service.CommboardMapper;


/*
 �씠	   由� : CommNewsfeedController
媛�  諛�   �옄 : �씠�뿬吏�
�꽕	   紐� : 而ㅻ�ㅻ땲�떚 �돱�뒪�뵾�뱶 而⑦듃濡ㅻ윭
*/


@Controller
public class CommNewsfeedController {

	@Autowired
	private CommboardMapper boardMapper;

	@Resource(name = "upLoadPath")
	private String upLoadPath;

	@RequestMapping("/commhome.comm")
	public String index(HttpServletRequest req, HttpSession session,Comm_MemberDTO dto) {
		
		String mbId = (String)session.getAttribute("mbId");
		boolean isLogin = false;
		if(mbId != null){
	         isLogin = true;
		}
	      
		return "comm/index";
	}

	

	@RequestMapping("/comm_newsfeed.do")
	public String newsFeed(HttpServletRequest req) {
		List<CommboardDTO> list = boardMapper.allListBoard();
		req.setAttribute("boardList", list);
		return "comm/newsfeed";
	}
}