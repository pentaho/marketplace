
// Init namespaces
wd.marketplace = wd.marketplace || {};
wd.marketplace.panels = wd.marketplace.panels || {};
wd.marketplace.components = wd.marketplace.components || {};
wd.marketplace.actions = wd.marketplace.actions || {};

            
var marketplace = wd.caf.application({
    name: 'Marketplace', 
    container:"#marketplace", 
    template:'simple', 
    transition: "basic"
});


wd.marketplace.engine = function(myself,spec){
    
    /** @private*/
    var impl = myself.engine = {};

    var marketplacePanel
    
    // start
    
    
    
    impl.init = function(){
        
        wd.debug("intializing marketplace panel");
        marketplacePanel = myself.panelEngine.getPanel("marketplacePanel");
        
        myself.notificationEngine.getNotification().debug("Starting engine");
        
        // Call update
        impl.update();
        
        
    }
    
    
    impl.update = function(){
        
        // 1. Notify panel to show connecting info
        marketplacePanel.showConnectingComponent();
        
    // 2. getPanelInfo, passing callback
        
    }
    
    
}


// Apply mixin
wd.marketplace.engine(marketplace);



// Components

wd.marketplace.components.label = function(spec){
    
    /**
     * Specific specs
     */
    
    var _spec = {
        name: "marketplaceTitle",
        description: "Marketplace title",
        cssClass: "",
        order: 10,
        color: "red"
    }; 
    
    
    spec = $.extend({},_spec,spec);
    //var myself = wd.caf.panel(spec);
    var myself = wd.caf.component(spec);
    
    
    myself.label = spec.label || function(){
        
        return "label";
    }
    
    myself.draw = function($ph){
        $("<div/>").addClass(spec.cssClass).text(typeof myself.label==="function"?myself.label():myself.label).appendTo($ph);
    }
    
    return myself;
};



// Add marketplacePanel

wd.marketplace.panels.marketplacePanel = function(spec){
  
        
    /**
     * Specific specs
     */
    
    var _spec = {
        name: "marketplacePanel",
        description: "Marketplace",
        order: 10,
        color: "red"
    };

    spec = $.extend({},_spec,spec);
    var myself = wd.caf.panel(spec);
    
    
    // Vars
    var $panel, $mainContent;
    
    // BlueprintMixin
    wd.caf.modules.blueprintPanelModule(myself);


    // Components
    
    var title = wd.marketplace.components.label({
        label: "Title",
        cssClass:"marketplaceTitle"
    });

    // Connecting componnet
    var connectingComponent = wd.marketplace.components.connectingComponent(
    {
        name: "connectingComponent",
        description: "Connecting component info",
        cssClass: "connectingComponent",
        draw: function($ph){
            $("<div/>").addClass(spec.cssClass).text("Connecting to server").appendTo($ph);
        }
    
    })
      
      
    /**
     * Describes this interface
     * @name panel.init
     * @memberof wd.caf.panel
     */
    myself.draw = spec.draw || function($ph){
        
        $panel = myself.generateBlueprintStructure().appendTo($ph);
        
        // Title
        title.draw($panel);
        
        
        $mainContent = $('<div/>').addClass("marketplacePanel");
        $panel.append($mainContent);
        
        
        
    }


    myself.showConnectingComponent = function(){
        
        connectingComponent.draw($mainContent.empty());
        
    }

    return myself;
    
    
};





// Register it
marketplace.getRegistry().registerPanel(wd.marketplace.panels.marketplacePanel());


$(function(){
    marketplace.init();
    marketplace.engine.init();
})


























/* Old stuff */


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

    this.installNow = function(pluginId, versionId) {
        var time = new Date().getTime();
          
        var resultStr = pentahoGet( CONTEXT_PATH + 'content/ws-run/MarketplaceService/installPluginJson?pluginId=' + pluginId+ (versionId !== undefined? '&versionId=' +versionId:"") + '&time=' + time, '', null, 'text/text' );
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

