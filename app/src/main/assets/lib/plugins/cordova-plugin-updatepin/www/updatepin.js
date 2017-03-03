cordova.define("cordova-plugin-updatepin.UpdatePIN", function(require, exports, module) { 
 function UpdatePIN() {

    /**
     * cordova执行函数的一个安全包装
     */
    
}

UpdatePIN.prototype.update =function(success, failed) { 
	cordova.exec(success,failed,"UpdatePIN","update", []); 
// 关键方法："OpenLocalFile" 该参数一定要与config.xml中的类名一致
//exec的参数依次是  回调函数，执行失败的回调函数， xml中注册的插件名，插件中用于判断的Action名，参数                                                                           

//"openLocalFile" 该参数是action参数，是Plugin中action参数

};

// export
window.UpdatePIN = new UpdatePIN();
module.exports = window.UpdatePIN;
});
