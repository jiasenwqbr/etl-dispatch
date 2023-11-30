$(document).ready(function () {
    // 隐藏动态表单
    hideForm();
    // 执行方式下拉列表
    getRunType();
    // 日志级别
    getTransLogLevel();
    // 执行方式下拉列表
    getRepository();
    // 任务分类
    getCategory();
    // 定时策略
    getQuartz();
    // 提交事件监听
    submitListener();
    // 监听文件下拉
    $('#transType').on('change', transTypeChange);
    //失败重试监听
    $('#transFailMethond').on('change', transFailMethondChange);
});

// 选择文件后把地址显示到输入框
$('#transFile').change(function () {
    var filePath = $(this).val();
    $('#location').val(filePath).blur();
    $('#transName').val(getFileName(filePath)).blur();
});

$("#transPath").click(function () {
    getAllScript(1);
});

$("#failRetryScriptPath").click(function () {
    getAllScript(null);
});

//获取所有脚本，当type不为空时，获取转换或者作业，为空时获取所有
function getAllScript(type) {
    var $transRepositoryId = $("#transRepositoryId").val();
    var $scriptType = $("#scriptType").val();
    if ($transRepositoryId && $transRepositoryId !== "") {
        var treeData = findTransRepTreeById($transRepositoryId, type == 1 ? $scriptType : null);
        if (treeData && treeData !== "") {
            var index = layer.open({
                type: 1,
                title: '请选择转换',
                area: ["400px", '60%'],
                skin: 'layui-layer-rim',
                content: '<div id="repositoryTree"></div>'
            });
            $('#repositoryTree').jstree({
                'core': {
                    'data': treeData
                },
                'plugins': ["search"]
            }).bind('select_node.jstree', function (event, data) {  //绑定的点击事件
                // jsTree实例对象
                var ins = data.instance;
                // 当前节点
                var transNode = data.node;
                // 是叶子节点才进入
                if (transNode.original.leaf) {
                    // 关闭弹窗
                    layer.close(index);
                    // 设置路径
                    if (type == null) {
                        $("#failRetryScriptPath").val(transNode.original.extra);
                        $('#failRetryScriptName').val(transNode.original.text).blur();
                        $("#failRetryScriptID").val(transNode.original.id);
                    } else {
                        $("#transPath").val(transNode.original.extra);
                        $('#transName').val(transNode.original.text).blur();
                    }
                }
            });
        } else {
            layer.msg("请等待资源库加载");
        }
    } else {
        layer.msg("请先选择资源库和采集类型");
    }
}

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


// 隐藏动态表单
function hideForm() {
    // 初始化隐藏动态表单
    var $sync = $('.sync-field');
    $sync.hide();
    $sync.find(":input").attr("disabled", true);
    $sync.find(":selected").attr("disabled", true);
}

function getRunType() {
    $.ajax({
        type: 'GET',
        async: false,
        url: '/dataCollection/enum/runType',
        data: {},
        success: function (data) {
            var list = data.result;
            for (var i = 0; i < list.length; i++) {
                $("#transType").append('<option value="' + list[i].code + '">' + list[i].value + '</option>');
            }
        },
        error: function () {
            //alert("请求失败！请刷新页面重试");
        },
        dataType: 'json'
    });
}

function getRepository() {
    $.ajax({
        type: 'GET',
        async: false,
        url: '/dataCollection/repository/findRepList',
        data: {},
        success: function (data) {
            var list = data.result;
            for (var i = 0; i < list.length; i++) {
                $("#transRepositoryId").append('<option value="' + list[i].id + '">' + list[i].repName + '</option>');
            }
        },
        error: function () {
            //alert("请求失败！请刷新页面重试");
        },
        dataType: 'json'
    });
}

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

function findTransRepTreeById(id, type) {
    var treeData = "";
    var url = '/dataCollection/repository/findJobRepTreeById?id=' + id;
    if (type != null) {
        url += "&type=" + type;
    }
    $.ajax({
        type: 'GET',
        async: false,
        url: url,
        data: {},
        success: function (data) {
            if (data.success) {
                treeData = data.result;
            }
        },
        error: function () {
            alert("请求失败！重新操作");
        },
        dataType: 'json'
    });
    return treeData;
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

function submitListener() {
    var icon = "<i class='fa fa-times-circle'></i> ";
    $("#RepositoryTransForm").validate({
        rules: {
            transType: {
                required: true
            },
            location: {
                required: true,
                checkFileType: "ktr"
            },
            transRepositoryId: {
                required: true
            },
            scriptType: {
                required: true
            },
            transPath: {
                required: true
            },
            transName: {
                required: true,
                maxlength: 100
                // remote: {
                //   type: 'POST',
                //   cache: false,
                //   url: '/dataCollection/trans/transNameExist.do',
                //    data: {
                //       transName: function () {
                //            return $("#transName").val();
                //        }
                //    }
                //}
            },
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
            transType: {
                required: icon + "请选择执行方式"
            },
            location: {
                required: icon + "请上传转换",
                checkFileType: icon + "只能上传ktr文件"
            },
            transRepositoryId: {
                required: icon + "请选择资源库"
            },
            transPath: {
                required: icon + "请选择转换"
            },
            transName: {
                required: icon + "请输入转换名称",
                maxlength: icon + "转换名称不能超过100个字符",
                remote: icon + "名称已存在"
            },
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
            var data = new FormData($("#RepositoryTransForm")[0]);
            //校验参数格式是否是json
            var transParams = data.get('scriptParams');
            var isJson = isJSON(transParams);
            if (isJson) {
                // 保存数据
                $.ajax({
                    type: 'POST',
                    async: false,
                    url: '/dataCollection/script/add',
                    data: data,
                    processData: false,
                    contentType: false,
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
            } else {
                alert(transParams)
                alert("转换参数格式有误");
            }

        }
    });
}

function cancel() {
    location.href = "./list.html";
}

function transTypeChange() {
    var type = $('#transType').val();
    // 隐藏动态表单
    hideForm();
    // 如果选择了具体的值
    if (type) {
        if (type === 'file') {
            var $fileData = $('[data-field="file"]');
            $fileData.show();
            $fileData.find(":input").attr("disabled", false);
            $fileData.find(":selected").attr("disabled", false);
        }
        if (type === 'rep') {
            var $repData = $('[data-field="rep"]');
            $repData.show();
            $repData.find(":input").attr("disabled", false);
            $repData.find(":selected").attr("disabled", false);
        }
    }
}

function transFailMethondChange() {
    var type = $('#transFailMethond').val();
    // 隐藏动态表单
    //hideForm();
    // 如果选择了具体的值
    if (type) {
        if (type === '0') {
            $("#failRetryTimeDiv").hide();
            $("#failRetryJobDiv").hide();
            $("#failRetryParams").hide();
        } else if (type === '1') {
            $("#failRetryTimeDiv").show();
            $("#failRetryJobDiv").hide();
            $("#failRetryParams").hide();
        } else if (type === '2') {
            $("#failRetryTimeDiv").hide();
            $("#failRetryJobDiv").show();
            $("#failRetryScript").show();
            $("#failRetryParams").show();
        }
    }
}

function getFileName(filePath) {
    var reg = new RegExp("\\\\", "g");
    var f = filePath.replace(reg, "/");
    var fileArr = f.split("/");
    var fileName = fileArr[fileArr.length - 1].toLowerCase();
    return fileName.substring(0, fileName.lastIndexOf("."));
}

function getFileType(filePath) {
    var reg = new RegExp("\\\\", "g");
    var f = filePath.replace(reg, "/");
    var fileArr = f.split("/");
    var fileNameArr = fileArr[fileArr.length - 1].toLowerCase().split(".");
    return fileNameArr[fileNameArr.length - 1];
}

function isJSON(str) {
    if (typeof str == 'string') {
        try {
            var obj = JSON.parse(str);
            if (typeof obj == 'object' && obj) {
                return true;
            } else {
                return false;
            }

        } catch (e) {
            console.log('error：' + str + '!!!' + e);
            return false;
        }
    }
    console.log('It is not a string!')
}

// 自定义校验
$.validator.addMethod("checkFileType", function (file, element, param) {
    return getFileType(file) === param
}, "只能上传ktr文件");

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
}

//判断字符是否为空的方法
function isEmpty(obj) {
    if (typeof obj == "undefined" || obj == null || obj == "") {
        return true;
    } else {
        return false;
    }
}