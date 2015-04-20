var exec = require('cordova/exec');
function ByteSocketPlugin() { 
	//console.log("CoolPlugin.js: is created");
}
ByteSocketPlugin.prototype.send = function(aString,ip,port,successC){ 
	//console.log("CoolPlugin.js: showToast"); 
	exec(function(result){ successC(result); },function(result){} ,"ByteSocketPlugin",'send,'+ip+','+port+','+aString,[]);
}
var byteSocketPlugin = new ByteSocketPlugin(); 
module.exports = byteSocketPlugin;
