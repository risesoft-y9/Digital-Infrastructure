// (function($) {
var WfMsg = (function(options) {
	function WfMsg(options) {
		var option = {}
		$.extend(option, this.defaultoption, options)
		console.log(option)
		this.init(option)
	}
	WfMsg.prototype = {
		init: function(option) {
			this.option = option
			this.creatHtml()
		},
		isPC: function() {
			var userAgentInfo = navigator.userAgent;
			var Agents = new Array("Android", "iPhone", "SymbianOS", "Windows Phone", "iPad", "iPod");
			var flag = true;
			for (var v = 0; v < Agents.length; v++) {
				if (userAgentInfo.indexOf(Agents[v]) > 0) { flag = false; break; }
			}
			return flag;
		},
		creatHtml: function() {
			var that = this
			var boxposition = this.option.position ? this.option.position : 'top-right'
			if(!this.isPC() && !this.option.position){
				boxposition="center-center"
			}
			if ($('.wfmsgbox.' + boxposition).length == 0) {
				if(!this.isPC()){
					this.elebox = $('<div class="m-wfmsgbox wfmsgbox ' + boxposition + '"></div>')
				}else{
					this.elebox = $('<div class="wfmsgbox ' + boxposition + '"></div>')
				}
				$("body").append(this.elebox)
			} else {
				this.elebox = $('.wfmsgbox.' + boxposition)
			}
			if (this.option.shadow) {
				this.shadow = $('<div class="wfmsgshadow"></div>')
				$("body").append(this.shadow)
			}
			this.ele = $('<div class="wfmsg ' + this.option.type + '"><div class="wfcontent">' + this
				.option.content + '</div></div>')
			this.autoclose(this.ele)
			this.elebox.append(this.ele)
			if (this.option.title) {
				var url = this.option.icon[this.option.type]
				if(this.option.showicon){
					this.ele.prepend(
						'<div class="wftitle"><span class="icon" style="background-image:url(' + url +
						')"></span><span>' + this.option.title + '</span></div>')
				}else{
					this.ele.prepend(
						'<div class="wftitle noicon"><span>' + this.option.title + '</span></div>')
				}
			}
			this.events()
		},
		autoclose: function(obj) {
			if (this.option.autoclose) {
				var that = this
				setTimeout(function() {
					obj.stop(true, true).slideUp(150, function() {
						var objpar = obj.parent()
						if (objpar.children("div").length == 1) {
							obj.remove()
							objpar.remove()
						} else {
							obj.remove()
						}
					})
					if (that.shadow) {
						that.shadow.stop(true, true).fadeOut(150, function() {
							that.shadow.remove()
						})
					}
				}, that.option.time)
			}
		},
		close: function(obj,time) {
				var that = this
				var hidetime=time?time:0
				setTimeout(function() {
					obj.stop(true, true).slideUp(150, function() {
						var objpar = obj.parent()
						if (objpar.children("div").length == 1) {
							obj.remove()
							objpar.remove()
						} else {
							obj.remove()
						}
					})
					if (that.shadow) {
						that.shadow.stop(true, true).fadeOut(150, function() {
							that.shadow.remove()
						})
					}
				}, hidetime)
		},
		events: function() {
			var that = this
			if (this.option.shadowclickclose && this.shadow) {
				this.shadow.on("click", function() {
					that.shadow.stop(true, true).fadeOut(150, function() {
						that.shadow.remove()
					})
					that.ele.stop(true, true).slideUp(150, function() {
						var objpar = that.ele.parent()
						objpar.remove()
					})

				})
			}
			this.ele.on("click", function(e) {
				console.log(e)
				if(that.option.callback){
					that.option.callback(e,that)
				}
			})
			this.ele.close=function(time){
				that.close(that.ele,time)
			}
			
		},
		defaultoption: {
			"type": "info", //err,success,warn
			"position": "", //top-left,top-center,top-right,center-left,center-center,center-right,bottom-left,bottom-center,bottom-right
			"title": "",
			"content": "",
			"shadow": "", //true,false
			"shadowclickclose": false, //true false
			"autoclose": false, //true false
			"time": 2000, //
			"icon": {
				"info": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAAAXNSR0IArs4c6QAAA0ZJREFUWEfFl1tIFGEUx//n2+wilUUWQUQWGYQPERTUQ6EPXeghKagoiF7S8bLjzvokFGTQRZ9211lbZ6WCHiQzontQUJIPFUFCYBeKsoIgMkpLrXTnxKzusp+3mRFj53XO/39+38z5znc+gsOnpsbImjWbdjJzIYAVDM4m0AJLzuBvBOoC8I6IrvX/4utVVUq3E2uyCwrUN64WbKoMHAAjyy4+/p7QTUCTSUL3lxe9nEgzIUBIN04woAKY6yjx6KAeAnSfqhwdTz8uQFA3LgHYM8nEI2UtmqrsHctrTIBg2HgPRs4UJR+yIXRqXmX5SM9RAIE64ysRsqc0+bAZM7r8FcrCVG8JIKA3PCTQJjfJiXAO4CYzg17gt6mQEMcm0jO4za+WbE7EJAGGC+6Im+QAtWpqcUGqJqgbpwFU2VT+yURhxgGsrUam+dhttRNB8XmVaGqycDi6apD5tc1CeliIDdYWjQOEwsYZZpS6Wz3AjJ/+CkXaokE9egvgHXZeRIj4vEoZ1daenTMjc/ADgPl2orHfUwebscvs8bwS4ENgbHfo8/1P37RlFApHdjGLKw5FUxpGZO6moG5cBLDPtTOhE0ydAOcCWOJaPyRotgCeAVjrxICBKBHd0LzFN1PjA3oknyDKJtE52ymgN3wk0FInAII5L0a8yIrlgf4nlZWV/TKI8YaAlU684h7gT9YX6AWQaS+iDoDzknGx2BpNK3suAYQi1XaNaESePhcAspRhFvjV0la5CTXUA2T9CqdPn6tfILnGxGJNK/oiAxhPAaxzmj3xCxwXYYrxHU1VpGYTCkcPMvMFp8mH49ontQ0JVOFTi3Vp9XXGfRCkc8EBTPOkGlGG8OSWlx9+m0gQCjcWMptXHSSUQuKNyG0rJuCuT1W2jSi+CEAlLgGGWrElcnMYsWke9/tKq2WA6AOA890AJA8jS+TqOCYENa/ilxtQQx2BrOHVeh4B2GgDIx/H8a8wNAE7GEjonqYWb5UAAufnYfpADv7GfogMz35mPjURAAHyQJIIdj6S8W1mbvFAtA0O9n4WYmYuPGI9QFsAWHDjHu3jjmRJiHQOpQmItI7lSYh0XkySDSadV7NkTaTzcpq6lf7X9fwfAy13OFaaPU8AAAAASUVORK5CYII=",
				"err": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAAAXNSR0IArs4c6QAAAlVJREFUWEfNlz1v01AUht8Tiwy56QAShH/ABkJlByEQgg4g0a4siEDimyIGFhbKwMZQ4WtXjdi6VagDQlQCKsTEAkLwHxAfI9RBqlQfdK2m2K4/rtukridHOfd9n3vO8fUxoeKLKvZHaQC/1zuNIJgGUQvA8a0N/ADzT9Rqz4XjfC6zKWMA37bbIJoGcLHA4A2Yl4XrPjMBKQQIjQFtPmkiOIwhojVmfiqUepG3LhfAl/IVgMtljHfEMi8K172TpZEJ4EvJezKOL14VSl1J00sFGMnOE24B8GhCqbkkxA4AX8rFsObjuJhvC9ftR6VjAP7s7CSC4OM4vENN5k/Cdc9kA+TsXqdQL6wBD/MAC+MSWdjOAM/M1P1W6zsBR9IMhjX8I+VcFoRJDIBYQ24D+L3eFJhfFu1ON1IahKF5KO8HwcQxz1vX9/8BbPsxiB4U1T/NqIx52ApBcKnpea/jAFLqo/NmEYD+P2qof2dlJUuLmW80XXcpCaDTP2UCEIXQ93l9kabHwP2mUk8OHEDFJai6Cf9KeT4A1vbjMWxsbAjq9wexHuB2+9CgXv8G4Og4DyICVhpKXR96xN8Ftu2BqJMFMKKj+FZ0WooB/O50TliW9QHAYdPHsUycnpIajnMh82W0m2e6DACAq8kRLXUgGUj5joFzJcVzw4lovuE495JB+zWSvRVKpU7TuUPpupTzBNzdUyaIusJxFrI0DvZYHqXe+jC5ZjCmr4J5ZWQfJsnU/ep2m8KyzgI4xcwnw9OM6CuAL/7m5vvhoGFatsISmArtNq5ygH/Ofz4whxyqrAAAAABJRU5ErkJggg==",
				"success": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAAAXNSR0IArs4c6QAAAmNJREFUWEfFlz1v01AUht9jRBiQUFUJ0d/AABItQnGwE6iECmWFGQlkhwHETAfKADsTsVv+QEFi4WOoKhQHrhkaIRj4B4giJGBCait8Kps4cpzrjySOnPHm3vd9zrnH595LKPlHJftjJIA7H88d299TqgDpTHQCzHNBAEQ7xPwDjC3v6G7HXuju5w0sF4DpVBcJdNcDlgiopIkz8JMYLxVFsZ9q77tZIJkATUddY+BWlpD0f4Zt1YWZtjYVwHCqnwl0aizz3iIGf7F193SSRiKA6ag8iXF8raULqZd0sNmpvmGmy0UCgPDE0sS9uOYQgNlWLRCMQs3D7SA2bc21o9oDALc75+c99ranYe5rMrhr6+5CIsA0ow9NOZaFfgaubZyszM7NfAcwW0QGmPHwf4/Cg1jEoqWLWjjWBzAcdZmAV0WZ23WxarTV1TiAr+8x19bqrggAQ8Omoz5i4P6kAH7kaeY908ctXawMAJid2jqYb04CkMc80Cd6Zmkfgu7az4DpqH76lxM71j/vgqcoDVlKgwrPEXlE+7Wli6sjAaQZjGju+0oAcmyBzGgMc/kW5C3CqKEfSlbBybaUgOEibLbVi0zYylOEIYQ/N+lTS9Mh5jOtuvtpoAaM7fnD+HvkGwHH80LIGk3mWsKmpYlLQ43IHyi1FfsApR9G085C/CAaqIHo3hVxFYvXAhG/bWnulaHxpKIp9UrWP6AKuJolRS79CmTZKPVaHgKFDxMmLIHTHyYAfoHxorCHSTQrN941ZiqHdhsK6KzsaeYB4vfOn83n17/uZTak3oTMl1FeoXHnlQ5wAAhLLTD9o+OoAAAAAElFTkSuQmCC",
				"warn": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAAAXNSR0IArs4c6QAAAqlJREFUWEftlk9IFGEYxp93XM2DRRSZzlgUdMhCoT8QtrPSqQi6BBVo7mxRFhTZraORx24VBbVF7awpVNAliDqFO5sEVqCUHYKidlbbKMI8mO7OG7sq6Lp+34ywWOB3/Z73eX/zzPePsMiDFrk//j+AoYh/P5MSAPFmAJVTCabA9IHYiVWH4k+9pOo6AdsMXCDwOQA1kgYJBl3TjNhlNyBSADsa2E7MNwHsdGM4Q9PHRKe1YOyNqE4IkDQD+4j4CTN8Hpvn5ERIM9MB1Yg9m69+XoCvpr7LR7AW2ny6YRYizdDXGdarQhDzAtim3k9A3UK+PL+GgQHNsOpdA9im/xKB2kXNSyqqUHnwYU6SenwYmdFhISuDOzQjfjFfNCcBfrClbHhs1XcGVogcy9Zuw+q9V3OSH8/bMP7trRCAgJGq8p9r6Mj78ZnCOQDJqP8omDpl0XsFmFyV3KIG4/fFABF/GEQniwLAfFsNxVuFALYZeEnghmIAMKhXM2K7xQmYgU8AbygGAECfVSO28R8HiOp9YOwoSgKE12rQmnWkz90FEb0LhCYZQHZ+ef3xnOx3/103coDRrYasZuEvGOrUz7CD6+4cvalIwdnqFuuGEODLHb/qKyVbZq2Ur0RFXSgnGx2IwBn7JStBeoK19SfiSSFAdjJp6rcAzNqv+e4LOIjCqmGdyvcpeBklOvfUKE76HSTHsYc1MOIovq01LS8SrgCyIvue3kQKuqS5uhCwg2btmNVdSCp5kOhtAK646CGSnFcNa/LWKjCkT7KkqQeZESbCMi8gzPhDhFbVsKKiOilAtjgVbdg04ZR0kMvzgRndpUqmvTLY+1EG7Qpg2iRpNtY6nDmkEDUCqKWpZzkDKQCDDnOPQiWPVKNnUNZ4et4TgFtTL7olgKUE/gLFwAUwhSpr9gAAAABJRU5ErkJggg=="
			}
		}
	}
	new WfMsg(options)
})
// return LayerMsg;
// })();
