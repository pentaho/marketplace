
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
    
    
    impl.installPlugin = function(pluginId, branchId, callback){

        wd.info("Marketplace engine: installing plugin: " + pluginId + " (" + branchId + ")");
        
        $.ajax({
            url: "../installpluginjson",
            dataType: 'json',
            data: {
                pluginId: pluginId, 
                versionId: branchId
            },
            success: callback,
            error: impl.errorUpdating
        });
        
    }
    

    impl.uninstallPlugin = function(pluginId, callback){
        
        wd.info("Marketplace engine: uninstalling plugin: " + pluginId);
        
        $.ajax({
            url: "../uninstallpluginjson",
            dataType: 'json',
            data: {
                pluginId: pluginId
            },
            success: callback,
            error: impl.errorUpdating
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
    description: "Refresh",
    order: 20,
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
        order: 110
    };

      
    spec = $.extend({},_spec,spec);    
    var myself = wd.caf.action(spec);
   
    var actionPh;
   
    
    myself.init = function(caf){

        myself.log("Generic entity init","debug");
        myself.caf = caf;
        
        if (!actionPh){
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

        if(Modernizr.csstransitions){          
            actionPh.toggleClass("marketplaceTransparent marketplaceMoveOut");
        }
        else{
            actionPh.toggle();
        }
        
    } 
    
     
    myself.setupAction = function(){
          
        var container = myself.caf.templateEngine.getTemplate().$toggleActionContainer;
        
        actionPh = $('<div/>').addClass('toggleActionWrapper marketplaceTransparent marketplaceMoveOut')
        .append( $('<div/>').addClass('toggleActionLogo') )
        .append( $('<div/>').addClass('toggleActionDesc')
            .text( spec.toggleText ) )
        .append( $('<div/>').addClass('toggleActionDevBy') );
       
        container.append(actionPh);
    }
        
    return myself;
        
};

marketplace.getRegistry().registerAction( wd.marketplace.actions.toggleAction({
    name: "about",
    description: "About",
    toggleText: 'Pentaho Marketplace plugin allows you to browse through available plugins and customize your Pentaho installation. Enjoy!',
    order: 10
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
    
    
    myself.getInstalledVersion = function(){

        if(!pluginInfo.installed){
            return null;
        }

        // Directly return an object that shows the installed version, filling in the details
        var version = {
            branch: pluginInfo.installedBranch,
            version: pluginInfo.installedVersion
        }
        
        // Try to find the name
        pluginInfo && $.isArray(pluginInfo.versions) && pluginInfo.versions.map(function(v){
            if(v.branch == version.branch){
                version.name = v.name;
                return false;
            }
        });
        
        // if not found, default to branch id
        if(!version.name){
            version.name = version.branch;
        }
        
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
        if (!pluginInfo.versions || pluginInfo.versions.length == 0){
            return installationStatus["INSTALLED"];
        }
        
        // loop through the others
        var isUpdateAvailable;
        pluginInfo.versions.map(function(v){
            if(v.branch == installedVersion.branch){
                isUpdateAvailable = v.version != installedVersion.version;
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
    var $wrapper, $versionWrapper;
    
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
        
        $("<div/>").addClass("pluginHeaderTitleWrapper pentaho-titled-toolbar pentaho-padding-sm pentaho-background contrast-color pentaho-rounded-panel2").append(
            $("<div/>").addClass("pluginHeaderLogo").append( $("<img/>").attr('src', plugin.getPluginInfo().img))
            .appendTo($wrapper))
        .append(
            $("<div/>").addClass("pluginHeaderTitle").text(plugin.getPluginInfo().name).appendTo($wrapper))
        .append(
            $("<div/>").addClass("pluginHeaderUpdates " + installationStatus.cssClass)
            .text(installationStatus.description).appendTo($wrapper)    
            ).appendTo($wrapper);
            
            
        var $versionWrapper = $("<div/>").addClass("pentaho-rounded-panel2 pluginHeaderVersionWrapper ")
        .appendTo($wrapper);
        

        if(plugin.getPluginInfo().installed){
         
            $("<div/>").addClass("pluginHeaderVersionLabel").text(plugin.getPluginInfo().installedBranch).appendTo($versionWrapper);
            $("<div/>").text(plugin.getPluginInfo().installedVersion).appendTo($versionWrapper);
        }
        else{
            $("<div/>").addClass("pluginHeaderVersionNotInstalled").text("Not installed").appendTo($versionWrapper);
        }

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
        cssClass: "pluginBody",
        installAction: undefined,
        uninstallAction: undefined
    }; 
    
    
    spec = $.extend({},_spec,spec);
    var myself = wd.caf.component(spec);

    // BlueprintMixin
    wd.caf.modules.blueprintPanelModule(myself);


    var plugin;

    // Containers
    var $element, $wrapper, $pluginBodyDetailsArea, $pluginBodyInstallWrapper, 
    $installedVersion, $availableVersions;
    
    // Details section
    var $pluginBodyDesc, $pluginVersionDesc;
    var pluginVersionDesc;
    
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
        
        
        // Wrapper for detailsArea
        $pluginBodyDetailsArea = $("<div/>").addClass("pluginBodyDetailsArea").appendTo($wrapper);
        
        $pluginBodyDesc = $("<div/>").addClass("pluginBodyDesc clearfix").appendTo($pluginBodyDetailsArea)
        .append($("<div/>").addClass("pluginBodyDescLogo prepend-1 span-4 append-1")
            .append( $("<img/>").attr('src', plugin.getPluginInfo().img) ) )
        .append($("<div/>").addClass("pluginBodyDescDesc span-18 last")
            .append($("<div/>").addClass("pluginBodyTitle").text("Information"))
            .append($("<div/>").addClass("pluginBodyDescription").text(plugin.getPluginInfo().description))
            )
        ;
        
        
        // Add the pluginVersionDescription, hidden at start
        pluginVersionDesc =  wd.marketplace.components.pluginVersionDesc({
            cssClass: "pluginVersionDesc marketplaceHidden",
            installAction: spec.installAction
        });
        
        pluginVersionDesc.draw($pluginBodyDetailsArea);
        $pluginVersionDesc = $pluginBodyDetailsArea.find(".pluginVersionDesc");
        
        
        
        // Wrapper for pluginBodyInstall
        $pluginBodyInstallWrapper = undefined; // todo
        
        
        // Current version
        var $installedVersionWrapper = $("<div/>").addClass("pluginBodyVersions prepend-1 span-4 append-1").appendTo($wrapper);
        
        if(plugin.getPluginInfo().installed){
            
            $installedVersion = $("<div/>").addClass("pluginVersions installedVersion clearfix");

            $installedVersionWrapper.append(
                $("<div/>").addClass("pluginBodyTitle").text("Installed Version")
                )
            .append($installedVersion);
            
            wd.marketplace.components.pluginVersion({
                cssClass: "pluginVersion",
                pluginVersion: plugin.getInstalledVersion(),
                clickAction: function(){
                    myself.showBodyDesc();
                }
            }).draw($installedVersion);

        }
         
        
        // Available versions
        var $availableVersionsWrapper = $("<div/>").addClass("pluginBodyVersions currentVersion span-15").appendTo($wrapper);
        
        if(plugin.getPluginInfo().versions){
    
            $availableVersions = $("<div/>").addClass("pluginVersions availableVersions clearfix");
        
            $availableVersionsWrapper.append(
                $("<div/>").addClass("pluginBodyTitle").text("Available Versions")
                )
            .append($availableVersions);
            
        
            plugin.getPluginInfo().versions.map(function(v){
                
                // see if this matches the installed plugin
                var highlight = !!plugin.getInstalledVersion() && v.branch == plugin.getInstalledVersion().branch;
                
                wd.marketplace.components.pluginVersion({
                    cssClass: "pluginVersion",
                    pluginVersion: v,
                    clickAction: function(){
                        
                        //spec.installAction(v.branch);
                        myself.showVersionDesc(v);
                    },
                    highlight: highlight
                }).draw($availableVersions);
            })
        }
        
    
        // Uninstall action
        var $uninstallWrapper = $("<div/>").addClass("pluginBodyVersions pluginVersions uninstallVersion span-3").append(
            $("<div/>").text("Uninstall")
            )
        .appendTo($wrapper)

        if(typeof spec.uninstallAction === "function"){
            $uninstallWrapper.addClass("cafPointer");
            $uninstallWrapper.click( spec.uninstallAction );
        }

        

        // Add footer
        var footerContent = $("<div/>").addClass("pluginBodyFooterContent")
        .append($("<div/>").addClass("pluginHorizontalSeparator"))
        .appendTo($("<div/>").addClass("clearfix pluginBodyFooter span-22 prepend-1 append-1 last").appendTo($wrapper))
        
        // we'll put company logo or name, and a link if we have it
        var content = plugin.getPluginInfo().companyLogoUrl?'<img src="'+plugin.getPluginInfo().companyLogoUrl+'" />':plugin.getPluginInfo().company;
        if(plugin.getPluginInfo().companyUrl){
            footerContent.append('<a href="'+plugin.getPluginInfo().companyUrl+'" target="_blank">'+content+'</a>');
        }
        else{
            footerContent.append(content);
        }

        
    }
    
    
    myself.showVersionDesc = function(version){
        
        myself.log("showVersionDesc " + version);
        
        pluginVersionDesc.setPluginVersion(version);
        pluginVersionDesc.update();
        
        $pluginBodyDesc.addClass("marketplaceHidden");
        $pluginVersionDesc.removeClass("marketplaceHidden");
        
        
        // Mark this one selected
        $availableVersions.find(".pluginVersion").each(function(idx,v){
            var $v = $(v);
            if($v.data("pluginVersion") == version){
                $v.addClass("pluginVersionSelected");
            }
            else{
                $v.removeClass("pluginVersionSelected");
            }
        });
        
        
    }
    
    myself.showBodyDesc = function(){
        
        myself.log("showBodyDesc");
        
        $pluginBodyDesc.removeClass("marketplaceHidden");
        $pluginVersionDesc.addClass("marketplaceHidden");

        // unselectAll
        $availableVersions.find(".pluginVersionSelected").removeClass("pluginVersionSelected");
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


wd.marketplace.components.pluginVersion = function(spec){
    
    /**
     * Specific specs
     */
    
    var _spec = {
        name: "pluginVersion",
        description: "Plugin Version",
        cssClass: "pluginVersion",
        pluginVersion: "",
        highlight: false,
        clickAction: undefined
    }; 
    
    
    spec = $.extend({},_spec,spec);
    var myself = wd.caf.component(spec);

    var $wrapper;


    myself.draw = function($ph){
        
        $wrapper = $("<div/>").addClass(spec.cssClass).data("pluginVersion",spec.pluginVersion)
        .append($("<span/>").addClass("pluginVersionNumber").text(spec.pluginVersion.version))
        
        if(spec.highlight){
            $wrapper.addClass("pluginVersionHighlight");
        }
        
        if(spec.pluginVersion.name){
            $wrapper.append($("<span/>").addClass("pluginVersionBranch").text(" (" + spec.pluginVersion.name + ")"))
            
        }
        
        if(typeof spec.clickAction === "function"){
            $wrapper.addClass("cafPointer");
            $wrapper.click(spec.clickAction);
        }

        $wrapper.appendTo($ph);

        
    }
    
    return myself;
}



wd.marketplace.components.pluginVersionDesc = function(spec){
    
    /**
     * Specific specs
     */
    
    var _spec = {
        name: "pluginVersionDesc",
        description: "Plugin Version Description",
        cssClass: "pluginVersionDesc",
        pluginVersion: undefined,
        installAction: undefined
    }; 
    
    
    spec = $.extend({},_spec,spec);
    var myself = wd.caf.component(spec);

    var pluginVersion;

    var $pluginVersionDescWrapper;


    myself.draw = function($ph){
        
        $pluginVersionDescWrapper = $("<div/>").addClass(spec.cssClass +" clearfix").appendTo($ph);
        
    //myself.update();

        
    }
    
    
    
    myself.update = function(){
        
        
        $pluginVersionDescWrapper.empty();
        
        // install button
        var $installButton = $("<div/>").addClass("pluginVersionDescInstallButton ");
        $installButton.addClass("cafPointer");
        $installButton.click(myself.installAction);
        
        $pluginVersionDescWrapper
        .append($("<div/>").addClass("pluginVersionDescLeftArea prepend-1 span-4 append-1")
            .append($("<div/>").addClass("clearfix pluginVersionDescTitle").text(pluginVersion.branch))
            .append($("<div/>").addClass("clearfix pluginVersionDescVersion").text(pluginVersion.version))
            .append($("<div/>").append($installButton))

            )
        .append($("<div/>").addClass("pluginVersionDescDesc span-18 last")
            .append($("<div/>").addClass("pluginVersionDescTitle").text("Version information"))
            .append($("<div/>").addClass("pluginVersionDescDescription").text(pluginVersion.changelog!=null?pluginVersion.changelog:"Not available"))
        
            );
        
    }
    
    
    myself.setPluginVersion = function(_pluginVersion){
        
        pluginVersion = _pluginVersion;
        
    }
    
    myself.installAction = function(){
        
        spec.installAction(pluginVersion.branch);
        
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
        img: ""
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
        
        var $ph = $("<div/>").addClass("basicPopupContainer");
        
        $centerContainer = $("<div/>").addClass("cafBasicPopupCenterContainer").appendTo($ph).append(myself.drawCenterContent(options));
        $bottomContainer = $("<div/>").addClass("cafBasicPopupBottomContainer").appendTo($ph)
            .append(options.bottom)
            .append(myself.drawBottomContent(options));

        myself.log("here");
        return $ph;
    }


    /**
     * Draws the center content
     * @name basicPopup.drawTopContent
     * @memberof wd.caf.impl.popups.basicPopup
     */
    myself.drawCenterContent = spec.drawCenterContent || function(options){
        var imgContainer = $('<div/>').addClass('imgContainer').append( $('<img/>').attr('src', options.img) ),
            msgContainer = $('<div/>').addClass('msgContainer')
                .append( $('<span/>').addClass('status').append(
                    (typeof options.status === "function") ? options.status(myself,options) : options.status) )
                .append( $('<span/>').addClass('info').append(
                    (typeof options.content === "function") ? options.content(myself,options) : options.content) )
                .append( $('<span/>').addClass('details').append(
                    (typeof options.details === "function") ? options.details(myself,options) : options.details) );
            
        return imgContainer, msgContainer;
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
        buttons:[{
            label: "Close",
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
    
    
    myself.installPlugin = function(plugin, branch){      
         myself.caf.popupEngine.getPopup("okcancel").show({
            status: "Do you want to install now?",
            content: plugin.getPluginInfo().name +" ("+ branch +")",
            details: "",
            bottom: "You are about to start the installation. Do you want to proceed?",

            okCallback: function(){
                myself.log("Install plugin " + plugin.getPluginInfo().id + ", Branch: " + branch,"info");
        
                // 1. Set the notification for the installing operation
                myself.startOperation(INSTALL, plugin, branch);

                // 2. Send to engine
                myself.caf.engine.installPlugin(plugin.getPluginInfo().id, branch, function(){
                    myself.log("Install plugin done: " + plugin.getPluginInfo().id +", branch " + branch ,"info")
                    myself.stopOperation(INSTALL, plugin, branch)
                })
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
            okCallback: function(){
                myself.log("Uninstall plugin " + plugin.getPluginInfo().id ,"info");
        
                // 1. Set the notification for the installing operation
                myself.startOperation(UNINSTALL, plugin);
        
                // 2. Send to engine
                myself.caf.engine.uninstallPlugin(plugin.getPluginInfo().id, function(){
                    myself.log("Uninstall plugin done: " + plugin.getPluginInfo().id ,"info");
                    myself.stopOperation(UNINSTALL, plugin); 
                });
            },
            validateFunction: function () {
                return true
            }
        });
      
    }


    myself.startOperation = function(operation, plugin, branch){
        
        if(operation == INSTALL){
            var popupStatus = "Installing ",
                popupContent = plugin.getPluginInfo().name +" ("+ branch +")";
        }
        else{
           var popupStatus = "Uninstalling ",
               popupContent = plugin.getPluginInfo().name;
        }
        myself.log("Starting " + operation + " operation");

        myself.caf.popupEngine.getPopup("basic").show({
            status: popupStatus,
            content: popupContent,
            details: "please wait...",
            bottom: popupStatus 
        });

    }
    
    
    myself.stopOperation = function(operation, plugin, branch){

        if(operation == INSTALL){
            var popupStatus = "Successfuly Installed ",
                popupDetails = "Installed" + plugin.getPluginInfo().name +" ("+ branch +")";
        }
        else{
           var popupStatus = "Successfully Uninstalled ",
               popupDetails= "Uninstalled" + plugin.getPluginInfo().name;
        }
        myself.log("Starting " + operation + " operation");

        myself.caf.popupEngine.getPopup("close").show({
            status: popupStatus,
            content: "Thank you.",
            bottom: "Close",
            details: popupDetails
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

