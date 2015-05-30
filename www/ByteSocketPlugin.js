var exec = require('cordova/exec');
function ByteSocketPlugin() { 
	//console.log("CoolPlugin.js: is created");
}
ByteSocketPlugin.prototype.send = function(aString,ip,port,rec_byte_count,successC){ 
	//console.log("CoolPlugin.js: showToast"); 
	exec(function(result){ successC(result); },function(result){} ,"ByteSocketPlugin",'send,'+ip+','+port+','+aString+','+rec_byte_count,[]);
}
var byteSocketPlugin = new ByteSocketPlugin(); 
module.exports = byteSocketPlugin;
