$(document).ready(function () {
    // var code = parent.strs[1].split('=')[1]
	// parent.dynamicMenu(code)
	// 加载列表
    getCategoryList();

    bindButton();
});

function getCategoryList() {
    $('#categoryList').bootstrapTable({
        url: '/dataCollection/category/findCategoryListByPage',            //请求后台的URL（*）
        method: 'POST',            //请求方式（*）
        toolbar: '#toolbar',        //工具按钮用哪个容器
        striped: true,                      //是否显示行间隔色
        cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
        pagination: true,                   //是否显示分页（*）
        sortable: false,                     //是否启用排序
        sortOrder: "asc",                   //排序方式
        queryParams:queryParams,//传递参数（*）
        sidePagination: "server",           //分页方式：client客户端分页，server服务端分页（*）
        pageNumber: 1,                       //初始化加载第一页，默认第一页
        pageSize: 10,                       //每页的记录行数（*）
        pageList: [5, 25, 50, 100],        //可供选择的每页的行数（*）
        search: false,                       //是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
        strictSearch: false,
        showColumns: true,                  //是否显示所有的列
        showRefresh: true,                  //是否显示刷新按钮
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
                "total": res.result.total,//总页数
                "rows": res.result.records//数据列表
            };
        },
        columns: [
            {
                field: 'id',
                title: '编号',
                visible: false
            }, {
                field: 'categoryName',
                title: '分类名称'
            }, {
                field: 'addTime',
                title: '创建时间'
            }, {
                field: 'editTime',
                title: '更新时间'
            }, {
                field: 'operate',
                title: '操作',
                width: 150,
                align: 'center',
                valign: 'middle',
                formatter: actionFormatter
            }]
    });
}

function queryParams(params) {
    return  JSON.stringify({
        rows: params.limit,
        page: (params.offset / params.limit) + 1
    }
    );
}

function actionFormatter(value, row, index) {
    return [
        '<a class="btn  btn-xs" id="edit" type="button" data-id="'+ row.id +'">&nbsp;编辑</a>',
        '&nbsp;&nbsp;',
        '<a class="btn  btn-xs" id="delete" type="button" data-id="'+ row.id +'">&nbsp;删除</a>'
    ].join('');
}

function bindButton() {
    $('#categoryList').delegate('#edit','click',function(e) {
        var $target = $(e.currentTarget);
        var categoryId = $target.data('id');
        location.href = "./edit.html?categoryId=" + categoryId;
    });

    $('#categoryList').delegate('#delete', 'click', function(e) {
        var $target = $(e.currentTarget);
        var categoryId = $target.data('id');
        layer.confirm('确定删除该分类？', {
                btn: ['确定', '取消']
            },
            function(index){
                layer.close(index);
                $.ajax({
                    type: 'DELETE',
                    async: true,
                    url: '/dataCollection/category/delete?id='+categoryId,
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
            function(){
                layer.msg('取消操作');
            }
        );
    });
}