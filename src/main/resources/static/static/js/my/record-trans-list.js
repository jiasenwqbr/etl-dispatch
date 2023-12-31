$(document).ready(function () {
    $("#transId").val(getQueryVariable("transId"));
    // 加载列表
    getTransRecordList();
    // 按钮绑定
    bindButton();
});

function getTransRecordList() {
    $('#transRecordList').bootstrapTable({
        url: '/dataCollection/script/monitor/findScriptRecordList',            //请求后台的URL（*）
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
        responseHandler: function (res) {
            return {
                "total": res.result.total,//总页数
                "rows": res.result.list//数据列表
            };
        },
        rowStyle: function (row, index) {
            var style = {};
            if (row.recordStatus === 0) {
                style = {css: {'background': '#f27a58'}}
            }
            return style;
        },
        columns: [
            {
                field: 'id',
                title: '编号',
                visible: false
            }, {
                field: 'scriptName',
                title: '脚本名称'
            }, {
                field: 'startTime',
                title: '启动时间'
            }, {
                field: 'stopTime',
                title: '停止时间'
            }, {
                field: 'recordStatus',
                title: '执行结果',
                formatter: recordStatusFormatter
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
            recordScriptId: $("#transId").val()
        }
    });
}

function recordStatusFormatter(value, row, index) {
    var result = "";
    if (row.recordStatus == 1) {
        result += '<a onclick="failRetryLog(\'' + row.id + '\')" title="成功" style="color: #315efb">成功</a></div>';
    } else {
        //result = "失败";
        result += '<a onclick="failRetryLog(\'' + row.id + '\')" title="失败" style="color: #315efb">失败</a></div>';
    }
    return result;
}

function actionFormatter(value, row, index) {
    return [
        '<a class="btn btn-primary btn-xs" id="view" type="button" data-id="' + row.id + '"><i class="fa fa-eye" aria-hidden="true"></i>&nbsp;查看</a>',
        '&nbsp;&nbsp;',
        '<a class="btn btn-primary btn-xs" id="download" type="button" data-id="' + row.id + '"><i class="fa fa-download" aria-hidden="true"></i>&nbsp;下载</a>'
    ].join('');
}

function refresh() {
    $('#transRecordList').bootstrapTable('refresh');
}

function transNameFormatter(value, row, index) {
    if (value.length > 15) {
        var newValue = value.substring(0, 14);
        return newValue + "……";
    } else {
        return value;
    }
}

/**
 * 查看失败重试日志
 * @param recordId
 */
function failRetryLog(recordId) {
    $.ajax({
        type: 'GET',
        async: false,
        url: '/dataCollection/kFailRetryLog/getLogByRecord?recordId=' + recordId,
        data: {},
        success: function (data) {
            if (data.success) {
                layer.open({
                    type: 1,
                    title: "失败重试日志记录",
                    area: ['50%', '50%'], //宽高
                    content: data.result.failRetryLog.replace(/\n/g, "<br>")
                });
            } else {
                layer.msg(data.message, {icon: 2});
            }
        },
        error: function () {
            alert("系统出现问题，请联系管理员");
        },
        dataType: 'json'
    });
}

function bindButton() {
    // 查看日志
    $('#transRecordList').delegate('#view', 'click', function (e) {
        var $target = $(e.currentTarget);
        var transRecordId = $target.data('id');
        $.ajax({
            type: 'GET',
            async: false,
            url: '/dataCollection/script/monitor/viewSriptRecordDetail?transRecordId=' + transRecordId,
            data: {},
            success: function (data) {
                if (data.success) {
                    layer.open({
                        type: 1,
                        title: "转换日志记录",
                        area: ['50%', '50%'], //宽高
                        content: data.result
                    });
                } else {
                    layer.msg(data.message, {icon: 2});
                }
            },
            error: function () {
                alert("系统出现问题，请联系管理员");
            },
            dataType: 'json'
        });
    });

    // 日志下载
    $('#transRecordList').delegate('#download', 'click', function (e) {
        var $target = $(e.currentTarget);
        var transRecordId = $target.data('id');
        layer.confirm('确定下载该日志记录？', {
                btn: ['确定', '取消']
            },
            function (index) {
                layer.close(index);
                var form = $('<form>');
                form.attr('style', 'display:none');
                form.attr('method', 'get');
                form.attr('action', '/dataCollection/script/monitor/downloadTransRecord');
                var $recordId = $('<input>');
                $recordId.attr('type', 'hidden');
                $recordId.attr('name', 'transRecordId');
                $recordId.attr('value', transRecordId);
                $('body').append(form);
                form.append($recordId);
                form.submit();
            },
            function () {
                layer.msg('取消操作');
            }
        );
    });
}

