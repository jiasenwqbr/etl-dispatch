$(document).ready(function () {
    // var code = parent.strs[1].split('=')[1]
    // parent.dynamicMenu(code)
    // 顶部监控任务数据
    getMonitorTask();
    // 7天数据统计图
    get7DayStatus();
    // 转换统计列表
    monitorTrans();
    // 作业统计列表
    //monitorJob();
    //获取错误记录
    getErrorList();
    // 按钮绑定
    bindButton();
});

function getMonitorTask() {
    /*获取全部在监控的任务*/
    $.ajax({
        type: 'POST',
        async: false,
        url: '/dataCollection/monitor/taskCount',
        success: function (data) {
            if (!data.success) {
                alert(data.message);
            } else {
                $("#allNum").text(data.result.totalTaskNum);
                $("#transNum").text(data.result.transTaskNum);
                $("#jobNum").text(data.result.jobTaskNum);
            }
        },
        error: function () {
            ////alert("请求失败！请刷新页面重试");
        },
        dataType: 'json'
    });
}

function get7DayStatus() {
    // 获取7天监控状况
    $.ajax({
        type: 'POST',
        async: false,
        url: '/dataCollection/monitor/runStatus',
        success: function (data) {
            if (!data.success) {
                alert(data.message)
            } else {
                var legend = [];
                var trans = [];
                var jobs = [];
                $.each(data.result, function (i, field) {
                    legend.push(field.date);
                    jobs.push(field.jobNum);
                    trans.push(field.transNum);
                })

            }
            var lineChart = echarts.init(document.getElementById("kettleLine"));
            var lineoption = {
                title: {
                    text: '7天内作业和转换的监控状况'
                },
                tooltip: {
                    trigger: 'axis'
                },
                legend: {
                    data: ['作业', '转换']
                },
                xAxis: [
                    {
                        type: 'category',
                        boundaryGap: false,
                        data: legend
                    }
                ],
                yAxis: [
                    {
                        type: 'value',
                        axisLabel: {
                            formatter: '{value}'
                        }
                    }
                ],
                series: [
                    {
                        name: '转换',
                        type: 'line',
                        data: trans,
                        markPoint: {
                            data: [
                                {type: 'max', name: '最大值'},
                                {type: 'min', name: '最小值'}
                            ]
                        },
                    },
                    {
                        name: '作业',
                        type: 'line',
                        data: jobs,
                        markPoint: {
                            data: [
                                {type: 'max', name: '最大值'},
                                {type: 'min', name: '最小值'}
                            ]
                        },
                    }
                ]
            };
            lineChart.setOption(lineoption);
            $(window).resize(lineChart.resize);
        },
        error: function () {
            ////alert("请求失败！请刷新页面重试");
        },
        dataType: 'json'
    });
}

function monitorTrans() {
    $('#transMonitorList').bootstrapTable({
        url: '/dataCollection/script/monitor/findScriptMonitorListByPage',            //请求后台的URL（*）
        method: 'POST',            //请求方式（*）
        toolbar: '#toolbar',        //工具按钮用哪个容器
        striped: true,                      //是否显示行间隔色
        cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
        pagination: true,                   //是否显示分页（*）
        sortable: false,                     //是否启用排序
        sortOrder: "asc",                   //排序方式
        queryParams: queryRecordListParams,//传递参数（*）
        sidePagination: "server",           //分页方式：client客户端分页，server服务端分页（*）
        pageNumber: 1,                       //初始化加载第一页，默认第一页
        pageSize: 5,                       //每页的记录行数（*）
        pageList: [5, 10, 20],        //可供选择的每页的行数（*）
        search: false,                       //是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
        strictSearch: false,
        showColumns: false,                  //是否显示所有的列
        showRefresh: false,                  //是否显示刷新按钮
        minimumCountColumns: 2,             //最少允许的列数
        clickToSelect: true,                //是否启用点击选中行
        // height: 500,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
        uniqueId: "id",                     //每一行的唯一标识，一般为主键列
        showToggle: false,                    //是否显示详细视图和列表视图的切换按钮
        cardView: false,                    //是否显示详细视图
        detailView: false,                   //是否显示父子表
        responseHandler: function (res) {
            return {
                "total": res.result.total,//总页数
                "rows": res.result.list//数据列表
            };
        },
        columns: [
            {
                field: 'id',
                title: '记录编号'
            }, {
                field: 'categoryName',
                title: '分类名'
            }, {
                field: 'scriptName',
                title: '转换名'
            }, {
                field: 'scriptDescription',
                title: '转换描述'
            }, {
                field: 'monitorSuccess',
                title: '成功次数'
            }, {
                field: 'monitorFail',
                title: '失败次数'
            }, {
                field: 'monitorStatus',
                title: '监控状态'
            }]
    });
}

function getErrorList() {

    $('#errorList').bootstrapTable({
        url: '/dataCollection/script/monitor/findErrorRecordList',            //请求后台的URL（*）
        method: 'GET',            //请求方式（*）
        toolbar: '#toolbar',        //工具按钮用哪个容器
        striped: true,                      //是否显示行间隔色
        cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
        pagination: false,                   //是否显示分页（*）
        sortable: false,                     //是否启用排序
        sortOrder: "asc",                   //排序方式
        //queryParams: queryParams,//传递参数（*）
        sidePagination: "server",           //分页方式：client客户端分页，server服务端分页（*）
        pageNumber: 1,                       //初始化加载第一页，默认第一页
        pageSize: 5,                       //每页的记录行数（*）
        pageList: [5, 10, 20],        //可供选择的每页的行数（*）
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
                "total": res.result.totalElements,//总页数
                "rows": res.result//数据列表
            };
        },
        columns: [
            {
                field: 'id',
                title: '编号',
                visible: false
            }, {
                field: 'recordScriptId',
                title: 'id'
            }, {
                field: 'scriptName',
                title: '名称'
            }, {
                field: 'scriptDescription',
                title: '描述'
            }, {
                field: 'categoryName',
                title: '分类名称'
            }, {
                field: 'stopTime',
                title: '失败时间'
            }, {
                field: 'recordStatusStr',
                title: '执行结果'
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
    if (params.limit == undefined) {
        params.limit = 5;
    }
    if (params.offset == undefined) {
        params.offset = 0;
    }
    return JSON.stringify({
        rows: params.limit,
        page: (params.offset / params.limit) + 1,
        query: {}
    });
}

/*
function queryParams(params) {
    return JSON.stringify({
        page: {
            size: 10,
            number: 1
        },
        query: {
            monitorStatus: 1
        }
    });
}
*/

function queryRecordListParams() {
    return JSON.stringify({
        rows: 10,
        page: 1,
        query: {
            recordScriptId: $("#transId").val()
        }
    });
}

function searchTrans() {
    $('#transMonitorList').bootstrapTable('refresh');
}

function searchJobs() {
    $('#jobMonitorList').bootstrapTable('refresh');
}

function actionFormatter(value, row, index) {
    return [
        '<a class="btn  btn-xs" id="view" type="button" data-id="' + row.id + '">&nbsp;查看</a>',
        '&nbsp;&nbsp;',
        '<a class="btn  btn-xs" id="download" type="button" data-id="' + row.id + '">&nbsp;下载</a>'
    ].join('');
}

function bindButton() {
    // 查看日志
    $('#errorList').delegate('#view', 'click', function (e) {
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
    $('#errorList').delegate('#download', 'click', function (e) {
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