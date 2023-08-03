/**************************************************************

 @Name : layer v1.6.0 弹层组件开发版
 @author: 贤心
 @date: 2013-08-30
 @blog: http://sentsin.com
 @微博：http://weibo.com/SentsinXu
 @QQ群：176047238(layUI交流群)
 @Copyright: Sentsin Xu(贤心)
 @官网说明：http://sentsin.com/jquery/layer
 @赞助layer: https://me.alipay.com/sentsin

 *************************************************************/

;!function (window, undefined) {

    var pathType = true, //是否采用自动获取绝对路径。false：将采用下述变量中的配置
        pathUrl = '../layer/', //当前js所在路径，上述变量为false才有效

        $, win, ready = {
            iE6: !-[1,] && !window.XMLHttpRequest,
            times: 0
        };

//获取当前路径
    ready.getPath = function () {
        var js = document.scripts || $('script'), jsPath = js[js.length - 1].src;
        return jsPath.substring(0, jsPath.lastIndexOf("/") + 1);
    };

//载入css
    ready.load = function () {
        pathType && (pathUrl = this.getPath());
        var head = $('head')[0], link = document.createElement("link");
        link.setAttribute('type', 'text/css');
        link.setAttribute('rel', 'stylesheet');
        link.setAttribute('href', pathUrl + 'skin/layer.css');
        head.appendChild(link);
    };


//默认内置方法。
    this.layer = {
        v: '1.6.0', //版本号
        ready: function (callback) {
            var load = '#layerCss';
            return $(load).ready(function () {
                callback();
            });
        }, alert: function (alertMsg, alertType, alertTit, alertYes) {	//普通对话框，类似系统默认的alert()
            return $.layer({
                dialog: {msg: alertMsg, type: alertType, yes: alertYes},
                title: alertTit,
                area: ['auto', 'auto']
            });
        }, confirm: function (conMsg, conYes, conTit, conNo) { //询问框，类似系统默认的confirm()
            return $.layer({
                dialog: {msg: conMsg, type: 4, btns: 2, yes: conYes, no: conNo},
                title: conTit
            });
        }, msg: function (msgText, msgTime, msgType, callback) { //普通消息框，一般用于行为成功后的提醒,默认两秒自动关闭
            (msgText == '' || msgText == undefined) && (msgText = '&nbsp;');
            msgTime === undefined && (msgTime = 2);
            return $.layer({
                dialog: {msg: msgText, type: msgType},
                time: msgTime,
                title: ['', false],
                closeBtn: ['', false], end: function () {
                    callback && callback()
                }
            });
        }, tips: function (html, follow, time, maxWidth, guide, style) {
            return $.layer({
                type: 4,
                shade: false,
                time: time,
                maxWidth: maxWidth,
                tips: {msg: html, guide: guide, follow: follow, style: style}
            });
        }, load: function (loadTime, loadgif, loadShade) {
            var border = true;
            loadgif === 3 && (border = false);
            return $.layer({
                time: loadTime,
                shade: loadShade,
                loading: {type: loadgif},
                border: [10, 0.3, '#000', border],
                type: 3,
                title: ['', false],
                closeBtn: [0, false]
            });
        }
    };

    var Class = function (setings) {
        ready.times++;
        this.index = ready.times;
        var config = this.config;
        this.config = $.extend({}, config, setings);
        this.config.dialog = $.extend({}, config.dialog, setings.dialog);
        this.config.page = $.extend({}, config.page, setings.page);
        this.config.iframe = $.extend({}, config.iframe, setings.iframe);
        this.config.loading = $.extend({}, config.loading, setings.loading);
        this.config.tips = $.extend({}, config.tips, setings.tips);
        this.creat();
    };

    Class.pt = Class.prototype;

//默认配置
    Class.pt.config = {
        type: 0,
        shade: [0.3, '#000', true],
        shadeClose: false,
        fix: true,
        move: ['.xubox_title', true],
        moveOut: false,
        title: ['信息', true],
        offset: ['200px', '50%'],
        area: ['310px', 'auto'],
        closeBtn: [0, true],
        time: 0,
        bgcolor: '#fff',
        border: [8, 0.3, '#000', true],
        zIndex: 19891014,
        maxWidth: 400,
        dialog: {
            btns: 1, btn: ['确定', '取消'], type: 3, msg: '', yes: function (index) {
                layer.close(index);
            }, no: function (index) {
                layer.close(index);
            }
        },
        page: {dom: '#xulayer', html: '', url: ''},
        iframe: {src: 'http://sentsin.com'},
        loading: {type: 0},
        tips: {
            msg: '',
            follow: '',
            guide: 0,
            isGuide: true,
            style: ['background-color:#FF9900; color:#fff;', '#FF9900']
        },
        success: function (layer) {
        }, //创建成功后的回调
        close: function (index) {
            layer.close(index);
        }, //右上角关闭回调
        end: function () {
        } //终极销毁回调
    };

    Class.pt.type = ['dialog', 'page', 'iframe', 'loading', 'tips'];

//容器
    Class.pt.space = function (html) {
        var html = html || '', times = this.index, config = this.config, dialog = config.dialog, dom = this.dom,
            ico = dialog.type === -1 ? '' : '<span class="xubox_msg xulayer_png32 xubox_msgico xubox_msgtype' + dialog.type + '"></span>',
            frame = [
                '<div class="xubox_dialog">' + ico + '<span class="xubox_msg xubox_text" style="' + (ico ? '' : 'padding-left:20px') + '">' + dialog.msg + '</span></div>',
                '<div class="xubox_page">' + html + '</div>',
                '<iframe allowtransparency="true" id="' + dom.ifr + '' + times + '" name="' + dom.ifr + '' + times + '" onload="$(this).removeClass(\'xubox_load\');" class="' + dom.ifr + '" frameborder="0" src="' + config.iframe.src + '"></iframe>',
                '<span class="xubox_loading xubox_loading_' + config.loading.type + '"></span>',
                '<div class="xubox_tips" style="' + config.tips.style[0] + '"><div class="xubox_tipsMsg">' + config.tips.msg + '</div><i class="layerTipsG"></i></div>'
            ],
            shade = '', border = '', zIndex = config.zIndex + times,
            shadeStyle = 'z-index:' + zIndex + '; background-color:' + config.shade[1] + '; opacity:' + config.shade[0] + '; filter:alpha(opacity=' + config.shade[0] * 100 + ');';

        config.shade[2] && (shade = '<div times="' + times + '" id="xubox_shade' + times + '" class="xubox_shade" style="' + shadeStyle + '"></div>');

        config.zIndex = zIndex;
        var title = '', closebtn = '',
            borderStyle = "z-index:" + (zIndex - 1) + ";  background-color: " + config.border[2] + "; opacity:" + config.border[1] + "; filter:alpha(opacity=" + config.border[1] * 100 + "); top:-" + config.border[0] + "px; left:-" + config.border[0] + "px;";

        config.border[3] && (border = '<div id="xubox_border' + times + '" class="xubox_border" style="' + borderStyle + '"></div>');
        config.closeBtn[1] && (closebtn = '<a class="xubox_close xulayer_png32 xubox_close' + config.closeBtn[0] + '" href="javascript:;"></a>');
        config.title[1] && (title = '<h2 class="xubox_title"><em>' + config.title[0] + '</em></h2>')

        var boxhtml = '<div times="' + times + '" showtime="' + config.time + '" style="z-index:' + zIndex + '" id="' + dom.lay + '' + times
            + '" class="' + dom.lay + '">'
            + '<div style="background-color:' + config.bgcolor + '; z-index:' + zIndex + '" class="xubox_main">'
            + frame[config.type]
            + title
            + closebtn
            + '<span class="xubox_botton"></span>'
            + '</div>' + border + '</div>';

        return [shade, boxhtml];
    };

//缓存字符
    Class.pt.dom = {
        lay: 'xubox_layer',
        ifr: 'xubox_iframe'
    };

//创建骨架
    Class.pt.creat = function () {
        var that = this, space = '', config = this.config, dialog = config.dialog, title = that.config.title,
            dom = that.dom;

        title.constructor === Array || (that.config.title = [title, true]);
        title === false && (that.config.title = [title, false]);

        var page = config.page, body = $("body"), setSpace = function (html) {
            var html = html || ''
            space = that.space(html);
            body.append(space[0]);
        };

        switch (config.type) {
            case 1:
                if (page.html !== '') {
                    setSpace('<div class="xuboxPageHtml">' + page.html + '</div>');
                    body.append(space[1]);
                } else if (page.url !== '') {
                    setSpace('<div class="xuboxPageHtml" id="xuboxPageHtml' + that.index + '">' + page.html + '</div>');
                    body.append(space[1]);
                    $.get(page.url, function (datas) {
                        $('#xuboxPageHtml' + that.index).html(datas);
                        page.ok && page.ok(datas);
                    });
                } else {
                    if ($(page.dom).parents('.xubox_page').length == 0) {
                        setSpace();
                        $(page.dom).show().wrap(space[1]);
                    } else {
                        return;
                    }
                }
                break;
            case 2:
                setSpace();
                body.append(space[1]);
                break;
            case 3:
                config.title = ['', false];
                config.area = ['auto', 'auto'];
                config.closeBtn = ['', false];
                $('.xubox_loading')[0] && layer.close($('.xubox_loading').parents('.' + dom.lay).attr('times'));
                setSpace();
                body.append(space[1]);
                break;
            case 4:
                config.title = ['', false];
                config.area = ['auto', 'auto'];
                config.fix = false;
                config.border = false;
                $('.xubox_tips')[0] && layer.close($('.xubox_tips').parents('.' + dom.lay).attr('times'));
                setSpace();
                body.append(space[1]);
                $('#' + dom.lay + ready.times).find('.xubox_close').css({top: 5, right: 5});
                break;
            default :
                config.title[1] || (config.area = ['auto', 'auto']);
                $('.xubox_dialog')[0] && layer.close($('.xubox_dialog').parents('.' + dom.lay).attr('times'));
                setSpace();
                body.append(space[1]);
                break;
        }
        ;

        var times = ready.times;
        this.layerS = $('#xubox_shade' + times);
        this.layerB = $('#xubox_border' + times);
        this.layerE = $('#' + dom.lay + times);

        var layerE = this.layerE;
        this.layerMian = layerE.find('.xubox_main');
        this.layerTitle = layerE.find('.xubox_title');
        this.layerText = layerE.find('.xubox_text');
        this.layerPage = layerE.find('.xubox_page');
        this.layerBtn = layerE.find('.xubox_botton');

        //设置layer面积坐标等数据 
        if (config.offset[1].indexOf("px") != -1) {
            var _left = parseInt(config.offset[1]);
        } else {
            if (config.offset[1] == '50%') {
                var _left = config.offset[1];
            } else {
                var _left = parseInt(config.offset[1]) / 100 * win.width();
            }
        }
        ;
        layerE.css({left: _left + config.border[0], width: config.area[0], height: config.area[1]});
        config.fix ? layerE.css({top: parseInt(config.offset[0]) + config.border[0]}) : layerE.css({
            top: parseInt(config.offset[0]) + win.scrollTop() + config.border[0],
            position: 'absolute'
        });


        //配置按钮 对话层形式专有
        if (config.type == 0 && config.title[1]) {
            switch (dialog.btns) {
                case 0:
                    that.layerBtn.html('').hide();
                    break;
                case 2:
                    that.layerBtn.html('<a href="javascript:;" class="xubox_yes xubox_botton2">' + dialog.btn[0] + '</a>' + '<a href="javascript:;" class="xubox_no xubox_botton3">' + dialog.btn[1] + '</a>');
                    break;
                default:
                    that.layerBtn.html('<a href="javascript:;" class="xubox_yes xubox_botton1">' + dialog.btn[0] + '</a>');
            }
        }
        ;

        if (layerE.css('left') === 'auto') {
            layerE.hide();
            setTimeout(function () {
                layerE.show();
                that.set(times);
            }, 500);
        } else {
            that.set(times);
        }
        config.time <= 0 || that.autoclose();
        this.callback();
    };

//初始化骨架
    Class.pt.set = function (times) {
        var that = this, layerE = this.layerE, config = this.config, dialog = config.dialog, page = config.page,
            dom = that.dom;
        that.autoArea(times);
        if (config.title[1]) {
            ready.iE6 && that.layerTitle.css({width: layerE.outerWidth()});
        } else {
            config.type != 4 && layerE.find('.xubox_close').addClass('xubox_close1');
        }
        ;

        layerE.attr({'type': that.type[config.type]});

        switch (config.type) {
            case 1:
                layerE.find(page.dom).addClass('layer_pageContent');
                config.shade[2] && layerE.css({zIndex: config.zIndex + 1});
                config.title[1] && that.layerPage.css({top: that.layerTitle.outerHeight()});
                break;
            case 2:
                var iframe = layerE.find('.' + dom.ifr), heg = layerE.height();
                iframe.addClass('xubox_load').css({width: layerE.width()});
                config.title[1] ? iframe.css({
                    top: that.layerTitle.height(),
                    height: heg - that.layerTitle.height()
                }) : iframe.css({top: 0, height: heg});
                ready.iE6 && iframe.attr('src', config.iframe.src);
                break;
            case 4 :
                var fow = $(config.tips.follow), ftop = fow.offset().top, top = ftop - layerE.outerHeight();
                var fleft = fow.offset().left, left = fleft, color = config.tips.style[1];
                var fHeight = layerE.outerHeight(), fWidth = layerE.outerWidth(), tipsG = layerE.find('.layerTipsG');
                fWidth > config.maxWidth && layerE.width(config.maxWidth);

                if (config.tips.guide === 1) {
                    var offleft = win.width() - left - fWidth - layerE.outerWidth() - 10, top = ftop;
                    if (offleft > 0) {
                        left = left + fow.outerWidth() + 10;
                        tipsG.removeClass('layerTipsL').addClass('layerTipsR').css({'border-right-color': color});
                    } else {
                        left = left - layerE.outerWidth() - 10
                        tipsG.removeClass('layerTipsR').addClass('layerTipsL').css({'border-left-color': color});
                    }
                } else {
                    if (top - win.scrollTop() - 12 <= 0) {
                        top = ftop + fHeight + 10;
                        tipsG.removeClass('layerTipsT').addClass('layerTipsB').css({'border-bottom-color': color});
                    } else {
                        top = top - 10;
                        tipsG.removeClass('layerTipsB').addClass('layerTipsT').css({'border-top-color': color});
                    }
                }
                config.tips.isGuide || tipsG.remove();
                layerE.css({top: top, left: left});
                break;
            default:
                this.layerMian.css({'background-color': '#fff'});
                if (config.title[1]) {
                    this.layerText.css({paddingTop: 18 + that.layerTitle.outerHeight()});
                } else {
                    layerE.find('.xubox_msgico').css({top: '10px'});
                    that.layerText.css({marginTop: 12});
                }
                break;
        }
        ;

        this.move();
    };

//自适应宽高
    Class.pt.autoArea = function (times) {
        var that = this, layerE = that.layerE, config = that.config, page = config.page,
            layerMian = that.layerMian, layerBtn = that.layerBtn, layerText = that.layerText,
            layerPage = that.layerPage, layerB = that.layerB, titHeight;
        if (config.area[0] === 'auto' && layerMian.outerWidth() >= config.maxWidth) {
            layerE.css({width: config.maxWidth});
        }
        config.title[1] ? titHeight = that.layerTitle.innerHeight() : titHeight = 0;
        switch (config.type) {
            case 0:
                var aBtn = layerBtn.find('a'),
                    outHeight = layerText.outerHeight() + 20;
                if (aBtn.length > 0) {
                    var btnHeight = aBtn.outerHeight() + 20;
                } else {
                    var btnHeight = 0;
                }
                break;
            case 1:
                var btnHeight = 0, outHeight = $(page.dom).outerHeight();
                config.area[0] === 'auto' && layerE.css({width: layerPage.outerWidth()});
                if (page.html !== '') {
                    outHeight = layerPage.outerHeight();
                }
                break;
            case 3:
                var load = $(".xubox_loading"), btnHeight = 0;
                var outHeight = load.outerHeight();
                layerMian.css({width: load.width()});
                break;
        }
        ;
        (config.area[1] === 'auto') && layerMian.css({height: titHeight + outHeight + btnHeight});
        layerB.css({
            width: layerE.outerWidth() + 2 * config.border[0],
            height: layerE.outerHeight() + 2 * config.border[0]
        });
        (ready.iE6 && config.area[0] != 'auto') && layerMian.css({width: layerE.outerWidth()});
        (config.offset[1] === '50%' || config.offset[1] == '') && (config.type !== 4) ? layerE.css({marginLeft: -layerE.outerWidth() / 2}) : layerE.css({marginLeft: 0});
    };

//拖拽层
    Class.pt.move = function () {
        var that = this, config = this.config, layerMove = that.layerE.find(config.move[0]), layerE, ismove,
            dom = that.dom;
        var moveX, moveY, move, setX = 0, setY = 0;
        config.move[1] && layerMove.attr('move', 'ok');
        config.move[1] ? that.layerE.find(config.move[0]).css({cursor: 'move'}) : that.layerE.find(config.move[0]).css({cursor: 'auto'});
        $(config.move[0]).on('mousedown', function (M) {
            M.preventDefault();
            if ($(this).attr('move') === 'ok') {
                ismove = true;
                layerE = $(this).parents('.' + dom.lay);
                var xx = layerE.offset().left, yy = layerE.offset().top, ww = layerE.width() - 6,
                    hh = layerE.height() - 6;
                if (!$('#xubox_moves')[0]) {
                    $('body').append('<div id="xubox_moves" class="xubox_moves" style="left:' + xx + 'px; top:' + yy + 'px; width:' + ww + 'px; height:' + hh + 'px; z-index:2147483584"></div>');
                }
                move = $('#xubox_moves');
                moveX = M.pageX - move.position().left;
                moveY = M.pageY - move.position().top;
                setX = win.scrollLeft();
                layerE.css('position') !== 'fixed' || (setY = win.scrollTop());
            }
        });
        $(document).mousemove(function (M) {
            if (ismove) {
                M.preventDefault();
                var offsetX = M.pageX - moveX;
                if (layerE.css('position') === 'fixed') {
                    var offsetY = M.pageY - moveY;
                } else {
                    var offsetY = M.pageY - moveY;
                }

                //控制元素不被拖出窗口外
                if (!config.moveOut) {
                    var setRig = win.width() - move.outerWidth() - config.border[0],
                        setY = win.scrollTop(), setTop = config.border[0] + setY;
                    offsetX < config.border[0] && (offsetX = config.border[0]);
                    offsetX > setRig && (offsetX = setRig);
                    offsetY < setTop && (offsetY = setTop);
                    offsetY > win.height() - move.outerHeight() - config.border[0] + setY && (offsetY = win.height() - move.outerHeight() - config.border[0] + setY)
                }

                move.css({left: offsetX, top: offsetY});
            }
        }).mouseup(function () {
            try {
                if (ismove) {
                    if (parseInt(layerE.css('margin-left')) == 0) {
                        var lefts = parseInt(move.css('left'));
                    } else {
                        var lefts = parseInt(move.css('left')) + (-parseInt(layerE.css('margin-left')))
                    }
                    layerE.css('position') === 'fixed' || (lefts = lefts - layerE.parent().offset().left);
                    layerE.css({left: lefts, top: parseInt(move.css('top')) - setY});
                    move.remove();
                }
                ismove = false;
            } catch (e) {
                ismove = false;
            }
            config.moveEnd && config.moveEnd();
        });
    };

//自动关闭layer
    Class.pt.autoclose = function () {
        var that = this, time = this.config.time;
        var maxLoad = function () {
            time--;
            if (time === 0) {
                layer.close(that.index);
                clearInterval(that.autotime);
            }
        };
        this.autotime = setInterval(maxLoad, 1000);
    };

    ready.config = {
        end: {}
    };

    Class.pt.callback = function () {
        this.openLayer();
        var that = this, layerE = this.layerE, config = this.config, dialog = config.dialog;
        this.config.success(layerE);
        ready.iE6 && this.IE6();
        layerE.find('.xubox_close').off('click').on('click', function (e) {
            e.preventDefault();
            config.close(that.index);
        });
        layerE.find('.xubox_yes').off('click').on('click', function (e) {
            e.preventDefault();
            dialog.yes(that.index);
        });
        layerE.find('.xubox_no').off('click').on('click', function (e) {
            e.preventDefault();
            dialog.no(that.index);
        });
        this.layerS.off('click').on('click', function (e) {
            e.preventDefault();
            that.config.shadeClose && layer.close(that.index);
        });
        ready.config.end[this.index] = config.end;
    };

    Class.pt.IE6 = function () {
        var that = this, layerE = this.layerE, select = $('select'), dom = that.dom;
        var _ieTop = layerE.offset().top;

        //ie6的固定与相对定位
        if (this.config.fix) {
            var ie6Fix = function () {
                layerE.css({top: $(document).scrollTop() + _ieTop});
            };
        } else {
            var ie6Fix = function () {
                layerE.css({top: _ieTop});
            };
        }
        ie6Fix();
        win.scroll(ie6Fix);

        //隐藏select
        $.each(select, function (index, value) {
            var sthis = $(this);
            if (!sthis.parents('.' + dom.lay)[0]) {
                sthis.css('display') == 'none' || sthis.attr({'layer': '1'}).hide();
            }
        });

        //恢复select
        this.reselect = function () {
            $.each(select, function (index, value) {
                var sthis = $(this);
                if (!sthis.parents('.' + dom.lay)[0]) {
                    (sthis.attr('layer') == 1 && $('.' + dom.lay).length < 1) && sthis.removeAttr('layer').show();
                }
            });
        };
    };

//给layer对象拓展方法
    Class.pt.openLayer = function () {
        var that = this, dom = that.dom;

        layer.index = ready.times;

        //自适应宽高
        layer.autoArea = function (index) {
            return that.autoArea(index);
        };

        //获取layer当前索引
        layer.getIndex = function (selector) {
            return $(selector).parents('.' + dom.lay).attr('times');
        };

        //获取子iframe的DOM
        layer.getChildFrame = function (selector, index) {
            index = index || $('.' + dom.ifr).parents('.' + dom.lay).attr('times');
            return $('#' + dom.lay + index).find('.' + dom.ifr).contents().find(selector);
        };

        //得到当前iframe层的索引，子iframe时使用
        layer.getFrameIndex = function (name) {
            return $(name ? '#' + name : '.' + dom.ifr).parents('.' + dom.lay).attr('times');
        };

        //iframe层自适应宽高
        layer.iframeAuto = function (index) {
            index = index || $('.' + dom.ifr).parents('.' + dom.lay).attr('times');
            var heg = this.getChildFrame('body', index).outerHeight(),
                lbox = $('#' + dom.lay + index), tit = lbox.find('.xubox_title'), titHt = 0;
            !tit || (titHt = tit.height());
            lbox.css({height: heg + titHt});
            var bs = -parseInt($('#xubox_border' + index).css('top'));
            $('#xubox_border' + index).css({height: heg + 2 * bs + titHt});
            $('#' + dom.ifr + index).css({height: heg});
        };

        //关闭layer
        layer.close = function (index) {
            var layerNow = $('#' + dom.lay + index), shadeNow = $('#xubox_moves, #xubox_shade' + index);
            if (layerNow.attr('type') == that.type[1]) {
                if (layerNow.find('.xuboxPageHtml')[0]) {
                    layerNow.remove();
                } else {
                    layerNow.find('.xubox_close,.xubox_botton,.xubox_title,.xubox_border').remove();
                    for (var i = 0; i < 3; i++) {
                        layerNow.find('.layer_pageContent').unwrap().hide();
                    }
                }
            } else {
                document.all && layerNow.find('#' + dom.ifr + index).remove();
                layerNow.remove();
            }
            shadeNow.remove();
            ready.iE6 && that.reselect();
            typeof ready.config.end[index] === 'function' && ready.config.end[index]();
            delete ready.config.end[index];
        };

        //关闭加载层，仅loading私有
        layer.loadClose = function () {
            var parent = $('.xubox_loading').parents('.' + dom.lay),
                index = parent.attr('times');
            layer.close(index);
        };

        //出场内置动画
        layer.shift = function (type, rate) {
            var config = that.config, iE6 = ready.iE6, layerE = that.layerE, cutWth = 0, ww = win.width(),
                wh = win.height();
            (config.offset[1] == '50%' || config.offset[1] == '') ? cutWth = layerE.outerWidth() / 2 : cutWth = layerE.outerWidth();
            var anim = {
                t: {top: config.border[0]},
                b: {top: wh - layerE.outerHeight() - config.border[0]},
                cl: cutWth + config.border[0],
                ct: -layerE.outerHeight(),
                cr: ww - cutWth - config.border[0],
                fn: function () {
                    iE6 && that.IE6();
                }
            };
            switch (type) {
                case 'left-top':
                    layerE.css({left: anim.cl, top: anim.ct}).animate(anim.t, rate, anim.fn);
                    break;
                case 'right-top':
                    layerE.css({left: anim.cr, top: anim.ct}).animate(anim.t, rate, anim.fn);
                    break;
                case 'left-bottom':
                    layerE.css({left: anim.cl, top: wh}).animate(anim.b, rate, anim.fn);
                    break;
                case 'right-bottom':
                    layerE.css({left: anim.cr, top: wh}).animate(anim.b, rate, anim.fn);
                    break;
            }
            ;
        };

        //初始化拖拽元素
        layer.setMove = function () {
            return that.move();
        };

        //给指定层重置属性
        layer.area = function (index, options) {
            var nowobect = [$('#' + dom.lay + index), $('#xubox_border' + index)],
                type = nowobect[0].attr('type'), main = nowobect[0].find('.xubox_main'),
                title = nowobect[0].find('.xubox_title');
            if (type === that.type[1] || type === that.type[2]) {
                nowobect[0].css(options);
                if (nowobect[1][0]) {
                    nowobect[1].css({
                        width: options.width - 2 * parseInt(nowobect[1].css('left')),
                        height: options.height - 2 * parseInt(nowobect[1].css('top'))
                    });
                }
                main.css({height: options.height});
                if (type === that.type[2]) {
                    var iframe = nowobect[0].find('iframe');
                    iframe.css({
                        width: options.width,
                        height: title ? options.height - title.outerHeight() : options.height
                    });
                }
                if (nowobect[0].css('margin-left') !== '0px') {
                    options.hasOwnProperty('top') && nowobect[0].css({top: options.top - (nowobect[1][0] && parseInt(nowobect[1].css('top')))});
                    options.hasOwnProperty('left') && nowobect[0].css({left: options.left + nowobect[0].outerWidth() / 2 - (nowobect[1][0] && parseInt(nowobect[1].css('left')))})
                    nowobect[0].css({marginLeft: -nowobect[0].outerWidth() / 2});
                }
            }
        };

        //关闭所有层
        layer.closeAll = function () {
            var layerObj = $('.' + dom.lay);
            $.each(layerObj, function () {
                var i = $(this).attr('times');
                layer.close(i);
            });
        };

        //置顶当前窗口
        layer.zIndex = that.config.zIndex;
        layer.setTop = function (layerNow) {
            layer.zIndex = parseInt(layerNow[0].style.zIndex), setZindex = function () {
                layer.zIndex++;
                layerNow.css('z-index', layer.zIndex + 1);
            };
            layerNow.on('mousedown', setZindex);
            return layer.zIndex;
        };
    };

//主入口
    ready.run = function () {
        $ = jQuery;
        win = $(window);
        this.load();
        $.layer = function (deliver) {
            var o = new Class(deliver);
            return o.index;
        };
    };

//为支持CMD规范的模块加载器
    var require = '../../init/jquery'; //若采用seajs，需正确配置jquery的相对路径。未用可无视此处。
    if (this.seajs) {
        define([require], function (require, exports, module) {
            ready.run();
            exports.layer = [window.layer, window['$'].layer];
        });
    } else {
        ready.run();
    }

}(window);