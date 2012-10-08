
// Init namespaces
wd.marketplace = wd.marketplace || {};
wd.marketplace.panels = wd.marketplace.panels || {};
wd.marketplace.components = wd.marketplace.components || {};
wd.marketplace.actions = wd.marketplace.actions || {};
wd.marketplace.popups = wd.marketplace.popups || {};

            
            
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
        
        $("html").addClass(active_theme);
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
        
        
        $.ajax({
            url: "../getpluginsjson",
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
    
    
    impl.installPlugin = function(pluginId, branchId, callbackSuccess, callbackError){

        wd.info("Marketplace engine: installing plugin: " + pluginId + " (" + branchId + ")");
        
        $.ajax({
            url: "../installpluginjson",
            dataType: 'json',
            data: {
                pluginId: pluginId, 
                versionId: branchId
            },
            success: function(jqXHR, textStatus){
                if ( jqXHR.code !== 'PLUGIN_INSTALLED'){
                    callbackError(jqXHR, textStatus);
                } else {
                    callbackSuccess(jqXHR, textStatus);
                }
            },
            error: callbackError
        });
        
    }
    

    impl.uninstallPlugin = function(pluginId, callbackSuccess, callbackError){
        
        wd.info("Marketplace engine: uninstalling plugin: " + pluginId);
        
        $.ajax({
            url: "../uninstallpluginjson",
            dataType: 'json',
            data: {
                pluginId: pluginId
            },
            success: function(jqXHR, textStatus){
                if ( jqXHR.code !== 'PLUGIN_UNINSTALLED'){
                    callbackError(jqXHR, textStatus);
                } else {
                    callbackSuccess(jqXHR, textStatus);
                }
            },
            error: callbackError
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
        myself.$title = $('<div class="templateTitle contrast-color"></div>')
           
        .appendTo(header);
  
        header.append($('<div class="templateHShadow"></div>'));
        
        myself.$toggleActionContainer = $('<div class="templateToggleActionContainer"></div>')
        .appendTo(wrapper);
        
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
    description: "<div class='actionRefresh'>&nbsp;</div>",
    order: 10,
    executeAction: function(){
        this.caf.engine.refresh();
    }
}) );








wd.marketplace.actions.toggleAction = function(spec){
    
    /**
     * Specific specs
     */
    
    var _spec = {
        name: "about",
        description: "About action",
        toggleText: "Information",
        order: 110,
        container: undefined 
    };

      
    spec = $.extend({},_spec,spec);    
    var myself = wd.caf.action(spec);
   
    var $actionPh;
   
    
    myself.init = function(caf){

        myself.log("Generic entity init","debug");
        myself.caf = caf;
        
        if (!$actionPh){
            myself.setupAction();
        }   
    }
    
    /**
     * File operations execute action
     * @name executeAction
     * @memberof wd.caf.impl.actions.alertAction
     * 
     */

    myself.executeAction = function(){

        myself.toggle();
        
    } 
    
    
    myself.hide = function (){   
        if(Modernizr.csstransitions){          
            $actionPh.addClass("marketplaceTransparent marketplaceMoveOut");
        }
        else{
            $actionPh.hide();
        }    
    }
    
    myself.show = function (){  
        if(Modernizr.csstransitions){          
            $actionPh.removeClass("marketplaceTransparent marketplaceMoveOut");
        }
        else{
            $actionPh.show();
        } 
    }
    
    myself.toggle = function (){  
        if(Modernizr.csstransitions){          
            $actionPh.toggleClass("marketplaceTransparent marketplaceMoveOut");
        }
        else{
            $actionPh.toggle();
        }  
    }
     
    myself.setupAction = function(){
        
        
        // TODO: Change the way the placeholder is passed? 
        //       This way the action is not dependent on the template.
        var container = spec.container || myself.caf.templateEngine.getTemplate().$toggleActionContainer;
        
        $actionPh = $('<div/>').addClass('toggleActionWrapper marketplaceTransparent marketplaceMoveOut')
        .append( $('<div/>').addClass('toggleActionLogo') )
        .append( $('<div/>').addClass('toggleActionDesc')
            .text( spec.toggleText ) )
        .append( $('<div/>').addClass('toggleActionDevBy') );
       
        container.append($actionPh);
    }
        
    return myself;
        
};

marketplace.getRegistry().registerAction( wd.marketplace.actions.toggleAction({
    name: "about",
    description: "<div class='actionAbout'>&nbsp;</div>",
    toggleText: 'Pentaho Marketplace plugin allows you to browse through available plugins and customize your Pentaho installation. Enjoy!',
    order: 20,
    container: marketplace.getRegistry()
    .getTemplate( marketplace.options.template ).$toggleActionContainer
}) );




/*
 *
 *  Components
 *
 */ 

wd.marketplace.components.infoDiv = function(spec) {


    /**
     * Specific specs
     */
    
    var _spec = {
        name: "override",
        type: "component",
        description: "override description",
        pulsatePeriod: undefined
    };
    
    spec = $.extend({},_spec,spec);
    var myself = wd.caf.component(spec);
    
    var isDrawn;
   
    myself.draw = function ($ph) {
        if (!isDrawn){
            var $actionPh = $('<div/>').addClass('infoDivWrapper').text( spec.description );
            $ph.append($actionPh);
            
            isDrawn = true;
            
            if (spec.pulsatePeriod){
                myself.pulsate($actionPh);
            }
        }   
    }
    
    myself.pulsate = function ($ph) {
        $ph.addClass('infoDivPulsate');
        
        setTimeout ( function(){
            $ph.removeClass('infoDivPulsate')
        },2500);
        
        setTimeout(function(){
            myself.pulsate($ph);
        }, spec.pulsatePeriod || 5000);
        
    }
    
    return myself;
};

marketplace.getRegistry().registerEntity('components', wd.marketplace.components.infoDiv({
    name: 'restart',
    description: 'Please restart the server now.',
    pulsatePeriod: 5000
}));




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
    
    var installationStatus = {
        
        UPTODATE:{
            description: "Up to date",
            cssClass: "pluginStatusUpToDate"
        },
        UPDATEAVAILABLE:{   
            description: "Update available",
            cssClass: "pluginStatusUpdateAvailable"
        },
        INSTALLED:{
            description: "Installed",
            cssClass: "pluginStatusInstalled"
        },
        NOTINSTALLED: {
            description: "Not installed",
            cssClass: "pluginStatusNotInstalled"
        },
        INSTALLING:{
            description: "Installing",
            cssClass: "pluginStatusInstalling"
        },
        UNINSTALLING:{
            description: "Uninstalling",
            cssClass: "pluginStatusUninstalling"
        }
    }
    
    
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
        
        pluginHeader.setPlugin(myself);
        pluginHeader.draw($wrapper);
        
        // pluginBody
        pluginBody = wd.marketplace.components.pluginBody({
            installAction: function(branch){
                panel.installPlugin(myself,branch);
            },
            uninstallAction: function(){
                panel.uninstallPlugin(myself);
            }
        });
        pluginBody.init(myself.caf);
        pluginBody.setPlugin(myself);
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
    
    //returns main plugin context excluding versions
    myself.getPluginMainContent = function(){
    	var version = {
        	//creator info
        	company: pluginInfo.company,
        	companyLogo: pluginInfo.companyLogo,
        	companyUrl: pluginInfo.companyUrl,
        	
        	//plugin info
        	name: pluginInfo.name,
        	description: pluginInfo.description,
        	dependencies: pluginInfo.dependencies,
        	license: pluginInfo.license,
        	img: pluginInfo.img,
        	smallImg: pluginInfo.smallImg,
        	screenshots: pluginInfo.screenshots,
        	
        	installed: pluginInfo.installed
        };
        
        return version;
    }
    
    //returns main plugin context excluding versions
    myself.getPluginVersions = function(){
    	return pluginInfo.versions;
    }
    
    //returns installed version main content
    myself.getInstalledVersion = function(){

        if(!pluginInfo.installed){
            return null;
        }

        // Directly return an object that shows the installed version, filling in the details
        // digest the current json and return only the needed info.
        var version = {      	
        	//installed version info
        	branch: pluginInfo.installedBranch,
        	buildId: pluginInfo.installedBuildId,
        	version: pluginInfo.installedVersion
        };
        
        
        // Try to find the name
        pluginInfo && $.isArray(pluginInfo.versions) && pluginInfo.versions.map(function(v){
            if(v.branch == version.branch){
                version.changeLog = v.changeLog;
                version.versionDescription = v.description;
                version.releaseDate = v.releaseDate;
                return false;
            }
        });
        
        // if not found, default to branch id
        if(!version.name){
            version.name = version.installedBranch;
        }
        
        return version;
        
    }

    myself.getDefaultVersion = function(){
        var version = pluginInfo.versions[0]; //TODO: Default is not necessarily the first on the list
        
        return version;
        
    }
        
    
    myself.getInstallationStatus = function(){
        
        // We'll compare the branch / version we have with the version in the same branch
        var installedVersion = myself.getInstalledVersion();
        
        // If nothing is installed, I can mark it as available TODO: implement other state
        if(!installedVersion){
            return installationStatus["NOTINSTALLED"];
        }
        
        
        
        // No versions available? 
        if (!myself.getPluginVersions() || myself.getPluginVersions().length == 0){
            return installationStatus["INSTALLED"];
        }
        
        // loop through the others
        var isUpdateAvailable;
        myself.getPluginVersions().map(function(v){
            if(v.branch == installedVersion.branch){
                
                if(v.version != installedVersion.version){
                    isUpdateAvailable = true;    
                }
                else{
                    // do we have buildId?
                    if(v.buildId){
                        isUpdateAvailable = v.buildId != installedVersion.buildId;
                    }
                    else{
                        isUpdateAvailable = false;
                    }
                }
                return false;
            }
        })
        
        return installationStatus[isUpdateAvailable?"UPDATEAVAILABLE":"UPTODATE"];
        
        
    };
    

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

    var plugin;

    // Containers
    var $wrapper, $buttonWrapper;
    
    myself.setPlugin = function(_plugin){
        plugin = _plugin;
    }
    
    
    myself.getPlugin = function(){
        return plugin;
    }
    
    
    myself.getInstallationStatus = function(){
        
        return plugin.getInstallationStatus();
        
    }
    
    myself.draw = function($ph){
        
        // Wrapper
        
        $wrapper = myself.generateBlueprintStructure().addClass(spec.cssClass).appendTo($ph);
        myself.update();

        
    }
    
    
    myself.update = function(){
        

        var installationStatus = plugin.getInstallationStatus();

        $wrapper.empty();
        
      /*  $("<div/>").addClass("pluginHeaderTitleWrapper pentaho-titled-toolbar pentaho-padding-sm pentaho-background contrast-color pentaho-rounded-panel2")
        .append(
            $("<div/>").addClass("pluginHeaderLogo").append( $("<img/>").attr('src', plugin.getPluginInfo().smallImg || plugin.getPluginInfo().img))
            .appendTo($wrapper))
        .append(
            $("<div/>").addClass("pluginHeaderTitle").text(plugin.getPluginInfo().name).appendTo($wrapper))
        .append(
            $("<div/>").addClass("pluginHeaderUpdates " + installationStatus.cssClass)
            .text(installationStatus.description).appendTo($wrapper)    
            ).appendTo($wrapper);*/

        $("<div/>").addClass("pluginHeaderTitleWrapper ")
          .append($("<div/>").addClass("pluginHeaderTitle").text(plugin.getPluginInfo().name ) )
          .append($("<div/>").addClass("pluginHeaderVersion").text(plugin.getPluginInfo().installedVersion ) )
        .appendTo($wrapper);
            
            
        var $buttonWrapper = $("<div/>").addClass("pluginHeaderButtonWrapper ").addClass('pluginHeaderVersionWrapper') //TODO: remove this last one
        .appendTo($wrapper);
        

        if(plugin.getPluginInfo().installed){
         
            $("<div/>").addClass("pluginHeaderVersionLabel")
                .text( !plugin.getPluginInfo().installedBranch ? "" : plugin.getPluginInfo().installedBranch )
                .appendTo($buttonWrapper);
            $("<div/>")
                .text( !plugin.getPluginInfo().installedVersion ? "" : plugin.getPluginInfo().installedVersion )
                .appendTo($buttonWrapper);
        }
        else{
            $("<div/>").addClass("pluginHeaderVersionNotInstalled").text("Not installed").appendTo($buttonWrapper);
        }

        if(typeof spec.clickAction === "function"){
            $wrapper.find('.pluginHeaderTitleWrapper').click(spec.clickAction);
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
        cssClass: "pluginBody",
        installAction: undefined,
        uninstallAction: undefined
    }; 
    
    
    spec = $.extend({},_spec,spec);
    var myself = wd.caf.component(spec);

    // BlueprintMixin
    wd.caf.modules.blueprintPanelModule(myself);

  

    var plugin, selectedVersion;

    // Containers
    var $element, $wrapper, 
        $pluginBodyTop, $pluginBodyDetailsArea, $pluginBodyBottom;

    // Top Elements
    var $closeButtonObj;
    
    // Details Area   
    var pluginVersionDetails;

    // Bottom Elements
    var $versionSelectorObj, $installButtonObj, $updateButtonObj, $uninstallButtonObj;



    myself.setPlugin = function(_plugin){
        plugin = _plugin;
    }
    
    
    myself.getPlugin = function(){
        return plugin;
    }

    
    myself.draw = function($ph){
        
        // Create a full wrapper and one for the animation
        $element = myself.generateBlueprintStructure().addClass(spec.cssClass).appendTo($ph);
        $wrapper = $("<div/>").addClass("pluginBodyVisibilityToggle").appendTo($element);
        
        // On draw, this will be collapsed
      /*  if(Modernizr.csstransitions){  
          myself.hide();
        }
        else{
            $element.addClass("marketplaceHidden");
        }
      */  // TODO: modify this when details area is final, to
       
        var mainPluginContent = plugin.getPluginMainContent(),
        	version = ( mainPluginContent.installed) ? plugin.getInstalledVersion() : plugin.getDefaultVersion(),
        	v = $.extend({},mainPluginContent,version);
        
        
        myself.selectVersion(v); 
        myself.update();

        
    }
    
    
    myself.update = function(){
        
        $wrapper.empty();

        
        // Top title and close buttom
        $pluginBodyTop = $("<div/>").addClass("pluginBodyTop clearfix").appendTo($wrapper);
        
        var pluginInfo = plugin.getPluginInfo();

         $("<div/>").addClass("pluginHeaderTitleWrapper")
          .append($("<div/>").addClass("pluginHeaderTitle").text(pluginInfo.name ) )
          .append($("<div/>").addClass("pluginHeaderVersion").text(pluginInfo.installedBuildId != undefined? pluginInfo.installedVersion + ' ('+ pluginInfo.installedBuildId +')' : pluginInfo.installedBranch + ' (' + pluginInfo.installedVersion + ')' ) )
        .appendTo($pluginBodyTop);

        var $closeButtonObj = $("<div/>").addClass("pluginHeaderButtonWrapper ").addClass('pluginHeaderVersionWrapper') //TODO: remove this last one when css is working
        .appendTo($pluginBodyTop);

        wd.marketplace.components.pluginButton({
                    cssClass: "closeButton ",
                    label: "Close",
                    clickAction: function () {
                      myself.log( "This one needs to close the details section. Implement");
                      // TODO: implement this thingie.
                    },
                    image: "img/close.png"
        }).draw($closeButtonObj);
        

        // Wrapper for detailsArea
        $pluginBodyDetailsArea = $("<div/>").addClass("pluginBodyDetailsArea clearfix").appendTo($wrapper);

        pluginVersionDetails = wd.marketplace.components.pluginVersionDetails({});
        pluginVersionDetails.setPluginVersion(selectedVersion);
        pluginVersionDetails.draw($pluginBodyDetailsArea);

        // Add footer
        $pluginBodyBottom = $("<div/>").addClass("pluginBodyBottom clearfix").appendTo($wrapper);
        $versionSelectorObj =  $("<div/>").addClass("pluginVersionSelectorContainer span-6").appendTo($wrapper);
        $installButtonObj =  $("<div/>").addClass("pluginButton span-4").appendTo($wrapper);
        $updateButtonObj =  $("<div/>").addClass("pluginButton span-4").appendTo($wrapper);
        $uninstallButtonObj =  $("<div/>").addClass("pluginButton span-4").appendTo($wrapper);
        

        if(plugin.getPluginInfo().versions){
            var selector = wd.marketplace.components.pluginVersionSelector({
                cssClass: "pluginVersionSelector",
                availableVersions: plugin.getPluginInfo().versions ,
                changeAction: myself.selectVersion
            });
            selector.setPluginVersion(selectedVersion);
            selector.draw($versionSelectorObj);
        }

        wd.marketplace.components.pluginButton({
            cssClass: "pluginButton",
            label: "Install",
            clickAction: function(){ 
                spec.installAction(selectedVersion.branch);
            }
        }).draw($installButtonObj);

        wd.marketplace.components.pluginButton({
        	cssClass: "pluginButton",
            label: "Update",
            clickAction: function(){ 
                var v = plugin.getInstalledVersion();
                spec.installAction( v.branch ) ;
            }
        }).draw($updateButtonObj);

        wd.marketplace.components.pluginButton({
            cssClass: "pluginButton",
            label: "Uninstall",
            clickAction: spec.uninstallAction
        }).draw($uninstallButtonObj);



        
    }
    
    
    myself.selectVersion = function(version){
        
        myself.log("Selecting branch: " + version.branch);
        
        selectedVersion = version;  
           
        if(pluginVersionDetails != undefined) {
        	pluginVersionDetails.setPluginVersion(selectedVersion);   
        	pluginVersionDetails.update();
        }
    }
    
    myself.showBodyDesc = function(){   
        myself.log("showBodyDesc");
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

        // Always force body desc to show
        myself.showBodyDesc();
        
        if(Modernizr.csstransitions){  
            $wrapper.css("margin-top","0px").removeClass("marketplaceTransparent");
        }
        else{
            $element.show();
        }        
        

    }


    return myself;

}

wd.marketplace.components.pluginVersionDetails = function(spec){
    
    //
    // Specific specs
    //
    
    var _spec = {
        name: "pluginVersionDetails",
        description: "Plugin Version Details",
        cssClass: "pluginVersionDetails"
    }; 
    
    
    spec = $.extend({},_spec,spec);
    var myself = wd.caf.component(spec);
    
    var propertyMappingLeft = {
        companyUrl:{
            label: "Created by"
        },
        version:{
            label: "Version"
        },
        license: {
            label: "License"
        },
        dependencies:{
            label: "Dependencies"
        }
    }
    
    var propertyMappingRight = {
    	description:{
            label: "Description"
        },
        versionDescription:{
        },
        screenshots:{
        	label: "Screenshots"
        }
    }

    var pluginVersion;
    
    myself.getPluginVersion = function(){
    	return pluginVersion;
    }
    
    myself.setPluginVersion = function(_pluginVersion){
    	pluginVersion = _pluginVersion;
    }

    myself.draw = function($ph){
        
        myself.setPlaceholder($ph);
        myself.update();
        
    }
    
    
    
    myself.update = function(){
        
        var $ph = myself.getPlaceholder().empty(); 
        


        //TODO: Temp. This 'if' might not be necessary

        if (pluginVersion ){
        
        	//build left side
        	var $leftSide = $("<div/>");
        	$leftSide.addClass("pluginVersionLeft");
        	$ph.append($leftSide);
        	
    		$("<img/>").attr('src',pluginVersion.img).appendTo($leftSide); 
    		$("<div style='height: 20px'><div class='infoDesc'>INFO</div> <div class='infoIcon'></div></div>").appendTo($leftSide);   
        	
        	$.each(propertyMappingLeft, function(prop){
        		var label = $("<div/>").addClass("clearfix pluginVersionPropLabel"),
        			value = $("<div/>").addClass("clearfix pluginVersionPropValue");
        			
        		label.text(propertyMappingLeft[prop].label);
        		value.text(pluginVersion[prop]);
        		
        		$leftSide.append(label);
        		$leftSide.append(value);
        	});
        	
        	//build right side
        	var $rightSide = $("<div/>");
        	$rightSide.addClass("pluginVersionRight last");
        	$ph.append($rightSide);
        	
        	//append description
        	$("<div/>").text("Description").appendTo($rightSide);
        	$("<div/>").html(pluginVersion.description + " <br/> <br/> " + pluginVersion.versionDescription).appendTo($rightSide);
        	
        	//append screenshots
        	$("<div/>").text("Screenshots").appendTo($rightSide);
        	var $screenshots = $("<div/>").addClass("screenshots"),
        		$screenshotHolder = $("<div/>").addClass("screenshotsHolder");
        		
        	$screenshotHolder.appendTo($screenshots);
        	
        	$.each(pluginVersion.screenshots, function(i){
        		var $imgPh = $("<a/>").addClass('image fancybox').attr('rel','gallery1').attr('href',pluginVersion.screenshots[i]);
        		$("<img/>").attr('src',pluginVersion.screenshots[i]).attr('height',75).attr('width',150).appendTo($imgPh);   
        		$screenshotHolder.append($imgPh);     	
        	}); 
        	
        	$screenshotHolder.css('width',pluginVersion.screenshots.length*160+'px');
        	
        	$screenshots.appendTo($rightSide);
        	
        	$(".fancybox").fancybox();
        }
        
    }
           
    return myself;
}



wd.marketplace.components.pluginVersionSelector = function(spec){
    
    /**
     * Specific specs
     */
    
    var _spec = {
        name: "pluginVersionSelector",
        description: "Plugin Version Selector",
        cssClass: "pluginVersionSelector",
        availableVersions: undefined,
        changeAction: undefined
    }; 
    
    
    spec = $.extend({},_spec,spec);
    var myself = wd.caf.component(spec);

    var pluginVersion, versionMap = {};

    var $ph, $pluginVersionSelector;


    myself.draw = function($ph){
        myself.setPlaceholder( $ph );

        $.each ( spec.availableVersions , function (idx, el ){
          versionMap[el.branch] = el;  
        });

        myself.update();
    }
    
    
    
    myself.update = function(){
        
        var $ph = myself.getPlaceholder().empty();        

        
        $pluginVersionSelector = $("<select/>").addClass(spec.cssClass + " chzn-select").appendTo($ph);
        $.each( spec.availableVersions , function (idx, el) {
        	var $option = $('<option/>').val(el.branch).text(el.version + " (" + el.name + ")");
        	if(el.branch == pluginVersion.branch){
        		$option.attr('selected','selected');
        	}
        	$option.appendTo($pluginVersionSelector);
        });
        $pluginVersionSelector.change( function () {
        	//get only relevant properties
        	var version = versionMap[$pluginVersionSelector.val()];
        	
        	var newVersion = {
        		branch: version.branch,
        		buildId: version.buildId,
        		version: version.version,
                changeLog: version.changeLog,
                versionDescription: version.description,
                releaseDate: version.releaseDate
        	}
        	
            myself.setPluginVersion( newVersion );
            myself.changeAction();     
        });
        // $pluginVersionSelector.chosen();
    }
    
    
    myself.setPluginVersion = function(_pluginVersion){
        
        pluginVersion = $.extend({},pluginVersion, _pluginVersion);
        
    }
    
    myself.changeAction = function(){
        
        spec.changeAction(pluginVersion);
        
    }
    
    return myself;
}



wd.marketplace.components.pluginButton = function(spec){
    
    /**
     * Specific specs
     */
    
    var _spec = {
        name: "pluginButton",
        description: "Plugin Button",
        cssClass: "pluginButton",
        clickAction: undefined,
        label: "Button"
    }; 
    
    
    spec = $.extend({},_spec,spec);
    var myself = wd.caf.component(spec);

    var $button;

    myself.draw = function($ph){
        myself.setPlaceholder( $ph );
        myself.update();
    }
    
    
    
    myself.update = function(){

        var $ph = myself.getPlaceholder().empty();        

        // version selector
        $button = $("<button/>").addClass(spec.cssClass);
        
        if(spec.image != undefined){
        	$button.append("<span style='line-height: 18px; padding-left: 20px;'>"+spec.label+"</span>");
        	$button.append("<div class='icon' style='background: url("+spec.image+")'></div>");
        } else {
        	$button.text(spec.label);
        }
        
        $button.appendTo($ph);
        $button.click( myself.clickAction);     
    }
    
        
    myself.clickAction = function(){
        
        spec.clickAction();
        
    }
    
    return myself;
}



/*
 *
 *  Popups
 *
 */ 


wd.marketplace.popups.basicPopup = function(spec){


    /**
     * Specific specs
     */
    var _spec = {
        name: "basic",
        description: "Basic Popup"        
    };

    /* Specific options for showing */
    var _options = {        
        content: "Content", 
        status: "Status",
        details: "details",
        bottom: "bottom",
        img: "",
        cssClass:"",
        buttons: []
    };


    spec = $.extend({},_spec,spec);
    var options = $.extend({},_options,options);
    var myself = wd.caf.impl.popups.basicPopup(spec);

    /**
     * Draws the content
     * @name basicPopup.drawContent
     * @memberof wd.caf.impl.popups.basicPopup
     * @param Object to control this popups' behavior
     */
    myself.drawContent = spec.drawContent || function(options){

        // Write: topPoup, contentPopup, bottomPopup
        
        var $ph = $("<div/>").addClass("basicPopupContainer " + " " + options.cssClass);
        
        $centerContainer = $("<div/>").addClass("cafBasicPopupCenterContainer").appendTo($ph)
        .append( $('<div>&nbsp;</div>').addClass('popupImgContainer') )
        .append( myself.drawCenterContent(options) );
        $bottomContainer = $("<div/>").addClass("cafBasicPopupBottomContainer").appendTo($ph)
        .append(myself.drawBottomContent(options).prepend(options.bottom) );

        myself.log("here");
        return $ph;
    }


    /**
     * Draws the center content
     * @name basicPopup.drawTopContent
     * @memberof wd.caf.impl.popups.basicPopup
     */
    myself.drawCenterContent = spec.drawCenterContent || function(options){
        return $('<div/>').addClass('popupInfoContainer')
        .append( $('<div/>').addClass('popupStatus').append(
            (typeof options.status === "function") ? options.status(myself,options) : options.status) )
        .append( $('<div/>').addClass('popupContent').append(
            (typeof options.content === "function") ? options.content(myself,options) : options.content) )
        .append( $('<div/>').addClass('popupDetails').append(
            (typeof options.details === "function") ? options.details(myself,options) : options.details) );
            
    }


    /**
     * Shows the popup
     * @name basicPopup.show
     * @memberof wd.caf.impl.popups.basicPopup
     * @param Object to control this popups' behavior. eg: {content: "Test",buttons:[...]}
     */
    myself.show = spec.show || function(options){
        
        options = $.extend({},_options,options);
        
        myself.log("Popup show action");
        myself.caf.popupEngine.show(myself,options);
        
    }
    
    return myself;
}

marketplace.getRegistry().registerPopup( wd.marketplace.popups.basicPopup() );


wd.marketplace.popups.closePopup = function(spec){


    /**
     * Specific specs
     */
    var _spec = {
        name: "close",
        description: "Close Popup"        
    };

    /* Specific options for showing */
    var _options = {        
        content: "Content", 
        status: "Status",
        details: "details",
        bottom: "bottom",
        img: "", 
        validateFunction: undefined,
        cssClass:"",
        buttons:[{
            label: "OK",
            validate: false,
            cssClass: "popupClose", 
            callback: function(popup,options){
                myself.hide()
            },
            validateFunction: undefined
        }
        ]
    }


    spec = $.extend({},_spec,spec);
    var options  = $.extend({},_options,options);
    var myself = wd.marketplace.popups.basicPopup(spec);
    
    
    /**
     * Shows the popup
     * @name basicPopup.show
     * @memberof wd.caf.impl.popups.basicPopup
     * @param Object to control this popups' behavior. eg: {content: "Test",buttons:[...]}
     */
    myself.show = spec.show || function(options){
        
        options = $.extend({},_options,options);
        
        myself.log("Popup show action");
        myself.caf.popupEngine.show(myself,options);
        
    }
    
    return myself;
}


marketplace.getRegistry().registerPopup( wd.marketplace.popups.closePopup() );



wd.marketplace.popups.okcancelPopup = function(spec){


    /**
     * Specific specs
     */
    var _spec = {
        name: "okcancel",
        description: "Ok/Cancel Popup"        
    };

    /* Specific options for showing */
    var _options = {        
        content: "Content", 
        status: "Status",
        details: "details",
        bottom: "bottom",
        img: "",
        cssClass:"",
        validateFunction: undefined,
        okCallback: function(){
            myself.log("Action not defined","warn")
        },
        buttons:[
        {
            label: "Ok",
            validate: true,
            cssClass: "popupClose", 
            callback: function(popup,options){
                // Call function. if return true, hide.
                if(options.okCallback(popup,options)){
                    myself.hide();
                }
            },
            validateFunction: undefined
        },
        {
            
            label: "Cancel",
            validate: false,
            cssClass: "popupClose", 
            callback: function(popup,options){
                myself.hide()
            },
            validateFunction: undefined
        }
        ]
    }


    spec = $.extend({},_spec,spec);
    var options  = $.extend({},_options,options);
    var myself = wd.marketplace.popups.basicPopup(spec);
    
    
    /**
     * Shows the popup
     * @name basicPopup.show
     * @memberof wd.caf.impl.popups.basicPopup
     * @param Object to control this popups' behavior. eg: {content: "Test",buttons:[...]}
     */
    myself.show = spec.show || function(options){
        
        options = $.extend({},_options,options);
        
        myself.log("Popup show action");
        myself.caf.popupEngine.show(myself,options);
        
    }
    
    return myself;
}


marketplace.getRegistry().registerPopup( wd.marketplace.popups.okcancelPopup() );



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
    var INSTALL = "install", UNINSTALL = "uninstall";
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
    
    });
    
    
    var restartInfoComponent; 
      
      
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
    
    
    myself.installPlugin = function(plugin, branch){      
        myself.caf.popupEngine.getPopup("okcancel").show({
            status: "Do you want to install now?",
            content: plugin.getPluginInfo().name +" ("+ branch +")",
            details: "",
            bottom: "You are about to start the installation. Do you want to proceed?",
            cssClass: "popupInstall",
            okCallback: function(){
                myself.log("Install plugin " + plugin.getPluginInfo().id + ", Branch: " + branch,"info");
        
                // 1. Set the notification for the installing operation
                myself.startOperation(INSTALL, plugin, branch);

                // 2. Send to engine
                myself.caf.engine.installPlugin(plugin.getPluginInfo().id, branch, function(){
                    myself.log("Install plugin done: " + plugin.getPluginInfo().id +", branch " + branch ,"info")
                    myself.stopOperation(INSTALL, plugin, branch)
                }, function(){
                    myself.log("Error installing plugin: " + plugin.getPluginInfo().id +", branch " + branch ,"info")
                    myself.errorOperation(INSTALL, plugin, branch)
                } );
            },
            validateFunction: function () {
                return true
            }
        });
    }
    
        
    myself.uninstallPlugin = function(plugin){
        
        myself.caf.popupEngine.getPopup("okcancel").show({
            status: "Do you want to uninstall now?",
            content: plugin.getPluginInfo().name,
            details: "",
            bottom: "You are about to uninstall. Do you want to proceed?",
            cssClass: "popupUninstall",
            okCallback: function(){
                myself.log("Uninstall plugin " + plugin.getPluginInfo().id ,"info");
        
                // 1. Set the notification for the installing operation
                myself.startOperation(UNINSTALL, plugin);
        
                // 2. Send to engine
                myself.caf.engine.uninstallPlugin(plugin.getPluginInfo().id, function(){
                    myself.log("Uninstall plugin done: " + plugin.getPluginInfo().id ,"info");
                    myself.stopOperation(UNINSTALL, plugin); 
                }, function(){
                    myself.log("Error uninstalling plugin: " + plugin.getPluginInfo().id ,"info");
                    myself.errorOperation(UNINSTALL, plugin); 
                } );
            },
            validateFunction: function () {
                return true
            }
        });
      
    }


    myself.startOperation = function(operation, plugin, branch){
        
        if(operation == INSTALL){
            var popupStatus = "Installing ",
            popupContent = plugin.getPluginInfo().name +" ("+ branch +")",
            cssClass =  "popupInstall popupOperation";
        }
        else{
            var popupStatus = "Uninstalling ",
            popupContent = plugin.getPluginInfo().name,
            cssClass =  "popupUninstall popupOperation";
        }
        myself.log("Starting " + operation + " operation");

        myself.caf.popupEngine.getPopup("basic").show({
            status: popupStatus,
            content: popupContent,
            details: "please wait...",
            bottom: popupStatus,
            cssClass: cssClass
        });

    }
    
    
    myself.stopOperation = function(operation, plugin, branch){

        if(operation == INSTALL){
            var popupStatus = "Successfuly Installed ",
            popupContent = plugin.getPluginInfo().name +" ("+ branch +")",
            popupDetails = (plugin.getPluginInfo().installationNotes) ? plugin.getPluginInfo().installationNotes : "",
            cssClass = "popupInstall popupSuccess";
        }
        else{
            var popupStatus = "Successfully Uninstalled ",
            popupContent = plugin.getPluginInfo().name,
            popupDetails = "Thank you",
            cssClass = "popupUninstall popupSuccess";
        }
        myself.log("Stopping " + operation + " operation");

        myself.caf.popupEngine.getPopup("close").show({
            status: popupStatus,
            content: popupContent,
            bottom: "Close",
            details: popupDetails,
            cssClass: cssClass
        });
        
        myself.caf.actionEngine.getAction('refresh').executeAction();
        
        if (!restartInfoComponent) {
            restartInfoComponent = myself.caf.getRegistry().getEntity('components', 'restart');
        }   
        restartInfoComponent.draw( myself.caf.templateEngine.getTemplate().$toggleActionContainer );
    }
    
    myself.errorOperation = function(operation, plugin, branch, errorMsg){
        
        errorMsg = errorMsg || "";
        if(operation == INSTALL){
            var popupStatus = "Error Installing ",
            popupDetails = errorMsg,
            cssClass = "popupInstall popupError";
        }
        else{
            var popupStatus = "Error Uninstalling ",
            popupDetails= errorMsg,
            cssClass = "popupUninstall popupError";
        }
        myself.log("Starting " + operation + " operation");

        myself.caf.popupEngine.getPopup("close").show({
            status: popupStatus,
            content: "Please try again later",
            bottom: "Close",
            details: popupDetails,
            cssClass: cssClass
        });
        
        myself.caf.actionEngine.getAction('refresh').executeAction();
        
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

/*
marketplace.getRegistry().registerPanel(wd.caf.impl.panels.underConstruction({
    name:"about",
    description:"About",
    order: 90
}));*/




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

alert('XML is invalid or no XML parser found');
        return null;
    }
}
var pentahoMarketplace = new PentahoMarketplace();

