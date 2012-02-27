
// Init namespaces
wd.marketplace = wd.marketplace || {};
wd.marketplace.panels = wd.marketplace.panels || {};
wd.marketplace.components = wd.marketplace.components || {};
wd.marketplace.actions = wd.marketplace.actions || {};

            
            
/*
 *
 *  Main application and engine
 *
 */ 
            
var marketplace = wd.caf.application({
    name: 'Marketplace', 
    container:"#marketplace", 
    template:'marketplaceTemplate', 
    transition: "basic"
});


wd.marketplace.engine = function(myself,spec){
    
    /** @private*/
    var impl = myself.engine = {};

    var installedPanel,allPanel;
    
    // start
    
    
    
    impl.init = function(){
        
        wd.debug("intializing marketplace panel");
        
        installedPanel = myself.panelEngine.getPanel("installedPluginsPanel");
        allPanel = myself.panelEngine.getPanel("allPluginsPanel");
        
        
        myself.notificationEngine.getNotification().debug("Starting engine");
        
        // Call refresh
        impl.refresh();
        
        
    }
    
    
    impl.refresh = function(){
       
        wd.info("Refreshing");
       
        // 1. Notify panels to show connecting info
        installedPanel.showConnectingComponent();
        allPanel.showConnectingComponent();
        
    // 2. getPliginIn, passing callback
    //impl.getPluginList
    }
    
    
}

// Apply mixin
wd.marketplace.engine(marketplace);



/*
 *
 *  Template
 *
 */ 

wd.marketplace.template = function(spec){
    
    /**
     * Specific specs
     */
    
    var _spec = {
        name:"marketplaceTemplate",
        cssFile: undefined
    }

    spec = $.extend({},_spec,spec);
    var myself = wd.caf.template(spec);


    // myself.addActions = function(){}; // nothing

    /**
     * Creates the main structure of the template
     * @name createMainSections
     * @memberof wd.caf.template
     * 
     */
    myself.createMainSections = spec.createMainSections || function(){
        
        
        var wrapper = $('<div class="templateWrapper"></div>');
        var header  = $('<div class="templateHead"></div>').appendTo(wrapper);

        myself.$logo = $('<div class="templateLogo"></div>').appendTo(header);
        myself.$actions = $('<div class="templateActions"></div>').appendTo(header);
        myself.$panels = $('<div class="templatePanels"></div>').appendTo(header);
        myself.$title = $('<div class="templateTitle">Pentaho Marketplace</div>').appendTo(header);
  
        header.append($('<div class="templateHShadow"></div>'));
        
        myself.$panelsContainer = $('<div class="templatePanelsContainer"></div>')
        .appendTo(wrapper);
        
        
        return wrapper;
        
    }
    


    myself.drawPanelOnContainer = spec.drawPanelOnContainer || function(panel){
    
        var container = $('<div class="panelContainer"></div>').appendTo(myself.$panelsContainer);
        myself.$panelsContainer.append(panel.draw(container));
        panel.setPlaceholder(container);


    }
    

    return myself;
};

marketplace.getRegistry().registerTemplate(wd.marketplace.template());


/*
 *
 *  Actions
 *
 */ 

marketplace.getRegistry().registerAction( wd.caf.action({
    name: "refresh",
    description: "Refresh",
    order: 10,
    executeAction: function(){
        this.caf.engine.refresh();
    }
}) );



/*
 *
 *  Components
 *
 */ 


wd.marketplace.components.label = function(spec){
    
    /**
     * Specific specs
     */
    
    var _spec = {
        name: "label",
        description: "Label",
        cssClass: "label",       
        clickAction: undefined
    }; 
    
    
    spec = $.extend({},_spec,spec);
    //var myself = wd.caf.panel(spec);
    var myself = wd.caf.component(spec);
    
    
    myself.label = spec.label || function(){
        
        return "label";
    }
    
    myself.draw = function($ph){
        
        var $c = $("<div/>").addClass(spec.cssClass)
        .text(typeof myself.label==="function"?myself.label():myself.label)
        
        
        if(typeof spec.clickAction === "function"){
            $c.addClass("cafPointer").click(spec.clickAction);
        }
        
        $c.appendTo($ph);
        
    }
    
    return myself;
};



/*
 *
 *  Panels
 *
 */ 

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
    
    var title =  wd.marketplace.components.label({
        label: spec.description,
        cssClass:"pentaho-titled-toolbar section-header-inner pentaho-padding-sm"
    });


    // Connecting componnet
    
    var connectingComponent = wd.caf.component(
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





// Register the panels

marketplace.getRegistry().registerPanel(wd.marketplace.panels.marketplacePanel({
    name:"installedPluginsPanel",
    description:"Installed plugins",
    order: 10
}));


marketplace.getRegistry().registerPanel(wd.marketplace.panels.marketplacePanel({
    name:"allPluginsPanel",
    description:"Available plugins",
    order: 20
}));


marketplace.getRegistry().registerPanel(wd.caf.impl.panels.underConstruction({
    name:"about",
    description:"About",
    order: 90
}));




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

