<%@ page language="java" pageEncoding="GBK"%>
<html>
  <head>
    <title></title>
    <link href="<%=request.getContextPath()%>/xzzx.css" rel="stylesheet" type="text/css" />
    <script type="text/javascript">
    	function al(){

    		alert("���β���ʧ�ܣ��������ֵ�д���   \' \" \|\| \+  �ȷǷ��ַ�,���������������ֵ!");
        }
    </script>
  </head>  
  <body style="color:gray;font-size:14px;text-align:center" onload="al()">
  	<!--jsp:include page="head.jsp"/-->
	<div style="padding:20px">���β���ʧ�ܣ��������ֵ�д���   <span style="color: red;"> ' " || + </span> �ȷǷ��ַ�,���������������ֵ!(<span id=sec>7</span>���ת����ҳ).</div>
	<!--jsp:include page="bottom.jsp"/-->
	<script type="text/javascript">
	//setTimeout(function(){location.href='<%=request.getContextPath()%>/index.jsp'},3000);
	setTimeout(function(){window.history.go(-1)},7000);
	setInterval(js,1000);
	function js(){
	 if(document.all.sec.innerText>0) 
	  document.all.sec.innerText--;
	}
	
	</script>
  </body>
</html>
