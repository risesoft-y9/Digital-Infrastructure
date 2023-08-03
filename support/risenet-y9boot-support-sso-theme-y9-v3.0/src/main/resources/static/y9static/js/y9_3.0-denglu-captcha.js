function validateIE() {
    try {
        $("#screenDimension").val(window.screen.width + '*' + window.screen.height);
        $("#screenDimension2").val(window.screen.width + '*' + window.screen.height);
    } catch (e) {
    }
    var userAgent = navigator.userAgent.toLowerCase();
    if (userAgent.indexOf("msie 9") > 0 || userAgent.indexOf("msie 10") > 0 || userAgent.indexOf("rv:11") > 0) {
        $("#isValidateIE").val("true");
    } else if (userAgent.indexOf("msie 6") > 0 || userAgent.indexOf("msie 7") > 0 || userAgent.indexOf("msie 8") > 0) {
        $("#isValidateIE").val("false");
    } else {
        $("#isValidateIE").val("true");
    }
}

// 根据用户名获取相应租户列表
function getTenant() {
    var userName = $('#username').val();
    if ($.trim(userName).length > 0 && userName.indexOf("@") != -1) {
        var tenantName = userName.split("@");
        userName = tenantName[0].split("&");
        $("#username1").val(tenantName[0]);
        $.ajax({
            type: 'POST',
            dataType: 'json',
            data: {
                loginName: userName[0],
                tenantName: tenantName[1]
            },
            url: ctx + '/api/getTenants',
            success: function (res) {
                var slt = $('#tenantShortName');
                slt.html('');
                if (res.length <= 0) {
                    alert("当前用户不存在或者已禁用!");
                    return;
                }
                for (var i in res) {
                    slt.append('<option value="' + res[i].tenantShortName + '">' + res[i].tenantName + '</option>');
                }
            }
        });
    }
}

// 系统登录校验
function submitLoginForm() {
    var tenantShortName = $("#tenantShortName").val();
    var username = $("#username1").val();
    var password = $("#password").val();
    var encodeUserName = encode64(username);
    var encodePassword = encode64(password);
    if (username.length == 0) {
        alert("用户名不能为空!");
        $("#username").focus();
        return;
    } else if (password.length == 0) {
        alert("密码不能为空!");
        $("#password").focus();
        return;
    } else if (null == tenantShortName || tenantShortName.length == 0) {
        alert("当前用户不存在或已禁用!");
        $("#username").focus();
        return;
    } else {
        if ($("#jzyhm").prop("checked")) {
            var username = $("#username1").val();
            var password = $("#password").val();
            var tenantShortName = $("#tenantShortName").val();
            $.cookie("username", null);
            $.cookie("username", username, {
                expires: 365
            });
            $.cookie("tenantShortName", null);
            $.cookie("tenantShortName", tenantShortName, {
                expires: 365
            });
            $.cookie("password", null);
            $.cookie("password", password, {
                expires: 365
            });
        } else {
            $.cookie("username", null, {
                expires: 365
            });
            $.cookie("tenantShortName", null, {
                expires: 365
            });
            $.cookie("password", null, {
                expires: 365
            });
        }
        $.cookie("current", null);


        $("#username1").val(encodeUserName);
        $("#password1").val(encodePassword);
        $("#fm1").submit();
    }
}