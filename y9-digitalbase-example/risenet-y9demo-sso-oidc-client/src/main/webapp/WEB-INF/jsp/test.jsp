<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.risesoft.model.user.*" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta charset="utf-8">
<title>登录成功</title>
<style>
body {font-family: Arial;}

/* Style the tab */
.tab {
  overflow: hidden;
  border: 1px solid #ccc;
  background-color: #f1f1f1;
}

/* Style the buttons inside the tab */
.tab button {
  background-color: inherit;
  float: left;
  border: none;
  outline: none;
  cursor: pointer;
  padding: 14px 16px;
  transition: 0.3s;
  font-size: 17px;
}

/* Change background color of buttons on hover */
.tab button:hover {
  background-color: #ddd;
}

/* Create an active/current tablink class */
.tab button.active {
  background-color: #ccc;
}

/* Style the tab content */
.tabcontent {
  display: none;
  padding: 6px 12px;
  border: 1px solid #ccc;
  border-top: none;
}
</style>
</head>
<body>
<center>
    <h1>登录成功!</h1>
    <h2 id="userName">欢迎您：${sessionScope.userInfo.loginName}</h2>
    
    <div class="tab">
      <button class="tablinks" onclick="openTab('tab1')" id="btn1">accessToken</button>
      <button class="tablinks" onclick="openTab('tab2')">jwtId</button>
      <button class="tablinks" onclick="openTab('tab3')">idToken</button>
      <button class="tablinks" onclick="openTab('tab4')">refreshToken</button>
    </div>

    <div id="tab1" class="tabcontent">
      <textarea id="accessToken" rows="10" cols="100">${sessionScope.accessToken}</textarea>
    </div>

    <div id="tab2" class="tabcontent">
      <textarea id="jwtId" rows="10" cols="100">${sessionScope.jwtId}</textarea>
    </div>

    <div id="tab3" class="tabcontent">
        <textarea id="idToken" rows="10" cols="100">${sessionScope.idToken}</textarea>
    </div>
    
    <div id="tab4" class="tabcontent">
        <textarea id="refreshToken" rows="10" cols="100">${sessionScope.refreshToken}</textarea>
    </div>
        
    <h3><a id="api01" href="#">api调用(Header传参)</a></h3>
    返回的json:<br/>
    <div id="content01"></div><br/>
    错误码：<span id="error_details_01"></span>
    
    <br/><br/><br/><br/>
    <h3><a id="api02" href="#">Token Exchange(切换岗位)</a></h3>
    返回的json:<br/>
    <textarea id="content02" rows="6" cols="100"></textarea><br/>
    错误码：<span id="error_details_02"></span>
    
    <br/><br/><br/><br/>
    <h3><a id="api03" href="#">another user logon(安全审计员)</a></h3>
    返回的json:<br/>
    <textarea id="content03" rows="6" cols="100"></textarea><br/>
    错误码：<span id="error_details_03"></span>
    
    <br/><br/><br/><br/>
    <h3><a id="api04" href="#">logout</a></h3>
    返回的json:<br/>
    <textarea id="content04" rows="4" cols="100"></textarea><br/>
    错误码：<span id="error_details_04"></span>
    
    <br/><br/><br/><br/>
    <h3><a id="api05" href="#">revoke token</a></h3>
    返回的json:<br/>
    <textarea id="content05" rows="4" cols="100"></textarea><br/>
    错误码：<span id="error_details_05"></span>
        
    <br/><br/><br/><br/>
    <h3><a id="logout" href="http://localhost:7055/sso/oidc/logout?id_token_hint=${sessionScope.idToken}&post_logout_redirect_uri=http://localhost:7099/oidc/admin/test">logout</a></h3>
    <iframe id="hiddenFrame" width="1px" height="1px"></iframe>
</center>
<script>
// Base64-urlencodes the input string
function base64urlencode(str) {
    // Convert the ArrayBuffer to string using Uint8 array to conver to what btoa accepts.
    // btoa accepts chars only within ascii 0-255 and base64 encodes them.
    // Then convert the base64 encoded to base64url encoded
    //   (replace + with -, replace / with _, trim trailing =)
    return btoa(String.fromCharCode.apply(null, new Uint8Array(str)))
        .replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
}

function parseJwt(token) {
    var base64Url = token.split('.')[1];
    var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    var jsonPayload = decodeURIComponent(window.atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));

    return JSON.parse(jsonPayload);
}

function openTab(tabId) {
    var i, tabcontent, tablinks;
    tabcontent = document.getElementsByClassName("tabcontent");
    for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
    }
    tablinks = document.getElementsByClassName("tablinks");
    for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
    }
    document.getElementById(tabId).style.display = "block";
    window.event.currentTarget.className += " active";
}

document.getElementById("api01").addEventListener("click", function(e){
    e.preventDefault();
    var xhr = new XMLHttpRequest();
    xhr.open("GET", "http://localhost:7099/oidc-resource/bars", true);
    //xhr.setRequestHeader("Authorization", "Bearer ${sessionScope.accessToken}");
    xhr.setRequestHeader("Authorization", "Bearer " + document.getElementById("accessToken").value);
    xhr.onload = function() {
         if(xhr.status == 200) {
            document.getElementById("content01").innerText = xhr.responseText;
            document.getElementById("error_details_01").innerText = "";
        } else {
            document.getElementById("content01").innerText = "";
            document.getElementById("error_details_01").innerText = xhr.status;
        }
    }
    xhr.send();
});

document.getElementById("api02").addEventListener("click", function(e){
    e.preventDefault();
    var jwtId = document.getElementById("jwtId").innerText;
    var params = new URLSearchParams("grant_type=" + encodeURIComponent("urn:ietf:params:oauth:grant-type:token-exchange"));
    params.append("subject_token", encodeURIComponent(jwtId));
    params.append("resource", encodeURIComponent("http://localhost:7099/oidc-resource/bars"));
    params.append("subject_token_type", encodeURIComponent("urn:ietf:params:oauth:token-type:access_token"));
    params.append("requested_token_type", encodeURIComponent("urn:ietf:params:oauth:token-type:jwt"));
    params.append("scope", encodeURIComponent("openid y9"));
    params.append("positionId", "3333-333333-333333333");
    
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "http://localhost:7055/sso/oidc/token?" + params.toString(), true);
    xhr.setRequestHeader("Authorization", "Basic " + btoa("clientid_oidc:secret_oidc"));
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    //xhr.setRequestHeader("Accept", "application/json");
    
    xhr.onload = function() {
        var body = {};
        try {
            body = JSON.parse(xhr.responseText);
        } catch(e) {
            alert(e);
        }

        if(xhr.status == 200) {
            document.getElementById("content02").value = xhr.responseText;
            document.getElementById("accessToken").value = body["access_token"];
            var payload = parseJwt(body["access_token"]);
            document.getElementById("jwtId").innerText = payload.jti;
            //alert(payload.jti);
        } else {
            document.getElementById("error_details_02").innerText = xhr.status;
        }
    }
    xhr.send();
});

document.getElementById("api03").addEventListener("click", function(e){
    e.preventDefault();
    var params = new URLSearchParams("grant_type=password");
    params.append("client_id", encodeURIComponent("clientid_oidc"));
    params.append("client_secret", encodeURIComponent("secret_oidc"));
    params.append("loginType", "loginName");
    params.append("noLoginScreen", "true");
    params.append("tenantShortName", "default");
    params.append("deptId", "11111111-1111-1111-1111-111111111115");
    params.append("positionId", "222222222-2222222");
    params.append("systemName", "mytestapp");
    params.append("username", "lpLuYAHFdiwxF/LVarFRzLrmmtFqspD16Ih84KYUXK41wrdAhBPoTz1ip6diJH8zagNpUQ8GgbXd9vsL+RTR2BNUqVPM9ioTKLGJSeKS0i4Osfd2Eu7IxmpNvkfIAuqRV7YRDaLdVAQvn1VWBQFLQpQi/sIxpoGiaOV+x3pg2X8=");
    params.append("password", "q2Rein0vb8IYSB7F7cWAs392AMWfBfz8giv9nEV8dDeUu2ASRcCT7/cJBZ5o3HxgIMoc9scCdQpMytpl4BInM4EkuIMmQ4mi/QOZFDl+E4UUtdU6SxSAai3vyzLEwLODhzVIp92Ao9oatAhTKv5wvyVhAf42vjtd01bNLigOnpY=");
    params.append("screenDimension", "1024x768");
    params.append("scope", "openid y9");
    
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "http://localhost:7055/sso/oidc/token?" + params.toString(), true);
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');    
    xhr.onload = function() {
        var body = {};
        try {
            body = JSON.parse(xhr.responseText);
        } catch(e) {
            alert(e);
        }

        if(xhr.status == 200) {
            document.getElementById("content03").value = xhr.responseText;
            document.getElementById("accessToken").value = body["access_token"];
            var payload = parseJwt(body["access_token"]);
            document.getElementById("jwtId").innerText = payload.jti;
            document.getElementById("userName").innerText = payload.sub;
        } else {
            document.getElementById("error_details_03").innerText = xhr.status;
        }
    }
    xhr.send();
});

function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

document.getElementById("api04").addEventListener("click", async () => {
    try {
        var params = new URLSearchParams("");
            params.append("id_token_hint", "${sessionScope.idToken}");
            params.append("post_logout_redirect_uri", "http://localhost:7099/oidc/admin/test");
            //alert(params.toString());
        const res = await fetch("http://localhost:7055/sso/oidc/logout?" + params.toString(), {
            method: "GET",
            redirect: "manual"
        }).then(res => {
            if (res.type === 'opaqueredirect') {
               //window.location.href = res.url;
               document.getElementById("hiddenFrame").src = res.url;
               //等待3秒,backchanel logout finished
               sleep(3000).then(() => {
                   //window.location.reload(true);
               });
               return;
            } else {
               return res.text();
            }
        })
        .catch(error => {
            alert(error);
        });
    } catch (error) {
        alert('There has been a problem with your fetch operation:'+error);
    }
});

document.getElementById("api05").addEventListener("click", function(e){
    e.preventDefault();
    var params = new URLSearchParams("");
    params.append("client_id", encodeURIComponent("clientid_oidc"));
    params.append("client_secret", encodeURIComponent("secret_oidc"));
    var jwtId = document.getElementById("jwtId").innerText;
    //var jwtId = document.getElementById("accessToken").innerText;
    params.append("token", encodeURIComponent(jwtId));
    
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "http://localhost:7055/sso/oidc/revoke?" + params.toString(), true);
    xhr.onload = function() {
         if(xhr.status == 200) {
            document.getElementById("content05").innerText = xhr.responseText;
            document.getElementById("error_details_05").innerText = "";
        } else {
            document.getElementById("content05").innerText = "";
            document.getElementById("error_details_05").innerText = xhr.status;
        }
    }
    xhr.send();
});

// Open the first tab by default
document.addEventListener("DOMContentLoaded", function() {
    //openTab('tab1');
    document.getElementById("btn1").click();
});

</script>
</body>
</html>