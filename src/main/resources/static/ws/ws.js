var BASE_URL = "http://" + window.location.host;
//var BASE_URL = "http://10.108.8.33";
//var OAUTH_URL = "http://" + window.location.hostname + ":8013";
var OAUTH_URL = "http://10.108.8.31:7001";
var SUCCESS_URL = "http://10.108.8.32:8090";
var FLOW_URL = "http://172.16.14.4:80";
var YEAR = "2019";
var MONTH = '06';
var azkabanName = "azkaban";
var azkabanPassword = "azkaban";
var ws = {
    util: {
        getQueryString: function (name) {
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
            var r = window.location.search.substr(1).match(reg);
            if (r != null)
                return decodeURI(r[2]);
            return null;
        },
        applyDate: function (value) {
            var date = new Date(value);// 时间戳为10位需*1000，时间戳为13位的话不需乘1000
            Y = date.getFullYear() + '-';
            M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date
                    .getMonth() + 1)
                + '-';
            D = (date.getDate() < 10 ? '0' + (date.getDate()) : date.getDate())
                + ' ';
            return Y + M + D;
        },
        checkNumber: function (value) {
            var reg = /^(0|[1-9]\d*|[1-9]\d*\.\d+|0\.\d*[1-9]\d*)$/;
            if (reg.test(value)) {
                return true;

            } else {
                return false;
            }
        },
        checkFloat: function (value, max) {
            return value.toFixed(max);
        },
        checkNeedParams: function (properties, propertiesContent) {
            var param = properties.split(",");
            var content = propertiesContent.split(",");
            for (var i = 0; i < param.length; i++) {
                var value = $("#" + param[i]).val();
                if (!value) {
                    layer.msg(content[i] + "必填！", {
                        icon: 2
                    });
                    return false;
                }
            }
            return true;
        },
        checkRedParams: function (properties) {
            var param = properties.split(",");
            for (var i = 0; i < param.length; i++) {
                if ($("#lable_" + param[i] + ":has(span)").length == 0) {
                    $("#lable_" + param[i]).append(
                        "<span style='color:red'>*</span>");
                    $("#" + param[i]).attr("lay-verify", "required");
                }

            }

        },
        startOut: function () {
            this.loginout();
            this.oauthLogout();
            store.remove("userInfo");
        },
        loginout: function () {
            var url = "http://" + window.location.host + "/logout"
            return request.post(url);
        },
        oauthLogout: function () {
            var num = Math.ceil(Math.random() * 1000);
            var script = document.createElement('script');// 创建一个script标签元素。
            script.setAttribute('type', 'text/javascript');// 设置script标签的type属性。
            script.setAttribute('src', OAUTH_URL + "/images/logout/0231.js?"
                + num);
            if (navigator.userAgent.indexOf("IE") >= 0) {
                // IE下的事件
                script.onreadystatechange = function () {
                    if (script
                        && (script.readyState == "loaded" || script.readyState == "complete")) {
                        window.location.reload(true);

                    }
                }
            } else {
                script.onload = function () {
                    window.location.reload(true);

                }
            }
            document.body.appendChild(script);
        },
        reload: function () {
            setTimeout(function () {

                window.location.reload();
            }, 1000);
        },
        parentReload: function () {
            setTimeout(function () {

                parent.location.reload();
            }, 2000);
        },
        close: function (layer, index) {
            setTimeout(function () {
                if (index) {
                    layer.close(index);
                } else {
                    layer.closeAll();
                }
            }, 500);
        }
    },
    uum: {
        getYear: function (status, process) {
            var url = BASE_URL + "/uum/busyear/getList?status=" + status;
            var data = {};
            return request.get(url, data, process);
        },
        getRegionList: function (process) {
            var url = BASE_URL + "/uum/region/getList";
            var data = {};
            return request.get(url, data, process);
        }

    },
    manage: {
        insertActivityProject: function (data, process) {
            var url = BASE_URL + "/baseDataManagerServer/activityProject/insertActivityProject";
            return request.post(url, JSON.stringify(data), process);
        },
        deleteActivityProjectById: function (id, process) {
            var url = BASE_URL + "/baseDataManagerServer/activityProject/deleteActivityProjectById?id=" + id;
            return request.get(url, {}, process);
        },
        insertActivityPlan: function (data, process) {
            var url = BASE_URL + "/baseDataManagerServer/activityPlan/insertActivityPlan";
            return request.post(url, JSON.stringify(data), process);
        },
        updateActivityPlan: function (data, process) {
            var url = BASE_URL + "/baseDataManagerServer/activityPlan/updateActivityPlan";
            return request.post(url, JSON.stringify(data), process);
        },
        insertCron: function (data, process) {
            var url = BASE_URL + "/baseDataManagerServer/activityCron/insertCron";
            return request.post(url, JSON.stringify(data), process);
        },
        updateCron: function (data, process) {
            var url = BASE_URL + "/baseDataManagerServer/activityCron/updateCron";
            return request.post(url, JSON.stringify(data), process);
        },
        deleteActivityPlanById: function (id, process) {
            var url = BASE_URL + "/baseDataManager/activityPlan/deleteActivityPlanById?id=" + id;
            return request.get(url, {}, process);
        },
        startActivityPlan: function (id, process) {
            var url = BASE_URL + "/baseDataManager/activityPlan/startActivityPlan?id=" + id;
            return request.get(url, {}, process);
        },
        endActivityPlan: function (id, process) {
            var url = BASE_URL + "/baseDataManagerServer/activityPlan/endActivityPlan?id=" + id;
            return request.get(url, {}, process);
        },
        deleteCron: function (id, process) {
            var url = BASE_URL + "/baseDataManagerServer/activityCron/deleteCron?id=" + id;
            return request.get(url, {}, process);
        },
        lookActivityPlanById: function (id, process) {
            var url = BASE_URL + "/baseDataManagerServer/activityPlan/lookActivityPlanById?id=" + id;
            return request.get(url, {}, process);
        },
        getActivityProjectById: function (id, process) {
            var url = BASE_URL + "/baseDataManagerServer/activityProject/getActivityProjectById?id=" + id;
            return request.get(url, {}, process);
        },
        getActivityProjectLogsByProjectName: function (projectName, process) {
            var url = BASE_URL + "/baseDataManagerServer/activityProject/getActivityProjectLogsByProjectName?projectName=" + projectName;
            return request.get(url, {}, process);
        },
        fetchexecflow: function (execid, process) {
            var url = BASE_URL + "/baseDataManagerServer/activityProject/fetchExecFlowLogs?execid=" + execid;
            return request.get(url, {}, process);
        }

    },
    tzps: {
        getYearList: function (status, process) {
            var url = UUM_URL + "/uum/busyear/getList?status=" + status;
            var data = {};
            return request.get(url, data, process);
        },
        findRepList: function () {
            var url = BASE_URL + "/baseDataManagerServer/sysConfCollectionTemplate/findRepList";
            return request.get(url);
        },
        getKettleScript: function (id, order) {
            var url = BASE_URL + "/dataCollection/repository/findRepTreegridById.do?id=" + id + "&order=" + order;
            return request.get(url);
        }
    }
}
