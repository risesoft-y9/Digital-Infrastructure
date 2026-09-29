var helper = (function () {
    var Y9Utils = function () {

    }
    /**
     * 识别IE浏览器，在vue3中，其实不必要的一个脚本（但是罗湖项目在vue2中是用到）
     */
    Y9Utils.prototype.IEVersion = function () {
        var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串  
        var isIE = userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1; //判断是否IE<11浏览器  
        var isEdge = userAgent.indexOf("Edge") > -1 && !isIE; //判断是否IE的Edge浏览器  
        var isIE11 = userAgent.indexOf('Trident') > -1 && userAgent.indexOf("rv:11.0") > -1;
        if (isIE) {
            var reIE = new RegExp("MSIE (\\d+\\.\\d+);");
            reIE.test(userAgent);
            var fIEVersion = parseFloat(RegExp["$1"]);
            if (fIEVersion == 7) {
                return 7;
            } else if (fIEVersion == 8) {
                return 8;
            } else if (fIEVersion == 9) {
                return 9;
            } else if (fIEVersion == 10) {
                return 10;
            } else {
                return 6;//IE版本<=7
            }
        } else if (isEdge) {
            return 'edge';//edge
        } else if (isIE11) {
            return 11; //IE11  
        } else {
            return -1;//不是ie浏览器
        }
    }

    /**
     * 生成随机字符串
     */
    Y9Utils.prototype.generateRandomString = function () {
        var array = new Uint32Array(28);
        window.crypto.getRandomValues(array);
        return Array.from(array, (dec) => ("0" + dec.toString(16)).substring(-2)).join("");
    }

    /**
     * 生成 uuid
     */
    Y9Utils.prototype.uuid = () => {
        let uuid = "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(
            /[xy]/g,
            function (c) {
                var r = (Math.random() * 16) | 0,
                    v = c == "x" ? r : (r & 0x3) | 0x8;
                return v.toString(16);
            }
        );
        return uuid;
    }
    /**
     * 获取url中的参数
     */
    Y9Utils.prototype.parseQueryString = function (string) {
        if (string == "") {
            return false;
        }
        var segments = string.split("&").map(s => s.split("="));
        var queryString = {};
        segments.forEach(s => queryString[s[0]] = s[1]);
        return queryString;
    }


    return new Y9Utils();
})();

export default helper;