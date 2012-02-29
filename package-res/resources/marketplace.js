
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
        
        // Add pentaho style
        $("body").addClass("pentaho-page-background");
        
        wd.debug("intializing marketplace panel");
        
        installedPanel = myself.panelEngine.getPanel("installedPluginsPanel");
        allPanel = myself.panelEngine.getPanel("allPluginsPanel");
        
        
        //myself.notificationEngine.getNotification().debug("Starting engine");
        
        // Call refresh
        impl.refresh();
        
        
    }
    
    
    impl.refresh = function(){
       
        wd.info("Refreshing");
       
        // 1. Notify panels to show connecting info
        installedPanel.showConnectingComponent();
        allPanel.showConnectingComponent();
        
        // 2. getPluginList, passing callback
        impl.getPluginList();
    
    }
    
    impl.getAllPanel = function(){
        return allPanel;
    }
    
    impl.getPluginList = function(){
        
        // Replace this later with the real deal
        wd.warn("Getting plugin list - Not done yet");
        
        
        $.ajax({
            url: "pluginList.json",
            dataType: 'json',
            data: [],
            success: impl.processPluginListResponse,
            error: impl.errorUpdating
        });
        
        
    }
    
    
    impl.processPluginListResponse = function(json){
        
        wd.log("Response: " + json);
        
        // 1. Clean plugins
        installedPanel.cleanPlugins();
        allPanel.cleanPlugins();

        // 2. Add plugins to panel

        json.map(function(plugin){
           
            allPanel.addPlugin(plugin);
            if(plugin.installed){
                installedPanel.addPlugin(plugin);
            }
            
        });

    }
    
    impl.errorUpdating = function(jqXHR, textStatus, errorThrown){
        
        myself.notificationEngine.getNotification().error("Error updating - try again later: " + errorThrown);
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
        myself.$title = $('<div class="templateTitle contrast-color">Pentaho Marketplace</div>').appendTo(header);
  
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
    // TODO: put some fade effects?

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


wd.marketplace.components.plugin = function(spec){
    
    /**
     * Specific specs
     */
    
    var _spec = {
        name: "plugin",
        description: "Plugin",
        cssClass: "plugin",       
        clickAction: undefined
    }; 
    
    
    spec = $.extend({},_spec,spec);
    var myself = wd.caf.component(spec);

    var pluginInfo, panel, pluginHeader, pluginBody, isShown=false;
    
    // containers
    var $wrapper;
    
    
    myself.setPluginInfo = function(_pluginInfo){
        pluginInfo = _pluginInfo;
    }
    
    
    myself.getPluginInfo = function(){
        return pluginInfo;
    }
  
    myself.setPanel = function(_panel){
        panel = _panel;
    }

    myself.getPanel = function(){
        return panel;
    }

    
    myself.draw = function($ph){
        
        // Wrapper
        
        $wrapper = $("<div/>").addClass(spec.cssClass + " pluginWrapper clearfix")
        .data("plugin",myself) // store it for convenience;

        // Plugin header
        pluginHeader = wd.marketplace.components.pluginHeader({
            clickAction: function(){
                panel.pluginHeaderClicked(myself);
            }
        });
        pluginHeader.init(myself.caf);
        
        pluginHeader.setPluginInfo(pluginInfo);
        pluginHeader.draw($wrapper);
        
        // pluginBody
        pluginBody = wd.marketplace.components.pluginBody();
        pluginBody.init(myself.caf);
        pluginBody.setPluginInfo(pluginInfo);
        pluginBody.draw($wrapper);
        

        $wrapper.appendTo($ph);
        
    }


    myself.isShown = function(){
        return isShown;
    }

    myself.hide = function(){
        
        if(!myself.isShown()){
            return; // already hidden
        }
        
        pluginHeader.unselect();
        pluginBody.hide()
        isShown = false;
    }
    
    
    myself.show = function(){

        pluginHeader.select();
        if(myself.isShown()){
            return; // already shown
        }

        pluginBody.show()
        isShown = true;
    }

    myself.toggleVisibility = function(){
        
        isShown?myself.hide():myself.show();
        
    }


    return myself;
};


wd.marketplace.components.pluginHeader = function(spec){
    
    /**
     * Specific specs
     */
    
    var _spec = {
        name: "pluginHeader",
        description: "Plugin Header",
        cssClass: "pluginHeader",
        clickAction: undefined
    }; 
    
    
    spec = $.extend({},_spec,spec);
    var myself = wd.caf.component(spec);

    // BlueprintMixin
    wd.caf.modules.blueprintPanelModule(myself);

    var pluginInfo;

    // Containers
    var $wrapper, $versionWrapper;
    
    myself.setPluginInfo = function(_pluginInfo){
        pluginInfo = _pluginInfo;
    }
    
    
    myself.getPluginInfo = function(){
        return pluginInfo;
    }
    
    myself.isUpdateAvailable = function(){
        return pluginInfo.availableVersion == pluginInfo.installedVersion;
    }
    
    myself.draw = function($ph){
        
        // Wrapper
        
        $wrapper = myself.generateBlueprintStructure().addClass(spec.cssClass).appendTo($ph);
        myself.update();

        
    }
    
    
    myself.update = function(){
        
        $wrapper.empty();
        $("<div/>").addClass("pluginHeaderLogo pluginGradient").text(pluginInfo.id).appendTo($wrapper);
        
        $("<div/>").addClass("pluginHeaderTitleWrapper pentaho-titled-toolbar pentaho-padding-sm pentaho-background contrast-color").append(
            $("<div/>").addClass("pluginHeaderTitle").text(pluginInfo.name).appendTo($wrapper))
        .append(
            $("<div/>").addClass("pluginHeaderUpdates " + ( myself.isUpdateAvailable()?"pluginHeaderUpdatesAvailable":"pluginHeaderUpdatesUpdated" ))
            .text(myself.isUpdateAvailable()?"Updates available":"Updated version").appendTo($wrapper)    
            ).appendTo($wrapper);
            
            
        var $versionWrapper = $("<div/>").addClass("pluginHeaderVersionWrapper " + 
            (myself.isUpdateAvailable()?"pluginHeaderVersionAvailable pluginGradientGreen":"pluginHeaderVersionUpdated pluginGradient"))
        .appendTo($wrapper);
        
        $("<div/>").addClass("pluginHeaderVersionLabel").text(pluginInfo.installedBranch+"").appendTo($versionWrapper);
        $("<div/>").text(pluginInfo.installedVersion+"").appendTo($versionWrapper);


        if(typeof spec.clickAction === "function"){
            $wrapper.click(spec.clickAction);
        }

        
    }
    
    myself.select = function(){
        $wrapper.addClass("pluginSelected");
    }

    myself.unselect = function(){
        $wrapper.removeClass("pluginSelected");
    }
    
    return myself;

}


wd.marketplace.components.pluginBody = function(spec){
    
    /**
     * Specific specs
     */
    
    var _spec = {
        name: "pluginBody",
        description: "Plugin Body",
        cssClass: "pluginBody"
    }; 
    
    
    spec = $.extend({},_spec,spec);
    var myself = wd.caf.component(spec);

    // BlueprintMixin
    wd.caf.modules.blueprintPanelModule(myself);


    var pluginInfo;

    // Containers
    var $element, $wrapper, $pluginBodyDescWrapper, $pluginBodyInstallWrapper, 
    $installedVersion, $availableVersions;
    
    myself.setPluginInfo = function(_pluginInfo){
        pluginInfo = _pluginInfo;
    }
    
    
    myself.getPluginInfo = function(){
        return pluginInfo;
    }
    
    myself.isUpdateAvailable = function(){
        return pluginInfo.availableVersion == pluginInfo.installedVersion;
    }
    
    myself.draw = function($ph){
        
        // Create a full wrapper and one for the animation
        $element = myself.generateBlueprintStructure().addClass(spec.cssClass).appendTo($ph);
        $wrapper = $("<div/>").addClass("pluginBodyVisibilityToggle").appendTo($element);
        
        // On draw, this will be collapsed
        if(Modernizr.csstransitions){
            
            myself.hide();

        }
        else{
            $element.addClass("marketplaceHidden");
        }
        
        myself.update();

        
    }
    
    
    myself.update = function(){
        
        $wrapper.empty();
        
        
        // Wrapper for description
        
        $pluginBodyDescWrapper = $("<div/>").addClass("pluginBodyDescWrapper clearfix").appendTo($wrapper)
        .append($("<div/>").addClass("pluginBodyDescLogo prepend-1 span-4 append-1").text("Logo"))
        .append($("<div/>").addClass("pluginBodyDescDesc span-18 last")
            .append($("<div/>").addClass("pluginBodyTitle").text("Information"))
            .append($("<div/>").addClass("pluginBodyDescription").text(pluginInfo.description))
            .append())
        ;
        
        // Wrapper for pluginBodyInstall
        $pluginBodyInstallWrapper = undefined; // todo
        
        
        // Current version
        $installedVersion = $("<div/>").addClass("pluginVersions installedVersion clearfix");
        
        var $installedVersionWrapper = $("<div/>").addClass("currentVersion prepend-1 span-4 append-1").append(
            $("<div/>").addClass("pluginBodyTitle").text("Installed Version")
            )
        .append($installedVersion)
        .appendTo($wrapper);
        
        
        // Available versions
        $availableVersions = $("<div/>").addClass("pluginVersions availableVersions clearfix");
        
        if(pluginInfo.versions){
        
            var $availableVersionsWrapper = $("<div/>").addClass("currentVersion span-18 last").append(
                $("<div/>").addClass("pluginBodyTitle").text("Available Versions")
                )
            .append($availableVersions)
            .appendTo($wrapper);

        
            pluginInfo.versions.map(function(v){
                wd.marketplace.components.pluginVersion({
                    cssClass: "pluginVersion",
                    pluginVersion: v,
                    clickAction: function(){
                        myself.caf.notificationEngine.getNotification().info("Showing details");
                    }
                }).draw($availableVersions);
            })
        }
        
        
        // Add version components
        // wd.marketplace.components.pluginVersion({pluginVersion: })
        


        //debugger;


        // Add footer
        var footerContent = $("<div/>").addClass("pluginBodyFooterContent")
        .appendTo($("<div/>").addClass("clearfix pluginBodyFooter span-22 prepend-1 append-1 last").appendTo($wrapper))
        
        // we'll put company logo or name, and a link if we have it
        var content = pluginInfo.companyLogoUrl?'<img src="'+pluginInfo.companyLogoUrl+'" />':pluginInfo.company;
        if(pluginInfo.companyUrl){
            footerContent.append('<a href="'+pluginInfo.companyUrl+'" target="_blank">'+content+'</a>');
        }
        else{
            footerContent.append(content);
        }



    /*
        
        var $versionWrapper = $("<div/>").addClass("pluginBodyVersionWrapper " + 
            (myself.isUpdateAvailable()?"pluginBodyVersionAvailable":"pluginBodyVersionUpdated"))
        .appendTo($wrapper);
        
        $("<div/>").text("Version").appendTo($versionWrapper);
        $("<div/>").text(pluginInfo.availableVersion+"").appendTo($versionWrapper);

        $("<div/>").addClass("pluginBodyUpdates")
        .text(myself.isUpdateAvailable()?"Updates available":"Updated version").appendTo($wrapper);
        */

        
    }
    
    
    myself.hide = function(){
        
        if(Modernizr.csstransitions){
            
            $wrapper.css("margin-top","-400px").addClass("marketplaceTransparent");

        }
        else{
            $element.hide();
        }
        
    }
    
    
    myself.show = function(){
        
        if(Modernizr.csstransitions){  
        
            $wrapper.css("margin-top","0px").removeClass("marketplaceTransparent");
            
        }
        else{
            $element.show();
        }        
        
    }


    return myself;

}


wd.marketplace.components.pluginVersion = function(spec){
    
    /**
     * Specific specs
     */
    
    var _spec = {
        name: "pluginVersion",
        description: "Plugin Version",
        cssClass: "pluginVersion",
        pluginVersion: "",
        clickAction: undefined
    }; 
    
    
    spec = $.extend({},_spec,spec);
    var myself = wd.caf.component(spec);

    var $wrapper;


    myself.draw = function($ph){
        
        $wrapper = $("<div/>").addClass(spec.cssClass)
        .append($("<span/>").addClass("pluginVersionNumber").text(spec.pluginVersion.version))
        .append($("<span/>").addClass("pluginVersionBranch").text(" (" + spec.pluginVersion.name + ")"))
        
        if(typeof spec.clickAction === "function"){
            $wrapper.addClass("cafPointer");
            $wrapper.click(spec.clickAction);
        }

        $wrapper.appendTo($ph);

        
    }
    
    return myself;
}


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
    var $panel, $mainContent, plugins = [];
    
    // BlueprintMixin
    wd.caf.modules.blueprintPanelModule(myself);


    // Components
    
    var title =  wd.marketplace.components.label({
        label: spec.description,
        cssClass:"marketplacePanelTitle pentaho-titled-toolbar pentaho-padding-sm pentaho-rounded-panel2 pentaho-background contrast-color" 
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
        
        
        //$panel = myself.generateBlueprintStructure().appendTo($ph);
        $panel = $("<div/>").appendTo($ph);
        
        // Title
        title.draw($panel);
        
        
        $mainContent = $('<div/>').addClass("marketplacePanel");
        $panel.append($mainContent);
        
        
        
    }


    myself.cleanPlugins = function(){
        
        plugins = [];
        $mainContent.empty();
    
    }
    
    
    myself.getPlugins = function(){
        return plugins;
    }


    myself.showConnectingComponent = function(){
        
        connectingComponent.draw($mainContent.empty());
        
    }


    myself.addPlugin = function(pluginInfo){
        
        var plugin = wd.marketplace.components.plugin();
        plugin.init(myself.caf);
        plugin.setPluginInfo(pluginInfo);
        plugin.setPanel(myself);
        
        // Add it
        plugin.draw($mainContent);
        plugins.push(plugin);
        
    }
    
    
    myself.pluginHeaderClicked = function(plugin){
        
        // Loop through all plugins. If shown, hide. On the specific plugin, toggle visibility
        myself.getPlugins().map(function(p){
            p==plugin?p.toggleVisibility():p.hide();
        })
        
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

