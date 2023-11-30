$(document).ready(function () {
    $("#id").val(getQueryVariable("transId"));
    // 日志级别
    getTransLogLevel();
    // 任务分类
    getCategory();
    // 定时策略
    getQuartz();
    // 提交事件监听
    submitListener();
    // 初始化数据
    initData();
});

function getTransLogLevel() {
    $.ajax({
        type: 'GET',
        async: false,
        url: '/dataCollection/enum/logLevel',
        data: {},
        success: function (data) {
            var list = data.result;
            for (var i = 0; i < list.length; i++) {
                $("#transLogLevel").append('<option value="' + list[i].code + '">' + list[i].value + '</option>');
            }
        },
        error: function () {
            //alert("请求失败！请刷新页面重试");
        },
        dataType: 'json'
    });
}

function getCategory() {
    $.ajax({
        type: 'GET',
        async: false,
        url: '/dataCollection/category/findCategoryList',
        data: {},
        success: function (data) {
            var list = data.result;
            for (var i = 0; i < list.length; i++) {
                $("#categoryId").append('<option value="' + list[i].id + '">' + list[i].categoryName + '</option>');
            }
        },
        error: function () {
            //alert("请求失败！请刷新页面重试");
        },
        dataType: 'json'
    });
}

function getQuartz() {
    $.ajax({
        type: 'GET',
        async: false,
        url: '/dataCollection/quartz/findQuartzList',
        data: {},
        success: function (data) {
            var list = data.result;
            for (var i = 0; i < list.length; i++) {
                $("#transQuartz").append('<option value="' + list[i].id + '">' + list[i].quartzDescription + '</option>');
            }
        },
        error: function () {
            //alert("请求失败！请刷新页面重试");
        },
        dataType: 'json'
    });
}

$.validator.setDefaults({
    highlight: function (element) {
        $(element).closest('.form-group').removeClass('has-success').addClass('has-error');
    },
    success: function (element) {
        element.closest('.form-group').removeClass('has-error').addClass('has-success');
    },
    errorElement: "span",
    errorPlacement: function (error, element) {
        if (element.is(":radio") || element.is(":checkbox") || element.is(":file") || element[0].id === "location") {
            error.appendTo(element.parent().parent());
        } else {
            error.appendTo(element.parent());
        }
    },
    errorClass: "help-block m-b-none",
    validClass: "help-block m-b-none"
});

//打开参数编辑页面
$("#transParams").click(function () {
    var transParams = $("#transParams").val();
    var datas = [];
    if (!isEmpty(transParams)) {
        var json2map = JSON.parse(transParams);
        for (var key in json2map) {
            var data = {};
            data["key"] = key;
            data["value"] = json2map[key];
            datas.push(data);
        }
    }

    var index = layer.open({
        type: 1,
        title: '参数编辑',
        area: ["450px", '40%'],
        skin: 'layui-layer-rim',
        content: '<div class="table-box" style="margin: 20px;"><div id="toolbar"><a  class="btn btn-primary" id="addRow" onclick="addRow()">添加行</a>&nbsp;<a class="btn btn-danger" id="removeRow" onclick="removeRow()">删除行</a></div><table id="exampleTable"></table><div id="toolbar"><a  class="btn btn-primary" id="addRow" onclick="saveParams()">保存</a></div>'
    });
    $('#exampleTable').bootstrapTable({
        data: datas,
        dataType: 'jsonp',
        columns: [{
            checkbox: true
        },
            {
                field: 'index',
                title: '序列',
                formatter: function (value, row, index) {
                    return row.index = index + 1; //返回行号
                }
            }, {
                field: 'key',
                title: 'key',
                formatter: function (value, row, index) {
                    var result = "<input id='" + index + "key' placeholder='key'" +
                        "class='form-control'  value='" + (value == undefined ? '' : value) + "'  onblur='getValues(" + index + ")'>";
                    return result //formatter 这里，里面的value一定要加，不然写值的时候会写不上去
                }
            },
            {
                field: 'value',
                title: 'value',
                formatter: function (value, row, index) {
                    var result = "<input id='" + index + "value' placeholder='value'" +
                        "class='form-control' value='" + (value == undefined ? '' : value) + "' onblur='getValues(" + index + ")'>";
                    return result
                }
            }]
    });
    $('#exampleTable').bootstrapTable("hideLoading");
});

function submitListener() {
    var icon = "<i class='fa fa-times-circle'></i> ";
    $("#RepositoryTransForm").validate({
        rules: {
            transQuartz: {
                required: true
            },
            syncStrategy: {
                checkRegex: '^((T\\+)\\d+)$'
            },
            transLogLevel: {
                required: true
            },
            transDescription: {
                maxlength: 500
            }
        },
        messages: {
            transQuartz: {
                required: icon + "请选择转换执行策略"
            },
            syncStrategy: {
                checkRegex: icon + "同步策略只能是T+N(N是正整数)"
            },
            transLogLevel: {
                required: icon + "请选择转换的日志记录级别"
            },
            transDescription: {
                maxlength: icon + "转换描述不能超过500个字符"
            }
        },
        // 提交按钮监听 按钮必须type="submit"
        submitHandler: function (form) {
            // 获取表单数据
            var data = {};
            $.each($("form").serializeArray(), function (i, field) {
                data[field.name] = field.value;
            });
            console.info($("form").serializeArray())
            // 保存数据
            $.ajax({
                type: 'PUT',
                async: false,
                url: '/dataCollection/script/update',
                data: JSON.stringify(data),
                processData: true,
                contentType: "application/json;charset=UTF-8",
                success: function (res) {
                    if (res.success) {
                        layer.msg('添加成功', {
                            time: 1000,
                            icon: 6
                        });
                        // 成功后跳转到列表页面
                        setTimeout(function () {
                            location.href = "./list.html";
                        }, 1000);
                    } else {
                        layer.msg(res.message, {icon: 2});
                    }
                },
                error: function () {
                    layer.msg(res.message, {icon: 5});
                },
                dataType: 'json'
            });
        }
    });
}

function cancel() {
    location.href = "./list.html";
}

function initData() {
    var transId = $("#id").val();
    //debugger;
    if (transId && transId !== "") {
        $.ajax({
            type: 'GET',
            async: false,
            url: '/dataCollection/script/getScriptDetail?id=' + transId,
            data: {},
            success: function (data) {
                if (data.success) {
                    var Trans = data.result;
                    if ('' != Trans.categoryId && typeof (Trans.categoryId) != "undefined" && Trans.categoryId != 0) {
                        $("#categoryId").find("option[value=" + Trans.categoryId + "]").prop("selected", true);
                    }
                    if ('' != Trans.scriptQuartz && typeof (Trans.scriptQuartz) != "undefined" && Trans.scriptQuartz != 0) {
                        $("#transQuartz").find("option[value=" + Trans.scriptQuartz + "]").prop("selected", true);
                    }
                    if ('' != Trans.scriptLogLevel && typeof (Trans.scriptLogLevel) != "undefined" && Trans.scriptLogLevel != 0) {
                        $("#transLogLevel").find("option[value=" + Trans.scriptLogLevel + "]").prop("selected", true);
                    }
                    $("#transDescription").val(Trans.scriptDescription);
                    $("#syncStrategy").val(Trans.syncStrategy);
                    $("#transParams").val(Trans.scriptParams);
                } else {
                    layer.msg(data.message, {icon: 2});
                }
            },
            error: function () {
                //layer.alert("请求失败！请刷新页面重试");
            },
            dataType: 'json'
        });
    } else {
        layer.msg("获取编辑信息失败", {icon: 5});
    }
}

// 自定义校验
$.validator.addMethod("checkRegex", function (value, element, param) {
    if (!value) {
        return true;
    }
    var regExp = new RegExp(param);
    return regExp.test(value);
}, "值与规则不匹配");

//添加行
function addRow() {
    var row =
        {
            "contactsName": "",
            "email": "",
            "telphone": "",
            "comment": ""
        };
    //append  追加到最后一行
    //prepend  新增到第一行
    $('#exampleTable').bootstrapTable('append', row);
    //更新行数据
    $('#exampleTable').bootstrapTable('updateRow', row);
    //定位到最后一行
    $('#exampleTable').bootstrapTable('scrollTo', 'bottom');
}

function getValues(index) {
    var rows = $('#exampleTable').bootstrapTable('getData');
    $.each(rows, function (i, row) {
        if (row.index == (index + 1)) {
            row.key = $("#" + index + "key").val();
            row.value = $("#" + index + "value").val();
            $('#exampleTable').bootstrapTable('updateRow', row);
            return false;
        }
    });
}

function removeRow() {
    var rows = $('#exampleTable').bootstrapTable('getSelections');//获取选中行
    if (rows.length == 0) {
        layer.msg("请选择要删除的数据");
        return;
    }
    var indexs = [];
    for (var i = 0; i < rows.length; i++) {
        indexs[i] = rows[i].index;
    }
//删除
    $('#exampleTable').bootstrapTable('remove', {
        field: 'index',
        values: indexs
    });

}

//保存参数，将参数转换为json格式
function saveParams() {
    var data = $('#exampleTable').bootstrapTable('getData', false);
    var obj = {};
    for (i = 0; i < data.length; i++) {
        obj[data[i].key] = data[i].value;
    }
    var params = JSON.stringify(obj)
    $("#transParams").val(params);
    layer.closeAll();
    layer.msg("保存成功！");
    // layer.msg($('#transParams').val)

}

//判断字符是否为空的方法
function isEmpty(obj) {
    if (typeof obj == "undefined" || obj == null || obj == "") {
        return true;
    } else {
        return false;
    }
}