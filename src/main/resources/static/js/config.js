/**
 * Created by fish on 2018/4/3.
 */
/**/
var host = window.location.origin
// 1.3版本接口
var getPrizeList = host + '/prize/list'
// 获取获奖历史
var getPriHis = host + '/Prize/getPrizeRecord'
// 抽奖
var needPrize = host + '/prize/getPrize'
// 跑马灯抽奖记录
var getExpensivePrizeRecord = host + '/Prize/getExpensivePrizeRecord'
// 提交实物奖信息
var getGoods = host + '/prize/getGoods'
// 获取用户积分信息
var getUserCreditsMsg = host + '/Credits/getUserCreditsMsg'

var width = window.innerWidth
var height = window.innerHeight

var urlTools = {
    //获取RUL参数值
    getUrlParam: function (name) {
        /*?videoId=identification  */
        var params = decodeURI(window.location.search);
        /*
        截取？号后面的部分
        index.html?act=doctor,截取后的字符串就是?act=doctor
        */
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = params.substr(1).match(reg);
        if (r != null) return unescape(r[2]);
        return null;
    },
    //设置URL变量值
    setUrlParam: function (url, name, val) {
        // 首先处理#号
        var pos = url.indexOf('#')
        if (pos != -1 && url[pos] == '#') {
            url = url.substring(0, url.length - 1)
        }
        // 接下来就是正常的处理
        if (url.indexOf('?') == -1) {
            return url + '?' + name + '=' + val
        } else {
            return url + '&' + name + '=' + val
        }
    },
    // 改变URL变量值
    changeParam: function (url, arg, arg_val) {
        // 首先处理#号
        var pos = url.indexOf('#')
        if (pos != -1 && url[pos] == '#') {
            url = url.substring(0, url.length - 1)
        }
        // 接下来就是正常的处理
        var pattern = arg + '=([^&]*)';
        var replaceText = arg + '=' + arg_val;
        if (url.match(pattern)) {
            var tmp = '/(' + arg + '=)([^&]*)/gi';
            tmp = url.replace(eval(tmp), replaceText);
            return tmp;
        } else {
            if (url.match('[\?]')) {
                return url + '&' + replaceText;
            } else {
                return url + '?' + replaceText;
            }
        }
        return url + '\n' + arg + '\n' + arg_val;
    }
}

// 游戏id要存起来，备用
var getGameId = urlTools.getUrlParam('gameId')
if (getGameId) {
    window.localStorage.setItem('user_gameId', getGameId)
}

/**
 * 微端UA解析
 * @type {{getGameId: UaTools.getGameId, getChannel: UaTools.getChannel}}
 */
var UaTools = {
    getGameId: function () {
        if (!window.KUKU_APP_JS) {
            return -1;
        }
        var ua = navigator.userAgent;
        var tmps = ua.split('/KUKU_APP');
        if (tmps.length != 2) {
            return -2;
        }
        var tmp = tmps[1];
        var x = tmp.split('/');
        return x.length > 15 ? x[15] : -3;
    },
    getChannel: function () {
        if (!window.KUKU_APP_JS) {
            return -1;
        }
        var ua = navigator.userAgent;
        var tmps = ua.split('/KUKU_APP');
        if (tmps.length != 2) {
            return -2;
        }
        var tmp = tmps[1];
        var x = tmp.split('/');
        return x.length > 3 ? x[3] : -3;
    }
}

var common = {
    // 获取实名礼包
    getRealNameGift: function (callback, failCallBack) {
        request(getRealNameGiftUrl, {}, function () {
            console.error("网络请求出错")
        }, function (result) {
            if (!result || result.code != 1) {
                console.error(result.msg)
                failCallBack && failCallBack()
                return
            }
            // 返回礼包相关的信息
            callback && callback(result.data)
        })
    },
    // 获取VIP专属客服的二维码
    getCustomerServiceQRCode: function (gameId, callback) {
        request(getCustomerServiceQRCodeUrl, {
            gameId: gameId
        }, function () {
            console.error("网络请求出错")
            return
        }, function (result) {
            if (!result || result.code != 1) {
                console.error(result.msg)
                return;
            }
            callback && callback(result.data.qrCode)
        })
    },
    // 判断用户今天是否为生日
    todayIsBirthday: function (timeStr) {
        if (new Date(timeStr.replace(/-/g, "/")).getMonth() == new Date().getMonth() && new Date(timeStr.replace(/-/g, "/")).getDate() == new Date().getDate())
            return true;
        return false;
    },
    // 判断当前是否为开发环境
    isDev: function () {
        if (window.location.href.indexOf("local.aiyinli.cn") != -1) {
            return true
        }
        return false
    },
    // 初始化复制的东西
    selfClipboard: function (that, classNames) {
        // classNames为带点类名，eg:.xxx,string
        if (typeof ClipboardJS === 'function') {
            var clipboard = new ClipboardJS(classNames);

            clipboard.on('success', function (e) {
                toast(that, "复制成功")
                e.clearSelection();
            })

            clipboard.on('error', function (e) {
                toast(that, "复制失败")
                console.error('Action:', e.action);
                console.error('Trigger:', e.trigger);
            });
            return
        }
        console.error("clipboard not init");
    },
    // 获取app名字，是在桌面可以直接看到到的名字
    getAppName: function () {
        if (window.KUKU_APP_JS && typeof window.KUKU_APP_JS.getAppName == "function") {
            return window.KUKU_APP_JS.getAppName()
        }
        return false
    },
    // 下载的过程
    appDownloading: function (params) {
        if (!params.gameName && params.name) {
            params.gameName = params.name
        }
        if (params.className.indexOf(".") == -1 && params.classNames.indexOf(".") != -1) {
            // className需要点，classNames不需要点
            var centerClassName = params.className
            params.className = params.classNames
            params.classNames = centerClassName
        }
        console.log(params, "appDownloading")
        // 如果显示的是安装的按钮,那就是要安装了，直接去呼唤下载，100时自动安装

        // 多次点击暂停下载
        if ($(params.className).find("span").text().indexOf("%") != -1 && window.KUKU_APP_JS && typeof window.KUKU_APP_JS.pauseDownloadTask === "function") {
            console.log("触发暂停")
            window.KUKU_APP_JS.pauseDownloadTask(params.downUrl, params.gameName + '.apk')
            $(params.className).find("span").text("暂停")
            return;
        }

        // 检测到本地已安装，就要打开
        if ((params.haveInstalled == true || $(params.className).find("span").text().indexOf("打开") != -1 || $(params.className).find("span").text().indexOf("马上玩") != -1 || $(params.className).find("span").text().indexOf("开始") != -1) && typeof window.KUKU_APP_JS.openAppByPackageNameV2 == "function") {
            console.log(window.KUKU_APP_JS.haveInstalled(params.pkgName), "触发打开")
            // 还是需要先检测一下是否安装，万一卸载了呢
            if (window.KUKU_APP_JS.haveInstalled(params.pkgName)) {
                window.KUKU_APP_JS.openAppByPackageNameV2(params.pkgName)
                return;
            }
            // 中途卸载了的话还是需要去下载的
            window.KUKU_APP_JS.downloadFile(params.downUrl, JSON.stringify({
                targetClassName: params.className,
                downloadPackName: params.gameName,
                pkgName: params.pkgName
            }), params.gameName + '.apk', 30)
            return
        }

        // 如果没有安装判断是否有下载的方法，有下载的方法就去下载
        if (params.downUrl && params.haveInstalled == false && typeof window.KUKU_APP_JS.downloadFile == "function") {
            console.log("触发下载")
            baiduStatistics({
                type: params.baiduStatisticsType,
                operation: 'click',
                userId: params.userId || 0000
            })
            toast(params.pointer, "正在下载“" + params.gameName + "”···", 3)
            window.KUKU_APP_JS.downloadFile(params.downUrl, JSON.stringify({
                targetClassName: params.className,
                downloadPackName: params.gameName,
                pkgName: params.pkgName
            }), params.gameName + '.apk', 30)
            return
        }
    },
    // 给端调用的接口，用于显示进度条之类的
    downloadProgress: function (params, progress) {
        params = JSON.parse(params)
        console.log(params, progress, "downloadProgress")
        // 止动处理
        if (progress < 0) {
            console.error("下载出错");
            return
        }
        // 如果下载完成了
        if (progress == 100) {
            console.log("下载完成，开始安装")
            // 修改文字
            $(params.targetClassName).css({
                "border": "none"
            }).find("span").text("安装").css({
                "color": "#fff",
                "background-color": "#ff8020"
            })
            // 修改样式
            $(params.targetClassName).find("i.progressBar").css({
                "width": $(params.targetClassName).width() + 'px'
            })
            if (window.KUKU_APP_JS && typeof window.KUKU_APP_JS.installApk === "function") {
                window.KUKU_APP_JS.installApk(params.downloadPackName + ".apk")
                var total = 60
                var checkInstallTime = setInterval(function () {
                    if (total >= 0 && window.KUKU_APP_JS.haveInstalled(params.pkgName)) {
                        if (window.location.href.indexOf("_gameContainer.html") != -1) {
                            $(params.targetClassName).find("span").text("马上玩")
                        } else {
                            $(params.targetClassName).find("span").text("打开")
                        }
                        $("#videoSwiper").find(params.targetClassName).find("span").text("开始")
                        clearInterval(checkInstallTime)
                    } else if (total < 0) {
                        clearInterval(checkInstallTime)
                    }
                    total--
                    console.log(window.KUKU_APP_JS.haveInstalled(params.pkgName), "检测安装")
                }, 4000)
            }
            return
        }
        console.log("正在下载：", params.targetClassName, progress)
        // 修改文字
        $(params.targetClassName).css({
            "background-color": "#ccc",
            "border": "none"
        }).find("span").text(progress + "%").css({
            "color": "#fff"
        })
        //修改样式
        $(params.targetClassName).find("i.progressBar").css({
            "width": $(params.targetClassName).width() / 100 * progress + "px",
        })
    },
    // 检测是否在某一时间段
    checkTimes: function (start, end) {
        var nowTimes = Date.parse(new Date());
        // var startTimes = Date.parse("2019/10/01")
        var startTimes = Date.parse(start)
        var endTimes = Date.parse(end)
        if (nowTimes >= startTimes && nowTimes <= endTimes) {
            console.log("在期望时间内")
            return true;
        }
        console.log("在期望时间外")
        return false;

    },
    // 获取各种位置的banner
    getBanner: function (params) {
        request(getBannerUrl, {
            gameId: params.gameId || 0,
            type: params.type,
            position: params.position,
            page: params.page,
            pageSize: params.pageSize
        }, function () {
            console.error("网络错误")
            return
        }, function (result) {
            params.successFunc && params.successFunc(result)
        })
    },
    // 获取各种位置的视频或图片banner
    getVideoBanner: function (params) {
        request(getVideoBannerUrl, {
            sectionId: params.sectionId,
            page: params.page,
            pageSize: params.pageSize
        }, function () {
            console.error("网络错误")
            return
        }, function (result) {
            params.successFunc && params.successFunc(result)
        })
    },
    // 初始化videoSwiper
    init_videoSwiper: function (obj, swiperClassName, targetId) {
        var that = obj
        that.videoSwiper = new Swiper(swiperClassName, {
            direction: 'horizontal', // 垂直切换选项
            spaceBetween: 20,
            loop: true, // 循环模式选项
            roundLengths: true, //将slide的宽和高取整
            watchOverflow: true, //因为仅有1个slide，swiper无效
            preventLinksPropagation: false, //阻止click冒泡。拖动Swiper时阻止click事件
            iOSEdgeSwipeDetection: true, //边缘探测
            observer: true, //改变swiper时自动初始化
            observeSlideChildren: true, //子slide更新时，swiper是否更新
            on: {
                slideChange: function () {
                    var videoPlayIndex = this.activeIndex,
                        $targetsVideo = $(targetId).find("video");
                    $targets = $(targetId).find(".swiper-slide");
                    $targetsVideo.removeAttr("controls");
                    $targetsVideo.trigger("pause");
                    if ($targets.eq(videoPlayIndex).find("video").length > 0) {
                        var _video = $targets.eq(videoPlayIndex).find("video").get(0)
                        setTimeout(function () {
                            _video.setAttribute("controls", "controls")
                        }, 200);
                        _video.muted = false;
                        _video.currentTime = 0;
                        _video.play();
                        _video.onended = function () {
                            that.videoSwiper.slideNext();
                        }
                    } else {
                        setTimeout(function () {
                            that.videoSwiper.slideNext();
                        }, 7000);
                    }
                },
            }
        })

    },
    // 自动写入sid
    syncSid: function (params) {
        request(syncSidUrl, {
            sid: params.userSid
        }, function () {
            params.errorFunc && params.errorFunc()
        }, function (result) {
            params.successFunc && params.successFunc(result)
        })
    },
    // 检测是0微信环境/1小程序环境/-1其他环境
    envCheck: function () {
        var ua = window.navigator.userAgent.toLowerCase()
        if (ua.match(/MicroMessenger/i) == 'micromessenger') {
            wx.miniProgram.getEnv(function (res) {
                if (res.miniprogram) {
                    // 小程序环境下逻辑
                    return 1;
                } else {
                    // 微信环境
                    return 2;
                }
            })
        } else {
            return -1;
        }
    },
    // 查看是否有某个包名
    checkBuildRes: function (params) {
        // 要屏蔽qqLogin和wxLogin的包名
        var buildNo = ['com.xyqk.ayl']
        if (buildNo.indexOf(params.appName) != -1) {
            return true
        }
        return false
    },
    // 微信登录检测
    wxCheckLogin: function (func) {
        $.ajax({
            url: wxLogin,
            type: "POST",
            dataType: "json",
            data: {},
            success: function (result) {
                func(result)
            }
        })
    },
    // 2.7接口，获取不同的游戏
    recommendList: function (params, func) {
        $.ajax({
            url: recommendListUrl,
            type: 'POST',
            dataType: 'json',
            data: params,
            error: function () {
                console.error('网络错误！')
            },
            success: function (result) {
                func && func(result)
            }
        })
    },
    // 检测是否登录
    checkLogin: function (func, tags) {
        $.ajax({
            url: checkSessionURL,
            type: "POST",
            dataType: "json",
            data: {
                tags: tags
            },
            //如果发生网络错误，不跳转登录页面登录，而是刷新当前页面重新监测登录态
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(jqXHR, "jqXHR01")
                console.log(textStatus, "error01")
                console.log(errorThrown, "errorThrown01")
                window.location.reload();
            },
            success: function (result) {
                //如果端存在js方法，则执行（解决用户清理缓存，导致的登录态丢失)
                func(result)
                if (result && result.code == 1 && window.KUKU_APP_JS &&
                    (typeof window.KUKU_APP_JS.userChange === 'function')
                ) {
                    window.KUKU_APP_JS.userChange(result.data.sid, result.data.tid);
                }
            }
        });
    },
    toast: function (handler, text, showSecond) {
        handler.mask = true
        handler.maskText = text
        setTimeout(function () {
            handler.mask = false
        }, showSecond * 1000 || 3000)
    },
    showLoading: function (handler, text) {
        handler.mask = true
        handler.maskText = text
    },
    hideLoading: function (handler) {
        setTimeout(function () {
            handler.mask = false
        }, 300)
    },
    trimAll: function (text) {
        return text.replace(/\s+/g, '')
    },
    trim: function (text) {
        return text.replace(/^\s+|\s+$/g, '')
    },
    stringEmpty: function (str) {
        str = str + ''
        return !str || str.replace(/\s+/g, '').length == 0
    },
    isMobileNumber: function (mobile) {
        if (/^(0|86|17951)?(13[0-9]|15[012356789]|17[0-8]|18[0-9]|14[5679]|19[189]|16[56])[0-9]{8}$/.test(mobile)) {
            return true
        }
        return false
    },
    bindAutoComplete: function (tags, obj, callback) {
        $(obj).autocomplete({
            lookup: tags,
            minChars: 0,
            width: 150,
            scroll: true,
            scrollHeight: 300,
            multiple: false,
            matchContains: true,
            maxHeight: 300,
            onSelect: function (s) {
                callback()
            }
        });
    },
    request: function (url, data, errorFunc, successFunc) {
        $.ajax({
            url: url,
            type: "POST",
            dataType: "json",
            data: data,
            error: function (jqXHR, error, errorThrown) {
                console.log(jqXHR, "jqXHR")
                console.log(error, "error")
                console.log(errorThrown, "errorThrown")
                errorFunc(error)
                return
            },
            success: function (result) {
                successFunc(result)
            }
        });
    },
    // 检测是否为移动端
    isMobile: function () {
        for (var e = navigator.userAgent.toLowerCase(), n = ["android", "iphone", "symbianos", "windows phone", "ipad", "ipod"], t = 0; t < n.length; t++)
            if (0 < e.indexOf(n[t])) return !0;
        return !1
    },
    isDivScrollToBottom: function (obj, func, params) {
        //绑定事件前，先解绑事件
        $(obj).off('scroll')
        if (params && params.type == 1) {
            // console.log('div', obj, 'to bottom')
            var t = 0,
                isUpScroll = false,
                upTimeOut = null;
            $(obj).scroll(function () {
                var st = $(this)[0].scrollTop; //滚动条的高度，即滚动条的当前位置到div顶部的距离
                if (t < st && !isUpScroll) {
                    // 如果是向下滑动
                    // 表示已经在执行显示的动画了
                    isUpScroll = true
                    $(".js-topMsg").stop(true, false).animate({
                        "top": '5px'
                    }, 1000, 'swing', function () {
                        isUpScroll = false
                        if (upTimeOut)
                            clearTimeout(upTimeOut)
                        upTimeOut = setTimeout(function () {
                            $(".js-topMsg").stop(true, false).animate({
                                'top': "-100px"
                            }, 500, 'linear')
                        }, 5000)
                    })
                }
                setTimeout(function () {
                    t = st;
                }, 0)
            })
            return
        }
        //绑定事件前，先解绑事件
        $(obj).off('scroll')
        // console.log('div', obj, 'to bottom')
        $(obj).scroll(function () {
            var h = $(this).height(); //div可视区域的高度
            var sh = $(this)[0].scrollHeight; //滚动的高度，$(this)指代jQuery对象，而$(this)[0]指代的是dom节点
            var st = $(this)[0].scrollTop; //滚动条的高度，即滚动条的当前位置到div顶部的距离
            //  alert(h+","+sh+","+st)
            if (h + st >= sh - 150) {
                // alert(1)
                func()
            }
        })
    },
    setCookie: function (name, value, days) {
        if (!days) {
            console.log(name, value, days, "cookie")
            document.cookie = name + "=" + escape(value) + ";path=/";
            return;
        }
        var exp = new Date();
        exp.setTime(exp.getTime() + days * 24 * 60 * 60 * 1000);
        document.cookie = name + "=" + escape(value) + ";path=/;expires=" + exp.toGMTString();
    },
    getCookie: function (name) {
        var arr, reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
        if (arr = document.cookie.match(reg))
            return unescape(arr[2]);
        else
            return null;
    },
    delCookie: function (name) {
        // var exp = new Date();
        // exp.setTime(exp.getTime() - 1);
        // var cval = getCookie(name);
        // if (cval != null)
        //     document.cookie = name + "=" + cval + ";expires=" + exp.toGMTString();
        // 旧方法有误，改成以下新方法
        setCookie(name, " ", -1)
    },
    // 判断是否是qq浏览器这个辣鸡
    isQQBrowser: function () {
        return navigator.userAgent.indexOf('QQBrowser') != -1
    },
    // 判断是否为safari浏览器
    isSafari: function () {
        return navigator.userAgent.indexOf("Safari") != -1 && navigator.userAgent.indexOf("Chrome") == -1
    },
    isQQ: function () {
        return "qq" == navigator.userAgent.toLowerCase().match(/\bqq\b/i)
    },
    isWeixin: function () {
       return "micromessenger" == navigator.userAgent.toLowerCase().match(/MicroMessenger/i)
    },
    isWeibo: function () {
        return "weibo" == navigator.userAgent.toLowerCase().match(/weibo/i)
    },
    isAndroid: function () {
        return -1 < navigator.userAgent.indexOf("Android") || -1 < navigator.userAgent.indexOf("Linux")
    },
    isiOS: function () {
        return !!navigator.userAgent.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/)
    },

    isPCWeixin: function () {
        return "windowswechat" == navigator.userAgent.toLowerCase().match(/WindowsWechat/i)
    },
    getURLVar: function (key) {
        var reg = new RegExp('(^|&)' + key + '=([^&]*)(&|$)', 'i')
        var r = window.location.search.substr(1).match(reg)
        if (r != null) return decodeURIComponent(r[2])
        return null
    },
    // 获取指定范围的随机整数
    getNum: function (min, max) {
        var num = Math.floor(Math.random() * (max - min) + min);
        return parseInt(num)
    },
    // 判断是否是模拟器环境
    isEmulator: function () {
        if (window.KUKU_APP_JS && typeof window.KUKU_APP_JS.isEmulator == 'function' && window.KUKU_APP_JS.isEmulator()) {
            return true
        }
        return false
    },
    // 用于百度统计
    baiduStatistics: function (params) {
        // 没有拿到用户id，统计也没用
        if (!params.userId) {
            console.error('baiduStatistics without userId:', params)
            return
        }
        var label = '';
        if (urlTools.getUrlParam('env') == 1) {
            // 如果是ios，则直接改变参数
            if (params.type.indexOf("an_wd") != -1)
                params.type = params.type.replace("an_wd", "ios_wd")
            label = 'ios'
        } else if (isAndroidSdk() && urlTools.getUrlParam('versions') == 1 && urlTools.getUrlParam('position') == 1) {
            // 如果是2.7.0版的原生android
            label = 'android_2.7.0'
        } else if (isAndroidSdk()) {
            // 如果是2.7.0版之前的原生android
            label = 'android_old'
        } else if (window.KUKU_APP_JS) {
            // 如果是微端环境
            label = 'android_wd'
        } else {
            // h5环境
            if (params.type.indexOf("an_wd") != -1)
                params.type = params.type.replace("an_wd_", "h5_")
            label = 'h5'
        }
        console.log(params, '埋点的东东')
        // 事件类型，操作，标签，用户id
        _hmt.push(['_trackEvent', params.type, params.operation, label || params.label, params.userId])
    },
}

//alias
var toast = common.toast
var checkLogin = common.checkLogin
var wxCheckLogin = common.wxCheckLogin
var trimAll = common.trimAll
var trim = common.trim
var showLoading = common.showLoading
var hideLoading = common.hideLoading
var stringEmpty = common.stringEmpty
var listSource = common.listSource
var isMobileNumber = common.isMobileNumber
var request = common.request
var isMobile = common.isMobile
var isDivScrollToBottom = common.isDivScrollToBottom
var setCookie = common.setCookie
var getCookie = common.getCookie
var delCookie = common.delCookie
var isQQBrowser = common.isQQBrowser
var isSafari = common.isSafari
var isQQ = common.isQQ
var isAndroid = common.isAndroid
var isiOS = common.isiOS
var isPCWeixin = common.isPCWeixin
var isYLAPP = common.isYLAPP
var isWeibo = common.isWeibo
var isWeixin = common.isWeixin
var getURLVar = common.getURLVar
var allowQQlogin = common.allowQQLogin
var isIOSApp = common.isIOSApp
var isAndroidApp = common.isAndroidApp
var checkBuildRes = common.checkBuildRes
var envCheck = common.envCheck
var isAndroidSdk = common.isAndroidSdk
var isAndroidAppAndInstallWeixin = common.isAndroidAppAndInstallWeixin
var syncSid = common.syncSid
var recommendList = common.recommendList
var getNum = common.getNum
var getBanner = common.getBanner
var getVideoBanner = common.getVideoBanner
var init_videoSwiper = common.init_videoSwiper
var isKUKUEnv = common.isKUKUEnv
var isEmulator = common.isEmulator
var baiduStatistics = common.baiduStatistics
var checkTimes = common.checkTimes
var downloadProgress = common.downloadProgress
var appDownloading = common.appDownloading
var getAppName = common.getAppName
var baiduStatisticsForAndroid = common.baiduStatisticsForAndroid
var isIOSSdk = common.isIOSSdk
var getTopMsgText_11_11 = common.getTopMsgText_11_11
var selfClipboard = common.selfClipboard
var statByeGame = common.statByeGame
var isDev = common.isDev
var todayIsBirthday = common.todayIsBirthday
var getCustomerServiceQRCode = common.getCustomerServiceQRCode
var getRealNameGift = common.getRealNameGift

// 过滤器，游戏名称超过五个字只显示五个字
Vue.filter('setGameName', function (value) {
    if (value.length > 5) {
        return value.substring(0, 5) + "..."
    } else {
        return value
    }
})

function loadJS(url, callback) {
    var script = document.createElement('script'),
        fn = callback || function () {};
    script.type = 'text/javascript';
    //IE
    if (script.readyState) {
        script.onreadystatechange = function () {
            if (script.readyState == 'loaded' || script.readyState == 'complete') {
                script.onreadystatechange = null;
                fn();
            }
        };
    } else {
        //其他浏览器
        script.onload = function () {
            fn();
        };
    }
    if (!url.startsWith("http")) {
        // 能不能进去游戏就要改这里
        if (window.location.host === "local.aiyinli.cn" || window.location.host === "192.168.0.8") {
            url = host + "/public/YL-JS-SDK/src/js/" + url;
        } else {
            url = host + "/public/js/" + url;
        }
    }
    script.src = url;
    document.getElementsByTagName('head')[0].appendChild(script);
}

function getZoomRatio() {
    var doc = document,
        user_webset_font = 16;

    try {
        if (doc.documentElement.currentStyle) {
            user_webset_font = doc.documentElement.currentStyle['fontSize'];
        } else {
            user_webset_font = getComputedStyle(doc.documentElement, false)['fontSize'];
        }
    } catch (e) {
        console.log(e)
    }
    //return 16/parseFloat(user_webset_font)
    return 1;
};


function promptCenter() {
    if (!$(".prompt-1"))
        return
    var promptW = $(".prompt-1").width()
    var promptH = $(".prompt-1").height()
    // console.log('window.innerWidth/2-promptW/2=', window.innerWidth / 2 - promptW / 2)
    $(".prompt-1").css("left", window.innerWidth / 2 - promptW / 2)
    $(".prompt-1").css("margin-top", -1 * promptH / 2)
}

function remSet() {
    var docEl = document.documentElement,
        resizeEvt = 'orientationchange' in window ? 'orientationchange' : 'resize';
    var zoomRatio = getZoomRatio();
    width = window.innerWidth
    var outSize = 0
    var recalc = function () {
        /* var clientWidth = docEl.clientWidth;*/
        if (!width) return;
        var FontSize = zoomRatio * 16 * (width / 375)
        FontSize = FontSize >= 32 ? 16 : FontSize;
        docEl.style.fontSize = FontSize + 'px'; /*计算出来的结果表示 1rem等于16px*/
        outSize = FontSize
    };
    recalc()
    var realfz = ~~(+window.getComputedStyle(document.getElementsByTagName("html")[0]).fontSize.replace('px', '') * 10000) / 10000
    if (realfz != outSize) {
        document.getElementsByTagName("html")[0].style.cssText = 'font-size: ' + outSize * (outSize / realfz) + "px";
    }
    // console.log("REALFZ:" + realfz + ",SETFZ:" + outSize)
    return "REALFZ:" + realfz + ",SETFZ:" + outSize
};

// 当屏幕发现改变的时候，需要重新设置rem
window.onresize = function () {
    remSet()
}

function remInit() {
    /*根据高清方案设置的rem*/
    // console.log('redd');
    var zoomRatio = getZoomRatio();
    var dpr, rem, scale;
    var docEl = document.documentElement;
    var fontEl = document.createElement('style');
    var metaEl = document.querySelector('meta[name="viewport"]');
    var clWidth = width || document.body.clientWidth;

    dpr = window.devicePixelRatio || 1;

    /* docEl.style.fontSize = 20 * (clientWidth / 375) + 'px'*/

    rem = zoomRatio * clWidth * dpr * 20 / 375;

    scale = 1 / dpr;


    // 设置viewport，进行缩放，达到高清效果
    metaEl.setAttribute('content', 'width=' + dpr * width + ',initial-scale=' + scale + ',maximum-scale=' + scale + ', minimum-scale=' + scale + ',user-scalable=no');

    // 设置data-dpr属性，留作的css hack之用
    docEl.setAttribute('data-dpr', dpr);

    // 动态写入样式
    docEl.firstElementChild.appendChild(fontEl);
    fontEl.innerHTML = 'html{font-size:' + rem + 'px!important;}';
};


var weixinShare = {
    /**
     * 微信分享终结者
     * 页面参数比服务器配置的参数级别高，不传，则分享服务器默认配置
     * by wenxy
     * @appId:哪个公众号就填哪个公众号主体的APPID
     * @param url：分享发起的页面地址,获取浏览器当前地址
     * @param uid：分享收益人UID
     * @param shareLink:分享出去的URL地址
     * @param shareTitle:分享出去的标题
     * @param shareDesc:分享出去的描述
     * @param shareImgUrl:分享出去的图片地址
     */
    /* wxe084a46cc14fc690为测试的公众号，
    wxb73348bf9dcfbb83是引力的公众号
    */
    testAppId: 'wxb73348bf9dcfbb83',
    // wx0f7c851c19c1dea1
    config: function (appId, shareLink, shareTitle, shareDesc, shareImgUrl, uid, debug) {
        this.configExt(appId, shareLink, shareTitle, shareDesc, shareImgUrl, uid, debug, null, null, 0)
    },
    configByshareTypeId: function (appId, shareLink, shareTitle, shareDesc, shareImgUrl, uid, debug, shareTypeId) {
        this.configExt(appId, shareLink, shareTitle, shareDesc, shareImgUrl, uid, debug, null, null, shareTypeId)
    },
    configExt: function (appId, shareLink, shareTitle, shareDesc, shareImgUrl, uid, debug, gameKey, callbackUrl, shareTypeId) {
        var that = this
        request(weixinShareSDKConfigFinal, {
            appId: appId,
            shareLink: shareLink,
            shareTitle: shareTitle,
            shareDesc: shareDesc,
            shareImgUrl: shareImgUrl,
            url: encodeURIComponent(window.location.href.split('#')[0]),
            callbackUrl: callbackUrl || '',
            gameKey: gameKey || '',
            uid: uid || 0,
            shareTypeId: shareTypeId || 0,
        }, function (error) {
            return
        }, function (result) {
            // console.log(result, "wxConfig")
            if (result && result.code === 1) {
                var res = result.data
                // console.log(res, "weixinConfig")
                var that = this
                // wx.config配置
                wx.config({
                    debug: debug || false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
                    appId: res.appId, // 必填，公众号的唯一标识
                    timestamp: res.timestamp, // 必填，生成签名的时间戳
                    nonceStr: res.nonceStr, // 必填，生成签名的随机串
                    signature: res.signature, // 必填，签名
                    jsApiList: ['onMenuShareAppMessage', 'onMenuShareTimeline'] // 必填，需要使用的JS接口列表
                });
                // // 分享接口
                wx.ready(function () {
                    // 判断当前客户端版本是否支持指定JS接口
                    wx.checkJsApi({
                        jsApiList: ['onMenuShareAppMessage', 'onMenuShareTimeline'], // 需要检测的JS接口列表，所有JS接口列表见附录2,
                        success: function (res) {
                            // 以键值对的形式返回，可用的api值true，不可用为false
                            // 如：{"checkResult":{"chooseImage":true},"errMsg":"checkJsApi:ok"}
                            console.log(res, '接口检测成功')
                        }
                    });
                    //that.wxShareFunc(res.title, res.desc, res.link, res.imgUrl)
                    // console.log(res.title, res.desc, res.link, res.imgUrl)
                    // 分享到盆友或者是QQ
                    wx.onMenuShareAppMessage({
                        title: res.title, // 分享标题
                        desc: res.desc, // 分享描述
                        link: res.link, // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
                        imgUrl: res.imgUrl, // 分享图标
                    }, function (res) {
                        //这里是回调函数
                        // console.log(res, '分享给朋友成功')
                    });
                    //分享到盆友圈或者是qq空间
                    wx.onMenuShareTimeline({
                        title: res.title, // 分享标题
                        link: res.link, // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
                        imgUrl: res.imgUrl, // 分享图标
                    }, function (res) {
                        //这里是回调函数
                        // console.log(res, '分享盆友圈成功')
                    });
                });
                // 处理失败
                wx.error(function (res) {
                    // console.log(res, "微信接口调用失败")
                })
            } else {
                return
            }
        })
    }
}
