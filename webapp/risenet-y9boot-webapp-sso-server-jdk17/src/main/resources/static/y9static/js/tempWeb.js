var isOAWebhref = "", isInWeb = false, isWebhref = '';
//初始化函数
function Load() {
    f_InOrOutWeb();
}
/*判断是内外网*/
function f_InOrOutWeb() {
    var urlstr = window.location.href;
    if (urlstr.indexOf("itc.crc.cr") > -1) {
        isOAWebhref = "http://itc.crc.cr";//OA内网
        isWebhref = "http://critc.crc.cr/"; //内网
        isInWeb = true;
    } else {
        isOAWebhref = "http://www.critc.cn";//OA外网
        isWebhref = "http://itc.critc.cn/";//外网
        isInWeb = false;
    }
}
function softDowLoad(i) {
    if (i == 0) {
        if (isInWeb == false) {
            alert("软件下载仅限内网使用，请登录内网机访问。");
            return false;
        } else { window.open("http://10.1.4.123/Soft/Index.asp") }
    } else if (i == 1) {
        if (isInWeb == false)
        { alert("病毒防范仅限内网使用，请登录内网机访问。"); return false; }
        else { window.open("http://10.1.3.23/virus/index.asp"); }
    }
}
/*头部菜单内容*/
function getTopMen(iweb) {
    var limenu = "", emailstr = '', logo = '', gaikuang = "style='display:none;'";
    if (isInWeb == false)//外网
    { emailstr = '<li><a href="http://qiye.163.com/login/" title="邮箱(@critc.cn)" target="_blank">邮箱(@critc.cn)</a></li><li><a href="http://mail.sinorail.com/" title="邮箱(@sinorail.com)" target="_blank">邮箱(@sinorail.com)</a></li>' }
    else { emailstr = '<li><a href="http://10.1.4.123/dd.htm">公司邮箱</a></li><li><a href="http://mail.crc.cr/indishare/office.nsf/(frame)/mail">铁总邮箱</a></li>'; };
    switch (iweb) {
        case 0:
            logo = "/sso/y9static/images/zhongtie/logo/logo.png";//信息公司
            gaikuang = "";
            break;
        case 1: logo = "/sso/y9static/images/zhongtie/logo/logo_jsfw.png;" + '\"style="width:580px;"\"'//技术服务
            break;
        case 2: logo = "/sso/y9static/images/zhongtie/logo/logo_dsjgstj.png";//大数据
            break;
        case 3: logo = "/sso/y9static/images/zhongtie/logo/logo_ztx.png";//中铁信
            break;
        case 4: logo = "/sso/y9static/images/zhongtie/logo/logo_jsjgs.png";//计算机
            break;
        case 5: logo = "/sso/y9static/images/zhongtie/logo/logo_sys.png";//城市
            break;
        case 6: logo = "/sso/y9static/images/zhongtie/logo/logo_hxsj.png";//弘信
            break;
        case 7: logo = "/sso/y9static/images/zhongtie/logo/logo_hyrj.png";//弘远
            break;
    }
    var str = '<div class="web_centerContainer">' +
    '<div class="web_logo">' +
        '<img alt="" src="' + logo + '">' +
    '</div>' +
    '<div class="web_right_02">' +
        '<div style="left: -25px; height: 58px; position: relative;">' +
            '<div class="web_right_02_top" style="float: right;">' +
                '<span class="icon2" id="date">2018/09/05 星期三</span>' +
            '</div>' +
            '<div class="web_right_02_bottom" style="padding:10px 90px 0px 0px; float: left;">请勿登载和传播涉密信息</div></div>' +//margin-left:180px;
   '<div class="index-nav" style="padding-right:77px;">' +
      '<ul class="menu">' +
           '<li><span><a href="#">首页</a></span></li>' +
           '<li ' + gaikuang + '>' +
               '<a href="#">公司概况</a>' +
               '<ul>' +
                   '<li><a href="jianjie.html?gk=0">公司简介</a></li>' +
                   '<li><a href="jianjie.html?gk=1">公司领导</a></li>' +
                   '<li><a href="jianjie.html?gk=2">组织机构</a></li>' + limenu +
               '</ul>' +
           '</li>' +
           '<li>' +
               '<a href="#">在线支持</a>' +
               '<ul>' +
                   '<li><a onclick="javascript:softDowLoad(0); " href="javascript:void(0);" >软件下载</a></li>' +
                   '<li><a href="ziliao.html" target="_blank">资料交换</a></li>' +
                   '<li><a onclick="javascript:softDowLoad(1); " href="javascript:void(0);" >病毒防范</a></li>' +
               '</ul>' +
           '</li>' +
           '<li><a href="' + isWebhref + 'indishare/addressbook.nsf/frmaddress_new?openform" target="view_window">电话号码</a></li>' +
           '<li>' +
               '<a href="#">电子邮箱</a>' +
               '<ul>' + emailstr + '</ul>' +
           '</li>' +
       '</ul>' +
   '</div>' +
'</div>' +
'</div>';
    return str;
}
/*其他家公司*/
function OtherCompany() {
    var flist = [{ title: "大数据公司（天津）", href: "/y9home/index5", isWeb: false, clas: "icon1" },
                { title: "中铁信集团", href: "/y9home/index1", isWeb: true, clas: "icon2" },
                { title: "计算机公司", href: "/y9home/index7", isWeb: true, clas: "icon3" },
                { title: "城市轨道交通<br />国家实验室", href: "/y9home/index4", isWeb: true, clas: "icon3" },
                { title: "弘信设计公司", href: "/y9home/index6", isWeb: true, clas: "icon4" },
                { title: "弘远软件公司", href: "/y9home/index2", isWeb: true, clas: "icon4" },
                { title: "技术服务公司", href: "/y9home/index3", isWeb: true, clas: "icon4" }];
    var fstr = '', pstr = '';
    for (var i = 0; i < flist.length; i++) {
        fstr += ' <li><a class="linkButton"  href="' + isOAWebhref + flist[i].href + '" target="_blank"><span class="' + flist[i].clas + '" >' + flist[i].title + '</span></a></li>';
    }
    pstr = '<div class="web_centerContainer"><div class="web_rightGg"><div class="web_rightGg_list"><ul>' + fstr + '</ul></div></div> </div>';
    return pstr;
}
/*友情链接*/
function friendLists() {
    var flist = [{ title: "中国铁路总公司", href: "http://www.crc.cr/", isWeb: false },
                { title: "哈尔滨铁路局", href: "http://10.16.4.33/bgzdh/", isWeb: true },
                { title: "沈阳铁路局", href: "http://10.32.3.36/", isWeb: true },
                { title: "北京铁路局", href: "http://10.64.3.46/", isWeb: true },
                { title: "太原铁路局", href: "http://10.72.4.25/", isWeb: true },
                { title: "呼和铁路局", href: "http://10.94.4.7/", isWeb: true },
                { title: "郑州铁路局", href: "http://10.96.4.36/bgzdh/", isWeb: true },
                { title: "武汉铁路局", href: "http://10.102.4.69/", isWeb: true },
                { title: "西安铁路局", href: "http://10.106.4.235/bgzdh/", isWeb: true },
                { title: "济南铁路局", href: "http://10.112.3.36/bgzdh/", isWeb: true },
                { title: "上海铁路局", href: "http://10.128.4.75/lyj_lj/index/index.asp?unit_numb=1163&CurrDeptNumb=1163", isWeb: true },
                { title: "南昌铁路局", href: "http://10.158.3.36/", isWeb: true },
                { title: "广铁集团", href: "http://10.160.4.28/gtoa/web/welcome.do", isWeb: true },
                { title: "南宁铁路局", href: "http://10.190.4.200/nntlj/", isWeb: true },
                { title: "成都铁路局", href: "http://10.192.4.39/", isWeb: true },
                { title: "兰州铁路局", href: "http://10.208.4.36/", isWeb: true },
                { title: "乌鲁木齐局", href: "http://10.224.4.61/", isWeb: true },
                { title: "昆明铁路局", href: "http://10.206.4.40/bgzdh/", isWeb: true },
                { title: "青藏公司", href: "http://10.216.4.46/", isWeb: true },
                { title: "哈尔滨局信息所", href: "http://10.16.72.254/", isWeb: true },
                { title: "北京局信息处", href: "http://10.64.72.100/", isWeb: true },
                { title: "太原局信息处", href: "http://10.72.4.67/", isWeb: true },
                { title: "呼和局信息处", href: "http://10.94.4.8/xxc/", isWeb: true },
                { title: "西安局信息处", href: "http://10.106.4.235/xxc/", isWeb: true },
                { title: "济南局电子中心", href: "http://10.112.3.36/bgzdh/jdw/dizizhongxin/index.htm", isWeb: true },
                { title: "上海局信息所", href: "http://10.128.4.43/", isWeb: true },
                { title: "南昌局信息处", href: "http://10.158.3.40:8080/dzs/dzs.htm", isWeb: true },
                { title: "广铁集团信息处", href: "http://10.160.4.28/xxjsc/index.jsp", isWeb: true },
                { title: "成都局信息技术处", href: "http://10.192.72.99/", isWeb: true },
                { title: "兰州局信息处", href: "http://10.208.3.36/单位网页/信息处/indexnew.asp", isWeb: true },
                { title: "乌鲁木齐局信息处", href: "http://10.224.4.41/", isWeb: false },
                { title: "昆明局信息处", href: "http://10.206.4.40/DireUnit/XXGLC/dszxzhk/default.asp", isWeb: true }];
    var fstr = '', pstr = '';
    for (var i = 0; i < flist.length; i++) {
        if ((isInWeb == true && flist[i].isWeb == true) || (isInWeb == false && flist[i].isWeb == false)) { fstr += '<a href="' + flist[i].href + '" target="_blank" >' + flist[i].title + '</a>'; } else { fstr += '<a>' + flist[i].title + '</a>'; }
    }
    pstr = '<div class="web_centerContainer">' +
            '<div class="web_mainBlock_left_title" style="padding: 0"><span class="lis zh">友情链接</span><span class="lis"><img src="/sso/y9static/images/zhongtie/xline.png" alt="" /></span> <span class="lis en">Train</span></div>' +
            '<div class="web_friendLinkBlock_list">' + fstr + '</div>' +
            '</div>';
    return pstr;
}
/*底部*/
function getBottom() {
    return '<div class="web_centerContainer"> Copyright © 2018 技术服务公司、弘远软件公司 &nbsp&nbsp Powered by &nbsp&nbsp 有生博大 &nbsp&nbsp  Rights Reserved.  &nbsp&nbsp 使用IE11(32位)及以上浏览器 1920x1080分辨率浏览使用本站</div>';
}
/*界面初始化*/
function HTMLLoad(iweb) {
    /*加载头部内容*/
    if (document.getElementById("topContent") != null) { document.getElementById("topContent").innerHTML = getTopMen(iweb); }
    getOldSyttem();
    //if (iweb == 0) {getOldSyttem();}
    /*其他家公司*/
    if (document.getElementById("divOtherCompany")!=null){document.getElementById("divOtherCompany").innerHTML = OtherCompany();}
    /*友情链接*/
    if (document.getElementById("divFriendlist") != null) { document.getElementById("divFriendlist").innerHTML = friendLists(); }
    /*底部*/
    if (document.getElementById("divBottom") != null) { document.getElementById("divBottom").innerHTML = getBottom(); }
}
//信息公司的首页显示旧版本
function getOldSyttem() {
    document.getElementById("oldDiv").innerHTML = '<div class="web_centerContainer">' +
    '<div class="web_rightGg" style="height: 45px;line-height: 45px;">' +
    '<span ><a href="' + isOAWebhref + '" target="_blank" style="font-weight: bold;font-size: 20px;">信息公司（信息中心）</a> </span>' +
    '<span><a style="color: rgb(0, 126, 223); padding-left: 280px; display: block;" href="' + isWebhref + '"  target="_blank" >旧版-网站门户</a> </span>' +
    '<div class="oldSystem">' +
    '<a href="http://oa.critc.cn:8080/"  target="_blank">登录-既有协同办公平台</a>' +
    '</div>' +
    '</div>' +
    '</div>';
}

