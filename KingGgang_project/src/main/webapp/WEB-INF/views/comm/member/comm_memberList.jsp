<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/top.jsp"%>
<footer class="footer-box">
		<div class="container">
			<div class="row">
				<div class="col-md-12 white_fonts">
					<div class="row">
						<div class="col-sm-6 col-md-6 col-lg-3">
							<div class="full">
								<h3><img src="${pageContext.request.contextPath}/resources/img/낑깡logo.png" width="100" height="100">회원목록</h3>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
</footer>
<!-- 		<table border="1" width="800">
		<tr bgcolor="skyblue">
			<th>이름</th>
			<th>닉네임</th>
			<th>생년월일</th>
			<th>프로필사진</th>
			<th>자기소개</th>
			<th>친구추가</th>
			<th>채팅하기</th>
			<th>삭제</th>
		</tr> -->
<div class="section layout_padding" align="center">
		<div id="more_list">
		<div class="row">
			<c:if test="${empty comm_memberList}">
				<h2>가입한 회원이 없습니다.</h2>
			</c:if>				
		<c:forEach var="dto" items="${comm_memberList }">
		<div class="col-md-3 col-sm-6 col-xs-12">
			<c:if test="${dto.comm_profilename  == 'basic.jpg'}">
				<img src="${pageContext.request.contextPath}/resources/img/basic.jpg" width="200" height="200">
			</c:if>
			<c:if test="${dto.comm_profilename  != 'basic.jpg'}">
			 	<img src="http://192.168.0.184:8080/img/${dto.comm_profilename}" width="200" height="200">
			</c:if>
				<p><font size=5>${dto.comm_name}(${dto.comm_nickname})</font></p>
				<p>${dto.comm_birth }</p>
				<p>${dto.comm_intro }</p>
				<p><a style="color: blue" href="comm_friend_insert.do?login_comm_memberNum=${login_comm_memberNum }&comm_memberNum=${dto.comm_memberNum }">친구추가</a>
				| <a style="color: red" href="" onclick="window.open('room?comm_memberNum=${dto.comm_memberNum}&comm_nickname=${dto.comm_nickname}', '_blank', 'width=600 height=600')">채팅하기</a></p>
		</div>
		</c:forEach>
		</div> 
		<div class="row" id="moreList">
         </div>
    	</div>
        
</div>
      
         <div class="row margin-top_30">
            <div class="col-sm-12">
               <div class="full">
                  <br>
                  <div class="center">
                     <a class="main_bt" id="more_btn_a"
                        href="javascript:loadNextPage();">See More ></a>
                  </div>
               </div>
            </div>
         </div>
</body>

<%@ include file="/WEB-INF/views/bottom.jsp"%>
<style>
#moreList{
width:200;
height:200;
}
</style>
<script>
/* 더보기기능 */
   function loadNextPage() {
      var list_length = $("#more_list img").length+1;
      
      var callLength = list_length;
      var cnt = 3;
      
      var startRow = list_length;
      var endRow = startRow+cnt;
      var obj = {"startRow":startRow,
               "endRow":endRow};
   
      $.ajax({
          type:'post', 
          url:"<c:url value="/memberajaxList.do" />",
          data:JSON.stringify(obj),
          dataType: 'json', 
          contentType: "application/json;", 
          success : function(data){
               for(var i=0; i<data.length; i++){
                     $('#moreList').append("<div class='col-md-3 col-sm-6 col-xs-12'><img src='http://192.168.0.184:8080/img/"+data[i].file+"' width='200' height='200' alt='#' /><p><font size=5>"+data[i].name+"</font><font size=5>("+data[i].nickname+")</font></p>"+
                    		 						"<p>"+data[i].birth+"</p><p>"+data[i].intro+"</p>"+
                    		 						"<p><a style='color: blue' href='comm_friend_insert.do?login_comm_memberNum="+data[i].login+
                    		 						"&comm_memberNum="+data[i].num+"'>친구추가</a> | "+
                    		 						"<a style='color: red' href='' onclick=\"window.open('room?comm_memberNum="+data[i].num+"&comm_nickname="+data[i].nickname+"','_blank', 'width=600 height=600')\">채팅하기</a></p></div>");
             }
                
             },
          error: function(errorThrown) { alert(errorThrown.statusText); }
       });
                 
   
}


</script>


<script type="text/javascript">
$(function() {
    $(document).on('click', function(e) {
        if (e.target.id === 'word') {
           $('#memberSearch').show();
        } else {
            $('#memberSearch').hide();
        }
    })
});
</script>
