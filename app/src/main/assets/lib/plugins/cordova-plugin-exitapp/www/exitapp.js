cordova.define("cordova-plugin-exitapp.exitapp", function(require, exports, module) { 
 function exitapp() {

    /**
     * cordova执行函数的一个安全包装
     */
    
}

exitapp.prototype.exit =function(success, failed) { 
	cordova.exec(success,failed,"ExitApp","exit", []); 

};

// export
window.exitapp = new exitapp();
module.exports = window.exitapp;
});
