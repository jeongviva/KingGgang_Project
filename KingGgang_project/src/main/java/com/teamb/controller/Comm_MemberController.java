package com.teamb.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.teamb.model.Comm_MemberDTO;
import com.teamb.model.CommboardDTO;
import com.teamb.model.MemberDTO;
import com.teamb.service.CommBookMarkMapper;
import com.teamb.service.CommTogetherMapper;
import com.teamb.service.CommWarnMapper;
import com.teamb.service.Comm_FriendMapper;
import com.teamb.service.Comm_MemberMapper;
import com.teamb.service.CommboardMapper;

/*
이	   름 : Comm_MemberController
개  발   자 : 황지은
성	   명 : 커뮤니티 로그인/멤버 컨트롤러
*/
@Controller
public class Comm_MemberController {
	@Autowired
	private Comm_MemberMapper memberMapper;

	
	
	@Resource(name="upLoadPath")
	private String upLoadPath;
	
	@RequestMapping(value = "/comm_login.do")
	public String comm_login(HttpServletRequest req, HttpSession session) {
		if (session.getAttribute("memberNum") != null) {
			int memberNum = (Integer) session.getAttribute("memberNum");
			return "comm/login/comm_login";
		} else {

			String msg = "낑깡 로그인 후 이용가능합니다.";
			String url = "login.log";
			req.setAttribute("msg", msg);
			req.setAttribute("url", url);
			return "message";

		}

	}
	
	@RequestMapping(value = "/comm_loginOk.do")
	   public String comm_loginOk(Comm_MemberDTO dto, HttpServletRequest req,HttpSession session) {
	      Comm_MemberDTO login = memberMapper.comm_loginOk(dto);
	      
	      if (session.getAttribute("memberNum") == null) {
	         String msg = "낑깡 로그인 후 이용가능합니다.";
	         String url = "login.log";
	         req.setAttribute("msg", msg);
	         req.setAttribute("url", url);
	         return "message";
	         
	      }
		String msg = null, url = null;
        	if (login == null) {
			session.setAttribute("comm_login", null);
			msg = "등록정보가 없습니다. 닉네임을 확인해주세요.";
			url = "commhome.comm";
		} else {
			
			//인아쓰
			int comm_memberNum = memberMapper.comm_getmemberNum(dto.getComm_nickname());
			session.setAttribute("comm_memberNum",comm_memberNum);
	        session.setAttribute("commmember",memberMapper.comm_getMember(comm_memberNum));
	        String comm_profilename = memberMapper.comm_getMember(comm_memberNum).getComm_profilename();
	        session.setAttribute("comm_profilename", comm_profilename);
	       
	        //지은쓰
			session.setAttribute("comm_login", login);
			session.setAttribute("login_comm_memberNum", login.getComm_memberNum());
			String comm_nickname = memberMapper.comm_getMember(comm_memberNum).getComm_nickname();
	        session.setAttribute("comm_nickname", comm_nickname);
	        
	       session.setAttribute("look", "비공개");
			msg = "로그인 하였습니다";
			url = "commhome.comm";
        	}
	
		req.setAttribute("msg", msg);
		req.setAttribute("url", url);
		return "message";
	}	
	
	@RequestMapping("/comm_checkMember.do")
	public String commcheckMember(HttpServletRequest req, HttpSession session) {
		String msg = null, url = null;
		if (session.getAttribute("memberNum") != null) {
			int memberNum = (Integer) session.getAttribute("memberNum");
			boolean isMember = memberMapper.comm_checkMember(memberNum);

			if (isMember) {
				msg = "이미 등록된 회원입니다. 로그인을 해주세요.";
				url = "comm_login.do";
			} else {
				session.setAttribute("memberNum", memberNum);
				msg = "회원가입 페이지로 이동합니다.";
				url = "comm_member_input.do";
			}
		} else {
			msg = "낑깡 로그인 후 이용가능합니다.";
			url = "login.log";
		}
		req.setAttribute("msg", msg);
		req.setAttribute("url", url);
		return "message";
	}
	
	
	@RequestMapping("/comm_member_input.do")
	public String comminsertMemberForm(HttpServletRequest req,HttpSession session){
		int memberNum = (Integer)session.getAttribute("memberNum");
		String name = (String)session.getAttribute("name");
		
		List<Comm_MemberDTO> list = memberMapper.comm_memberList();
		req.setAttribute("comm_memberList", list);
		return "comm/member/comm_insertMember";
	}
	
	@RequestMapping("/comm_member_input_ok.do")
	public String comminsertMember(HttpServletRequest req, HttpSession session,
											Comm_MemberDTO dto,BindingResult result){
		
		int memberNum = (Integer)session.getAttribute("memberNum");
		 String comm_name = (String)session.getAttribute("name"); 
		 String comm_birth = (String)session.getAttribute("birth");
		
		if (result.hasErrors()){
			dto.setMemberNum(0);
		}
		
		String comm_profilename="";
		int comm_profilesize=0;
		MultipartHttpServletRequest mr = (MultipartHttpServletRequest)req;
		MultipartFile file = mr.getFile("comm_profilename");
		File target = new File(upLoadPath, file.getOriginalFilename());
		if(file.getSize()>0){
			try {
				file.transferTo(target);
			} catch (IOException e) {}
			
			comm_profilename = file.getOriginalFilename();
			comm_profilesize=(int)file.getSize();
		}
		
		if(dto.getComm_profilename() == null){
			dto.setComm_profilename("basic.jpg");;
		}else{
			dto.setComm_profilename(comm_profilename);
		}
			dto.setMemberNum(memberNum);
			dto.setComm_name(comm_name);
			dto.setComm_profilesize(comm_profilesize);
			
			
		    int res = memberMapper.comm_insertMember(dto);
		String msg = null, url = null;
		if(res>0){
			msg="가입성공";
			url="commhome.comm"; 
		}else{
			msg="가입실패";
			url="commhome.comm";
		}
		
		req.setAttribute("msg", msg);
		req.setAttribute("url", url);
		return "message";
	}

// 더보기	
	 @SuppressWarnings("unchecked")
	   @ResponseBody
	   @RequestMapping(value = "/memberajaxList.do", method = RequestMethod.POST)
	   public Object moerContent(@RequestBody HashMap<String, Object> map, HttpServletRequest req,HttpSession session){
	      
	      int startRow = (int) map.get("startRow");
	      int endRow = (int)map.get("endRow");
	      int count = memberMapper.getComm_memberCount();
	      if (endRow>count) endRow = count;
	      
	      List<Comm_MemberDTO> list = memberMapper.comm_memberList(startRow,endRow);
	     
	      JSONArray jsonArray = new JSONArray();
	      JSONObject json = null;
	      for (int i = 0; i < list.size(); i++) {
	         json = new JSONObject();
	         Comm_MemberDTO dto = (Comm_MemberDTO) list.get(i);
	         json.put("num", dto.getComm_memberNum());
	         json.put("file", dto.getComm_profilename());
	         json.put("name", dto.getComm_name());
	         json.put("nickname", dto.getComm_nickname());
	         json.put("birth",dto.getComm_birth());
	         json.put("intro", dto.getComm_intro());
	         json.put("login", session.getAttribute("login_comm_memberNum"));
	         jsonArray.add(json);
	         }
	     
	      return jsonArray;
	     }
	
	 @RequestMapping(value = "/comm_memberList.do")
		public ModelAndView commlistMember(HttpServletRequest req,HttpSession session,Comm_MemberDTO dto){
		 int pageSize = 4;
	      
	      String pageNum = req.getParameter("pageNum");
	      if (pageNum == null){
	         pageNum = "1";
	      }
	      
	      int currentPage = Integer.parseInt(pageNum);
	      int startRow = currentPage * pageSize - (pageSize-1);
	      int endRow = currentPage * pageSize;

	      List<Comm_MemberDTO> member = null;
	      member = memberMapper.comm_memberList(startRow, endRow);
		 
	      ModelAndView mav = new ModelAndView();
	      mav.addObject("comm_memberList", member);

	      mav.setViewName("comm/member/comm_memberList");
	      return mav;
		}
	 
	

/*	@RequestMapping(value = "/comm_memberList.do")
	public String commlistMember(HttpServletRequest req,HttpSession session,Comm_MemberDTO dto){
		
		List<Comm_MemberDTO> list = memberMapper.comm_memberList();
		
		session.setAttribute("comm_memberList", list);
		
	
		return "comm/member/comm_memberList";
	}*/
	
	@RequestMapping(value = "/admin_comm_memberList.do")
	public String admin_commlistMember(HttpServletRequest req,HttpSession session,Comm_MemberDTO dto){
		
		List<Comm_MemberDTO> list = memberMapper.comm_memberList();
		
		session.setAttribute("comm_memberList", list);
		
		return "comm/admin_comm_memberList";
	}
	
	
	@RequestMapping(value="/comm_member_edit.do")
	public ModelAndView commMemberEdit(HttpServletRequest req,HttpSession session,
											@RequestParam int comm_memberNum){
		Comm_MemberDTO dto = memberMapper.comm_getMember(comm_memberNum);
		List<Comm_MemberDTO> list = memberMapper.comm_memberList();
		req.setAttribute("comm_memberList", list);
		ModelAndView mav = new ModelAndView
				("comm/member/comm_member_edit", "comm_getMember", dto);
		
		
		return mav;
	}
		
	@RequestMapping(value = "/comm_member_edit_ok.do", method = RequestMethod.POST)
	public String commMemberEditOk(HttpServletRequest req, HttpSession session,
									Comm_MemberDTO dto, @RequestParam int comm_memberNum , BindingResult result) {
		
		String comm_profilename="";
		int comm_profilesize=0;
		MultipartHttpServletRequest mr = (MultipartHttpServletRequest)req;
		MultipartFile file = mr.getFile("filename");
		File target = new File(upLoadPath, file.getOriginalFilename());
		if(file.getSize()>0){
			try {
				file.transferTo(target);
			} catch (IOException e) {}
			comm_profilename = file.getOriginalFilename();
			comm_profilesize=(int)file.getSize();
			dto.setComm_profilename(comm_profilename);
			dto.setComm_profilesize(comm_profilesize);
		}
		else{
			Comm_MemberDTO mdto = memberMapper.comm_getMember(comm_memberNum);
			dto.setComm_profilename(mdto.getComm_profilename());
			dto.setComm_profilesize(mdto.getComm_profilesize());
		}
			
		
		int res = memberMapper.comm_updateMember(dto);
		String msg = null, url = null;
		if (res>0) {
			msg = "회원수정성공! 메인페이지로 이동합니다.";
			url = "commhome.comm";
		} else {
			msg = "회원수정실패! 메인페이지로 이동합니다.";
			url = "commhome.comm";
		}
		
		req.setAttribute("msg", msg);
		req.setAttribute("url", url);
		return "message";
	}
	
	
	@RequestMapping("/comm_member_delete.do")
	public String memberDelete(HttpServletRequest req,@RequestParam int comm_memberNum){
				
		int res = memberMapper.comm_deleteMember(comm_memberNum);
		Comm_MemberDTO login = memberMapper.comm_getMember(comm_memberNum);
		
		String msg = null, url = null;
		if(res>0){
			HttpSession session = req.getSession();
			session.setAttribute("comm_login", login);
			msg="회원삭제성공!";
			url="commhome.comm";
		}else{
			msg="회원삭제실패!";
			url="commhome.comm";
		}
		req.setAttribute("msg", msg);
		req.setAttribute("url", url);
		return "message";	
	}
	
}