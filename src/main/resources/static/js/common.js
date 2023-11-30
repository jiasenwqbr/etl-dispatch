// /**
//  * 原->改
//  * @type {string}
//  */
var BASE_URL = "http://" + window.location.host;

var FLOW_URL5 = BASE_URL;//数据源
/**
 * 原->改
 * @type {string}
 */
//徐光辉的接口调数据分层
// var FLOW_URL6_FUWU = BASE_URL + '/base-data-manager-server';
var FLOW_URL6_FUWU = BASE_URL;

function GetRequest() {
	var url = location.search; //获取url中"?"符后的字串
//	console.log(location.search,'location.search');
	
	var theRequest = new Object();
	if (url.indexOf("?") != -1) {
		var str = url.substr(1);
		strs = str.split("&");
		for (var i = 0; i < strs.length; i++) {
			theRequest[strs[i].split("=")[0]] = unescape(strs[i].split("=")[1]);
		}
	}
	return theRequest;
}

//自动加逗号¥
function formatNum(str) {
	if (str) {
		var newStr = "";
		var count = 0;
		if (str.indexOf(".") == -1) {
			for (var i = str.length - 1; i >= 0; i--) {
				if (count % 3 == 0 && count != 0) {
					newStr = str.charAt(i) + "," + newStr;
				} else {
					newStr = str.charAt(i) + newStr;
				};
				count++;
			};
			str = newStr; //自动补小数点后两位
			return str;
		} else {
			for (var i = str.indexOf(".") - 1; i >= 0; i--) {
				if (count % 3 == 0 && count != 0) {
					newStr = str.charAt(i) + "," + newStr;
				} else {
					newStr = str.charAt(i) + newStr; //逐个字符相接起来
				}
				count++;
			}
			str = newStr + (str + "00").substr((str + "00").indexOf("."), 3);
			return str;
		}
	}
};

function initMoney(account, dividend, decimal) {
	if (!dividend) {
		dividend = 10000;
	}
	if (decimal == 0) {
		decimal = 0;
	} else {
		if (!decimal) {
			decimal = 2;
		}
	}

	if (account) {
		account = account / dividend;
		account = account.toFixed(decimal);
		return account;
	} else {
		return "";
	}
}
// 关闭弹窗
function closeFun() {
	var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
	parent.layer.close(index); //再执行关闭  
};
