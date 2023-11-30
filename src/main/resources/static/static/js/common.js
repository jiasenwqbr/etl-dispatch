var webSpoonUrl = "http://124.254.6.162:18090/spoon/spoon";

//var kettleUtilUrl = 'http://172.16.23.99:8080/kettle/';
var kettleUtilUrl = 'http://124.254.6.162:18090/kettle/';

function getQueryVariable(variable) {
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var i = 0; i < vars.length; i++) {
        var pair = vars[i].split("=");
        if (pair[0] == variable) {
            return pair[1];
        }
    }
    return (false);
}