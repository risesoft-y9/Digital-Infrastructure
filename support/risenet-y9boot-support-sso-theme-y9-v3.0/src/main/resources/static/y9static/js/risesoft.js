//三个input的焦点动画效果
$(function () {
    $("#username").focus(function () {
        $(this).attr("placeholder", "");
        $(".username").css("border", "#FFB205 1px solid");
    });
    $("#username").blur(function () {
        $(this).attr("placeholder", "请输入您的用户名");
        $(".username").css("border", "#FFFFFF 1px solid");
    });
    $("#captcha").focus(function () {
        $(this).attr("placeholder", "");
        $(".valid").css("border", "#FFB205 1px solid");
    });
    $("#captcha").blur(function () {
        $(this).attr("placeholder", "验证码");
        $(".valid").css("border", "#FFFFFF 1px solid");
    });
    $("#password").focus(function () {
        $(this).attr("placeholder", "");
        $(".password").css("border", "#FFB205 1px solid");
    });
    $("#password").blur(function () {
        $(this).attr("placeholder", "请输入您的密码");
        $(".password").css("border", "#FFFFFF 1px solid");
    });

    // 忘记密码&&登录的手势
    $("#remember").hover(function () {
        $(this).css("cursor", "pointer");
    });
    $(".login").hover(function () {
        $(this).css("cursor", "pointer");
    });

    // 转换二维码和密码登录
    $(".doubleCode").click(function () {
        $("#switch").css("display", "none");
        $(".passTrans").toggle(1000);
        $(".doubleCode").toggle(1000);
        $("#switch1").fadeIn(1800);

    });

    $(".passTrans").click(function () {
        $("#switch1").css("display", "none");
        $(".passTrans").toggle(1000);
        $(".doubleCode").toggle(1000);
        $("#switch").fadeIn(1800);
    });

    // 显示提醒
    $(".passTrans").hover(function () {
        $(".mention1").show(500);
    }, function () {
        $(".mention1").hide(0);
    })

    $(".doubleCode").hover(function () {
        $(".mention").show(500);
    }, function () {
        $(".mention").hide(0);
    })

    $(".userLogin").click(function () {
        $.cookie("current", "用户登录");
        $(".refresh").html("用户登录");
    });
    $(".tenement").click(function () {
        $.cookie("current", "租户登录");
        $(".refresh").html("租户登录");
    });
    $(".operation").click(function () {
        $.cookie("current", "运维登录");
        $(".refresh").html("运维登录");
    });
    $(".developers").click(function () {
        $.cookie("current", "开发商登录");
        $(".refresh").html("开发商登录");
    });
});

// 获取MAP中元素属性
function adjustTreeCoords() {
    var map = document.getElementById("treeMap");
    var element = $("#treeMap").children();
    var itemNumber = element.length;
    // console.debug('areaNumber:' + itemNumber);
    for (var i = 0; i < itemNumber; i++) {
        var oldCoords = element[i].coords;
        var newcoords = adjustPosition(oldCoords);
        element[i].setAttribute("coords", newcoords);
    }
    // var test = element;
}

// 调整MAP中坐标
function adjustPosition(position) {
    var img = document.getElementById("treeImg");
    console.debug(img.offsetWidth + ' ' + img.offsetHeight);
    var pageWidth = img.offsetWidth;// 图片显示宽度
    var pageHeith = img.offsetHeight;// 图片显示高度

    var imageWidth = 835;// 图片的实际长度
    var imageHeigth = 727;// 图片的实际高度
    // console.debug('oldPosition:' + position);
    var each = position.split(",");
    // 获取每个坐标点
    for (var i = 0; i < 4; i++) {
        if (i % 2 == 0) {
            each[i] = Math.round(parseInt(each[i]) * pageWidth / imageWidth).toString();// x坐标
        } else {
            each[i] = Math.round(parseInt(each[i]) * pageHeith / imageHeigth).toString();// y坐标
        }
    }
    // 生成新的坐标点
    var newPosition = "";
    for (var i = 0; i < 4; i++) {
        newPosition += each[i];
        if (i != 3) {
            newPosition += ",";
        }
    }
    // console.debug('newPosition:' + newPosition);
    return newPosition;
}

function getForwardSystem() {
    var serviceUrl = window.location.search;
    var systemList = ['admin-tenant', 'y9home', 'risecloud', 'portal'];
    var systemCNList = ['内网配置', '有生集团-内网协同', '有生集团-外网维护', '内网协同'];
    var systemCN = '';
    for (var i = 0; i < systemList.length; i++) {
        var currentSystem = systemList[i];
        if (serviceUrl.indexOf(currentSystem) != -1) {
            systemCN = systemCNList[i];
            break;
        }
    }
    $('#forwardSystem').text(systemCN);
    // alert(systemCN);
}