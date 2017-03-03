var mimeType = {
		'.au':	'audio/basic',
		'.avi':	'video/x-msvideo',
		'.bmp':	'image/bmp',
		'.css':	'text/css',
		'.doc':	'application/msword',
		'.dot':	'application/msword',
		'.gif':	'image/gif',
		'.gtar':	'application/x-gtar',
		'.gz':	'application/x-gzip',
		'.htm':	'text/html',
		'.html':	'text/html',
		'.ico':	'image/x-icon',
		'.jpe':	'image/jpeg',
		'.jpeg':	'image/jpeg',
		'.jpg':	'image/jpeg',
		'.js':	'application/x-javascript',
		'.mid':	'audio/mid',
		'.movie':	'video/x-sgi-movie',
		'.mp2':	'video/mpeg',
		'.mp3':	'audio/mpeg',
		'.mpa':	'video/mpeg',
		'.mpe':	'video/mpeg',
		'.mpeg':	'video/mpeg',
		'.mpg':	'video/mpeg',
		'.mpp':	'application/vnd.ms-project',
		'.mpv2':	'video/mpeg',
		'.pbm':	'image/x-portable-bitmap',
		'.pdf':	'application/pdf',
		'.pps':	'application/vnd.ms-powerpoint',
		'.ppt':	'application/vnd.ms-powerpoint',
		'.rtf':	'application/rtf',
		'.swf':	'application/x-shockwave-flash',
		'.tar':	'application/x-tar',
		'.txt':	'text/plain',
		'.wav':	'audio/x-wav',
		'.xla':	'application/vnd.ms-excel',
		'.xlc':	'application/vnd.ms-excel',
		'.xlm':	'application/vnd.ms-excel',
		'.xls':	'application/vnd.ms-excel',
		'.xlt':	'application/vnd.ms-excel',
		'.xlw':	'application/vnd.ms-excel',
		'.z':	'application/x-compress',
		'.zip':	'application/zip',
		'.docx':   'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
		'.xlsx':    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
		'.pptx':    'application/vnd.openxmlformats-officedocument.presentationml.presentation',
		'.ppsx':   'application/vnd.openxmlformats-officedocument.presentationml.slideshow'
}
/**
 * 依赖phoneGap的文件下载功能
 * @param source 文件下载url
 * @param targetDir 保存到本地的保存路径
 * @param fileName  保存到本地的文件名
 * @param id    UI中显示下载文件，显示进度条等所在的 Dom元素的 jQuery对象
 *                  如提供此参数，Dom元素中必须包含以下元素
 *                  <div class="sme_process_bar" id="b_{id}"><div class="sme_current_process" id="c_{id}"></div><span style="padding-right:5px; float:right" id="p_{id}"></span></div>
 * @param fileSize  文件大小
 * @param downloadSuccess 下载成功时的回调函数，如果提供参数此参数，参数domObj可以不提供。该回调函数的参数为 phoneGap提供的 FileEntry
 * @param downloadError 下载失败时的回调函数
 * @param downloadProcess 下载过程处理的回调函数，其参数为 ProgressEvent
 */
function downloadFile(source, targetDir, fileName, id, fileSize, downloadSuccess, downloadError, downloadProcess){

	var ft = new FileTransfer();
	var uri = encodeURI(source).replace(/[+]/g, '%2B');
	target = targetDir + fileName.replace(/[ ]/g, '%20');

	var downloadSuccessHandler = null;
	var downloadErrorHandler = null;
	var downloadProcessHandler = null;
	
	if(downloadSuccess && downloadSuccess!=null){
		downloadSuccessHandler = downloadSuccess;
	}else{
		downloadSuccessHandler = defaultSuccessHandler;
	};
	
	if(downloadProcess && downloadProcess!=null){
		downloadProcessHandler = downloadProcess;
	}else{
		downloadProcessHandler = defaultProcessing;
	};
	
	if(downloadError && downloadError!=null){
		downloadErrorHandler = downloadError;
	}else{
		downloadErrorHandler = defaultErrorHandler;
	};

	// 判断路径是否存在，如果不存在，则创建
	window.resolveLocalFileSystemURI("file:///", onSuccess, fail);
	function onSuccess(dirEntry) {
		dirEntry.getDirectory(targetDir);
	}
	function fail(err){
		Ext.Msg.alert('错 误',"信息：" + JSON.stringify(err));
	}
	
	Ext.getCmp('frame').showMessage('下载中...', true);
	ft.download(
	    uri,
	    target,
	    downloadSuccessHandler,
	    downloadErrorHandler,
	    true  //  defaults to false. If set to true then it will accept all security certificates
	);

	document.getElementById('b_' + id).style.display = 'inline';
	ft.onprogress = downloadProcessHandler;
	/**
	 * 清除进度，处理失败，中断，成功
	 */
	function clearProcess() {
		
		document.getElementById('b_' + id).style.display = 'none';
		ft.abort();
		
	};
	
	function defaultSuccessHandler(entry){
		document.getElementById('p_' + id).innerHTML = "100%";
		document.getElementById('c_' + id).style.width = "100%";
		Ext.getCmp('frame').hideMessage();
//    	Ext.Msg.alert("提示","文件已下载到: " + entry.fullPath);
/*		Ext.Msg.confirm('提示', '下载成功，是否打开文件?',
                function (confirmed) {
                   if (confirmed == 'yes') {
                	   var fullPath = entry.fullPath.substring(6);
                	   var d=/\.[^\.]+$/.exec(fullPath);
                	   var type = mimeType[d[0]];
                	   if(!type){
                		   Ext.Msg.alert("提示","不支持的文件类型");
                		   return false;
                	   }
                	   window.plugins.openLocalFile.open(
                			   fullPath, 
                			   type,
                				function(msg){
                				   
                				}, function(msg){
                					Ext.Msg.alert("提示",msg);
                				}
                		);
                   }
               },
               this
         );*/
		

 	   var fullPath = entry.fullPath.substring(6);
 	   var d=/\.[^\.]+$/.exec(fullPath);
 	   var type = mimeType[d[0]];
 	   if(!type){
 		   Ext.Msg.alert("提示","不支持的文件类型");
 		   return false;
 	   }
 	   window.plugins.openLocalFile.open(
 			   fullPath, 
 			   type,
 				function(msg){
 				   
 				}, function(msg){
 					Ext.Msg.alert("提示",msg);
 				}
 		);
		
	}
	
	function defaultErrorHandler(error){
		Ext.getCmp('frame').hideMessage();
		Ext.Msg.alert("提示","下载错误 code: " + error.code);
	}
	
	/**
	 * 用于处理进度，如显示进度条等。
	 */
	function defaultProcessing(progressEvent){
		if (progressEvent.lengthComputable) {
			//已经load
			var loaded = progressEvent.loaded;
			var total = progressEvent.total;

			//计算百分比，用于显示进度条
			var percent=parseInt((loaded/total)*100);
			if(percent>100){
				percent=100;
			}
			document.getElementById('p_' + id).innerHTML = percent + "%";
			document.getElementById('c_' + id).style.width = percent+"%";
		}
	};
	
};

function listLocalFiles(path, successCallback, failCallback){
//	wapAlert("inner--1");
	window.resolveLocalFileSystemURI(path, onSuccess, fail);
	function onSuccess(fileEntry) {
		
		var directoryReader = fileEntry.createReader();
		directoryReader.readEntries(success,fail);
		
	    function success(entries){
	    	
	    	successCallback(entries);
	    }
	}
	
	function fail(err){
		if(failCallback && null != failCallback)
			failCallback(err);
	}
};

function docDownload(source, id, fileName){

	//document.getElementById("attach_" + id).style.paddingBottom = "25px";
	var sourceUrl = global_url + '/fileupdown/down?path=' + BASE64.encode(source) +'&filename=' + BASE64.encode(fileName);
	if(device && 'iOS' == device.platform){
		sourceUrl = sourceUrl.replace(/[+]/g, '%2B').replace(/[ ]/g, '%20');
		var ref = window.open(sourceUrl, '_system', 'location=no');
	}else{
		var d=/\.[^\.]+$/.exec(fileName);
	    var type = mimeType[d[0]];
	    if(!type){
		    sourceUrl = sourceUrl.replace(/[+]/g, '%2B').replace(/[ ]/g, '%20');
		    var ref = window.open(sourceUrl, '_system', 'location=no');
		    return false;
	    }else{
		   sourceUrl = sourceUrl.replace(/[+]/g, '%2B').replace(/[ ]/g, '%20');
		    if(fileName.length > 40) {
		 	   fileName = fileName.substring(0,40)+d;
		    }
		   window.downloadfile.download(sourceUrl,(new Date().getTime()+"_").substr(3)+fileName, type);//下载的插件判断文件已存在时不会重新下载, 此方法强制每次下载新的文件
	    }
//		window.requestFileSystem(LocalFileSystem.PERSISTENT, 0, onFileSystemSuccess, fail);
	}
	
    function onFileSystemSuccess(fileSystem) {
    	fileSystem.root.getDirectory("smewap", {create: true, exclusive: false}, 
        		function(entry){
		    		fileSystem.root.getDirectory("smewap/Download", {create: true, exclusive: false}, 
		    	    		function(entry1){
		    	    			downloadFile(sourceUrl,  entry1.fullPath + "/", fileName, id);
		    				}
		    	    		, fail
		    	    );
    			}
        		, fail
        );
    	
    	
    	
    };
    
    function fail(evt) {
    	Ext.Msg.alert('提示', '获取路径失败,code:' + evt.target.error.code);
    }
	
};

//android 采用浏览器直接下载附件
function browserDown(source, id, fileName){
	if(null != id){
		//document.getElementById("attach_" + id).style.paddingBottom = "25px";
	}
	
	var sourceUrl = global_url + '/fileupdown/down?path=' + BASE64.encode(source) +'&filename=' + BASE64.encode(fileName);

	if(Ext.os.is.iOS){
		sourceUrl = sourceUrl.replace(/[+]/g, '%2B').replace(/[ ]/g, '%20');
		var ref = window.open(sourceUrl, '_blank', 'location=no,enableViewportScale=yes,closebuttoncaption=关闭,toolbarposition=top');
		ref.addEventListener('loadstart', function(event){
			Ext.getCmp('mainCmp').getMasked().show();
		});
		ref.addEventListener('loadstop', function(event){
			Ext.getCmp('mainCmp').getMasked().hide();
		});
		ref.addEventListener('loaderror', function(event){
			Ext.getCmp('mainCmp').getMasked().hide();
		});
		ref.addEventListener('exit', function(event){
			Ext.getCmp('mainCmp').getMasked().hide();
		});
	}else{
		sourceUrl = sourceUrl.replace(/[+]/g, '%2B').replace(/[ ]/g, '%20');
		window.open(sourceUrl, '_system', 'location=no');
	}
	
};

function dominoDownload(id, fileName, docid){
	
	var param = {};
	param.username = Sme.app.tempCache.loginUser.fullname;//'shadmin/chinaccs';
	param.docid = docid;
	param.fjname = fileName;
	var url = global_url + "/wap/execute?remoteName=domino&cmd=getAttatch&command=getAttatch";
	Sme.util.Request.request(url, param, function(response, requst){
		docDownload(response.result[0].text, id, response.result[0].label);
	})
};

/**
 * 依赖phoneGap的文件上传功能
 * @param fileInfo 本地文件信息，{filename:"文件名",mimeType:"文件类型", path:"文件路径，包括文件名"}
 * @param targetUrl 文件上传路径
 * @param uploadSuccess 上传成功时的回调函数
 * @param uploadError 上传失败时的回调函数
 * @param uploadProcess 上传过程处理的回调函数，其参数为 ProgressEvent
 */
function uploadFile(fileInfo, targetUrl, uploadSuccess, uploadError, uploadProcess){
	
	var uploadSuccessHandler = null;
	var uploadErrorHandler = null;
	var uploadProcessHandler = null;
	
	if(uploadSuccess && uploadSuccess!=null){
		uploadSuccessHandler = function(data){
			data.response = data.response.replace(/{\s*'/g, '{"');
	    	data.response = data.response.replace(/'\s*}/g, '"}');
	    	data.response = data.response.replace(/'\s*:\s*'/g, '":"');
	    	data.response = data.response.replace(/'\s*,\s*'/g, '","');
			uploadSuccess(JSON.parse(data.response));
		};
	}else{
		uploadSuccessHandler = defaultSuccessHandler;
	};
	
	if(uploadProcess && uploadProcess!=null){
		uploadProcessHandler = uploadProcess;
	}else{
		uploadProcessHandler = defaultUploadProcess;
	};
	
	if(uploadError && uploadError!=null){
		uploadErrorHandler = uploadError;
	}else{
		uploadErrorHandler = defaultErrorHandler;
	};
	var options = new FileUploadOptions();
	options.fileKey = "file";
	options.mimeType = "multipart/form-data";  //"image/jpeg";  "text/plain"; 
	options.chunkedMode = false; //If you experience problems uploading to a Nginx server then make sure you have the chunkedMode option set to false.
	options.fileName = fileInfo.name;

	ft = new FileTransfer();
	var uploadUrl=encodeURI(targetUrl + "");

	ft.upload(fileInfo.path,
			uploadUrl,
			uploadSuccessHandler,
			uploadErrorHandler, 
			options,
			true //  defaults to false. If set to true then it will accept all security certificates 
			);
	
	ft.onprogress = uploadProcessHandler;
	
	/**
	 * 处理上传成功 
	 */
	function defaultSuccessHandler(data){
		console.log("上传成功");
	}
	
	/**
	 * 处理上传失败 
	 */
	function defaultErrorHandler(error){
		console.log("上传失败");
	}
	
	/**
	 * 上传过程回调，用于处理上传进度，如显示进度条等。
	 */
	function defaultUploadProcess(progressEvent){
		if (progressEvent.lengthComputable) {
			//已经上传
			var loaded=progressEvent.loaded;
			//文件总长度
			var total=progressEvent.total;
			//计算百分比，用于显示进度条
			var percent=parseInt((loaded/total )*100);
			console.log("上传进度：" + percent + "%");
		}
	};
};


function readFileOnlineNew(p_url,id,p_filename,from){
	console.log(p_url);
	if(global_qybm != 'GDSLYJ' && global_qybm != 'BLH'){
		docDownload(p_url,id,p_filename);
		return;
	}
	
	p_url=p_url.replace(/[+]/g, '%2B').replace(/[ ]/g, '%20');
	var sourceUrl = (global_url + '/fileupdown/down?path=' + BASE64.encode(p_url) +'&filename=' + BASE64.encode(p_filename)).replace(/[+]/g, '%2B').replace(/[ ]/g, '%20');
	var suffix=/\.[^\.]+$/.exec(p_filename)[0].toLowerCase();	
	console.log(sourceUrl);
	if(Ext.os.is.iOS){
		var ref = window.open(sourceUrl, '_system', 'location=no');
		
		return;
	}

	if( '.pdf'==suffix){

		var url = UrlUtil.encodeUrl(BASE64.encode(p_url));
		
		var filename = UrlUtil.encodeUrl(BASE64.encode(p_filename));
		var frame = Ext.getCmp('frame');
		frame.fireEvent(from +'ShowPdfImg', frame, {url:url, filename:filename});
		/*controller.redirectTo('showPdfImg/'+url+'/'+filename);
		controller.backFromOtherPage = true;*/
	}else if(suffix=='.doc'||suffix=='.docx'||suffix=='.xls'||suffix=='.xlsx'){
		var url = BASE64.encode(p_url);
    	var fileName = BASE64.encode(p_filename);
		try{

			Ext.getCmp('frame').showMessage("读取数据中", true);
			var loadstopEvent = function(event) {
				console.log('load end.......');
	        	ref.show();
	        	setTimeout(function(){
	        		Ext.getCmp('frame').hideMessage();
	        	},200);
	        	
	        	
	        };
	        
			var sourceUrl = (global_url + '/wap/readonline/openhtml?path=' + url +'&filename=' + fileName).replace(/[+]/g, '%2B').replace(/[ ]/g, '%20');
    		ref = window.open(sourceUrl, '_blank', 'location=yes,hidden=true,closebuttoncaption=关闭'); //location设为no, closebuttoncaption将失效
    		ref.addEventListener('loadstop', loadstopEvent);
    		
    		
		}catch(e){ // 打开失败时使用旧的方式打开
			
		}
		
	}else if(suffix=='.jpg'||suffix=='.png'||suffix=='.jpeg'||suffix=='.bmp'||suffix=='.ico'||suffix=='.gif'||suffix=='.jpe'){
		var url = UrlUtil.encodeUrl(BASE64.encode(p_url));
		var filename = UrlUtil.encodeUrl(BASE64.encode(p_filename));
		
		var frame = Ext.getCmp('frame');
		frame.fireEvent(from +'ShowImageImg', frame, {url:url, filename:filename});
		/*controller.redirectTo('showJpgImg/'+url+'/'+filename);
		controller.backFromOtherPage = true;*/
	
	}else{
		/*setTimeout(function(){
			Ext.getCmp('mainCmp').getMasked().hide();
		},200);*/
		
		docDownload(p_url,id,p_filename);

		
	}
};

