var getPrize = new Vue({
    el: "#getPrize",
    data: {
        storeNone:0,
        count:0,
        isWeixin: false,
        bigUrl: "",
        payCredite: 200,    //要消耗的积分
        prizeList: [],
        userPrizeList:[],
        turnplate: {
            restaraunts: [], //大转盘奖品名称
            prizeImg: [], //大转盘奖品的图片
            colors: ["#FCC129", "#FFDF3D", "#FCC129", "#FFDF3D","#FCC129", "#FFDF3D", "#FCC129", "#FFDF3D"], //大转盘奖品区块对应背景颜色
            outsideRadius: 244, //大转盘外圆的半径
            textRadius: 200, //大转盘奖品位置距离圆心的距离
            insideRadius: 10, //大转盘内圆的半径
            startAngle: 0, //开始角度
            bRotate: false //false:停止;ture:旋转
        },
        showMoreInte: false,
        userInfo: {},
        isLogin: true,
        showDrawBox: false,
        showHisBox: false,
        showEntity: false,   //抽中实物奖显示输入地址
        showInputForm: false,
        goods:{
          id:0,
        },
        prizeActivityInfo:{},
        prizesInfo: {
            id: 0,
            productName: "",
            prizeId:'',
            winBgImgUrl:'',
            productIcon:'',
        },
        prizerInfo: {
            platform: 3,
            address: '',
            phone: '17607095238',
            userName: ''
        },
        showRecharge: false,
        userCreditsMsg: {   //用户积分信息
            credits: 10000000,
            ifSignIn: false,
            subscribeStatus: 2,
            signinRecord: {}
        },
        lastChance: 0,
        scrollInfo: [],
        prizeThings: [],
        maskText:'',
        mask:false,
        // 色子的路径集合
        diceUrl:['dice-1@2x.png','dice-2@2x.png','dice-3@2x.png','dice-4@2x.png','dice-5@2x.png','dice-6@2x.png'],
        showNormalPrize: false,  //显示普通中奖
        subInfo: false
    },
    methods: {
        // 获取奖品列表的东东
        getPrizeName: function (callback) {
            var that = this
            request(getPrizeList, {
                prizeId: urlTools.getUrlParam("prizeId")
            }, function (error) {
                console.log('error', error)
                return
            }, function (result) {
                console.log('get prize list -> ',result)
                if (result && result.code === 1) {
                    that.prizeList = result.data.content
                    console.log(result.data.content)
                    that.prizeList.forEach(function (val, index) {
                        console.log('for each val = ', val)
                        that.turnplate.restaraunts.push(val.productName)
                        that.turnplate.prizeImg.push(val.productIcon)
                        //var imgDom = "<img src='"+ val.img +"' alt='img"+ index +"' id='img"+ index +"' style='width:33px;display:none;' class='showImg' />"
                       // console.log(imgDom)
                       // $("#imgBox").append(imgDom)
                    })
                    that.payCredite = result.data.payCredite
                    callback && callback()
                } else {
                    toast(that, result.msg, 2)
                }
            })
        },
        // 可能是转盘在转的时候超不超时
        rotateTimeOut: function () {
            var that = this
            $('#wheelcanvas').rotate({
                angle: 0,
                animateTo: 2160,
                duration: 8000,
                callback: function () {
                    toast(that,'网络超时，请检查您的网络设置！',2);
                }
            });
        },
        //旋转转盘 item:奖品位置; txt：提示语;
        rotateFn: function (item, txt) {
            var that = this
            var angles = item * (360 / that.turnplate.restaraunts.length) - (360 / (that.turnplate.restaraunts.length * 2));
            if (angles < 270) {
                angles = 270 - angles;
            } else {
                angles = 360 - angles + 270;
            }
            $('#wheelcanvas').stopRotate();
            $('#wheelcanvas').rotate({
                angle: 0,
                animateTo: angles + 1800,
                duration: 8000,
                callback: function () {
                    // 根据获奖不同显示不同的窗口
                    that.showNormalPrize = true

                    $("#winPop").css({"background-image":'url('+that.prizesInfo.winBgImgUrl+')',"background-size":"100%,100%","background-repeat":"no-repeat"});
                    that.getGoods();
                    // 计算剩余抽奖的次数
                    // that.userCreditsMsg.credits = that.prizesInfo.credites
                    // that.lastChance = Math.floor(that.prizesInfo.credites / that.payCredite)
                    console.log('----->', that.turnplate)
                    that.turnplate.bRotate = !that.turnplate.bRotate;
                }
            });
        },
        // 点击抽奖的时候，获取随机数
        getPrize: function () {
            var that = this
            console.log(that.turnplate.bRotate);
            if (that.turnplate.bRotate) return;
            that.turnplate.bRotate = !that.turnplate.bRotate;
            that.prizesInfo = {}
            that.needPrize()
            // console.log(that.prizesInfo)
            //获取随机数(奖品个数范围内)
            // var item = that.rnd(1, that.turnplate.restaraunts.length);
            //奖品数量等于10,指针落在对应奖品区域的中心角度[252, 216, 180, 144, 108, 72, 36, 360, 324, 288]
            // that.rotateFn(item, that.turnplate.restaraunts[item - 1]);
            // console.log(item);
        },
        // 获取随机数
        rnd: function (n, m) {
            var random = Math.floor(Math.random() * (m - n + 1) + n);
            return random;
        },
        // 页面渲染的函数
        drawRouletteWheel: function () {
            var that = this
            // var canvas = document.getElementById("wheelcanvas");
            if ($("#wheelcanvas")[0].getContext) {
                //根据奖品个数计算圆周角度
                var arc = (2 * Math.PI) / that.turnplate.restaraunts.length;
                // var ctx = $("#wheelcanvas")[0].getContext("2d");
                var ctx = document.getElementById("wheelcanvas").getContext("2d")
                //在给定矩形内清空一个矩形
                ctx.clearRect(0, 0, 600, 600);
                //strokeStyle 属性设置或返回用于笔触的颜色、渐变或模式
                ctx.strokeStyle = "#FFFFFF";
                //font 属性设置或返回画布上文本内容的当前字体属性
                ctx.font = '18px Microsoft YaHei';

                for (var i = 0; i < that.turnplate.restaraunts.length; i++) {
                    var angle = that.turnplate.startAngle + i * arc;
                    ctx.fillStyle = that.turnplate.colors[i];
                    ctx.beginPath();
                    console.log('start',angle,'end', angle + arc, 'arc', arc)
                    //arc(x,y,r,起始角,结束角,绘制方向) 方法创建弧/曲线（用于创建圆或部分圆）
                    ctx.arc(300, 300, that.turnplate.outsideRadius, angle, angle + arc, false);
                    ctx.arc(300, 300, that.turnplate.insideRadius, angle + arc, angle, true);
                    ctx.closePath();
                    ctx.stroke();
                    ctx.fill();


                    //锁画布(为了保存之前的画布状态)
                    ctx.save();
                    //----绘制奖品开始----
                    ctx.fillStyle = "#66226F";
                    ctx.shadowColor = '#66226F';
                    var text = that.turnplate.restaraunts[i];
                    var oldText = that.turnplate.restaraunts[i];
                    var line_height = 17;
                    //translate方法重新映射画布上的 (0,0) 位置
                    ctx.translate(300 + Math.cos(angle + arc / 2) * that.turnplate.textRadius, 300 + Math.sin(angle + arc / 2) * that.turnplate.textRadius);

                    //rotate方法旋转当前的绘图
                    ctx.rotate(angle + arc / 2 + Math.PI / 2);

                    /** 下面代码根据奖品类型、奖品名称长度渲染不同效果，如字体、颜色、图片效果。(具体根据实际情况改变) **/
                    ctx.fillText(text, -ctx.measureText(text).width / 2, 0);
                    ctx.restore();
                    //----绘制奖品结束----
                }

                ctx.save();
                ctx.lineWidth = 10;
                ctx.fillStyle = that.turnplate.colors[i];
                ctx.beginPath();
                ctx.arc(300, 300, that.turnplate.outsideRadius, 0, 2 * Math.PI, false);
                ctx.stroke();
                //锁画布(为了保存之前的画布状态)
                ctx.restore();

                console.log(that.turnplate.prizeImg[0],that.turnplate.prizeImg[1],that.turnplate.prizeImg[2])
                console.log(that.turnplate.prizeImg[3],that.turnplate.prizeImg[4],that.turnplate.prizeImg[5])
                console.log(that.turnplate.prizeImg[6],that.turnplate.prizeImg[7])

                console.log(that.turnplate.restaraunts[0],that.turnplate.restaraunts[1],that.turnplate.restaraunts[2])
                console.log(that.turnplate.restaraunts[3],that.turnplate.restaraunts[4],that.turnplate.restaraunts[5])
                console.log(that.turnplate.restaraunts[6],that.turnplate.restaraunts[7])

                var img0 = new Image()
                img0.src=that.turnplate.prizeImg[0]
                img0.onload = function(){
                    var ctx1 = document.getElementById("wheelcanvas").getContext("2d")
                    var angle = that.turnplate.startAngle + 0 * arc;
                    ctx.save()
                    var tx = 486;
                    var ty = 334;
                    ctx1.translate(tx, ty);
                    ctx1.rotate(angle + arc / 2 + Math.PI / 2);
                    ctx1.drawImage(img0, 0, 0,80,80);
                    // ctx.restore();
                    console.log('=======>img0 load.', angle + arc / 2 + Math.PI / 2, tx, ty, angle,that.turnplate.startAngle);
                    ctx.restore();
                    var img1 = new Image()
                    img1.src=that.turnplate.prizeImg[1]
                    img1.onload = function(){
                        var ctx1 = document.getElementById("wheelcanvas").getContext("2d")
                        var angle = that.turnplate.startAngle + 1 * arc;
                        ctx.save()
                        var tx = 408;
                        var ty = 456;
                        ctx1.translate(tx, ty);
                        ctx1.rotate(angle + arc / 2 + Math.PI / 2);
                        ctx1.drawImage(img1, 0, 0,80,80);
                        console.log('=======>img1 load.', angle + arc / 2 + Math.PI / 2, tx, ty,angle,that.turnplate.startAngle)
                        ctx.restore()
                        var img2 = new Image()
                        img2.src=that.turnplate.prizeImg[2]
                        // img.crossOrigin="anonymous";
                        img2.onload = function(){
                            var ctx1 = document.getElementById("wheelcanvas").getContext("2d")
                            var angle = that.turnplate.startAngle + 2 * arc;

                            ctx.save()
                            var tx = 264;
                            var ty = 488;
                            ctx1.translate(tx, ty);
                            ctx1.rotate(angle + arc / 2 + Math.PI / 2);
                            ctx1.drawImage(img2, 0, 0,80,80);
                            console.log('=======>img2 load.', angle + arc / 2 + Math.PI / 2, tx, ty,angle,that.turnplate.startAngle)
                            ctx1.restore();
                            var img3 = new Image()
                            img3.src=that.turnplate.prizeImg[3]
                            // img.crossOrigin="anonymous";
                            img3.onload = function(){
                                var ctx1 = document.getElementById("wheelcanvas").getContext("2d")
                                var angle = that.turnplate.startAngle + 3 * arc;
                                ctx.save()
                                var tx = 142;
                                var ty = 408;
                                ctx1.translate(tx, ty);
                                ctx1.rotate(angle + arc / 2 + Math.PI / 2);
                                ctx1.drawImage(img3, 0, 0,80,80);
                                console.log('=======>img3 load.', angle + arc / 2 + Math.PI / 2, tx, ty,angle,that.turnplate.startAngle)
                                ctx1.restore();
                                var img4 = new Image()
                                img4.src=that.turnplate.prizeImg[4]
                                // img.crossOrigin="anonymous";
                                img4.onload = function(){
                                    var ctx1 = document.getElementById("wheelcanvas").getContext("2d")
                                    var angle = that.turnplate.startAngle + 4 * arc;
                                    ctx1.save()
                                    var tx = 108;
                                    var ty = 264;
                                    ctx1.translate(tx, ty);
                                    ctx1.rotate(angle + arc / 2 + Math.PI / 2);
                                    ctx1.drawImage(img4, 0, 0,80,80);
                                    console.log('=======>img4 load.', angle + arc / 2 + Math.PI / 2, tx, ty,angle,that.turnplate.startAngle)
                                    ctx1.restore();

                                    var img5 = new Image()
                                    img5.src=that.turnplate.prizeImg[5]
                                    // img.crossOrigin="anonymous";
                                    img5.onload = function(){
                                        var ctx1 = document.getElementById("wheelcanvas").getContext("2d")
                                        var angle = that.turnplate.startAngle + 5 * arc;
                                        ctx1.save()
                                        var tx = 192;
                                        var ty = 142;
                                        ctx1.translate(tx, ty);
                                        ctx1.rotate(angle + arc / 2 + Math.PI / 2);
                                        ctx1.drawImage(img5, 0, 0,80,80);
                                        console.log('=======>img5 load.', angle + arc / 2 + Math.PI / 2, tx, ty,angle,that.turnplate.startAngle)
                                        ctx1.restore();

                                        var img6 = new Image()
                                        img6.src=that.turnplate.prizeImg[6]
                                        // img.crossOrigin="anonymous";
                                        img6.onload = function(){
                                            var ctx1 = document.getElementById("wheelcanvas").getContext("2d")
                                            var angle = that.turnplate.startAngle + 6 * arc;
                                            ctx1.save()
                                            var tx = 334;
                                            var ty = 110;
                                            ctx1.translate(tx, ty);
                                            ctx1.rotate(angle + arc / 2 + Math.PI / 2);
                                            ctx1.drawImage(img6, 0, 0,80,80);
                                            console.log('=======>img6 load.', angle + arc / 2 + Math.PI / 2, tx, ty,angle,that.turnplate.startAngle)
                                            ctx1.restore();

                                            var img7 = new Image()
                                            img7.src=that.turnplate.prizeImg[7]
                                            // img.crossOrigin="anonymous";
                                            img7.onload = function(){
                                                var ctx1 = document.getElementById("wheelcanvas").getContext("2d")
                                                var angle = that.turnplate.startAngle + 7 * arc;
                                                ctx1.save()
                                                var tx = 458;
                                                var ty = 192;
                                                ctx1.translate(tx, ty);
                                                ctx1.rotate(angle + arc / 2 + Math.PI / 2);
                                                ctx1.drawImage(img4, 0, 0,80,80);
                                                console.log('=======>img7 load.', angle + arc / 2 + Math.PI / 2, tx, ty,angle,that.turnplate.startAngle)
                                                ctx1.restore();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        },
        getDeviceId(){
            return urlTools.getUrlParam("deviceId") || getCookie("deviceId");
        },
        //跑马灯
        getEPrizeRecord:function(){
            var that = this
            request(getExpensivePrizeRecord,{platform:3,page:1,pageSize:100},function(error){
                return
            },function(result){
                console.log(result,"跑马灯")
                if(result && result.code === 1){
                    that.scrollInfo = result.data.list

                    var mt = -1
                    setInterval(function(){
                        if(mt === -1){
                            $('#prizePer').append('<li>恭喜'+ that.scrollInfo[0].phone +'获得'+ that.scrollInfo[0].prizeName +'</li>')
                            mt = 0
                        }
                        mt -= 2.1875
                        if(mt < -$('#prizePer').height()/16 + 2.1875){
                            mt = 0
                            $('#prizePer').css({'margin-top':mt+'rem','transition':'none'})
                            // console.log($('#prizePer').height())
                        }else{
                            $('#prizePer').css({'margin-top':mt+'rem','transition':'all 0.5s'})
                        }
                    },1000)

                    console.log(that.scrollInfo)
                }
            })
        },
        // 抽奖的动作
        needPrize: function(){
            var that = this
            request(needPrize,{prizeId: urlTools.getUrlParam("prizeId")},function(error){
                console.log(error);
                return
            },function(result){
                console.log(result.data,"抽奖的动作")
                if(result && result.code === 1){
                    that.prizesInfo =  result.data
                    that.count = result.data.count
                    console.log('--->', that.prizesInfo)
                    var productName = '',productIdIndex = 0
                    that.prizeList.forEach(function(val,index){
                        if(val.id === that.prizesInfo.id){
                            productName = val.productName
                            productIdIndex = index
                            // console.log(val.id)
                        }
                        // console.log(that.prizesInfo)
                    })
                    console.log('productIdIndex=',productIdIndex)
                    that.rotateFn(productIdIndex+1,productName)
                }else if(result && result.code === 40005){
                    toast(that, '今天抽奖次数已用完，明天再来吧！', 4);
                    that.turnplate.bRotate = !that.turnplate.bRotate;
                }
            })
        },
        // 获取屏幕可视区，赋值给盒子

        getUserPrize: function(){
            var that = this;
            request(getUserPrizeList,{prizeId: that.prizeActivityInfo.id},function(error){
                console.log(error)
                return
            },function(result){
                if(result && result.code === 1){
                    that.userPrizeList = result.data.content;
                    that.showHisBox = true
                    console.log(that.userPrizeList);
                }
            })
        },
        getScreenSize: function(){
            var sw = $(window).width()
            var sh = $(window).height()
            $(".isMoreIntePage").css({width:sw+"px",height:sh+"px"})
        },
        getGoods: function(){
            var that = this;
            request(getGoods,{productId: that.prizesInfo.id, prizeId: that.prizeActivityInfo.id},function(error){
                console.log(error)
                return
            },function(result){
                if(result && result.code === 1){
                    that.goods = result.data;
                    that.count = result.data.count
                }
                if(result && result.code === 40004){
                    that.storeNone = 1;
                }
            })
        },
        goToWardPage: function(){
            var that = this;
            console.log('goToWardPage', that.goods);
            if(!that.goods.targetUrl) return ;
            window.location.href = that.goods.targetUrl;
        },
        // 用户签到的时候
        // 返回上一级
        back: function () {
            if (window.history.length > 2) {
                window.history.back();
            } else {
                window.location.href = "/";
            }
        }
    },
    created: function () {
        var that = this
        remSet()

        // set cookies
        if(urlTools.getUrlParam("appKey") && urlTools.getUrlParam("appKey").length > 0) {
            setCookie("appKey", urlTools.getUrlParam("appKey"), 10);
        }
        if(urlTools.getUrlParam("deviceId") && urlTools.getUrlParam("deviceId").length > 0) {
            setCookie("deviceId", urlTools.getUrlParam("deviceId"), 10)
        }else{
            // 新生成deviceId
            window.location.href= host + '/prize/index'+window.location.search
        }

        request(getPrizeActivityInfo, {
            prizeId: urlTools.getUrlParam("prizeId")
        }, function (error) {
            console.log('error', error)
            return
        }, function (result) {
            if (result && result.code === 1) {
                that.prizeActivityInfo = result.data
                that.count = result.data.count
                console.log('get getPrizeActivityInfo  -> ', that.prizeActivityInfo)
            } else {
                toast(that, result.msg, 2)
            }
        })
    },
    mounted: function () {
        var that = this
        that.isWeixin = isWeixin()
        if(isWeixin()){
            $("main").css({"background-image":'url(../images/wx-bg.png)',"background-size":"100% 100%"});
            $("body").css("background", "#7f5c6b");
        } else {
            $("main").css({"background-image":'url(../images/zfb-bg.png)',"background-size":"100% 100%"});
            $("body").css("background", "#0f6dda");
        }
        this.getPrizeName(this.drawRouletteWheel)
        // 跑马灯
        // that.getEPrizeRecord()
        this.getScreenSize()
        // this.getUserCreditsMsg()
        // 设置图片的位置
        if($(window).width()>=414){
            $("main .giftImg").css('left','19%')
        }
    }
})
