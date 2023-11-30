$(document).ready(function () {
    hideForm();
    // 资源库下拉列表
    getRepType();
    // 数据库类型下拉列表
    getDatabaseType();
    // 数据库访问类型
    getDatabaseAccessType();
    // 提交按钮监听
    submitListener();
    // 监听文件下拉
    $('#repType').on('change', repTypeChange);
    // 监听资源库创建方式（如果是连接资源库则需要显示用户名密码字段。如果是创建则无需显示）
    $('#createType').on('change', createTypeChange);
});

console.log(kettleUtilUrl);

//      var kettleUtilUrl = 'http://172.16.23.99:8080/kettle/';
//var kettleUtilUrl = 'http://172.17.5.25:8888/';

function hideForm() {
    // 初始化隐藏动态表单
    var $sync = $('.sync-field');
    $sync.hide();
    $sync.find(":input").attr("disabled", true);
    $sync.find(":selected").attr("disabled", true);
}

function repTypeChange() {
    var type = $('#repType').val();
    // 隐藏动态表单
    hideForm();
    // 如果选择了具体的值
    if (type) {
        if (type === 'fileRep') {
            var $fileData = $('[data-field="file"]');
            $fileData.show();
            $fileData.find(":input").attr("disabled", false);
            $fileData.find(":selected").attr("disabled", false);
        }
        if (type === 'dbRep') {
            var $dbData = $('[data-field="database"]');
            $dbData.show();
            $dbData.find(":input").attr("disabled", false);
            $dbData.find(":selected").attr("disabled", false);
        }
    }
}

function createTypeChange() {
    var type = $('#createType').val();
    // 如果选择了具体的值
    if (type) {
        if (type === '1') {
            var $fileData = $('[data-field="repLogin"]');
            $fileData.show();
            $fileData.find(":input").attr("disabled", false);
            $fileData.find(":selected").attr("disabled", false);
        } else if (type === '2') {
            var $fileData = $('[data-field="repLogin"]');
            $fileData.hide();
        }
    }
}

// 数据库类型下拉列表
function getDatabaseType() {
    $.ajax({
        type: 'GET',
        async: false,
        url: '/dataCollection/enum/databaseType',
        data: {},
        success: function (data) {
            var list = data.result;
            for (var i = 0; i < list.length; i++) {
                $("#dbType").append('<option value="' + list[i].code + '">' + list[i].value + '</option>');
            }
        },
        error: function () {
            //alert("请求失败！请刷新页面重试");
        },
        dataType: 'json'
    });
}

// 资源库下拉列表
function getRepType() {
    $.ajax({
        type: 'GET',
        async: false,
        url: '/dataCollection/enum/repositoryType',
        data: {},
        success: function (data) {
            var list = data.result;
            for (var i = 0; i < list.length; i++) {
                $("#repType").append('<option value="' + list[i].code + '">' + list[i].value + '</option>');
            }
        },
        error: function () {
            //alert("请求失败！请刷新页面重试");
        },
        dataType: 'json'
    });
}

// 数据库访问类型
function getDatabaseAccessType() {
    $.ajax({
        type: 'GET',
        async: false,
        url: '/dataCollection/enum/databaseAccessType',
        data: {},
        success: function (data) {
            var list = data.result;
            for (var i = 0; i < list.length; i++) {
                $("#dbAccess").append('<option value="' + list[i].code + '">' + list[i].value + '</option>');
            }
        },
        error: function () {
            //alert("请求失败！请刷新页面重试");
        },
        dataType: 'json'
    });
}

var connecetionName = '';

// 到连接库中查询
function addConnection() {
    // 获取表单数据
    var data = {};
    $.each($("form").serializeArray(), function (i, field) {
        data[field.name] = field.value;
    });


    var tmp = {};
    tmp.name = data.repName;
    tmp.server = data.dbHost;
    tmp.type = data.dbType;
    tmp.access = data.dbAccess;
    tmp.database = data.dbName;
    tmp.port = data.dbPort;
    tmp.username = data.dbUsername;
    tmp.password = 'Encrypted 2be98afc86aa7f2e4cb79ce10bef2cfdb';

    var returnType = false;


    $.ajax({
        type: 'POST',
        async: false,
        url: kettleUtilUrl + '/connection/add',
        data: JSON.stringify(tmp),
        contentType: "application/json;charset=UTF-8",
        success: function (data) {

            if (data.code == '0000') {
                connecetionName = tmp.name;
                // returnType = true;
                // layer.msg("改资源库连接已存在，名称："+data.name+'，将使用之', {icon: 6});
            } else if (data.code == '9999') {
                connecetionName = data.data;
                // layer.msg(data.message, {icon: 5});
            }
        },
        error: function () {
            layer.msg("资源库连接查询失败", {icon: 5});
        },
        dataType: 'json'
    });

    return returnType;
}


// 测试链接
function testConnection() {
    // 获取表单数据
    var data = {};
    $.each($("form").serializeArray(), function (i, field) {
        data[field.name] = field.value;
    });
//判断是创建资源库还是连接已有资源库
    var type = $('#createType').val();
    if(type==="2"){
        //设置默认账号及密码
        data['repUsername'] = "admin";
        data['repPassword'] = "admin";
    }
    var returnType = false;
    $.ajax({
        type: 'POST',
        async: false,
        url: '/dataCollection/repository/testConnection',
        data: JSON.stringify(data),
        contentType: "application/json;charset=UTF-8",
        success: function (data) {
            if (data.success) {
                returnType = true;
                layer.msg("连接成功", {icon: 6});
            } else {
                layer.msg(data.message, {icon: 5});
            }
        },
        error: function () {
            layer.msg("连接失败，请检查参数重试", {icon: 5});
        },
        dataType: 'json'
    });

    return returnType;
}


function submitListener() {
    var icon = "<i class='fa fa-times-circle'></i> ";
    $("#RepositoryForm").validate({
        rules: {
            repName: {
                required: true
                // maxlength: 50,
                // remote: {
                //     type: 'POST',
                //     cache: false,
                //     url: '/dataCollection/repository/repNameExist',
                //     data: {
                //         repName: function () {
                //             return $("#repName").val();
                //         }
                //     }
                // }
            },
            repType: {
                required: true
            },
            repUsername: {
                required: true,
                maxlength: 50
            },
            repPassword: {
                required: true,
                maxlength: 50
            },
            repBasePath: {
                required: true
            },
            dbType: {
                required: true
            },
            dbAccess: {
                required: true
            },
            dbHost: {
                required: true,
                maxlength: 50
            },
            dbPort: {
                required: true,
                maxlength: 10
            },
            dbName: {
                required: true,
                maxlength: 20
            },
            dbUsername: {
                required: true,
                maxlength: 50
            },
            dbPassword: {
                required: true,
                maxlength: 50
            }
        },
        messages: {
            repName: {
                required: icon + "请输入资源库名称",
                maxlength: icon + "资源库名称不能超过50个字符",
                remote: icon + "资源库名称已存在"
            },
            repType: {
                required: icon + "请选择资源库类型"
            },
            repUsername: {
                required: icon + "请输入登录资源库用户名",
                maxlength: icon + "登录资源库用户名不能超过50个字符"
            },
            repPassword: {
                required: icon + "请输入登录资源库密码",
                maxlength: icon + "登录资源库密码不能超过50个字符"
            },
            repBasePath: {
                required: icon + "请输入文件资源库路径"
            },
            dbType: {
                required: icon + "请选择数据库类型"
            },
            dbAccess: {
                required: icon + "请选择数据库访问模式"
            },
            dbHost: {
                required: icon + "请输入数据库主机名或者IP地址",
                maxlength: icon + "作业描述不能超过50个字符"
            },
            dbPort: {
                required: icon + "请输入数据库端口号",
                maxlength: icon + "数据库端口号不能超过10个字符"
            },
            dbName: {
                required: icon + "请输入数据库名称",
                maxlength: icon + "数据库名称不能超过20个字符"
            },
            dbUsername: {
                required: icon + "请输入数据库登录账号",
                maxlength: icon + "数据库登录账号不能超过50个字符"
            },
            dbPassword: {
                required: icon + "请输入数据库登录密码",
                maxlength: icon + "数据库登录密码不能超过50个字符"
            }
        },
        // 提交按钮监听 按钮必须type="submit"
        submitHandler: function (form) {
            // 获取表单数据
            var data = {};
            $.each($("form").serializeArray(), function (i, field) {
                data[field.name] = field.value;
            });
            //判断是创建资源库还是连接已有资源库
            var type = $('#createType').val();
            if (type === '1') {
                // 做判断
                if (testConnection()) {
                    //addConnection();

                    $.ajax({
                        type: 'POST',
                        async: false,
                        url: '/dataCollection/repository/add',
                        data: JSON.stringify(data),
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
            } else if (type === '2') {
                var repository = {};
                repository.name = data.repName;
                repository.type = data.dbType;
                repository.access = 0;
                repository.hostname = data.dbHost;
                repository.databaseName = data.dbName;
                repository.port = data.dbPort;
                repository.username = data.dbUsername;
                repository.password = data.dbPassword;
                repository.supportBooleanDataType = true;
                repository.supportTimestampDataType = true;
                repository.preserveReservedCaseCheck = true;
                repository.extraOptions = new Array();
                repository.usingConnectionPool = "N";
                repository.initialPoolSize = "5";
                repository.maximumPoolSize = "10";
                repository.partitioned = "N";
                repository.partitionInfo = new Array();
                $.ajax({
                    type: 'POST',
                    async: false,
                    url: '/dataCollection/repository/createRepository',
                    data: JSON.stringify(repository),
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

        }
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
        if (element.is(":radio") || element.is(":checkbox")) {
            error.appendTo(element.parent().parent().parent());
        } else {
            error.appendTo(element.parent());
        }
    },
    errorClass: "help-block m-b-none",
    validClass: "help-block m-b-none"
});

function cancel() {
    location.href = "./list.html";
}