$(document).ready(function () {
    // 加载搜索选项
    getCategoryj();
    // getMonitorsStatus();
	// 加载列表
 //   getJobCountInfo();
    getJobErrorList();
    // 按钮绑定
    bindButtonj();
});

//
//function getJobCountInfo() {
//     $.ajax({
//         type: 'GET',
//         async: false,
//         url: '/dataCollection/trans/monitor/countTrans.do',
//         data: {},
//         success: function (data) {
//             if (data.success) {
//                 var count = data.result;
//                 $("#allTrans").text(count.total);
//                 $("#allSuccess").text(count.success);
//                 $("#allFail").text(count.fail);
//             } else {
//                 layer.msg(data.message, {icon:2})
//             }
//         },
//         error: function () {
//             //alert("请求失败！请刷新页面重试");
//         },
//         dataType: 'json'
//     });
// }
function getCategoryj() {
    $.ajax({
        type: 'GET',
        async: false,
        url: '/dataCollection/category/findCategoryList.do',
        data: {},
        success: function (data) {
            var list = data.result;
            for (var i=0; i<list.length; i++){
                $("#categoryIdj").append('<option value="' + list[i].id + '">' + list[i].categoryName + '</option>');
            }
        },
        error: function () {
            ////alert("请求失败！请刷新页面重试");
        },
        dataType: 'json'
    });
}

// function getMonitorsStatus() {
//     $.ajax({
//         type: 'GET',
//         async: false,
//         url: '/enum/runStatus.do',
//         data: {},
//         success: function (data) {
//             var list = data.result;
//             for (var i=0; i<list.length; i++){
//                 $("#monitorStatus").append('<option value="' + list[i].code + '">' + list[i].value + '</option>');
//             }
//         },
//         error: function () {
//             //alert("请求失败！请刷新页面重试");
//         },
//         dataType: 'json'
//     });
// }

function getJobErrorList() {
    $('#joberrorList').bootstrapTable({
        url: '/dataCollection/job/monitor/findjobByError.do',            //请求后台的URL（*）
        method: 'POST',            //请求方式（*）
        toolbar: '#toolbar',        //工具按钮用哪个容器
        striped: true,                      //是否显示行间隔色
        cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
        pagination: true,                   //是否显示分页（*）
        sortable: false,                     //是否启用排序
        sortOrder: "asc",                   //排序方式
        queryParams: queryjobParams,//传递参数（*）
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
        responseHandler: function(res) {
            return {
                "total": res.result.totalElements,//总页数
                "rows": res.result.content//数据列表
            };
        },
        columns: [
            {
                field: 'id',
                title: '编号',
                visible: false
            }, {
                field: 'recordJobId',
                title: '作业id'
            }, {
                field: 'jobName',
                title: '作业名称'
            }, {
                field: 'jobDescription',
                title: '作业描述'
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
                formatter: actionFormatterj
            }]
    });
}

function queryjobParams(params) {
    return  JSON.stringify({
        page: {
            size: params.limit,
            number: (params.offset / params.limit) + 1
        },
        query: {
            categoryId: $("#categoryId").val(),
            // scriptName: $("#transName").val(),
            // monitorStatus: $("#monitorStatus").val()
        }
    });
}

function actionFormatterj(value, row, index) {
    return [
        '<a class="btn  btn-xs" id="view" type="button" data-id="'+ row.id +'">&nbsp;查看</a>',
        '&nbsp;&nbsp;',
        '<a class="btn  btn-xs" id="download" type="button" data-id="'+ row.id +'">&nbsp;下载</a>'
    ].join('');
}

function search2() {
    $('#joberrorList').bootstrapTable('refresh');
}

// function transNameFormatter(value, row, index) {
//     if (value.length > 15) {
//         var newValue = value.substring(0, 14);
//         return newValue + "……";
//     } else {
//         return value;
//     }
// }
    function bindButtonj() {
        // 查看日志
        $('#joberrorList').delegate('#view','click',function(e) {
            var $target = $(e.currentTarget);
            var jobRecordId = $target.data('id');
            $.ajax({
                type: 'GET',
                async: false,
                url: '/dataCollection/job/monitor/viewJobRecordDetail.do?jobRecordId=' + jobRecordId,
                data: {},
                success: function (data) {
                    if (data.success) {
                        layer.open({
                            type: 1,
                            title: "作业日志记录",
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
        $('#joberrorList').delegate('#download','click',function(e) {
            var $target = $(e.currentTarget);
            var jobRecordId = $target.data('id');
            layer.confirm('确定下载该日志记录？', {
                    btn: ['确定', '取消']
                },
                function (index) {
                    layer.close(index);
                    var form = $('<form>');
                    form.attr('style', 'display:none');
                    form.attr('method', 'get');
                    form.attr('action', '/dataCollection/job/monitor/downloadJobRecord.do');
                    var $recordId = $('<input>');
                    $recordId.attr('type', 'hidden');
                    $recordId.attr('name', 'jobRecordId');
                    $recordId.attr('value', jobRecordId);
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

