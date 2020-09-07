<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>	
<html>
	<head>
		<title>낑깡</title>
		<meta charset="utf-8" />
	</head>

	<body>
		<div>

			<!-- Header -->
				<header id="header">
					<h1 id="logo"><a href="home.do">낑깡</a></h1>
					<nav id="nav">
						<ul>
							<li><a href="comm_newsfeed.do">News Feed</a></li>
							<li><a href="comm_writeForm.do">Write</a></li>
							<li>
								<a href="#">Mypage</a>
								<ul>
									<li><a href="comm_myPage.do">Mypage</a></li>
									<li><a href="comm_bookMark.do">BookMark</a></li>
									<li>
									<a href="comm_member_edit.do?memberNum=${memberNum}">Settings</a>
									</li>
									
								</ul>
							</li>
							<li><a href="comm_friendAll.do">Friends 목록</a></li>
							<li><a href="comm_insertFriend.do?memberNum=${memberNum }">Friends 추가</a></li>
							<li><a href="comm_memberAll.do">Friends</a></li>
							<%-- <c:if test="${isLogin}">
							<li><a href="logout.do" class="button primary">로그아웃</a></li>
							</c:if>
							<c:if test="${!isLogin}">
							<li><a href="login.do" class="button primary">로그인 하기</a></li>
							</c:if>				  --%>
						</ul>
					</nav>
				</header>