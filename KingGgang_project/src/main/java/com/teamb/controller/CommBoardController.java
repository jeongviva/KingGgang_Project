package com.teamb.controller;

import java.io.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.swing.text.html.HTML.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.teamb.model.CommBookmarkDTO;
import com.teamb.model.CommLikeDTO;
import com.teamb.model.CommReplyDTO;
import com.teamb.model.Comm_MemberDTO;
import com.teamb.model.CommboardDTO;
import com.teamb.model.HashTagDTO;
import com.teamb.model.MemberDTO;
import com.teamb.model.Post_TagDTO;
import com.teamb.model.WishlistDTO;
import com.teamb.service.CommBookMarkMapper;
import com.teamb.service.CommLikeMapper;
import com.teamb.service.CommReplyMapper;
import com.teamb.service.Comm_MemberMapper;
import com.teamb.service.CommboardMapper;
import com.teamb.service.HashTagMapper;
import com.teamb.service.Post_TagMapper;

@Controller
public class CommBoardController {

	@Autowired
	private CommboardMapper boardMapper;

	@Autowired
	private Comm_MemberMapper comm_memberMapper;

	@Autowired
	private CommReplyMapper replyMapper;

	@Autowired
	private CommBookMarkMapper bookmarkMapper;

	@Autowired
	private HashTagMapper hashtagMapper;

	@Autowired
	private Post_TagMapper post_tagMapper;

	@Autowired
	private CommLikeMapper likemapper;
	
	@Resource(name = "upLoadPath")
	private String upLoadPath;

	//글쓰기
	@RequestMapping(value = "/comm_writeForm.do", method = RequestMethod.GET)
	public String writeForm(HttpServletRequest req) {
		return "comm/board/comm_writeForm";
	}

	@RequestMapping(value = "/comm_writePro.do", method = RequestMethod.POST)
	public String writePro(HttpServletRequest req, HttpSession session, @ModelAttribute CommboardDTO dto,
			BindingResult result) {

		if (result.hasErrors()) {
			dto.setBoardNum(0);
		}

		MemberDTO member = (MemberDTO) session.getAttribute("login");

		int comm_memberNum = (Integer) session.getAttribute("comm_memberNum");
		Comm_MemberDTO commmember = (Comm_MemberDTO) session.getAttribute("commmember");

		String file_name = "";
		int file_size = 0;
		MultipartHttpServletRequest mr = (MultipartHttpServletRequest) req;
		MultipartFile file = mr.getFile("file_name");
		File target = new File(upLoadPath, file.getOriginalFilename());
		if (file.getSize() > 0) {
			try {
				file.transferTo(target);
			} catch (IOException e) {
			}
			file_name = file.getOriginalFilename();
			file_size = (int) file.getSize();
		}
		dto.setComm_memberNum(comm_memberNum);
		dto.setFile_name(file_name);
		dto.setFile_size(file_size);
		int boardNum = boardMapper.writeBoard(dto);

		// 여진
		String tag = req.getParameter("hashtag").replaceAll("\\p{Z}","");
		String hashtag[] = tag.split("#");
		List<HashTagDTO> hashList = new ArrayList<>();
		for (int i = 1; i < hashtag.length; i++) {
			if (hashtagMapper.isHashTag(hashtag[i]) == null) {
				hashtagMapper.insertTag(hashtag[i]);
				hashList.add(hashtagMapper.isHashTag(hashtag[i]));
			} else {
				hashList.add(hashtagMapper.isHashTag(hashtag[i]));
			}
		}

		for (int i = 0; i < hashList.size(); i++) {
			HashTagDTO list = hashList.get(i);
			post_tagMapper.insertPostTag(boardNum, list.getTagId());
		}

		// 지은
		req.setAttribute("look", dto.getLook());
		System.out.println("look값INSERT"+dto.getLook());;

		String msg = null, url = null;
		if (boardNum > 0) {
			msg = "게시물이 등록되었습니다.";
			url = "comm_myPage.do";
		} else {
			msg = "게시물 등록에 실패하였습니다.";
			url = "comm_writeForm.do";
		}
		req.setAttribute("msg", msg);
		req.setAttribute("url", url);
		return "message";
	}
	
	//마이페이지
	@RequestMapping("/comm_myPage.do")

	public String myPage(HttpServletRequest req, HttpSession session) {

		Comm_MemberDTO login = (Comm_MemberDTO) session.getAttribute("comm_login");
		int comm_memberNum = login.getComm_memberNum();

		List<CommboardDTO> list = boardMapper.listBoard(comm_memberNum);
		Comm_MemberDTO dto = comm_memberMapper.comm_getMember(comm_memberNum);

		req.setAttribute("boardList", list);
		req.setAttribute("comm_profilename", dto.getComm_profilename());
		req.setAttribute("comm_nickname", dto.getComm_nickname());
		req.setAttribute("comm_intro", dto.getComm_intro());
		req.setAttribute("loginNum", comm_memberNum);
		req.setAttribute("memberNum", comm_memberNum);
		return "comm/board/comm_myPage";  
	}

	//상세보기
	@RequestMapping(value = "/comm_content.do", method = RequestMethod.GET)
	public String content(HttpServletRequest req, HttpSession session, @RequestParam int boardNum) {
		
		CommBookmarkDTO cmdto = new CommBookmarkDTO();
		cmdto.setBoardNum(boardNum);
		cmdto.setComm_memberNum(cmdto.getComm_memberNum());
		
		CommBookmarkDTO markCheck = bookmarkMapper.markPro(cmdto);
		
		int check2 = 1;
		
		if(markCheck == null) {
			check2 = 1;
		} else {
			check2 = 2;
		}

		System.out.println(boardNum);

		CommboardDTO dto = boardMapper.getBoard(boardNum);
		req.setAttribute("getBoard", dto);
		
		System.out.println(dto.getComm_memberNum());
		
		//원세호 게시판 좋아요
		CommLikeDTO cdto =  new CommLikeDTO();
		cdto.setBoardNum(boardNum);
		cdto.setComm_memberNum(dto.getComm_memberNum());
		
		CommLikeDTO likeCheck = likemapper.getCommLIke(cdto);
		
		int check1 =1;
		
		if(likeCheck == null) {
			check1 = 1;
		}else {
			check1 = 2;
		}
		
		System.out.println(check1);
		req.setAttribute("check1", check1);
		List<CommReplyDTO> list = replyMapper.listReply(boardNum);
		
		req.setAttribute("replyList", list);
		req.setAttribute("check1", check1);

		return "comm/board/comm_content";
	}
	
	//북마크
	@ResponseBody 
	@RequestMapping(value = "/bookmark", method = RequestMethod.POST) 
	public HashMap<String, Object> init(HttpSession session, @RequestBody HashMap<String, Object> map) {
		
		System.out.println(map);
		
		Comm_MemberDTO login = (Comm_MemberDTO) session.getAttribute("comm_login");
		int comm_memberNum = login.getComm_memberNum();
		System.out.println("로그인"+comm_memberNum);
		
		int boardNum = Integer.parseInt(map.get("boardNum").toString());
		
		System.out.println(boardNum);
		CommBookmarkDTO dto = new CommBookmarkDTO();
		dto.setBoardNum(boardNum);
		dto.setComm_memberNum(comm_memberNum);
		
		boolean check1 = true;
		
		CommBookmarkDTO markCheck = bookmarkMapper.markPro(dto);
		
		if(markCheck ==null) {
			check1 = true;
		} else {
				check1 = false;
		}
		
		System.out.println(check1);
		
		if(check1) {
			int res = bookmarkMapper.insertmark(dto);
			map.put("wstatus", 1);
		} else {
			int res = bookmarkMapper.deleteMark(dto);
			map.put("wstatus", 2);
		}
		
		System.out.println(map);
		
		return map;
	}

	@RequestMapping("/comm_bookMarkPro.do")
	public String bookMarkPro(HttpServletRequest req, HttpSession session, CommBookmarkDTO dto, Comm_MemberDTO mdto,
			@RequestParam int boardNum) {

		CommboardDTO bdto = boardMapper.getBoard(boardNum);
		req.setAttribute("getBoard", bdto);

		int comm_memberNum = mdto.getComm_memberNum();

		int res = bookmarkMapper.insertmark(dto);
		String msg = null, url = null;
		if (res > 0) {
			msg = "보관함에 저장되었습니다.";
			url = "comm_bookMark.do";
		}
		req.setAttribute("msg", msg);
		req.setAttribute("url", url);
		return "message";
	}

	@RequestMapping("/comm_bookMark.do")
	public String bookmark(HttpServletRequest req, HttpSession session) {
		
		int comm_memberNum = (Integer) session.getAttribute("comm_memberNum");
		List<CommBookmarkDTO> list = bookmarkMapper.listMark(comm_memberNum);
		for (CommBookmarkDTO cmdto : list) {
			int cm = cmdto.getBoardNum();
			CommboardDTO dto = boardMapper.getBoard(cm);
			cmdto.setCm_file_name(dto.getFile_name());
			cmdto.setCm_file_size(dto.getFile_size());
		}
		req.setAttribute("bookmarkList", list);

		return "comm/board/comm_bookMark";
	}
	
	//원세호 게시판 좋아요
	@ResponseBody 
	@RequestMapping(value = "/insDelLike", method = RequestMethod.POST) 
	public HashMap<String, Object> init(@RequestBody HashMap<String, Object> map,HttpSession session) {
		
		System.out.println(map); // {no=${boardNum} 출력 }
		
		int boardNum = Integer.parseInt(map.get("boardNum").toString());
		//로그인세션
		Comm_MemberDTO login = (Comm_MemberDTO) session.getAttribute("comm_login");
	    int comm_memberNum = login.getComm_memberNum();
		
		System.out.println(boardNum);
		
		CommLikeDTO cdto = new CommLikeDTO();
		cdto.setBoardNum(boardNum);
		cdto.setComm_memberNum(comm_memberNum);
		
		boolean check1 = true;
		
		CommLikeDTO likeCheck = likemapper.getCommLIke(cdto);
		if(likeCheck == null) {
			check1 = true;
		}else {
			check1 = false;
		}
		
		System.out.println(check1);
		
		if(check1) {
			int res = likemapper.insertLike(cdto);
			int res1 = likemapper.plusLikeCount(boardNum);
			map.put("wstatus", 1);
			
		} else {
			int res = likemapper.deleteLike(cdto);
			int res1 = likemapper.minusLikeCount(boardNum);
			map.put("wstatus", 2);
		}
		
		int likeCount = likemapper.getLikeCount(boardNum);
			map.put("likeCount", likeCount);
			
		System.out.println(map); //{"boardNum"= ${boardNum}, "wstatus"=1}
		
		return map;
	}

	//글수정
	@RequestMapping(value = "/comm_updateForm.do", method = RequestMethod.GET)
	public ModelAndView updateForm(@RequestParam int boardNum, HttpSession session) {
		ModelAndView mav = new ModelAndView();
		CommboardDTO dto = boardMapper.getBoard(boardNum);
		List<Post_TagDTO> post = post_tagMapper.getPostTagId(boardNum);
		List<HashTagDTO> tag = new ArrayList<>();

		for (int i = 0; i < post.size(); i++) {
			Post_TagDTO tagId = post.get(i);
			HashTagDTO tagImform = (hashtagMapper.getTagName(tagId.getTagId()));
			tag.add(tagImform);
		}
		mav.addObject("tag", tag);
		mav.addObject("getBoard", dto);
		mav.setViewName("comm/board/comm_updateForm");
		return mav;
	}

	@RequestMapping(value = "/comm_updatePro.do", method = RequestMethod.POST)
	public String updatePro(HttpServletRequest req, HttpSession session, CommboardDTO dto, @RequestParam int boardNum) {

		String file_name = "";
		int file_size = 0;
		MultipartHttpServletRequest mr = (MultipartHttpServletRequest) req;
		MultipartFile file = mr.getFile("filename");
		File target = new File(upLoadPath, file.getOriginalFilename());
		if (file.getSize() > 0) {
			try {
				file.transferTo(target);
			} catch (IOException e) {
			}

			file_name = file.getOriginalFilename();
			file_size = (int) file.getSize();
			dto.setFile_name(file_name);
			dto.setFile_size(file_size);
		} else {
			CommboardDTO bdto = boardMapper.getBoard(boardNum);
			dto.setFile_name(bdto.getFile_name());
			dto.setFile_size(bdto.getFile_size());
		}

		int res = boardMapper.updateBoard(dto);

		// 여진
		String updateTag = req.getParameter("updateTag").replaceAll("\\p{Z}","");
		String hashtag[] = updateTag.split("#");
		List<HashTagDTO> hashList = new ArrayList<>();
		for (int i = 1; i < hashtag.length; i++) {
			if (hashtagMapper.isHashTag(hashtag[i]) == null) {
				hashtagMapper.insertTag(hashtag[i]);
				hashList.add(hashtagMapper.isHashTag(hashtag[i]));
			} else {
				hashList.add(hashtagMapper.isHashTag(hashtag[i]));
			}
		}
		
		post_tagMapper.deletePostTag(boardNum);
		for (int i = 0; i < hashList.size(); i++) {
			HashTagDTO list = hashList.get(i);
			post_tagMapper.insertPostTag(boardNum, list.getTagId());
		}
		
		hashtagMapper.deleteHash();
			
		String msg = null, url = null;
		if (res > 0) {
			msg = "게시글 수정되었습니다.";
			url = "comm_myPage.do";
		} else {
			msg = "게시글 수정에 실패하였습니다. 다시 시도 해 주세요.";
			url = "comm_updateForm.do";
		}
		req.setAttribute("msg", msg);
		req.setAttribute("url", url);

	      return "message";
	   }

	//글삭제
	@RequestMapping(value = "/comm_deletePro.do")
	public ModelAndView deletePro(@RequestParam int boardNum) {
		int res = boardMapper.deleteBoard(boardNum);
		String msg = null, url = null;
		if (res > 0) {
			msg = "게시글이 삭제되었습니다.";
			url = "comm_myPage.do";
		}
		post_tagMapper.deletePostTag(boardNum);
		hashtagMapper.deleteHash();
		ModelAndView mav = new ModelAndView();
		mav.addObject("msg", msg);
		mav.addObject("url", url);
		mav.setViewName("message");
		return mav;
	}
	
	//댓글
	@RequestMapping(value = "/comm_writeReplyPro.do", method = RequestMethod.POST)
	public String writeReplyPro(HttpServletRequest req, CommReplyDTO dto, BindingResult result,
			@RequestParam int boardNum) {

		if (result.hasErrors()) {
			dto.setReplyNum(0);
		}

		int res = replyMapper.writeReply(dto);
		String msg = null, url = null;
		if (res > 0) {
			msg = "댓글작성완료";
			url = "comm_otherContent.do?boardNum=" + boardNum;
		} else {
			msg = "댓글작성실패";
			url = "comm_content.do";
		}
		req.setAttribute("msg", msg);
		req.setAttribute("url", url);
		return "message";
	}

	@RequestMapping(value = "/reply_deletePro.do")

	public String deletereplyPro(HttpServletRequest req, @RequestParam int replyNum, int boardNum) {

		int res = replyMapper.deleteReply(replyNum);
		String msg = null, url = null;
		if (res > 0) {
			msg = "댓글삭제성공";
			url = "comm_content.do?boardNum=" + boardNum;
		} else {
			msg = "댓글삭제실패!!";
			url = "comm_content.do";
		}
		req.setAttribute("msg", msg);
		req.setAttribute("url", url);
		req.setAttribute("replyNum", replyNum);
		req.setAttribute("boardNum", boardNum);

		return "message";
	}

	// 여진
	@RequestMapping("/comm_otherPage.do")
	public String otherPage(HttpServletRequest req, HttpSession session) {
		Comm_MemberDTO login = (Comm_MemberDTO) session.getAttribute("comm_login");
		int loginNum = 0;
		if (login != null) {
			loginNum = login.getComm_memberNum();
		}

		int comm_memberNum = Integer.parseInt(req.getParameter("comm_memberNum"));
		List<CommboardDTO> list = boardMapper.listBoard(comm_memberNum);
		Comm_MemberDTO dto = comm_memberMapper.comm_getMember(comm_memberNum);

		req.setAttribute("boardList", list);
		req.setAttribute("comm_profilename", dto.getComm_profilename());
		req.setAttribute("comm_nickname", dto.getComm_nickname());
		req.setAttribute("comm_intro", dto.getComm_intro());
		req.setAttribute("loginNum", loginNum);
		req.setAttribute("memberNum", comm_memberNum);
		return "comm/board/comm_myPage";
	}

	// 여진
	@RequestMapping(value = "/comm_otherContent.do", method = RequestMethod.GET)
	public String otherContent(HttpServletRequest req, @RequestParam int boardNum, HttpSession session) {
		Comm_MemberDTO login = (Comm_MemberDTO) session.getAttribute("comm_login");
		int loginNum = 0;
		if (login != null) {
			loginNum = login.getComm_memberNum();
		}
		
		//인아
		System.out.println(boardNum);
		CommboardDTO dto = boardMapper.getBoard(boardNum);
		req.setAttribute("getBoard", dto);
		
		CommBookmarkDTO cmdto = new CommBookmarkDTO();
		cmdto.setBoardNum(boardNum);
		cmdto.setComm_memberNum(dto.getComm_memberNum());
		
		CommBookmarkDTO markCheck = bookmarkMapper.markPro(cmdto);
		
		int check2 = 1;
		
		if(markCheck == null) {
			check2 = 1;
		} else {
			check2 = 2;
		}
		System.out.println(check2);

		//세호
		CommLikeDTO cdto =  new CommLikeDTO();
		cdto.setBoardNum(boardNum);
		cdto.setComm_memberNum(dto.getComm_memberNum());
		
		CommLikeDTO likeCheck = likemapper.getCommLIke(cdto);
		
		int check1 =1;
		
		if(likeCheck == null) {
			check1 = 1;
		}else {
			check1 = 2;
		}
		
		int likeCount = likemapper.getLikeCount(boardNum);
		req.setAttribute("likeCount", likeCount);
		
		System.out.println(check1);
		req.setAttribute("check1", check1);
		
		List<CommReplyDTO> list = replyMapper.listReply(boardNum);
		req.setAttribute("replyList", list);
		req.setAttribute("check1", check1);

		List<Post_TagDTO> post = post_tagMapper.getPostTagId(boardNum);
		List<HashTagDTO> tag = new ArrayList<>();

		for (int i = 0; i < post.size(); i++) {
			Post_TagDTO tagId = post.get(i);
			HashTagDTO tagImform = (hashtagMapper.getTagName(tagId.getTagId()));
			tag.add(tagImform);
		}
		Comm_MemberDTO member = comm_memberMapper.comm_getMember(dto.getComm_memberNum());
		req.setAttribute("loginNum", loginNum);
		req.setAttribute("comm_profilename", member.getComm_profilename());
		req.setAttribute("comm_nickname", member.getComm_nickname());
		req.setAttribute("memberNum", member.getComm_memberNum());
		req.setAttribute("tag", tag);
		
		return "comm/board/comm_content";
	}
}
