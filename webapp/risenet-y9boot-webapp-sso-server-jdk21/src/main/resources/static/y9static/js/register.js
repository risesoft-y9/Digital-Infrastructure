/*  
 * 判断可信任站点(可信任站点可以为IP地址也可以为域名)
 * 需要启用IE:对标记为可安全执行脚本的ActiveX控件执行脚本，对未标记为可安全执行脚本的ActiveX控件;js所在站点需要加入信任站点
 * 自动添加信任站点可解决的问题：IE站点丢失，丢失Range或http、https值，丢失域名。
 * 信任站点优先添加至Range空缺位置。如Range0，Range2，则新加站点优先添加至Range1，之后是Rang3。。。
 */
var numNotInStr = [];//记录没有的数字编号,防止用户删除站点导致Range数字不连续
//var WshShell = new ActiveXObject("WScript.Shell");

function register() {
	var userAgent = navigator.userAgent.toLowerCase();
	if (userAgent.indexOf("msie 8") > 0 || userAgent.indexOf("msie 9") > 0 || userAgent.indexOf("msie 10") > 0 || userAgent.indexOf("rv:11") > 0) {
		var hostname = window.location.hostname;
		var WshShell = new ActiveXObject("WScript.Shell");
		//普通站点
		var httpnames = [];
		httpnames[0] = "http://10.169.1.13";
		httpnames[1] = "http://10.169.2.3";
		httpnames[2] = "http://10.169.2.8";
		httpnames[3] = "http://10.169.2.121";
		httpnames[4] = "http://10.169.2.122";
		httpnames[5] = "http://10.169.2.123";
		httpnames[6] = "http://10.169.2.131";
		httpnames[7] = "http://10.169.2.132";
		httpnames[8] = "http://10.169.2.134";
		httpnames[9] = "http://10.169.2.135";
		httpnames[10] = "http://10.169.2.136";
		httpnames[11] = "http://10.169.2.137";
		httpnames[12] = "http://10.169.2.245";
		httpnames[13] = "http://10.169.3.3";
		httpnames[14] = "http://10.169.3.30";
		httpnames[15] = "http://10.169.7.1";
		httpnames[16] = "http://10.169.7.20";
		httpnames[17] = "http://10.169.10.11";
		httpnames[18] = "http://10.169.11.30";
		httpnames[19] = "http://10.169.14.1";
		httpnames[20] = "http://10.169.17.1";
		httpnames[21] = "http://10.161.61.90";
		httpnames[22] = "http://10.10.100.10";
		httpnames[23] = "http://10.169.2.22";
		httpnames[24] = "http://10.169.17.146";
		httpnames[25] = "http://10.169.2.201";
		httpnames[26] = "http://10.169.17.37";
		
		//https站点
		var httpsnames=[];
		//httpsnames[0] = "https://10.169.1.13";
		//httpsnames[1] = "https://10.169.2.22";
		
		//域名站点
		var domains = [];
		domains[0] = "http://yun.szlh.gov.cn";
		domains[1] = "https://yun.szlh.gov.cn";
		domains[2] = "http://testyun.szlh.gov.cn";
		domains[3] = "https://testyun.szlh.gov.cn";
		
		//注册域名
		registerDomain(domains,WshShell);
		//获取注册表https开头的Range
		var regeditHostNameHttps = getRegeditHttps(WshShell);
		//获取注册表http开头的Range
		var regeditHostNameHttp = getRegeditHttp(WshShell);
		var allHostname = regeditHostNameHttp.concat(regeditHostNameHttps);
		//获取未注册的http开头的HostName
		var hostnotin = getHostNameNotRegister(httpnames,regeditHostNameHttp,WshShell);
		//注册http开头的HostName
		registerHostName(hostnotin,allHostname,WshShell);
		
		regeditHostNameHttp = getRegeditHttp(WshShell);
		regeditHostNameHttps = getRegeditHttps(WshShell);
		allHostname = regeditHostNameHttp.concat(regeditHostNameHttps);
		//获取未注册的https开头的HostName
		hostnotin = getHostNameNotRegister(httpsnames,regeditHostNameHttps,WshShell);
		//注册http开头的HostName
		registerHostName(hostnotin,allHostname,WshShell);
		//判断是否安装金山毒霸
		isInstallKingsoft(WshShell);
	}
}

//第一步：查询HKEY_CURRENT_USER\SOFTWARE\Microsoft\Windows\CurrentVersion\Internet Settings\ZoneMap\Domains
//注册表里面不存在，则添加域名至信任站点
function registerDomain(domains,WshShell){
	var temphostname;
	for (var i = 0;i < domains.length;i++)
	{
		temphostname = domains[i].split("://");
		var pos = temphostname[1].indexOf(".");
		if (-1 != pos)
		{
			try{
				WshShell.RegWrite("HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\ZoneMap\\Domains\\" + temphostname[1].substr(pos+1,temphostname[1].length-pos-1) + "\\" + temphostname[1].substr(0,pos)+"\\"+temphostname[0], 2, "REG_DWORD");
			}
			catch(e){
				
			}
		}
	}
}

//第二步：获取zones里面的信任站点
//获取HKEY_CURRENT_USER\SOFTWARE\Microsoft\Windows\CurrentVersion\Internet Settings\ZoneMap\Ranges里面的信任站点。
//用户手动删除的信任站点只，可能只会删除http键值,添加的时候只需要加上http即可。
function getRegeditHttp(WshShell){
	var str = [];
	numNotInStr = [];//记录没有的数字编号,防止用户删除站点导致Range数字不连续
	for (var i = 0; i < 50; i++) {
		//http
		try {
			var http = WshShell.RegRead("HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\ZoneMap\\Ranges\\Range" + i + "\\http");
			str[str.length] = "http://" + WshShell.RegRead("HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\ZoneMap\\Ranges\\Range" + i + "\\:Range");
		} catch (e) {
			try {
				str[str.length] = WshShell.RegRead("HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\ZoneMap\\Ranges\\Range" + i + "\\:Range");
			}
			catch(ex) {
				numNotInStr[numNotInStr.length] = i;
			}
		}
	}
	return str;
}


function getRegeditHttps(WshShell){
	var str = [];
	numNotInStr = [];//记录没有的数字编号,防止用户删除站点导致Range数字不连续
	for (var i = 0; i < 50; i++) {
		//http
		try {
			var https = WshShell.RegRead("HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\ZoneMap\\Ranges\\Range" + i + "\\https");
			str[str.length] = "https://" + WshShell.RegRead("HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\ZoneMap\\Ranges\\Range" + i + "\\:Range");
		} catch (e) {
			numNotInStr[numNotInStr.length] = i;
		}
	}
	return str;
}


//第二步：获取zones里面的信任站点
//获取HKEY_CURRENT_USER\SOFTWARE\Microsoft\Windows\CurrentVersion\Internet Settings\ZoneMap\Ranges里面的信任站点。
//用户手动删除的信任站点只，可能只会删除http键值,添加的时候只需要加上http即可。
function getHostNameNotRegister(hostnames,str,WshShell){
	var isExist = false;
	var hostnotin = [];
	var temphostname = [];
	var reg = /[a-zA-Z]+/g
	for (var j = 0; j < hostnames.length; j++) {
		hostname = hostnames[j];
		if (undefined == hostname){
			continue;
		}
		for (var i = 0; i < str.length; i++) {
			if (undefined == str[i]) {
				continue;
			} else {
				temphostname = hostname.split("://");
				tempdomain = str[i].replace("http://","").replace("https://","");
				if (str[i] == hostname) {
					//var tipInfo = "信任站点：" + hostname + " 已添加！\n";
					isExist = true;
					break;
				}else if(null != reg.exec(tempdomain)) {
					//对于域名被错误地加入到zones里面的，则删掉。
					try{
						var wrongdomain = str[i].split("://");
						WshShell.RegDelete("HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\ZoneMap\\Ranges\\Range" + i + "\\"+wrongdomain[0]);
						WshShell.RegDelete("HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\ZoneMap\\Ranges\\Range" + i + "\\"+wrongdomain[1]);
						WshShell.RegDelete("HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\ZoneMap\\Ranges\\Range" + i + "\\:Range");
						WshShell.RegDelete("HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\ZoneMap\\Ranges\\Range" + i + "");
						delete str[i];
					}
					catch(e){
						
					}
				}							
				//用IE手动删掉的，有时只会删除http键值,只需要加上http或https协议即可
				else if(str[i] == temphostname[1])
				{
					WshShell.RegWrite("HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\ZoneMap\\Ranges\\Range" + i + "\\" + temphostname[0], 2, "REG_DWORD");
					isExist = true;
					break;
				}
				else
				{
					isExist = false;
				}
			}
		}

		if (!isExist) {
			hostnotin[hostnotin.length] = hostname;
		}
	}
	return 	hostnotin;
}

//第三步：添加第二步中获得的未添加的信任站点至注册表
function registerHostName(hostnotin,allstr,WshShell){
	//防止用户直接删除注册表Ranges项
	WshShell.RegWrite("HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\ZoneMap\\Ranges\\", "");
	for (var i = 0; i < hostnotin.length; i++) {
		//alert("信任站点：" + hostnotin[i] + " 未添加！\n");
		var flag = 0;//http，https其中一个是否被加入
		var sernum = numNotInStr[i];//Range号
		var ip_domain = hostnotin[i].split("://");
		for ( var j = 0;j<allstr.length;j++)
		{
			//http，https其中一个被加入，例如：http://8.8.8.8已加入信任站点；https://8.8.8.8未加入，加入时需要放到http://8.8.8.8所在的Range
			if (allstr[j].split("://")[1]==ip_domain[1])
			{
				WshShell.RegWrite("HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\ZoneMap\\Ranges\\Range" + j + "\\" + ip_domain[0], 2, "REG_DWORD");
				flag = 1;
				break;
			}			
		}
		if (!flag)
		{
			try	{
				WshShell.RegWrite("HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\ZoneMap\\Ranges\\Range" + sernum + "\\", "");
				WshShell.RegWrite("HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\ZoneMap\\Ranges\\Range" + sernum + "\\:Range", ip_domain[1], "REG_SZ");
				WshShell.RegWrite("HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\ZoneMap\\Ranges\\Range" + sernum + "\\" + ip_domain[0], 2, "REG_DWORD");
				//continue;
			}catch(e)
			{
				continue;
			}
		}
	}
}
function isInstallKingsoft(WshShell){
	var version = "";
	try {
		version = WshShell.RegRead("HKLM\\SOFTWARE\\WOW6432Node\\kingsoft\\antivirus\\InstallVersion");
	} catch (e) {
	}
	if(version==""){
		//alert("未安装");
	}else{
		//alert("安装,版本："+version);
	}
}
