/* spig.js */
//右键菜单

//鼠标在消息上时
jQuery(document).ready(function ($) {
    $("#message").hover(function () {
        $("#message").fadeTo("100", 1);
    });
});


//鼠标在上方时
jQuery(document).ready(function ($) {
    //$(".mumu").jrumble({rangeX: 2,rangeY: 2,rangeRot: 1});
    $(".mumu").mouseover(function () {
        $(".mumu").fadeTo("300", 0.3);
        msgs = ["我是隐身瑶，你看不到我", "瑶瑶要隐身了", "别动手动脚的，把手拿开！", "把手拿开我才出来！"];
        var i = Math.floor(Math.random() * msgs.length);
        showMessage(msgs[i]);
    });
    $(".mumu").mouseout(function () {
        $(".mumu").fadeTo("300", 1)
    });
});

//开始
jQuery(document).ready(function ($) {
    var now = (new Date()).getHours();
    if (now > 0 && now <= 6) {
        showMessage(' 瑶宝在线提醒您睡觉啦，再不睡明天起不来哦。', 6000);
    } else if (now > 7 && now <= 9) {
        showMessage(' 早上好，起床啦！瑶宝已经起来了哦！', 6000);
    } else if (now > 11 && now <= 13) {
        showMessage(' 中午了，瑶宝提醒您吃饭啦！', 6000);
    } else if (now > 15 && now <= 18) {
        showMessage(' 下午茶时光！我小瑶又来了！', 6000);
    } else if (now > 18 && now <= 20) {
        showMessage(' 小瑶提醒您晚上少吃点，不然容易胖嘟嘟哦', 6000);
    } else {
        showMessage(' 我是你们的新朋友瑶宝，你们也可以叫我小瑶', 6000);
    }
    $(".spig").animate({
            top: $(".spig").offset().top + 600,
            left: 50
        },
        {
            queue: false,
            duration: 1000
        });
});


//无聊讲点什么
jQuery(document).ready(function ($) {

    window.setInterval(function () {
        msgs = ["我是瑶宝，英文名字叫Baby Yao", "陪我聊天吧！小瑶一直在线哦", "我叫小瑶，但是我可不会说谎哦",
            "主人比较忙呀，我是你们的朋友瑶宝。",
            "目前就这些啦，更多功能等待主人开发哦", "好无聊哦，你们都不陪我聊天！", "这里有瑶宝为您推荐的歌曲哦", "^%#&*!@*(&#)(!)(", "我可爱吧！嘻嘻!~^_^!~~", "1+1=？这个问题好像有点难，小瑶不会哎", "从前有座山，山上有座庙..."];
        var i = Math.floor(Math.random() * msgs.length);
        showMessage(msgs[i], 5000);
    }, 10000);
});
/*

var spig_top = 450;
//滚动条移动
jQuery(document).ready(function ($) {
    var f = $(".spig").offset().top;
    $(window).scroll(function () {
        $(".spig").animate({
            top: $(window).scrollTop() + f +600
        },
		{
		    queue: false,
		    duration: 1000
		});
    });
});*/

//鼠标点击时
jQuery(document).ready(function ($) {
    var stat_click = 0;
    $(".mumu").click(function () {
        if (!ismove) {
            stat_click++;
            if (stat_click > 4) {
                //msgs = ["你有完没完呀？", "你已经摸我" + stat_click + "次了", "非礼呀！救命！OH，My ladygaga"];
                //var i = Math.floor(Math.random() * msgs.length);
                //showMessage(msgs[i]);
            } else {
                //msgs = ["筋斗云！~我飞！", "我跑呀跑呀跑！~~", "别摸我，大男人，有什么好摸的！", "惹不起你，我还躲不起你么？", "不要摸我了，我会告诉老婆来打你的！", "干嘛动我呀！小心我咬你！"];
                //var i = Math.floor(Math.random() * msgs.length);
                //showMessage(msgs[i]);
            }
            //s = [0.1, 0.2, 0.3, 0.4, 0.5, 0.6,0.7,0.75,-0.1, -0.2, -0.3, -0.4, -0.5, -0.6,-0.7,-0.75];
            //var i1 = Math.floor(Math.random() * s.length);
            //var i2 = Math.floor(Math.random() * s.length);
            //$(".spig").animate({
            //left: document.body.offsetWidth/2*(1+s[i1]),
            //top:  document.body.offsetHeight/2*(1+s[i1])
            //},
            //{
            //    duration: 500,
            //   complete: showMessage(msgs[i])
            //});
        } else {
            ismove = false;
        }
    });
});

//显示消息函数
function showMessage(a, b) {
    if (b == null) b = 10000;
    jQuery("#message").hide().stop();
    jQuery("#message").html(a);
    jQuery("#message").fadeIn();
    jQuery("#message").fadeTo("1", 1);
    jQuery("#message").fadeOut(b);
};

//拖动
