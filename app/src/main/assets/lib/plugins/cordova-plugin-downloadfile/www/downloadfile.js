cordova.define("cordova-plugin-downloadfile.downloadfile", function(require, exports, module) { 
 function downloadfile() {

    /**
     * cordova执行函数的一个安全包装
     */
    
}

downloadfile.prototype.download =function(url, filename, mimetype, success, failed) { 
	cordova.exec(success,failed,"DownloadFile","download", [url, filename, mimetype]); 
// 关键方法："OpenLocalFile" 该参数一定要与config.xml中的类名一致
//exec的参数依次是  回调函数，执行失败的回调函数， xml中注册的插件名，插件中用于判断的Action名，参数                                                                           

//"openLocalFile" 该参数是action参数，是Plugin中action参数

};

// export
window.downloadfile = new downloadfile();
module.exports = window.downloadfile;
});
