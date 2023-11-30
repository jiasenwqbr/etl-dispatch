$(document).ready(function () {
    // var code = parent.strs[1].split('=')[1]
    // parent.dynamicMenu(code)
    // 加载搜索选项
    getCategory();
    // 加载列表
    getTransList();
    // 按钮绑定
    bindButton();
});

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

function getTransList() {
    $('#transList').bootstrapTable({
        url: '/dataCollection/script/findScriptListByPage',            //请求后台的URL（*）
        method: 'POST',            //请求方式（*）
        toolbar: '#toolbar',        //工具按钮用哪个容器
        striped: true,                      //是否显示行间隔色
        cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
        pagination: true,                   //是否显示分页（*）
        sortable: false,                     //是否启用排序
        sortOrder: "asc",                   //排序方式
        queryParams: queryParams,//传递参数（*）
        sidePagination: "server",           //分页方式：client客户端分页，server服务端分页（*）
        pageNumber: 1,                       //初始化加载第一页，默认第一页
        pageSize: 10,                       //每页的记录行数（*）
        pageList: [5, 25, 50, 100],        //可供选择的每页的行数（*）
        search: false,                       //是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
        strictSearch: false,
        showColumns: false,                  //是否显示所有的列
        showRefresh: false,                  //是否显示刷新按钮
        minimumCountColumns: 2,             //最少允许的列数
        clickToSelect: false,                //是否启用点击选中行
        // height: 500,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
        uniqueId: "id",                     //每一行的唯一标识，一般为主键列
        showToggle: false,                    //是否显示详细视图和列表视图的切换按钮
        cardView: false,                    //是否显示详细视图
        smartDisplay: false,
        detailView: false,                   //是否显示父子表
        singleSelect: true, //开启单选,想要获取被选中的行数据必须要有该参数
        idField: 'id', //指定主键
        responseHandler: function (res) {
            return {
                "total": res.result.total,//总页数
                "rows": res.result.list//数据列表

            };
        },
        columns: [
            {
                checkbox: true  //第一列显示复选框
            }, {
                field: 'id',
                title: '转换编号',
                visible: false
            }, {
                field: 'scriptName',
                title: '转换名称'
            },
            {
                field: 'categoryName',
                title: '任务分类'
            },
            {
                field: 'scriptDescription',
                title: '转换描述'
            }, {
                field: 'executeType',
                title: '执行方式'
            },
            {
                field: 'scriptType',
                title: '采集类型',
                formatter: scriptType
            },
            {
                field: 'scriptPath',
                title: '转换路径'
            },
            {
                field: 'quartzDescription',
                title: '执行策略'
            },
            {
                field: 'syncStrategy',
                title: '同步策略'
            }, {
                field: 'scriptLogLevel',
                title: '日志级别'
            }, {
                field: 'scriptStatus',
                title: '转换状态',
                formatter: Formatter
            }, {
                field: 'operate',
                title: '操作',
                width: 200,
                align: 'center',
                valign: 'middle',
                formatter: actionFormatter
            }]
    });
}

function queryParams(params) {
    return JSON.stringify({
        rows: params.limit,
        page: (params.offset / params.limit) + 1,
        query: {
            categoryId: $("#categoryId").val(),
            scriptName: $("#transName").val()
        }
    });
}

function actionFormatter(value, row, index) {
    if (row.scriptStatus === '1') {
        return [
            '<a class="btn btn-danger btn-xs" id="stop" type="button" data-id="' + row.id + '">&nbsp;停止</a>',
            '&nbsp;&nbsp;',
            '<a class="btn btn-info btn-xs" id="loggingEventListener" type="button" data-id="' + row.id + '">&nbsp;日志监听</a>'
        ].join('');
    } else if (row.scriptStatus === '2') {
        return [
            '<a class="btn  btn-xs" id="start" type="button" data-id="' + row.id + '">&nbsp;启动</a>',
            '&nbsp;&nbsp;',
            '<a class="btn  btn-xs" id="edit" type="button" data-id="' + row.id + '">&nbsp;编辑</a>',
            '&nbsp;&nbsp;',
            '<a class="btn  btn-xs" id="delete" type="button" data-id="' + row.id + '">&nbsp;删除</a>'
        ].join('');
    } else {
        return [
            '<a class="btn  btn-xs" id="edit" type="button" data-id="' + row.id + '">&nbsp;编辑</a>',
            '&nbsp;&nbsp;',
            '<a class="btn  btn-xs" id="delete" type="button" data-id="' + row.id + '">&nbsp;删除</a>'
        ].join('');
    }
}

function Formatter(value, row, index) {
    if (row.scriptStatus === '1') {
        return "运行中";
    } else if (row.scriptStatus === '2') {
        return "未启动";
    } else {
        return "未启动";
    }
}

function scriptType(value, row, index) {
    if (row.scriptType === '1') {
        return "转换";
    } else if (row.scriptType === '0') {
        return "作业";
    } else {
        return "-";
    }
}

function search() {
    $('#transList').bootstrapTable('refresh');
}


function transNameFormatter(value, row, index) {
    if (value.length > 15) {
        var newValue = value.substring(0, 14);
        return newValue + "……";
    } else {
        return value;
    }
}

function bindButton() {
    // 编辑
    $('#transList').delegate('#edit', 'click', function (e) {
        var $target = $(e.currentTarget);
        var transId = $target.data('id');
        location.href = "./edit.html?transId=" + transId;
    });

    // 删除
    $('#transList').delegate('#delete', 'click', function (e) {
        var $target = $(e.currentTarget);
        var transId = $target.data('id');
        layer.confirm('确定删除该转换？', {
                btn: ['确定', '取消']
            },
            function (index) {
                layer.close(index);
                $.ajax({
                    type: 'DELETE',
                    async: true,
                    url: '/dataCollection/script/delete?id=' + transId,
                    success: function (data) {
                        if (data.success) {
                            location.replace(location.href);
                        } else {
                            layer.alert(data.message, {icon: 5});
                        }
                    },
                    error: function () {
                        layer.alert("系统出现问题，请联系管理员");
                    },
                    dataType: 'json'
                });
            },
            function () {
                layer.msg('取消操作');
            }
        );
    });

    // 单个任务启动
    $('#transList').delegate('#start', 'click', function (e) {
        var $target = $(e.currentTarget);
        var transId = $target.data('id');
        layer.confirm(
            '确定启动该转换？',
            {btn: ['确定', '取消']},
            function (index) {
                layer.close(index);
                $.ajax({
                    type: 'GET',
                    async: true,
                    url: '/dataCollection/script/startScript?id=' + transId,
                    data: {},
                    success: function (data) {
                        if (data.success) {
                            location.replace(location.href);
                        } else {
                            layer.alert(data.message, {icon: 5});
                        }
                    },
                    error: function () {
                        alert("系统出现问题，请联系管理员");
                    },
                    dataType: 'json'
                });
            },
            function () {
                layer.msg('取消操作');
            }
        );
    });

    // 单个任务停止
    $('#transList').delegate('#stop', 'click', function (e) {
        var $target = $(e.currentTarget);
        var transId = $target.data('id');
        layer.confirm(
            '确定停止该转换？',
            {btn: ['确定', '取消']},
            function (index) {
                layer.close(index);
                $.ajax({
                    type: 'GET',
                    async: true,
                    url: '/dataCollection/script/stopScript?id=' + transId,
                    data: {},
                    success: function (data) {
                        if (data.success) {
                            location.replace(location.href);
                        } else {
                            layer.alert(data.message, {icon: 5});
                        }
                    },
                    error: function () {
                        alert("系统出现问题，请联系管理员");
                    },
                    dataType: 'json'
                });
            },
            function () {
                layer.msg('取消操作');
            }
        );
    });

    // 启动全部停止的任务
    $('#startAll').on('click', function (e) {
        layer.confirm(
            '确定启动全部已停止的转换？',
            {btn: ['确定', '取消']},
            function (index) {
                layer.close(index);
                $.ajax({
                    type: 'GET',
                    async: true,
                    url: '/dataCollection/script/startAllScript',
                    data: {},
                    success: function (data) {
                        if (data.success) {
                            location.replace(location.href);
                        } else {
                            layer.alert(data.message, {icon: 5});
                        }
                    },
                    error: function () {
                        alert("系统出现问题，请联系管理员");
                    },
                    dataType: 'json'
                });
            },
            function () {
                layer.msg('取消操作');
            }
        );
    });

    // 启动全部停止的任务
    $('#stopAll').on('click', function (e) {
        layer.confirm(
            '确定停止全部已启动的转换？',
            {btn: ['确定', '取消']},
            function (index) {
                layer.close(index);
                $.ajax({
                    type: 'GET',
                    async: true,
                    url: '/dataCollection/script/stopAllScript',
                    data: {},
                    success: function (data) {
                        if (data.success) {
                            location.replace(location.href);
                        } else {
                            layer.alert(data.message, {icon: 5});
                        }
                    },
                    error: function () {
                        alert("系统出现问题，请联系管理员");
                    },
                    dataType: 'json'
                });
            },
            function () {
                layer.msg('取消操作');
            }
        );
    });

    // 执行一次
    $('#executeOnce').on('click', function (e) {
        var row = $("#transList").bootstrapTable('getSelections');
        if (row.length == 0) {
            layer.msg('请选择转换任务！');
            return;
        }
        var transId = row[0].id
        layer.confirm(
            '是否立即执行一次？',
            {btn: ['确定', '取消']},
            function (index) {
                layer.close(index);
                $.ajax({
                    type: 'GET',
                    async: true,
                    url: '/dataCollection/script/startScript?executeOnce=true&id=' + transId,
                    data: {},
                    success: function (data) {
                        if (data.success) {
                            location.replace(location.href);
                        } else {
                            layer.alert(data.message, {icon: 5});
                        }
                    },
                    error: function () {
                        alert("系统出现问题，请联系管理员");
                    },
                    dataType: 'json'
                });
            },
            function () {
                layer.msg('取消操作');
            }
        );
    });

    // 日志监听
    $('#transList').delegate('#loggingEventListener', 'click', function (e) {
        var $target = $(e.currentTarget);
        var transId = $target.data('id');
        //'../log/kettleLoggingEvent.html?transID='+transId,
        var loggingEventListener = layer.open({
            title: '实时日志',
            id: 'loggingEventListener',
            type: 2,
            shade: 0.2,
            btn: ['关闭'],
            area: ['40%', '60%'],
            content: '../log/kettleLoggingEvent.html?transID=' + transId,
        });
    });
}

