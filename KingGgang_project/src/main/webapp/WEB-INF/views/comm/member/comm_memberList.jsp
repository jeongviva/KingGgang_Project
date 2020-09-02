<%@ include file="/WEB-INF/views/top.jsp"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
	<div class="wrap wd668" align="center">
            <div class="container">
                <div class="form_txtInput">
                    <h2 class="sub_tit_txt">회원목록</h2>
		<table border="1" width="800">
		<tr bgcolor="skyblue">
			<th>memberNum/Comm_memberNum</th>
			<th>이름</th>
			<th>닉네임</th>
			<th>생년월일</th>
			<th>프로필사진</th>
			<th>자기소개</th>
			<th>삭제</th>
		</tr>
		<c:if test="${empty comm_memberList}">
			<tr>
					<td colspan="6">등록된 회원이 없습니다.</td>
			</tr>
		</c:if>
		
		<c:forEach var="dto" items="${comm_memberList }">
			<tr>
				<td>${dto.memberNum} //// ${dto.comm_memberNum }</td>
				<td>${dto.comm_name}</td>
				<td>${dto.comm_nickname}</td>
				<td>${dto.comm_birth }</td>
				<td>${dto.comm_profilename }</td>
				<td>${dto.comm_intro }</td>
				<td><a href="comm_friend_insert.do?comm_memberNum=${dto.comm_memberNum }">친구추가</a></td> 
				<td><a href="comm_member_delete.do?comm_memberNum=${dto.comm_memberNum }">삭제</a></td> 
			</tr>
		</c:forEach>
		</table>
	</div>
	</div>
	</div>
<%@ include file="/WEB-INF/views/bottom.jsp"%>