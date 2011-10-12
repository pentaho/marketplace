function PentahoMarketplace() {
  this.getPlugins = function(solution, path, filename ) {
    var time = new Date().getTime();
    var resultStr = pentahoGet( CONTEXT_PATH + 'content/ws-run/MarketplaceService/getPluginsJson?time=' + time, '', null, 'text/text' );
    // pull the state, status, and message out
    if( !resultStr ) {
        return null;
    }
    var jsonObject = this.getResultMessage(resultStr);
    return jsonObject;
  }

  this.getResultMessage = function( str ) {
    var xml  = this.parseXML(str);
    var nodeList = xml.getElementsByTagName('return');
    if( nodeList.length > 0 && nodeList[0].firstChild ) {
        return nodeList[0].firstChild.nodeValue;
    }
    return null;
  }

  this.installNow = function(pluginId) {
	  var time = new Date().getTime();
	    var resultStr = pentahoGet( CONTEXT_PATH + 'content/ws-run/MarketplaceService/installPluginJson?pluginId=' + pluginId+ '&time=' + time, '', null, 'text/text' );
	    // pull the state, status, and message out
	    if( !resultStr ) {
	        return null;
	    }
	    var jsonObject = this.getResultMessage(resultStr);
	    return jsonObject;
  }
  
  this.uninstall = function(pluginId) {
	  var time = new Date().getTime();
	    var resultStr = pentahoGet( CONTEXT_PATH + 'content/ws-run/MarketplaceService/uninstallPluginJson?pluginId=' + pluginId+ '&time=' + time, '', null, 'text/text' );
	    // pull the state, status, and message out
	    if( !resultStr ) {
	        return null;
	    }
	    var jsonObject = this.getResultMessage(resultStr);
	    return jsonObject;
  }
  
  this.parseXML = function(sText) {
    if( !sText ) {
        return null;
    }
    var xmlDoc;
    try { //Firefox, Mozilla, Opera, etc.
        parser=new DOMParser();
        xmlDoc=parser.parseFromString(sText,"text/xml");
        return xmlDoc;
    } catch(e){
        try { //Internet Explorer
            xmlDoc=new ActiveXObject("Microsoft.XMLDOM");
            xmlDoc.async="false";
            xmlDoc.loadXML(sText);
            return xmlDoc;
        } catch(e) {
        }
    }
    alert('XML is invalid or no XML parser found');
    return null;
  }
}
var pentahoMarketplace = new PentahoMarketplace();

