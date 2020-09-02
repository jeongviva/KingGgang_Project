// 지은
package com.teamb.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.teamb.model.Comm_MemberDTO;
import com.teamb.model.MemberDTO;
import com.teamb.service.Comm_MemberMapper;


@Controller
public class Comm_MemberController {
	@Autowired
	private Comm_MemberMapper memberMapper;
	
	
	@Resource(name="upLoadPath")
	private String upLoadPath;
	
	
	@RequestMapping(value = "/Comm_loginOk.log")
	public String comm_loginOk(Comm_MemberDTO dto, HttpServletRequest req,HttpSession session, int comm_memberNum) {
	    // 가입되어있으면 가입된 멤버입니다.(or 가입하기 버튼이 회원입니다.) 메세지 창 나오고, 가입안되있으면 insert 하는 폼 나오기(or 가입하기 버튼)
		session.getAttribute("mbId");
		boolean isLogin=false;
		dto = memberMapper.comm_getMember(comm_memberNum);
		int res = memberMapper.comm_loginOk(dto);      
	      String msg = null, url = null;
	      switch(res){
	      case Comm_MemberDTO.OK:
	       //  session = req.getSession();
	         isLogin=true;
	         session.setAttribute("isLogin", isLogin);
	         session.setAttribute("memberNum", dto.getMemberNum());
	         session.setAttribute("comm_memberNum", dto.getComm_memberNum());
	         session.setAttribute("comm_member",memberMapper.comm_getMember(comm_memberNum));
	         
	         MemberDTO mdto = new MemberDTO();
	            if(mdto.getId().equals("admin")) {
	            msg = "관리자만 이용 가능";
	            url = "commhome.comm";
	            }
	            else{
	            	if(session.getAttribute("comm_memberNum") == null){
	            		msg = "가입페이지로 이동";
	            		url = "commhome.comm";
	            	}else{
	            		msg = "이미 등록된 회원입니다.";
	            		url = "commhome.comm";
	            	}
	            } 
	            break;
	      }
	      req.setAttribute("msg", msg);
	      req.setAttribute("url", url);
	      return "message";
	   }
	
	
	@RequestMapping("/comm_member_input.do")
	public String comminsertMemberForm(HttpServletRequest req,HttpSession session){
		//Comm_MemberDTO dto = memberMapper.comm_getMember(comm_memberNum);
		int memberNum = (Integer)session.getAttribute("memberNum");
			//int comm_memberNum = (Integer)session.getAttribute("comm_memberNum");
			//List<Comm_MemberDTO> list = (List<Comm_MemberDTO>) session.getAttribute("comm_memberList"); //20200902
			//session.setAttribute("num",list.get(0)); //20200902
			//System.out.println(list.get(0)); //20200902
			//int num = (Integer)session.getAttribute("num"); // 가입완료 후 받는 memberNum값 넘겨받기.
		
		String msg = null, url = null;
		
			//20200902
			/*if(memberNum == num){
				msg = "이미 가입되어 있습니다.";
				url = "commhome.comm";
				
				req.setAttribute("msg", msg);
				req.setAttribute("url", url);
				return "message";
			} 				*/	
			//20200902
		return "comm/member/comm_insertMember";
	}
	
	@RequestMapping("/comm_member_input_ok.do")
	public String comminsertMember(HttpServletRequest req, HttpSession session,
											Comm_MemberDTO dto,BindingResult result){
		
		int memberNum = (Integer)session.getAttribute("memberNum");
	       
		
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
			
			dto.setComm_profilename(comm_profilename);
			dto.setComm_profilesize(comm_profilesize);
			dto.setMemberNum(memberNum);
		
		    
		
		    int res = memberMapper.comm_insertMember(dto);
		    session.setAttribute("commId", dto.getComm_name()); //20200902
		    session.setAttribute("comm_memberNum", dto.getComm_memberNum());
		  //  session.setAttribute("num", ); 
		    // 가입하면서 dto에 저장된 memberNum값 저장!!!! 해서 'input.do'실행시 값 가져오기 해아 중복 가입이 안됨.
		String msg = null, url = null;
		if(res>0){
			msg="가입성공";
			url="comm_memberList.do";
			//url="commhome.comm"; 
		}else{
			msg="가입실패";
			url="commhome.comm";
		}
		req.setAttribute("msg", msg);
		req.setAttribute("url", url);
		return "message";
	}

	// 목록은 관리자만 볼 수 있음.
	@RequestMapping(value = "/comm_memberList.do")
	public String commlistMember(HttpServletRequest req,HttpSession session,Comm_MemberDTO dto){
		
		List<Comm_MemberDTO> list = memberMapper.comm_memberList();
		
		session.setAttribute("comm_memberList", list);
		
		req.setAttribute("upLoadPath", upLoadPath);
		
		return "comm/member/comm_memberList";
	}
	
	
	@RequestMapping(value="/comm_member_edit.do")
	public ModelAndView commMemberEdit(HttpServletRequest req,HttpSession session,
											@RequestParam int comm_memberNum){
		Comm_MemberDTO dto = memberMapper.comm_getMember(comm_memberNum);
		session.getAttribute("comm_memberNum");
		ModelAndView mav = new ModelAndView
				("comm/member/comm_member_edit", "getMember", dto);
		
		req.setAttribute("comm_getMember", memberMapper.comm_getMember(comm_memberNum));
		
		return mav;
	}
		
	@RequestMapping(value = "/comm_member_edit_ok.do", method = RequestMethod.POST)
	public String commMemberEditOk(HttpServletRequest req, HttpSession session, 
			@ModelAttribute Comm_MemberDTO dto, BindingResult result) {
		
		int res = memberMapper.comm_updateMember(dto);

		String msg = null, url = null;
		if (res > 0) {
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
		String msg = null, url = null;
		if(res>0){
			msg="회원삭제성공!";
			url="comm_memberList.do";
		}else{
			msg="회원삭제실패!";
			url="comm_memberList.do";
		}
		req.setAttribute("msg", msg);
		req.setAttribute("url", url);
		return "message";	
	}
}